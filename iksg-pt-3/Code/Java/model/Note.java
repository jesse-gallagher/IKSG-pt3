package model;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Table;

import org.openntf.domino.*;

import util.JSFUtil;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.model.TabularDataModel;

import frostillicus.xsp.bean.ApplicationScoped;
import frostillicus.xsp.bean.ManagedBean;
import frostillicus.xsp.model.domino.AbstractDominoManager;
import frostillicus.xsp.model.domino.AbstractDominoModel;
import frostillicus.xsp.model.domino.DominoModelList;
import frostillicus.xsp.util.FrameworkUtils;

@Table(name="Note")
public class Note extends AbstractDominoModel {
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
	public Project getProject() {
		String projectId = (String)getValue("ProjectID");
		if(StringUtil.isEmpty(projectId)) {
			return null;
		}
		return (Project)Project.Manager.get().getValue(projectId);
	}

	@ManagedBean(name="Notes")
	@ApplicationScoped
	public static class Manager extends AbstractDominoManager<Note> {
		private static final long serialVersionUID = 1L;

		public static Manager get() {
			Note.Manager instance = (Note.Manager)FrameworkUtils.resolveVariable(Note.Manager.class.getAnnotation(ManagedBean.class).name());
			return instance == null ? new Note.Manager() : instance;
		}

		@Override
		protected Database getDatabase() {
			return JSFUtil.getDataDatabase();
		}
		@Override
		protected String getViewPrefix() {
			return "Notes\\";
		}

		@Override
		public DominoModelList<Note> getNamedCollection(final String name, final String category) {
			if("mine".equals(name)) {
				DominoModelList<Note> tasks = new DominoModelList<Note>(getDatabase(), "Notes\\All", null, Note.class);
				tasks.search(Client.Manager.get().getClientQuery());
				tasks.setResortOrder("$Created", TabularDataModel.SORT_DESCENDING);
				return tasks;
			}
			return super.getNamedCollection(name, category);
		}

	}
}
