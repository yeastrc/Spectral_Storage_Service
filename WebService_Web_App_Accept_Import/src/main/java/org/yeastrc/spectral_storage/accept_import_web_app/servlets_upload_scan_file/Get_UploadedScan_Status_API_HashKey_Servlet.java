package org.yeastrc.spectral_storage.accept_import_web_app.servlets_upload_scan_file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.ServetResponseFormatEnum;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileBadRequestToServletException;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileDeserializeRequestException;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileFileUploadFileSystemException;
import org.yeastrc.spectral_storage.accept_import_web_app.import_processing__marked_deleted_file.UploadProcessing_MarkedDeletedFile_Create_Check;
import org.yeastrc.spectral_storage.accept_import_web_app.import_processing_status_file__read_write.UploadProcessingReadStatusFile;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.GetRequestObjectFromInputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.Get_ServletResultDataFormat_FromServletInitParam;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.WriteResponseObjectToOutputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.WriteResponseStringToOutputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.constants_enums.WebserviceSpectralStorageAcceptImport_ProcessStatusEnum;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.Get_UploadedScanFileInfo_Request;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.Get_UploadedScanFileInfo_Response;
import org.yeastrc.spectral_storage.accept_import_web_app.upload_scan_file.Get_scanProcessStatusKeyDir_PostProcessing;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessingStatusFileConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_api_key_processing.ScanFileAPIKey_ToFileReadWrite;

/**
 * This gets the status and if successful the hash key
 *
 */
public class Get_UploadedScan_Status_API_HashKey_Servlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger( Get_UploadedScan_Status_API_HashKey_Servlet.class );

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

			Get_UploadedScanFileInfo_Response webserviceResponse = new Get_UploadedScanFileInfo_Response();

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
					String msg = "scanProcessStatusKeyDir Status file not exist.  scanProcessStatusKeyDir: " 
							+ scanProcessStatusKeyDir.getAbsolutePath();
					log.info( msg );
				}

				webserviceResponse.setScanProcessStatusKey_NotFound( true );

				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( webserviceResponse, servetResponseFormat, response );

				return; // EARLY EXIT
			}


			if ( UploadProcessingStatusFileConstants.STATUS_COMPUTE_API_KEY.equals( status )
					|| UploadProcessingStatusFileConstants.STATUS_PENDING.equals( status ) 
					|| UploadProcessingStatusFileConstants.STATUS_PROCESSING_STARTED.equals( status ) 
					|| UploadProcessingStatusFileConstants.STATUS_PROCESSING_KILLED.equals( status ) ) {

				webserviceResponse.setStatus( WebserviceSpectralStorageAcceptImport_ProcessStatusEnum.PENDING );

			} else if ( UploadProcessing_MarkedDeletedFile_Create_Check.getInstance().doesMarkedDeleteFileExist( scanProcessStatusKeyDir ) ) {

				webserviceResponse.setStatus( WebserviceSpectralStorageAcceptImport_ProcessStatusEnum.DELETED );

			} else if ( UploadProcessingStatusFileConstants.STATUS_PROCESSING_FAILED.equals( status ) ) {

				webserviceResponse.setStatus( WebserviceSpectralStorageAcceptImport_ProcessStatusEnum.FAIL );
				webserviceResponse.setDataErrorFailMessage( getDataErrorFailMessage( scanProcessStatusKeyDir ) );

			} else if ( UploadProcessingStatusFileConstants.STATUS_PROCESSING_SUCCESSFUL.equals( status ) ) {

				String hashKey = ScanFileAPIKey_ToFileReadWrite.getInstance().readScanFileHashFromFinalHashKeyFile( scanProcessStatusKeyDir );

				webserviceResponse.setScanFileAPIKey( hashKey );
				webserviceResponse.setStatus( WebserviceSpectralStorageAcceptImport_ProcessStatusEnum.SUCCESS );

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
	
	/**
	 * @return
	 */
	private String getDataErrorFailMessage( File scanProcessStatusKeyDir ) throws Exception {
		
		File dataErrorFailMsgFile = new File( scanProcessStatusKeyDir, ScanFileToProcessConstants.DATA_ERROR_HUMAN_READABLE_FILENAME );
		
		if ( ! dataErrorFailMsgFile.exists() ) {
			// No data error fail msg file, return null
			return null;
		}
		
		long dataErrorFailMsgFileLength = dataErrorFailMsgFile.length();
		
		if ( dataErrorFailMsgFileLength > Integer.MAX_VALUE ) {
			String msg = "dataErrorFailMsgFileLength > Integer.MAX_VALUE.  Unable to retrieve error msg file: "
					+ dataErrorFailMsgFile.getAbsolutePath();
			log.error( msg );
			throw new SpectralStorageProcessingException( msg );
		}
		
		ByteArrayOutputStream baosErrorMsgFileContents = new ByteArrayOutputStream( ((int) dataErrorFailMsgFileLength ) );
		
		try ( InputStream is = new FileInputStream(dataErrorFailMsgFile) ) {
			byte[] buffer = new byte[ 10000 ];
			int bytesRead = 0;
			while ( ( bytesRead = is.read( buffer ) ) != -1 ) {
				baosErrorMsgFileContents.write(buffer, 0, bytesRead);
			}
		}
		
		byte[] dataErrorMsgFileContents = baosErrorMsgFileContents.toByteArray();
		
		String dataErrorMsgFileContentsString = new String( dataErrorMsgFileContents, StandardCharsets.UTF_8 );
		
		return dataErrorMsgFileContentsString;
	}
}
