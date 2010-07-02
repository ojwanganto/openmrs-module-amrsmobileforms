package org.openmrs.module.amrsmobileforms.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Process for logging all synchronizations from mobile devices
 * 
 * @author Samuel Mbugua
 *
 */
public class GPSOutput {
	private static final Log log = LogFactory.getLog(GPSOutput.class);

	private File getLogFile() {
		File logDir=MobileFormEntryUtil.getMobileFormsSyncLogDir();
		String logFileName=logDir.getAbsolutePath() + File.separator + "GpsComparison.csv";
		File logFile = new File(logFileName);
		if (!logFile.exists())
			try {
				log.debug("Creating new gps log file");
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return logFile;
	}
	
	private void writer(String filePath, String line) {
		try
		{
		    FileWriter fileWriter = new FileWriter(filePath,true);
		    fileWriter.write(line);
		    fileWriter.close();
		}
		catch(IOException e){e.printStackTrace();} 
	}
	
	public void createComparison(String line){
		try {
			File syncLogFile = getLogFile();
			writer(syncLogFile.getAbsolutePath(),line);
		}
		catch (Throwable t) {
			log.error("Kimeumna");
		}
	}
}