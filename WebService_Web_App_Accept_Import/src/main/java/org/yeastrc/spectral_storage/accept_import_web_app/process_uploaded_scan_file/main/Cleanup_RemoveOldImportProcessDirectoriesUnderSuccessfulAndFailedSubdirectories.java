package org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.delete_directory_and_contents.DeleteDirectoryAndContents;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;

/**
 * Cleanup Old "Import Processing" Directories.  Remove after 3 days.
 *
 */
public class Cleanup_RemoveOldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories {

	private static final Logger log = LoggerFactory.getLogger(Cleanup_RemoveOldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories.class);
	
	private static final int MAX_KEEP_SUCCESS_FAILED_DIRECTORIES_IN_DAYS = 3;
	
	private static final long MAX_KEEP_SUCCESS_FAILED_DIRECTORIES_IN_MILLISECONDS = ( (long) MAX_KEEP_SUCCESS_FAILED_DIRECTORIES_IN_DAYS) * 1000 * 60 * 60 * 24;

	private volatile boolean keepRunning = true;
	

	private Cleanup_RemoveOldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories() { }
	public static Cleanup_RemoveOldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories getInstance() { 
		return new Cleanup_RemoveOldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories(); 
	}
	

	/**
	 * awaken thread to process request, calls "notify()"
	 */
	public void awaken() {

		if ( log.isDebugEnabled() ) {
			log.debug("awaken() called:  " );
		}

		synchronized (this) {
			notify();
		}
	}

	/**
	 * shutdown was received from the operating system.  This is called on a different thread.
	 */
	public void shutdown() {
		log.info("shutdown() called");
		synchronized (this) {
			this.keepRunning = false;
		}
		
		this.awaken();
	}

	/**
	 * 
	 */
	public void cleanup_RemoveOldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories() {
		
//		log.warn("INFO:: cleanup_RemoveOldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories(): ENTER");

		try {
			{  //  Clean Successful

				delete_Successful_OR_Failed( 
						
						ScanFileToProcessConstants.SCAN_FILES_PROCESSED_SUCCESS_BASE_DIR
						);						
			
			}
			{  //  Clean Failed

				delete_Successful_OR_Failed( 
						
						ScanFileToProcessConstants.SCAN_FILES_PROCESSED_FAILED_BASE_DIR
						);						
			
			}
			
		} catch ( Throwable e ) {
			
			//	throw e;

		} finally {
			
		}
	}
	


	/**
	 * @param scanFilesProcessedBaseDirString
	 * @param max_ImportExecutionDirectoriesToKeep
	 * @param max_DaysToKeep_ImportExecutionDirectories
	 * @throws Exception
	 */
	private void delete_Successful_OR_Failed( 
			
			String scanFilesProcessedBaseDirString

			) throws Exception {

		File tempScanUploadBaseDirectoryFile =
				ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getTempScanUploadBaseDirectory();

		File scanFilesProcessedBaseDir = new File( tempScanUploadBaseDirectoryFile, scanFilesProcessedBaseDirString );
		if ( ! scanFilesProcessedBaseDir.exists() ) {
			//  Not exist so skip
			return;  // EARLY RETURN
		}
		
		//  Get oldest directories to delete 
		
		File[] scanFilesProcessedBaseDirContents = scanFilesProcessedBaseDir.listFiles();
		
		//  sort oldest to newest
		Arrays.sort( scanFilesProcessedBaseDirContents, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				if (  o1.lastModified() < o2.lastModified() ) {
					return -1;
				}
				if (  o1.lastModified() > o2.lastModified() ) {
					return 1;
				}
				return 0;
			}
		});
		
		long deleteDirectoryBefore_Milliseconds = System.currentTimeMillis() - MAX_KEEP_SUCCESS_FAILED_DIRECTORIES_IN_MILLISECONDS;
		
		for ( int index = 0; index < scanFilesProcessedBaseDirContents.length; index++ ) {
			
			if ( ! keepRunning ) {
				//  shutdown() called
				return; // EARLY RETURN
			}
		
			File dirEntry = scanFilesProcessedBaseDirContents[ index ];

			if ( ! dirEntry.isDirectory() ) {
				//  Must be a directory
				continue;  //  EARLY CONTINUE
			}
			
			if ( dirEntry.lastModified() < deleteDirectoryBefore_Milliseconds ) {
				
				//  Can delete directory
				
				DeleteDirectoryAndContents.getInstance().deleteDirectoryAndContents(dirEntry);
				
			}
		}
		
	}
		
}
