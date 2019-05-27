package org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main;

import java.io.File;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileWebappInternalException;
import org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main.ProcessNextUploadedScanFile.ProcessingSuccessFailKilled;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;

/**
 * Pro
 *
 */
public class ProcessNextAvailableUploadedScanFile {

	private static final Logger log = Logger.getLogger(ProcessNextAvailableUploadedScanFile.class);

	
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
		synchronized (this) {
//			this.keepRunning = false;
		}
		
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
				scanFileDir = GetNextScanFileDirToProcess.getInstance().getNextScanFileDirToProcess();

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
				
				try {

					processNextUploadedScanFile = ProcessNextUploadedScanFile.getInstance();

					//				processUploadedFilesState = ProcessUploadedFilesState.PROCESSING;

					ProcessingSuccessFailKilled processingSuccessFailKilled_Result = processNextUploadedScanFile.processNextUploadedScanFile( scanFileDir );

					//  Move Processing directory to 'after processing' directory under base directory  
				
					moveProcessingDirectoryToOneof_Processed_Directories( scanFileDir, processingSuccessFailKilled_Result );
					
				} catch ( Throwable e ) {
					
					moveProcessingDirectoryToOneof_Processed_Directories( scanFileDir, ProcessingSuccessFailKilled.FAIL );
					
					throw e;
				}

				if ( log.isInfoEnabled() ) {
					log.info( "processNextAvailableUploadedScanFile(): END Processing Scan File in Directory [after calling processNextUploadedScanFile(...): " + scanFileDir.getAbsolutePath() );
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
	 * @param from_scanFileDir
	 * @param processingSuccessFailKilled_Result
	 * @throws Exception
	 */
	private void moveProcessingDirectoryToOneof_Processed_Directories( 
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
			
		} else if ( processingSuccessFailKilled_Result == ProcessingSuccessFailKilled.KILLED ) {
			scanFilesProcessedBaseDirString = ScanFileToProcessConstants.SCAN_FILES_PROCESSED_KILLED_BASE_DIR;
			
		} else {
			String msg = "Invalid value for 'processingSuccessFailKilled_Result': " + processingSuccessFailKilled_Result;
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
		
		if ( ! from_scanFileDir.renameTo( to_scanFileDir ) ) {
			String msg = "Failed to create subdir': " + scanFilesProcessedBaseDir.getAbsolutePath();
			log.error(msg);
			throw new SpectralFileWebappInternalException(msg);
		}
	}

	
}
