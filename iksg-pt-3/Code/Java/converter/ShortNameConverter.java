package converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.util.*;

import org.openntf.domino.*;

import config.ShortNames;

import frostillicus.xsp.util.FrameworkUtils;

public class ShortNameConverter implements Converter {

	public Object getAsObject(final FacesContext context, final UIComponent component, final String value) {
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
		if (value instanceof String) {
			return toShort((String) value);
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
					result.append(toShort((String)node));
				}
			}
			return result.toString();
		}
		return String.valueOf(value);
	}

	private String toCanonical(final String value) {
		Session session = FrameworkUtils.getSession();
		Name name = session.createName(value);
		String result = name.getCanonical();
		return result;
	}

	private String toShort(final String value) {
		//		String common = toCommon(value);
		//		String[] bits = common.split(" ");
		//		if(bits.length < 2) {
		//			return common;
		//		} else {
		//			String first = bits[0];
		//			String last = bits[bits.length-1];
		//			return first + " " + (last.length() < 2 ? last : (last.substring(0, 1) + "."));
		//		}
		ShortNames shortNames = (ShortNames)FrameworkUtils.resolveVariable("shortNames");
		return shortNames.getValue(value);
	}
}