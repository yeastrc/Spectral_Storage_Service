package org.yeastrc.spectral_storage.accept_import_web_app.background_thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.accept_import_web_app.background_thread.ProcessScanFileThread.Reset_From_Request_stopAfterCurrentFile_Result;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileWebappInternalException;

/**
 * Container that holds current ProcessScanFileThread  Instance or null
 *
 */
public class ProcessScanFile_Thread_Container {

	

	private static final Logger log = LoggerFactory.getLogger(ProcessScanFile_Thread_Container.class);
	
	private static final ProcessScanFile_Thread_Container containerInstance = new ProcessScanFile_Thread_Container();
	
	/**
	 * @return
	 */
	public static ProcessScanFile_Thread_Container getSingletonInstance(){
		
		return containerInstance;
	}
	
	
	private volatile ProcessScanFileThread threadInstance = null;

	private volatile int threadCreateCount = 0;

	private volatile boolean shutdownRequested = false;

	/**
	 * From Context
	 */
	public synchronized void initial_CreateStart_Thread() {

		if ( log.isInfoEnabled() ) {
			log.info("initial_CreateStart_Thread() called:  " );
		}

		createThread();
	}
	
	/**
	 * @return
	 */
	public boolean isThreadAlive() {
		
		if ( threadInstance != null ) {
			
			return threadInstance.isAlive();
		}
		
		return false;
	}
	
	/**
	 * awaken thread to process request
	 */
	public synchronized void awakenToProcessAScanFile() {

		if ( log.isInfoEnabled() ) {
			log.info("awakenToProcessAScanFile() called:  " );
		}

		if ( shutdownRequested ) {
			
			log.warn( "!!!!!!!!!!!!" );
			log.warn( "!!!!!!  awakenToProcessAScanFile() called and shutdownRequested is true!!!!  Skipping processing in this method. !!!!" );
			log.warn( "!!!!!!!!!!!!" );
			
			return; //  EARLY RETURN
		}
		
		if ( threadInstance != null ) {
			
			threadInstance.awaken();
		}
	}
	



	/**
	 * shutdown was received from the operating system.  This is called on a different thread.
	 */
	public synchronized void shutdown() {
		
		log.warn( "INFO: shutdown() called" );
		
		shutdownRequested = true;

		if ( threadInstance != null ) {
			
			threadInstance.shutdown();
		}
	}

	/**
	 * 
	 */
	public synchronized void stopAfterCurrentFile() {

		log.warn( "INFO: stopAfterCurrentFile() called" );
		
		if ( shutdownRequested ) {
			
			log.warn( "stopAfterCurrentFile() called and shutdownRequested is true" );
		}

		if ( threadInstance != null ) {
			
			threadInstance.stopAfterCurrentFile();
		}
	}

	/**
	 * @throws SpectralFileWebappInternalException 
	 * 
	 */
	public synchronized void startIfStopped_ClearStopAfterCurrentFile() throws SpectralFileWebappInternalException {

		log.warn( "INFO: startIfStopped_ClearStopAfterCurrentFile() called" );

		if ( shutdownRequested ) {
			
			log.warn( "startIfStopped_ClearStopAfterCurrentFile() called and shutdownRequested is true.  Skipping processing in this method." );
			
			return; //  EARLY RETURN
		}

		if ( threadInstance != null ) {
			
			try {
							
				Reset_From_Request_stopAfterCurrentFile_Result reset_From_Request_stopAfterCurrentFile_Result = 
						threadInstance.reset_From_Request_stopAfterCurrentFile();
				
				if ( reset_From_Request_stopAfterCurrentFile_Result == Reset_From_Request_stopAfterCurrentFile_Result.SUCCESS ) {
					return;  // EARLY RETURN
				}
				
				if ( reset_From_Request_stopAfterCurrentFile_Result == Reset_From_Request_stopAfterCurrentFile_Result.SHUTDOWN_IN_PROGRESS ) {
					String msg = "startIfStopped_ClearStopAfterCurrentFile(): Exit since shutdown has been called.";
					log.warn(msg);
					return;  // EARLY RETURN
				}
				
				if ( reset_From_Request_stopAfterCurrentFile_Result == Reset_From_Request_stopAfterCurrentFile_Result.THREAD_DEAD ) {
					//  Thread is dead or about to be dead so create a new thread
					
					createThread();
					
					return;  // EARLY RETURN
				}
				
				String msg = "Result from threadInstance.reset_From_Request_stopAfterCurrentFile() is not expected value.  result: " + reset_From_Request_stopAfterCurrentFile_Result;
				log.error(msg);
				throw new SpectralFileWebappInternalException(msg);
				
			} catch ( NullPointerException e ) {
				//  If got this, then eat it and do next step since now have no thread
			}
		}
		
		//  threadInstance == null
		
		createThread();
	}

	
	/**
	 * @return
	 */
	public synchronized boolean isProcessingFiles() {

		log.warn( "INFO: isProcessingFiles() called" );

		if ( threadInstance != null ) {
			
			return threadInstance.isProcessingFiles();
		}
		
		return false;
	}

	/**
	 * 
	 */
	private void createThread() {
		
		threadCreateCount++;
		
		threadInstance = new ProcessScanFileThread();
		threadInstance.setName( "ProcessScanFileThread-Thread-" + threadCreateCount );
		
		threadInstance.start();
		
		if ( log.isWarnEnabled() ) {
			String msg = "INFO: Exit: createThread(startThread) Creating new ProcessScanFileThread (extends Thread) object. threadCreateCount: " + threadCreateCount
					+ ", instance: " + threadInstance;
			log.warn( msg );
		}

	}
	
}
