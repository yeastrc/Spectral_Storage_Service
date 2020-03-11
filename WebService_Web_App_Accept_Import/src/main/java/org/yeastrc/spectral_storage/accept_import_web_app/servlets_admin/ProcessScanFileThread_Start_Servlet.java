package org.yeastrc.spectral_storage.accept_import_web_app.servlets_admin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.background_thread.ProcessScanFile_Thread_Container;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.AdminPageConstants;

/**
 * 
 *
 */
public class ProcessScanFileThread_Start_Servlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(ProcessScanFileThread_Start_Servlet.class);


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String adminKeyFromQueryString = null;
		String adminKeyFromSession = null;

		try {
			adminKeyFromQueryString = request.getParameter( AdminPageConstants.ADMIN_KEY_QUERY_STRING_PARAMETER_NAME );
		} catch (Throwable e) {
			log.error( "Failed to get query string parameter:", e );
			response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
			try ( PrintWriter writer = response.getWriter() ) {
				writer.append( "Fail bad request" );
			}

			return;
		}

		try {
			adminKeyFromSession = (String) request.getSession().getAttribute( AdminPageConstants.ADMIN_KEY_SESSION_KEY_PARAMETER_NAME );
		} catch (Throwable e) {
			log.error( "Failed to get session parameter:", e );
			response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
			try ( PrintWriter writer = response.getWriter() ) {
				writer.append( "Fail bad request" );
			}

			return;
		}
		
		if ( StringUtils.isEmpty( adminKeyFromQueryString ) ) {
			log.warn( "No adminKeyFromQueryString request parameter '" + AdminPageConstants.ADMIN_KEY_QUERY_STRING_PARAMETER_NAME + "'");
			response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
			try ( PrintWriter writer = response.getWriter() ) {
				writer.append( "Fail bad request" );
			}

			return;
		}

		if ( StringUtils.isEmpty( adminKeyFromSession ) ) {
			log.warn( "No adminKeyFromSession session parameter '" + AdminPageConstants.ADMIN_KEY_SESSION_KEY_PARAMETER_NAME + "'");
			response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
			try ( PrintWriter writer = response.getWriter() ) {
				writer.append( "Fail bad request" );
			}

			return;
		}

		if ( ! adminKeyFromQueryString.equals( adminKeyFromSession ) ) {
			log.warn( "adminKeyFromQueryString not match adminKeyFromSession session parameter '" + AdminPageConstants.ADMIN_KEY_SESSION_KEY_PARAMETER_NAME + "'");
			response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
			try ( PrintWriter writer = response.getWriter() ) {
				writer.append( "Fail bad request" );
			}
			
			return;
		}
		
		
		try {
			ProcessScanFile_Thread_Container.getSingletonInstance().startIfStopped_ClearStopAfterCurrentFile();

			try ( PrintWriter writer = response.getWriter() ) {
				writer.append( "Starting and Clearing: Stopping Processing files after current file" );
			}
				
		} catch (Throwable e) {
			log.error( "Failed to stop :", e );
			response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR /* 500  */ );
			
			return;
		}
		
	}
}
