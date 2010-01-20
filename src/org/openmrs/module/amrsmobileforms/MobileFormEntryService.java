package org.openmrs.module.amrsmobileforms;

import java.io.File;
import java.util.List;
import java.util.SortedMap;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MobileFormEntryService {
	
	public List<File> getMobileResources();
	
	public MobileFormQueue getMobileFormEntryQueue(String absoluteFilePath);

	public SortedMap<String, String> getSystemVariables();
	
	//HOUSEHOLD RELATED METHODS 
	public Household getHousehold(String householdIdentifier);
	
	public void createHouseholdInDatabase(Household household);
	
	public HouseholdMember getHouseholdMemberById(Integer identifier);
	
	//ECONOMIC RELATED METHODS
	public void createEconomicInDatabase(Economic economic);
	
	public List<EconomicObject> getAllEconomicObjects();
	
	public EconomicObject getEconomicObjectByObjectName(String objectName);
	
	public void saveEconomicObject(EconomicObject economicObject);
	
	public boolean deleteEconomicObject(EconomicObject economicObject);
	
	public EconomicObject getEconomicObjectById(Integer economicObjectId);
	
	//SURVEY RELATED METHODS
	public void createSurvey(Survey survey);
	
	//ERRORS RELATED METHODS
	public List<MobileFormEntryError> getAllMobileFormEntryErrors();
	
	public MobileFormEntryError getErrorById(Integer errorId);
	
	public void saveErrorInDatabase(MobileFormEntryError mobileFormEntryError);
	
	public void deleteError(MobileFormEntryError error);

	/** Creates a new HouseholdMember object in the database. If <b> {@link HouseholdMember} </b> exists
	 * it updates the existing object.
	 * @param householdMember
	 */
	public void saveHouseholdMember(HouseholdMember householdMember);
}