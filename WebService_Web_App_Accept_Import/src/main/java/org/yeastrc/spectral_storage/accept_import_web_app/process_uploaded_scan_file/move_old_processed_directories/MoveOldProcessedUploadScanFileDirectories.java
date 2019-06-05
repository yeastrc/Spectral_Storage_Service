package org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.move_old_processed_directories;

import java.io.File;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileWebappInternalException;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.a_upload_processing_status_file.UploadProcessingReadStatusFile;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessingStatusFileConstants;

/**
 * A cleanup action to move existing 'Processed' 
 * Scan File Upload processing directories 
 * to under the 'Processed' directory names
 *
 */
public class MoveOldProcessedUploadScanFileDirectories {

	private static final Logger log = LoggerFactory.getLogger( MoveOldProcessedUploadScanFileDirectories.class );
	
	// Private constructor
	private MoveOldProcessedUploadScanFileDirectories() {}
	
	public static MoveOldProcessedUploadScanFileDirectories getInstance() {
		return new MoveOldProcessedUploadScanFileDirectories();
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public void moveOldProcessedUploadScanFileDirectories() throws Exception {
	
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
			ifDirProcessed_moveProcessingDirectoryToOneof_Processed_Directories( processing_scanFileDir, tempScanUploadBaseDirectoryFile );
		}
	}
	
	/**
	 * @param processing_scanFileDir
	 * @param tempScanUploadBaseDirectoryFile
	 * @throws Exception
	 */
	private void ifDirProcessed_moveProcessingDirectoryToOneof_Processed_Directories( 
			File processing_scanFileDir,
			File tempScanUploadBaseDirectoryFile ) throws Exception {

		if ( ! processing_scanFileDir.isDirectory() ) {
			//  Must be a directory
			return;  //  EARLY EXIT
		}

		String status =
				UploadProcessingReadStatusFile.getInstance().uploadProcessingReadStatusFile( processing_scanFileDir );
		
		if ( status == null ) {
			//  No status file
			
		}
		
		if ( UploadProcessingStatusFileConstants.STATUS_PENDING.equals( status ) ) {
			
			
		}
		//  Get the scan files processed base dir to move to 
		
		String scanFilesProcessedBaseDirString = null;

		if ( UploadProcessingStatusFileConstants.STATUS_PROCESSING_SUCCESSFUL.equals( status ) ) {
			scanFilesProcessedBaseDirString = ScanFileToProcessConstants.SCAN_FILES_PROCESSED_SUCCESS_BASE_DIR;
			
		} else if ( UploadProcessingStatusFileConstants.STATUS_PROCESSING_FAILED.equals( status ) ) {
			scanFilesProcessedBaseDirString = ScanFileToProcessConstants.SCAN_FILES_PROCESSED_FAILED_BASE_DIR;
			
		} else {
			//  Status is other so skip this directory
			
			return; //  EARLY RETURN
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
		
		String from_scanFileDir_Name = processing_scanFileDir.getName();
		
		File to_scanFileDir = new File( scanFilesProcessedBaseDir, from_scanFileDir_Name );
		
		if ( ! processing_scanFileDir.renameTo( to_scanFileDir ) ) {
			String msg = "Failed to move processing subdir': " 
					+ processing_scanFileDir.getAbsolutePath()
					+ ", to processed subdir: "
					+ to_scanFileDir.getAbsolutePath();
			log.error(msg);
			throw new SpectralFileWebappInternalException(msg);
		}
	}

}
