package org.openmrs.module.amrsmobileforms;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsmobileforms.util.MobileFormEntryUtil;
import org.openmrs.module.amrsmobileforms.util.XFormEditor;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;


/**
 * Processes Mobile forms Queue entries.
 * 
 * When the processing is successful, the queue entry is submitted to the XFormEntry Queue.
 * For unsuccessful processing, the queue entry is put in the Mobile forms error folder.
 * 
 * @author Samuel Mbugua
 *
 */
@Transactional
public class MobileFormSplitProcessor {

	private static final Log log = LogFactory.getLog(MobileFormSplitProcessor.class);
	private static Boolean isRunning = false; // allow only one running
	private static final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder docBuilder;
	private MobileFormEntryService mobileService;
	
	public MobileFormSplitProcessor(){
		try{
			docBuilder = docBuilderFactory.newDocumentBuilder();
			this.getMobileService();
		}
		catch(Exception e){
			log.error("Problem occurred while creating document builder", e);
		}
	}

	/**
	 * Process all existing queue entries in the mobile form queue
	 * @param queue 
	 */
	private boolean splitMobileForm(MobileFormQueue queue) throws APIException {
		String formData = queue.getFormData();
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(IOUtils.toInputStream(formData));
			log.debug("Splitting mobile xforms");
			XFormEditor.createIndividualFiles(doc);
		}
		catch (Throwable t) {
			log.error("Error splitting document", t);
			//Move form to error queue
			saveFormInError(queue.getFileSystemUrl());
			mobileService.saveErrorInDatabase(MobileFormEntryUtil.
					createError(getFormName(queue.getFileSystemUrl()), "Error splitting document", t.getMessage()));
			return false;
		}
		return true;
	}
	
	/**
	 * Split the next pending MobileForm drop entry. If there are no pending
	 * items in the drop_dir, this method simply returns quietly.
	 * 
	 * @return true if a queue entry was processed, false if queue was empty
	 */
	public void splitForms() {
		MobileFormEntryService mobileService;
		synchronized (isRunning) {
			if (isRunning) {
				log.warn("MobileForms splitting process aborting (another process already running)");
				return;
			}
			isRunning = true;
		}
		try {
			mobileService= (MobileFormEntryService)Context.getService(MobileFormEntryService.class);
		}catch (APIException e) {
			log.debug("MobileFormEntryService not found");
			return;
		}
		try {			
			File dropDir = MobileFormEntryUtil.getMobileFormsDropDir();
			for (File file : dropDir.listFiles()) {
				MobileFormQueue queue = mobileService.getMobileFormEntryQueue(file.getAbsolutePath());
				if (splitMobileForm(queue))
					//Move form to queue for processing
					putFormInQueue(queue.getFileSystemUrl());
			}
		}
		catch(Exception e){
			log.error("Problem occured while splitting", e);
		}
		finally {
			isRunning = false;
		}
	}
	

	/**
	 * Stores a form in a specified folder after processing.
	 */
	private void saveForm(String oldFormPath, String newFormPath){
		try{
			if(oldFormPath != null){
				File file=new File(oldFormPath);
				
				//move the file to specified new directory
				file.renameTo(new File(newFormPath));
			}
		}
		catch(Exception e){
			log.error(e.getMessage(),e);
		}

	}

	/**
	 * Moves split file to the queue for processing
	 */
	private void putFormInQueue(String formPath){
		String queueFilePath= MobileFormEntryUtil.getMobileFormsQueueDir().getAbsolutePath() + getFormName(formPath);
		
		saveForm(formPath, queueFilePath);
	}
	
	private void saveFormInError(String formPath){
		String errorFilePath= MobileFormEntryUtil.getMobileFormsErrorDir().getAbsolutePath() + getFormName(formPath);
		saveForm(formPath, errorFilePath);
	}
	
	/**
	 * Extracts form name from an absolute file path
	 * @param formPath
	 * @return
	 */
	private String getFormName(String formPath) {
		return formPath.substring(formPath.lastIndexOf(File.separatorChar)); 
	}
	
	/**
	 * @return MobileFormEntryService to be used by the process
	 */
	private MobileFormEntryService getMobileService() {
		if (mobileService == null) {
			try {
				mobileService= (MobileFormEntryService)Context.getService(MobileFormEntryService.class);
			}catch (APIException e) {
				log.debug("MobileFormEntryService not found");
				return null;
			}
		}
		return mobileService;
	}
}

