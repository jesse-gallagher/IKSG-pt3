package service.freshbooks;

import java.io.Serializable;
import org.openntf.domino.utils.xml.XMLNode;

public class Task implements Serializable {
	private static final long serialVersionUID = 5688873617318770993L;

	private final int id_;
	private final String name_;

	public Task(final XMLNode node) throws Exception {
		id_ = Integer.parseInt(node.selectSingleNode("task_id").getText(), 10);
		name_ = node.selectSingleNode("name").getText();
	}

	public int getId() { return id_; }
	public String getName() { return name_; }
}
