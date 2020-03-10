package org.yeastrc.spectral_storage.accept_import_web_app.reset_killed_import_to_pending_on_webapp_startup;

import java.io.File;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.import_processing_status_file__read_write.UploadProcessingReadStatusFile;
import org.yeastrc.spectral_storage.accept_import_web_app.import_processing_status_file__read_write.UploadProcessingWriteOrUpdateStatusFile;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessingStatusFileConstants;

/**
 * On Webapp Startup, Process the subdirs in the Import Processing Base directory.
 * For any subdir that has status of Killed, change to status Pending so it will get processed again.
 *
 */
public class ResetKilledImportToPendingOnWebappStartup {

	private static final Logger log = LoggerFactory.getLogger(ResetKilledImportToPendingOnWebappStartup.class);

	private ResetKilledImportToPendingOnWebappStartup() { }
	public static ResetKilledImportToPendingOnWebappStartup getInstance() { 
		return new ResetKilledImportToPendingOnWebappStartup(); 
	}
	
	/**
	 * @throws Exception
	 */
	public void resetKilledImportToPendingOnWebappStartup() throws Exception {

		File tempScanUploadBaseDirectoryFile =
			ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getTempScanUploadBaseDirectory();

		//  Get the File object for the Base Subdir used to store the scan file for processing 
		String scanFilesToProcessBaseDirString = ScanFileToProcessConstants.SCAN_FILES_TO_PROCESS_BASE_DIR;

		File scanFilesToProcessBaseDir = new File( tempScanUploadBaseDirectoryFile, scanFilesToProcessBaseDirString );
		if ( ! scanFilesToProcessBaseDir.exists() ) {
			//  Scan Files Uploaded Base Dir does not exist.  Nothing uploaded yet
			return;  //  EARLY EXIT
		}

		//  Process all directories 

		File[] scanFilesToProcessBaseDirContents = scanFilesToProcessBaseDir.listFiles();


		for ( File processing_scanFileDir : scanFilesToProcessBaseDirContents ) {
			ifDir_Status_Killed_ResetToPending( processing_scanFileDir );
		}
	}
	
	/**
	 * @param processing_scanFileDir
	 * @param tempScanUploadBaseDirectoryFile
	 * @throws Exception
	 */
	private void ifDir_Status_Killed_ResetToPending( 
			File processing_scanFileDir ) throws Exception {

		if ( ! processing_scanFileDir.isDirectory() ) {
			//  Must be a directory
			return;  //  EARLY EXIT
		}

		String status =
				UploadProcessingReadStatusFile.getInstance().uploadProcessingReadStatusFile( processing_scanFileDir );
		
		if ( status == null ) {
			//  No status file
			
		}
		
		if ( UploadProcessingStatusFileConstants.STATUS_PROCESSING_KILLED.equals( status ) ) {
			
			//  Status is Killed so change to Pending

			UploadProcessingWriteOrUpdateStatusFile.getInstance()
			.uploadProcessingWriteOrUpdateStatusFile( 
					UploadProcessingStatusFileConstants.STATUS_PENDING, 
					processing_scanFileDir,
					UploadProcessingStatusFileConstants.STATUS_PROCESSING_CALLER_LABEL__ACCEPT_IMPORT_WEBAPP );

		}
	}
	
}
