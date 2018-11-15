package util;

import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import service.FreshBooksService;

import org.openntf.domino.*;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.util.JSUtil;

import config.AppConfig;
import config.UserInfo;

import frostillicus.xsp.util.FrameworkUtils;

public enum JSFUtil {
	;

	public static AppConfig getAppConfig() {
		AppConfig config = (AppConfig) FrameworkUtils.resolveVariable("appConfig");
		if(config == null) {
			return new AppConfig();
		}
		return config;
	}
	public static FreshBooksService getFreshBooksService() {
		return (FreshBooksService) FrameworkUtils.resolveVariable("FreshBooks");
	}
	public static UserInfo getUserInfo() {
		return (UserInfo) FrameworkUtils.resolveVariable("userInfo");
	}

	//	public static Database getDatabase(final String apiPath) {
	//		Map<String, Object> requestScope = ExtLibUtil.getRequestScope();
	//		String key = "database-" + server + "!!" + filePath;
	//		if (!requestScope.containsKey(key)) {
	//			Session session = getSession();
	//			requestScope.put(key, session.getDatabase(server, filePath));
	//		}
	//
	//		return (Database) requestScope.get(key);
	//	}

	public static Database getDataDatabase() {
		AppConfig appConfig = getAppConfig();
		return FrameworkUtils.getDatabase((String)appConfig.getValue("dataDatabaseServer"), (String)appConfig.getValue("dataDatabasePath"));
	}
	public static Database getClientDatabase() {
		AppConfig appConfig = getAppConfig();
		return FrameworkUtils.getDatabase((String)appConfig.getValue("clientDatabaseServer"), (String)appConfig.getValue("clientDatabasePath"));
	}
	public static Database getBillingDatabase() {
		AppConfig appConfig = getAppConfig();
		return FrameworkUtils.getDatabase((String)appConfig.getValue("billingDatabaseServer"), (String)appConfig.getValue("billingDatabasePath"));
	}
	public static Database getEventsDatabase() {
		AppConfig appConfig = getAppConfig();
		return FrameworkUtils.getDatabase((String)appConfig.getValue("eventsDatabaseServer"), (String)appConfig.getValue("eventsDatabasePath"));
	}
	public static Database getUpdatesDatabase() {
		AppConfig appConfig = getAppConfig();
		return FrameworkUtils.getDatabase((String)appConfig.getValue("updatesDatabaseServer"), (String)appConfig.getValue("updatesDatabasePath"));
	}

	public static void toaster(final String summary, final String detail, final boolean sticky, final String styleClass) {
		StringBuilder result = new StringBuilder();
		result.append("jQuery.gritter.add({\n");
		if(StringUtil.isNotEmpty(summary)) {
			result.append("\ttitle: ");
			JSUtil.addString(result, summary);
			result.append(",\n");
		}
		if(StringUtil.isNotEmpty(detail)) {
			result.append("\ttext: ");
			JSUtil.addString(result, detail);
			result.append(",\n");
		}
		result.append("\tsticky: " + sticky + ",\n");

		String effectiveStyleClass = (styleClass == null ? "gritter-info" : styleClass);
		result.append("\tclass_name: ");
		JSUtil.addString(result, effectiveStyleClass);
		result.append("\n");

		result.append("})");

		FrameworkUtils.getViewRoot().postScript(result.toString());
	}
	public static void toastMessage(final FacesMessage message) {
		String styleClass = null;
		if(FacesMessage.SEVERITY_ERROR.equals(message.getSeverity())) {
			styleClass = "gritter-error";
		} else if(FacesMessage.SEVERITY_FATAL.equals(message.getSeverity())) {
			styleClass = "gritter-error";
		} else if(FacesMessage.SEVERITY_INFO.equals(message.getSeverity())) {
			styleClass = "gritter-info";
		} else if(FacesMessage.SEVERITY_WARN.equals(message.getSeverity())) {
			styleClass = "gritter-warning";
		} else {
			styleClass = "gritter-success";
		}
		toaster(message.getSummary(), message.getDetail(), false, styleClass);
	}
	@SuppressWarnings("unchecked")
	public static void toastMessages() {
		Iterator<FacesMessage> messages = FacesContext.getCurrentInstance().getMessages();
		while(messages.hasNext()) {
			FacesMessage message = messages.next();
			toastMessage(message);
		}
	}
}