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
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_Delete_For_ScanProcessStatusKey_Request;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_Delete_For_ScanProcessStatusKey_Response;
import org.yeastrc.spectral_storage.accept_import_web_app.upload_scan_file.Get_scanProcessStatusKeyDir_PostProcessing;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.a_upload_processing_marked_deleted_file.UploadProcessing_MarkedDeletedFile_Create_Check;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.a_upload_processing_status_file.UploadProcessingReadStatusFile;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessingStatusFileConstants;


/**
 * Delete the Scan file upload for scanProcessStatusKey from the system
 * 
 *  This Servlet should be considered a webservice as it returns JSON or XML
 */
public class UploadScanFile_Delete_For_ScanProcessStatusKey_Servlet extends HttpServlet {

	private static final Logger log = Logger.getLogger( UploadScanFile_Delete_For_ScanProcessStatusKey_Servlet.class );

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
		
		log.info( "INFO:  doPost called");
		

		UploadScanFile_Delete_For_ScanProcessStatusKey_Request webservice_Request = null;

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
				webservice_Request = (UploadScanFile_Delete_For_ScanProcessStatusKey_Request) requestObj;
			} catch (Exception e) {
				String msg = "Failed to cast requestObj to UploadScanFile_Delete_UploadTempKey_Request";
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
		
		processRequest( webservice_Request, request, response );
	}
	
	/**
	 * @param uploadScanFile_Delete_UploadTempKey_Request
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processRequest( 
			UploadScanFile_Delete_For_ScanProcessStatusKey_Request uploadScanFile_Delete_UploadTempKey_Request,
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
	
		try {

			String scanProcessStatusKey = uploadScanFile_Delete_UploadTempKey_Request.getScanProcessStatusKey();

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


			UploadScanFile_Delete_For_ScanProcessStatusKey_Response webserviceResponse = new UploadScanFile_Delete_For_ScanProcessStatusKey_Response();

			File scanProcessStatusKeyDir = 
					Get_scanProcessStatusKeyDir_PostProcessing.getInstance()
					.get_scanProcessStatusKeyDir_PostProcessing( scanProcessStatusKey );
			
			
			if ( scanProcessStatusKeyDir == null ) {
				if ( log.isInfoEnabled() ) {
					String msg = "scanProcessStatusKeyDir does not exist for scanProcessStatusKey or there are errors: " 
							+ scanProcessStatusKey;
					log.info( msg );
				}

				webserviceResponse.setScanProcessStatusKey_NotFound( true );

				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( webserviceResponse, servetResponseFormat, response );
				
				return;  // EARLY EXIT
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

			String currentStatus = 
					UploadProcessingReadStatusFile.getInstance().uploadProcessingReadStatusFile( scanProcessStatusKeyDir );

			if ( ! ( UploadProcessingStatusFileConstants.STATUS_PROCESSING_FAILED.equals( currentStatus )
					|| UploadProcessingStatusFileConstants.STATUS_PROCESSING_SUCCESSFUL.equals( currentStatus )
					|| UploadProcessingStatusFileConstants.STATUS_PROCESSING_KILLED.equals( currentStatus ) ) ) {

				webserviceResponse.setCurrentUploadProcessingStatusNotAllowDelete( true );
				
				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( webserviceResponse, servetResponseFormat, response );
				
				return;  // EARLY EXIT
			}

			//  Create Marked Deleted file
			UploadProcessing_MarkedDeletedFile_Create_Check.getInstance().createMarkedDeleteFile( scanProcessStatusKeyDir );

			webserviceResponse.setStatusSuccess(true);

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
