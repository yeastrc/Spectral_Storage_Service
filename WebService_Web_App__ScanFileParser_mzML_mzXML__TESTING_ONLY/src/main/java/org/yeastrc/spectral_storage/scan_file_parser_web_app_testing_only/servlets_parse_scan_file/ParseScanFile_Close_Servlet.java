package org.yeastrc.spectral_storage.scan_file_parser_web_app_testing_only.servlets_parse_scan_file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.scan_file_parser_web_app_testing_only.scan_parsing_in_progress.ScanFile_Parsing_InProgress_Container;
import org.yeastrc.spectral_storage.scan_file_parser_web_app_testing_only.scan_parsing_in_progress.ScanFile_Parsing_InProgress_Item;
import org.yeastrc.spectral_storage.scan_file_parser_web_app_testing_only.servlet_utils.ServletUtil__Read_ServletRequest_Into_ByteArrayOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;

/**
 * Parse Scan File: Close
 *
 */
public class ParseScanFile_Close_Servlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(ParseScanFile_Close_Servlet.class);

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

			log.info( "webservice_Request.scan_filename_with_path: " + webservice_Request.scan_filename_with_path );

			log.info( "webservice_Request.converter_identifier_for_scan_file: " + webservice_Request.converter_identifier_for_scan_file );

			log.info( "webservice_Request.last_scan_batch_number_received: " + webservice_Request.last_scan_batch_number_received );
			
			log.warn( "Need to validate webservice_Request.last_scan_batch_number_received" );
			
//			if ( true ) {   //  Error returned from 'Close' is currently ignored
//				
//
//				Webservice_Response webservice_Response = new Webservice_Response();
//
//				webservice_Response.isError = true;
//				webservice_Response.errorMessageCode = "Close ScanFile Failed";
//				webservice_Response.errorMessageToLog = "Close ScanFile Failed: " + webservice_Request.scan_filename_with_path;
//				
//				webservice_Response.errorMessage_ScanFileContentsError_ForEndUser = "Fake Error Message from Parser App On Close ScanFile";
//				
//				jacksonJSON_Mapper.writeValue( response.getOutputStream(), webservice_Response );
//				
//				return;
//			}
			
			
			Webservice_Response webservice_Response = new Webservice_Response();

			ScanFile_Parsing_InProgress_Item scanFile_Parsing_InProgress_Item =
					ScanFile_Parsing_InProgress_Container.get_SingletonInstance().getItem(webservice_Request.converter_identifier_for_scan_file);
			
			if ( scanFile_Parsing_InProgress_Item != null) {
				
				//  TODO:  complete this 'if' 
//				if ( webservice_Request.previous_scan_batch_number.intValue() != scanFile_Parsing_InProgress_Item. ) {
//					
//					
//				}

				try {
					scanFile_Parsing_InProgress_Item.close();
					
					log.info( "Close of Scan File Parser: Found and Successful: webservice_Request.converter_identifier_for_scan_file: " + webservice_Request.converter_identifier_for_scan_file );
					
				} catch ( Throwable t ) {
					
					log.error( "Close of Scan File Parser: Found but NOT successful: webservice_Request.converter_identifier_for_scan_file: " + webservice_Request.converter_identifier_for_scan_file, t );
					
					//  Eat Exception
				} finally {
					ScanFile_Parsing_InProgress_Container.get_SingletonInstance().removeItem(webservice_Request.converter_identifier_for_scan_file);
				}
			} else {
				log.warn( "Close requested for converter_identifier_for_scan_file that is not active: " + webservice_Request.converter_identifier_for_scan_file );
				
				webservice_Response.isError = true;
				webservice_Response.errorMessageCode = "converter_identifier_for_scan_file not found";
				webservice_Response.errorMessageToLog = "converter_identifier_for_scan_file not found: webservice_Request.converter_identifier_for_scan_file";
			}
			
			jacksonJSON_Mapper.writeValue( response.getOutputStream(), webservice_Response );
			
		} catch ( Throwable t ) {
			log.error( "Exception in Servlet: ", t );
			throw t;
		}
	}
	
	
	public static class Webservice_Request {
		
		private Integer spectr_core_version;
	    private String scan_filename_with_path;
		private String converter_identifier_for_scan_file;
		private Integer last_scan_batch_number_received;
	    
		public void setSpectr_core_version(Integer spectr_core_version) {
			this.spectr_core_version = spectr_core_version;
		}
		public void setScan_filename_with_path(String scan_filename_with_path) {
			this.scan_filename_with_path = scan_filename_with_path;
		}
		public void setConverter_identifier_for_scan_file(String converter_identifier_for_scan_file) {
			this.converter_identifier_for_scan_file = converter_identifier_for_scan_file;
		}
		public void setLast_scan_batch_number_received(Integer last_scan_batch_number_received) {
			this.last_scan_batch_number_received = last_scan_batch_number_received;
		}
	}

	public static class Webservice_Response {
		
		private Boolean isError;
		private String errorMessageCode; // : <string>,   -- agreed upon strings like 'filenotfound', 'fileformatincorrect'
		private String errorMessageToLog; // : <string> --  Spectr Core Log Error Message
		private String errorMessage_ScanFileContentsError_ForEndUser;

		public Boolean getIsError() {
			return isError;
		}
		public String getErrorMessageCode() {
			return errorMessageCode;
		}
		public String getErrorMessageToLog() {
			return errorMessageToLog;
		}
		public String getErrorMessage_ScanFileContentsError_ForEndUser() {
			return errorMessage_ScanFileContentsError_ForEndUser;
		}
	}
}
