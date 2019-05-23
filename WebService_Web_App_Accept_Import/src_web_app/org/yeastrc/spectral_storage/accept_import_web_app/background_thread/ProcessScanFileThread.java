package org.yeastrc.spectral_storage.accept_import_web_app.background_thread;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main.ProcessAvailableUploadedScanFiles;

/**
 * 
 *
 */
public class ProcessScanFileThread extends Thread {

	private static final Logger log = Logger.getLogger(ProcessScanFileThread.class);
	
	//  A long sleep/wait time since it is awaken whenever a file is uploaded
	
//	private static final int WAIT_TIME_WHEN_NO_WORK_TO_DO = 10 * 60 * 1000;  // in milliseconds

	//  Shortened sleep/wait time to make sure never too long to wait to start processing a scan file
	private static final int WAIT_TIME_WHEN_NO_WORK_TO_DO = 10 * 1000;  // in milliseconds
	
	//  For Reporting
	private static final int WAIT_TIME_WHEN_NO_WORK_TO_DO_80_PERCENT = (int) ( WAIT_TIME_WHEN_NO_WORK_TO_DO * 0.8 );  // in milliseconds
	
	
	//  Wait after being awakened for processing import to wait for File system to update
	private static final int WAIT_TIME_BRIEF_WAIT_AFTER_AWAKEN_FOR_FILE_SYSTEM = 3 * 1000;  // in milliseconds
	
	
	private static ProcessScanFileThread instance = null;
	
	private static int threadCreateCount = 0;
	
	
	
	private volatile ProcessAvailableUploadedScanFiles processAvailableUploadedScanFiles;
	


	private volatile boolean keepRunning = true;
	
	
	private volatile boolean skipWait = false;
	

	private volatile boolean awakenCalledSinceCalledMainWait = false;
	
	private volatile long awakenCalledTime = 0;
	
	

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

				log.error( "ProcessScanFileThread has died and will be replaced.", exception );
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

				log.error( "ProcessScanFileThread has died and will be replaced.", exception );
			}
			
			createThread( true );
		}
	}
	
	/**
	 * 
	 */
	private synchronized static void createThread( boolean startThread ) {
		
		threadCreateCount++;

		if ( log.isInfoEnabled() ) {
			log.info( "Creating new ProcessScanFileThread (extends Thread) object. threadCreateCount: " + threadCreateCount );
		}
		
		
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
			
			awakenCalledSinceCalledMainWait = true;
			
			awakenCalledTime = System.currentTimeMillis();

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

		log.warn("INFO: stopAfterCurrentFile() called:  " );
		
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

		log.warn("INFO: startIfStopped_ClearStopAfterCurrentFile() called:  " );

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
						log.debug( "IN 'while ( keepRunning )', before wait( WAIT_TIME_WHEN_NO_WORK_TO_DO ) called" );
						
						{
							long timeBefore_WAIT_TIME_WHEN_NO_WORK_TO_DO = System.currentTimeMillis();

							//  Set to false just before call wait(...), set to true in method awaken()
							awakenCalledSinceCalledMainWait = false;  
							
							wait( WAIT_TIME_WHEN_NO_WORK_TO_DO );

							long timeAfter_WAIT_TIME_WHEN_NO_WORK_TO_DO = System.currentTimeMillis();

							if ( log.isDebugEnabled() ) {
								log.debug("IN 'while ( keepRunning )', after wait( WAIT_TIME_WHEN_NO_WORK_TO_DO ) called:  ProcessScanFileThread.getId() = " + this.getId() );
							}

							long timeInWait = timeAfter_WAIT_TIME_WHEN_NO_WORK_TO_DO - timeBefore_WAIT_TIME_WHEN_NO_WORK_TO_DO;
							
							if ( awakenCalledSinceCalledMainWait ) {
								// awaken() was called after call to wait( WAIT_TIME_WHEN_NO_WORK_TO_DO );
								if ( timeInWait > WAIT_TIME_WHEN_NO_WORK_TO_DO_80_PERCENT ) {
									if ( log.isInfoEnabled() ) {
										log.info( "After wait( WAIT_TIME_WHEN_NO_WORK_TO_DO ):  awaken() was called after  wait( WAIT_TIME_WHEN_NO_WORK_TO_DO ) was called.  Time spent in wait( WAIT_TIME_WHEN_NO_WORK_TO_DO ); is > 80% of WAIT_TIME_WHEN_NO_WORK_TO_DO. Time spent in wait (milliseconds): "  
												+ timeInWait );
									}
								}
								// timeSinceAwakenCalled: time in milliseconds
								long timeSinceAwakenCalled = timeAfter_WAIT_TIME_WHEN_NO_WORK_TO_DO - awakenCalledTime;
								if ( timeSinceAwakenCalled > 2000 ) {
									if ( log.isInfoEnabled() ) {
										log.info( "After wait( WAIT_TIME_WHEN_NO_WORK_TO_DO ):  awaken() was called after  wait( WAIT_TIME_WHEN_NO_WORK_TO_DO ) was called. Time since awaken was called > 2000 milliseconds. Time since awaken was called (milliseconds): "  
												+ timeSinceAwakenCalled );
									}
								}
							}
						}
						
						if ( keepRunning ) {
							log.debug( "IN 'while ( keepRunning )', before 'if ( keepRunning )', before wait( WAIT_TIME_BRIEF_WAIT_AFTER_AWAKEN_FOR_FILE_SYSTEM ) called" );

							wait( WAIT_TIME_BRIEF_WAIT_AFTER_AWAKEN_FOR_FILE_SYSTEM );

							log.debug( "IN 'while ( keepRunning )', before 'if ( keepRunning )', before wait( WAIT_TIME_BRIEF_WAIT_AFTER_AWAKEN_FOR_FILE_SYSTEM ) called" );
						}

						if ( log.isDebugEnabled() ) {
							log.debug("before 'while ( keepRunning )', after wait() called:  ProcessScanFileThread.getId() = " + this.getId() );
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
