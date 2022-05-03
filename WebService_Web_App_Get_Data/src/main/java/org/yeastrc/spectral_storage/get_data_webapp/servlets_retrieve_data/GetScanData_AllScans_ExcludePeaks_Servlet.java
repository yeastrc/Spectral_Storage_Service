package org.yeastrc.spectral_storage.get_data_webapp.servlets_retrieve_data;

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
import org.yeastrc.spectral_storage.get_data_webapp.constants_enums.ServetResponseFormatEnum;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileBadRequestToServletException;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileDeserializeRequestException;
import org.yeastrc.spectral_storage.get_data_webapp.servlet_response_factories.SingleScan_SubResponse_Factory;
import org.yeastrc.spectral_storage.get_data_webapp.servlet_response_factories.SingleScan_SubResponse_Factory_Parameters;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.GetRequestObjectFromInputStream;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.Get_ServletResultDataFormat_FromServletInitParam;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.WriteResponseObjectToOutputStream;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.WriteResponseStringToOutputStream;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_IncludeReturnIonInjectionTimeData;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_IncludeReturnScanLevelTotalIonCurrentData;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_ScanFileAPI_Key_NotFound;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanData_AllScans_ExcludePeaks_Request;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanData_AllScans_ExcludePeaks_Response;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts.SingleScan_SubResponse;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.CommonCore_Get_ScanData_IncludeReturnIonInjectionTimeData_Enum;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.CommonCore_Get_ScanData_IncludeReturnTotalIonCurrentData_Enum;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataNotFoundException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScan_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3_Holder;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader_Factory;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader__IF;

/**
 * Get Scan data for scanFileAPIKey (scan file hash code) and scan numbers
 *
 */
public class GetScanData_AllScans_ExcludePeaks_Servlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger( GetScanData_AllScans_ExcludePeaks_Servlet.class );

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


	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Get_ScanData_AllScans_ExcludePeaks_Request get_ScanData_AllScans_ExcludePeaks_Request = null;

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
					get_ScanData_AllScans_ExcludePeaks_Request = (Get_ScanData_AllScans_ExcludePeaks_Request) requestObj;
				} catch (Exception e) {
					String msg = "Failed to cast requestObj to Get_ScanData_AllScans_ExcludePeaks_Request";
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
					get_ScanData_AllScans_ExcludePeaks_Request = 
							GetRequestObjectFromInputStream.getSingletonInstance().
							getRequestObjectFromStream_RequestFormat_JSON( Get_ScanData_AllScans_ExcludePeaks_Request.class, request );
					
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
		
		

		processRequest( get_ScanData_AllScans_ExcludePeaks_Request, request, response );
	}

	/**
	 * @param get_ScanData_AllScans_ExcludePeaks_Request
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processRequest( 
			Get_ScanData_AllScans_ExcludePeaks_Request get_ScanData_AllScans_ExcludePeaks_Request,
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {

		try {
			String scanFileAPIKey = get_ScanData_AllScans_ExcludePeaks_Request.getScanFileAPIKey();

			Get_ScanData_IncludeReturnIonInjectionTimeData includeReturnIonInjectionTimeData =
					get_ScanData_AllScans_ExcludePeaks_Request.getIncludeReturnIonInjectionTimeData();
			Get_ScanData_IncludeReturnScanLevelTotalIonCurrentData includeReturnScanLevelTotalIonCurrentData =
					get_ScanData_AllScans_ExcludePeaks_Request.getIncludeReturnScanLevelTotalIonCurrentData();

			if ( StringUtils.isEmpty( scanFileAPIKey ) ) {
				String msg = "missing scanFileAPIKey ";
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}

			Get_ScanData_AllScans_ExcludePeaks_Response webserviceResponse = new Get_ScanData_AllScans_ExcludePeaks_Response();

			SpectralFile_Reader__IF spectralFile_Reader = null;

			try {
				CommonReader_File_And_S3 commonReader_File_And_S3 = CommonReader_File_And_S3_Holder.getSingletonInstance().getCommonReader_File_And_S3();

				//  SpectralStorageDataNotFoundException thrown if Data File (and complete) does not exist
				spectralFile_Reader = SpectralFile_Reader_Factory.getInstance()
						.getSpectralFile_Reader_ForHash( scanFileAPIKey, commonReader_File_And_S3 );

				if ( spectralFile_Reader == null ) {
					webserviceResponse.setStatus_scanFileAPIKeyNotFound( Get_ScanData_ScanFileAPI_Key_NotFound.YES );

				} else {

					List<SingleScan_SubResponse> scans = getScanData( 
							includeReturnIonInjectionTimeData,
							includeReturnScanLevelTotalIonCurrentData,
							spectralFile_Reader );

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
			.writeResponseObjectToOutputStream( webserviceResponse, this.getClass(), servetResponseFormat, response );

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

	/**
	 * @param includeReturnIonInjectionTimeData
	 * @param includeReturnScanLevelTotalIonCurrentData
	 * @param spectralFile_Reader
	 * @return
	 * @throws Exception
	 */
	private List<SingleScan_SubResponse> getScanData( 

			Get_ScanData_IncludeReturnIonInjectionTimeData includeReturnIonInjectionTimeData,
			Get_ScanData_IncludeReturnScanLevelTotalIonCurrentData includeReturnScanLevelTotalIonCurrentData,
			SpectralFile_Reader__IF spectralFile_Reader ) throws Exception {


		CommonCore_Get_ScanData_IncludeReturnIonInjectionTimeData_Enum commonCore_Get_ScanData_IncludeReturnIonInjectionTimeData_Enum =
				CommonCore_Get_ScanData_IncludeReturnIonInjectionTimeData_Enum.NO;

		if ( includeReturnIonInjectionTimeData == Get_ScanData_IncludeReturnIonInjectionTimeData.YES ) {
			commonCore_Get_ScanData_IncludeReturnIonInjectionTimeData_Enum = CommonCore_Get_ScanData_IncludeReturnIonInjectionTimeData_Enum.YES;
		}

		CommonCore_Get_ScanData_IncludeReturnTotalIonCurrentData_Enum commonCore_Get_ScanData_IncludeReturnTotalIonCurrentData_Enum =
				CommonCore_Get_ScanData_IncludeReturnTotalIonCurrentData_Enum.NO;

		if ( includeReturnScanLevelTotalIonCurrentData == Get_ScanData_IncludeReturnScanLevelTotalIonCurrentData.YES ) {
			commonCore_Get_ScanData_IncludeReturnTotalIonCurrentData_Enum = CommonCore_Get_ScanData_IncludeReturnTotalIonCurrentData_Enum.YES;
		}

		SingleScan_SubResponse_Factory singleScan_SubResponse_Factory = SingleScan_SubResponse_Factory.getInstance();

		SingleScan_SubResponse_Factory_Parameters singleScan_SubResponse_Factory_Parameters = new SingleScan_SubResponse_Factory_Parameters();

		List<Integer> scanNumbers =
				spectralFile_Reader.getScanNumbersForScanLevelsToIncludeScanLevelsToExclude( null /* scanLevelsToInclude */, null /* scanLevelsToExclude */ );
		
//		log.warn( "scanNumbers.size(): " + scanNumbers.size() );

		List<SingleScan_SubResponse> scans = new ArrayList<>( scanNumbers.size() );
		
//		int counter = 0;

		for ( Integer scanNumber : scanNumbers ) {
			
//			counter++;
//			
//			if ( counter % 5000 == 0 ) {
//				log.warn( "counter: " + counter );
//			}
			SingleScan_SubResponse singleScan_SubResponse = getScanData_SingleScan_SubResponse(scanNumber, commonCore_Get_ScanData_IncludeReturnIonInjectionTimeData_Enum, commonCore_Get_ScanData_IncludeReturnTotalIonCurrentData_Enum, spectralFile_Reader, singleScan_SubResponse_Factory, singleScan_SubResponse_Factory_Parameters);
			if ( singleScan_SubResponse != null ) {
				scans.add( singleScan_SubResponse );
			}
		}
		
		return scans;
	}
	

	/**
	 * @param includeReturnIonInjectionTimeData
	 * @param includeReturnScanLevelTotalIonCurrentData
	 * @param spectralFile_Reader
	 * @return
	 * @throws Exception
	 */
	private SingleScan_SubResponse getScanData_SingleScan_SubResponse( 
			int scanNumber,
			CommonCore_Get_ScanData_IncludeReturnIonInjectionTimeData_Enum commonCore_Get_ScanData_IncludeReturnIonInjectionTimeData_Enum,
			CommonCore_Get_ScanData_IncludeReturnTotalIonCurrentData_Enum commonCore_Get_ScanData_IncludeReturnTotalIonCurrentData_Enum,
			SpectralFile_Reader__IF spectralFile_Reader,
			SingleScan_SubResponse_Factory singleScan_SubResponse_Factory,
			SingleScan_SubResponse_Factory_Parameters singleScan_SubResponse_Factory_Parameters
			) throws Exception {


		//  Parameter commonCore_Get_ScanData_IncludeReturnIonInjectionTimeData_Enum 
		//  will be ignored in version 003 of Data File since NO Ion Inject Time is stored in version 003 of Data File

		//  Parameter commonCore_Get_ScanData_IncludeReturnIonInjectionTimeData_Enum
		//  will be ignored if there is no value stored for Ion Injection Time

		SpectralFile_SingleScan_Common spectralFile_SingleScan_Common = 
				spectralFile_Reader.getScanDataNoScanPeaksForScanNumber( 
						scanNumber, 
						commonCore_Get_ScanData_IncludeReturnIonInjectionTimeData_Enum,
						commonCore_Get_ScanData_IncludeReturnTotalIonCurrentData_Enum );

		if ( spectralFile_SingleScan_Common == null ) {
			
			return null;  // EARLY RETURN
		}

		SingleScan_SubResponse singleScan_SubResponse = 
				singleScan_SubResponse_Factory
				.buildSingleScan_SubResponse( spectralFile_SingleScan_Common, singleScan_SubResponse_Factory_Parameters );
		
		return singleScan_SubResponse;
	}

}
