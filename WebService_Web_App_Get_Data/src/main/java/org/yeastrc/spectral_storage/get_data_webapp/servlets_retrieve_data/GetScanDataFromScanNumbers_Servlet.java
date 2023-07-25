package org.yeastrc.spectral_storage.get_data_webapp.servlets_retrieve_data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.get_data_webapp.config.ConfigData_ScanDataLocation_InWorkDirectory;
import org.yeastrc.spectral_storage.get_data_webapp.constants_enums.MaxNumberScansReturnConstants;
import org.yeastrc.spectral_storage.get_data_webapp.constants_enums.ServetResponseFormatEnum;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileBadRequestToServletException;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileDeserializeRequestException;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileWebappInternalRuntimeException;
import org.yeastrc.spectral_storage.get_data_webapp.servlet_response_factories.SingleScan_SubResponse_Factory;
import org.yeastrc.spectral_storage.get_data_webapp.servlet_response_factories.SingleScan_SubResponse_Factory_Parameters;
import org.yeastrc.spectral_storage.get_data_webapp.servlet_response_factories.SingleScan_SubResponse_Factory_Parameters.SingleScan_SubResponse_Factory_Parameters__M_Over_Z_Range;
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
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts.Get_ScanDataFromScanNumbers_M_Over_Z_Range_SubRequest;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts.SingleScan_SubResponse;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.CommonCore_Get_ScanData_IncludeReturnIonInjectionTimeData_Enum;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.CommonCore_Get_ScanData_IncludeReturnTotalIonCurrentData_Enum;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataNotFoundException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_rt_mz_binned.ScanLevel_1_RT_MZ_Binned_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_rt_mz_binned.ScanLevel_1_RT_MZ_Binned_ReadFile_GetParsedContents_JSON_GZIP_NoIntensities__ObjectCache;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_rt_mz_binned.ScanLevel_1_RT_MZ_Binned_ReadFile_GetParsedContents_JSON_GZIP_NoIntensities__ObjectCache.ScanLevel_1_RT_MZ_Binned_ReadFile_GetParsedContents_JSON_GZIP_NoIntensities__ObjectCache__Get_Result;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScan_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3_Holder;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_request_results.SpectralFile_Result_RetentionTime_ScanNumber;
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
		} else if ( servetResponseFormat == ServetResponseFormatEnum.JSON  ) {
			try {
				try {
					get_ScanDataFromScanNumbers_Request = 
							GetRequestObjectFromInputStream.getSingletonInstance().
							getRequestObjectFromStream_RequestFormat_JSON( Get_ScanDataFromScanNumbers_Request.class, request );
					
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
			
			//  maxNumberScansReturn

			Integer maxNumberScansReturn = ConfigData_ScanDataLocation_InWorkDirectory.getSingletonInstance().getMaxNumberScansReturn();
			
			if ( maxNumberScansReturn == null ) {
				//  Nothing in config so use default
				
				maxNumberScansReturn = MaxNumberScansReturnConstants.MAX_NUMBER_SCANS_RETURN_FOR_IMMEDIATE_WEBSERVICES__DEFAULT;
			}
			
			/////
			
			Get_ScanDataFromScanNumbers_Response webserviceResponse = new Get_ScanDataFromScanNumbers_Response();

			if ( excludeReturnScanPeakData != null 
					&& excludeReturnScanPeakData == Get_ScanData_ExcludeReturnScanPeakData.YES ) {
				
			} else {
				//  Only apply max number of scans for when returning scan peaks
				
				if ( scanNumbers.size() > maxNumberScansReturn ) {

					webserviceResponse.setTooManyScansToReturn( true );
					webserviceResponse.setMaxScansToReturn( maxNumberScansReturn );

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
					
					final SpectralFile_Reader__IF spectralFile_Reader_AfterAssigned_InsideTry = spectralFile_Reader;
					
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

						if ( excludeReturnScanPeakData != null 
								&& excludeReturnScanPeakData == Get_ScanData_ExcludeReturnScanPeakData.YES ) {
							
						} else {
							//  Only apply max number of scans for when returning scan peaks
								
							int allowedMaxScanNumbers = maxNumberScansReturn;
	
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
					}

					int returnedScanListInitialSize = scanNumbers.size();

					if ( maxScanLevelFound != null ) {
						returnedScanListInitialSize *= maxScanLevelFound;
					}

					SingleScan_SubResponse_Factory singleScan_SubResponse_Factory = SingleScan_SubResponse_Factory.getInstance();

					SingleScan_SubResponse_Factory_Parameters singleScan_SubResponse_Factory_Parameters = new SingleScan_SubResponse_Factory_Parameters();

					singleScan_SubResponse_Factory_Parameters.setMzHighCutoff( get_ScanDataFromScanNumbers_Request.getMzHighCutoff() );
					singleScan_SubResponse_Factory_Parameters.setMzLowCutoff( get_ScanDataFromScanNumbers_Request.getMzLowCutoff() );
					
					if ( get_ScanDataFromScanNumbers_Request.getM_Over_Z_Range_Filters() != null ) {
						
						List<SingleScan_SubResponse_Factory_Parameters__M_Over_Z_Range> m_Over_Z_Range_Filters = new ArrayList<>( get_ScanDataFromScanNumbers_Request.getM_Over_Z_Range_Filters().size() );
						singleScan_SubResponse_Factory_Parameters.setM_Over_Z_Range_Filters(m_Over_Z_Range_Filters);
						
						for ( Get_ScanDataFromScanNumbers_M_Over_Z_Range_SubRequest inputItem : get_ScanDataFromScanNumbers_Request.getM_Over_Z_Range_Filters()  ) {
							
							SingleScan_SubResponse_Factory_Parameters__M_Over_Z_Range outputItem = new SingleScan_SubResponse_Factory_Parameters__M_Over_Z_Range();
							outputItem.setMzHighCutoff( inputItem.getMzHighCutoff() );
							outputItem.setMzLowCutoff( inputItem.getMzLowCutoff() );
							
							m_Over_Z_Range_Filters.add(outputItem);
						}
						
						
						//   Filter  Scan Numbers for scans that do NOT have any peaks in the binned data for the M/Z filters
						
						//  Following code is NOT TESTED and NOT COMPLETE
					
//						ScanLevel_1_RT_MZ_Binned_ReadFile_GetParsedContents_JSON_GZIP_NoIntensities__ObjectCache__Get_Result scanLevel_1_RT_MZ_Binned_ReadFile_Parsed_JSON_GZIP_NoIntensities__ObjectCache__Get_Result =
//								ScanLevel_1_RT_MZ_Binned_ReadFile_GetParsedContents_JSON_GZIP_NoIntensities__ObjectCache.getSingletonInstance()
//								.getCacheValue(	scanFileAPIKey, 
//										(int) ScanLevel_1_RT_MZ_Binned_Constants.RETENTION_TIME_BIN_SIZE_IN_SECONDS_1,
//										(int) ScanLevel_1_RT_MZ_Binned_Constants.MZ_BIN_SIZE_IN_MZ_1 );
//				
//						
//						List<Integer> scanNumbers_Filtered = new ArrayList<>( scanNumbers.size() );
//						
//						for ( Integer scanNumber : scanNumbers ) {
//							
//
//							SpectralFile_Result_RetentionTime_ScanNumber result_RetentionTime = spectralFile_Reader_AfterAssigned_InsideTry.getScanRetentionTimeForScanNumber( scanNumber );
//
//							if ( result_RetentionTime == null ) {
//								//  Scan Number NOT FOUND in scan file so skip
//								continue; // EARLY CONTINUE
//							}
//							
//							List<Integer> scan_RetentionTime_Binned_List = new ArrayList<>( 2 );
//							
//							float scan_RetentionTime = result_RetentionTime.getRetentionTime();
//							
//							//   Search for scan_RetentionTime +/- 0.1 to ensure that do not miss a scan peak due to rounding
//							
//							float scan_RetentionTime_Subtract_0_point_1 = scan_RetentionTime - 0.1f;
//							float scan_RetentionTime_Add_0_point_1 = scan_RetentionTime + 0.1f;
//							
//							int scan_RetentionTime_Subtract_0_point_1__Floor = (int) Math.floor( scan_RetentionTime_Subtract_0_point_1 );
//							int scan_RetentionTime_Add_0_point_1__Floor = (int) Math.floor( scan_RetentionTime_Add_0_point_1 );
//							
//							scan_RetentionTime_Binned_List.add( scan_RetentionTime_Subtract_0_point_1__Floor );
//							if ( scan_RetentionTime_Subtract_0_point_1__Floor != scan_RetentionTime_Add_0_point_1__Floor ) {
//								scan_RetentionTime_Binned_List.add( scan_RetentionTime_Add_0_point_1__Floor );
//							}
//							
//							for ( Integer scan_RetentionTime_Binned_Item : scan_RetentionTime_Binned_List ) {
//								
//								
////								sss
//								
//							}
//							
//							
//							scanNumbers_Filtered.add( scanNumber );
//							
////							sss;
//						}
//						
//						scanNumbers = scanNumbers_Filtered;
						
					}
					
					//  Updated in method processScanNumber(...):   (Not synchronized here since always read and updated in a synchronized block on 'insertedScansScanNumbers'

					List<SingleScan_SubResponse> scans = new ArrayList<>( returnedScanListInitialSize );

					Set<Integer> insertedScansScanNumbers = new HashSet<>();
					
					Set<Integer> loadingScansInProgress_ScanNumbers = new HashSet<>();

		        	{
		        		AtomicBoolean anyThrownInsideStreamProcessing = new AtomicBoolean(false);
		        		
		        		List<Throwable> thrownInsideStream_List = Collections.synchronizedList(new ArrayList<>());
		        		
		        		SpectralStorageDataNotFoundException_HolderClass spectralStorageDataNotFoundException_HolderClass = new SpectralStorageDataNotFoundException_HolderClass();
		        		
			    		if ( ConfigData_ScanDataLocation_InWorkDirectory.getSingletonInstance().isParallelStream_DefaultThreadPool_Java_Processing_Enabled_True() ) {
			
			    			//  YES execute in parallel
			
			    			scanNumbers.parallelStream().forEach( scanNumber -> { 

			    				try {

			    					processScanNumber( 
			    							1, // recursionLevel
			    							scanNumber, 
			    							includeParentScans,
			    							excludeReturnScanPeakData,
			    							includeReturnIonInjectionTimeData,
			    							includeReturnScanLevelTotalIonCurrentData,
			    							scans, 
			    							insertedScansScanNumbers, 
			    							loadingScansInProgress_ScanNumbers,
			    							spectralFile_Reader_AfterAssigned_InsideTry,
			    							singleScan_SubResponse_Factory,
			    							singleScan_SubResponse_Factory_Parameters );
			    					
			    				} catch (SpectralStorageDataNotFoundException e) {

			    					spectralStorageDataNotFoundException_HolderClass.spectralStorageDataNotFoundException = e;
			    					
			    				} catch (Throwable t) {

		        					log.error( "Fail processing scanNumbers: scanNumber" + scanNumber, t);

		        					anyThrownInsideStreamProcessing.set(true);
		        					
		        					thrownInsideStream_List.add(t);
		        				}
		        			});
		        			
		        		} else {
		        			
		        			//  NOT execute in parallel

		        			scanNumbers.forEach( scanNumber -> {

		        				try {

		        					processScanNumber( 
		        							1, // recursionLevel
		        							scanNumber, 
		        							includeParentScans,
		        							excludeReturnScanPeakData,
		        							includeReturnIonInjectionTimeData,
		        							includeReturnScanLevelTotalIonCurrentData,
		        							scans, 
		        							insertedScansScanNumbers, 
		        							loadingScansInProgress_ScanNumbers,
		        							spectralFile_Reader_AfterAssigned_InsideTry,
		        							singleScan_SubResponse_Factory,
		        							singleScan_SubResponse_Factory_Parameters );

			    				} catch (SpectralStorageDataNotFoundException e) {

			    					spectralStorageDataNotFoundException_HolderClass.spectralStorageDataNotFoundException = e;
			    					
		        				} catch (Throwable t) {

		        					log.error( "Fail processing scanNumbers: scanNumber" + scanNumber, t);

		        					anyThrownInsideStreamProcessing.set(true);

		        					thrownInsideStream_List.add(t);

		        					throw new SpectralFileWebappInternalRuntimeException( t );
		        				}
		        			});
		        		}
			    		

		        		if ( anyThrownInsideStreamProcessing.get() ) {
		        			
		        			throw new SpectralFileWebappInternalRuntimeException( "At least 1 exception processing resultList_Temp" );
		        		}

			        	if ( spectralStorageDataNotFoundException_HolderClass.spectralStorageDataNotFoundException != null ) {
			        		
			        		throw spectralStorageDataNotFoundException_HolderClass.spectralStorageDataNotFoundException;
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
			Set<Integer> loadingScansInProgress_ScanNumbers,
			SpectralFile_Reader__IF spectralFile_Reader,
			SingleScan_SubResponse_Factory singleScan_SubResponse_Factory,
			SingleScan_SubResponse_Factory_Parameters singleScan_SubResponse_Factory_Parameters ) throws Exception {

		synchronized (insertedScansScanNumbers) {

			if ( insertedScansScanNumbers.contains( scanNumber ) ) {
				// exit since already processed this scan number
				return;
			}

			if ( loadingScansInProgress_ScanNumbers.contains( scanNumber ) ) {
				// exit since already currently loading this scan number
				return;
			}
			
			//  Add since currently loading
			loadingScansInProgress_ScanNumbers.add(scanNumber);
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
		if ( spectralFile_SingleScan_Common == null ) {

			synchronized (insertedScansScanNumbers) {

				//  Remove since Done loading and not found loading
				loadingScansInProgress_ScanNumbers.remove(scanNumber);
			}
			
		} else {
			singleScan_SubResponse = 
					singleScan_SubResponse_Factory
					.buildSingleScan_SubResponse( spectralFile_SingleScan_Common, singleScan_SubResponse_Factory_Parameters );
			
			synchronized (insertedScansScanNumbers) {

				//  Do check again in case loaded same scan number in parallel
				if ( ! insertedScansScanNumbers.contains( scanNumber ) ) {
					scans.add( singleScan_SubResponse );
					insertedScansScanNumbers.add( scanNumber );
				}
				
				//  Remove since Done loading and found loading
				loadingScansInProgress_ScanNumbers.remove(scanNumber);
			}
			
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
								loadingScansInProgress_ScanNumbers,
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
								loadingScansInProgress_ScanNumbers,
								spectralFile_Reader,
								singleScan_SubResponse_Factory,
								singleScan_SubResponse_Factory_Parameters );
					}
					
				}

			}
		}

	}
	
	private static class SpectralStorageDataNotFoundException_HolderClass {
		volatile SpectralStorageDataNotFoundException spectralStorageDataNotFoundException;
	}

}
