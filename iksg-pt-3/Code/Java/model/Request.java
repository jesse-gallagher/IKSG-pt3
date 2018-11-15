package model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.openntf.domino.*;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.model.TabularDataModel;

import config.UserInfo;

import util.JSFUtil;

import frostillicus.xsp.bean.ApplicationScoped;
import frostillicus.xsp.bean.ManagedBean;
import frostillicus.xsp.model.AbstractModelList;
import frostillicus.xsp.model.domino.AbstractDominoManager;
import frostillicus.xsp.model.domino.AbstractDominoModel;
import frostillicus.xsp.model.domino.DominoColumnInfo;
import frostillicus.xsp.model.domino.DominoModelList;
import frostillicus.xsp.util.FrameworkUtils;

@Table(name="Request")
@SuppressWarnings("unused")
public class Request extends AbstractDominoModel {
	private static final long serialVersionUID = 1L;

	@NotEmpty String summary;

	@SuppressWarnings("unchecked")
	@Override
	public void initFromDatabase(final Database database) {
		super.initFromDatabase(database);

		UserInfo userInfo = JSFUtil.getUserInfo();
		setValue("Requester", userInfo.getName());
		if(userInfo.isClient()) {
			Client.Manager clients = Client.Manager.get();
			List<Client> myClients = (List<Client>)clients.getValue("myClients");
			if(!myClients.isEmpty()) {
				setValue("ClientID", myClients.get(0).getValue("id"));
			}
		}
	}

	@Override
	protected Collection<String> richTextFields() {
		return Arrays.asList(new String[] { "Body" });
	}

	public Client getClient() {
		String clientId = (String)getValue("ClientID");
		if(clientId == null || clientId.isEmpty()) {
			return null;
		}
		return (Client)Client.Manager.get().getValue(clientId);
	}

	public Project getProject() {
		String projectId = (String)getValue("ProjectID");
		if(projectId == null || projectId.isEmpty()) {
			return null;
		}
		return (Project)Project.Manager.get().getValue(projectId);
	}

	public Task getParentTask() {
		String parentId = (String)getValue("ParentID");
		if(StringUtil.isEmpty(parentId)) {
			return null;
		}
		return (Task)Task.Manager.get().getValue(parentId);
	}

	@ManagedBean(name="Requests")
	@ApplicationScoped
	public static class Manager extends AbstractDominoManager<Request> {
		private static final long serialVersionUID = 1L;

		public static Manager get() {
			Request.Manager instance = (Request.Manager)FrameworkUtils.resolveVariable(Request.Manager.class.getAnnotation(ManagedBean.class).name());
			return instance == null ? new Request.Manager() : instance;
		}

		@Override
		protected Database getDatabase() {
			return JSFUtil.getDataDatabase();
		}
		@Override
		protected String getViewPrefix() {
			return "Requests\\";
		}

		@Override
		public DominoModelList<Request> getNamedCollection(final String name, final String category) {
			if("mine".equals(name)) {
				DominoModelList<Request> tasks = new DominoModelList<Request>(getDatabase(), "Requests\\All", null, Request.class);
				tasks.search(Client.Manager.get().getClientQuery());
				tasks.setResortOrder("$Created", TabularDataModel.SORT_DESCENDING);
				return tasks;
			}
			return super.getNamedCollection(name, category);
		}

		@Override
		public Object getValue(final Object keyObject) {
			if("openRequestCountDisplay".equals(keyObject)) {
				UserInfo userInfo = UserInfo.get();
				AbstractModelList<Request> result = getNamedCollection(userInfo.isAdmin() ? "all" : "mine", null);
				return result.isEmpty() ? "" : String.valueOf(result.size());
			}
			return super.getValue(keyObject);
		}
	}

}
