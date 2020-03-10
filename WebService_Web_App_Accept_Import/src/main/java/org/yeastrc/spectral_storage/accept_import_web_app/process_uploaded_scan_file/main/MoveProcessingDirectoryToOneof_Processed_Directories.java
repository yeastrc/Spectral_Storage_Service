package org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main;

import java.io.File;
import java.util.Date;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileWebappInternalException;
import org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main.ProcessNextUploadedScanFile.ProcessingSuccessFailKilled;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;

/**
 * 
 *
 */
public class MoveProcessingDirectoryToOneof_Processed_Directories {

	private static final Logger log = LoggerFactory.getLogger(MoveProcessingDirectoryToOneof_Processed_Directories.class);

	private MoveProcessingDirectoryToOneof_Processed_Directories() { }
	public static MoveProcessingDirectoryToOneof_Processed_Directories getInstance() { 
		return new MoveProcessingDirectoryToOneof_Processed_Directories(); 
	}
		
	/**
	 * @param from_scanFileDir
	 * @param processingSuccessFailKilled_Result
	 * @throws Exception
	 */
	public void moveProcessingDirectoryToOneof_Processed_Directories( 
			File from_scanFileDir,
			ProcessingSuccessFailKilled processingSuccessFailKilled_Result ) throws Exception {

		File tempScanUploadBaseDirectoryFile =
				ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getTempScanUploadBaseDirectory();
		
		//  Get the scan files processed base dir to move to 
		
		String scanFilesProcessedBaseDirString = null;

		if ( processingSuccessFailKilled_Result == ProcessingSuccessFailKilled.SUCCESS ) {
			scanFilesProcessedBaseDirString = ScanFileToProcessConstants.SCAN_FILES_PROCESSED_SUCCESS_BASE_DIR;
			
		} else if ( processingSuccessFailKilled_Result == ProcessingSuccessFailKilled.FAIL ) {
			scanFilesProcessedBaseDirString = ScanFileToProcessConstants.SCAN_FILES_PROCESSED_FAILED_BASE_DIR;
			
		} else {
			String msg = "In moveProcessingDirectoryToOneof_Processed_Directories(...): Invalid value for 'processingSuccessFailKilled_Result' (Killed not allowed): " + processingSuccessFailKilled_Result;
			log.error(msg);
			throw new SpectralFileWebappInternalException(msg);
		}
		
		File scanFilesProcessedBaseDir = new File( tempScanUploadBaseDirectoryFile, scanFilesProcessedBaseDirString );
		if ( ! scanFilesProcessedBaseDir.exists() ) {
			//  Scan Files Processed (for result) Base Dir does not exist.  Create it
			if ( ! scanFilesProcessedBaseDir.mkdir() ) {
				String msg = "Failed to create subdir': " + scanFilesProcessedBaseDir.getAbsolutePath();
				log.error(msg);
				throw new SpectralFileWebappInternalException(msg);
			}
		}
		
		String from_scanFileDir_Name = from_scanFileDir.getName();
		
		File to_scanFileDir = new File( scanFilesProcessedBaseDir, from_scanFileDir_Name );
		
		
		if ( log.isInfoEnabled() ) {

//			Exception fakeException = new Exception("FAKE Exception for Stack Trace");

			log.info( "INFO: Moving scan file processing dir.  Now: " + new Date() + ", From Dir: " + from_scanFileDir.getAbsolutePath() 
				+ ", To Dir: " + to_scanFileDir
				+ ", Fake Exception: " /* + fakeException.toString(), fakeException */ );
		}
		
		if ( ! from_scanFileDir.renameTo( to_scanFileDir ) ) {
			String msg = "Failed to move processing from_scanFileDir': " 
					+ from_scanFileDir.getAbsolutePath()
					+ ", to processed subdir: "
					+ to_scanFileDir.getAbsolutePath();
			log.error(msg);
			throw new SpectralFileWebappInternalException(msg);
		}
	}

}
