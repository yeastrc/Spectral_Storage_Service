package org.yeastrc.spectral_storage.scan_file_parser_web_app_testing_only.servlets_healthcheck;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.scan_file_parser_web_app_testing_only.servlet_utils.ServletUtil__Read_ServletRequest_Into_ByteArrayOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;

/**
 * Health And Version Check
 *
 */
public class HealthAndVersionCheck_Servlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(HealthAndVersionCheck_Servlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config)
	          throws ServletException {
		
		super.init(config); //  Must call this first

		log.warn( "INFO: init(...) called: ");
		
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.warn( "INFO: doPost(...) called: ");
		
		try {

			ByteArrayOutputStream outputStreamBufferOfClientRequest = 
					ServletUtil__Read_ServletRequest_Into_ByteArrayOutputStream.read_ServletRequest_Into_ByteArrayOutputStream(request);

			//  Jackson JSON Mapper object for JSON deserialization and serialization
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object

			Webservice_Request webservice_Request = null;
			try {
				webservice_Request = jacksonJSON_Mapper.readValue( outputStreamBufferOfClientRequest.toByteArray(), Webservice_Request.class );

			} catch ( Exception e ) {
				log.error( "Failed to parse webservice request. ", e );
				throw e;
			}

			log.info( "webservice_Request.spectr_core_version: " + webservice_Request.spectr_core_version );

			Webservice_Response webservice_Response = new Webservice_Response();

			webservice_Response.spectr_minimum_version_supported = 2;

			jacksonJSON_Mapper.writeValue( response.getOutputStream(), webservice_Response );
			
		} catch ( Throwable t ) {
			log.error( "Exception in Servlet: ", t );
			throw t;
		}
	}
	
	
	public static class Webservice_Request {
		
		private Integer spectr_core_version;

		public void setSpectr_core_version(Integer spectr_core_version) {
			this.spectr_core_version = spectr_core_version;
		}
	}

	public static class Webservice_Response {
		private Integer spectr_minimum_version_supported; // : <number>,
		private Boolean isError;
		private String errorMessageCode; // : <string>,   -- agreed upon strings like 'filenotfound', 'fileformatincorrect'
		private String errorMessageToLog; // : <string> --  Spectr Core Log Error Message
		
		public Integer getSpectr_minimum_version_supported() {
			return spectr_minimum_version_supported;
		}
		public Boolean getIsError() {
			return isError;
		}
		public String getErrorMessageCode() {
			return errorMessageCode;
		}
		public String getErrorMessageToLog() {
			return errorMessageToLog;
		}
	}
}
