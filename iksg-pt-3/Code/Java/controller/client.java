package controller;

import java.util.*;
import javax.faces.model.SelectItem;

import model.Client;

import util.JSFUtil;
import util.UserUtil;

public class client extends AbstractModelPageController<Client> {
	private static final long serialVersionUID = 1L;

	public List<SelectItem> getBillingUsersItems() {
		return UserUtil.namesToItems(getObject().getValue("billingUsers"));
	}
	public List<SelectItem> getUsersItems() {
		return UserUtil.namesToItems(getObject().getValue("users"));
	}

	public boolean isBillingUser() throws Exception {
		Object billingUsers = getObject().getValue("BillingUsers");
		if(billingUsers == null || "".equals(billingUsers)) { return false; }

		Collection<String> names = JSFUtil.getUserInfo().getNamesList();

		if(billingUsers instanceof Collection) {
			Collection<?> listValue = (Collection<?>)billingUsers;
			for(Object node : listValue) {
				if(names.contains(node)) return true;
			}
			return false;
		} else if(billingUsers instanceof String) {
			return names.contains(billingUsers);
		}
		return false;
	}
}
