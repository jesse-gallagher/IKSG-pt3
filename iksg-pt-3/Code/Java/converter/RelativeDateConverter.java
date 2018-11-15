package converter;

import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class RelativeDateConverter implements Converter {
	public static final ThreadLocal<DateFormat> SHORT_DATE = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("MMM d, yyyy");
		}
	};

	public Object getAsObject(final FacesContext context, final UIComponent component, final String value) {
		return value;
	}

	public String getAsString(final FacesContext context, final UIComponent component, final Object value) {
		if(value == null || (value instanceof String && ((String)value).isEmpty())) {
			return "(No Date)";
		}

		if(value instanceof Date) {
			Date dateValue = (Date)value;
			Calendar calValue = getMidDay(dateValue);

			Calendar today = getMidDay(new Date());
			Calendar yesterday = getMidDay(new Date());
			yesterday.add(Calendar.DATE, -1);
			Calendar tomorrow = getMidDay(new Date());
			tomorrow.add(Calendar.DATE, 1);

			if(calValue.equals(today)) {
				return "Today";
			} else if(calValue.equals(yesterday)) {
				return "Yesterday";
			} else if(calValue.equals(tomorrow)) {
				return "Tomorrow";
			}
			return SHORT_DATE.get().format(dateValue);
		}

		return String.valueOf(value);
	}

	private Calendar getMidDay(final Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 12);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}
}