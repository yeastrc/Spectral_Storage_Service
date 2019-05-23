package org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main;

import java.io.File;

import org.apache.log4j.Logger;

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

				processNextUploadedScanFile = ProcessNextUploadedScanFile.getInstance();

//				processUploadedFilesState = ProcessUploadedFilesState.PROCESSING;

				processNextUploadedScanFile.processNextUploadedScanFile( scanFileDir );

				if ( log.isInfoEnabled() ) {
					log.info( "processNextAvailableUploadedScanFile(): END Processing Scan File in Directory [after calling processNextUploadedScanFile(...): " + scanFileDir.getAbsolutePath() );
				}
				
			}

		} catch ( Exception e ) {
			
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

	
}
