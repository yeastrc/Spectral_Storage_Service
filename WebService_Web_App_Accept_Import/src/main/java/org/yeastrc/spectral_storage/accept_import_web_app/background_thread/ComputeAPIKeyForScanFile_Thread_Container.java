package org.yeastrc.spectral_storage.accept_import_web_app.background_thread;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;

/**
 * Container that holds current ComputeAPIKeyForScanFileThread  Instance or null
 *
 */
public class ComputeAPIKeyForScanFile_Thread_Container {

	private static final Logger log = LoggerFactory.getLogger(ComputeAPIKeyForScanFile_Thread_Container.class);

	private static final ComputeAPIKeyForScanFile_Thread_Container containerInstance = new ComputeAPIKeyForScanFile_Thread_Container();
	
	/**
	 * @return
	 */
	public static ComputeAPIKeyForScanFile_Thread_Container getSingletonInstance(){
		
		return containerInstance;
	}
	
	
	private volatile ComputeAPIKeyForScanFileThread threadInstance = null;

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
	private void createThread() {
		
		threadCreateCount++;
		
		threadInstance = new ComputeAPIKeyForScanFileThread();
		threadInstance.setName( "ComputeAPIKeyForScanFileThread-Thread-" + threadCreateCount );
		
		threadInstance.start();
		
		if ( log.isWarnEnabled() ) {
			String msg = "INFO: Exit: createThread(startThread) Creating new ComputeAPIKeyForScanFileThread (extends Thread) object. threadCreateCount: " + threadCreateCount
					+ ", instance: " + threadInstance;
			log.warn( msg );
		}

	}
	
}
