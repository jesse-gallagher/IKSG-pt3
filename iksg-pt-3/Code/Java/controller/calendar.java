package controller;

import java.util.*;

import javax.faces.context.FacesContext;

import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.xsp.component.UIViewRootEx2;
import com.ibm.xsp.extlib.util.ExtLibUtil;

import config.UserInfo;
import model.*;
import util.JSFUtil;
import frostillicus.xsp.controller.BasicXPageController;

public class calendar extends BasicXPageController {
	private static final long serialVersionUID = -1L;

	@Override
	public void afterPageLoad() throws Exception {
		Map<String, Object> sessionScope = ExtLibUtil.getSessionScope();
		UIViewRootEx2 view = (UIViewRootEx2)FacesContext.getCurrentInstance().getViewRoot();
		sessionScope.put("lastTaskListPage", view.getPageName());
	}

	@SuppressWarnings("unchecked")
	public String getTasksJSON() throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		FacesContext facesContext = FacesContext.getCurrentInstance();
		String contextPath = facesContext.getExternalContext().getRequestContextPath();

		UserInfo userInfo = JSFUtil.getUserInfo();

		Task.Manager taskManager = Task.Manager.get();
		List<Task> tasks = (List<Task>)taskManager.getValue(userInfo.isStaff() || userInfo.isAdmin() ? "mine" : "clientPending");

		for(Task task : tasks) {
			if(!task.category()) {
				Object dateObject = task.getValue("Due");
				if(dateObject instanceof Date) {
					Date dueDate = (Date)dateObject;

					Map<String, Object> node = new HashMap<String, Object>();
					node.put("title", task.getValue("Summary"));
					node.put("start", dueDate.getTime() / 1000);
					node.put("allDay", true);
					node.put("url", contextPath + "/task.xsp?id=" + task.getValue("id"));

					String status = String.valueOf(task.getValue("Status"));
					if("Submitted to Client".equals(status)) {
						node.put("className", "label-grey");
					} else {
						node.put("className", "label-purple");
					}

					result.add(node);
				}
			}
		}

		Event.Manager eventManager = Event.Manager.get();
		List<Event> events = (List<Event>)eventManager.getValue(userInfo.isStaff() ? "all" : "mine");

		for(Event event : events) {
			if(!event.category()) {
				Object type = event.getValue("AppointmentType");

				Object startDateObject = event.getValue("0".equals(type) ? "StartDateTime" : "StartDate");
				if(startDateObject instanceof Date) {
					Date startDate = (Date)startDateObject;

					Object endDateObject = event.getValue("0".equals(type) ? "EndDateTime" : "EndDate");
					Date endDate = endDateObject instanceof Date ? (Date)endDateObject : null;

					Map<String, Object> node = new HashMap<String, Object>();
					node.put("title", event.getValue("Summary"));
					node.put("start", startDate.getTime() / 1000);
					node.put("end", (endDate == null ? startDate.getTime() : endDate.getTime()) / 1000);
					node.put("url", contextPath + "/event.xsp?id=" + event.getValue("id"));
					node.put("allDay", "2".equals(event.getValue("AppointmentType")));
					node.put("className", "label-yellow");
					result.add(node);

				}
			}
		}

		return JsonGenerator.toJson(JsonJavaFactory.instance, result);
	}
}
