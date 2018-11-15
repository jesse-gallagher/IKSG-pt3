package model;

import java.util.*;

import javax.persistence.Table;

import lotus.domino.NotesException;

import org.hibernate.validator.constraints.NotEmpty;
import org.openntf.domino.*;

import com.ibm.commons.util.StringUtil;

import config.UserInfo;

import service.FreshBooksService;
import util.JSFUtil;

import frostillicus.xsp.bean.ApplicationScoped;
import frostillicus.xsp.bean.ManagedBean;
import frostillicus.xsp.model.domino.AbstractDominoManager;
import frostillicus.xsp.model.domino.AbstractDominoModel;
import frostillicus.xsp.model.AbstractModelList;
import frostillicus.xsp.model.ModelUtils;
import frostillicus.xsp.model.domino.DominoColumnInfo;
import frostillicus.xsp.util.FrameworkUtils;

@Table(name="Client")
@SuppressWarnings("unused")
public class Client extends AbstractDominoModel {
	private static final long serialVersionUID = 1L;

	@NotEmpty String name;

	@Override
	protected Collection<String> nonSummaryFields() {
		return Arrays.asList(new String[] { });
	}

	@Override
	protected Collection<String> namesFields() {
		return Arrays.asList(new String[] { "BillingUsers", "Users" });
	}

	public List<String> getProjectDropDownValues() throws Exception {
		List<String> result = new ArrayList<String>();

		Project.Manager projectManager = Project.Manager.get();
		List<Project> projects = projectManager.getNamedCollection("By Client", (String)getValue("id"));
		for(Project project : projects) {
			result.add(project.getValue("Name") + "|" + project.getValue("id"));
		}

		return result;
	}

	public SortedSet<service.freshbooks.Project> getFreshBooksProjects() throws Exception {
		String freshBooksId = (String)getValue("FreshBooksID");
		if(freshBooksId == null || freshBooksId.isEmpty()) {
			return new TreeSet<service.freshbooks.Project>();
		}

		FreshBooksService freshBooks = FreshBooksService.get();
		return freshBooks.getProjectsForClient(Integer.parseInt(freshBooksId, 10));
	}
	public SortedSet<service.freshbooks.TimeEntry> getOpenTimeEntries() throws Exception {
		String freshBooksId = (String)getValue("freshbooksid");
		if(freshBooksId == null || freshBooksId.isEmpty()) {
			return new TreeSet<service.freshbooks.TimeEntry>();
		}

		FreshBooksService freshBooks = FreshBooksService.get();
		return freshBooks.getOpenTimeEntriesForClient(Integer.parseInt(freshBooksId, 10));
	}
	public double getOpenTimeEntryTotal() throws Exception {
		double result = 0;
		for(service.freshbooks.TimeEntry timeEntry : getOpenTimeEntries()) {
			result += timeEntry.getHours();
		}
		return result;
	}


	@SuppressWarnings("unchecked")
	public AbstractModelList<RecurringCharge> getRecurringCharges() {
		return (AbstractModelList<RecurringCharge>)RecurringCharge.Manager.get().getValue("By Client^^" + getId());
	}
	@SuppressWarnings("unchecked")
	public AbstractModelList<OneTimeCharge> getOneTimeCharges() {
		return (AbstractModelList<OneTimeCharge>)OneTimeCharge.Manager.get().getValue("By Client^^" + getId());
	}
	@SuppressWarnings("unchecked")
	public AbstractModelList<Rate> getRates() {
		return (AbstractModelList<Rate>)Rate.Manager.get().getValue("By Client^^" + getId());
	}
	@SuppressWarnings("unchecked")
	public AbstractModelList<Project> getProjects() {
		return (AbstractModelList<Project>)Project.Manager.get().getValue("By Client^^" + getId());
	}

	@Override
	protected boolean querySave() {
		// Stash the current name value for comparison post-save to update children
		Document doc = document();
		FrameworkUtils.getRequestScope().put("initName" + getId(), doc.getItemValueString("name"));

		return super.querySave();
	}

	@Override
	protected void postSave() {
		super.postSave();

		// See if the name changed
		String initName = (String)FrameworkUtils.getRequestScope().get("initName" + getId());
		String newName = (String)getValue("name");
		if(!StringUtil.equals(initName, newName)) {
			for(Project project : getProjects()) {
				project.setValue("clientName", newName);
				project.save();
			}
		}
	}

	@ManagedBean(name="Clients")
	@ApplicationScoped
	public static class Manager extends AbstractDominoManager<Client> {
		private static final long serialVersionUID = 1L;

		public static Manager get() {
			Client.Manager instance = (Client.Manager)FrameworkUtils.resolveVariable(Client.Manager.class.getAnnotation(ManagedBean.class).name());
			return instance == null ? new Client.Manager() : instance;
		}

		@Override
		protected Database getDatabase() {
			return JSFUtil.getClientDatabase();
		}
		@Override
		protected String getViewPrefix() {
			return "Clients\\";
		}

		@Override
		public Object getValue(final Object keyObject) {
			if("dropdown_list".equals(keyObject)) {
				return getDatabase().getView("Clients\\All").getColumnValues(1);
			} else if("myClients".equals(keyObject)) {
				return getMyClients();
			}
			return super.getValue(keyObject);
		}

		public List<Client> getMyClients() {
			try {
				UserInfo userInfo = JSFUtil.getUserInfo();
				Collection<String> names = userInfo.getNamesList();
				Set<Client> result = new HashSet<Client>();

				// Build a list of applicable client IDs for the user's various names
				for(String name : names) {
					AbstractModelList<Client> clients = Client.Manager.get().getNamedCollection("By Users", name);
					for(Client client : clients) {
						if("Yes".equals(client.getValue("IncludeAsProfile"))) {
							result.add(client);
						}
					}
				}
				return new ArrayList<Client>(result);
			} catch(NotesException ne) {
				ModelUtils.publishException(ne);
				return null;
			}
		}

		@SuppressWarnings("unchecked")
		public String getClientQuery() {
			try {
				Collection<String> names = new lotus.notes.addins.DominoServer(getDatabase().getServer()).getNamesList(FrameworkUtils.getUserName());
				Set<String> clientIds = new HashSet<String>();

				// Build a list of applicable client IDs for the user's various names
				for(String name : names) {
					AbstractModelList<Client> clients = Client.Manager.get().getNamedCollection("By Users", name);
					for(Client client : clients) {
						clientIds.add((String)client.getValue("id"));
					}
				}

				// Now build a FT query string out of the unique IDs
				StringBuilder query = new StringBuilder();
				boolean added = false;
				for(String clientId : clientIds) {
					if(added) query.append(" or ");
					query.append("[ClientID]=\"" + clientId + "\"");
					added = true;
				}
				if(query.length() == 0) {
					return "[NotARealField]=NotARealValue";
				} else {
					return query.toString();
				}
			} catch(NotesException ne) {
				throw new RuntimeException(ne);
			}
		}

		@SuppressWarnings("unchecked")
		public String getBillingQuery() {
			try {
				Collection<String> names = new lotus.notes.addins.DominoServer(getDatabase().getServer()).getNamesList(FrameworkUtils.getUserName());
				Set<String> clientIds = new HashSet<String>();

				// Build a list of applicable client IDs for the user's various names
				for(String name : names) {
					AbstractModelList<Client> clients = Client.Manager.get().getNamedCollection("By Billing Users", name);
					for(Client client : clients) {
						clientIds.add((String)client.getValue("id"));
					}
				}

				// Now build a FT query string out of the unique IDs
				StringBuilder query = new StringBuilder();
				boolean added = false;
				for(String clientId : clientIds) {
					if(added) query.append(" or ");
					query.append("[ClientID]=\"" + clientId + "\"");
					added = true;
				}
				if(query.length() == 0) {
					return "[NotARealField]=NotARealValue";
				} else {
					return query.toString();
				}
			} catch(NotesException ne) {
				throw new RuntimeException(ne);
			}
		}
	}

}
