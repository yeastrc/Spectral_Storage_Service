package org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;

/**
 * Cleanup Old "Import Processing" Directories - remove the scan file to reduce disk usage until the whole directory can be deleted
 *
 */
public class Cleanup_Remove_ScanFileIn_OldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories {

	private static final Logger log = LoggerFactory.getLogger(Cleanup_Remove_ScanFileIn_OldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories.class);

	private volatile boolean keepRunning = true;
	

	private Cleanup_Remove_ScanFileIn_OldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories() { }
	public static Cleanup_Remove_ScanFileIn_OldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories getInstance() { 
		return new Cleanup_Remove_ScanFileIn_OldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories(); 
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
	public void cleanup_Remove_ScanFileIn_OldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories() {
		
//		log.warn("INFO:: cleanup_RemoveOldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories(): ENTER");

		try {
			{  //  Clean Successful

				delete_Successful_OR_Failed( 
						
						ScanFileToProcessConstants.SCAN_FILES_PROCESSED_SUCCESS_BASE_DIR,
						ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getMax_ImportScanFilesToKeep_SuccessfulImport(),
						ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getMax_DaysToKeep_ImportScanFiles_SuccessfulImport()
						);						
			
			}
			{  //  Clean Failed

				delete_Successful_OR_Failed( 
						
						ScanFileToProcessConstants.SCAN_FILES_PROCESSED_FAILED_BASE_DIR,
						ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getMax_ImportScanFilesToKeep_FailedImport(),
						ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getMax_DaysToKeep_ImportScanFiles_FailedImport()
						);						
			
			}
			
		} catch ( Throwable e ) {
			
			//	throw e;

		} finally {
			
		}
	}
	


	/**
	 * @param scanFilesProcessedBaseDirString
	 * @param max_ImportScanFilesToKeep
	 * @param max_DaysToKeep_ImportScanFiles
	 * @throws Exception
	 */
	private void delete_Successful_OR_Failed( 
			
			String scanFilesProcessedBaseDirString,

			int max_ImportScanFilesToKeep,
			int max_DaysToKeep_ImportScanFiles
			
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
		
		long deleteDirectoryBefore_Milliseconds = System.currentTimeMillis() - ( ( (long) max_DaysToKeep_ImportScanFiles ) * 1000 * 60 * 60 * 24 );
		
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
			
			if ( dirEntry.lastModified() < deleteDirectoryBefore_Milliseconds  
					|| index < scanFilesProcessedBaseDirContents.length - max_ImportScanFilesToKeep ) {
				
				//  Can delete scan file in directory
				
				for ( File fileInSubdir : dirEntry.listFiles() ) {
				
					if ( fileInSubdir.getName().startsWith( ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX ) ) {
						
						if ( ! fileInSubdir.delete() ) {
							log.error( "Unable delete scan file under success or fail dir. scan file: " + fileInSubdir.getAbsolutePath() );
						}
					}
				}
			}
		}
		
	}
		
}
