package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.ibm.commons.util.StringUtil;

import config.AppConfig;
import model.Project;
import model.Request;
import model.Task;
import util.JSFUtil;
import util.UserUtil;
import frostillicus.xsp.model.ModelObject;
import frostillicus.xsp.util.FrameworkUtils;

public class request extends AbstractModelPageController<Request> {
	private static final long serialVersionUID = 1L;

	@Override
	public void afterPageLoad() throws Exception {
		super.afterPageLoad();

		ModelObject request = getObject();
		if(request.isNew()) {
			Map<String, String> param = FrameworkUtils.getParam();
			String parentId = param.get("parentId");
			if(StringUtil.isNotEmpty(parentId)) {
				request.setValue("parentId", parentId);
				Task parentTask = (Task)Task.Manager.get().getValue(parentId);
				request.setValue("ClientID", parentTask.getValue("ClientID"));
				request.setValue("ProjectID", parentTask.getValue("ProjectID"));
			}
		}
	}

	public List<Task> getLineage() {
		List<Task> result = new ArrayList<Task>();

		Task parent = getObject().getParentTask();
		while(parent != null) {
			result.add(0, parent);

			parent = parent.getParentTask();
		}

		return result;
	}

	public String getEmailSubject() {
		ModelObject request = getObject();
		String result = String.valueOf(request.getValue("Summary"));
		Project project = (Project)request.getValue("project");
		if(project != null) {
			result += " (" + project.getValue("Name") + ")";
		}
		return result;
	}

	public String getEmailSendTo() {
		return UserUtil.toEmailAddress(getObject().getValue("requester"));
	}

	@Override
	public String save() {
		ModelObject request = getObject();
		boolean isNew = request.isNew();
		if(FrameworkUtils.getViewRoot().save()) {
			if(isNew) {
				// Send out a notification
				AppConfig appConfig = JSFUtil.getAppConfig();
				Object sendTo = appConfig.getValue("newRequestTargets");

				ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
				HttpServletRequest servletRequest = (HttpServletRequest)externalContext.getRequest();
				String url = (servletRequest.isSecure() ? "https://" : "http://") + servletRequest.getServerName() + externalContext.getRequestContextPath() + "/request.xsp?id=" + request.getValue("id");

				System.out.println("Sending email to " + sendTo);
				FrameworkUtils.getDatabase().createDocument(
						"Principal", "Milo Messenger",
						"SendTo", sendTo,
						"Subject", "New " + request.getValue("TimeFrame") + " Request in Milo",
						"Body", request.getValue("Summary") + "\n\n" + url
				).send();
			}

			FrameworkUtils.flashMessage("confirmation", "Request saved successfully");
			return "xsp-success";
		}
		return "xsp-failure";
	}

	public String convertToTask() {
		ModelObject request = getObject();
		request.setValue("Form", "Task");
		request.setValue("OriginalForm", "Request");
		request.setValue("ConvertedBy", FrameworkUtils.getUserName());
		request.setValue("Status", "Open");
		if(request.save()) {
			return "converted-to-task";
		}
		return "xsp-failure";
	}
}
