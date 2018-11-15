package converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.util.*;

import org.openntf.domino.*;

import frostillicus.xsp.util.FrameworkUtils;

public class DisplayNameConverter implements Converter {

	public Object getAsObject(final FacesContext context, final UIComponent component, final String value) {
		//		System.out.println("Called DisplayNameConverter#getAsObject with: '" + value + "'");

		if(value == null || value.trim().isEmpty()) {
			return "";
		}
		List<String> result = new ArrayList<String>();
		StringTokenizer tokens = new StringTokenizer(value, ",;\n");
		while(tokens.hasMoreTokens()) {
			String token = tokens.nextToken().trim();
			if(!token.isEmpty()) {
				result.add(toCanonical(token));
			}
		}
		return result.size() == 1 ? result.get(0) : result;
	}

	public String getAsString(final FacesContext context, final UIComponent component, final Object value) {
		//		System.out.println("Called DisplayNameConverter#getAsString with: '" + value + "'");

		if (value instanceof String) {
			return toAbbreviated((String) value);
		} else if (value instanceof Iterable) {
			StringBuilder result = new StringBuilder();
			boolean appended = false;
			for (Object node : (Iterable<?>) value) {
				if (node != null) {
					if (appended) {
						result.append(", ");
					} else {
						appended = true;
					}
					result.append(toAbbreviated(node.toString()));
				}
			}
			return result.toString();
		}
		return String.valueOf(value);
	}

	private String toAbbreviated(final String value) {
		Session session = FrameworkUtils.getSession();
		Name name = session.createName(value);
		String result = name.getAbbreviated();
		return result;
	}
	private String toCanonical(final String value) {
		Session session = FrameworkUtils.getSession();
		Name name = session.createName(value);
		String result = name.getCanonical();
		return result;
	}
}