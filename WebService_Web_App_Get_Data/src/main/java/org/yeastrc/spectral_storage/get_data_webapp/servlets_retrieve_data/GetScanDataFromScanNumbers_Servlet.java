package org.yeastrc.spectral_storage.get_data_webapp.servlets_retrieve_data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.get_data_webapp.constants_enums.MaxNumberScansReturnConstants;
import org.yeastrc.spectral_storage.get_data_webapp.constants_enums.ServetResponseFormatEnum;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileBadRequestToServletException;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileDeserializeRequestException;
import org.yeastrc.spectral_storage.get_data_webapp.servlet_response_factories.SingleScan_SubResponse_Factory;
import org.yeastrc.spectral_storage.get_data_webapp.servlet_response_factories.SingleScan_SubResponse_Factory_Parameters;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.GetRequestObjectFromInputStream;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.Get_ServletResultDataFormat_FromServletInitParam;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.WriteResponseObjectToOutputStream;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.WriteResponseStringToOutputStream;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanDataFromScanNumbers_IncludeParentScans;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_ExcludeReturnScanPeakData;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_IncludeReturnIonInjectionTimeData;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_IncludeReturnScanLevelTotalIonCurrentData;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_ScanFileAPI_Key_NotFound;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanDataFromScanNumbers_Request;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanDataFromScanNumbers_Response;
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
public class GetScanDataFromScanNumbers_Servlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger( GetScanDataFromScanNumbers_Servlet.class );

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

		Get_ScanDataFromScanNumbers_Request get_ScanDataFromScanNumbers_Request = null;

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
				get_ScanDataFromScanNumbers_Request = (Get_ScanDataFromScanNumbers_Request) requestObj;
			} catch (Exception e) {
				String msg = "Failed to cast requestObj to Get_ScanDataFromScanNumbers_Request";
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
		
		processRequest( get_ScanDataFromScanNumbers_Request, request, response );
	}
	
	/**
	 * @param get_ScanDataFromScanNumbers_Request
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processRequest( 
			Get_ScanDataFromScanNumbers_Request get_ScanDataFromScanNumbers_Request,
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
	
		try {
			String scanFileAPIKey = get_ScanDataFromScanNumbers_Request.getScanFileAPIKey();
			List<Integer> scanNumbers = get_ScanDataFromScanNumbers_Request.getScanNumbers();
			Get_ScanDataFromScanNumbers_IncludeParentScans includeParentScans =
					get_ScanDataFromScanNumbers_Request.getIncludeParentScans();
			Get_ScanData_ExcludeReturnScanPeakData excludeReturnScanPeakData =
					get_ScanDataFromScanNumbers_Request.getExcludeReturnScanPeakData();
			Get_ScanData_IncludeReturnIonInjectionTimeData includeReturnIonInjectionTimeData =
					get_ScanDataFromScanNumbers_Request.getIncludeReturnIonInjectionTimeData();
			Get_ScanData_IncludeReturnScanLevelTotalIonCurrentData includeReturnScanLevelTotalIonCurrentData =
					get_ScanDataFromScanNumbers_Request.getIncludeReturnScanLevelTotalIonCurrentData();

			if ( StringUtils.isEmpty( scanFileAPIKey ) ) {
				String msg = "missing scanFileAPIKey ";
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}

			if ( scanNumbers == null || scanNumbers.isEmpty() ) {
				String msg = "missing scanNumbers or scanNumbers is empty ";
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}

			Get_ScanDataFromScanNumbers_Response webserviceResponse = new Get_ScanDataFromScanNumbers_Response();

			if ( excludeReturnScanPeakData != null 
					&& excludeReturnScanPeakData == Get_ScanData_ExcludeReturnScanPeakData.YES ) {
				
			} else {
				//  Only apply max number of scans for when returning scan peaks
				if ( scanNumbers.size() > MaxNumberScansReturnConstants.MAX_NUMBER_SCANS_RETURN_FOR_IMMEDIATE_WEBSERVICES ) {

					webserviceResponse.setTooManyScansToReturn( true );
					webserviceResponse.setMaxScansToReturn( MaxNumberScansReturnConstants.MAX_NUMBER_SCANS_RETURN_FOR_IMMEDIATE_WEBSERVICES );

					WriteResponseObjectToOutputStream.getSingletonInstance()
					.writeResponseObjectToOutputStream( webserviceResponse, servetResponseFormat, response );

					return; // EARLY RETURN
				}
			}
			
			SpectralFile_Reader__IF spectralFile_Reader = null;
			
			try {
				CommonReader_File_And_S3 commonReader_File_And_S3 = CommonReader_File_And_S3_Holder.getSingletonInstance().getCommonReader_File_And_S3();
				
				//  SpectralStorageDataNotFoundException thrown if Data File (and complete) does not exist
				spectralFile_Reader = SpectralFile_Reader_Factory.getInstance()
						.getSpectralFile_Reader_ForHash( scanFileAPIKey, commonReader_File_And_S3 );

				if ( spectralFile_Reader == null ) {
					webserviceResponse.setStatus_scanFileAPIKeyNotFound( Get_ScanData_ScanFileAPI_Key_NotFound.YES );
				
				} else {

					Byte maxScanLevelFound = null;

					if ( includeParentScans != null
							&& ( includeParentScans == 
							Get_ScanDataFromScanNumbers_IncludeParentScans.IMMEDIATE_PARENT 
							|| includeParentScans == 
							Get_ScanDataFromScanNumbers_IncludeParentScans.ALL_PARENTS ) ) {

						for ( Integer scanNumber : scanNumbers ) {
							Byte scanLevel = spectralFile_Reader.getScanLevelForScanNumber( scanNumber );

							if ( scanLevel != null ) {
								if ( maxScanLevelFound == null ) {
									maxScanLevelFound = scanLevel;
								} else {
									if ( scanLevel > maxScanLevelFound ) {
										maxScanLevelFound = scanLevel;
									}
								}
							}
						}

						int allowedMaxScanNumbers = MaxNumberScansReturnConstants.MAX_NUMBER_SCANS_RETURN_FOR_IMMEDIATE_WEBSERVICES;

						if ( includeParentScans == 
								Get_ScanDataFromScanNumbers_IncludeParentScans.IMMEDIATE_PARENT ) {

							allowedMaxScanNumbers = allowedMaxScanNumbers / 2;  // Scan and parent
						} else {
							allowedMaxScanNumbers = allowedMaxScanNumbers / maxScanLevelFound;
						}

						if ( scanNumbers.size() > allowedMaxScanNumbers ) {

							webserviceResponse.setTooManyScansToReturn( true );
							webserviceResponse.setMaxScansToReturn( allowedMaxScanNumbers );
							webserviceResponse.setMaxScanLevelFound( maxScanLevelFound );

							WriteResponseObjectToOutputStream.getSingletonInstance()
							.writeResponseObjectToOutputStream( webserviceResponse, servetResponseFormat, response );

							return; // EARLY RETURN
						}
					}

					int returnedScanListInitialSize = scanNumbers.size();

					if ( maxScanLevelFound != null ) {
						returnedScanListInitialSize *= maxScanLevelFound;
					}

					SingleScan_SubResponse_Factory singleScan_SubResponse_Factory = SingleScan_SubResponse_Factory.getInstance();

					SingleScan_SubResponse_Factory_Parameters singleScan_SubResponse_Factory_Parameters = new SingleScan_SubResponse_Factory_Parameters();

					singleScan_SubResponse_Factory_Parameters.setMzHighCutoff( get_ScanDataFromScanNumbers_Request.getMzHighCutoff() );
					singleScan_SubResponse_Factory_Parameters.setMzLowCutoff( get_ScanDataFromScanNumbers_Request.getMzLowCutoff() );

					List<SingleScan_SubResponse> scans = new ArrayList<>( returnedScanListInitialSize );

					Set<Integer> insertedScansScanNumbers = new HashSet<>();

					for ( Integer scanNumber : scanNumbers ) {
						processScanNumber( 
								1, // recursionLevel
								scanNumber, 
								includeParentScans,
								excludeReturnScanPeakData,
								includeReturnIonInjectionTimeData,
								includeReturnScanLevelTotalIonCurrentData,
								scans, 
								insertedScansScanNumbers, 
								spectralFile_Reader,
								singleScan_SubResponse_Factory,
								singleScan_SubResponse_Factory_Parameters );
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
	
	/**
	 * @param recursionLevel
	 * @param scanNumber
	 * @param includeParentScans
	 * @param scans
	 * @param insertedScansScanNumbers
	 * @param spectralFile_Reader
	 * @param singleScan_SubResponse_Factory
	 * @throws Exception
	 */
	private void processScanNumber( 
			int recursionLevel, // Starts at 1
			Integer scanNumber, 
			Get_ScanDataFromScanNumbers_IncludeParentScans includeParentScans,
			Get_ScanData_ExcludeReturnScanPeakData excludeReturnScanPeakData,
			Get_ScanData_IncludeReturnIonInjectionTimeData includeReturnIonInjectionTimeData,
			Get_ScanData_IncludeReturnScanLevelTotalIonCurrentData includeReturnScanLevelTotalIonCurrentData,
			List<SingleScan_SubResponse> scans,
			Set<Integer> insertedScansScanNumbers,
			SpectralFile_Reader__IF spectralFile_Reader,
			SingleScan_SubResponse_Factory singleScan_SubResponse_Factory,
			SingleScan_SubResponse_Factory_Parameters singleScan_SubResponse_Factory_Parameters ) throws Exception {
		
		if ( insertedScansScanNumbers.contains( scanNumber ) ) {
			// exit since already processed this scan number
			return;
		}
		
		SpectralFile_SingleScan_Common spectralFile_SingleScan_Common = null;
		
		if ( excludeReturnScanPeakData != null
				&& excludeReturnScanPeakData == Get_ScanData_ExcludeReturnScanPeakData.YES ) {
			//  Get scan WITHOUT scan peak data
			
//			Get_ScanData_IncludeReturnIonInjectionTimeData includeReturnIonInjectionTimeData
			
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
				
			
			//  Parameter commonCore_Get_ScanData_IncludeReturnIonInjectionTimeData_Enum 
			//  will be ignored in version 003 of Data File since NO Ion Inject Time is stored in version 003 of Data File
			
			//  Parameter commonCore_Get_ScanData_IncludeReturnIonInjectionTimeData_Enum
			//  will be ignored if there is no value stored for Ion Injection Time
			
			spectralFile_SingleScan_Common = 
					spectralFile_Reader.getScanDataNoScanPeaksForScanNumber( 
							scanNumber, 
							commonCore_Get_ScanData_IncludeReturnIonInjectionTimeData_Enum,
							commonCore_Get_ScanData_IncludeReturnTotalIonCurrentData_Enum );
			
		} else {
			//  Get scan WITH scan peak data
			spectralFile_SingleScan_Common = 
					spectralFile_Reader.getScanForScanNumber( scanNumber );
		}		
		
		SingleScan_SubResponse singleScan_SubResponse = null;
		if ( spectralFile_SingleScan_Common != null ) {
			singleScan_SubResponse = 
					singleScan_SubResponse_Factory
					.buildSingleScan_SubResponse( spectralFile_SingleScan_Common, singleScan_SubResponse_Factory_Parameters );
			scans.add( singleScan_SubResponse );
			insertedScansScanNumbers.add( scanNumber );
			
			if ( includeParentScans != null ) { 
				if ( includeParentScans == Get_ScanDataFromScanNumbers_IncludeParentScans.IMMEDIATE_PARENT 
						&& recursionLevel == 1 ) {
					//  Retrieve next level scans for value IMMEDIATE_PARENT
					Integer parentScanNumber = singleScan_SubResponse.getParentScanNumber();
					if ( parentScanNumber == null ) {
						
					}
					if ( parentScanNumber != null ) {
						int recursionLevelNextCall = recursionLevel + 1;
						processScanNumber( 
								recursionLevelNextCall, // recursionLevel
								parentScanNumber, 
								includeParentScans,
								excludeReturnScanPeakData,
								includeReturnIonInjectionTimeData,
								includeReturnScanLevelTotalIonCurrentData,
								scans, 
								insertedScansScanNumbers, 
								spectralFile_Reader,
								singleScan_SubResponse_Factory,
								singleScan_SubResponse_Factory_Parameters );
					}
				}
				if ( includeParentScans == Get_ScanDataFromScanNumbers_IncludeParentScans.ALL_PARENTS 
						&& singleScan_SubResponse.getLevel() > 1 ) {
					Integer parentScanNumber = singleScan_SubResponse.getParentScanNumber();
					if ( parentScanNumber == null ) {
						
					}
					if ( parentScanNumber != null ) {
						int recursionLevelNextCall = recursionLevel++;
						processScanNumber( 
								recursionLevelNextCall, // recursionLevel
								parentScanNumber, 
								includeParentScans,
								excludeReturnScanPeakData,
								includeReturnIonInjectionTimeData,
								includeReturnScanLevelTotalIonCurrentData,
								scans, 
								insertedScansScanNumbers, 
								spectralFile_Reader,
								singleScan_SubResponse_Factory,
								singleScan_SubResponse_Factory_Parameters );
					}
					
				}

			}
		}

	}

}
