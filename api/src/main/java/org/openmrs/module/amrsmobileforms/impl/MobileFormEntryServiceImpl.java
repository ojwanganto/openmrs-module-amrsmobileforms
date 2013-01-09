package org.openmrs.module.amrsmobileforms.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.IOUtils;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kxml2.io.KXmlSerializer;
import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.mockito.Mockito;
import org.openmrs.Encounter;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;

import org.openmrs.module.amrsmobileforms.Economic;
import org.openmrs.module.amrsmobileforms.EconomicConceptMap;
import org.openmrs.module.amrsmobileforms.EconomicObject;
import org.openmrs.module.amrsmobileforms.HouseholdMember;
import org.openmrs.module.amrsmobileforms.MobileFormEntryError;
import org.openmrs.module.amrsmobileforms.MobileFormEntryService;
import org.openmrs.module.amrsmobileforms.MobileFormHousehold;
import org.openmrs.module.amrsmobileforms.MobileFormQueue;
import org.openmrs.module.amrsmobileforms.Survey;
import org.openmrs.module.amrsmobileforms.SyncLogModel;

import org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO;
import org.openmrs.module.amrsmobileforms.util.MobileFormEntryUtil;
import org.openmrs.module.amrsmobileforms.util.XFormEditor;
import org.openmrs.module.xforms.Xform;
import org.openmrs.module.xforms.XformBuilder;
import org.openmrs.module.xforms.XformConstants;
import org.openmrs.module.xforms.XformObsEdit;
import org.openmrs.module.xforms.XformsService;
import org.openmrs.module.xforms.formentry.FormEntryWrapper;
import org.openmrs.util.FormUtil;
import org.springframework.util.StringUtils;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Samuel Mbugua
 *
 */
public class MobileFormEntryServiceImpl implements MobileFormEntryService {
	private static Log log = LogFactory.getLog(MobileFormEntryServiceImpl.class);
	
	private MobileFormEntryDAO dao;
	
	public MobileFormEntryServiceImpl() {
	}
	
	@SuppressWarnings("unused")
	private MobileFormEntryDAO getMobileFormEntryDAO() {
		return dao;
	}
	
	public void setMobileFormEntryDAO(MobileFormEntryDAO dao) {
		this.dao = dao;
	}
	

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getMobileFormEntryQueue(java.lang.String)
	 */
	public MobileFormQueue getMobileFormEntryQueue(String absoluteFilePath) {
		MobileFormQueue queueItem = new MobileFormQueue();
		queueItem.setFileSystemUrl(absoluteFilePath);
		log.debug(absoluteFilePath);
		return queueItem;
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getSystemVariables()
	 */
	public SortedMap<String,String> getSystemVariables() {
		TreeMap<String, String> systemVariables = new TreeMap<String, String>();
		systemVariables.put("MOBILE_FORMS_RESOURCES_DIR", MobileFormEntryUtil.getMobileFormsResourcesDir().getAbsolutePath());
		systemVariables.put("MOBILE_FORMS_DROP_DIR", MobileFormEntryUtil.getMobileFormsDropDir().getAbsolutePath());
		systemVariables.put("MOBILE_FORMS_QUEUE_DIR", MobileFormEntryUtil.getMobileFormsQueueDir().getAbsolutePath());
		systemVariables.put("MOBILE_FORMS_ARCHIVE_DIR", MobileFormEntryUtil.getMobileFormsArchiveDir(null).getAbsolutePath());
		systemVariables.put("MOBILE_FORMS_ERROR_DIR", MobileFormEntryUtil.getMobileFormsErrorDir().getAbsolutePath());
		return systemVariables;
	}
	
	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getMobileResources()
	 */
	public List<File> getMobileResources() {
		File resourcesDir=MobileFormEntryUtil.getMobileFormsResourcesDir();
		List<File> lst=new ArrayList<File>();
		for(File file:resourcesDir.listFiles()) {
			lst.add(file);
		}
		return lst;
	}
	
	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getHousehold(java.lang.String)
	 */
	public MobileFormHousehold getHousehold(String householdIdentifier) {
		return dao.getHousehold(householdIdentifier);
	}
	
	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#createHouseholdInDatabase(org.openmrs.module.amrsmobileforms.MobileFormHousehold)
	 */
	public void saveHousehold(MobileFormHousehold household) {
		dao.saveHousehold(household);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getEconomicObjectByObjectName(java.lang.String)
	 */
	public EconomicObject getEconomicObjectByObjectName(String objectName) {
		return dao.getEconomicObjectByObjectName(objectName);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#createEconomicInDatabase(org.openmrs.module.amrsmobileforms.Economic)
	 */
	public void createEconomicInDatabase(Economic economic) {
		dao.createEconomicInDatabase(economic);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#createSurvey(org.openmrs.module.amrsmobileforms.Survey)
	 */
	public void createSurvey(Survey survey) {
		dao.createSurvey(survey);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getAllEconomicObjects()
	 */
	public List<EconomicObject> getAllEconomicObjects() {
		return dao.getAllEconomicObjects();
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#saveEconomicObject(org.openmrs.module.amrsmobileforms.EconomicObject)
	 */
	public void saveEconomicObject(EconomicObject economicObject) {
		dao.saveEconomicObject(economicObject);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#deleteEconomicObject(org.openmrs.module.amrsmobileforms.EconomicObject)
	 */
	public boolean deleteEconomicObject(EconomicObject economicObject) {
		return dao.deleteEconomicObject(economicObject);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#saveErrorInDatabase(org.openmrs.module.amrsmobileforms.MobileFormEntryError)
	 */
	public void saveErrorInDatabase(MobileFormEntryError mobileFormEntryError) {
		dao.saveErrorInDatabase(mobileFormEntryError);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getAllMobileFormEntryErrors()
	 */
	public List<MobileFormEntryError> getAllMobileFormEntryErrors() {
		return dao.getAllMobileFormEntryErrors();
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getErrorById(java.lang.Integer)
	 */
	public MobileFormEntryError getErrorById(Integer errorId) {
		return dao.getErrorById(errorId);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getHouseholdMemberById(java.lang.Integer)
	 */
	public HouseholdMember getHouseholdMemberById(Integer identifier) {
		return dao.getHouseholdMemberById(identifier);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getEconomicObjectById(java.lang.Integer)
	 */
	public EconomicObject getEconomicObjectById(Integer economicObjectId) {
		return dao.getEconomicObjectById(economicObjectId);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#deleteError(org.openmrs.module.amrsmobileforms.MobileFormEntryError)
	 */
	public void deleteError(MobileFormEntryError error) {
		dao.deleteError(error);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#saveHouseholdMember(org.openmrs.module.amrsmobileforms.HouseholdMember)
	 */
	public void saveHouseholdMember(HouseholdMember householdMember) {
		dao.saveHouseholdMember(householdMember);
	}
	
	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getAllMembersInHousehold(java.lang.Integer)
	 */
	public List<HouseholdMember> getAllMembersInHousehold(MobileFormHousehold household) {
		return dao.getAllMembersInHousehold(household);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getSyncLog(java.util.Date)
	 */
	public List<SyncLogModel> getSyncLog(Date logDate) {
		List<SyncLogModel> logList = new ArrayList<SyncLogModel>();
		File logDir=MobileFormEntryUtil.getMobileFormsSyncLogDir();
		if (logDate == null)
			logDate=new Date();
		String logFileName = logDir.getAbsolutePath() + File.separator + "log-" + new SimpleDateFormat("yyyy-MM-dd").format(logDate) + ".log";
		File logFile = new File(logFileName);
		if (!logFile.exists())
			return null;			
		String line = null;
		try {
			BufferedReader input =  new BufferedReader(new FileReader(logFile));
			try {
				while (( line = input.readLine()) != null){
					if (line.indexOf(",")!=-1) {
						SyncLogModel logModel = getLogModel(line);
						if (logModel != null)
							logList.add(logModel);
					}
				}
			}
			finally {
				 input.close();
			 }
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
		return logList;
	}
	
	/**
	 * Takes a Comma Separated line and creates an object of type {@link SyncLogModel}
	 */
	private static SyncLogModel getLogModel(String line) {
		SyncLogModel syncLogModel = new SyncLogModel();
		// syncId
		if (line.indexOf(",") != -1) {
			syncLogModel.setSyncId(Integer.parseInt(line.substring(0,line.indexOf(","))));
			line=line.substring(line.indexOf(",") + 1);
		}else
			return null;
		// syncDate
		if (line.indexOf(",") != -1) {
			try {
				DateFormat df =new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
				syncLogModel.setSyncDate(df.parse(line.substring(0,line.indexOf(","))));
				line=line.substring(line.indexOf(",") + 1);
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}else
			return null;
		
		// providerId
		if (line.indexOf(",") != -1) {
			syncLogModel.setProviderId(line.substring(0,line.indexOf(",")));
			line=line.substring(line.indexOf(",") + 1);
		}else
			return null;
		
		// deviceId
		if (line.indexOf(",") != -1) {
			syncLogModel.setDeviceId(line.substring(0,line.indexOf(",")));
			line=line.substring(line.indexOf(",") + 1);
		}else
			return null;
		
		// 	householdId;
		if (line.indexOf(",") != -1) {
			syncLogModel.setHouseholdId(line.substring(0,line.indexOf(",")));
			line=line.substring(line.indexOf(",") + 1);
		}else
			return syncLogModel;
		
		// fileName;
		if (line.indexOf(",") != -1) {
			syncLogModel.setFileName(line.substring(0,line.indexOf(",")));
			line=line.substring(line.indexOf(",") + 1);
		}else
			return syncLogModel;
		
		// fileSize;
		if (line.indexOf(",") != -1) {
			syncLogModel.setFileSize(line.substring(0,line.indexOf(",")));
			line=line.substring(line.indexOf(",") + 1);
		}else
			syncLogModel.setFileSize(line);
		
		return syncLogModel;
	
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.amrsmobileforms.MobileFormEntryService#getAllSyncLogs()
	 */
	public List<String> getAllSyncLogs() {
		List<String> logFiles = new ArrayList<String>();
		File logDir=MobileFormEntryUtil.getMobileFormsSyncLogDir();
		DateFormat df =new SimpleDateFormat("yyyy-MM-dd");
		DateFormat df1 =new SimpleDateFormat("yyyy-MMM-dd");
		for (File file : logDir.listFiles()) {
			String fileName=file.getName();
			if (fileName.indexOf("-") != -1 && fileName.indexOf(".") != -1) {
				try {
					fileName=fileName.substring(fileName.indexOf("-")+1,fileName.lastIndexOf("."));
					Date date=df.parse(fileName);
					logFiles.add(df1.format(date));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		return logFiles;
	}

	public EconomicConceptMap getEconomicConceptMapFor(EconomicObject eo) {
		return dao.getEconomicConceptMapFor(eo);
	}

	public EconomicConceptMap getEconomicConceptMap(Integer id) {
		return dao.getEconomicConceptMap(id);
	}

	public EconomicConceptMap saveEconomicConceptMap(EconomicConceptMap ecm) {
		return dao.saveEconomicConceptMap(ecm);
	}

	/**
	 * @see MobileFormEntryService#renderExportForEncounter(org.openmrs.Encounter) 
	 */
	public String renderExportForEncounter(Encounter encounter) {
		if (encounter == null)
			return null;
		
		XformsService xformsService = Context.getService(XformsService.class);
		
		// create a mock request for the sake of rendering data
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		HttpSession session = Mockito.mock(HttpSession.class);
		Mockito.when(request.getSession()).thenReturn(session);
		
		// get the xform
		Xform xform = xformsService.getXform(encounter.getForm());
		String xformXml = xform.getXformXml();
		
		// start a new document from the xform template
		Document doc = XformBuilder.getDocument(xformXml);

		// set a few values
		XformBuilder.setNodeValue(doc, XformConstants.NODE_SESSION, request.getSession().getId());
		XformBuilder.setNodeValue(doc, XformConstants.NODE_UID, FormEntryWrapper.generateFormUid());
		XformBuilder.setNodeValue(doc, XformConstants.NODE_DATE_ENTERED, FormUtil.dateToString(encounter.getDateCreated()));
	
		// add enterer
		User user = encounter.getCreator();
		String enterer = user.getUserId() + "^" + user.getGivenName() + " " + user.getFamilyName();
		XformBuilder.setNodeValue(doc, XformConstants.NODE_ENTERER, enterer);

		// add provider
		// TODO see if this really works ... should be personId, right?
		List<User> providers = Context.getUserService().getUsersByPerson(encounter.getProvider(), true);
		if (!providers.isEmpty())
			XformBuilder.setNodeValue(doc, XformBuilder.NODE_ENCOUNTER_PROVIDER_ID, providers.get(0).getUserId().toString());

		// add encounter location
		XformBuilder.setNodeValue(doc, XformBuilder.NODE_ENCOUNTER_LOCATION_ID, encounter.getLocation().getLocationId().toString());
		
		// populate xform with patient information
		try {
			XformBuilder.setPatientFieldValues(encounter.getPatient(), encounter.getForm(), doc.getRootElement(), xformsService);
		} catch (Exception ex) {
			log.error("could not populate xform with patient information.", ex);
		}
		
		// populate xform with observations
		try {
			XformObsEdit.fillObs(request, doc, encounter.getEncounterId(), xformXml);
		} catch (Exception ex) {
			log.error("could not populate xform with observations.", ex);
		}

		// get the <form/> element ... we care most about it
		Element formNode = XformBuilder.getElement(doc.getRootElement(),"form");

		// get the patient, encounter and observation nodes
		Element patientNode = XformBuilder.getElement(formNode, "patient");
		Element encounterNode = XformBuilder.getElement(formNode, "encounter");
		Element obsNode = XformBuilder.getElement(formNode, "obs");

		// remove the aforementioned nodes from formNode
		removeChildNode(formNode, "patient");
		removeChildNode(formNode, "encounter");
		removeChildNode(formNode, "obs");
		
		// start over again with a clean document
		doc = new Document();
		
		// add the form node
		doc.addChild(0, Node.ELEMENT, formNode);

		// create the meta node and add it to the form
		// TODO use survey data for this
		Element metaNode = formNode.createElement(null, null);
		metaNode.setName("meta");
		addTextNode(metaNode, "start_time", "");
		addTextNode(metaNode, "end_time", "");
		addTextNode(metaNode, "device_id", "");
		addTextNode(metaNode, "subscriber_id", null);
		formNode.addChild(Node.ELEMENT, metaNode);
		
		// ... add <survey/> and <household> ...
		
		// create the individual(s) nodes
		Element individualsNode = formNode.createElement(null, null);
		individualsNode.setName("individuals");
		formNode.addChild(Node.ELEMENT, individualsNode);

		Element individualNode = individualsNode.createElement(null, null);
		individualNode.setName("individual");
		individualsNode.addChild(Node.ELEMENT, individualNode);
		
		// load up the individual node with stuff
		individualNode.addChild(Node.ELEMENT, patientNode);
		individualNode.addChild(Node.ELEMENT, encounterNode);
		individualNode.addChild(Node.ELEMENT, obsNode);
		
		// render the document to a string (stolen from XformBuilder#fromDoc2String)
		KXmlSerializer serializer = new KXmlSerializer();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		
		try {
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			serializer.setOutput(dos, XformConstants.DEFAULT_CHARACTER_ENCODING);
			doc.write(serializer);
			serializer.flush();
		}
		catch (Exception ex) {
			log.error("could not serialize or render node to xml.", ex);
			return null;
		}

		String xml;
		try {
			xml = new String(bos.toByteArray(), XformConstants.DEFAULT_CHARACTER_ENCODING);
		} catch (UnsupportedEncodingException ex) {
			log.error("could not encode xml.", ex);
			return null;
		}

//		// wrap this form data and render to a w3c Document
//		// TODO seems hacky ... but no way to do this otherwise afaict
//		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();		
//		DocumentBuilder documentBuilder;
//		try {
//			documentBuilder = documentBuilderFactory.newDocumentBuilder();
//		} catch (ParserConfigurationException ex) {
//			throw new APIException("could not create a document builder.", ex);
//		}
//		
//		org.w3c.dom.Document formDoc;
//		try {
//			 formDoc = documentBuilder.parse(IOUtils.toInputStream(xml));
//		} catch (SAXException ex) {
//			throw new APIException("could not parse kdom xml.", ex);
//		} catch (IOException ex) {
//			throw new APIException("IO error while parsing kdom xml.", ex);
//		}
//
//		// use the XformEditor to wrap the form data with household information
//		try {
//			XFormEditor.wrapFormWithMobileMetadata(formDoc, encounter);
//		} catch (ParserConfigurationException ex) {
//			throw new APIException("could not wrap form with mobile metadata.", ex);
//		}
//		
//		// print the formDoc to a string ...
//		return formDoc.toString();
		
		return xml;
	}
	
	/**
	 * stolen almost verbatim from XformBuilder.
	 * 
	 * @param node
	 * @param name 
	 */
	private static void removeChildNode(Element node, String name) {
		for (int index = 0; index < node.getChildCount(); index++) {
			if (node.getType(index) == Element.ELEMENT) {
				Element n = node.getElement(index);
				if (name.equals(n.getName())) {
					log.warn("removing " + name + " from node " + node.toString());
					node.removeChild(index);
					return;
				}
			}
		}
	}
	
	private static void addTextNode(Element parent, String name, String value) {
		Element e = parent.createElement(null, null);
		e.setName(name);
		if (StringUtils.hasText(value))
			e.addChild(Node.TEXT, value);
		parent.addChild(Element.ELEMENT, e);
	}

	public List<MobileFormEntryError> getErrorBatch(Integer start, Integer length, String query) {
		return dao.getErrorBatch(start, length, query);
	}

	public Number countErrors(String query) {
		return dao.countErrors(query);
	}

}