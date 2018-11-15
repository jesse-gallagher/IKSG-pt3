package controller;

import config.AppConfig;
import util.JSFUtil;
import util.UserUtil;
import java.util.*;
import java.io.*;

import javax.faces.model.SelectItem;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.util.ExtLibUtil;

import frostillicus.xsp.controller.BasicXPageController;
import frostillicus.xsp.util.FrameworkUtils;

public class appconfig extends BasicXPageController {
	private static final long serialVersionUID = -742926616471152920L;

	@Override
	public void beforePageLoad() throws Exception {
		super.beforePageLoad();

		Map<String, Object> configData = new HashMap<String, Object>();
		AppConfig appConfig = JSFUtil.getAppConfig();
		for(String key : appConfig.keySet()) {
			configData.put(key, appConfig.getValue(key));
		}
		ExtLibUtil.getViewScope().put("appConfigData", configData);
	}

	public Collection<SelectItem> getNewRequestTargets() {
		AppConfig appConfig = JSFUtil.getAppConfig();
		Object assignee = appConfig.getValue("newRequestTargets");
		return UserUtil.namesToItems(assignee);
	}
	@SuppressWarnings("unchecked")
	public Collection<String> getNewRequestTargetValues() {
		List<String> result = new ArrayList<String>();

		AppConfig appConfig = JSFUtil.getAppConfig();
		Object assignee = appConfig.getValue("newRequestTargets");
		if(assignee instanceof String) {
			String user = (String)assignee;
			if(!result.contains(user) && StringUtil.isNotEmpty(user)) result.add(UserUtil.toCanonical(user));
		} else if(assignee instanceof List) {
			for(String user : (List<String>)assignee) {
				if(!result.contains(user) && StringUtil.isNotEmpty(user)) result.add(UserUtil.toCanonical(user));
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public String save() throws IOException {
		Map<String, Object> viewScope = ExtLibUtil.getViewScope();
		Map<String, Object> appConfigData = (Map<String, Object>)viewScope.get("appConfigData");

		AppConfig appConfig = JSFUtil.getAppConfig();
		for(Map.Entry<String, Object> configEntry : appConfigData.entrySet()) {
			appConfig.setValue(configEntry.getKey(), configEntry.getValue());
		}
		appConfig.save();

		FrameworkUtils.flashMessage("confirmation", "Updated app configuration.");

		return "xsp-success";
	}
}
