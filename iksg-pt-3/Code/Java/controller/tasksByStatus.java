package controller;

import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewRootEx2;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.designer.context.XSPContext;
import com.ibm.xsp.model.TabularDataModel;

import frostillicus.xsp.controller.BasicXPageController;
import frostillicus.xsp.model.AbstractModelList;
import frostillicus.xsp.util.FrameworkUtils;
import model.Task;

public class tasksByStatus extends BasicXPageController {
	private static final long serialVersionUID = 1L;

	@Override
	public void afterPageLoad() throws Exception {
		Map<String, Object> sessionScope = FrameworkUtils.getSessionScope();
		UIViewRootEx2 view = FrameworkUtils.getViewRoot();
		sessionScope.put("lastTaskListPage", view.getPageName());
	}

	public AbstractModelList<Task> getTasks() {
		Task.Manager manager = Task.Manager.get();
		AbstractModelList<Task> result = manager.getNamedCollection("By Status", getActiveStatus().name());

		Map<String, Object> viewScope = FrameworkUtils.getViewScope();
		String searchQuery = (String)viewScope.get("searchQuery");
		if(StringUtil.isNotEmpty(searchQuery)) {
			result.search(searchQuery);
			result.setResortOrder("$NoDateSorter", TabularDataModel.SORT_ASCENDING);
		}
		return result;
	}

	public Task.TaskStatus getActiveStatus() {
		Map<String, String> param = FrameworkUtils.getParam();
		String status = param.get("status");
		try {
			return Task.TaskStatus.valueOf(status);
		} catch(IllegalArgumentException e) {
			return Task.TaskStatus.Closed;
		}
	}

	public String getStatusName() {
		Task.TaskStatus status = getActiveStatus();
		try {
			ResourceBundle translation = FacesContextEx.getCurrentInstance().getApplicationEx().getResourceBundle("model_translation", XSPContext.getXSPContext(FacesContext.getCurrentInstance()).getLocale());
			return translation.getString(status.getClass().getName() + "." + status.name());
		} catch(Exception e) {
			return status.name();
		}
	}
}
