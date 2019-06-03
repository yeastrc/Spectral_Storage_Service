package org.yeastrc.spectral_storage.get_data_webapp.servlets_retrieve_data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.get_data_webapp.config.ConfigData_ScanDataLocation_InWorkDirectory;
import org.yeastrc.spectral_storage.get_data_webapp.constants_enums.MaxNumberScansReturnConstants;
import org.yeastrc.spectral_storage.get_data_webapp.constants_enums.ServetResponseFormatEnum;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileBadRequestToServletException;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileDeserializeRequestException;
import org.yeastrc.spectral_storage.get_data_webapp.servlet_response_factories.SingleScan_SubResponse_Factory;
import org.yeastrc.spectral_storage.get_data_webapp.servlet_response_factories.SingleScan_SubResponse_Factory_Parameters;
import org.yeastrc.spectral_storage.get_data_webapp.servlet_response_factories.Single_ScanRetentionTime_ScanNumber_SubResponse_Factory;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.GetRequestObjectFromInputStream;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.Get_ServletResultDataFormat_FromServletInitParam;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.WriteResponseObjectToOutputStream;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.WriteResponseStringToOutputStream;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_ExcludeScansWithoutPeaks;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_ScanFileAPI_Key_NotFound;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScansDataFromRetentionTimeRange_Request;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScansDataFromRetentionTimeRange_Response;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts.SingleScan_SubResponse;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts.Single_ScanRetentionTime_ScanNumber_SubResponse;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataNotFoundException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScan_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_request_results.SpectralFile_Result_RetentionTime_ScanNumber;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader_Factory;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader__IF;

/**
 * Get Scan data for Scans for scanFileAPIKey (scan file hash code) and Retention Time Range
 *
 */
public class GetScansDataFromRetentionTimeRange_Servlet extends HttpServlet {

	private static final Logger log = Logger.getLogger( GetScansDataFromRetentionTimeRange_Servlet.class );

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
//		Get_ScansDataFromRetentionTimeRange_Request get_ScanNumbersFromRetentionTimeRange_Request = new Get_ScansDataFromRetentionTimeRange_Request();
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

		Get_ScansDataFromRetentionTimeRange_Request get_ScanNumbersFromRetentionTimeRange_Request = null;

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
				get_ScanNumbersFromRetentionTimeRange_Request = (Get_ScansDataFromRetentionTimeRange_Request) requestObj;
			} catch (Exception e) {
				String msg = "Failed to cast requestObj to Get_ScansDataFromRetentionTimeRange_Request";
				log.error( msg, e );
				throw new SpectralFileBadRequestToServletException( e );
			}
		} catch (SpectralFileBadRequestToServletException e) {

			response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );

			if ( StringUtils.isNotEmpty( e.getMessage() ) ) {
				WriteResponseStringToOutputStream.getInstance()
				.writeResponseStringToOutputStream( e.getMessage(), response);
			}
			
			return;  // EARLY EXIT

		} catch (Throwable e) {
			String msg = "Failed to process request";
			log.error( msg, e );
			response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR /* 500  */ );
			
			return;  // EARLY EXIT
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
			Get_ScansDataFromRetentionTimeRange_Request get_ScanNumbersFromRetentionTimeRange_Request,
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
	
		try {
			String scanFileAPIKey = get_ScanNumbersFromRetentionTimeRange_Request.getScanFileAPIKey();
			float retentionTimeStart = get_ScanNumbersFromRetentionTimeRange_Request.getRetentionTimeStart();
			float retentionTimeEnd = get_ScanNumbersFromRetentionTimeRange_Request.getRetentionTimeEnd();
			Byte scanLevel = get_ScanNumbersFromRetentionTimeRange_Request.getScanLevel(); // Optional scan level
			
			if ( StringUtils.isEmpty( scanFileAPIKey ) ) {
				String msg = "missing scanFileAPIKey ";
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}

			Get_ScansDataFromRetentionTimeRange_Response webserviceResponse = new Get_ScansDataFromRetentionTimeRange_Response();
			

			File scanStorageBaseDirectoryFile =
					ConfigData_ScanDataLocation_InWorkDirectory.getSingletonInstance()
					.getScanStorageBaseDirectory();
			
			SpectralFile_Reader__IF spectralFile_Reader = null;
			
			try {
				//  null returned if directory does not exist
				spectralFile_Reader = SpectralFile_Reader_Factory.getInstance()
						.getSpectralFile_Reader_ForHash( scanFileAPIKey, scanStorageBaseDirectoryFile );

				if ( spectralFile_Reader == null ) {
					webserviceResponse.setStatus_scanFileAPIKeyNotFound( Get_ScanData_ScanFileAPI_Key_NotFound.YES );
				
				} else {

					List<Integer> scanNumbers = null;

					if ( scanLevel != null ) {
						scanNumbers = spectralFile_Reader.getScanNumbersForRetentionTimeRangeScanLevel( retentionTimeStart, retentionTimeEnd, scanLevel );
					} else {
						scanNumbers = spectralFile_Reader.getScanNumbersForRetentionTimeRange( retentionTimeStart, retentionTimeEnd );
					}

					if ( scanNumbers.size() > MaxNumberScansReturnConstants.MAX_NUMBER_SCANS_RETURN_FOR_IMMEDIATE_WEBSERVICES ) {

						webserviceResponse.setTooManyScansToReturn( true );
						webserviceResponse.setMaxScansToReturn( MaxNumberScansReturnConstants.MAX_NUMBER_SCANS_RETURN_FOR_IMMEDIATE_WEBSERVICES );

						Single_ScanRetentionTime_ScanNumber_SubResponse_Factory single_ScanRetentionTime_ScanNumber_SubResponse_Factory
						= Single_ScanRetentionTime_ScanNumber_SubResponse_Factory.getInstance();

						List<Single_ScanRetentionTime_ScanNumber_SubResponse> scanNumbersRetentionTimes = new ArrayList<>( scanNumbers.size() );

						for ( int scanNumber : scanNumbers ) {
							SpectralFile_Result_RetentionTime_ScanNumber spectralFile_Result_RetentionTime_ScanNumber =
									spectralFile_Reader.getScanRetentionTimeForScanNumber( scanNumber );

							if ( spectralFile_Result_RetentionTime_ScanNumber != null ) {
								Single_ScanRetentionTime_ScanNumber_SubResponse single_ScanRetentionTime_ScanNumber_SubResponse =
										single_ScanRetentionTime_ScanNumber_SubResponse_Factory.buildSingle_ScanRetentionTime_ScanNumber_SubResponse( spectralFile_Result_RetentionTime_ScanNumber );
								scanNumbersRetentionTimes.add( single_ScanRetentionTime_ScanNumber_SubResponse );
							}
						}

						webserviceResponse.setScanNumbersRetentionTimes( scanNumbersRetentionTimes );

						WriteResponseObjectToOutputStream.getSingletonInstance()
						.writeResponseObjectToOutputStream( webserviceResponse, servetResponseFormat, response );

						return; // EARLY RETURN
					}

					SingleScan_SubResponse_Factory singleScan_SubResponse_Factory = SingleScan_SubResponse_Factory.getInstance();

					SingleScan_SubResponse_Factory_Parameters singleScan_SubResponse_Factory_Parameters = new SingleScan_SubResponse_Factory_Parameters();

					singleScan_SubResponse_Factory_Parameters.setMzHighCutoff( get_ScanNumbersFromRetentionTimeRange_Request.getMzHighCutoff() );
					singleScan_SubResponse_Factory_Parameters.setMzLowCutoff( get_ScanNumbersFromRetentionTimeRange_Request.getMzLowCutoff() );

					List<SingleScan_SubResponse> scans = new ArrayList<>( scanNumbers.size() );

					for ( Integer scanNumber : scanNumbers ) {
						SpectralFile_SingleScan_Common spectralFile_SingleScan_Common = 
								spectralFile_Reader.getScanForScanNumber( scanNumber );
						if ( spectralFile_SingleScan_Common != null ) {
							SingleScan_SubResponse singleScan_SubResponse =
									singleScan_SubResponse_Factory
									.buildSingleScan_SubResponse( spectralFile_SingleScan_Common, singleScan_SubResponse_Factory_Parameters );
							if ( get_ScanNumbersFromRetentionTimeRange_Request.getExcludeScansWithoutPeaks() != null 
									&& get_ScanNumbersFromRetentionTimeRange_Request.getExcludeScansWithoutPeaks() == Get_ScanData_ExcludeScansWithoutPeaks.YES ) {
								if ( singleScan_SubResponse.getPeaks() == null || singleScan_SubResponse.getPeaks().isEmpty() ) {
									// No Peaks so skip this scan
									continue; // EARLY CONTINUE
								}
							}
							scans.add( singleScan_SubResponse );
						}
					}
					webserviceResponse.setScans( scans );
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
