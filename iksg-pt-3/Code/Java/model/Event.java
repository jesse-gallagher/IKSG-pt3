package model;

import java.util.*;

import javax.persistence.Table;

import org.openntf.domino.Database;

import util.JSFUtil;

import frostillicus.xsp.bean.ApplicationScoped;
import frostillicus.xsp.bean.ManagedBean;
import frostillicus.xsp.model.domino.AbstractDominoManager;
import frostillicus.xsp.model.domino.AbstractDominoModel;
import frostillicus.xsp.model.domino.DominoModelList;
import frostillicus.xsp.util.FrameworkUtils;

@Table(name="Event")
public class Event extends AbstractDominoModel {
	private static final long serialVersionUID = 1L;

	@Override
	public void initFromDatabase(final Database database) {
		super.initFromDatabase(database);
		setValue("AppointmentType", "0");
	}

	@Override
	protected Collection<String> richTextFields() {
		return Arrays.asList(new String[] { "Body" });
	}

	@Override
	protected boolean querySave() {
		if("0".equals(getValue("AppointmentType"))) {
			setValue("StartDateTime", timeMerge(getValue("StartDate"), getValue("StartTime")));
			setValue("EndDateTime", timeMerge(getValue("EndDate"), getValue("EndTime")));
		}
		return true;
	}

	private Date timeMerge(final Object date, final Object time) {
		if(!(date instanceof Date) || !(time instanceof Date)) { return null; }

		Date dateObj = (Date)date;
		Date timeObj = (Date)time;

		Calendar timeCal = Calendar.getInstance();
		timeCal.setTime(timeObj);

		Calendar result = Calendar.getInstance();
		result.setTime(dateObj);
		result.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
		result.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
		result.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));

		return result.getTime();
	}

	@ManagedBean(name="Events")
	@ApplicationScoped
	public static class Manager extends AbstractDominoManager<Event> {
		private static final long serialVersionUID = 1L;

		public static Manager get() {
			Event.Manager instance = (Event.Manager)FrameworkUtils.resolveVariable(Event.Manager.class.getAnnotation(ManagedBean.class).name());
			return instance == null ? new Event.Manager() : instance;
		}

		@Override
		protected Database getDatabase() {
			return JSFUtil.getEventsDatabase();
		}

		@Override
		protected String getViewPrefix() {
			return "Events\\";
		}

		@Override
		public DominoModelList<Event> getNamedCollection(final String name, final String category) {
			if("mine".equals(name)) {
				DominoModelList<Event> events = new DominoModelList<Event>(getDatabase(), "Events\\All", null, Event.class);
				events.search(Client.Manager.get().getClientQuery());
				return events;
			}
			return super.getNamedCollection(name, category);
		}
	}

}
