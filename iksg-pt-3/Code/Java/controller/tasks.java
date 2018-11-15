package controller;

import java.util.Map;

import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewRootEx2;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.TabularDataModel;

import frostillicus.xsp.controller.BasicXPageController;
import frostillicus.xsp.model.AbstractModelList;
import model.Task;

public class tasks extends BasicXPageController {
	private static final long serialVersionUID = 1L;

	@Override
	public void afterPageLoad() throws Exception {
		Map<String, Object> sessionScope = ExtLibUtil.getSessionScope();
		UIViewRootEx2 view = (UIViewRootEx2)FacesContext.getCurrentInstance().getViewRoot();
		sessionScope.put("lastTaskListPage", view.getPageName());
	}

	public AbstractModelList<Task> getTasks() {
		Task.Manager manager = Task.Manager.get();
		AbstractModelList<Task> result = manager.getNamedCollection("active", null);
		Map<String, Object> viewScope = ExtLibUtil.getViewScope();
		String searchQuery = (String)viewScope.get("searchQuery");
		if(StringUtil.isNotEmpty(searchQuery)) {
			result.search(searchQuery);
			result.setResortOrder("$NoDateSorter", TabularDataModel.SORT_ASCENDING);
		}
		return result;
	}
}
