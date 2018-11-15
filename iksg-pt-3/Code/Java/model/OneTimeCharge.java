package model;

import java.util.Arrays;
import java.util.Collection;

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

@Table(name="One-Time Charge")
public class OneTimeCharge extends AbstractDominoModel {
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

	@ManagedBean(name="OneTimeCharges")
	@ApplicationScoped
	public static class Manager extends AbstractDominoManager<OneTimeCharge> {
		private static final long serialVersionUID = 1L;

		public static Manager get() {
			OneTimeCharge.Manager instance = (OneTimeCharge.Manager)FrameworkUtils.resolveVariable(OneTimeCharge.Manager.class.getAnnotation(ManagedBean.class).name());
			return instance == null ? new OneTimeCharge.Manager() : instance;
		}

		@Override
		protected Database getDatabase() {
			return JSFUtil.getBillingDatabase();
		}

		@Override
		protected String getViewPrefix() {
			return "One-Time Charges\\";
		}

		@Override
		public DominoModelList<OneTimeCharge> getNamedCollection(final String name, final String category) {
			if("mine".equals(name)) {
				DominoModelList<OneTimeCharge> entities = new DominoModelList<OneTimeCharge>(getDatabase(), "One-Time Charges\\All", null, OneTimeCharge.class);
				entities.search(Client.Manager.get().getBillingQuery());
				entities.setResortOrder("Date", TabularDataModel.SORT_DESCENDING);
				return entities;
			}
			return super.getNamedCollection(name, category);
		}
	}
}
