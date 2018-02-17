package org.yeastrc.spectral_storage.accept_import_web_app.servlets_upload_scan_file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.FileUploadConstants;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.ServetResponseFormatEnum;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.Get_ServletResultDataFormat_FromServletInitParam;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.WriteResponseObjectToOutputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.constants.WebserviceSpectralStorageQueryParamsConstants;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.constants.WebserviceSpectralStorageScanFileAllowedSuffixesConstants;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_UploadScanFile_Response;
import org.yeastrc.spectral_storage.accept_import_web_app.upload_scan_file.ValidateTempDirToUploadScanFileTo;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.ScanFileToProcessConstants;


/**
 * Upload Scan File Process - Actual upload of Scan File
 * 
 * Receives uploadScanFileTempKey as query parameter
 * 
 * Receives Uploaded scan file as a Stream
 * 
 * Reads the input stream from the "HttpServletRequest request" object
 * 
 *  This Servlet should be considered a webservice as it returns JSON or XML
 */
public class UploadScanFile_UploadScanFile_Servlet extends HttpServlet {

	private static final Logger log = Logger.getLogger( UploadScanFile_UploadScanFile_Servlet.class );

	private static final long serialVersionUID = 1L;
	
	public static final int COPY_FILE_ARRAY_SIZE = 32 * 1024; // 32 KB
	
	
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
		
		String uploadScanFileTempKey = request.getParameter( WebserviceSpectralStorageQueryParamsConstants.UPLOAD_SCAN_FILE_TEMP_KEY_QUERY_PARAM );

		File uploadedFileOnDisk = null;
		
		String scanFilenameToProcess = null;


		try {
			String requestURL = request.getRequestURL().toString();

		       // uploadScanFileTempKey validation
			if ( StringUtils.isEmpty( uploadScanFileTempKey ) ) {

				log.warn( "Query param '" + WebserviceSpectralStorageQueryParamsConstants.UPLOAD_SCAN_FILE_TEMP_KEY_QUERY_PARAM 
						+ "' not on URL.  requestURL: " + requestURL );
				
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );

				UploadScanFile_UploadScanFile_Response uploadResponse = new UploadScanFile_UploadScanFile_Response();
				uploadResponse.setStatusSuccess(false);
				uploadResponse.setFileSizeLimitExceeded(true);
				uploadResponse.setMaxSize( FileUploadConstants.MAX_FILE_UPLOAD_SIZE );
				uploadResponse.setMaxSizeFormatted( FileUploadConstants.MAX_FILE_UPLOAD_SIZE_FORMATTED );

				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
				
				throw new FailResponseSentException();
			}
			
		       // file upload size limit
			if ( request.getContentLengthLong() > FileUploadConstants.MAX_FILE_UPLOAD_SIZE ) {

				log.warn( "Upload File size Exceeded.  File size uploaded: " + request.getContentLengthLong()
						+ ", max upload file size: " + FileUploadConstants.MAX_FILE_UPLOAD_SIZE 
						+ ", max upload file size (formatted): " + FileUploadConstants.MAX_FILE_UPLOAD_SIZE_FORMATTED);
				
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				
				UploadScanFile_UploadScanFile_Response uploadResponse = new UploadScanFile_UploadScanFile_Response();
				uploadResponse.setStatusSuccess(false);
				uploadResponse.setFileSizeLimitExceeded(true);
				uploadResponse.setMaxSize( FileUploadConstants.MAX_FILE_UPLOAD_SIZE );
				uploadResponse.setMaxSizeFormatted( FileUploadConstants.MAX_FILE_UPLOAD_SIZE_FORMATTED );

				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
				
				throw new FailResponseSentException();
			}
			
			String scanFilenameSuffix = request.getParameter( WebserviceSpectralStorageQueryParamsConstants.UPLOAD_SCAN_FILE_SERVLET_QUERY_PARAM_SCAN_FILENAME_SUFFIX );

		    if ( ! ( WebserviceSpectralStorageScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML.equals( scanFilenameSuffix ) 
		    		||  WebserviceSpectralStorageScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML.equals( scanFilenameSuffix ) ) ) {

				log.warn( "Filename suffix provided in parameter '"
						+ WebserviceSpectralStorageQueryParamsConstants.UPLOAD_SCAN_FILE_SERVLET_QUERY_PARAM_SCAN_FILENAME_SUFFIX
						+ "' is NOT a valid suffix: " + scanFilenameSuffix );
				
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				
				UploadScanFile_UploadScanFile_Response uploadResponse = new UploadScanFile_UploadScanFile_Response();
				uploadResponse.setStatusSuccess(false);
				uploadResponse.setUploadedFileSuffixNotValid( true );

				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
				
				throw new FailResponseSentException();
		    }
		    
		    scanFilenameToProcess = ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX + scanFilenameSuffix;

			File uploadBaseDir = ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getTempScanUploadBaseDirectory();

			UploadScanFile_UploadScanFile_Response webserviceResponse = new UploadScanFile_UploadScanFile_Response();

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
				
				throw new FailResponseSentException();
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
			
				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( webserviceResponse, servetResponseFormat, response );
				
				throw new FailResponseSentException();
			}
					
			
			uploadedFileOnDisk = new File( uploadScanFileTempKey_Dir, scanFilenameToProcess );
					
			//  Transfer the file from the stream to a disk file
			InputStream inputStream = null;
			OutputStream outStream = null;
			try {
				inputStream = request.getInputStream();
				outStream = new FileOutputStream( uploadedFileOnDisk );

				byte[] byteBuffer = new byte[ COPY_FILE_ARRAY_SIZE ];
				int bytesRead;
				long bytesReadTotal = 0;

				while ( ( bytesRead = inputStream.read( byteBuffer ) ) > 0 ){
					
					bytesReadTotal += bytesRead;

				       // file upload size limit
					if ( bytesReadTotal > FileUploadConstants.MAX_FILE_UPLOAD_SIZE ) {

						log.warn( "Upload File size Exceeded.  Bytes Read count so far: " + bytesReadTotal
								+ ", max upload file size: " + FileUploadConstants.MAX_FILE_UPLOAD_SIZE 
								+ ", max upload file size (formatted): " + FileUploadConstants.MAX_FILE_UPLOAD_SIZE_FORMATTED
								+ ", writing to file: " + uploadedFileOnDisk.getAbsolutePath() );
						
						response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
						
						UploadScanFile_UploadScanFile_Response uploadResponse = new UploadScanFile_UploadScanFile_Response();
						uploadResponse.setStatusSuccess(false);
						uploadResponse.setFileSizeLimitExceeded(true);
						uploadResponse.setMaxSize( FileUploadConstants.MAX_FILE_UPLOAD_SIZE );
						uploadResponse.setMaxSizeFormatted( FileUploadConstants.MAX_FILE_UPLOAD_SIZE_FORMATTED );

						WriteResponseObjectToOutputStream.getSingletonInstance()
						.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
						
						throw new FailResponseSentException();
					}

					outStream.write( byteBuffer, 0, bytesRead );
					
				}
			} catch ( Exception e ) {
				
				String msg = "Failed writing request to file: " + uploadedFileOnDisk.getAbsolutePath();
				log.error(msg, e);
				response.setStatus( 500 );

				throw new FailResponseSentException();
			} finally {

				boolean closeOutputStreamFail = false;
				try {
					if ( outStream != null ) {
						outStream.close();
					}
				} catch(Exception e){
					closeOutputStreamFail = true;

					String msg = "Failed closing file: " + uploadedFileOnDisk.getAbsolutePath();
					log.error(msg, e);
					response.setStatus( 500 );

					throw new FailResponseSentException();
				} finally {
					try {
						if ( inputStream != null ) {
							inputStream.close();
						}
					} catch(Exception e){ 
						if ( ! closeOutputStreamFail ) {
						}
						String msg = "Failed closing input stream for file: " + uploadedFileOnDisk.getAbsolutePath();
						log.error(msg, e);
						response.setStatus( 500 );

						throw new FailResponseSentException();
					}
				}
			}
			
			
			UploadScanFile_UploadScanFile_Response uploadResponse = new UploadScanFile_UploadScanFile_Response();
			
			uploadResponse.setStatusSuccess(true);
			
			WriteResponseObjectToOutputStream.getSingletonInstance()
			.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
			
			log.info( "Completed processing Upload");
			
		} catch ( FailResponseSentException e ) {
			
			
		} catch (Throwable ex){

			log.error( "Exception: " + ex.toString(), ex );

			response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR /* 500  */ );

			UploadScanFile_UploadScanFile_Response uploadResponse = new UploadScanFile_UploadScanFile_Response();
			uploadResponse.setStatusSuccess(false);
			
			try {
				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
			} catch ( Exception e ) {
				throw new ServletException( e );
			} finally {

			}
			
			//  response.sendError  sends a HTML page so don't use here since return JSON instead
			
//			response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR /* 500  */ );
			
//			response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR /* 500  */, responseJSONString );
			
//			throw new ServletException( ex );
		}

	}
	
	
	
	/**
	 * 
	 *
	 */
	private static class FailResponseSentException extends Exception {

		private static final long serialVersionUID = 1L;
	}
}
