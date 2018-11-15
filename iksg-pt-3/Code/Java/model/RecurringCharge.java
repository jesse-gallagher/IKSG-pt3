package model;

import java.util.*;

import javax.persistence.Table;

import org.openntf.domino.Database;

import util.JSFUtil;

import com.ibm.xsp.model.TabularDataModel;

import frostillicus.xsp.bean.ApplicationScoped;
import frostillicus.xsp.bean.ManagedBean;
import frostillicus.xsp.model.domino.AbstractDominoManager;
import frostillicus.xsp.model.domino.AbstractDominoModel;
import frostillicus.xsp.model.domino.DominoModelList;
import frostillicus.xsp.util.FrameworkUtils;

@Table(name="Recurring Charge")
public class RecurringCharge extends AbstractDominoModel {
	private static final long serialVersionUID = 1L;

	@Override
	protected Collection<String> richTextFields() {
		return Arrays.asList(new String[] { "Body" });
	}

	public Client getClient() {
		String clientId = (String)getValue("ClientID");
		if(clientId == null || clientId.isEmpty()) {
			return null;
		}
		return (Client)Client.Manager.get().getValue(clientId);
	}

	@ManagedBean(name="RecurringCharges")
	@ApplicationScoped
	public static class Manager extends AbstractDominoManager<RecurringCharge> {
		private static final long serialVersionUID = 1L;

		public static Manager get() {
			RecurringCharge.Manager instance = (RecurringCharge.Manager)FrameworkUtils.resolveVariable(RecurringCharge.Manager.class.getAnnotation(ManagedBean.class).name());
			return instance == null ? new RecurringCharge.Manager() : instance;
		}

		@Override
		protected Database getDatabase() {
			return JSFUtil.getBillingDatabase();
		}
		@Override
		protected String getViewPrefix() {
			return "Recurring Charges\\";
		}

		@Override
		public DominoModelList<RecurringCharge> getNamedCollection(final String name, final String category) {
			if("mine".equals(name)) {
				DominoModelList<RecurringCharge> entities = new DominoModelList<RecurringCharge>(getDatabase(), "Recurring Charges\\All", null, RecurringCharge.class);
				entities.search(Client.Manager.get().getBillingQuery());
				entities.setResortOrder("Frequency", TabularDataModel.SORT_ASCENDING);
				return entities;
			}
			return super.getNamedCollection(name, category);
		}
	}
}
