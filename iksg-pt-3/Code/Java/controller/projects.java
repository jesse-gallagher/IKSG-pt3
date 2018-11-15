package controller;

import java.util.Map;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.designer.context.XSPContext;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.TabularDataModel;

import frostillicus.xsp.controller.BasicXPageController;
import frostillicus.xsp.model.AbstractModelList;
import model.Project;
import frostillicus.xsp.util.FrameworkUtils;

public class projects extends BasicXPageController {
	private static final long serialVersionUID = 1L;

	public void create() {
		Project project = (Project)FrameworkUtils.resolveVariable("project");
		project.save();
	}
	public void createAndView() {
		Project project = (Project)FrameworkUtils.resolveVariable("project");
		if(project.save()) {
			XSPContext context = ExtLibUtil.getXspContext();
			context.redirectToPage("/project.xsp?id=" + project.getValue("id"));
		}
	}

	public AbstractModelList<Project> getProjects() {
		Project.Manager manager = Project.Manager.get();
		AbstractModelList<Project> result = manager.getNamedCollection("all", null);
		Map<String, Object> viewScope = ExtLibUtil.getViewScope();
		String searchQuery = (String)viewScope.get("searchQuery");
		if(StringUtil.isNotEmpty(searchQuery)) {
			result.search(searchQuery);
			result.setResortOrder("Name", TabularDataModel.SORT_ASCENDING);
		}
		return result;
	}
}
