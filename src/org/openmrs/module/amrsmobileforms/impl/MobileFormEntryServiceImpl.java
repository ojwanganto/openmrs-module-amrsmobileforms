package org.openmrs.module.amrsmobileforms.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.amrsmobileforms.Economic;
import org.openmrs.module.amrsmobileforms.EconomicObject;
import org.openmrs.module.amrsmobileforms.Household;
import org.openmrs.module.amrsmobileforms.HouseholdMember;
import org.openmrs.module.amrsmobileforms.MobileFormEntryError;
import org.openmrs.module.amrsmobileforms.MobileFormEntryService;
import org.openmrs.module.amrsmobileforms.MobileFormQueue;
import org.openmrs.module.amrsmobileforms.Survey;
import org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO;
import org.openmrs.module.amrsmobileforms.util.MobileFormEntryUtil;

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
	

	public MobileFormQueue getMobileFormEntryQueue(String absoluteFilePath) {
		MobileFormQueue queueItem = new MobileFormQueue();
		queueItem.setFileSystemUrl(absoluteFilePath);
		log.debug(absoluteFilePath);
		return queueItem;
	}

	public SortedMap<String,String> getSystemVariables() {
		TreeMap<String, String> systemVariables = new TreeMap<String, String>();
		systemVariables.put("MOBILE_FORMS_RESOURCES_DIR", MobileFormEntryUtil.getMobileFormsResourcesDir().getAbsolutePath());
		systemVariables.put("MOBILE_FORMS_DROP_DIR", MobileFormEntryUtil.getMobileFormsDropDir().getAbsolutePath());
		systemVariables.put("MOBILE_FORMS_SPLIT_QUEUE_DIR", MobileFormEntryUtil.getMobileFormsSplitQueueDir().getAbsolutePath());
		systemVariables.put("MOBILE_FORMS_QUEUE_DIR", MobileFormEntryUtil.getMobileFormsQueueDir().getAbsolutePath());
		systemVariables.put("MOBILE_FORMS_ARCHIVE_DIR", MobileFormEntryUtil.getMobileFormsArchiveDir(null).getAbsolutePath());
		systemVariables.put("MOBILE_FORMS_ERROR_DIR", MobileFormEntryUtil.getMobileFormsErrorDir().getAbsolutePath());
		return systemVariables;
	}
	
	public List<File> getMobileResources() {
		File resourcesDir=MobileFormEntryUtil.getMobileFormsResourcesDir();
		List<File> lst=new ArrayList<File>();
		for(File file:resourcesDir.listFiles()) {
			lst.add(file);
		}
		return lst;
	}
	
	public Household getHousehold(String householdIdentifier) {
		return dao.getHousehold(householdIdentifier);
	}
	
	public void createHouseholdInDatabase(Household household) {
		dao.createHouseholdInDatabase(household);
	}

	public EconomicObject getEconomicObjectByObjectName(String objectName) {
		return dao.getEconomicObjectByObjectName(objectName);
	}

	public void createEconomicInDatabase(Economic economic) {
		dao.createEconomicInDatabase(economic);
	}

	public void createSurvey(Survey survey) {
		dao.createSurvey(survey);
	}

	public List<EconomicObject> getAllEconomicObjects() {
		return dao.getAllEconomicObjects();
	}

	public void saveEconomicObject(EconomicObject economicObject) {
		dao.saveEconomicObject(economicObject);
	}

	public boolean deleteEconomicObject(EconomicObject economicObject) {
		return dao.deleteEconomicObject(economicObject);
	}

	public void saveErrorInDatabase(MobileFormEntryError mobileFormEntryError) {
		dao.saveErrorInDatabase(mobileFormEntryError);
	}

	public List<MobileFormEntryError> getAllMobileFormEntryErrors() {
		return dao.getAllMobileFormEntryErrors();
	}

	public MobileFormEntryError getErrorById(Integer errorId) {
		return dao.getErrorById(errorId);
	}

	public HouseholdMember getHouseholdMemberById(Integer identifier) {
		return dao.getHouseholdMemberById(identifier);
	}

	public EconomicObject getEconomicObjectById(Integer economicObjectId) {
		return dao.getEconomicObjectById(economicObjectId);
	}

	public void deleteError(MobileFormEntryError error) {
		dao.deleteError(error);
	}

	public void saveHouseholdMember(HouseholdMember householdMember) {
		dao.saveHouseholdMember(householdMember);
	}

	public List<Survey> getSyncLog() {
		return dao.getSyncLog();
	}
}
