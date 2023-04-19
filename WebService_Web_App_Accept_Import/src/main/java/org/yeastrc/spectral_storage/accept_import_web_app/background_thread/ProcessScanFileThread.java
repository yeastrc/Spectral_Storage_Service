package org.yeastrc.spectral_storage.accept_import_web_app.background_thread;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.cleanup_temp_upload_dir.CleanupUploadFileTempBaseDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.log_error_after_webapp_undeploy_started.Log_Info_Error_AfterWebAppUndeploy_Started;
import org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main.Cleanup_RemoveOldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories;
import org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main.Cleanup_Remove_ScanFileIn_OldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories;
import org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main.ProcessNextAvailableUploadedScanFile;
import org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.move_old_processed_directories.MoveOldProcessedUploadScanFileDirectories;
import org.yeastrc.spectral_storage.accept_import_web_app.reset_killed_import_to_pending_on_webapp_startup.ResetKilledImportToPendingOnWebappStartup;
import org.yeastrc.spectral_storage.accept_import_web_app.servlet_context.Webapp_Undeploy_Started_Completed;

/**
 * Executes the code to run the Scan File Processor on an submitted Scan File
 * 
 * Package Private
 *
 */
class ProcessScanFileThread extends Thread {

	private static final Logger log = LoggerFactory.getLogger(ProcessScanFileThread.class);
	
	enum Reset_From_Request_stopAfterCurrentFile_Result {
		SUCCESS, THREAD_DEAD, SHUTDOWN_IN_PROGRESS
	}
	
	//  A long sleep/wait time since it is awaken whenever a file is uploaded
	
	private static final int WAIT_TIME_WHEN_NO_WORK_TO_DO = 10 * 60 * 1000;  // in milliseconds

	//  Shortened sleep/wait time to make sure never too long to wait to start processing a scan file
//	private static final int WAIT_TIME_WHEN_NO_WORK_TO_DO = 10 * 1000;  // in milliseconds
	
	//  For Reporting
	private static final int WAIT_TIME_WHEN_NO_WORK_TO_DO_80_PERCENT = (int) ( WAIT_TIME_WHEN_NO_WORK_TO_DO * 0.8 );  // in milliseconds
	
	
	//  Wait after being awakened for processing import to wait for File system to update
	private static final int WAIT_TIME_BRIEF_WAIT_AFTER_AWAKEN_FOR_FILE_SYSTEM = 3 * 1000;  // in milliseconds
	

	private volatile ProcessNextAvailableUploadedScanFile processNextAvailableUploadedScanFile;
	
	private volatile Cleanup_RemoveOldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories cleanup_RemoveOldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories;
	
	private volatile Cleanup_Remove_ScanFileIn_OldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories cleanup_Remove_ScanFileIn_OldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories;
	
	private volatile boolean keepRunning = true;
	
	//  internal private Setter/Getter for synchronized

	private synchronized boolean isKeepRunning() {
		return keepRunning;
	}
	private synchronized void setKeepRunning(boolean keepRunning) {
		this.keepRunning = keepRunning;
	}
	
	
	/**
	 * Set to true if keepRunning is false at bottom of main run loop.
	 * Main Run loop is exitted if exited_Main_keepRunning_Loop is true at the top of the loop.
	 * If true, then main run() method on Thread has exitted  or is about to exit.
	 */
	private volatile boolean exited_Main_keepRunning_Loop = false;
	
	private volatile boolean shutdownRequested = false;

	private volatile boolean skipWait = false;
	

	private volatile boolean awakenCalledSinceCalledMainWait = false;
	
	private volatile long awakenCalledTime = 0;
	

	/**
	 * Set when created
	 */
	private int threadCreateCount;  

	public void setThreadCreateCount(int threadCreateCount) {
		this.threadCreateCount = threadCreateCount;
	}

	
	/**
	 * Constructor - Package Private
	 */
	ProcessScanFileThread() {
		super();
	}

	
	/**
	 * awaken thread to process request, calls "notify()"
	 */
	void awaken() {

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
	synchronized void shutdown() {

		if ( Webapp_Undeploy_Started_Completed.isWebapp_Undeploy_Started() ) {
			
			//  Log this way since Log4J is now stopped

			String msg = "ProcessScanFileThread: shutdown() called.  Validate that 'Exitting run().' for this class is also written to log before undeploy is considered complete.";
			Log_Info_Error_AfterWebAppUndeploy_Started.log_INFO_AfterWebAppUndeploy_Started(msg);
		}
		
		log.info("shutdown() called");
		synchronized (this) {
			this.shutdownRequested = true;
			this.keepRunning = false;
		}

		//  awaken this thread if it is in 'wait' state ( not currently processing a job )
		this.awaken();
		
		try {
			if ( processNextAvailableUploadedScanFile != null ) {
				processNextAvailableUploadedScanFile.shutdown();
			}
		} catch ( Throwable t ) {
			
		}
		try {
			if ( cleanup_RemoveOldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories != null ) {
				cleanup_RemoveOldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories.shutdown();
			}
		} catch ( Throwable t ) {
			
		}
		try {
			if ( cleanup_Remove_ScanFileIn_OldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories != null ) {
				cleanup_Remove_ScanFileIn_OldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories.shutdown();
			}
		} catch ( Throwable t ) {
			
		}
	}

	/**
	 * Reverse request: stopAfterCurrentFile
	 * @return
	 */
	public synchronized Reset_From_Request_stopAfterCurrentFile_Result reset_From_Request_stopAfterCurrentFile() {
		
		if ( shutdownRequested ) {
		
			return Reset_From_Request_stopAfterCurrentFile_Result.SHUTDOWN_IN_PROGRESS;  // EARY RETURN
		}
		if ( ! isAlive() ) {
			return Reset_From_Request_stopAfterCurrentFile_Result.THREAD_DEAD;  // EARY RETURN
		}
		if ( exited_Main_keepRunning_Loop ) {
			return Reset_From_Request_stopAfterCurrentFile_Result.THREAD_DEAD;  // EARY RETURN
		}

		this.keepRunning = true;
		
		return Reset_From_Request_stopAfterCurrentFile_Result.SUCCESS;
	}	



	/**
	 * 
	 */
	public void stopAfterCurrentFile() {

		if ( log.isDebugEnabled() ) {
			log.debug("stopAfterCurrentFile() called:  " );
		}

		log.warn("INFO: stopAfterCurrentFile() called:  " );
		
		setKeepRunning( false ); 
		
		awaken();
	}
	
	/**
	 * @return
	 */
	public boolean isProcessingFiles() {
		if ( processNextAvailableUploadedScanFile != null ) {
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
		
		try {
			//  On first thread start, move existing directories
			if ( threadCreateCount <= 1 ) {
				
				MoveOldProcessedUploadScanFileDirectories.getInstance().moveOldProcessedUploadScanFileDirectories();
			}
		} catch ( Throwable t ) {
			log.warn( "Error calling MoveOldProcessedUploadScanFileDirectories.getInstance().moveOldProcessedUploadScanFileDirectories();", t );
		}

		try {
			//  On first thread start, Reset imports with status Killed to Pending
			if ( threadCreateCount <= 1 ) {
				
				ResetKilledImportToPendingOnWebappStartup.getInstance().resetKilledImportToPendingOnWebappStartup();
			}
		} catch ( Throwable t ) {
			log.warn( "Error calling ResetKilledImportToPendingOnWebappStartup.getInstance().resetKilledImportToPendingOnWebappStartup();", t );
		}

		try {
			CleanupUploadFileTempBaseDirectory.getSingletonInstance().cleanupUploadFileTempBaseDirectory();
		} catch ( Throwable t ) {
			log.warn( "Error calling CleanupUploadFileTempBaseDirectory.getSingletonInstance().cleanupUploadFileTempBaseDirectory();", t );
		}
		
		while ( isKeepRunning() ) {
			
			if ( Webapp_Undeploy_Started_Completed.isWebapp_Undeploy_Completed() ) {
				
				//  ERROR, this Thread should be dead before Undeploy has completed.

				String msg = "ProcessScanFileThread: In run().  In while ( isKeepRunning() ) when is true: if ( Webapp_Undeploy_Started_Completed.isWebapp_Undeploy_Completed() ).  Breaking from loop now so run() will exit.";
				Log_Info_Error_AfterWebAppUndeploy_Started.log_ERROR_AfterWebAppUndeploy_Started(msg);
				
				//   EXIT This loop and exit run() method immediately
				
				break;  //  EARLY BREAK
			}
			
			if ( exited_Main_keepRunning_Loop ) {
				
				//  exited_Main_keepRunning_Loop is set to true if keepRunning is false at the bottom of this loop
				
				break;  //  EARLY BREAK
			}

			/////////////////////////////////////////
			
			try {
				skipWait = false; //  set true in awaken()
				
				processAvailableUploadedScanFiles();

			} catch (Throwable throwable) {
				
				String msg = "ProcessScanFileThread:  Exception from: processAvailableUploadedScanFiles()";

				log.error( msg, throwable );


				if ( Webapp_Undeploy_Started_Completed.isWebapp_Undeploy_Started() ) {
					
					Log_Info_Error_AfterWebAppUndeploy_Started.log_ERROR_AfterWebAppUndeploy_Started(msg, throwable);
				}
			}

			try {
				CleanupUploadFileTempBaseDirectory.getSingletonInstance().cleanupUploadFileTempBaseDirectory();
				
			} catch ( Throwable throwable ) {
				
				String msg ="ProcessScanFileThread: Error calling CleanupUploadFileTempBaseDirectory.getSingletonInstance().cleanupUploadFileTempBaseDirectory();"; 
				
				log.warn( msg, throwable );
				

				if ( Webapp_Undeploy_Started_Completed.isWebapp_Undeploy_Started() ) {
					
					Log_Info_Error_AfterWebAppUndeploy_Started.log_ERROR_AfterWebAppUndeploy_Started(msg, throwable);
				}
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
						
						if ( isKeepRunning() ) {
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
			

			if ( ! isKeepRunning()  ) {
				
				exited_Main_keepRunning_Loop = true;  //  Ensures that this is only true when about to exit this while loop
			}
				
				//  exited_Main_keepRunning_Loop is set to true if keepRunning is false at the bottom of this loop
		}
		
		log.warn( "INFO: Exitting run()" );
		

		if ( Webapp_Undeploy_Started_Completed.isWebapp_Undeploy_Started() ) {
			
			//  Log this way since Log4J is now stopped

			String msg = "ComputeAPIKeyForScanFileThread: Exitting run().";
			Log_Info_Error_AfterWebAppUndeploy_Started.log_INFO_AfterWebAppUndeploy_Started(msg);
		}
		
	}


	
	/**
	 * Process all uploaded scan files that have not been fully processed
	 * 
	 * Return after processing all uploaded scan files or shutdown() has been called
	 */
	private void processAvailableUploadedScanFiles() {

		boolean processed_A_Scanfile = true; // init to true to prime loop
		
		while ( isKeepRunning() && processed_A_Scanfile ) {

			/////////////////////////////////////////
			
//			try {

			processNextAvailableUploadedScanFile = ProcessNextAvailableUploadedScanFile.getInstance();

			//  Process all available uploaded scan files
			processed_A_Scanfile = processNextAvailableUploadedScanFile.processNextAvailableUploadedScanFile();

			processNextAvailableUploadedScanFile = null;
				
			if ( log.isDebugEnabled() ) {
				if ( processed_A_Scanfile ) {
					log.debug( "processNextAvailableUploadedScanFile() returned true so YES processed a file" );
				} else {
					log.debug( "processNextAvailableUploadedScanFile() returned false so NO processed a file" );
				}
			}
				
//			} ( catch)
			
			try {
				cleanup_RemoveOldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories = Cleanup_RemoveOldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories.getInstance();
				
				cleanup_RemoveOldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories.cleanup_RemoveOldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories();
				
				cleanup_RemoveOldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories = null;
				
			} catch ( Throwable t ) {
				
			}

			try {
				cleanup_Remove_ScanFileIn_OldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories = Cleanup_Remove_ScanFileIn_OldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories.getInstance();
				
				cleanup_Remove_ScanFileIn_OldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories.cleanup_Remove_ScanFileIn_OldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories();
				
				cleanup_Remove_ScanFileIn_OldImportProcessDirectoriesUnderSuccessfulAndFailedSubdirectories = null;
			
			} catch ( Throwable t ) {
				
			}
		}
	}








}
