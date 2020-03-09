package org.yeastrc.spectral_storage.accept_import_web_app.servlets_upload_scan_file;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.background_thread.ComputeAPIKeyForScanFileThread;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.FileUploadConstants;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.ServetResponseFormatEnum;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileBadRequestToServletException;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileDeserializeRequestException;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileFileUploadInternalException;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileWebappInternalException;
import org.yeastrc.spectral_storage.accept_import_web_app.import_scan_filename_local_disk.ImportScanFilename_LocalDisk;
import org.yeastrc.spectral_storage.accept_import_web_app.process_import_request_api_key_value_in_file.ProcessImportRequest_APIKey_Value_InFile;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.GetRequestObjectFromInputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.Get_ServletResultDataFormat_FromServletInitParam;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.WriteResponseObjectToOutputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.WriteResponseStringToOutputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_Submit_Request;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_Submit_Response;
import org.yeastrc.spectral_storage.accept_import_web_app.upload_scan_file.CreateProcessScanFileDir;
import org.yeastrc.spectral_storage.accept_import_web_app.upload_scan_file.ValidateTempDirToUploadScanFileTo;
import org.yeastrc.spectral_storage.accept_import_web_app.upload_scan_file.ValidateTempDirToUploadScanFileTo.ValidationResponse;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.shared_server_importer.create__xml_input_factory__xxe_safe.Create_XMLInputFactory_XXE_Safe;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.a_upload_processing_status_file.UploadProcessingWriteOrUpdateStatusFile;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessingStatusFileConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessing_InputScanfileS3InfoConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3_Holder;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.upload_scanfile_s3_location.UploadScanfileS3Location;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

/**
 * Submit the Upload Scan Process  
 * and returning the scanProcessStatusKey
 * 
 *  This Servlet should be considered a webservice as it returns JSON or XML
 */
public class UploadScanFile_Submit_Servlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger( UploadScanFile_Submit_Servlet.class );

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

		if ( log.isInfoEnabled() ) {
			log.info("INFO: processRequest(...) Start: NEW: uploadScanFile_Submit_Request.getUploadScanFileTempKey(): " + uploadScanFile_Submit_Request.getUploadScanFileTempKey() );
		}
		
		long startTime = System.currentTimeMillis();
		
		String uploadScanFileTempKey = null;
		
		try {
			uploadScanFileTempKey = uploadScanFile_Submit_Request.getUploadScanFileTempKey();

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
				String msg = "uploadScanFileTempKey_Dir does not exist for uploadScanFileTempKey. uploadScanFileTempKey: " 
						+ uploadScanFileTempKey
						+ ", uploadScanFileTempKey_Dir: " 
						+ uploadScanFileTempKey_Dir.getAbsolutePath();
				log.warn( msg );

				webserviceResponse.setUploadScanFileTempKey_NotFound( true );

				WriteResponseObjectToOutputStream.getSingletonInstance()
				.writeResponseObjectToOutputStream( webserviceResponse, servetResponseFormat, response );
				
				return;  // EARLY EXIT
			}
			
			if ( ! uploadScanFileTempKey_Dir.getName().equals( uploadScanFileTempKey ) ) {

				//  Return not found but actually problem with provided key, contains Operating System path separator or something
				String msg = "uploadScanFileTempKey value is not same as directory name when put into Java File object. "
						+ "uploadScanFileTempKey from request: '" + uploadScanFileTempKey + "'"
						+ ", uploadScanFileTempKey directory name from Java File object: '" 
						+ uploadScanFileTempKey_Dir.getName() 
						+ "'";
				
				log.warn( msg );

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
			
			String scanProcessStatusKey = null;
			
			if ( StringUtils.isNotEmpty( ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getS3Bucket() ) ) {
				//  Scan File on S3

				//  throws exceptions if errors
				scanProcessStatusKey =
						moveUpload_S3_ScanFileTempKey_Dir_To_scanProcessStatusKey_Dir( uploadScanFileTempKey_Dir );

			} else {
				// Scan File on local disk
				
				//  Find the scan file 
				String scanFilenameToMove = ImportScanFilename_LocalDisk.getInstance().getImportScanFilename_LocalDisk( uploadScanFileTempKey_Dir );

				if ( scanFilenameToMove == null ) {
					
					String msg = "No Scan file uploaded. uploadScanFileTempKey: "
							+ uploadScanFileTempKey
							+ ", uploadScanFileTempKey_Dir: " 
							+ uploadScanFileTempKey_Dir.getAbsolutePath();
					log.warn( msg );

					webserviceResponse.setNoUploadedScanFile(true);

					WriteResponseObjectToOutputStream.getSingletonInstance()
					.writeResponseObjectToOutputStream( webserviceResponse, servetResponseFormat, response );

					return;  // EARLY EXIT
				}

				//  throws exceptions if errors
				scanProcessStatusKey =
						moveUpload_LocalDisk_ScanFileTempKey_Dir_To_scanProcessStatusKey_Dir( 
								scanFilenameToMove,
								uploadScanFileTempKey_Dir );
				
				log.warn("INFO: processRequest(...) processed uploadScanFile_Submit_Request.getUploadScanFileTempKey(): " 
						+ uploadScanFile_Submit_Request.getUploadScanFileTempKey()
						+ ", and returned scanProcessStatusKey: "
						+ scanProcessStatusKey );
			}
			
			webserviceResponse.setScanProcessStatusKey( scanProcessStatusKey );

			webserviceResponse.setStatusSuccess(true);

			WriteResponseObjectToOutputStream.getSingletonInstance()
			.writeResponseObjectToOutputStream( webserviceResponse, servetResponseFormat, response );
			
		} catch (SpectralFileBadRequestToServletException e) {
		
			String msg = "SpectralFileBadRequestToServletException caught: scanFilenameToMove == null.  uploadScanFileTempKey: " 
					+ uploadScanFileTempKey ;
			log.warn( msg, e );
				
			response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );

			if ( StringUtils.isNotEmpty( e.getMessage() ) ) {
				WriteResponseStringToOutputStream.getInstance()
				.writeResponseStringToOutputStream( e.getMessage(), response);
			}

		} catch (Throwable e) {

			String msg = "Failed to process request, uploadScanFileTempKey: " + uploadScanFileTempKey;
			log.error( msg, e );
			response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR /* 500  */ );
		}
		
		long endTime = System.currentTimeMillis();
		
		long timeForMethod = endTime - startTime;
		
		if ( log.isInfoEnabled() ) {
			log.info("INFO: processRequest(...) took " + timeForMethod + " millseconds for uploadScanFile_Submit_Request.getUploadScanFileTempKey(): " 
					+ uploadScanFile_Submit_Request.getUploadScanFileTempKey() );
		}
		long timeTaken = endTime - startTime;
		if ( timeTaken > 7000 ) {
			log.warn( "Time taken in processRequest(...) > 7 seconds for for Submit Upload.  timeTaken (milliseconds): " + timeTaken 
					+ ", uploadScanFileTempKey: " + uploadScanFile_Submit_Request.getUploadScanFileTempKey() );
		}
	}
	

	/**
	 * @param scanFilenameToMove
	 * @param uploadFileTempDir
	 * @return scanProcessStatusKey
	 * @throws Exception
	 */
	private String moveUpload_S3_ScanFileTempKey_Dir_To_scanProcessStatusKey_Dir( File uploadFileTempDir ) throws Exception {

		File dirToProcessScanFile =
				CreateProcessScanFileDir.getInstance().createDirToProcessScanFile();
		
		String scanProcessStatusKey = dirToProcessScanFile.getName();

		//  Change to have a single S3 object path for uploaded scan files 
		//  since cannot change the S3 object key for a S3 object
		
		//  Not practical to copy a multi-gigabyte scan file just to change the object key
		
		//  IMPORTANT.  If decide that changing the object key is a must,
		//              must use "Copy an Object Using the AWS SDK for Java Multipart Upload API"
		//              https://docs.aws.amazon.com/AmazonS3/latest/dev/CopyingObjctsUsingLLJavaMPUapi.html
		
		
		createScanFileS3Location_File( scanProcessStatusKey, uploadFileTempDir, dirToProcessScanFile );
		
		commonProcessingOfUploadedScanFile( uploadFileTempDir, dirToProcessScanFile, scanProcessStatusKey );
		
		return scanProcessStatusKey;
	}
	
	/**
	 * @param scanProcessStatusKey
	 * @param uploadFileTempDir
	 * @param dirToProcessScanFile
	 */
	private void createScanFileS3Location_File( 
			String scanProcessStatusKey, 
			File uploadFileTempDir, 
			File dirToProcessScanFile ) throws Exception {

		JAXBContext jaxbContext = JAXBContext.newInstance( UploadScanfileS3Location.class );

		UploadScanfileS3Location uploadScanfileS3Location_InUploadTemp = 
				getTempUpload_UploadScanfileS3Location( uploadFileTempDir, jaxbContext );

		write_scanfileS3Location_InDirToProcessScanFile( uploadScanfileS3Location_InUploadTemp, dirToProcessScanFile, jaxbContext );
		
		if ( ! uploadScanfileS3Location_InUploadTemp.isS3_infoFrom_RemoteSystem() ) {
			createScanFileS3_Submitted_S3_Object( uploadScanfileS3Location_InUploadTemp );
		}
	}

	/**
	 * @param scanProcessStatusKey
	 * @param uploadFileTempDir
	 * @param dirToProcessScanFile
	 */
	private void createScanFileS3_Submitted_S3_Object( UploadScanfileS3Location uploadScanfileS3Location_InUploadTemp ) throws Exception {

		final AmazonS3 amazonS3 = CommonReader_File_And_S3_Holder.getSingletonInstance().getCommonReader_File_And_S3().getS3_Client();

//		PutObjectResult	putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata)
//		Uploads the specified input stream and object metadata to Amazon S3 under the specified bucket and key name.
		
		String objectKey = uploadScanfileS3Location_InUploadTemp.getS3_objectName()
				+ ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_SUBMITTED_FILE_SUFFIX;
		
		byte[] dummyBytes = { 0 };
		ByteArrayInputStream bais = new ByteArrayInputStream(dummyBytes);
		
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength( dummyBytes.length );
		
		amazonS3.putObject(
				uploadScanfileS3Location_InUploadTemp.getS3_bucketName(), 
				objectKey, 
				bais,
				objectMetadata );
	}
	
	/**
	 * @param scanfileS3Location_InDirToProcessScanFile
	 * @param dirToProcessScanFile
	 * @param jaxbContext
	 * @throws Exception
	 */
	private void write_scanfileS3Location_InDirToProcessScanFile( UploadScanfileS3Location scanfileS3Location_InDirToProcessScanFile, File dirToProcessScanFile, JAXBContext jaxbContext ) throws Exception {

		File scanfileS3InfoFile = new File( dirToProcessScanFile, UploadProcessing_InputScanfileS3InfoConstants.SCANFILE_S3_LOCATION_FILENAME );
		
		Marshaller marshaller = jaxbContext.createMarshaller();
		
		try ( OutputStream os = new FileOutputStream(scanfileS3InfoFile) ) {
			marshaller.marshal( scanfileS3Location_InDirToProcessScanFile, os );
		} catch (Exception e) {
			String msg = "Failed to Marshal XML.  Should be type UploadScanfileS3Location.  File: " +
					scanfileS3InfoFile.getAbsolutePath();
			log.error( msg, e );
			throw new SpectralFileWebappInternalException(msg, e);
		}
	}
	
	/**
	 * @param uploadFileTempDir
	 * @param jaxbContext
	 * @return
	 * @throws Exception
	 */
	private UploadScanfileS3Location getTempUpload_UploadScanfileS3Location( File uploadFileTempDir, JAXBContext jaxbContext ) throws Exception {

		File scanfileS3InfoFile = new File( uploadFileTempDir, UploadProcessing_InputScanfileS3InfoConstants.SCANFILE_S3_LOCATION_FILENAME );
		
		if ( ! scanfileS3InfoFile.exists() ) {
			String msg = "Input file of type UploadScanfileS3Location is missing. File: " +
					scanfileS3InfoFile.getAbsolutePath();
			log.error( msg );
			throw new SpectralFileWebappInternalException(msg);
		}
		
		try ( InputStream is = new FileInputStream( scanfileS3InfoFile) ) {
			XMLInputFactory xmlInputFactory = Create_XMLInputFactory_XXE_Safe.create_XMLInputFactory_XXE_Safe();
			XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader( new StreamSource( is ) );
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			Object uploadScanfileS3LocationObj = unmarshaller.unmarshal( xmlStreamReader );
			if ( ! ( uploadScanfileS3LocationObj instanceof UploadScanfileS3Location ) ) {
				String msg = "Unmarshaled object is not type UploadScanfileS3Location. Source file: " +
						scanfileS3InfoFile.getAbsolutePath();
				log.error( msg );
				throw new SpectralFileWebappInternalException(msg);
			}
			UploadScanfileS3Location uploadScanfileS3Location = (UploadScanfileS3Location) uploadScanfileS3LocationObj;
			return uploadScanfileS3Location;
		} catch (Exception e) {
			String msg = "Failed to Unmarshal XML.  Should be type UploadScanfileS3Location.  File: " +
					scanfileS3InfoFile.getAbsolutePath();
			log.error( msg, e );
			throw new SpectralFileWebappInternalException(msg, e);
		}
	}



	/**
	 * @param scanFilenameToMove
	 * @param uploadFileTempDir
	 * @return scanProcessStatusKey
	 * @throws Exception
	 */
	private String moveUpload_LocalDisk_ScanFileTempKey_Dir_To_scanProcessStatusKey_Dir( 
			String scanFilenameToMove,
			File uploadFileTempDir ) throws Exception {

		File dirToProcessScanFile =
				CreateProcessScanFileDir.getInstance().createDirToProcessScanFile();
		
		String scanProcessStatusKey = dirToProcessScanFile.getName();
		
		///   move the uploaded Scan file into processing dir.
		moveFileToScanProcessDir( scanFilenameToMove, uploadFileTempDir, dirToProcessScanFile );

		commonProcessingOfUploadedScanFile( uploadFileTempDir, dirToProcessScanFile, scanProcessStatusKey );
		
		return scanProcessStatusKey;
	}

	/**
	 * @param uploadFileTempDir
	 * @param dirToProcessScanFile
	 * @param scanProcessStatusKey
	 * @throws Exception
	 */
	private void commonProcessingOfUploadedScanFile( 
			File uploadFileTempDir, 
			File dirToProcessScanFile, 
			String scanProcessStatusKey
			) throws Exception {
		
		boolean apiKeyComputed = false;

		{ ///   If exists, move the computed API Key from hash into processing dir.
			File computedAPIKeyFromhashFile = new File( uploadFileTempDir, ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_HASH_STRING_API_KEY_FILENAME );
			if ( computedAPIKeyFromhashFile.exists() ) {
				moveFileToScanProcessDir( ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_HASH_STRING_API_KEY_FILENAME, uploadFileTempDir, dirToProcessScanFile );
				apiKeyComputed = true;
			}
		}
		{  //  Move file containing submitted filename with path, if exists.  Only exists if filename with path submitted
			File file = new File( uploadFileTempDir, ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_SUBMITTED_SCAN_FILENAME_WITH_PATH );
			if ( file.exists() ) {
				moveFileToScanProcessDir( ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_SUBMITTED_SCAN_FILENAME_WITH_PATH, uploadFileTempDir, dirToProcessScanFile );
			}
		}
		
		
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
		
		// Key returned to client, store in file
		
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
		
		if ( apiKeyComputed ) {

			//  API Key computed
			
			ProcessImportRequest_APIKey_Value_InFile.getInstance().
			processImportRequest_APIKey_Value_InFile( dirToProcessScanFile );
		
		} else {
			
			//  API Key NOT computed
			compute_API_Key_InProcessingDir( dirToProcessScanFile );
		}
	}
	

	/**
	 * @param dirToProcessScanFile
	 * @throws Exception
	 */
	private void compute_API_Key_InProcessingDir(  File dirToProcessScanFile ) throws Exception {
		
		try {
			//  Create status file for pending
			UploadProcessingWriteOrUpdateStatusFile.getInstance()
			.uploadProcessingWriteOrUpdateStatusFile( 
					UploadProcessingStatusFileConstants.STATUS_COMPUTE_API_KEY, 
					dirToProcessScanFile,
					UploadProcessingStatusFileConstants.STATUS_PROCESSING_CALLER_LABEL__ACCEPT_IMPORT_WEBAPP );
		} catch ( Exception e ) {
			String msg = "Failed to create status file, dirToProcessScanFile: " + dirToProcessScanFile.getAbsolutePath();
			log.error( msg, e );
			throw new SpectralStorageProcessingException( msg, e );
		}
		
		ComputeAPIKeyForScanFileThread.getInstance().awaken();
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
