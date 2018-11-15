package renderkit.dialog;

import com.ibm.xsp.extlib.renderkit.html_extended.dialog.DialogButtonBarRenderer;

public class AceDialogButtonBarRenderer extends DialogButtonBarRenderer {

	@Override
	protected Object getProperty(final int prop) {
		switch(prop) {
		case PROP_PANELSTYLECLASS:          return "modal-footer"; // $NON-NLS-1$
		}
		return super.getProperty(prop);
	}

}