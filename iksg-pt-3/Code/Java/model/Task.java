package model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.*;

import org.openntf.domino.*;

import util.JSFUtil;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.model.TabularDataModel;

import frostillicus.xsp.bean.ApplicationScoped;
import frostillicus.xsp.bean.ManagedBean;
import frostillicus.xsp.model.domino.AbstractDominoManager;
import frostillicus.xsp.model.domino.AbstractDominoModel;
import frostillicus.xsp.model.domino.DominoModelList;
import frostillicus.xsp.util.FrameworkUtils;

@Table(name="Task")
public class Task extends AbstractDominoModel {
	private static final long serialVersionUID = 1L;

	@NotEmpty String summary;
	@NotNull TimeFrame timeFrame;
	TaskType type;
	List<String> tags;
	java.sql.Date due;
	TaskStatus status;



	@Override
	public void initFromDatabase(final Database database) {
		super.initFromDatabase(database);

		setValueImmediate("CreatedBy", database.getParent().getEffectiveUserName());
		setValueImmediate("Status", "Open");
	}

	@Override
	protected Collection<String> richTextFields() {
		return Arrays.asList(new String[] { "Body" });
	}

	@Override
	protected Collection<String> authorsFields() {
		return Arrays.asList(new String[] { "Assignee" });
	}

	public Project getProject() {
		String projectId = (String)getValue("ProjectID");
		if(projectId == null || projectId.isEmpty()) {
			return null;
		}
		return (Project)Project.Manager.get().getValue(projectId);
	}

	public Client getClient() {
		String clientId = (String)getValue("ClientID");
		if(clientId == null || clientId.isEmpty()) {
			return null;
		}
		return (Client)Client.Manager.get().getValue(clientId);
	}

	public Task getParentTask() {
		String parentId = (String)getValue("ParentID");
		if(StringUtil.isEmpty(parentId)) {
			return null;
		}
		return (Task)Task.Manager.get().getValue(parentId);
	}


	public static enum TimeFrame { Normal, Rush, Urgent }
	public static enum TaskType { Normal, Question }
	public static enum TaskStatus { Open, InProgress, SubmittedToClient, Closed, Canceled, OnHold }

	@ManagedBean(name="Tasks")
	@ApplicationScoped
	public static class Manager extends AbstractDominoManager<Task> {
		private static final long serialVersionUID = 1L;

		public static Manager get() {
			Task.Manager instance = (Task.Manager)FrameworkUtils.resolveVariable(Task.Manager.class.getAnnotation(ManagedBean.class).name());
			return instance == null ? new Task.Manager() : instance;
		}

		@Override
		protected Database getDatabase() {
			return JSFUtil.getDataDatabase();
		}
		@Override
		protected String getViewPrefix() {
			return "Tasks\\";
		}

		@Override
		public DominoModelList<Task> getNamedCollection(final String name, final String category) {
			if("mine".equalsIgnoreCase(name)) {
				return getNamedCollection("Active by Assignee", FrameworkUtils.getUserName());
			} else if("clientPending".equalsIgnoreCase(name)) {
				DominoModelList<Task> tasks = getNamedCollection("Active", null);
				tasks.search(Client.Manager.get().getClientQuery());
				tasks.setResortOrder("Due", TabularDataModel.SORT_ASCENDING);
				return tasks;
			} else if("clientClosed".equalsIgnoreCase(name)) {
				DominoModelList<Task> tasks = getNamedCollection("Closed", null);
				tasks.search(Client.Manager.get().getClientQuery());
				tasks.setResortOrder("$NoDateSorter", TabularDataModel.SORT_ASCENDING);
				return tasks;
			} else if("unassigned".equalsIgnoreCase(name)) {
				return getNamedCollection("Active by Assignee", "");
			}
			return super.getNamedCollection(name, category);
		}
	}
}
