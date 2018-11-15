package controller;

import util.JSFUtil;
import frostillicus.xsp.controller.BasicXPageController;
import frostillicus.xsp.model.ModelObject;
import frostillicus.xsp.util.FrameworkUtils;

public abstract class AbstractModelPageController<T extends ModelObject> extends BasicXPageController {
	private static final long serialVersionUID = 1L;

	protected String getVar() {
		return getClass().getSimpleName();
	}
	protected String getFriendlyModelName() {
		String var = getVar();
		return var.substring(0, 1).toUpperCase() + var.substring(1);
	}

	@Override
	public String save() {
		if(FrameworkUtils.getViewRoot().save()) {
			FrameworkUtils.flashMessage("confirmation", getFriendlyModelName() + " saved successfully");
			return "xsp-success";
		}
		return "xsp-failure";
	}

	public void saveOnly() {
		if(getObject().save()) {
			FrameworkUtils.addMessage(getFriendlyModelName() + " saved successfully");
		}

		JSFUtil.toastMessages();
	}

	public String delete() {
		if(getObject().delete()) {
			FrameworkUtils.flashMessage("confirmation", getFriendlyModelName() + " deleted successfully");
			return "xsp-success";
		}
		return "xsp-failure";
	}

	public boolean isReadonly() {
		if(FrameworkUtils.getViewRoot().isReadonly()) {
			return true;
		} else {
			return getObject().readonly();
		}
	}

	@SuppressWarnings("unchecked")
	protected T getObject() {
		return (T)FrameworkUtils.resolveVariable(getVar());
	}
}
