package org.yeastrc.spectral_storage.accept_import_web_app.background_thread;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.accept_import_web_app.log_error_after_webapp_undeploy_started.Log_Info_Error_AfterWebAppUndeploy_Started;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;

/**
 * Manage the Containers that manage the Background Threads
 * 
 * Any new Background Threads need to be added here
 * 
 * 
 *
 */
public class A_BackgroundThreads_Containers_Manager {

	private static final Logger log = LoggerFactory.getLogger(A_BackgroundThreads_Containers_Manager.class);

	private static final A_BackgroundThreads_Containers_Manager containerInstance = new A_BackgroundThreads_Containers_Manager();
	
	/**
	 * @return
	 */
	public static A_BackgroundThreads_Containers_Manager getSingletonInstance(){
		
		return containerInstance;
	}
	
	private List<A_BackgroundThreadContainers_Common_AbstractBaseClass> backgroundThreadContainers = null;
	
	//  Also list individually so can share with where needed
	
	private ComputeAPIKeyForScanFile_Thread_Container computeAPIKeyForScanFile_Thread_Container;
	
	private ProcessScanFile_Thread_Container processScanFile_Thread_Container;
	

	public ComputeAPIKeyForScanFile_Thread_Container getComputeAPIKeyForScanFile_Thread_Container() {
		if ( computeAPIKeyForScanFile_Thread_Container == null ) {
			String msg = "getComputeAPIKeyForScanFile_Thread_Container() called before initial_CreateStart_Thread() called.";
			log.error(msg);
			throw new SpectralStorageProcessingException(msg);
		}
		return computeAPIKeyForScanFile_Thread_Container;
	}

	public ProcessScanFile_Thread_Container getProcessScanFile_Thread_Container() {
		if ( processScanFile_Thread_Container == null ) {
			String msg = "getProcessScanFile_Thread_Container() called before initial_CreateStart_Thread() called.";
			log.error(msg);
			throw new SpectralStorageProcessingException(msg);
		}
		return processScanFile_Thread_Container;
	}
	
	/**
	 * 
	 */
	public synchronized void initial_CreateStart_Thread() {
		
		//  Create containers
		
		backgroundThreadContainers = new ArrayList<>();

		computeAPIKeyForScanFile_Thread_Container = ComputeAPIKeyForScanFile_Thread_Container.getInstance();
		backgroundThreadContainers.add(computeAPIKeyForScanFile_Thread_Container);
		
		processScanFile_Thread_Container = ProcessScanFile_Thread_Container.getInstance();

		backgroundThreadContainers.add(processScanFile_Thread_Container);
		
		startBackgroundThreads();
	}
	
	public synchronized void shutdownBackgroundThreads() {
		
		shutdownBackgroundThreads_Internal();
	}
	
	/**
	 * 
	 */
	private void startBackgroundThreads() {
		
		for ( A_BackgroundThreadContainers_Common_AbstractBaseClass backgroundThreadContainer : backgroundThreadContainers ) {
			
			backgroundThreadContainer.initial_CreateStart_Thread();
		}
	}
	/**
	 * 
	 */
	private void shutdownBackgroundThreads_Internal() {
		
		for ( A_BackgroundThreadContainers_Common_AbstractBaseClass backgroundThreadContainer : backgroundThreadContainers ) {
			
			try {
				backgroundThreadContainer.shutdown();

			} catch (Throwable e) {
				
				String clasName = backgroundThreadContainer.getClass().getCanonicalName();
				
				Log_Info_Error_AfterWebAppUndeploy_Started.log_ERROR_AfterWebAppUndeploy_Started(
						" Failed: " + clasName + ".shutdown() (class name in variable)", e );
				
				//  Nothing output since Log4J2 has stopped logging
				log.error( " Failed: " + clasName + ".shutdown() (class name in variable)", e );
			}
		}
	}

	
}
