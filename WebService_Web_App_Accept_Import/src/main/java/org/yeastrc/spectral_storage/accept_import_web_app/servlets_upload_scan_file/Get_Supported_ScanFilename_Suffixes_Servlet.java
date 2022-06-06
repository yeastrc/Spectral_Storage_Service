package org.yeastrc.spectral_storage.accept_import_web_app.servlets_upload_scan_file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_ScanFilenameSuffix_To_ConverterMapping;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_ScanFilenameSuffix_To_ConverterMapping.ConfigData_ScanFilenameSuffix_To_ConverterMapping_SingleEntry;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.ServetResponseFormatEnum;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileBadRequestToServletException;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileDeserializeRequestException;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.GetRequestObjectFromInputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.Get_ServletResultDataFormat_FromServletInitParam;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.WriteResponseObjectToOutputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.WriteResponseStringToOutputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.Get_Supported_ScanFilename_Suffixes_Request;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.Get_Supported_ScanFilename_Suffixes_Response;

/**
 * This gets the supported Scan Filename Suffixes
 *
 */
public class Get_Supported_ScanFilename_Suffixes_Servlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger( Get_Supported_ScanFilename_Suffixes_Servlet.class );

	private static final long serialVersionUID = 1L;

	private ServetResponseFormatEnum servetResponseFormat;
	
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config)
	          throws ServletException {
		
		super.init(config); //  Must call this first

		servetResponseFormat = 
				Get_ServletResultDataFormat_FromServletInitParam.getInstance()
				.get_ServletResultDataFormat_FromServletInitParam( config );

		log.warn( "INFO: servetResponseFormat: " + servetResponseFormat );
		
	}
	
	//  doGet may not be current, check properties in request object
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
//	@Override
//	protected void doGet(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//
//		Get_Supported_ScanFilename_Suffixes_Request get_UploadedScanFileInfo_Request = new Get_Supported_ScanFilename_Suffixes_Request();
//		
//		get_UploadedScanFileInfo_Request.setScanProcessStatusKey( request.getParameter( "scanProcessStatusKey" ) );
//		
//		processRequest( get_UploadedScanFileInfo_Request, request, response );
//
//	}
	
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Get_Supported_ScanFilename_Suffixes_Request get_UploadedScanFileInfo_Request = null;

		try {
			Object requestObj = null;

			try {
				requestObj = GetRequestObjectFromInputStream.getSingletonInstance().getRequestObjectFromStream( request );
			} catch ( SpectralFileDeserializeRequestException e ) {
				throw e;
			} catch (Exception e) {
				String msg = "Failed to deserialize request";
				log.error( msg, e );
				throw new SpectralFileBadRequestToServletException( e );
			}

			try {
				get_UploadedScanFileInfo_Request = (Get_Supported_ScanFilename_Suffixes_Request) requestObj;
			} catch (Exception e) {
				String msg = "Failed to cast requestObj to Get_Supported_ScanFilename_Suffixes_Request";
				log.error( msg, e );
				throw new SpectralFileBadRequestToServletException( e );
			}
		} catch (SpectralFileBadRequestToServletException e) {

			response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );

			if ( StringUtils.isNotEmpty( e.getMessage() ) ) {
				WriteResponseStringToOutputStream.getInstance()
				.writeResponseStringToOutputStream( e.getMessage(), response);
			}
			
			return;

		} catch (Throwable e) {
			String msg = "Failed to process request";
			log.error( msg, e );
			response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR /* 500  */ );
			
			return;
		}
		
		processRequest( get_UploadedScanFileInfo_Request, request, response );
	}
	
	/**
	 * @param get_UploadedScanFileInfo_Request
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processRequest( 
			Get_Supported_ScanFilename_Suffixes_Request get_UploadedScanFileInfo_Request,
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
	
		try {
			Get_Supported_ScanFilename_Suffixes_Response webserviceResponse = new Get_Supported_ScanFilename_Suffixes_Response();
			
			List<ConfigData_ScanFilenameSuffix_To_ConverterMapping_SingleEntry> scanfilename_suffix_to_converter_base_url_mapping_List = 
					ConfigData_ScanFilenameSuffix_To_ConverterMapping.getSingletonInstance().getEntries();
			
			
			List<String> scanFilenameSuffixes = new ArrayList<>( scanfilename_suffix_to_converter_base_url_mapping_List.size() );

			for ( ConfigData_ScanFilenameSuffix_To_ConverterMapping_SingleEntry entry : scanfilename_suffix_to_converter_base_url_mapping_List ) {
				
				scanFilenameSuffixes.add( entry.getScan_filename_suffix() );
			}
			
			webserviceResponse.setScanFilenameSuffixes(scanFilenameSuffixes);
			
			WriteResponseObjectToOutputStream.getSingletonInstance()
			.writeResponseObjectToOutputStream( webserviceResponse, servetResponseFormat, response );

		} catch (SpectralFileBadRequestToServletException e) {

			response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );

			if ( StringUtils.isNotEmpty( e.getMessage() ) ) {
				WriteResponseStringToOutputStream.getInstance()
				.writeResponseStringToOutputStream( e.getMessage(), response);
			}

		} catch (Throwable e) {
			String msg = "Failed to process request";
			log.error( msg, e );
			response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR /* 500  */ );
		}

	}
}
