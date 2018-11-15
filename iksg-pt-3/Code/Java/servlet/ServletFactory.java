package servlet;

import javax.servlet.ServletException;
import com.ibm.designer.runtime.domino.adapter.*;

public class ServletFactory implements IServletFactory {
	private ComponentModule module;

	public ServletMatch getServletMatch(final String contextPath, final String path) throws ServletException {

		if (path.startsWith("/xsp/namelookup")) {
			return new ServletMatch(module.createServlet("servlet.NameLookup", "Name Lookup Servlet", null), "", path);
		}

		return null;
	}

	public void init(final ComponentModule module) {
		this.module = module;
	}

}