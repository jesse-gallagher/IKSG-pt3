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

@Table(name="Rate")
public class Rate extends AbstractDominoModel {
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

	@ManagedBean(name="Rates")
	@ApplicationScoped
	public static class Manager extends AbstractDominoManager<Rate> {
		private static final long serialVersionUID = 1L;

		public static Manager get() {
			Rate.Manager instance = (Rate.Manager)FrameworkUtils.resolveVariable(Rate.Manager.class.getAnnotation(ManagedBean.class).name());
			return instance == null ? new Rate.Manager() : instance;
		}

		@Override
		protected Database getDatabase() {
			return JSFUtil.getBillingDatabase();
		}

		@Override
		protected String getViewPrefix() {
			return "Rates\\";
		}

		@Override
		public DominoModelList<Rate> getNamedCollection(final String name, final String category) {
			if("mine".equals(name)) {
				DominoModelList<Rate> entities = new DominoModelList<Rate>(getDatabase(), "Rates\\All", null, Rate.class);
				entities.search(Client.Manager.get().getBillingQuery());
				entities.setResortOrder("Summary", TabularDataModel.SORT_ASCENDING);
				return entities;
			}
			return super.getNamedCollection(name, category);
		}
	}

}
