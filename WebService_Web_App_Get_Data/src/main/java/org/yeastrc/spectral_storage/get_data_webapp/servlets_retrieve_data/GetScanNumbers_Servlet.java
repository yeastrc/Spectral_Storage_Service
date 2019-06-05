package org.yeastrc.spectral_storage.get_data_webapp.servlets_retrieve_data;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.get_data_webapp.config.ConfigData_ScanDataLocation_InWorkDirectory;
import org.yeastrc.spectral_storage.get_data_webapp.constants_enums.ServetResponseFormatEnum;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileBadRequestToServletException;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileDeserializeRequestException;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.GetRequestObjectFromInputStream;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.Get_ServletResultDataFormat_FromServletInitParam;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.WriteResponseObjectToOutputStream;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.WriteResponseStringToOutputStream;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_ScanFileAPI_Key_NotFound;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanNumbers_Request;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanNumbers_Response;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataNotFoundException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader_Factory;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader__IF;

/**
 * Get Scan Numbers for scanFileAPIKey (scan file hash code) for Scan Level Include and Exclude
 *
 */
public class GetScanNumbers_Servlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger( GetScanNumbers_Servlet.class );

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

		Get_ScanNumbers_Request get_ScanNumbers_Request = null;

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
				get_ScanNumbers_Request = (Get_ScanNumbers_Request) requestObj;
			} catch (Exception e) {
				String msg = "Failed to cast requestObj to Get_ScanNumbers_Request";
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
		
		processRequest( get_ScanNumbers_Request, request, response );
	}
	
	/**
	 * @param get_ScanNumbers_Request
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processRequest( 
			Get_ScanNumbers_Request get_ScanNumbers_Request,
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
	
		try {
			String scanFileAPIKey = get_ScanNumbers_Request.getScanFileAPIKey();
			List<Integer> scanLevelsToInclude = get_ScanNumbers_Request.getScanLevelsToInclude();
			List<Integer> scanLevelsToExclude = get_ScanNumbers_Request.getScanLevelsToExclude();
			
			if ( StringUtils.isEmpty( scanFileAPIKey ) ) {
				String msg = "missing scanFileAPIKey ";
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}

			Get_ScanNumbers_Response webserviceResponse = new Get_ScanNumbers_Response();
			

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

					List<Integer> scanNumbers =
							spectralFile_Reader.getScanNumbersForScanLevelsToIncludeScanLevelsToExclude( scanLevelsToInclude, scanLevelsToExclude );

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
