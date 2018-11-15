package service;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.*;
import java.net.*;


import service.freshbooks.Client;
import service.freshbooks.Project;
import service.freshbooks.TimeEntry;
import service.freshbooks.Task;
import util.JSFUtil;
import frostillicus.xsp.bean.ApplicationScoped;
import frostillicus.xsp.bean.ManagedBean;
import org.openntf.domino.utils.xml.*;
import frostillicus.xsp.util.FrameworkUtils;

@ManagedBean(name="FreshBooks")
@ApplicationScoped
public class FreshBooksService implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final int PER_PAGE = 50;

	private List<Client> clients_;
	private List<Project> projects_;
	private final Map<Integer, Task> tasksById_ = new HashMap<Integer, Task>();

	private final Map<Integer, SortedSet<TimeEntry>> timeEntriesByClientId_ = new HashMap<Integer, SortedSet<TimeEntry>>();
	private final Map<Integer, Long> timeEntriesByClientIdRefreshed_ = new HashMap<Integer, Long>();

	public static FreshBooksService get() {
		FreshBooksService instance = (FreshBooksService)FrameworkUtils.resolveVariable(FreshBooksService.class.getAnnotation(ManagedBean.class).name());
		return instance == null ? new FreshBooksService() : instance;
	}

	/* ********************************************************************************
	 * Client API
	 **********************************************************************************/
	public List<Client> getClients() throws Exception {
		if(clients_ == null) {
			ClientResponse response = fetchClientPage(1);
			clients_ = new ArrayList<Client>();
			clients_.addAll(response.entries);

			// fetch any remaining pages
			for(int i = 2; i <= response.pages; i++) {
				response = fetchClientPage(i);
				clients_.addAll(response.entries);
			}
		}

		return clients_;
	}

	public List<String> getClientChoices() throws Exception {
		SortedSet<Client> sortedClients = new TreeSet<Client>();
		sortedClients.addAll(getClients());
		List<String> result = new ArrayList<String>();
		for(Client client : sortedClients) {
			result.add(client.getOrganization() + "|" + client.getId());
		}
		return result;
	}

	private ClientResponse fetchClientPage(final int page) throws Exception {
		XMLDocument request = createRequest("client.list", page);
		request.selectSingleNode("/request").addChildElement("folder").setText("active");

		XMLDocument response = postRequest(request);

		ClientResponse result = new ClientResponse();
		XMLNode clients = response.selectSingleNode("/response/clients");
		result.page = Integer.parseInt(clients.getAttribute("page"), 10);
		result.perPage = Integer.parseInt(clients.getAttribute("per_page"), 10);
		result.pages = Integer.parseInt(clients.getAttribute("pages"), 10);
		result.total = Integer.parseInt(clients.getAttribute("total"), 10);

		for(XMLNode clientNode : response.selectNodes("/response/clients/client")) {
			result.entries.add(new Client(clientNode));
		}

		return result;
	}

	/* ********************************************************************************
	 * Projects API
	 **********************************************************************************/
	public List<Project> getProjects() throws Exception {
		if(projects_ == null) {
			ProjectResponse response = fetchProjectPage(1);
			projects_ = new ArrayList<Project>();
			projects_.addAll(response.entries);

			// fetch any remaining pages
			for(int i = 2; i <= response.pages; i++) {
				response = fetchProjectPage(i);
				projects_.addAll(response.entries);
			}
		}
		return projects_;
	}

	public SortedSet<Project> getProjectsForClient(final int clientId) throws Exception {
		SortedSet<Project> result = new TreeSet<Project>();
		for(Project project : getProjects()) {
			if(project.getClientId() == clientId) {
				result.add(project);
			}
		}
		return result;
	}

	private ProjectResponse fetchProjectPage(final int page) throws Exception {
		XMLDocument request = createRequest("project.list", page);
		XMLDocument response = postRequest(request);

		ProjectResponse result = new ProjectResponse();
		XMLNode projects = response.selectSingleNode("/response/projects");
		result.page = Integer.parseInt(projects.getAttribute("page"), 10);
		result.perPage = Integer.parseInt(projects.getAttribute("per_page"), 10);
		result.pages = Integer.parseInt(projects.getAttribute("pages"), 10);

		for(XMLNode projectNode : response.selectNodes("/response/projects/project")) {
			result.entries.add(new Project(projectNode));
		}

		return result;
	}

	/* ********************************************************************************
	 * Tasks
	 **********************************************************************************/
	public Task getTaskById(final int id) throws Exception {
		if(!tasksById_.containsKey(id)) {
			XMLDocument request = createRequest("task.get");
			request.selectSingleNode("/request").addChildElement("task_id").setText(String.valueOf(id));
			XMLDocument response = postRequest(request);
			tasksById_.put(id, new Task(response.selectSingleNode("/response/task")));
		}
		return tasksById_.get(id);
	}

	/* ********************************************************************************
	 * Time Entries
	 **********************************************************************************/
	public SortedSet<TimeEntry> getOpenTimeEntriesForClient(final int clientId) throws Exception {
		long lastRefreshed = timeEntriesByClientIdRefreshed_.containsKey(clientId) ? timeEntriesByClientIdRefreshed_.get(clientId) : 0;
		long minutesSinceRefresh = (System.currentTimeMillis() - lastRefreshed) / 1000 / 60;

		if(!timeEntriesByClientId_.containsKey(clientId) || minutesSinceRefresh > 30) {
			SortedSet<TimeEntry> result = new TreeSet<TimeEntry>();

			// We have to search for time entries by project, so loop through projects
			for(Project project : getProjectsForClient(clientId)) {
				boolean foundBilled = false;

				TimeEntryResponse response = fetchTimeEntryPage(1, project.getId());
				for(TimeEntry timeEntry : response.entries) {
					if(timeEntry.isBilled()) {
						foundBilled = true;
					} else {
						result.add(timeEntry);
					}
				}

				// fetch any remaining pages
				for(int i = 2; i <= response.pages; i++) {
					if(foundBilled) break;

					response = fetchTimeEntryPage(i, project.getId());
					for(TimeEntry timeEntry : response.entries) {
						if(timeEntry.isBilled()) {
							foundBilled = true;
						} else {
							result.add(timeEntry);
						}
					}
				}
			}
			timeEntriesByClientId_.put(clientId, result);
			timeEntriesByClientIdRefreshed_.put(clientId, System.currentTimeMillis());
		}

		return timeEntriesByClientId_.get(clientId);
	}

	private TimeEntryResponse fetchTimeEntryPage(final int page, final int projectId) throws Exception {
		XMLDocument request = createRequest("time_entry.list", page);
		request.selectSingleNode("/request").addChildElement("project_id").setText(String.valueOf(projectId));

		XMLDocument response = postRequest(request);

		TimeEntryResponse result = new TimeEntryResponse();
		XMLNode timeEntries = response.selectSingleNode("/response/time_entries");
		result.page = Integer.parseInt(timeEntries.getAttribute("page"), 10);
		result.perPage = Integer.parseInt(timeEntries.getAttribute("per_page"), 10);
		result.pages = Integer.parseInt(timeEntries.getAttribute("pages"), 10);

		for(XMLNode timeEntryNode : response.selectNodes("/response/time_entries/time_entry")) {
			result.entries.add(new TimeEntry(timeEntryNode));
		}

		return result;
	}

	/* ********************************************************************************
	 * Response structures
	 **********************************************************************************/
	private class APIResponse {
		int page;
		int perPage;
		int pages;
		int total;
	}
	private class ClientResponse extends APIResponse {
		List<Client> entries = new ArrayList<Client>();
	}
	private class ProjectResponse extends APIResponse {
		List<Project> entries = new ArrayList<Project>();
	}
	private class TimeEntryResponse extends APIResponse {
		List<TimeEntry> entries = new ArrayList<TimeEntry>();
	}

	/* ********************************************************************************
	 * HTTP service methods
	 **********************************************************************************/

	private String getServiceURL() {
		return (String)JSFUtil.getAppConfig().getValue("freshBooksAPIURL");
	}
	private String getAuthToken() {
		return (String)JSFUtil.getAppConfig().getValue("freshBooksAuthToken");
	}
	private XMLDocument createRequest(final String method) throws Exception {
		XMLDocument request = new XMLDocument();
		request.loadString("<?xml version='1.0' encoding='UTF-8'?>\n<request/>");
		request.selectSingleNode("/request").setAttribute("method", method);
		return request;
	}
	private XMLDocument createRequest(final String method, final int page) throws Exception {
		XMLDocument request = createRequest(method);
		request.selectSingleNode("/request").addChildElement("page").setText(String.valueOf(page));
		request.selectSingleNode("/request").addChildElement("per_page").setText(String.valueOf(PER_PAGE));
		return request;
	}
	private XMLDocument postRequest(final XMLDocument request) throws Exception {
		// What? This can't be right.
		Authenticator.setDefault(new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(getAuthToken(), new char[] { 'X' });
			}
		});

		URL url = new URL(getServiceURL());
		URLConnection conn = url.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty("Content-Type", "application/xml");

		// Write the POST data
		DataOutputStream postData = new DataOutputStream(conn.getOutputStream());
		postData.writeBytes(request.getXml());
		postData.flush();
		postData.close();

		// Get the response
		BufferedReader is = new BufferedReader(new InputStreamReader(new DataInputStream(conn.getInputStream())));
		StringBuilder result = new StringBuilder();
		String aLine;
		while(null != (aLine = is.readLine())) {
			result.append(aLine);
			result.append("\n");
		}
		is.close();

		XMLDocument xml = new XMLDocument();
		xml.loadString(result.toString());

		return xml;
	}
}
