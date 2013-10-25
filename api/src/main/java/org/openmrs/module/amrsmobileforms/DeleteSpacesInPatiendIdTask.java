package org.openmrs.module.amrsmobileforms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsmobileforms.groovy.CleanPatientIdentifiers;
import org.openmrs.module.amrsmobileforms.util.MobileFormEntryUtil;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * A task to clean up patient identifier prior to processing.
 * 
 *
 */
public class DeleteSpacesInPatiendIdTask extends AbstractTask {

	private static Log log = LogFactory.getLog(DeleteSpacesInPatiendIdTask.class);
    String filePath = MobileFormEntryUtil.getMobileFormsDropDir().getAbsolutePath();
    private CleanPatientIdentifiers cleanPatientIdentifiers;

    public CleanPatientIdentifiers getCleanPatientIdentifiers() {
        return cleanPatientIdentifiers;
    }

    public void setCleanPatientIdentifiers(CleanPatientIdentifiers cleanPatientIdentifiers) {
        this.cleanPatientIdentifiers = cleanPatientIdentifiers;
    }

    /**
	 * Call Groovy class that cleans patient identifiers
	 */
	public void execute() {
		Context.openSession();
		log.debug("Running task that cleans Patient Identifier prior to form processing ");
		try {
			if (Context.isAuthenticated() == false)
				authenticate();
            //It would be good if we pass the full URL to path of the directory with forms to be processed
            cleanPatientIdentifiers.getDirFileListing(filePath);
		} catch (APIException e) {
			log.error("Error running mobile forms relationships task", e);
			throw e;
		} finally {
			Context.closeSession();
		}
	}

	/*
	 * Resources clean up
	 */
	public void shutdown() {
		super.shutdown();
		log.debug("Shutting down task that cleans Patient Identifier");
	}


}