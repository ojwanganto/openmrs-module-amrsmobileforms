package org.openmrs.module.amrsmobileforms.web.controller;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import javax.servlet.http.HttpSession;
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
import org.openmrs.module.amrsmobileforms.Economic;
import org.openmrs.module.amrsmobileforms.Household;
import org.openmrs.module.amrsmobileforms.MobileFormEntryConstants;
import org.openmrs.module.amrsmobileforms.MobileFormEntryError;
import org.openmrs.module.amrsmobileforms.MobileFormEntryService;
import org.openmrs.module.amrsmobileforms.MobileFormQueue;
import org.openmrs.module.amrsmobileforms.util.GPSOutput;
import org.openmrs.module.amrsmobileforms.util.MobileFormEntryUtil;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXParseException;

/**
 * Controller for Mobile errors resolution jsp pages
 * 
 * @author Samuel Mbugua
 */
@Controller
public class FixGPSController {
	private static final Log log = LogFactory.getLog(FixGPSController.class);
	
	private XPathFactory xPathFactory;
	private static final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder docBuilder;
	private static GPSOutput gpsOutput;
	
	public FixGPSController(){
		try{
			docBuilder = docBuilderFactory.newDocumentBuilder();
		}
		catch(Exception e){
			log.error("Problem occurred while creating document builder", e);
		}
	}
	
	@ModelAttribute("formEntyGPSErrors")
	@RequestMapping(value="/module/amrsmobileforms/fixGps", method=RequestMethod.GET)
	public List<MobileFormEntryError> populateForm() {
		if (Context.isAuthenticated()) {
			MobileFormEntryService mfs = (MobileFormEntryService)Context.getService(MobileFormEntryService.class);
			return mfs.getAllMobileFormEntryErrors();
		}
		return null;
	}
	
	@RequestMapping(value="/module/amrsmobileforms/fixGps", method=RequestMethod.POST)
	public String resolveError(HttpSession httpSession, @RequestParam("errorId") List<Integer> errorIds){
		MobileFormEntryService mobileService;
		String filePath;
		
		// user must be authenticated (avoids authentication errors)
		if (Context.isAuthenticated()) {
			if (!Context.getAuthenticatedUser().hasPrivilege(
					MobileFormEntryConstants.PRIV_RESOLVE_MOBILE_FORM_ENTRY_ERROR)) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "amrsmobileforms.action.noRights");
				return "redirect:fixGps.list";
			}
			
			//confirm process forms task scheduler is running
			TaskDefinition prsFormsTask = Context.getSchedulerService().getTaskByName("Process Mobile Forms");
			if (prsFormsTask.getStarted()){
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Process Mobile Forms task is running. Please stop is first.");
				return "redirect:fixGps.list";
			}
			
				
			mobileService=Context.getService(MobileFormEntryService.class);
			
			//for every error confirm gps of form and db
			for (Integer errorId : errorIds) {
				MobileFormEntryError errorItem = mobileService.getErrorById(errorId);
				filePath= MobileFormEntryUtil.getMobileFormsErrorDir().getAbsolutePath() + errorItem.getFormName();
				
				//delete blank errors
				if (cleanError(errorItem, filePath, mobileService) && gpsError(errorItem))
					processMobileForm(createQueue(filePath, mobileService), errorItem);
			}
			
		}
		return "redirect:fixGps.list";
	}
	
	private static boolean cleanError(MobileFormEntryError error, String filePath, MobileFormEntryService mfs){
		File f =new File(filePath);
		if (!f.exists()) {
			mfs.deleteError(error);
			return false;
		}
		return true;
	}
	private static boolean gpsError(MobileFormEntryError error){
			String errorPrefix="A duplicate household different from this one exists with the same identifier (";
			//remove all other errors apart from the one we want
			if (error.getErrorDetails().startsWith(errorPrefix)){
				//delete this error from this list
				return true;
			}
				
		return false;
		
	}

	private static MobileFormQueue createQueue (String filePath, MobileFormEntryService mfs) {
		MobileFormQueue queue= mfs.getMobileFormEntryQueue(filePath);
		return queue;
	}

	private void processMobileForm(MobileFormQueue queue, MobileFormEntryError errorItem) throws APIException {
		String formData = queue.getFormData();
		String householdIdentifier = null;
		String householdGps=null;
		MobileFormEntryService mfes=(MobileFormEntryService)Context.getService(MobileFormEntryService.class);
		
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			XPathFactory xpf = getXPathFactory();
			XPath xp = xpf.newXPath();
			Document doc = docBuilder.parse(IOUtils.toInputStream(formData));
			Node curNode=(Node) xp.evaluate(MobileFormEntryConstants.HOUSEHOLD_PREFIX + MobileFormEntryConstants.HOUSEHOLD_META_PREFIX, doc, XPathConstants.NODE);
			householdIdentifier = xp.evaluate(MobileFormEntryConstants.HOUSEHOLD_META_HOUSEHOLD_ID , curNode); 
			householdGps = xp.evaluate(MobileFormEntryConstants.HOUSEHOLD_META_GPS_LOCATION, curNode);
			
			// check household identifier and gps were entered correctly
			if (householdIdentifier == null || householdIdentifier.trim() == "" ||
					householdGps == null || householdGps.trim() == ""){
				log.debug("Null household identifier or GPS");
				return;
			}
			
			/** pull out household data: includes meta, survey, economic, household_meta */
			
			//Search for the identifier in the household database
			if (MobileFormEntryUtil.isNewHousehold(householdIdentifier)){
				log.debug("Sorry could not get household " + householdIdentifier);
			}else{
				
				Household household = mfes.getHousehold(householdIdentifier);
				//if GPS is same update with new one
				if (isSameHousehold(household.getGpsLocation(), householdGps)) {
					household = MobileFormEntryUtil.getHousehold(mfes.getHousehold(householdIdentifier), doc, xp);
					//update household
					log.error("updating household with id " + householdIdentifier);
					
					//Add economic
					for (Economic economic : MobileFormEntryUtil.getEconomic(doc, xp)) {
						household.addEconomic(economic);
					}
					//Add Survey
					household.addSurvey(MobileFormEntryUtil.getSurvey(doc, xp));
					//Save the household
					mfes.saveHousehold(household);
					
					//queue form for splitting
					saveFormInPendingSplit(queue.getFileSystemUrl());
					
					//delete error item from db
					mfes.deleteError(errorItem);
				}
				
			}
		}
		catch (SAXParseException s){
			log.error("An invalid household file.");
		}
		catch (Throwable t) {
			log.error("Error while parsing mobile entry (" + householdIdentifier + ")");
			if (householdIdentifier == null)
				mfes.deleteError(errorItem);
		}
	}
	
	private XPathFactory getXPathFactory() {
		if (xPathFactory == null)
			xPathFactory = XPathFactory.newInstance();
		return xPathFactory;
	}
	
	private static void saveForm(String oldFormPath, String newFormPath){
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
	
	private void saveFormInPendingSplit(String formPath){
		String pendingSplitFilePath= MobileFormEntryUtil.getMobileFormsPendingSplitDir().getAbsolutePath() + getFormName(formPath);
		
		saveForm(formPath, pendingSplitFilePath);
	}
	
	private String getFormName(String formPath) {
		return formPath.substring(formPath.lastIndexOf(File.separatorChar)); 
	}
	
	public boolean isSameHousehold(String dbGps, String newGps){
		boolean same=false;
		if (dbGps.equals(getGPS(newGps)) || dbGps.equals(getGPSNew(newGps)))
			same=true;
		GPSOutput outfile = getGpsOutput();
		outfile.createComparison(dbGps + "," + getGPS(newGps) + "," + getGPSNew(newGps)  + "," + same + "\n");
		return same;
	}
	
	private static String getGPS(String s) {
        String[] sa = s.split(" ");
        return formatGps(Double.parseDouble(sa[0]),"lat") + " " + formatGps(Double.parseDouble(sa[1]),"lon");
    }
	
	private static String getGPSNew(String s) {
        String[] sa = s.split(" ");
        return formatGpsNew(Double.parseDouble(sa[0]),"lat") + " " + formatGpsNew(Double.parseDouble(sa[1]),"lon");
    }
	
	private static String formatGps(double gps, String locatType) {
		String location= Double.toString(gps);
    	String degreeSign="\u00B0";
    	String degree=location.substring(0, location.indexOf("."))+degreeSign;
    	location="0." +location.substring(location.indexOf(".") + 1);
    	double temp=Double.valueOf(location)*60;
    	location= Double.toString(temp);
    	String mins=location.substring(0, location.indexOf(".")) + "'";
    	
    	location="0." +location.substring(location.indexOf(".") + 1);
    	temp=Double.valueOf(location)*60;
    	location= Double.toString(temp);
    	String secs=location.substring(0, location.indexOf(".")) + '"';
    	if (locatType.equalsIgnoreCase("lon")){
    		if (degree.startsWith("-")){
    			degree="W " + degree.replace("-", "") + mins + secs;
    		}
    		else
    			degree="E " + degree.replace("-", "") + mins + secs;
    	}
    	else {
    		if (degree.startsWith("-")){
    			degree="S " + degree.replace("-", "") + mins + secs;
    		}
    		else
    			degree="N " + degree.replace("-", "") + mins + secs;
    	}
    	return degree;
	}
	
	 private static String formatGpsNew(double coordinates, String type) {
	        String location = Double.toString(coordinates);
	        String degreeSign = "\u00B0";
	        
	      //set degree section DD
	        String degree = location.substring(0, location.indexOf(".")) + degreeSign;
	        
	        //set the minutes part MM
	        location = "0." + location.substring(location.indexOf(".") + 1);
	        double temp = Double.valueOf(location) * 60;
	        location = Double.toString(temp);
	        String mins = location.substring(0, location.indexOf(".")) + "'";
	        
	        //set the seconds part SS.s
	        location = "0." + location.substring(location.indexOf(".") + 1);
	        temp = Double.valueOf(location) * 60;
	        String secs = roundToOneDecimalPlace(temp) + '"';
	        
	        if (type.equalsIgnoreCase("lon")) {
	            if (degree.startsWith("-")) {
	                degree = "W" + degree.replace("-", "") + mins + secs;
	            } else
	                degree = "E" + degree.replace("-", "") + mins + secs;
	        } else {
	            if (degree.startsWith("-")) {
	                degree = "S" + degree.replace("-", "") + mins + secs;
	            } else
	                degree = "N" + degree.replace("-", "") + mins + secs;
	        }
	        return degree;
	    }
	    
	    private static String roundToOneDecimalPlace(double dbl){
	    	DecimalFormat decimalFormat = new DecimalFormat( "#,###,###,##0.0" );
			String formated = decimalFormat.format(dbl);
			return formated.substring(0,formated.indexOf(".") ) + formated.substring(formated.indexOf(".") + 1);
	    }
	    
	    private static GPSOutput getGpsOutput() {
			if (gpsOutput == null) {
				try {
					gpsOutput= new GPSOutput();
				}catch (APIException e) {
					log.debug("GPSLogger not found");
					return null;
				}
			}
			return gpsOutput;
		}
}	