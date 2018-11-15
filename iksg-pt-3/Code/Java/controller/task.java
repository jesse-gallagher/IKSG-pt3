package controller;

import java.util.*;

import javax.faces.model.SelectItem;

import com.ibm.commons.util.StringUtil;

import util.UserUtil;
import model.Task;
import model.Project;
import frostillicus.xsp.model.ModelObject;
import frostillicus.xsp.util.FrameworkUtils;

public class task extends AbstractModelPageController<Task> {
	private static final long serialVersionUID = 1L;

	private String newProjectName_;

	@Override
	public void afterPageLoad() throws Exception {
		super.afterPageLoad();

		Task task = getObject();
		if(task.isNew()) {
			Map<String, String> param = FrameworkUtils.getParam();
			String parentId = param.get("parentId");
			if(StringUtil.isNotEmpty(parentId)) {
				task.setValue("parentId", parentId);
			}
		}
	}

	public List<Task> getLineage() {
		List<Task> result = new ArrayList<Task>();

		Task parent = (getObject()).getParentTask();
		while(parent != null) {
			result.add(0, parent);

			parent = parent.getParentTask();
		}

		return result;
	}

	public List<SelectItem> getPotentialAssignees() {
		return UserUtil.namesToItems(getObject().getValue("assignee"));
	}

	public List<SelectItem> getPotentialContacts() {
		return UserUtil.namesToItems(getObject().getValue("contacts"));
	}

	public String getEmailSubject() {
		ModelObject task = getObject();
		String result = String.valueOf(task.getValue("Summary"));
		Project project = (Project)task.getValue("project");
		if(project != null) {
			result += " (" + project.getValue("Name") + ")";
		}
		return result;
	}

	public String getEmailSendTo() {
		ModelObject task = getObject();
		String requester = UserUtil.toEmailAddress(task.getValue("requester"));
		String contacts = UserUtil.toEmailAddress(task.getValue("contacts"));
		if(StringUtil.isEmpty(requester)) {
			return contacts;
		} else if(StringUtil.isEmpty(contacts)) {
			return requester;
		} else {
			return requester + "," + contacts;
		}
	}

	@SuppressWarnings("unchecked")
	public void assignToSelf() {
		ModelObject task = getObject();
		Object newTaskAssignee = task.getValue("Assignee");
		String username = FrameworkUtils.getUserName();
		if(newTaskAssignee instanceof List) {
			List<Object> current = (List<Object>)newTaskAssignee;
			current.add(username);
		} else if(newTaskAssignee instanceof String) {
			String current = (String)newTaskAssignee;
			if(current.trim().isEmpty()) {
				task.setValue("Assignee", username);
			} else {
				List<String> result = new ArrayList<String>();
				result.add(current.trim());
				result.add(username);
				task.setValue("Assignee", result);
			}
		} else {
			task.setValue("Assignee", username);
		}
	}
	public void assignToNone() {
		getObject().setValue("Assignee", "");
	}

	public String getNewProjectName() { return newProjectName_; }
	public void setNewProjectName(final String newProjectName) { newProjectName_ = newProjectName; }

	@Override
	public String save() {
		ModelObject task = getObject();

		String projectId = (String)task.getValue("projectId");
		if("new".equals(projectId)) {
			String newProjectName = getNewProjectName();
			Project newProject = Project.Manager.get().create();
			newProject.setValue("clientId", task.getValue("clientId"));
			newProject.setValue("name", newProjectName);
			if(newProject.save()) {
				task.setValue("projectId", newProject.getId());
			} else {
				return "xsp-failure";
			}
		}

		if(FrameworkUtils.getViewRoot().save()) {
			FrameworkUtils.flashMessage("confirmation", "Task saved successfully");
			return "xsp-success";
		}
		return "xsp-failure";
	}
}
