package config;

import java.util.regex.Pattern;

import javax.faces.context.FacesContext;

import com.ibm.xsp.context.RequestCustomizerFactory;
import com.ibm.xsp.context.RequestParameters;

public class ConfigRequestCustomizerFactory extends RequestCustomizerFactory {

	@Override
	public void initializeParameters(final FacesContext facesContext, final RequestParameters parameters) {
		//		parameters.setUrlProcessor(ConfigUrlProcessor.instance);
	}

	public static class ConfigUrlProcessor implements RequestParameters.UrlProcessor {
		public static ConfigUrlProcessor instance = new ConfigUrlProcessor();

		public String processActionUrl(final String url) {
			String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
			String pathPattern = "^" + Pattern.quote(contextPath);
			return url.replaceAll(pathPattern, "/m");
		}

		public String processGlobalUrl(final String url) {
			return url;
		}

		public String processResourceUrl(final String url) {
			String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
			String pathPattern = "^" + Pattern.quote(contextPath);
			return url.replaceAll(pathPattern, "/m");
		}
	}
}
