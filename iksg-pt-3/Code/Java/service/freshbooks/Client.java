package service.freshbooks;

import java.io.Serializable;
import org.openntf.domino.utils.xml.XMLNode;

public class Client implements Serializable, Comparable<Client> {
	private static final long serialVersionUID = -2247213751104804138L;

	private final int id_;
	private final String organization_;

	public Client(final XMLNode node) throws Exception {
		id_ = Integer.parseInt(node.selectSingleNode("client_id").getText(), 10);
		organization_ = node.selectSingleNode("organization").getText();
	}

	public int getId() { return id_; }
	public String getOrganization() { return organization_; }

	public int compareTo(final Client arg0) {
		return organization_.compareTo(arg0.getOrganization());
	}


}