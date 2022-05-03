package org.yeastrc.spectral_storage.get_data_webapp.servlets_retrieve_data;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.get_data_webapp.constants_enums.ServetResponseFormatEnum;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileBadRequestToServletException;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileDeserializeRequestException;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.GetRequestObjectFromInputStream;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.Get_ServletResultDataFormat_FromServletInitParam;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.WriteResponseObjectToOutputStream;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.WriteResponseStringToOutputStream;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_ScanFileAPI_Key_NotFound;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanNumbersFromRetentionTimeRange_Request;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanNumbersFromRetentionTimeRange_Response;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataNotFoundException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3_Holder;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader_Factory;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader__IF;

/**
 * Get Scan Numbers for scanFileAPIKey (scan file hash code) and Retention Time Range
 *
 */
public class GetScanNumbersFromRetentionTimeRange_Servlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger( GetScanNumbersFromRetentionTimeRange_Servlet.class );

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

		if ( servetResponseFormat == ServetResponseFormatEnum.XML  ) {
			try {
				Object requestObj = null;

				try {
					requestObj = GetRequestObjectFromInputStream.getSingletonInstance().getRequestObjectFromStream_RequestFormat_XML( request );
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
		} else if ( servetResponseFormat == ServetResponseFormatEnum.JSON  ) {
			try {
				try {
					get_ScanNumbersFromRetentionTimeRange_Request = 
							GetRequestObjectFromInputStream.getSingletonInstance().
							getRequestObjectFromStream_RequestFormat_JSON( Get_ScanNumbersFromRetentionTimeRange_Request.class, request );
					
				} catch ( SpectralFileDeserializeRequestException e ) {
					throw e;
				} catch (Exception e) {
					String msg = "Failed to deserialize request";
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
		} else {
			String msg = "Failed to process request. unknown value for servetResponseFormat: " + servetResponseFormat;
			log.error( msg );
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
			
			SpectralFile_Reader__IF spectralFile_Reader = null;
			
			try {
				CommonReader_File_And_S3 commonReader_File_And_S3 = CommonReader_File_And_S3_Holder.getSingletonInstance().getCommonReader_File_And_S3();
				
				//  SpectralStorageDataNotFoundException thrown if Data File (and complete) does not exist
				spectralFile_Reader = SpectralFile_Reader_Factory.getInstance()
						.getSpectralFile_Reader_ForHash( scanFileAPIKey, commonReader_File_And_S3 );

				if ( spectralFile_Reader == null ) {
					webserviceResponse.setStatus_scanFileAPIKeyNotFound( Get_ScanData_ScanFileAPI_Key_NotFound.YES );
				
				} else {

					List<Integer> scanNumbers =
							spectralFile_Reader.getScanNumbersForRetentionTimeRange( retentionTimeStart, retentionTimeEnd );

					webserviceResponse.setScanNumbers( scanNumbers );
				}

			} catch ( SpectralStorageDataNotFoundException e ) {
				
				webserviceResponse.setStatus_scanFileAPIKeyNotFound( Get_ScanData_ScanFileAPI_Key_NotFound.YES );
				
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
