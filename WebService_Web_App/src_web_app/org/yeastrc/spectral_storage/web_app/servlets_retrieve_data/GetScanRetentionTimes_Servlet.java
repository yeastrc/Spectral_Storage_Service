package org.yeastrc.spectral_storage.web_app.servlets_retrieve_data;

import java.io.File;
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
import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.Get_ScanRetentionTimes_Request;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.Get_ScanRetentionTimes_Response;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.sub_parts.Single_ScanRetentionTime_ScanNumber_SubResponse;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_request_results.SpectralFile_Result_RetentionTime_ScanNumber;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader_Factory;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader__IF;
import org.yeastrc.spectral_storage.web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.web_app.constants_enums.ServetResponseFormatEnum;
import org.yeastrc.spectral_storage.web_app.exceptions.SpectralFileBadRequestToServletException;
import org.yeastrc.spectral_storage.web_app.exceptions.SpectralFileDeserializeRequestException;
import org.yeastrc.spectral_storage.web_app.servlet_response_factories.Single_ScanRetentionTime_ScanNumber_SubResponse_Factory;
import org.yeastrc.spectral_storage.web_app.servlets_common.GetRequestObjectFromInputStream;
import org.yeastrc.spectral_storage.web_app.servlets_common.Get_ServletResultDataFormat_FromServletInitParam;
import org.yeastrc.spectral_storage.web_app.servlets_common.WriteResponseObjectToOutputStream;
import org.yeastrc.spectral_storage.web_app.servlets_common.WriteResponseStringToOutputStream;

/**
 * Get Scan Retention Times:
 * for scanFileAPIKey (scan file hash code)
 * filtered based on request, or all 
 *
 */
public class GetScanRetentionTimes_Servlet extends HttpServlet {

	private static final Logger log = Logger.getLogger( GetScanRetentionTimes_Servlet.class );

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

		Get_ScanRetentionTimes_Request get_ScanDataFromScanNumbers_Request = null;

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
				get_ScanDataFromScanNumbers_Request = (Get_ScanRetentionTimes_Request) requestObj;
			} catch (Exception e) {
				String msg = "Failed to cast requestObj to Get_ScanRetentionTimes_All_Request";
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
			Get_ScanRetentionTimes_Request get_ScanDataFromScanNumbers_Request,
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
	
		try {
			String scanFileAPIKey = get_ScanDataFromScanNumbers_Request.getScanFileAPIKey();

			if ( StringUtils.isEmpty( scanFileAPIKey ) ) {
				String msg = "missing scanFileAPIKey ";
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}
			
			if ( ( get_ScanDataFromScanNumbers_Request.getScanNumbers() != null
					&& ( ! get_ScanDataFromScanNumbers_Request.getScanNumbers().isEmpty() ) )
					&& ( ( get_ScanDataFromScanNumbers_Request.getScanLevelsToInclude() != null 
							&& ( ! get_ScanDataFromScanNumbers_Request.getScanLevelsToInclude().isEmpty() ) ) 
							|| ( get_ScanDataFromScanNumbers_Request.getScanLevelsToInclude() != null 
							&& ( ! get_ScanDataFromScanNumbers_Request.getScanLevelsToInclude().isEmpty() ) ) ) ) {
				
				String msg = "Cannot populate other options if 'scanNumbers' is populated.";
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
				
			}

			Get_ScanRetentionTimes_Response webserviceResponse = new Get_ScanRetentionTimes_Response();

			
			File scanStorageBaseDirectoryFile =
					ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance()
					.getScanStorageBaseDirectory();
			
			SpectralFile_Reader__IF spectralFile_Reader = null;
			
			try {
				List<Single_ScanRetentionTime_ScanNumber_SubResponse> scanParts = null;
			
				spectralFile_Reader = SpectralFile_Reader_Factory.getInstance()
						.getSpectralFile_Writer_ForHash( scanFileAPIKey, scanStorageBaseDirectoryFile );

				Single_ScanRetentionTime_ScanNumber_SubResponse_Factory single_ScanRetentionTime_ScanNumber_SubResponse_Factory =
						Single_ScanRetentionTime_ScanNumber_SubResponse_Factory.getInstance();

				List<Integer> scanNumbers = get_ScanDataFromScanNumbers_Request.getScanNumbers();
				
				if ( scanNumbers != null && ( ! scanNumbers.isEmpty() ) ) {
					
					//  scanNumbers list populated so use that to retrieve retention times
					
					scanParts = new ArrayList<>( scanNumbers.size() );

					for ( Integer scanNumber : scanNumbers ) {
						SpectralFile_Result_RetentionTime_ScanNumber internalResult = 
								spectralFile_Reader.getScanRetentionTimeForScanNumber( scanNumber );
						if ( internalResult != null ) {
							Single_ScanRetentionTime_ScanNumber_SubResponse subResponse = 
									single_ScanRetentionTime_ScanNumber_SubResponse_Factory.buildSingle_ScanRetentionTime_ScanNumber_SubResponse( internalResult );
							scanParts.add( subResponse );
						}
					}
				} else {
					// Use other than scanNumbers list to filter results list
					
					//  Scan Levels to Include
					
					Set<Integer> scanLevelsToIncludeSet = null;
					
					List<Integer> scanLevelsToIncludeList = get_ScanDataFromScanNumbers_Request.getScanLevelsToInclude();
					boolean haveScanLevelsToInclude = false;
					boolean have_One_ScanLevelToInclude = false;
					int onlyScanLevelToInclude = -1;
					
					if ( scanLevelsToIncludeList != null 
							&& ( ! scanLevelsToIncludeList.isEmpty() ) ) {
						haveScanLevelsToInclude = true;
						if ( scanLevelsToIncludeList.size() == 1 ) {
							have_One_ScanLevelToInclude = true;
							onlyScanLevelToInclude = scanLevelsToIncludeList.iterator().next();
						} else {
							scanLevelsToIncludeSet = new HashSet<>( scanLevelsToIncludeList );
						}
					}
					

					//  Scan Levels to Exclude
					
					Set<Integer> scanLevelsToExcludeSet = null;
					
					List<Integer> scanLevelsToExcludeList = get_ScanDataFromScanNumbers_Request.getScanLevelsToExclude();
					boolean haveScanLevelsToExclude = false;
					boolean have_One_ScanLevelToExclude = false;
					int onlyScanLevelToExclude = -1;
					
					if ( scanLevelsToExcludeList != null 
							&& ( ! scanLevelsToExcludeList.isEmpty() ) ) {
						haveScanLevelsToExclude = true;
						if ( scanLevelsToExcludeList.size() == 1 ) {
							have_One_ScanLevelToExclude = true;
							onlyScanLevelToExclude = scanLevelsToExcludeList.iterator().next();
						} else {
							scanLevelsToExcludeSet = new HashSet<>( scanLevelsToExcludeList );
						}
					}

					List<SpectralFile_Result_RetentionTime_ScanNumber> internalResults = 
						spectralFile_Reader.getScanRetentionTimes_All();

					scanParts = new ArrayList<>( internalResults.size() );

					for ( SpectralFile_Result_RetentionTime_ScanNumber internalResult : internalResults ) {
						
						boolean includeThisValue = false;
						
						if ( have_One_ScanLevelToInclude ) { 
							if ( internalResult.getLevel() == onlyScanLevelToInclude ) {
								includeThisValue = true;
							}
						} else if ( haveScanLevelsToInclude ) {
							if ( scanLevelsToIncludeSet.contains( internalResult.getLevel() ) ) {
								includeThisValue = true;
							}
						} else {
							includeThisValue = true;
						}
						
						if ( ! includeThisValue ) {
							continue;  //  EARLY CONTINUE
						}

						boolean excludeThisValue = false;
						
						if ( have_One_ScanLevelToExclude ) {
							if ( internalResult.getLevel() == onlyScanLevelToExclude ) {
								excludeThisValue = true;
							}
						} else if ( haveScanLevelsToExclude ) {
							if ( scanLevelsToExcludeSet.contains( internalResult.getLevel() ) ) {
								excludeThisValue = true;
							}
						}

						if ( excludeThisValue ) {
							continue;  //  EARLY CONTINUE
						}
						
						Single_ScanRetentionTime_ScanNumber_SubResponse subResponse = 
								single_ScanRetentionTime_ScanNumber_SubResponse_Factory.buildSingle_ScanRetentionTime_ScanNumber_SubResponse( internalResult );
						scanParts.add( subResponse );
					}
				}
				
				webserviceResponse.setScanParts( scanParts );
				
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
