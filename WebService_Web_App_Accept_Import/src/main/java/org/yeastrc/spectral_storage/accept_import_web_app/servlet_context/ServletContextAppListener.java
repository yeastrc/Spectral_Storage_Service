package org.yeastrc.spectral_storage.accept_import_web_app.servlet_context;

import java.util.Properties;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.background_thread.ComputeAPIKeyForScanFileThread;
import org.yeastrc.spectral_storage.accept_import_web_app.background_thread.ProcessScanFileThread;
import org.yeastrc.spectral_storage.accept_import_web_app.config.A_Load_Config;

/**
 * This class is loaded and the method "contextInitialized" is called when the web application is first loaded by the container
 *
 */
public class ServletContextAppListener extends HttpServlet implements ServletContextListener {
	
	private static Logger log = Logger.getLogger( ServletContextAppListener.class );
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
		
		try {
			ProcessScanFileThread.getInstance().start();
		} catch (Exception e) {
			log.error( "Failed: ProcessScanFileThread.getInstance().start();", e );
			throw new RuntimeException( e );
		} 

		try {
			ComputeAPIKeyForScanFileThread.getInstance().start();
		} catch (Exception e) {
			log.error( "Failed: ComputeAPIKeyForScanFileThread.getInstance().start();", e );
			throw new RuntimeException( e );
		} 
		

		
		log.warn( "INFO:  !!!!!!!!!!!!!!!   Start up of web app  'Spectral Storage' complete  !!!!!!!!!!!!!!!!!!!! " );
//		log.warn( "INFO: Application context values set.  Key = " + WebConstants.APP_CONTEXT_CONTEXT_PATH + ": value = " + contextPath
//				+ "" );
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {
		
//		ServletContext context = event.getServletContext();
		
		log.warn("INFO:  !!!!!!!!  Web app Undeploying STARTING  !!!!!!!!");
		
		try {
			ProcessScanFileThread.getInstance().shutdown();
		} catch (Exception e) {
		}

		log.warn("INFO:  !!!!!!!!  Web app Undeploying FINISHED  !!!!!!!!");
		

	}
}
