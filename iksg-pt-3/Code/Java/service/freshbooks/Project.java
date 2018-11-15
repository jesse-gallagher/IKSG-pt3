package service.freshbooks;

import java.io.Serializable;
import org.openntf.domino.utils.xml.XMLNode;

public class Project implements Serializable, Comparable<Project> {
	private static final long serialVersionUID = -4572597193807673824L;

	private final int id_;
	private final int clientId_;
	private final String name_;

	public Project(final XMLNode node) throws Exception {
		id_ = Integer.parseInt(node.selectSingleNode("project_id").getText(), 10);
		clientId_ = Integer.parseInt(node.selectSingleNode("client_id").getText(), 10);
		name_ = node.selectSingleNode("name").getText();
	}

	public int getId() { return id_; }
	public int getClientId() { return clientId_; }
	public String getName() { return name_; }

	public int compareTo(final Project o) {
		return name_.compareTo(o.getName());
	}

}
