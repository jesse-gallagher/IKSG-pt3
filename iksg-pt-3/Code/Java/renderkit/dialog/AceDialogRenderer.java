package renderkit.dialog;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.renderkit.html_extended.dialog.DialogRenderer;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;

public class AceDialogRenderer extends DialogRenderer {

	@Override
	protected String getDefaultDojoType(final FacesContext context, final FacesDojoComponent component) {
		return "dijit.Dialog"; // $NON-NLS-1$
	}

	@Override
	protected DojoModuleResource getDefaultDojoModule(final FacesContext context, final FacesDojoComponent component) {
		return ExtLibResources.extlibDialog;
	}

	@Override
	public void dialogEncodeBegin(final FacesContext facesContext, final UIComponent component) throws IOException {
		ResponseWriter w = facesContext.getResponseWriter();
		w.startElement("div", component);
		w.writeAttribute("class", "tester", null);

		super.dialogEncodeBegin(facesContext, component);
	}
	@Override
	public void dialogEncodeEnd(final FacesContext facesContext, final UIComponent component) throws IOException {
		super.dialogEncodeEnd(facesContext, component);

		ResponseWriter w = facesContext.getResponseWriter();
		w.endElement("div");
	}

	@Override
	public void encodeChildren(final FacesContext facesContext, final UIComponent component) throws IOException {
		ResponseWriter w = facesContext.getResponseWriter();
		w.startElement("div", component);
		w.writeAttribute("class", "test2", null);
		super.encodeChildren(facesContext, component);
		w.endElement("div");
	}
}