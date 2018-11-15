package service.freshbooks;

import java.util.Date;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import service.FreshBooksService;
import util.JSFUtil;
import org.openntf.domino.utils.xml.XMLNode;

public class TimeEntry implements Serializable, Comparable<TimeEntry> {
	private static final long serialVersionUID = -3035564480784176023L;
	private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() { return new SimpleDateFormat("yyyy-MM-dd"); };
	};

	private final int id_;
	private final int projectId_;
	private final int taskId_;
	private final double hours_;
	private final String date_;
	private final String notes_;
	private final boolean billed_;
	private final boolean rush_;

	public TimeEntry(final XMLNode node) throws Exception {
		id_ = Integer.parseInt(node.selectSingleNode("time_entry_id").getText(), 10);
		projectId_ = Integer.parseInt(node.selectSingleNode("project_id").getText(), 10);
		taskId_ = Integer.parseInt(node.selectSingleNode("task_id").getText(), 10);
		hours_ = Double.parseDouble(node.selectSingleNode("hours").getText());
		date_ = node.selectSingleNode("date").getText();
		billed_ = "1".equals(node.selectSingleNode("billed").getText());

		String notes = node.selectSingleNode("notes").getText();
		rush_ = notes.toLowerCase().contains("(rush)");
		if(rush_) {
			notes = notes.replaceAll("\\s+\\(rush\\)", "");
		}
		notes_ = notes;
	}

	public int getId() { return id_; }
	public int getProjectId() { return projectId_; }
	public int getTaskId() { return taskId_; }
	public double getHours() { return hours_; }
	public Date getDate() {
		try {
			return DATE_FORMAT.get().parse(date_);
		} catch(ParseException pe) { return null; }
	}
	public String getNotes() { return notes_; }
	public boolean isBilled() { return billed_; }
	public boolean isRush() { return rush_; }

	public Task getTask() throws Exception {
		FreshBooksService freshBooks = JSFUtil.getFreshBooksService();
		return freshBooks.getTaskById(getTaskId());
	}


	public int compareTo(final TimeEntry o) {
		int result = o.getDate().compareTo(getDate());
		int id = getId();
		int oId = o.getId();
		return result != 0 ? result : id < oId ? 1 : id == oId ? 0 : -1;
	}

}
