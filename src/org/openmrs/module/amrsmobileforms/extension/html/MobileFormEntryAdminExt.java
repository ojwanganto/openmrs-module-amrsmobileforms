package org.openmrs.module.amrsmobileforms.extension.html;

import java.util.Map;
import java.util.TreeMap;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;
import org.openmrs.util.InsertedOrderComparator;

/**
 *
 */
@SuppressWarnings("deprecation")
public class MobileFormEntryAdminExt extends AdministrationSectionExt {
	
	private static String requiredPrivileges = null;
	

	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	

	public String getTitle() {
		return "mobileformentry.title";
	}
	
	public String getRequiredPrivilege() {
		if (requiredPrivileges == null) {
			StringBuilder builder = new StringBuilder();
			requiredPrivileges = builder.toString();
		}
		
		return requiredPrivileges;
	}
	
	public Map<String, String> getLinks() {
		
		Map<String, String> map = new TreeMap<String, String>(new InsertedOrderComparator());
		map.put("module/mobileformentry/propertiesPage.form", "mobileformentry.properties");
		map.put("module/mobileformentry/mobileResources.list", "mobileformentry.mobileResources");
		map.put("module/mobileformentry/economicObject.form", "mobileformentry.economicObjects");
		map.put("module/mobileformentry/resolveErrors.list", "mobileformentry.resolveErrors.title");
		return map;
	}
	
}
