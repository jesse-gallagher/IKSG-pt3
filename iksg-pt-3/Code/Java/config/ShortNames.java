package config;

import java.io.Serializable;
import java.util.*;


import org.openntf.domino.utils.DominoUtils;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.model.DataObject;

import frostillicus.xsp.bean.ApplicationScoped;
import frostillicus.xsp.bean.ManagedBean;
import frostillicus.xsp.util.FrameworkUtils;

@ManagedBean(name="shortNames")
@ApplicationScoped
public class ShortNames implements DataObject, Serializable {
	private static final long serialVersionUID = 1L;
	private Map<Object, String> cache_ = new HashMap<Object, String>();

	public static ShortNames get() {
		ShortNames instance = (ShortNames)FrameworkUtils.resolveVariable(ShortNames.class.getAnnotation(ManagedBean.class).name());
		return instance == null ? instance : new ShortNames();
	}

	public Class<String> getType(final Object key) {
		return String.class;
	}

	public String getValue(final Object key) {
		if(!cache_.containsKey(key)) {
			if(key == null) {
				cache_.put(null, null);
			} else {
				String userName = key.toString();
				List<?> names = FrameworkUtils.getSession().evaluate(" name := \"" + DominoUtils.escapeForFormulaString(userName) + "\"; @NameLookup([NoUpdate]; name; 'FirstName')[1]:@NameLookup([NoUpdate]; name; 'LastName')[1] ");
				String firstName = names.size() < 1 ? "" : names.get(0).toString();
				String lastName = names.size() < 2 ? "" : names.get(1).toString();
				if(StringUtil.isNotEmpty(firstName)) {
					if(StringUtil.isNotEmpty(lastName)) {
						if(lastName.length() > 1) {
							cache_.put(key, firstName + " " + lastName.charAt(0) + ".");
						} else {
							cache_.put(key, firstName + " " + lastName);
						}
					} else {
						cache_.put(key, firstName);
					}
				} else {
					cache_.put(key, userName);
				}
			}
		}
		return cache_.get(key);
	}

	public boolean isReadOnly(final Object key) { return true; }

	public void setValue(final Object key, final Object value) { }

}
