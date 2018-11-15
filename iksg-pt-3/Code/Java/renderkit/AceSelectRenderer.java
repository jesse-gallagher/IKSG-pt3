package renderkit;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.renderkit.html_basic.MenuRenderer;

public class AceSelectRenderer extends MenuRenderer {
	@Override
	public void encodeEnd(final FacesContext facesContext, final UIComponent c) throws IOException {
		super.encodeEnd(facesContext, c);

		String id = c.getClientId(facesContext);
		ResponseWriter w = facesContext.getResponseWriter();
		w.startElement("script", null);
		w.writeAttribute("type", "text/javascript", null);
		w.writeText("XSP.addOnLoad(function() {\n" +
				"	var input = dojo.byId('" + id + "');\n" +
				"	if(!input.__chosenified) {" +
				"		jQuery(input).chosen().on('change', function() {\n" +
				"			input.dispatchEvent(new Event('change'));\n" +
				"		})\n" +
				"		input.__chosenified = true\n" +
				"	}\n" +
				"})", null);
		w.endElement("script");
	}
}
