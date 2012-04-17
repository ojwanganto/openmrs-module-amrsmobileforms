/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.amrsmobileforms.web;

import org.openmrs.api.context.Context;
import org.openmrs.module.amrsmobileforms.EconomicConceptMap;
import org.openmrs.module.amrsmobileforms.MobileFormEntryService;

/**
 *
 * @author jkeiper
 */
public class DWRAMRSMobileFormsService {
	public EconomicConceptMap saveEconomicConceptMap(Integer id, Integer economicId, Integer conceptId) {
		MobileFormEntryService service = Context.getService(MobileFormEntryService.class);

		EconomicConceptMap ecm;
		
		if (id == null) {
			ecm = new EconomicConceptMap();
		} else {
			ecm = service.getEconomicConceptMap(id);
		}
		
		ecm.setEconomic(service.getEconomicObjectById(economicId));
		ecm.setConcept(Context.getConceptService().getConcept(conceptId));

		return service.saveEconomicConceptMap(ecm);
	}
}
