package org.yeastrc.spectral_storage.accept_import_web_app.servlet_context;

import java.util.Properties;
import javax.servlet.*;
import javax.servlet.http.*;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.background_thread.ComputeAPIKeyForScanFileThread;
import org.yeastrc.spectral_storage.accept_import_web_app.background_thread.ProcessScanFile_Thread_Container;
import org.yeastrc.spectral_storage.accept_import_web_app.config.A_Load_Config;

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
		

		
		log.warn( "INFO:  !!!!!!!!!!!!!!!   Start up of web app  'Spectral Storage' complete  !!!!!!!!!!!!!!!!!!!! " );
		log.warn( "INFO: contextPath: " + contextPath );
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {
		
//		ServletContext context = event.getServletContext();
		
		log.warn("INFO:  !!!!!!!!  Web app Undeploying STARTING  !!!!!!!!");
		
		System.out.println( 
				"Spectral Storage Accept Import Webapp: CurrentContext: " + CurrentContext.getCurrentWebAppContext() 
				+ ".  INFO:  !!!!!!!!  Web app Undeploying STARTING  !!!!!!!!" );

		try {
			ComputeAPIKeyForScanFileThread.getInstance().shutdown();
		} catch (Exception e) {
			log.error( "Failed: ComputeAPIKeyForScanFileThread.getInstance().shutdown();", e );

			System.err.println( 
					"Spectral Storage Accept Import Webapp: CurrentContext: " + CurrentContext.getCurrentWebAppContext() 
					+ ". Failed: ComputeAPIKeyForScanFileThread.getInstance().shutdown();" );
			e.printStackTrace();
		} 
		
		try {
			ProcessScanFile_Thread_Container.getSingletonInstance().shutdown();
		} catch (Exception e) {
			log.error( "ProcessScanFile_Thread_Container.getSingletonInstance().shutdown();", e );

			System.err.println( 
					"Spectral Storage Accept Import Webapp: CurrentContext: " + CurrentContext.getCurrentWebAppContext() 
					+ ". ProcessScanFile_Thread_Container.getSingletonInstance().shutdown();" );
			e.printStackTrace();
		}
		
		log.warn("INFO:  !!!!!!!!  Web app Undeploying FINISHED  !!!!!!!!");

		System.out.println( 
				"Spectral Storage Accept Import Webapp: CurrentContext: " + CurrentContext.getCurrentWebAppContext() 
				+ ".  INFO:  !!!!!!!!  Web app Undeploying FINISHED  !!!!!!!!" );

	}
}
