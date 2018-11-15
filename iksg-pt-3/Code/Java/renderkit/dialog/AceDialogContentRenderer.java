package renderkit.dialog;

import com.ibm.xsp.extlib.renderkit.html_extended.dialog.DialogContentRenderer;

public class AceDialogContentRenderer extends DialogContentRenderer {

	@Override
	protected Object getProperty(final int prop) {
		switch(prop) {
		case PROP_PANELSTYLECLASS:          return "modal-body"; // $NON-NLS-1$
		}
		return super.getProperty(prop);
	}

}