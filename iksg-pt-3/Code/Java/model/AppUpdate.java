package model;

import javax.persistence.Table;

import org.openntf.domino.Database;

import util.JSFUtil;

import frostillicus.xsp.bean.ApplicationScoped;
import frostillicus.xsp.bean.ManagedBean;
import frostillicus.xsp.model.domino.AbstractDominoManager;
import frostillicus.xsp.model.domino.AbstractDominoModel;
import frostillicus.xsp.util.FrameworkUtils;

@Table(name="App Update")
public class AppUpdate extends AbstractDominoModel {
	private static final long serialVersionUID = 1L;

	@ManagedBean(name="AppUpdates")
	@ApplicationScoped
	public static class Manager extends AbstractDominoManager<AppUpdate> {
		private static final long serialVersionUID = 1L;

		public static Manager get() {
			AppUpdate.Manager instance = (AppUpdate.Manager)FrameworkUtils.resolveVariable(AppUpdate.Manager.class.getAnnotation(ManagedBean.class).name());
			return instance == null ? new AppUpdate.Manager() : instance;
		}

		@Override
		protected String getViewPrefix() {
			return "App Updates\\";
		}

		@Override
		protected Database getDatabase() {
			return JSFUtil.getUpdatesDatabase();
		}
	}
}
