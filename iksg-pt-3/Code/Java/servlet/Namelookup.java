package servlet;

import javax.faces.context.FacesContext;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.ibm.commons.util.io.json.*;
import com.ibm.commons.util.io.json.JsonFactory;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.xsp.webapp.DesignerFacesServlet;
import org.openntf.domino.*;

import java.io.*;
import java.util.*;
import frostillicus.xsp.util.FrameworkUtils;

public class Namelookup extends DesignerFacesServlet {
	@Override
	public void service(final ServletRequest servletRequest, final ServletResponse servletResponse) throws ServletException, IOException {
		HttpServletResponse res = (HttpServletResponse) servletResponse;
		HttpServletRequest req = (HttpServletRequest) servletRequest;

		res.setContentType("text/plain");
		ServletOutputStream out = res.getOutputStream();
		FacesContext facesContext = this.getFacesContext(req, res);

		try {
			Map<String, String> param = FrameworkUtils.getParam();
			if(!param.containsKey("term")) {
				res.setContentType("application/json");
				out.println("[]");
			}


			String term = param.get("term");

			Session session = FrameworkUtils.getSession();
			Directory dir = session.getDirectory();

			Vector<String> names = new Vector<String>();
			names.add(term);
			Vector<String> items = new Vector<String>();
			items.add("FullName");


			List<Map<String, String>> result = new ArrayList<Map<String, String>>();

			// Add the requested term in case it's intentional
			Map<String, String> self = new HashMap<String, String>();
			self.put("text", term);
			if(term.contains("@")) {
				self.put("value", term);
			} else {
				self.put("value", toCanonical(term));
			}
			result.add(self);

			DirectoryNavigator nav = dir.lookupNames("$NamesFieldLookup", names, items, true);
			int breaker = 0;
			if(nav.findFirstMatch()) {
				String value = String.valueOf(nav.getFirstItemValue().get(0));
				Map<String, String> node = new HashMap<String, String>();
				node.put("text", toAbbreviated(value));
				node.put("value", value);
				result.add(node);
			}
			while(nav.findNextMatch()) {
				String value = String.valueOf(nav.getFirstItemValue().get(0));
				Map<String, String> node = new HashMap<String, String>();
				node.put("text", toAbbreviated(value));
				node.put("value", value);
				result.add(node);

				if(breaker++ > 100) break;
			}


			JsonFactory jsonFactory = new JsonJavaFactory();
			String json = JsonGenerator.toJson(jsonFactory, result);
			res.setContentType("application/json");
			res.setContentLength(json.length());
			out.print(json);

		} catch (Exception e) {
			e.printStackTrace(new PrintStream(out));
		} finally {
			facesContext.responseComplete();
			facesContext.release();
			out.close();
		}
	}

	public static String toAbbreviated(final String value) {
		Session session = FrameworkUtils.getSession();
		Name name = session.createName(value);
		return name.getAbbreviated();
	}
	public static String toCanonical(final String value) {
		Session session = FrameworkUtils.getSession();
		Name name = session.createName(value);
		return name.getCanonical();
	}
}
