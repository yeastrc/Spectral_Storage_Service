package org.yeastrc.spectral_storage.web_app.background_thread;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.web_app.process_uploaded_scan_file.main.ProcessAvailableUploadedScanFiles;

/**
 * 
 *
 */
public class ProcessScanFileThread extends Thread {

	private static final Logger log = Logger.getLogger(ProcessScanFileThread.class);
	
	//  A long sleep/wait time since it is awaken whenever a file is uploaded
	
	private static final int WAIT_TIME_WHEN_NO_WORK_TO_DO = 10 * 60 * 1000;  // in milliseconds

	//  Temp for testing
//	private static final int WAIT_TIME_WHEN_NO_WORK_TO_DO = 5 * 1000;  // in milliseconds
	
	
	private static ProcessScanFileThread instance = null;
	
	private static int threadCreateCount = 0;
	
	
	
	private volatile ProcessAvailableUploadedScanFiles processAvailableUploadedScanFiles;
	


	private volatile boolean keepRunning = true;
	
	
	private volatile boolean skipWait = false;
	
	

	/**
	 * @return
	 */
	public static synchronized ProcessScanFileThread getInstance(){
		
		try {
			if ( instance != null && ( ! instance.keepRunning ) ) {
				
				//  Requested that thread was stopped so just return it.
				return instance;
			}
		} catch ( NullPointerException e ) {
			//  Eat exception and continue, instance is now null
			
		}
		
		if ( instance == null ) {
			
			createThread( false );
		
		} else if ( ! instance.isAlive() ) {
			
			if ( ! instance.keepRunning ) {

				Exception exception = new Exception( "Fake Exception to get call stack" );

				log.error( "ProcessImportFASTAFileThread has died and will be replaced.", exception );
			}
			
			createThread( false );
		}
		
		return instance;
	}
	

	/**
	 * 
	 */
	private void createThreadAndStartIfNotExistOrDead() {
		
		if ( instance == null ) {
			
			createThread( true );
		
		} else if ( ! instance.isAlive() ) {
			
			if ( ! instance.keepRunning ) {

				Exception exception = new Exception( "Fake Exception to get call stack" );

				log.error( "ProcessImportFASTAFileThread has died and will be replaced.", exception );
			}
			
			createThread( true );
		}
	}
	
	/**
	 * 
	 */
	private synchronized static void createThread( boolean startThread ) {
		
		threadCreateCount++;
		
		instance = new ProcessScanFileThread();
		instance.setName( "ProcessImportFASTAFile-Thread-" + threadCreateCount );
		
		if ( startThread ) {
			instance.start();
		}
	}
	
	/**
	 * awaken thread to process request, calls "notify()"
	 */
	public void awaken() {

		if ( log.isDebugEnabled() ) {
			log.debug("awaken() called:  " );
		}

		synchronized (this) {
			
			skipWait = true;

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

		//  awaken this thread if it is in 'wait' state ( not currently processing a job )
		this.awaken();
		if ( processAvailableUploadedScanFiles != null ) {
			processAvailableUploadedScanFiles.shutdown();
		}
	}

	/**
	 * 
	 */
	public void stopAfterCurrentFile() {

		if ( log.isDebugEnabled() ) {
			log.debug("stopAfterCurrentFile() called:  " );
		}
		
		keepRunning = false; 
		
		awaken();
	}

	/**
	 * 
	 */
	public void startIfStopped_ClearStopAfterCurrentFile() {

		if ( log.isDebugEnabled() ) {
			log.debug("startIfStopped_ClearStopAfterCurrentFile() called:  " );
		}
		
		keepRunning = true; 
		
		createThreadAndStartIfNotExistOrDead();
	}

	
	/**
	 * @return
	 */
	public boolean isProcessingFiles() {
		if ( processAvailableUploadedScanFiles != null ) {
			return true;
		}
		return false;
	}

	

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
//		RestartAndResetInProgressRequestsOnWebappStartup.getInstance().process();
		
		while ( keepRunning ) {

			/////////////////////////////////////////
			
			try {
				skipWait = false; //  set true in awaken()
				
				processAvailableUploadedScanFiles = ProcessAvailableUploadedScanFiles.getInstance();
				
				processAvailableUploadedScanFiles.processAvailableUploadedScanFile();
				
				processAvailableUploadedScanFiles = null;

			} catch (Throwable e) {

				log.error("ProcessUploadedScanFiles.getInstance().processNextFASTAFile()", e );

			}
			
			////////////////////////////////////

			//  Then put thread to sleep until more work to do

			synchronized (this) {
				try {
					if ( skipWait ) {  //  set true in awaken()
					} else {
						log.debug( "before 'while ( keepRunning )', before wait() called" );

						wait( WAIT_TIME_WHEN_NO_WORK_TO_DO );

						if ( log.isDebugEnabled() ) {
							log.debug("before 'while ( keepRunning )', after wait() called:  ProcessImportFASTAFileThread.getId() = " + this.getId() );
						}
					}
				} catch (InterruptedException e) {
					log.info("wait() interrupted with InterruptedException");
				}
			}
		}
		
		log.info("Exitting run()" );
	}


}
