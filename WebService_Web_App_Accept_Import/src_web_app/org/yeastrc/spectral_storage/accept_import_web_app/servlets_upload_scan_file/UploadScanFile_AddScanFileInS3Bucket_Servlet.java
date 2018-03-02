package org.yeastrc.spectral_storage.accept_import_web_app.servlets_upload_scan_file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.amazon_s3_client_builder.SpectralStorageWebappAmazonS3ClientBuilder;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.FileUploadConstants;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.ServetResponseFormatEnum;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileBadRequestToServletException;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileDeserializeRequestException;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileWebappInternalException;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.GetRequestObjectFromInputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.Get_ServletResultDataFormat_FromServletInitParam;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.WriteResponseObjectToOutputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.WriteResponseStringToOutputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.constants_enums.WebserviceSpectralStorageScanFileAllowedSuffixesConstants;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_AddScanFileInS3Bucket_Request;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_AddScanFileInS3Bucket_Response;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_UploadScanFile_Response;
import org.yeastrc.spectral_storage.accept_import_web_app.upload_scan_file.ValidateTempDirToUploadScanFileTo;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessing_InputScanfileS3InfoConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.upload_scanfile_s3_location.UploadScanfileS3Location;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;


/**
 * Submit the Upload Scan Process  
 * and returning the scanProcessStatusKey
 * 
 *  This Servlet should be considered a webservice as it returns JSON or XML
 */
public class UploadScanFile_AddScanFileInS3Bucket_Servlet extends HttpServlet {

	private static final Logger log = Logger.getLogger( UploadScanFile_AddScanFileInS3Bucket_Servlet.class );

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
		

		UploadScanFile_AddScanFileInS3Bucket_Request uploadScanFile_AddScanFileInS3Bucket_Request = null;

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
				uploadScanFile_AddScanFileInS3Bucket_Request = (UploadScanFile_AddScanFileInS3Bucket_Request) requestObj;
			} catch (Exception e) {
				String msg = "Failed to cast requestObj to UploadScanFile_AddScanFileInS3Bucket_Request";
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
		
		processRequest( uploadScanFile_AddScanFileInS3Bucket_Request, request, response );
	}
	
	/**
	 * @param uploadScanFile_AddScanFileInS3Bucket_Request
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processRequest( 
			UploadScanFile_AddScanFileInS3Bucket_Request uploadScanFile_AddScanFileInS3Bucket_Request,
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		try {
			String uploadScanFileTempKey = uploadScanFile_AddScanFileInS3Bucket_Request.getUploadScanFileTempKey();

			String s3_Region = uploadScanFile_AddScanFileInS3Bucket_Request.getS3Region();
			String s3_Bucket =uploadScanFile_AddScanFileInS3Bucket_Request.getS3Bucket();
			String s3_ObjectKey = uploadScanFile_AddScanFileInS3Bucket_Request.getS3ObjectKey();

			if ( StringUtils.isEmpty( uploadScanFileTempKey ) ) {
				String msg = "request is missing uploadScanFileTempKey ";
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}

			if ( StringUtils.isEmpty( s3_Bucket ) ) {
				String msg = "request is missing s3_Bucket ";
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}

			if ( StringUtils.isEmpty( s3_ObjectKey ) ) {
				String msg = "request is missing s3_ObjectKey ";
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}


			UploadScanFile_AddScanFileInS3Bucket_Response webserviceResponse = new UploadScanFile_AddScanFileInS3Bucket_Response();

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

				UploadScanFile_AddScanFileInS3Bucket_Response uploadResponse = new UploadScanFile_AddScanFileInS3Bucket_Response();
				uploadResponse.setStatusSuccess(false);
				uploadResponse.setUploadScanFileTempKey_NotFound(true);

				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
				
				return;  // EARLY EXIT
			}

			//  Validate Scan Filename Suffix and set "Uploaded Scan Filename" : In quotes since it is fake set in this web app from passed in suffix
			
			String scanFilenameToProcess = null;
			
			if ( StringUtils.isNotEmpty( uploadScanFile_AddScanFileInS3Bucket_Request.getScanFilenameSuffix() ) ) {

				String scanFilenameSuffix = uploadScanFile_AddScanFileInS3Bucket_Request.getScanFilenameSuffix();

				if ( ! ( WebserviceSpectralStorageScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML.equals( scanFilenameSuffix ) 
						||  WebserviceSpectralStorageScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML.equals( scanFilenameSuffix ) ) ) {

					log.warn( "Filename suffix is NOT a valid suffix: " + scanFilenameSuffix );

					response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );

					UploadScanFile_UploadScanFile_Response uploadResponse = new UploadScanFile_UploadScanFile_Response();
					uploadResponse.setStatusSuccess(false);
					uploadResponse.setUploadedFileSuffixNotValid( true );

					WriteResponseObjectToOutputStream.getSingletonInstance()
					.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );

					return;  // EARLY EXIT
				}
				
				scanFilenameToProcess = ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX + scanFilenameSuffix;
			
			} else {
//				String s3_Bucket =uploadScanFile_AddScanFileInS3Bucket_Request.getS3Bucket();
//				String s3_ObjectKey = uploadScanFile_AddScanFileInS3Bucket_Request.getS3ObjectKey();

				if ( s3_ObjectKey.endsWith( WebserviceSpectralStorageScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML ) ) {
					scanFilenameToProcess = ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX 
							+ WebserviceSpectralStorageScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML;
				} else if ( s3_ObjectKey.endsWith( WebserviceSpectralStorageScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML ) ) {
						scanFilenameToProcess = ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX 
								+ WebserviceSpectralStorageScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML;
				} else {
				
					log.warn( "Filename suffix at end of S3 Object Key is NOT a valid suffix: " + s3_ObjectKey );

					response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );

					UploadScanFile_UploadScanFile_Response uploadResponse = new UploadScanFile_UploadScanFile_Response();
					uploadResponse.setStatusSuccess(false);
					uploadResponse.setUploadedFileSuffixNotValid( true );

					WriteResponseObjectToOutputStream.getSingletonInstance()
					.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );

					return;  // EARLY EXIT
				}
			}

		    
			
			AmazonS3 amazonS3client = null;
			
			//  Get amazon S3 client, using region if supplied
			try {
				SpectralStorageWebappAmazonS3ClientBuilder spectralStorageWebappAmazonS3ClientBuilder = SpectralStorageWebappAmazonS3ClientBuilder.newBuilder();
				if ( StringUtils.isNotEmpty( s3_Region ) ) {
					spectralStorageWebappAmazonS3ClientBuilder = spectralStorageWebappAmazonS3ClientBuilder.withRegion( s3_Region );
				}
				amazonS3client = spectralStorageWebappAmazonS3ClientBuilder.build();
			} catch ( Exception e ) {

				String msg = "Failed to create amazon S3 client.  Region in request: " + s3_Region;
				log.warn( msg, e );
				throw e;
			}
			
			//  Validate that specified S3 object exists in specified bucket and that it can be accessed using this app's permissions

			try {
				if ( ! amazonS3client.doesObjectExist( s3_Bucket, s3_ObjectKey ) ) {

					String msg = "S3 object in request does NOT exist.  Region in request: " 
							+ s3_Region
							+ ", bucket: " + s3_Bucket
							+ ", object key: " + s3_ObjectKey;
					log.warn( msg );

					UploadScanFile_AddScanFileInS3Bucket_Response uploadResponse = new UploadScanFile_AddScanFileInS3Bucket_Response();
					uploadResponse.setStatusSuccess(false);
					uploadResponse.setUploadScanFileS3BucketOrObjectKey_NotFound(true);

					WriteResponseObjectToOutputStream.getSingletonInstance()
					.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
					
					return;  // EARLY EXIT
				}
				
				//  TODO  try to catch correct exception to determine if do not have permissions for bucket or object

			} catch ( Exception e ) {

				String msg = "Failed to check if S3 object exists.  Region in request: " 
						+ s3_Region
						+ ", bucket: " + s3_Bucket
						+ ", object key: " + s3_ObjectKey;
				log.warn( msg, e );
				throw e;
			}
			
			long objectLength = 0;
			
			try {
				S3Object s3Object = amazonS3client.getObject( s3_Bucket, s3_ObjectKey );

				ObjectMetadata objectMetadata_s3Object = s3Object.getObjectMetadata();

				objectLength = objectMetadata_s3Object.getContentLength();

			} catch ( Exception e ) {

				String msg = "Failed to get S3 object length.  Region in request: " 
						+ s3_Region
						+ ", bucket: " + s3_Bucket
						+ ", object key: " + s3_ObjectKey;
				log.warn( msg, e );
				throw e;
			}
			

		       // file upload size limit
			if ( objectLength > FileUploadConstants.MAX_FILE_UPLOAD_SIZE ) {

				log.warn( "Scan File in S3 object Exceeds allowed size.  S3 object size: " + objectLength
						+ ", max upload file size: " + FileUploadConstants.MAX_FILE_UPLOAD_SIZE 
						+ ", max upload file size (formatted): " + FileUploadConstants.MAX_FILE_UPLOAD_SIZE_FORMATTED
						+ ",  Region in request: " 
						+ s3_Region
						+ ", bucket: " + s3_Bucket
						+ ", object key: " + s3_ObjectKey );
				
				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
				
				UploadScanFile_UploadScanFile_Response uploadResponse = new UploadScanFile_UploadScanFile_Response();
				uploadResponse.setStatusSuccess(false);
				uploadResponse.setFileSizeLimitExceeded(true);
				uploadResponse.setMaxSize( FileUploadConstants.MAX_FILE_UPLOAD_SIZE );
				uploadResponse.setMaxSizeFormatted( FileUploadConstants.MAX_FILE_UPLOAD_SIZE_FORMATTED );

				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
				
			}
			
			UploadScanfileS3Location scanfileS3Location = new UploadScanfileS3Location();
			scanfileS3Location.setS3_infoFrom_RemoteSystem( true );
			scanfileS3Location.setS3_bucketName( s3_Bucket );
			scanfileS3Location.setS3_objectName( s3_ObjectKey );
			if ( StringUtils.isNotEmpty( s3_Region ) ) {
				scanfileS3Location.setS3_region( s3_Region );
			}
			scanfileS3Location.setScanFilenameToProcess( scanFilenameToProcess );

			createScanFileS3Location_File( scanfileS3Location, uploadScanFileTempKey_Dir );
			
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
	 * @param scanfileS3Location
	 * @param uploadFileTempDir
	 * @throws Exception
	 */
	private void createScanFileS3Location_File( UploadScanfileS3Location scanfileS3Location, File uploadFileTempDir ) throws Exception {

		JAXBContext jaxbContext = JAXBContext.newInstance( UploadScanfileS3Location.class );
				
		File scanfileS3InfoFile = new File( uploadFileTempDir, UploadProcessing_InputScanfileS3InfoConstants.SCANFILE_S3_LOCATION_FILENAME );
		
		Marshaller marshaller = jaxbContext.createMarshaller();
		
		try ( OutputStream os = new FileOutputStream(scanfileS3InfoFile) ) {
			marshaller.marshal( scanfileS3Location, os );
		} catch (Exception e) {
			String msg = "Failed to Marshal XML.  Should be type UploadScanfileS3Location.  File: " +
					scanfileS3InfoFile.getAbsolutePath();
			log.error( msg, e );
			throw new SpectralFileWebappInternalException(msg, e);
		}
	}
	
}
