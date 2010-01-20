package org.openmrs.module.amrsmobileforms;

/**
 * Module wide constants are kept here
 * 
 * @author Samuel Mbugua
 * 
 */
public class MobileFormEntryConstants {
	
	/** 
	  * Name of the global property for the directory where mobile xforms and other special files are stored.
	 */
	public final static String GP_MOBILE_FORMS_RESOURCES_DIR = "mobileformentry.resources_dir";

	/** The default mobile-forms-resources  directory. */
	public final static String GP_MOBILE_FORMS_RESOURCES_DIR_DEFAULT = "mobileformentry/resources_dir";
	
	/** 
	  * Name of the global property for the directory where mobile devices sends forms.
	 */
	public final static String GP_MOBILE_FORMS_DROP_DIR = "mobileformentry.drop_dir";

	/** The default mobile-forms-drop  directory. */
	public final static String GP_MOBILE_FORMS_DROP_DIR_DEFAULT = "mobileformentry/drop_dir";
	
	/**
	 * Name of the global property for the directory where mobile forms
	 * are dropped.
	 */
	public final static String GP_MOBILE_FORMS_SPLIT_QUEUE_DIR = "mobileformentry.split_queue_dir";

	/** The default mobile-forms-split queue directory. */
	public final static String GP_MOBILE_FORMS_SPLIT_QUEUE_DIR_DEFAULT = "mobileformentry/split_queue";
	
	/**
	 * Name of the global property for the directory for queuing mobile forms
	 * before they are processed.
	 */
	public final static String GP_MOBILE_FORMS_QUEUE_DIR = "mobileformentry.queue_dir";

	/** The default mobile-forms queue directory. */
	public final static String GP_MOBILE_FORMS_QUEUE_DIR_DEFAULT = "mobileformentry/queue";

	/**
	 * Name of the global property for the directory where to put forms that
	 * erred during processing
	 */
	public final static String GP_MOBILE_FORMS_ERROR_DIR = "mobileformentry.error_dir";

	/** The default mobile-forms error directory. */
	public final static String GP_MOBILE_FORMS_ERROR_DIR_DEFAULT = "mobileformentry/error";

	/*
	 * I do not believe this is the best way since the xform module will archive
	 * the files as well as the formentry module, this could be a table entry
	 * with just form names that can later be retrieved from the xform archive
	 * directory
	 */
	public final static String GP_MOBILE_FORMS_ARCHIVE_DIR = "mobileformentry.archive_dir";

	/** The default mobile forms archive directory. */
	public final static String GP_MOBILE_FORMS_ARCHIVE_DIR_DEFAULT = "mobileformentry/archive/%Y/%M";
	
	/** The metadata nodes prefix. */
	public static final String METADATA_PREFIX = "/form/meta";

	/** The survey nodes prefix. */
	public final static String SURVEY_PREFIX = "/form/survey";

	/** The household nodes prefix. */
	public final static String HOUSEHOLD_PREFIX = "/form/household";

	/** The economics nodes prefix. */
	public final static String HOUSEHOLD_ECONOMIC_PREFIX = "/economics";
	
	/** The household metadata nodes prefix. */
	public final static String HOUSEHOLD_META_PREFIX = "/meta_data";

	/** The individuals nodes prefix. */
	public final static String HOUSEHOLD_INDIVIDUALS_PREFIX = "/individuals";
	
	/** The text/xml http content type. */
    public final static String HTTP_HEADER_CONTENT_TYPE_XML = "text/xml; charset=utf-8";


	public final static String META_START_TIME = "start_time";
	public final static String META_END_TIME = "end_time";
	public final static String META_DEVICE_ID = "device_id";
	public final static String META_SUBSCRIBER_ID= "subscriber_id";
	
	
	public final static String SURVEY_PROVIDER_ID = "provider_id";
	public final static String SURVEY_TEAM_ID = "team_id";
	public final static String SURVEY_SURVEY_ID= "survey_id";
	public final static String SURVEY_ALLOWED_IN = "allowed_in";
	public final static String SURVEY_RETURN_DATE = "return_date";
	
	public final static String HOUSEHOLD_META_HOUSEHOLD_ID = "household_id";
	public final static String HOUSEHOLD_META_GPS_LOCATION = "gps_location";
	public final static String HOUSEHOLD_META_VILLAGE = "village";
	public final static String HOUSEHOLD_META_LOCATION = "location";
	public final static String HOUSEHOLD_META_SUBLOCATION = "sublocation";
	public final static String HOUSEHOLD_META_DISTRICT = "district";
	public final static String HOUSEHOLD_META_DIVISION = "division";
	public final static String HOUSEHOLD_META_HOUSEHOLD_ADULTS = "household_adults";
	public final static String HOUSEHOLD_META_CHILDREN_UNDER13 = "children_under13";
	
	
	
	public final static String ECONOMIC_BEDNETS_OWNED = "bednets_owned";
	public final static String ECONOMIC_BEDNETS_GIVEN = "bednets_given";
	public final static String ECONOMIC_BEDNET_VOUCHER = "bednet_voucher";
	public final static String ECONOMIC_LAND_OWNED = "land_owned";
	public final static String ECONOMIC_COWS_OWNED = "cows_owned";
	public final static String ECONOMIC_GOATS_OWNED = "goats_owned";
	public final static String ECONOMIC_SHEEP_OWNED = "sheep_owned";
	public final static String ECONOMIC_CHILDREN_IN_HOUSEHOLD = "children_in_household";
	public final static String ECONOMIC_CHILDREN_IN_SCHOOL = "children_in_school";
	
	public final static String PATIENT_IDENTIFIER = "patient.medical_record_number";
	public final static String PATIENT_IDENTIFIER_TYPE = "patient_identifier.identifier_type_id";
	public final static String PATIENT_HCT_IDENTIFIER = "patient.hct_id";
	public final static String HOUSEHOLD_IDENTIFIER = "household";
	public final static String ENCOUNTER_DATETIME = "/form/encounter/encounter.encounter_datetime";
	public final static String PATIENT_NODE = "/form/patient";
	
	//PRIVILEGES
	public static final String PRIV_RESOLVE_MOBILE_FORM_ENTRY_ERROR = "Resolve Mobile Form Entry Error";
	public static final String PRIV_COMMENT_ON_MOBILE_FORM_ENTRY_ERRORS = "Comment on Mobile Form Entry Errors";
	public static final String PRIV_VIEW_MOBILE_FORM_ERROR = "View Mobile Form Error";
	public static final String PRIV_VIEW_MOBILE_FORM_PROPERTY = "View Mobile Form Entry Properties";
	public static final String PRIV_MANAGE_ECONOMIC_OBJECT = "Manage Economic Objects";
}
