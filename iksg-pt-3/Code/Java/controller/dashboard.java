package controller;

import java.util.*;

import javax.faces.context.FacesContext;

import model.Task;

import com.ibm.xsp.component.UIViewRootEx2;
import com.ibm.xsp.designer.context.XSPContext;
import com.ibm.xsp.extlib.util.ExtLibUtil;

import frostillicus.xsp.controller.BasicXPageController;
import frostillicus.xsp.util.FrameworkUtils;

public class dashboard extends BasicXPageController {
	private static final long serialVersionUID = 1L;

	@Override
	public void afterPageLoad() throws Exception {
		super.afterPageLoad();

		Map<String, Object> sessionScope = ExtLibUtil.getSessionScope();
		UIViewRootEx2 view = (UIViewRootEx2)FacesContext.getCurrentInstance().getViewRoot();
		sessionScope.put("lastTaskListPage", view.getPageName());
	}

	public void createTask() {
		getDialogNewTask().save();
	}
	public void createTaskAndView() {
		Task task = getDialogNewTask();
		if(task.save()) {
			XSPContext context = ExtLibUtil.getXspContext();
			context.redirectToPage("/task.xsp?id=" + task.getValue("id"));
		}
	}

	@SuppressWarnings("unchecked")
	public void assignToSelf() throws Exception {
		Map<String, Object> viewScope = ExtLibUtil.getViewScope();
		Object newTaskAssignee = viewScope.get("newTaskAssignee");
		String username = FrameworkUtils.getSession().getEffectiveUserName();
		if(newTaskAssignee instanceof List) {
			List<Object> current = (List<Object>)newTaskAssignee;
			current.add(username);
		} else if(newTaskAssignee instanceof String) {
			String current = (String)newTaskAssignee;
			if(current.trim().isEmpty()) {
				viewScope.put("newTaskAssignee", username);
			} else {
				List<String> result = new ArrayList<String>();
				result.add(current.trim());
				result.add(username);
				viewScope.put("newTaskAssignee", result);
			}
		} else {
			viewScope.put("newTaskAssignee", username);
		}
	}
	public void assignToNone() {
		ExtLibUtil.getViewScope().remove("newTaskAssignee");
	}

	public Task getDialogNewTask() {
		return (Task)FrameworkUtils.resolveVariable("newTask");
	}
}
