package controller;

import model.RecurringCharge;

public class recurringCharge extends AbstractModelPageController<RecurringCharge> {
	private static final long serialVersionUID = 1L;

	@Override
	protected String getFriendlyModelName() {
		return "Recurring Charge";
	}
}
