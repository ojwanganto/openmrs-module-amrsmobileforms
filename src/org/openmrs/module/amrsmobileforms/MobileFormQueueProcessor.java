package org.openmrs.module.amrsmobileforms;

import java.io.File;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsmobileforms.util.MobileFormEntryUtil;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


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
public class MobileFormQueueProcessor {

	private static final Log log = LogFactory.getLog(MobileFormQueueProcessor.class);
	private XPathFactory xPathFactory;
	private static Boolean isRunning = false; // allow only one running
	private static final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder docBuilder;
	
	public MobileFormQueueProcessor(){
		try{
			docBuilder = docBuilderFactory.newDocumentBuilder();
		}
		catch(Exception e){
			log.error("Problem occurred while creating document builder", e);
		}
	}

	/**
	 * Process all existing queue entries in the mobile form queue
	 * @param queue 
	 */
	private void processMobileForm(MobileFormQueue queue) throws APIException {
		log.debug("Transforming mobile form entry queue");
		String formData = queue.getFormData();
		String householdIdentifier = null;
		MobileFormEntryService mfse=(MobileFormEntryService)Context.getService(MobileFormEntryService.class);
		
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			XPathFactory xpf = getXPathFactory();
			XPath xp = xpf.newXPath();
			Document doc = docBuilder.parse(IOUtils.toInputStream(formData));
			Node curNode=(Node) xp.evaluate(MobileFormEntryConstants.HOUSEHOLD_PREFIX + MobileFormEntryConstants.HOUSEHOLD_META_PREFIX, doc, XPathConstants.NODE);
			householdIdentifier = xp.evaluate(MobileFormEntryConstants.HOUSEHOLD_META_HOUSEHOLD_ID , curNode); 
			
			//pull out household data: includes meta, survey, economic, household_meta
			
			//Search for the identifier in the household database
			if (MobileFormEntryUtil.isNewHousehold(householdIdentifier)) {
				
				//create household
				log.debug("Creating a new household with id " + householdIdentifier);
				Household household = MobileFormEntryUtil.getHousehold(doc, xp);
				
				//Add economic
				for (Economic economic : MobileFormEntryUtil.getEconomic(doc, xp)) {
					household.addEconomic(economic);
				}
				//Add Survey
				household.addSurvey(MobileFormEntryUtil.getSurvey(doc, xp));
				//Save the household
				mfse.createHouseholdInDatabase(household);
				
				//Archive this file
				saveFormInArchive(queue.getFileSystemUrl());
				
			}else{
				//compare the households
				log.debug("Will edit household with id " + householdIdentifier);
				//Archive this file
				saveFormInArchive(queue.getFileSystemUrl());
			}
		}
		catch (Throwable t) {
			log.error("Error while parsing mobile entry (" + householdIdentifier + ")", t);
			//put file in error table and move it to error directory
			saveFormInError(queue.getFileSystemUrl());
			mfse.saveErrorInDatabase(MobileFormEntryUtil.
					createError(getFormName(queue.getFileSystemUrl()), "Error Passing household form", t.getMessage()));
		}
	}
	
	/**
	 * Transform the next pending MobileFormQueue entry. If there are no pending
	 * items in the queue, this method simply returns quietly.
	 * 
	 * @return true if a queue entry was processed, false if queue was empty
	 */
	public void processMobileFormQueue() {
		MobileFormEntryService mobileService;
		synchronized (isRunning) {
			if (isRunning) {
				log.warn("MobileFormsQueue processor aborting (another processor already running)");
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
			File queueDir = MobileFormEntryUtil.getMobileFormsQueueDir();
			for (File file : queueDir.listFiles()) {
				MobileFormQueue queue = mobileService.getMobileFormEntryQueue(file.getAbsolutePath());
				processMobileForm(queue);
			}
		}
		catch(Exception e){
			log.error("Problem occured while processing Xforms queue", e);
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
	 * Archives a mobile form after successful processing
	 */
	private void saveFormInArchive(String formPath){
		String archiveFilePath= MobileFormEntryUtil.getMobileFormsArchiveDir(new Date()).getAbsolutePath() + getFormName(formPath);
		
		saveForm(formPath, archiveFilePath);
	}

	/**
	 * Stores an erred form in the error directory
	 * @param formPath 
	 */
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
	 * @return XPathFactory to be used for obtaining data from the parsed XML
	 */
	private XPathFactory getXPathFactory() {
		if (xPathFactory == null)
			xPathFactory = XPathFactory.newInstance();
		return xPathFactory;
	}
}

