package controller;

import java.util.Map;

import model.Client;
import model.Note;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.TabularDataModel;

import config.UserInfo;

import frostillicus.xsp.controller.BasicXPageController;
import frostillicus.xsp.model.AbstractModelList;

public class notes extends BasicXPageController {
	private static final long serialVersionUID = 1L;

	public AbstractModelList<Note> getNotes() {
		UserInfo userInfo = UserInfo.get();
		Map<String, Object> viewScope = ExtLibUtil.getViewScope();
		String searchQuery = (String)viewScope.get("searchQuery");

		Note.Manager manager = Note.Manager.get();
		AbstractModelList<Note> result;
		if(userInfo.isAdmin()) {
			result = manager.getNamedCollection("all", null);
			if(StringUtil.isNotEmpty(searchQuery)) {
				result.search(searchQuery);
				result.setResortOrder("$Created", TabularDataModel.SORT_DESCENDING);
			}
		} else {
			result = manager.getNamedCollection("mine", null);
			if(StringUtil.isNotEmpty(searchQuery)) {
				String query = "(" + Client.Manager.get().getClientQuery() + ") and (" + searchQuery + ")";
				System.out.println(query);
				result.search(query);
				result.setResortOrder("$Created", TabularDataModel.SORT_DESCENDING);
			}
		}
		return result;
	}
}
