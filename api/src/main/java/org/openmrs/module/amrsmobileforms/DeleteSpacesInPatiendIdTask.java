package org.openmrs.module.amrsmobileforms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsmobileforms.util.MobileFormEntryUtil;
import org.openmrs.scheduler.tasks.AbstractTask;
import groovy.lang.GroovyShell ;
import groovy.lang.GroovyClassLoader ;
import groovy.util.GroovyScriptEngine ;
import java.io.File ;
import java.io.IOException;

/**
 * A task to clean up patient identifier prior to processing.
 * 
 *
 */
public class DeleteSpacesInPatiendIdTask extends AbstractTask {

	private static Log log = LogFactory.getLog(DeleteSpacesInPatiendIdTask.class);
    String filePath = MobileFormEntryUtil.getMobileFormsDropDir().getAbsolutePath();


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
            new GroovyShell().parse( new File( "groovy/ParseXML.groovy" ) ).invokeMethod("getDirFileListing", filePath) ;
		} catch (APIException e) {
			log.error("Error running mobile forms relationships task", e);
			throw e;
		} catch (IOException e) {
            e.printStackTrace();
            log.error("File error occurred during processing");
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