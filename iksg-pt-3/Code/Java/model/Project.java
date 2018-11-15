package model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.openntf.domino.*;

import com.ibm.commons.util.StringUtil;

import util.JSFUtil;

import frostillicus.xsp.bean.ApplicationScoped;
import frostillicus.xsp.bean.ManagedBean;
import frostillicus.xsp.model.domino.AbstractDominoManager;
import frostillicus.xsp.model.domino.AbstractDominoModel;
import frostillicus.xsp.model.domino.DominoColumnInfo;
import frostillicus.xsp.util.FrameworkUtils;

@Table(name="Project")
@SuppressWarnings("unused")
public class Project extends AbstractDominoModel {
	private static final long serialVersionUID = 1L;

	@NotEmpty
	private String name;


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

	public List<Task> getTasks() {
		Task.Manager taskManager = Task.Manager.get();
		return taskManager.getNamedCollection("By Project", (String)getValue("id"));
	}

	public List<Note> getNotes() {
		return Note.Manager.get().getNamedCollection("By Project", (String)getValue("id"));
	}

	@Override
	protected boolean querySave() {
		// Set the internal client name field
		String clientName = (String)getValueImmediate("clientName");
		String newClientName = "";
		Client client = getClient();
		if(client != null) {
			newClientName = (String)client.getValue("name");
		}
		if(!StringUtil.equals(clientName, newClientName)) {
			setValue("clientName", newClientName);
		}

		return super.querySave();
	}

	@ManagedBean(name="Projects")
	@ApplicationScoped
	public static class Manager extends AbstractDominoManager<Project> {
		private static final long serialVersionUID = 1L;

		public static Manager get() {
			Project.Manager instance = (Project.Manager)FrameworkUtils.resolveVariable(Project.Manager.class.getAnnotation(ManagedBean.class).name());
			return instance == null ? new Project.Manager() : instance;
		}

		@Override
		protected Database getDatabase() {
			return JSFUtil.getDataDatabase();
		}
		@Override
		protected String getViewPrefix() {
			return "Projects\\";
		}

		@Override
		public Object getValue(final Object keyObject) {
			if("dropdown_list".equals(keyObject)) {
				return getDatabase().getView("Projects\\All").getColumnValues(1);
			}
			return super.getValue(keyObject);
		}

	}

}
