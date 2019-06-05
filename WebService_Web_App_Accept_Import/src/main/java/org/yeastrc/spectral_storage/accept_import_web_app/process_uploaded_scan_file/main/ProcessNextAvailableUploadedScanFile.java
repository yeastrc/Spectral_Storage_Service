package org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main.ProcessNextUploadedScanFile.ProcessingSuccessFailKilled;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.a_upload_processing_status_file.UploadProcessingReadStatusFile;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessingStatusFileConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_api_key_processing.ScanFileAPIKey_ToFileReadWrite;

/**
 * Pro
 *
 */
public class ProcessNextAvailableUploadedScanFile {

	private static final Logger log = LoggerFactory.getLogger(ProcessNextAvailableUploadedScanFile.class);

	
//	private static enum ProcessUploadedFilesState {
//		
//		IDLE, PROCESSING
//	}
	
	
	private volatile ProcessNextUploadedScanFile processNextUploadedScanFile = null;
	
//	private volatile ProcessUploadedFilesState processUploadedFilesState = ProcessUploadedFilesState.IDLE;

//	private volatile boolean keepRunning = true;
	

	private ProcessNextAvailableUploadedScanFile() { }
	public static ProcessNextAvailableUploadedScanFile getInstance() { 
		return new ProcessNextAvailableUploadedScanFile(); 
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
//		synchronized (this) {
//			this.keepRunning = false;
//		}
		
		if ( processNextUploadedScanFile != null ) {
			processNextUploadedScanFile.shutdown();
		}
		//  awaken this thread if it is in 'wait' state ( not currently processing a job )
		this.awaken();
	}
	

	
	/**
	 * Process the next uploaded scan file that has not been fully processed
	 * 
	 * @return true if process a scan file (success or fail or killed)
	 */
	public boolean processNextAvailableUploadedScanFile() {

		String scanFileDirString = null;
		
		try {
			//  Process next Scan file

			File scanFileDir = null;
			try {
				scanFileDir = GetNextScanFileDirToProcessForStatus.getInstance().getNextScanFileDirToProcessForStatus( UploadProcessingStatusFileConstants.STATUS_PENDING );

			} catch ( Exception e ) {
				log.error( "Exception getting next Scan file to process", e );
				throw e;
			}

			if ( scanFileDir == null ) {

				return false;   //  EARLY LOOP EXIT

			} else {
				
				if ( log.isInfoEnabled() ) {
					log.info( "processNextAvailableUploadedScanFile(): START Processing Scan File in Directory [calling processNextUploadedScanFile(...): " + scanFileDir.getAbsolutePath() );
				}
				
				scanFileDirString = scanFileDir.getAbsolutePath();
				
				ProcessingSuccessFailKilled processingSuccessFailKilled_Result = null;
				
				try {

					processNextUploadedScanFile = ProcessNextUploadedScanFile.getInstance();

					//				processUploadedFilesState = ProcessUploadedFilesState.PROCESSING;

					processingSuccessFailKilled_Result = processNextUploadedScanFile.processNextUploadedScanFile( scanFileDir );

				} catch ( Throwable e ) {
					
					MoveProcessingDirectoryToOneof_Processed_Directories.getInstance().moveProcessingDirectoryToOneof_Processed_Directories( scanFileDir, ProcessingSuccessFailKilled.FAIL );
					
					throw e;
				}

				String scanFileHashKey_API_Key = null;
				
				try {
					scanFileHashKey_API_Key =
							ScanFileAPIKey_ToFileReadWrite.getInstance()
							.readScanFileHashFromInProcessFile( scanFileDir );
				} catch ( Exception e ) {
					String msg = "Failed Call to ScanFileAPIKey_ToFileReadWrite.getInstance(). readScanFileHashFromInProcessFile( scanFileDir ); scanFileDir: " + scanFileDir.getAbsolutePath();
					log.error( msg );
				}
				
				if ( processingSuccessFailKilled_Result != ProcessingSuccessFailKilled.KILLED ) {
					
					//  Only move if not Killed
				
					//  Move Processing directory to 'after processing' directory under base directory  

					MoveProcessingDirectoryToOneof_Processed_Directories.getInstance().moveProcessingDirectoryToOneof_Processed_Directories( scanFileDir, processingSuccessFailKilled_Result );
				}
				
				if ( log.isInfoEnabled() ) {
					log.info( "processNextAvailableUploadedScanFile(): END Processing Scan File in Directory [after calling processNextUploadedScanFile(...): " + scanFileDir.getAbsolutePath() );
				}
				
				if ( processingSuccessFailKilled_Result == ProcessingSuccessFailKilled.SUCCESS && StringUtils.isNotEmpty( scanFileHashKey_API_Key ) ) {
					
					if ( log.isInfoEnabled() ) {
						log.info( "processNextAvailableUploadedScanFile(): Processing Scan File in Directory Successful so update other Pending processing dirs with same scan file key to Success: " + scanFileDir.getAbsolutePath() );
					}
					
					if ( StringUtils.isNotEmpty( scanFileHashKey_API_Key ) ) {
					
						updateOtherProcessingDirsPendingToSuccessForSameAPIKey( scanFileHashKey_API_Key );
					}
				}
				
			}

		} catch ( Throwable e ) {
			
			String scanFileDirInfo = "";
			if ( scanFileDirString != null ) {
				scanFileDirInfo = ", scanFileDir: " + scanFileDirString;
			}

			log.error( "Exception Processing Scan file" + scanFileDirInfo, e );

			synchronized (this) {
				try {
					wait( 10000 );  //  sleep 10 seconds so don't quickly and repeatedly generate system errors.
				} catch (InterruptedException e2) {
					log.info("wait() interrupted with InterruptedException");
				}
			}

			//	throw e;

		} finally {
			processNextUploadedScanFile = null;

//			processUploadedFilesState = ProcessUploadedFilesState.IDLE;
		}

		return true;
	}
	


	/**
	 * @param scanFileHashKey_API_Key
	 * @throws Exception
	 */
	private void updateOtherProcessingDirsPendingToSuccessForSameAPIKey( String scanFileHashKey_API_Key ) throws Exception {
		
		//  Processing is successful
		
		//  Update other processing subdirectories with same hash key / API key to Success since the API Key is now in Spectr
		
		File tempScanUploadBaseDirectoryFile =
				ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getTempScanUploadBaseDirectory();
		
		//  Get the File object for the Base Subdir used to store the scan file for processing 
		String scanFilesToProcessBaseDirString = ScanFileToProcessConstants.SCAN_FILES_TO_PROCESS_BASE_DIR;
		
		File scanFilesToProcessBaseDir = new File( tempScanUploadBaseDirectoryFile, scanFilesToProcessBaseDirString );
		if ( ! scanFilesToProcessBaseDir.exists() ) {
			//  Scan Files Uploaded Base Dir does not exist.  Nothing uploaded yet
			return;  //  EARLY EXIT
		}
		
		//  Get oldest directory with a scan file to process 
		
		File[] scanFilesToProcessBaseDirContents = scanFilesToProcessBaseDir.listFiles();
		
		//  sort oldest to newest
		Arrays.sort( scanFilesToProcessBaseDirContents, new Comparator<File>() {
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
		
		for ( File scanFileDir : scanFilesToProcessBaseDirContents ) {
			//  Process each Subdir Scan File Processing dir
			processProcessDir( scanFileHashKey_API_Key, scanFileDir );
		}
		
	}
	
	
	/**
	 * @param scanFilesToProcessBaseDirContent
	 * @param statusValueToFind
	 * @return
	 * @throws Exception 
	 */
	private void processProcessDir( String scanFileHashKey_API_Key_InSuccessProcessedDir, File scanFileDir ) throws Exception {
		
		if ( ! scanFileDir.isDirectory() ) {
			//  Must be a directory
			return;  //  EARLY EXIT
		}
		
		String status =
				UploadProcessingReadStatusFile.getInstance().uploadProcessingReadStatusFile( scanFileDir );
		
		if ( status == null ) {
			//  No status file
			return;  //  EARLY EXIT
		}
		
		if ( ! UploadProcessingStatusFileConstants.STATUS_PENDING.equals( status ) ) {
			//  status is NOT pending so return
			return;  //  EARLY EXIT
		}
		

		String scanFileHashKey_API_Key_InCurrent_scanFileDir =
				ScanFileAPIKey_ToFileReadWrite.getInstance()
				.readScanFileHashFromInProcessFile( scanFileDir );
		
		if ( ! scanFileHashKey_API_Key_InSuccessProcessedDir.equals( scanFileHashKey_API_Key_InCurrent_scanFileDir ) ) {
			//  Not same API Key or no API key in current dir
			return;  //  EARLY EXIT
		}

		//  Update other processing subdirectories with same hash key / API key to Success since the API Key is now in Spectr

		ProcessUploadedScanFile_Final_OnSuccess.getInstance()
		.processUploadedScanFile_Final_OnSuccess( scanFileHashKey_API_Key_InCurrent_scanFileDir, scanFileDir );
		

		//  Move Processing directory to 'after processing' directory under base directory  
	
		MoveProcessingDirectoryToOneof_Processed_Directories.getInstance()
		.moveProcessingDirectoryToOneof_Processed_Directories( scanFileDir, ProcessingSuccessFailKilled.SUCCESS );
		
	}
	
	
}
