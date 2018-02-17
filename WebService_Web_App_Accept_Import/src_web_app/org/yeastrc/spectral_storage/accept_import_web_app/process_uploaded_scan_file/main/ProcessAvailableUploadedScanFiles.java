package org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * 
 *
 */
public class ProcessAvailableUploadedScanFiles {

	private static final Logger log = Logger.getLogger(ProcessAvailableUploadedScanFiles.class);

	
	private static enum ProcessUploadedFilesState {
		
		IDLE, PROCESSING
	}
	
	
	private volatile ProcessNextUploadedScanFile processNextUploadedScanFile = null;
	
	private volatile ProcessUploadedFilesState processUploadedFilesState = ProcessUploadedFilesState.IDLE;

	private volatile boolean keepRunning = true;
	

	private ProcessAvailableUploadedScanFiles() { }
	public static ProcessAvailableUploadedScanFiles getInstance() { 
		return new ProcessAvailableUploadedScanFiles(); 
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
		
		if ( processNextUploadedScanFile != null ) {
			processNextUploadedScanFile.shutdown();
		}
		//  awaken this thread if it is in 'wait' state ( not currently processing a job )
		this.awaken();
	}

	
	/**
	 * Process all uploaded scan files that have not been fully processed
	 * 
	 * Return after processing all uploaded scan files or shutdown() has been called
	 */
	public void processAvailableUploadedScanFile() {

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

				return;   //  EARLY LOOP EXIT

			} else {

				processNextUploadedScanFile = ProcessNextUploadedScanFile.getInstance();

				processUploadedFilesState = ProcessUploadedFilesState.PROCESSING;

				processNextUploadedScanFile.processNextUploadedScanFile( scanFileDir );
			}

		} catch ( Exception e ) {

			log.error( "Exception", e );

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

			processUploadedFilesState = ProcessUploadedFilesState.IDLE;
		}


	}

	
}
