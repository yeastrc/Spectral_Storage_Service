package org.yeastrc.spectral_storage.accept_import_web_app.servlets_upload_scan_file;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.ServetResponseFormatEnum;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileBadRequestToServletException;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileDeserializeRequestException;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileFileUploadFileSystemException;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.GetRequestObjectFromInputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.Get_ServletResultDataFormat_FromServletInitParam;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.WriteResponseObjectToOutputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.WriteResponseStringToOutputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.Get_UploadedScanFileInfo_Request;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.Get_UploadedScanFileInfo_Response;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.a_upload_processing_marked_deleted_file.UploadProcessing_MarkedDeletedFile_Create_Check;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.a_upload_processing_status_file.UploadProcessingReadStatusFile;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessingStatusFileConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_hash_processing.ScanFileHashToFileReadWrite;

/**
 * This gets the status and if successful the hash key
 *
 */
public class Get_UploadedScan_Status_API_HashKey_Servlet extends HttpServlet {

	private static final Logger log = Logger.getLogger( Get_UploadedScan_Status_API_HashKey_Servlet.class );

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
//		Get_UploadedScanFileInfo_Request get_UploadedScanFileInfo_Request = new Get_UploadedScanFileInfo_Request();
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

		Get_UploadedScanFileInfo_Request get_UploadedScanFileInfo_Request = null;

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
				get_UploadedScanFileInfo_Request = (Get_UploadedScanFileInfo_Request) requestObj;
			} catch (Exception e) {
				String msg = "Failed to cast requestObj to Get_UploadedScanFileInfo_Request";
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
			Get_UploadedScanFileInfo_Request get_UploadedScanFileInfo_Request,
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
	
		try {

			String scanProcessStatusKey = get_UploadedScanFileInfo_Request.getScanProcessStatusKey();

			if ( StringUtils.isEmpty( scanProcessStatusKey ) ) {
				String msg = "request is missing scanProcessStatusKey ";
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}

			File uploadBaseDir = ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getTempScanUploadBaseDirectory();

			//  Get the File object for the Base Subdir used to store the scan file for processing 
			String scanFilesToProcessBaseDirString = ScanFileToProcessConstants.SCAN_FILES_TO_PROCESS_BASE_DIR;
			File scanFilesToProcessBaseDir = new File( uploadBaseDir, scanFilesToProcessBaseDirString );
			if ( ! scanFilesToProcessBaseDir.exists() ) {
				String msg = "scanFilesToProcessBaseDir does not exist.  scanFilesToProcessBaseDir: " 
						+ scanFilesToProcessBaseDir.getAbsolutePath();
				log.error( msg );
				throw new SpectralFileFileUploadFileSystemException(msg);
			}


			Get_UploadedScanFileInfo_Response webserviceResponse = new Get_UploadedScanFileInfo_Response();

			File scanProcessStatusKeyDir = new File( scanFilesToProcessBaseDir, scanProcessStatusKey );
			if ( ! scanProcessStatusKeyDir.exists() ) {
				if ( log.isInfoEnabled() ) {
					String msg = "scanProcessStatusKeyDir does not exist.  scanProcessStatusKeyDir: " 
							+ scanProcessStatusKeyDir.getAbsolutePath();
					log.info( msg );
				}

				webserviceResponse.setScanProcessStatusKey_NotFound( true );

				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( webserviceResponse, servetResponseFormat, response );

				return; // EARLY EXIT
			} 

			if ( ! scanProcessStatusKeyDir.getName().equals( scanProcessStatusKey ) ) {

				//  Return not found but actually problem with provided key, contains Operating System path separator or something
				
				log.warn( "scanProcessStatusKey value is not same as directory name when put into Java File object. "
						+ "scanProcessStatusKey from request: '" + scanProcessStatusKey + "'"
						+ ", uploadScanFileTempKey directory name from Java File object: '" 
						+ scanProcessStatusKeyDir.getName() 
						+ "'" );

				webserviceResponse.setScanProcessStatusKey_NotFound( true );

				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( webserviceResponse, servetResponseFormat, response );
				
				return;  // EARLY EXIT
			}


			String status =
					UploadProcessingReadStatusFile.getInstance().uploadProcessingReadStatusFile( scanProcessStatusKeyDir );

			if ( StringUtils.isEmpty( status ) ) {
				if ( log.isInfoEnabled() ) {
					String msg = "scanFilesToProcessBaseDir Status file not exist.  scanFilesToProcessBaseDir: " 
							+ scanFilesToProcessBaseDir.getAbsolutePath();
					log.info( msg );
				}

				webserviceResponse.setScanProcessStatusKey_NotFound( true );

				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( webserviceResponse, servetResponseFormat, response );

				return; // EARLY EXIT
			}


			if ( UploadProcessingStatusFileConstants.STATUS_PENDING.equals( status ) 
					|| UploadProcessingStatusFileConstants.STATUS_PROCESSING_STARTED.equals( status ) 
					|| UploadProcessingStatusFileConstants.STATUS_PROCESSING_KILLED.equals( status ) ) {

				webserviceResponse.setStatusPending( true );

			} else if ( UploadProcessing_MarkedDeletedFile_Create_Check.getInstance().doesMarkedDeleteFileExist( scanProcessStatusKeyDir ) ) {

				webserviceResponse.setStatusDeleted( true );

			} else if ( UploadProcessingStatusFileConstants.STATUS_PROCESSING_FAILED.equals( status ) ) {

				webserviceResponse.setStatusFail( true );

			} else if ( UploadProcessingStatusFileConstants.STATUS_PROCESSING_SUCCESSFUL.equals( status ) ) {

				String hashKey = ScanFileHashToFileReadWrite.getInstance().readScanFileHashFromFinalHashKeyFile( scanProcessStatusKeyDir );

				webserviceResponse.setScanFileAPIKey( hashKey );
				webserviceResponse.setStatusSuccess( true );

			} else {

				String msg = "Unexpected status value '" + status + "' in directory " + scanProcessStatusKeyDir.getCanonicalPath();
				log.error( msg );
				throw new SpectralFileFileUploadFileSystemException( msg );
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
