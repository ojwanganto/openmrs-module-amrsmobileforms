package org.openmrs.module.amrsmobileforms.db.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.openmrs.module.amrsmobileforms.Economic;
import org.openmrs.module.amrsmobileforms.EconomicObject;
import org.openmrs.module.amrsmobileforms.Household;
import org.openmrs.module.amrsmobileforms.HouseholdMember;
import org.openmrs.module.amrsmobileforms.MobileFormEntryError;
import org.openmrs.module.amrsmobileforms.Survey;
import org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO;

public class HibernateMobileFormEntryDAO implements MobileFormEntryDAO {
	
	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;
	
	/**
	 * Default public constructor
	 */
	public HibernateMobileFormEntryDAO() { }
	
	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) { 
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#getHousehold(java.lang.String)
	 */
	public Household getHousehold(String householdIdentifier) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Household.class);
		Household household = (Household) criteria.add(Expression.like("householdIdentifier", householdIdentifier)).uniqueResult();
		
		return household;	
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#createHouseholdInDatabase(org.openmrs.module.amrsmobileforms.Household)
	 */
	public void createHouseholdInDatabase(Household household) {
		sessionFactory.getCurrentSession().saveOrUpdate(household);
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#getEconomicObjectByObjectName(java.lang.String)
	 */
	public EconomicObject getEconomicObjectByObjectName(String objectName) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EconomicObject.class);
		EconomicObject econObject = (EconomicObject)criteria.add(Expression.like("objectName", objectName)).uniqueResult();
		
		return econObject;
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#createEconomicInDatabase(org.openmrs.module.amrsmobileforms.Economic)
	 */
	public void createEconomicInDatabase(Economic economic) {
		sessionFactory.getCurrentSession().saveOrUpdate(economic);
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#createSurvey(org.openmrs.module.amrsmobileforms.Survey)
	 */
	public void createSurvey(Survey survey) {
		sessionFactory.getCurrentSession().saveOrUpdate(survey);
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#getAllEconomicObjects()
	 */
	@SuppressWarnings("unchecked")
	public List<EconomicObject> getAllEconomicObjects() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EconomicObject.class);
		return (List<EconomicObject>)criteria.list();
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#saveEconomicObject(org.openmrs.module.amrsmobileforms.EconomicObject)
	 */
	public void saveEconomicObject(EconomicObject economicObject) {
		sessionFactory.getCurrentSession().saveOrUpdate(economicObject);
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#deleteEconomicObject(java.lang.Integer)
	 */
	public boolean deleteEconomicObject(EconomicObject economicObject) {
			sessionFactory.getCurrentSession().delete(economicObject);
		return true;
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#saveErrorInDatabase(org.openmrs.module.amrsmobileforms.MobileFormEntryError)
	 */
	public void saveErrorInDatabase(MobileFormEntryError mobileFormEntryError) {
		sessionFactory.getCurrentSession().saveOrUpdate(mobileFormEntryError);
	}

	/**
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#getAllMobileFormEntryErrors()
	 */
	@SuppressWarnings("unchecked")
	public List<MobileFormEntryError> getAllMobileFormEntryErrors() {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MobileFormEntryError.class);
		return (List<MobileFormEntryError>) criteria.list();
	}

	/***
	 * @see org.openmrs.module.amrsmobileforms.db.MobileFormEntryDAO#getErrorById(java.lang.Integer)
	 */
	public MobileFormEntryError getErrorById(Integer errorId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MobileFormEntryError.class);
		criteria.add(Expression.like("mobileFormEntryErrorId", errorId));
		return (MobileFormEntryError) criteria.uniqueResult();
	}

	public HouseholdMember getHouseholdMemberById(Integer identifier) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(HouseholdMember.class);
		criteria.add(Expression.eq("householdMemberId", identifier));
		return (HouseholdMember) criteria.uniqueResult();
	}

	public EconomicObject getEconomicObjectById(Integer economicObjectId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EconomicObject.class);
		criteria.add(Expression.like("objectId", economicObjectId));
		return (EconomicObject) criteria.uniqueResult();
	}

	public void deleteError(MobileFormEntryError error) {
		sessionFactory.getCurrentSession().delete(error);
	}

	public void saveHouseholdMember(HouseholdMember householdMember) {
		sessionFactory.getCurrentSession().saveOrUpdate(householdMember);
	}
}