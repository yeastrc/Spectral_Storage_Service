package org.yeastrc.spectral_storage.accept_import_web_app.servlets_upload_scan_file;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.FileUploadConstants;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.ServetResponseFormatEnum;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileBadRequestToServletException;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileDeserializeRequestException;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileFileUploadFileSystemException;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileFileUploadInternalException;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.GetRequestObjectFromInputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.Get_ServletResultDataFormat_FromServletInitParam;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.WriteResponseObjectToOutputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.WriteResponseStringToOutputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.constants_enums.WebserviceSpectralStorageAcceptImportScanFileAllowedSuffixesConstants;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_AddScanFileFromFilenameAndPath_Request;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_AddScanFileFromFilenameAndPath_Response;
import org.yeastrc.spectral_storage.accept_import_web_app.upload_scan_file.ValidateTempDirToUploadScanFileTo;
import org.yeastrc.spectral_storage.accept_import_web_app.upload_scan_file.ValidateTempDirToUploadScanFileTo.ValidationResponse;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;


/**
 * Accept a scan filename with it's path
 * 
 *  This Servlet should be considered a webservice as it returns JSON or XML
 */
public class UploadScanFile_AddScanFileFromFilenameAndPath_Servlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger( UploadScanFile_AddScanFileFromFilenameAndPath_Servlet.class );

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
		

		UploadScanFile_AddScanFileFromFilenameAndPath_Request UploadScanFile_AddScanFileFromFilenameAndPath_Request = null;

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
				UploadScanFile_AddScanFileFromFilenameAndPath_Request = (UploadScanFile_AddScanFileFromFilenameAndPath_Request) requestObj;
			} catch (Exception e) {
				String msg = "Failed to cast requestObj to UploadScanFile_AddScanFileFromFilenameAndPath_Request";
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
		
		processRequest( UploadScanFile_AddScanFileFromFilenameAndPath_Request, request, response );
	}
	
	/**
	 * @param uploadScanFile_AddScanFileFromFilenameAndPath_Request
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processRequest( 
			UploadScanFile_AddScanFileFromFilenameAndPath_Request uploadScanFile_AddScanFileFromFilenameAndPath_Request,
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		try {
			UploadScanFile_AddScanFileFromFilenameAndPath_Response webserviceResponse = new UploadScanFile_AddScanFileFromFilenameAndPath_Response();

			String uploadScanFileTempKey = uploadScanFile_AddScanFileFromFilenameAndPath_Request.getUploadScanFileTempKey();

			ConfigData_Directories_ProcessUploadInfo_InWorkDirectory configData_Directories_ProcessUploadInfo_InWorkDirectory = ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance();
			
			if ( configData_Directories_ProcessUploadInfo_InWorkDirectory.getSubmittedScanFilePathRestrictions() == null
					|| configData_Directories_ProcessUploadInfo_InWorkDirectory.getSubmittedScanFilePathRestrictions().isEmpty() ) {
				//  No Configuration for allowed Paths so reject request
				String msg = "No Configuration for allowed Paths so reject request. configData_Directories_ProcessUploadInfo_InWorkDirectory.getSubmittedScanFilePathRestrictions() is null or empty.  uploadScanFileTempKey: " 
						+ uploadScanFileTempKey;
				log.warn( msg );
				
				webserviceResponse.setUploadScanFileWithPath_FilePathsAllowedNotConfigured(true);
				// Set in case caller not checking the first thing set true
				webserviceResponse.setUploadScanFileWithPath_FilePathNotAllowed(true);

				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( webserviceResponse, servetResponseFormat, response );
				
				return;  // EARLY EXIT
			}
			
			//  Values from the webservice request
			String submitted_filenameWithPathString = uploadScanFile_AddScanFileFromFilenameAndPath_Request.getFilenameWithPath();
			BigInteger submitted_fileSize = uploadScanFile_AddScanFileFromFilenameAndPath_Request.getFileSize();
			long submitted_fileSize_AsLong = 0; 

			if ( StringUtils.isEmpty( uploadScanFileTempKey ) ) {
				String msg = "request is missing uploadScanFileTempKey ";
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}

			if ( StringUtils.isEmpty( submitted_filenameWithPathString ) ) {
				String msg = "request is missing filenameWithPath ";
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}

			if ( submitted_fileSize == null ) {
				String msg = "request is missing fileSize ";
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}
			
			try {
				submitted_fileSize_AsLong = submitted_fileSize.longValueExact();
			} catch ( Exception e ) {
				String msg = "fileSize in request is outside the value of a Java numeric type 'long': " + submitted_fileSize.toString();
				log.warn( msg, e );
				String msgReturned = "fileSize in request is outside the range of allowed values";
				throw new SpectralFileBadRequestToServletException( msgReturned );
			}


			File uploadBaseDir = configData_Directories_ProcessUploadInfo_InWorkDirectory.getTempScanUploadBaseDirectory();

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

			{
				ValidationResponse validationResponse = 
						ValidateTempDirToUploadScanFileTo.getInstance()
						.validateTempDirToUploadScanFileTo( uploadScanFileTempKey_Dir );
				
				if ( validationResponse != ValidationResponse.VALID ) {

					if ( validationResponse == ValidationResponse.KEY_NOT_FOUND ) {
						String msg = "ValidateTempDirToUploadScanFileTo.getInstance().validateTempDirToUploadScanFileTo(...) returns KEY_NOT_FOUND. uploadScanFileTempKey from request: '" 
								+ uploadScanFileTempKey 
								+ "', uploadScanFileTempKey_Dir: " 
								+ uploadScanFileTempKey_Dir.getAbsolutePath();
						log.warn( msg );

						webserviceResponse.setStatusSuccess(false);
						webserviceResponse.setUploadScanFileTempKey_NotFound(true);

						WriteResponseObjectToOutputStream.getSingletonInstance()
						.writeResponseObjectToOutputStream( webserviceResponse, servetResponseFormat, response );

						return;  // EARLY EXIT
						
					} else if ( validationResponse == ValidationResponse.KEY_EXPIRED ) {
						String msg = "ValidateTempDirToUploadScanFileTo.getInstance().validateTempDirToUploadScanFileTo(...) returns KEY_EXPIRED. uploadScanFileTempKey from request: '" 
								+ uploadScanFileTempKey 
								+ "', uploadScanFileTempKey_Dir: " 
								+ uploadScanFileTempKey_Dir.getAbsolutePath();
						log.warn( msg );

						webserviceResponse.setStatusSuccess(false);
						webserviceResponse.setUploadScanFileTempKey_Expired(true);

						WriteResponseObjectToOutputStream.getSingletonInstance()
						.writeResponseObjectToOutputStream( webserviceResponse, servetResponseFormat, response );

						return;  // EARLY EXIT
					} else {
					
						String msg = "validationResponse is not an expected value.  is: " + validationResponse;
						log.error( msg );
						throw new SpectralFileFileUploadInternalException( msg );
					}
				}
			}
			
			//  Process Submitted Filename With Path
			
			File scanFileWithPath = new File( submitted_filenameWithPathString );
			if ( ! scanFileWithPath.exists() ) {
				//  File with path not found
				
				log.warn("Provided filenameWithPath does not exist: " + submitted_filenameWithPathString );

				UploadScanFile_AddScanFileFromFilenameAndPath_Response uploadResponse = new UploadScanFile_AddScanFileFromFilenameAndPath_Response();
				uploadResponse.setStatusSuccess(false);
				uploadResponse.setUploadScanFileWithPath_FileNotFound(true);

				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
				
				return;  // EARLY EXIT		
			}
			
			{ // Validate that File Path Submitted is allowed
				String fileWithPathCanonical = scanFileWithPath.getCanonicalPath();
				
				boolean matchedAPathRestrictionToStartOfFilenameWithPath = false;
				
				for ( String pathStartRestrictionEntry : configData_Directories_ProcessUploadInfo_InWorkDirectory.getSubmittedScanFilePathRestrictions() ) {
					if ( fileWithPathCanonical.startsWith( pathStartRestrictionEntry ) ) {
						matchedAPathRestrictionToStartOfFilenameWithPath = true;
						break;
					}
				}
				
				if ( ! matchedAPathRestrictionToStartOfFilenameWithPath ) {
					//  Start of File with path not found not found in PathsRestrictions
					
					log.warn("Submitted filenameWithPath does not start with one of the configured allowed paths.  Submitted filenameWithPath (enclosed with '|'): |" 
							+ submitted_filenameWithPathString
							+ "|, submitted_filenameWithPath as Java .getCanonicalPath() string: (enclosed with '|'): |"
							+ fileWithPathCanonical
							+ "|, configured allowed paths (comma separated): " 
							+ StringUtils.join( configData_Directories_ProcessUploadInfo_InWorkDirectory.getSubmittedScanFilePathRestrictions(), "," ) );
	
					UploadScanFile_AddScanFileFromFilenameAndPath_Response uploadResponse = new UploadScanFile_AddScanFileFromFilenameAndPath_Response();
					uploadResponse.setStatusSuccess(false);
					uploadResponse.setUploadScanFileWithPath_FilePathNotAllowed( true );
	
					WriteResponseObjectToOutputStream.getSingletonInstance()
					.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
					
					return;  // EARLY EXIT		
				}
			}

			
			long scanFileLength = scanFileWithPath.length();
			
			if ( submitted_fileSize_AsLong != scanFileLength ) {
				//  Filesize of actual file does not match file size in the request
				
				String msg = "fileSize in request does not match size of file submitted. File Submitted: "
						+ submitted_filenameWithPathString
						+ ", File Size Submitted: "
						+ submitted_fileSize_AsLong
						+ ", Size of file submitted: "
						+ scanFileLength
						+ ", submitted filenameWithPath: "
						+ submitted_filenameWithPathString ;
				log.warn( msg );

				UploadScanFile_AddScanFileFromFilenameAndPath_Response uploadResponse = new UploadScanFile_AddScanFileFromFilenameAndPath_Response();
				uploadResponse.setStatusSuccess(false);
				uploadResponse.setUploadScanFileWithPath_NotMatch_SubmittedFileSize(true);

				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
				
				return;  // EARLY EXIT		
			}
			
		       // file upload size limit
			if ( scanFileLength > FileUploadConstants.MAX_FILE_UPLOAD_SIZE ) {

				log.warn( "Scan File Exceeds allowed size.  File size: " + scanFileLength
						+ ", max upload file size: " + FileUploadConstants.MAX_FILE_UPLOAD_SIZE 
						+ ", max upload file size (formatted): " + FileUploadConstants.MAX_FILE_UPLOAD_SIZE_FORMATTED
						+ ", submitted filenameWithPath: " 
						+ submitted_filenameWithPathString );
				
				UploadScanFile_AddScanFileFromFilenameAndPath_Response uploadResponse = new UploadScanFile_AddScanFileFromFilenameAndPath_Response();
				uploadResponse.setStatusSuccess(false);
				uploadResponse.setFileSizeLimitExceeded(true);
				uploadResponse.setMaxSize( FileUploadConstants.MAX_FILE_UPLOAD_SIZE );
				uploadResponse.setMaxSizeFormatted( FileUploadConstants.MAX_FILE_UPLOAD_SIZE_FORMATTED );

				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
			}
			
			String scanFilename = scanFileWithPath.getName();

			//  Validate Scan Filename Suffix and set "Uploaded Scan Filename" : In quotes since it is fake set in this web app from passed in suffix
			
			String scanFilenameToProcess = null;
		
			if ( scanFilename.endsWith( WebserviceSpectralStorageAcceptImportScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML ) ) {
				scanFilenameToProcess = ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX 
						+ WebserviceSpectralStorageAcceptImportScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML;
			} else if ( scanFilename.endsWith( WebserviceSpectralStorageAcceptImportScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML ) ) {
					scanFilenameToProcess = ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX 
							+ WebserviceSpectralStorageAcceptImportScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML;
			} else {
				log.warn( "Filename suffix at end of filenameWithPath is NOT a valid suffix: " + submitted_filenameWithPathString );

				UploadScanFile_AddScanFileFromFilenameAndPath_Response uploadResponse = new UploadScanFile_AddScanFileFromFilenameAndPath_Response();
				uploadResponse.setStatusSuccess(false);
				uploadResponse.setFilenameSuffixNotValid(true);

				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );

				return;  // EARLY EXIT
			}
			
			
			//  Create soft link in upload processing directory to submitted scan file
			//   (The importer will read directly from the submitted scan file with path through this soft link)
			{
				File scanFile_Softlink_File = new File( uploadScanFileTempKey_Dir, scanFilenameToProcess );
				
				Path newLink = scanFile_Softlink_File.toPath();
				Path target = scanFileWithPath.toPath();
				
				try {
					Files.createSymbolicLink( newLink, target );
				} catch ( Exception e ) {
					String msg = "Failed to create soft link from scanFile: "
							+ scanFileWithPath.getAbsolutePath()
							+ ", to soft link: " 
							+ scanFile_Softlink_File.getAbsolutePath();
					System.err.println( msg );
					throw new SpectralFileFileUploadFileSystemException(msg, e);
				}
			}
			
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
