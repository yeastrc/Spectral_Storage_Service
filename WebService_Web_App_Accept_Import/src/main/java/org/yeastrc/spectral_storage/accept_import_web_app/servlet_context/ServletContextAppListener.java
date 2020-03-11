package org.yeastrc.spectral_storage.accept_import_web_app.servlet_context;

import java.util.Properties;
import javax.servlet.*;
import javax.servlet.http.*;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.background_thread.ComputeAPIKeyForScanFileThread;
import org.yeastrc.spectral_storage.accept_import_web_app.background_thread.ProcessScanFile_Thread_Container;
import org.yeastrc.spectral_storage.accept_import_web_app.config.A_Load_Config;
import org.yeastrc.spectral_storage.accept_import_web_app.log_error_after_webapp_undeploy_started.Log_Info_Error_AfterWebAppUndeploy_Started;

/**
 * This class is loaded and the method "contextInitialized" is called when the web application is first loaded by the container
 *
 */
public class ServletContextAppListener extends HttpServlet implements ServletContextListener {
	
	private static final Logger log = LoggerFactory.getLogger( ServletContextAppListener.class );
	private static final long serialVersionUID = 1L;
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {
		log.warn( "INFO:  !!!!!!!!!!!!!!!   Start up of web app  'Spectral Storage' beginning  !!!!!!!!!!!!!!!!!!!! " );
		boolean isDevEnv = false;
		Properties prop = System.getProperties();
		String devEnv = prop.getProperty("devEnv");
		if ( "Y".equals(devEnv ) ) {
			isDevEnv = true;
		}
		
		try {
			A_Load_Config.getInstance().load_Config();
		} catch (Exception e) {
			String msg = "Failed to load config";
			log.error( msg, e );
			throw new RuntimeException( e );
		} 

		ServletContext context = event.getServletContext();
		String contextPath = context.getContextPath();
		CurrentContext.setCurrentWebAppContext( contextPath );
		
		startBackgroundThreads(); 
		

		
		log.warn( "INFO:  !!!!!!!!!!!!!!!   Start up of web app  'Spectral Storage' complete  !!!!!!!!!!!!!!!!!!!! " );
		log.warn( "INFO: contextPath: " + contextPath );
	}

	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {
		
		//  !!! Log4J2 stops logging before ServletContext::contextDestroyed(...) is called  !!!
		
		Webapp_Undeploy_Started_Completed.setWebapp_Undeploy_Started(true);
		
//		ServletContext context = event.getServletContext();
		
		//  Nothing output since Log4J2 has stopped logging
//		log.warn("INFO:  !!!!!!!!  Web app Undeploying STARTING  !!!!!!!!");

		Log_Info_Error_AfterWebAppUndeploy_Started.log_INFO_AfterWebAppUndeploy_Started("  !!!!!!!!  Web app Undeploying STARTING  !!!!!!!!" );

		
		stopBackgroundThreads();
		
		
		//  Nothing output since Log4J2 has stopped logging
//		log.warn("INFO:  !!!!!!!!  Web app Undeploying FINISHED  !!!!!!!!");

		Log_Info_Error_AfterWebAppUndeploy_Started.log_INFO_AfterWebAppUndeploy_Started("  !!!!!!!!  Web app Undeploying FINISHED  !!!!!!!!" );

		Webapp_Undeploy_Started_Completed.setWebapp_Undeploy_Completed(true);
	}


	
	//  Start / Stop Background Threads

	/**
	 * Start Background Threads
	 */
	private void startBackgroundThreads() {
		try {
			ProcessScanFile_Thread_Container.getSingletonInstance().initial_CreateStart_Thread();
		} catch (Exception e) {
			log.error( "Failed: ProcessScanFile_Thread_Container.getSingletonInstance().initial_CreateStart_Thread();", e );
			throw new RuntimeException( e );
		} 
		try {
			ComputeAPIKeyForScanFileThread.getInstance().start();
		} catch (Exception e) {
			log.error( "Failed: ComputeAPIKeyForScanFileThread.getInstance().start();", e );
			throw new RuntimeException( e );
		}
	}
	

	/**
	 * Stop Background Threads
	 */
	private void stopBackgroundThreads() {
		try {

			Log_Info_Error_AfterWebAppUndeploy_Started.log_INFO_AfterWebAppUndeploy_Started(
					" Calling ComputeAPIKeyForScanFileThread.getInstance().shutdown().  Check for log msg 'ComputeAPIKeyForScanFileThread: Exitting run().'" );

			ComputeAPIKeyForScanFileThread.getInstance().shutdown();
			
		} catch (Exception e) {

			Log_Info_Error_AfterWebAppUndeploy_Started.log_ERROR_AfterWebAppUndeploy_Started(
					" Failed: ComputeAPIKeyForScanFileThread.getInstance().shutdown()", e );
		} 
		try {
			Log_Info_Error_AfterWebAppUndeploy_Started.log_INFO_AfterWebAppUndeploy_Started(
					" Calling ProcessScanFile_Thread_Container.getSingletonInstance().shutdown().  Check for log msg 'ProcessScanFileThread: Exitting run().'" );

			ProcessScanFile_Thread_Container.getSingletonInstance().shutdown();
			
		} catch (Exception e) {
			
			Log_Info_Error_AfterWebAppUndeploy_Started.log_ERROR_AfterWebAppUndeploy_Started(
					" Failed: ProcessScanFile_Thread_Container.getSingletonInstance().shutdown()", e );
			
			//  Nothing output since Log4J2 has stopped logging
			log.error( "ProcessScanFile_Thread_Container.getSingletonInstance().shutdown();", e );
		}
	}
}
