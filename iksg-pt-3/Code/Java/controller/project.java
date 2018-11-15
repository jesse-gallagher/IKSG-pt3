package controller;

import java.util.Arrays;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewRootEx2;
import com.ibm.xsp.extlib.util.ExtLibUtil;

import model.Project;

public class project extends AbstractModelPageController<Project> {
	private static final long serialVersionUID = 1L;

	@Override
	public void afterPageLoad() throws Exception {
		HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String queryString = req.getQueryString();
		if(StringUtil.isNotEmpty(queryString)) {
			Map<String, Object> sessionScope = ExtLibUtil.getSessionScope();
			UIViewRootEx2 view = (UIViewRootEx2)FacesContext.getCurrentInstance().getViewRoot();
			sessionScope.put("lastTaskListPage", view.getPageName() + "?" + queryString);
		}
	}

	public boolean getDisplayAdditionalInfo() {
		String type = (String)getObject().getValue("type");
		return Arrays.asList("Fixed Scope", "Product").contains(type);
	}
}
