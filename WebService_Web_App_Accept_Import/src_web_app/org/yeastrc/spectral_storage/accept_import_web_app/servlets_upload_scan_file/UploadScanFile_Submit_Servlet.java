package org.yeastrc.spectral_storage.accept_import_web_app.servlets_upload_scan_file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.background_thread.ProcessScanFileThread;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.FileUploadConstants;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.ServetResponseFormatEnum;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileBadRequestToServletException;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileDeserializeRequestException;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileFileUploadInternalException;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.GetRequestObjectFromInputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.Get_ServletResultDataFormat_FromServletInitParam;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.WriteResponseObjectToOutputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.WriteResponseStringToOutputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_Submit_Request;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_Submit_Response;
import org.yeastrc.spectral_storage.accept_import_web_app.upload_scan_file.CreateProcessScanFileDir;
import org.yeastrc.spectral_storage.accept_import_web_app.upload_scan_file.ValidateTempDirToUploadScanFileTo;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.a_upload_processing_status_file.UploadProcessingWriteOrUpdateStatusFile;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessingStatusFileConstants;


/**
 * Submit the Upload Scan Process  
 * and returning the scanProcessStatusKey
 * 
 *  This Servlet should be considered a webservice as it returns JSON or XML
 */
public class UploadScanFile_Submit_Servlet extends HttpServlet {

	private static final Logger log = Logger.getLogger( UploadScanFile_Submit_Servlet.class );

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
		

		UploadScanFile_Submit_Request uploadScanFile_Submit_Request = null;

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
				uploadScanFile_Submit_Request = (UploadScanFile_Submit_Request) requestObj;
			} catch (Exception e) {
				String msg = "Failed to cast requestObj to UploadScanFile_Submit_Request";
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
		
		processRequest( uploadScanFile_Submit_Request, request, response );
	}
	
	/**
	 * @param uploadScanFile_Submit_Request
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processRequest( 
			UploadScanFile_Submit_Request uploadScanFile_Submit_Request,
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
	
		try {
			String uploadScanFileTempKey = uploadScanFile_Submit_Request.getUploadScanFileTempKey();

			if ( StringUtils.isEmpty( uploadScanFileTempKey ) ) {
				String msg = "request is missing uploadScanFileTempKey ";
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}

			UploadScanFile_Submit_Response webserviceResponse = new UploadScanFile_Submit_Response();

			File uploadBaseDir = ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getTempScanUploadBaseDirectory();

			String uploadFileTempDirString = FileUploadConstants.UPLOAD_FILE_TEMP_BASE_DIR;
			
			File uploadFileTempDir = new File( uploadBaseDir, uploadFileTempDirString );

			File uploadScanFileTempKey_Dir = new File( uploadFileTempDir, uploadScanFileTempKey );
			if ( ! uploadScanFileTempKey_Dir.exists() ) {
				if ( log.isInfoEnabled() ) {
					String msg = "uploadScanFileTempKey_Dir does not exist.  uploadScanFileTempKey_Dir: " 
							+ uploadScanFileTempKey_Dir.getAbsolutePath();
					log.info( msg );
				}

				webserviceResponse.setUploadScanFileTempKey_NotFound( true );

				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( webserviceResponse, servetResponseFormat, response );
				
				return;  // EARLY EXIT
			}
			
			if ( ! uploadScanFileTempKey_Dir.getName().equals( uploadScanFileTempKey ) ) {

				//  Return not found but actually problem with provided key, contains Operating System path separator or something
				
				log.warn( "uploadScanFileTempKey value is not same as directory name when put into Java File object. "
						+ "uploadScanFileTempKey from request: '" + uploadScanFileTempKey + "'"
						+ ", uploadScanFileTempKey directory name from Java File object: '" 
						+ uploadScanFileTempKey_Dir.getName() 
						+ "'" );

				webserviceResponse.setUploadScanFileTempKey_NotFound( true );

				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( webserviceResponse, servetResponseFormat, response );
				
				return;  // EARLY EXIT
			}

			if ( ! ValidateTempDirToUploadScanFileTo.getInstance().validateTempDirToUploadScanFileTo( uploadScanFileTempKey_Dir ) ) {

				webserviceResponse.setUploadScanFileTempKey_NotFound( true );
			

				UploadScanFile_Submit_Response uploadResponse = new UploadScanFile_Submit_Response();
				uploadResponse.setStatusSuccess(false);
				uploadResponse.setUploadScanFileTempKey_NotFound(true);

				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
				
				return;  // EARLY EXIT
			}

			//  Find the scan file 
			String scanFilenameToMove = getScanFileToMove( uploadScanFileTempKey_Dir );
				
			if ( scanFilenameToMove == null ) {
				webserviceResponse.setNoUploadedScanFile(true);

				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( webserviceResponse, servetResponseFormat, response );

				return;  // EARLY EXIT
			}

			//  throws exceptions if errors
			String scanProcessStatusKey =
					moveUploadScanFileTempKey_Dir_To_scanProcessStatusKey_Dir( 
							scanFilenameToMove,
							uploadScanFileTempKey_Dir );

			webserviceResponse.setScanProcessStatusKey( scanProcessStatusKey );

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
	
	/**
	 * @param uploadFileTempDir
	 * @return
	 * @throws SpectralFileFileUploadInternalException
	 */
	private String getScanFileToMove( File uploadFileTempDir ) throws SpectralFileFileUploadInternalException {
		
		//  Find the scan filename
		String scanFilenameToMove = null;
		
		File[] uploadFileTempDir_Files = uploadFileTempDir.listFiles();

		for ( File dirEntry : uploadFileTempDir_Files ) {
			String dirEntryFilename = dirEntry.getName();
			if ( dirEntryFilename.startsWith( ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX )
					&& ( dirEntryFilename.endsWith( ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML ) 
							|| dirEntryFilename.endsWith( ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML ) ) ) {
				if ( scanFilenameToMove != null ) {
					String msg = "Found more than one scan file. Previous filename: " + scanFilenameToMove
							+ ", current filename: " + dirEntry.getName();
					log.error( msg );
					throw new SpectralFileFileUploadInternalException(msg);
				}
				scanFilenameToMove = dirEntry.getName();
			}
		}
		if ( scanFilenameToMove == null ) {
			String msg = "No Scan file Found. Allowed filenames are: " 
					+ ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX 
					+ ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML
					+ ", and "
					+ ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX 
					+ ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML
					;
			log.error( msg );
			
			return null;  //  EARLY EXIT
		}
		
		return scanFilenameToMove;
	}
	

	/**
	 * @param scanFilenameToMove
	 * @param uploadFileTempDir
	 * @return scanProcessStatusKey
	 * @throws Exception
	 */
	private String moveUploadScanFileTempKey_Dir_To_scanProcessStatusKey_Dir( 
			String scanFilenameToMove,
			File uploadFileTempDir ) throws Exception {


		File dirToProcessScanFile =
				CreateProcessScanFileDir.getInstance().createDirToProcessScanFile();
		
		String scanProcessStatusKey = dirToProcessScanFile.getName();
		
		///   move the uploaded Scan file into processing dir.
		moveFileToScanProcessDir( scanFilenameToMove, uploadFileTempDir, dirToProcessScanFile );
		
		//  Empty and delete temp upload directory uploadFileTempDir
		
		try {
			File[] uploadFileTempDir_Files = uploadFileTempDir.listFiles();

			for ( File dirEntry : uploadFileTempDir_Files ) {
				if ( ! dirEntry.delete() ) {
					String msg = "Failed to delete temp dir entry for uploaded file, file failed to delete: " + dirEntry.getAbsolutePath()
							+ ", uploadFileTempDir: " + uploadFileTempDir.getAbsolutePath();
					log.error( msg );
					throw new Exception( msg );
				}
			}
			if ( ! uploadFileTempDir.delete() ) {
				String msg = "Failed to delete temp dir for uploaded file, uploadFileTempDir: " + uploadFileTempDir.getAbsolutePath();
				log.error( msg );
				throw new Exception( msg );
			}
		} catch ( Exception e ) {
			String msg = "Failed to delete temp dir for uploaded file, uploadFileTempDir: " + uploadFileTempDir.getAbsolutePath();
			log.error( msg, e );
			throw new Exception(msg, e);
		}
		
		// Key returned to client
		
		{
			File scanProcessStatusKeyFile = new File( dirToProcessScanFile, ScanFileToProcessConstants.SCAN_PROCESS_STATUS_KEY_FILENAME );

			try ( BufferedWriter writer = new BufferedWriter( new FileWriterWithEncoding( scanProcessStatusKeyFile, StandardCharsets.UTF_8 ) ) ) {
				writer.write( scanProcessStatusKey );
			} catch ( Exception e ) {
				String msg = "Failed to write scanProcessStatusKey to file: " + scanProcessStatusKeyFile.getAbsolutePath();
				log.error( msg );
				throw new Exception(msg);
			}
		}
		
		
		try {
			//  Create status file for pending
			UploadProcessingWriteOrUpdateStatusFile.getInstance()
			.uploadProcessingWriteOrUpdateStatusFile( UploadProcessingStatusFileConstants.STATUS_PENDING, dirToProcessScanFile );
		} catch ( Exception e ) {
			String msg = "Failed to create status file, dirToProcessScanFile: " + dirToProcessScanFile.getAbsolutePath();
			log.error( msg, e );
			throw new Exception(msg, e);
		}
		

		ProcessScanFileThread.getInstance().awaken();
		
		return scanProcessStatusKey;
	}
	
	/**
	 * @param filenameToMove
	 * @param uploadFileTempDir
	 * @param dirToProcessScanFile
	 * @throws Exception
	 */
	private void moveFileToScanProcessDir( String filenameToMove, File uploadFileTempDir, File dirToProcessScanFile ) throws Exception {
		
		File uploadedFile_In_uploadFileTempDir = new File( uploadFileTempDir, filenameToMove );
		
		File uploadedFile_In_dirForProcessingScan = new File( dirToProcessScanFile, filenameToMove );

		try {
			FileUtils.moveFile( uploadedFile_In_uploadFileTempDir, uploadedFile_In_dirForProcessingScan );

		} catch ( Exception e ) {

			String msg = "Failed to move uploaded file to dirToProcessScanFile.  Src file: " + uploadedFile_In_uploadFileTempDir.getAbsolutePath()
			+ ", dest file: " + uploadedFile_In_dirForProcessingScan.getAbsolutePath();
			log.error( msg, e );
			throw new Exception(msg, e);
		}
	}
}
