package org.yeastrc.spectral_storage.scan_file_parser_web_app_testing_only.servlets_parse_scan_file;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
 * Parse Scan File: Init
 *
 */
public class ParseScanFile_Init_Servlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(ParseScanFile_Init_Servlet.class);

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

			log.info( "webservice_Request.scan_batch_size_maximum: " + webservice_Request.scan_batch_size_maximum );
			
//			if ( true ) {
//				
//
//				Webservice_Response webservice_Response = new Webservice_Response();
//
//				webservice_Response.spectr_minimum_version_supported = 2;
//				webservice_Response.isError = true;
//				webservice_Response.errorMessageCode = "Parse Initialize Failed";
//				webservice_Response.errorMessageToLog = "Parse Initialize Failed: " + webservice_Request.scan_filename_with_path;
//				
//				webservice_Response.errorMessage_ScanFileContentsError_ForEndUser = "Fake Error Message from Parser App On Init";
//
//				jacksonJSON_Mapper.writeValue( response.getOutputStream(), webservice_Response );
//				
//				return;
//			}
			
			{
				File scan_file = new File( webservice_Request.scan_filename_with_path );
				if ( ! scan_file.exists() ) {
					
					log.warn("Scan File to parse cannot be processed. It does NOT exist: " + webservice_Request.scan_filename_with_path );

					Webservice_Response webservice_Response = new Webservice_Response();

					webservice_Response.spectr_minimum_version_supported = 2;
					webservice_Response.isError = true;
					webservice_Response.errorMessageCode = "scan file not exist";
					webservice_Response.errorMessageToLog = "scan file not exist: " + webservice_Request.scan_filename_with_path;

					jacksonJSON_Mapper.writeValue( response.getOutputStream(), webservice_Response );

					return;
				}
				if ( ! scan_file.canRead() ) {
					
					log.warn("Scan File to parse cannot be processed. It CANNOT be read ( scan_file.canRead() returned false ): " + webservice_Request.scan_filename_with_path );

					Webservice_Response webservice_Response = new Webservice_Response();

					webservice_Response.spectr_minimum_version_supported = 2;
					webservice_Response.isError = true;
					webservice_Response.errorMessageCode = "scan file cannot read";
					webservice_Response.errorMessageToLog = "scan file cannot read: " + webservice_Request.scan_filename_with_path;

					jacksonJSON_Mapper.writeValue( response.getOutputStream(), webservice_Response );

					return;
				}
			}
			
			ScanFile_Parsing_InProgress_Item scanFile_Parsing_InProgress_Item = ScanFile_Parsing_InProgress_Item.getNewInstance(webservice_Request.scan_filename_with_path, webservice_Request.scan_batch_size_maximum);
			
			boolean put_Succeeded =
					ScanFile_Parsing_InProgress_Container.get_SingletonInstance().putItem_IfIdentifierNotInMap(webservice_Request.scan_filename_with_path, scanFile_Parsing_InProgress_Item);
			
			if ( ! put_Succeeded ) {

				Webservice_Response webservice_Response = new Webservice_Response();

				webservice_Response.spectr_minimum_version_supported = 2;
				webservice_Response.isError = true;
				webservice_Response.errorMessageCode = "Already processing scan file";
				webservice_Response.errorMessageToLog = "Already processing scan file: " + webservice_Request.scan_filename_with_path;

				jacksonJSON_Mapper.writeValue( response.getOutputStream(), webservice_Response );
				
				return;
			}
			
			try {
				scanFile_Parsing_InProgress_Item.initializeParsing();
			} catch ( Throwable t ) {

				Webservice_Response webservice_Response = new Webservice_Response();

				webservice_Response.spectr_minimum_version_supported = 2;
				webservice_Response.isError = true;
				webservice_Response.errorMessageCode = "Parse Initialize Failed";
				webservice_Response.errorMessageToLog = "Parse Initialize Failed: " + webservice_Request.scan_filename_with_path;

				jacksonJSON_Mapper.writeValue( response.getOutputStream(), webservice_Response );
				
				return;
			}

			Webservice_Response webservice_Response = new Webservice_Response();

			webservice_Response.spectr_minimum_version_supported = 2;
			webservice_Response.converter_identifier_for_scan_file = webservice_Request.scan_filename_with_path;

			jacksonJSON_Mapper.writeValue( response.getOutputStream(), webservice_Response );
			
		} catch ( Throwable t ) {
			log.error( "Exception in Servlet: ", t );
			throw t;
		}
	}
	
	
	public static class Webservice_Request {
		
		private Integer spectr_core_version;
	    private String scan_filename_with_path;
	    private Integer scan_batch_size_maximum;
	    
		public void setSpectr_core_version(Integer spectr_core_version) {
			this.spectr_core_version = spectr_core_version;
		}

		public void setScan_filename_with_path(String scan_filename_with_path) {
			this.scan_filename_with_path = scan_filename_with_path;
		}

		public void setScan_batch_size_maximum(Integer scan_batch_size_maximum) {
			this.scan_batch_size_maximum = scan_batch_size_maximum;
		}
	}

	public static class Webservice_Response {
		
		private String converter_identifier_for_scan_file;
		private Integer spectr_minimum_version_supported; // : <number>,
		private Boolean isError;
		private String errorMessageCode; // : <string>,   -- agreed upon strings like 'filenotfound', 'fileformatincorrect'
		private String errorMessageToLog; // : <string> --  Spectr Core Log Error Message
		private String errorMessage_ScanFileContentsError_ForEndUser;
		
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
		public String getConverter_identifier_for_scan_file() {
			return converter_identifier_for_scan_file;
		}
		public String getErrorMessage_ScanFileContentsError_ForEndUser() {
			return errorMessage_ScanFileContentsError_ForEndUser;
		}
	}
}
