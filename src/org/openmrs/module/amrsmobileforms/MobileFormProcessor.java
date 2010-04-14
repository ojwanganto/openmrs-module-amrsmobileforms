package org.openmrs.module.amrsmobileforms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Processes Composite Mobile forms (With both household and patient data) .
 * 
 * First splits the forms to create distinct forms for each individual then
 * sends the forms to xform module for processing. Then process the form to
 * extract household and survey data
 * 
 * @author Samuel Mbugua
 */

public class MobileFormProcessor {

	private static final Log log = LogFactory.getLog(MobileFormProcessor.class);
	private MobileFormSplitProcessor splitProcessor = null;
	private MobileFormQueueProcessor queueProcessor = null;
	private MobileFormUploadProcessor uploadProcessor = null;
	private MobileFormHouseholdLinksProcessor linksProcessor = null;

	public void processMobileForms() {
		log.debug("Processing Mobile forms");

		// First process household data part of xforms
		if (queueProcessor == null)
			queueProcessor = new MobileFormQueueProcessor();
		queueProcessor.processMobileFormQueue();

		// Split submitted xforms
		if (splitProcessor == null)
			splitProcessor = new MobileFormSplitProcessor();
		splitProcessor.splitForms();
		
		// Upload patients to xforms module for processing
		if (uploadProcessor == null)
			uploadProcessor = new MobileFormUploadProcessor();
		uploadProcessor.processMobileFormUploadQueue();

		// Finally link patients to households
		if (linksProcessor == null)
			linksProcessor = new MobileFormHouseholdLinksProcessor();
		linksProcessor.processMobileFormPendingLinkQueue();
	}
}