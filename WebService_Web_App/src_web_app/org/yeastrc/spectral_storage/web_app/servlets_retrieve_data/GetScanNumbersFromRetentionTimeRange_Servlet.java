package org.yeastrc.spectral_storage.web_app.servlets_retrieve_data;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.Get_ScanNumbersFromRetentionTimeRange_Request;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.Get_ScanNumbersFromRetentionTimeRange_Response;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader_Factory;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader__IF;
import org.yeastrc.spectral_storage.web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.web_app.constants_enums.ServetResponseFormatEnum;
import org.yeastrc.spectral_storage.web_app.exceptions.SpectralFileBadRequestToServletException;
import org.yeastrc.spectral_storage.web_app.exceptions.SpectralFileDeserializeRequestException;
import org.yeastrc.spectral_storage.web_app.servlets_common.GetRequestObjectFromInputStream;
import org.yeastrc.spectral_storage.web_app.servlets_common.Get_ServletResultDataFormat_FromServletInitParam;
import org.yeastrc.spectral_storage.web_app.servlets_common.WriteResponseObjectToOutputStream;
import org.yeastrc.spectral_storage.web_app.servlets_common.WriteResponseStringToOutputStream;

/**
 * Get Scan Numbers for scanFileAPIKey (scan file hash code) and Retention Time Range
 *
 */
public class GetScanNumbersFromRetentionTimeRange_Servlet extends HttpServlet {

	private static final Logger log = Logger.getLogger( GetScanNumbersFromRetentionTimeRange_Servlet.class );

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
//		Get_ScanNumbersFromRetentionTimeRange_Request get_ScanNumbersFromRetentionTimeRange_Request = new Get_ScanNumbersFromRetentionTimeRange_Request();
//
//		try {
//			get_ScanNumbersFromRetentionTimeRange_Request.setScanFileAPIKey( request.getParameter( "scanFileAPIKey" ) );
//
//			String retentionTimeStartString = request.getParameter( "retentionTimeStart" );
//			String retentionTimeEndString = request.getParameter( "retentionTimeEnd" );
//
//			if ( StringUtils.isEmpty( retentionTimeStartString ) ) {
//				String msg = "request is missing retentionTimeStart ";
//				log.warn( msg );
//				throw new SpectralFileBadRequestToServletException( msg );
//			}
//
//			if ( StringUtils.isEmpty( retentionTimeEndString ) ) {
//				String msg = "request is missing retentionTimeEnd ";
//				log.warn( msg );
//				throw new SpectralFileBadRequestToServletException( msg );
//			}
//
//			try {
//				get_ScanNumbersFromRetentionTimeRange_Request.setRetentionTimeStart( Float.parseFloat( retentionTimeStartString ) );
//			} catch ( Exception e ) {
//				String msg = "request retentionTimeStart is not an float.";
//				log.warn( msg );
//				throw new SpectralFileBadRequestToServletException( msg );
//			}
//
//			try {
//				get_ScanNumbersFromRetentionTimeRange_Request.setRetentionTimeEnd( Float.parseFloat( retentionTimeEndString ) );
//			} catch ( Exception e ) {
//				String msg = "request retentionTimeEnd is not an float.";
//				log.warn( msg );
//				throw new SpectralFileBadRequestToServletException( msg );
//			}
//			
//		} catch (SpectralFileBadRequestToServletException e) {
//			response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
//			if ( StringUtils.isNotEmpty( e.getMessage() ) ) {
//				WriteResponseStringToOutputStream.getInstance()
//				.writeResponseStringToOutputStream( e.getMessage(), response);
//			}
//	
//			return;
//	
//		} catch (Throwable e) {
//			String msg = "Failed to process request";
//			log.error( msg, e );
//			response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR /* 500  */ );
//	
//			return;
//		}
//		
//		processRequest( get_ScanNumbersFromRetentionTimeRange_Request, request, response );
//
//	}
	
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Get_ScanNumbersFromRetentionTimeRange_Request get_ScanNumbersFromRetentionTimeRange_Request = null;

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
				get_ScanNumbersFromRetentionTimeRange_Request = (Get_ScanNumbersFromRetentionTimeRange_Request) requestObj;
			} catch (Exception e) {
				String msg = "Failed to cast requestObj to Get_ScanNumbersFromRetentionTimeRange_Request";
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
		
		processRequest( get_ScanNumbersFromRetentionTimeRange_Request, request, response );
	}
	
	/**
	 * @param get_ScanNumbersFromRetentionTimeRange_Request
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processRequest( 
			Get_ScanNumbersFromRetentionTimeRange_Request get_ScanNumbersFromRetentionTimeRange_Request,
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
	
		try {
			String scanFileAPIKey = get_ScanNumbersFromRetentionTimeRange_Request.getScanFileAPIKey();
			Float retentionTimeStart = get_ScanNumbersFromRetentionTimeRange_Request.getRetentionTimeStart();
			Float retentionTimeEnd = get_ScanNumbersFromRetentionTimeRange_Request.getRetentionTimeEnd();
			
			if ( StringUtils.isEmpty( scanFileAPIKey ) ) {
				String msg = "missing scanFileAPIKey ";
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}

			if ( retentionTimeStart == null ) {
				String msg = "missing retentionTimeStart ";
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}

			if ( retentionTimeEnd == null ) {
				String msg = "missing retentionTimeEnd ";
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}


			Get_ScanNumbersFromRetentionTimeRange_Response webserviceResponse = new Get_ScanNumbersFromRetentionTimeRange_Response();
			

			File scanStorageBaseDirectoryFile =
					ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance()
					.getScanStorageBaseDirectory();
			
			SpectralFile_Reader__IF spectralFile_Reader = null;
			
			try {
				spectralFile_Reader = SpectralFile_Reader_Factory.getInstance()
						.getSpectralFile_Writer_ForHash( scanFileAPIKey, scanStorageBaseDirectoryFile );

				List<Integer> scanNumbers =
						spectralFile_Reader.getScanNumbersForRetentionTimeRange( retentionTimeStart, retentionTimeEnd );

				webserviceResponse.setScanNumbers( scanNumbers );
			} finally {
				if ( spectralFile_Reader != null ) {
					spectralFile_Reader.close();
				}
			}
			
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
