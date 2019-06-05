package org.yeastrc.spectral_storage.accept_import_web_app.process_import_request_compute_api_key_store_in_file;

import java.io.File;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main.GetNextScanFileDirToProcessForStatus;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessingStatusFileConstants;

/**
 * Compute the API Key for the Scan File and store in the file
 *
 */
public class Compute_APIKey_Value_StoreInFile_NextAvailableProcessingDir {

	private static final Logger log = LoggerFactory.getLogger( Compute_APIKey_Value_StoreInFile_NextAvailableProcessingDir.class );

	//  private constructor
	private Compute_APIKey_Value_StoreInFile_NextAvailableProcessingDir() { }
	
	/**
	 * @return newly created instance
	 */
	public static Compute_APIKey_Value_StoreInFile_NextAvailableProcessingDir getInstance() { 
		return new Compute_APIKey_Value_StoreInFile_NextAvailableProcessingDir(); 
	}
	
	private volatile boolean shutdownReceived = false;
	
	private Compute_APIKey_Value_StoreInFile_SingleProcessingDir compute_APIKey_Value_StoreInFile_SingleProcessingDir;


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
		
		if ( compute_APIKey_Value_StoreInFile_SingleProcessingDir != null ) {
			compute_APIKey_Value_StoreInFile_SingleProcessingDir.shutdown();
		}
		//  awaken this thread if it is in 'wait' state ( not currently processing a job )
		this.awaken();
	}
	

	/**
	 * @return
	 */
	public boolean compute_APIKey_Value_StoreInFile_NextAvailableProcessingDir() {

		String scanFileDirString = null;
		
		try {
			//  Process next Scan file

			File scanFileProcessingDir = null;
			try {
				scanFileProcessingDir = 
						GetNextScanFileDirToProcessForStatus.getInstance()
						.getNextScanFileDirToProcessForStatus( UploadProcessingStatusFileConstants.STATUS_COMPUTE_API_KEY );

			} catch ( Exception e ) {
				log.error( "Exception getting next Scan file to process", e );
				throw e;
			}

			if ( scanFileProcessingDir == null ) {

				return false;   //  EARLY LOOP EXIT

			} else {
				
				if ( log.isInfoEnabled() ) {
					log.info( "compute_APIKey_Value_StoreInFile_NextAvailableProcessingDir(): START Processing Scan File in Directory [calling compute_APIKey_Value_StoreInFile_SingleProcessingDir(...): " + scanFileProcessingDir.getAbsolutePath() );
				}
				
				scanFileDirString = scanFileProcessingDir.getAbsolutePath();
				
				try {

					compute_APIKey_Value_StoreInFile_SingleProcessingDir = Compute_APIKey_Value_StoreInFile_SingleProcessingDir.getInstance();

					compute_APIKey_Value_StoreInFile_SingleProcessingDir.compute_APIKey_Value_StoreInFile_SingleProcessingDir( scanFileProcessingDir );

				} catch ( Throwable e ) {
					
					throw e;
				}

				if ( log.isInfoEnabled() ) {
					log.info( "compute_APIKey_Value_StoreInFile_NextAvailableProcessingDir(): END Processing Scan File in Directory [after calling compute_APIKey_Value_StoreInFile_SingleProcessingDir(...): " + scanFileProcessingDir.getAbsolutePath() );
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
			compute_APIKey_Value_StoreInFile_SingleProcessingDir = null;

		}

		return true;
	}
}
