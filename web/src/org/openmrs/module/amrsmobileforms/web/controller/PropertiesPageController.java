package org.openmrs.module.amrsmobileforms.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.amrsmobileforms.MobileFormEntryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PropertiesPageController {
	
	@RequestMapping(value="/module/mobileformentry/propertiesPage", method=RequestMethod.GET)
	public Map<String, Object> populateForm() {
		MobileFormEntryService mfs = (MobileFormEntryService)Context.getService(MobileFormEntryService.class);
		Map<String, Object> map =new HashMap<String, Object>();
		map.put("systemVars", mfs.getSystemVariables());
		return map;
		
	}
}
