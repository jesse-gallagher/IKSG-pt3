package config;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

//import org.openntf.domino.utils.DominoUtils;

import frostillicus.xsp.controller.ControllingViewHandler;

public class ConfigViewHandler extends ControllingViewHandler {

	public ConfigViewHandler(final ViewHandler arg0) {
		super(arg0);
	}

	@Override
	public UIViewRoot createView(final FacesContext context, final String pageName) {
		//		DominoUtils.setBubbleExceptions(true);

		if(!AppConfig.get().isComplete()) {
			return super.createView(context, "/appconfig");
		}

		return super.createView(context, pageName);
	}
}
