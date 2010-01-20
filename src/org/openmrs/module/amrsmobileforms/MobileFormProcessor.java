package org.openmrs.module.amrsmobileforms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Processes Composite Mobile forms (With both household and patient data) .
 * 
 * First splits the forms to create distinct forms for each individual then sends the
 * forms to xform module for processing.
 * Then process the form to extract household and survey data 
 * 
 * @author Samuel Mbugua
 */

public class MobileFormProcessor {
	
	private static final Log log = LogFactory.getLog(MobileFormProcessor.class);
	private MobileFormSplitProcessor splitter = null;
	private MobileFormQueueProcessor queueProcessor = null;
	private MobileFormUploadProcessor uploadProcessor = null;
	
	
	public void processMobileForms() {
		log.debug("Splitting Mobile forms");
		
		//First split submitted xforms
		if (splitter == null)
			splitter=new MobileFormSplitProcessor();
		splitter.splitForms();
		
		//Process household data part of xforms 
		if (queueProcessor == null)
			queueProcessor=new MobileFormQueueProcessor();
		queueProcessor.processMobileFormQueue();
		
		//Finally upload patients to xforms module for processing
		if (uploadProcessor == null)
			uploadProcessor=new MobileFormUploadProcessor();
		uploadProcessor.processMobileFormSplitQueue();
	}
}

