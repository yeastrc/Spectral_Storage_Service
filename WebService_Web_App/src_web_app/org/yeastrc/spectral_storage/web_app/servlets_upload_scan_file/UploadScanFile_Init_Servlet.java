package org.yeastrc.spectral_storage.web_app.servlets_upload_scan_file;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.UploadScanFile_Init_Response;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.web_app.constants_enums.FileUploadConstants;
import org.yeastrc.spectral_storage.web_app.constants_enums.ServetResponseFormatEnum;
import org.yeastrc.spectral_storage.web_app.exceptions.SpectralFileFileUploadFileSystemException;
import org.yeastrc.spectral_storage.web_app.servlets_common.Get_ServletResultDataFormat_FromServletInitParam;
import org.yeastrc.spectral_storage.web_app.servlets_common.WriteResponseObjectToOutputStream;
import org.yeastrc.spectral_storage.web_app.upload_scan_file.CreateTempDirToUploadScanFileTo;


/**
 * Initializes the Upload Scan Process by Creating the directory to upload into 
 * and returning the uploadScanFileTempKey
 * 
 *  This Servlet should be considered a webservice as it returns JSON or XML
 */
public class UploadScanFile_Init_Servlet extends HttpServlet {

	private static final Logger log = Logger.getLogger( UploadScanFile_Init_Servlet.class );

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
		

		try {
			String requestURL = request.getRequestURL().toString();
		    
			File uploadFileTempDir = CreateTempDirToUploadScanFileTo.getInstance().createTempDirToUploadScanFileTo();
			
			String uploadScanFileTempKey = uploadFileTempDir.getName();
			

			//  Create a file in the directory to track the create date/time of the directory
			File createdDirFile = new File( uploadFileTempDir, ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_SUB_DIR_CREATE_TRACKING_FILE );
			if ( ! createdDirFile.createNewFile() ) {
				String msg = "Failed to create file in subdir: " + createdDirFile.getCanonicalPath();
				log.error( msg );
				throw new SpectralFileFileUploadFileSystemException(msg);
			}
			
			// Key returned to client

			UploadScanFile_Init_Response uploadInitResponse = new UploadScanFile_Init_Response();
			
			uploadInitResponse.setStatusSuccess(true);
			
			uploadInitResponse.setUploadScanFileTempKey( uploadScanFileTempKey );
			
			uploadInitResponse.setMaxUploadFileSize( FileUploadConstants.MAX_FILE_UPLOAD_SIZE );
			uploadInitResponse.setMaxUploadFileSizeFormatted( FileUploadConstants.MAX_FILE_UPLOAD_SIZE_FORMATTED );


			WriteResponseObjectToOutputStream.getSingletonInstance()
			.writeResponseObjectToOutputStream( uploadInitResponse, servetResponseFormat, response );
			
			log.info( "Completed processing Upload Init");
			
		} catch ( FailResponseSentException e ) {
			
		} catch (Throwable ex){

			log.error( "Exception: " + ex.toString(), ex );

			response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR /* 500  */ );

			UploadScanFile_Init_Response uploadResponse = new UploadScanFile_Init_Response();
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
	 * @param uploadedFileOnDisk
	 */
//	private void cleanupOnError( File uploadedFileOnDisk ) {
//		
//		if ( uploadedFileOnDisk != null && uploadedFileOnDisk.exists() ) {
//			uploadedFileOnDisk.delete();
//		}
//		
//	}
//	
	
	/**
	 * 
	 *
	 */
	private static class FailResponseSentException extends Exception {

		private static final long serialVersionUID = 1L;
	}
}
