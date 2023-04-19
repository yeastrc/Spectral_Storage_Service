package org.yeastrc.spectral_storage.accept_import_web_app.servlets_upload_scan_file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.FileUploadConstants;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.ServetResponseFormatEnum;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileFileUploadInternalException;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileWebappInternalException;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.Get_ServletResultDataFormat_FromServletInitParam;
import org.yeastrc.spectral_storage.accept_import_web_app.servlets_common.WriteResponseObjectToOutputStream;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.constants_enums.WebserviceSpectralStorageAcceptImportQueryParamsConstants;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.constants_enums.WebserviceSpectralStorageAcceptImportScanFileAllowedSuffixesConstants;
import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.UploadScanFile_UploadScanFile_Response;
import org.yeastrc.spectral_storage.accept_import_web_app.upload_scan_file.ValidateTempDirToUploadScanFileTo;
import org.yeastrc.spectral_storage.accept_import_web_app.upload_scan_file.ValidateTempDirToUploadScanFileTo.ValidationResponse;
import org.yeastrc.spectral_storage.accept_import_web_app.utils.Create_S3_Object_Paths;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessing_InputScanfileS3InfoConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.file_contents_hash_processing.Compute_Hashes;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.s3_aws_interface.S3_AWS_InterfaceObjectHolder;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_api_key_processing.ScanFileAPIKey_ComputeFromScanFileContentHashes;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_api_key_processing.ScanFileAPIKey_ToFileReadWrite;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.upload_scanfile_s3_location.UploadScanfileS3Location;

  import com.amazonaws.services.s3.AmazonS3;
  import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
  import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
  import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
  import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
  import com.amazonaws.services.s3.model.PartETag;
  import com.amazonaws.services.s3.model.UploadPartRequest;
  import com.amazonaws.services.s3.model.UploadPartResult;


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

	private static final Logger log = LoggerFactory.getLogger( UploadScanFile_UploadScanFile_Servlet.class );

	private static final long serialVersionUID = 1L;
	
	public static final int COPY_FILE_ARRAY_SIZE = 32 * 1024; // 32 KB
	
	//  Large since can have at most 10,000 parts
	public static final int S3_MULIPART_UPLOAD_PART_SIZE = 40 * 1024 * 1024; // each part 40 MB

//	public static final int S3_MULIPART_UPLOAD_PART_SIZE = 5 * 1024 * 1024; // each part 5 MB - Min Size

	private static final int UPLOAD_SCAN_FILE_INIT_UPLOAD_TO_S3_RETRY_COUNT_MAX = 3;
	private static final int UPLOAD_SCAN_FILE_INIT_UPLOAD_TO_S3_RETRY_DELAY = 200; // in milliseconds

	private static final int UPLOAD_SCAN_FILE_PART_TO_S3_RETRY_COUNT_MAX = 4;
	private static final int UPLOAD_SCAN_FILE_PART_TO_S3_RETRY_DELAY = 300; // in milliseconds

	private static final int UPLOAD_SCAN_FILE_COMPLETE_UPLOAD_TO_S3_RETRY_COUNT_MAX = 6;
	private static final int UPLOAD_SCAN_FILE_COMPLETE_UPLOAD_TO_S3_RETRY_DELAY = 500; // in milliseconds

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
		
		String uploadScanFileTempKey = request.getParameter( WebserviceSpectralStorageAcceptImportQueryParamsConstants.UPLOAD_SCAN_FILE_TEMP_KEY_QUERY_PARAM );

		String scanFilenameToProcess = null;


		try {
			String requestURL = request.getRequestURL().toString();
			
			long postContentLength = request.getContentLengthLong();

		       // uploadScanFileTempKey validation
			if ( StringUtils.isEmpty( uploadScanFileTempKey ) ) {

				log.warn( "Query param '" + WebserviceSpectralStorageAcceptImportQueryParamsConstants.UPLOAD_SCAN_FILE_TEMP_KEY_QUERY_PARAM 
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
			if ( postContentLength > FileUploadConstants.MAX_FILE_UPLOAD_SIZE ) {

				log.warn( "Upload File size Exceeded.  File size uploaded: " + postContentLength
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
			
			String scanFilenameSuffix = request.getParameter( WebserviceSpectralStorageAcceptImportQueryParamsConstants.UPLOAD_SCAN_FILE_SERVLET_QUERY_PARAM_SCAN_FILENAME_SUFFIX );

		    if ( ! ( WebserviceSpectralStorageAcceptImportScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML.equals( scanFilenameSuffix ) 
		    		||  WebserviceSpectralStorageAcceptImportScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML.equals( scanFilenameSuffix ) ) ) {

				log.warn( "Filename suffix provided in parameter '"
						+ WebserviceSpectralStorageAcceptImportQueryParamsConstants.UPLOAD_SCAN_FILE_SERVLET_QUERY_PARAM_SCAN_FILENAME_SUFFIX
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

						throw new FailResponseSentException();
						
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

						throw new FailResponseSentException();
					} else {
					
						String msg = "validationResponse is not an expected value.  is: " + validationResponse;
						log.error( msg );
						throw new SpectralFileFileUploadInternalException( msg );
					}
				}
			}

		//   AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.

			if ( StringUtils.isNotEmpty( ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getS3Bucket_InputScanFileStorage() ) ) {
				
				//  Save uploaded scan file to S3 Object. Returns a response to client if fail
				saveUploadedScanFileToS3Object( request, response, scanFilenameToProcess, uploadScanFileTempKey_Dir );

			} else {
			
				//  Save uploaded scan file to local disk file. Returns a response to client if fail
				saveUploadedScanFileToLocalDiskFile( request, response, scanFilenameToProcess, uploadScanFileTempKey_Dir );
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
	

//  AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.

	/**
	 * @param request
	 * @param response
	 * @param scanFilenameToProcess
	 * @param uploadScanFileTempKey_Dir
	 * @throws Exception 
	 */
	private void saveUploadedScanFileToS3Object(
			HttpServletRequest request, 
			HttpServletResponse response,
			String scanFilenameToProcess, 
			File uploadScanFileTempKey_Dir ) throws Exception {
		

		//  Compute the API key on the fly as the scan file data comes in: 
		Compute_Hashes compute_Hashes = Compute_Hashes.getNewInstance();
		
		final String bucketName = ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getS3Bucket_InputScanFileStorage();
		final String s3_region = ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getS3Region_InputScanFileStorage();
		
		String uploadScanFileTempKey_Dir_Name = uploadScanFileTempKey_Dir.getName();
		
		String s3_Object_Key = 
				Create_S3_Object_Paths.getInstance()
				.get_ScanFile_Uploaded_S3ObjectPath( uploadScanFileTempKey_Dir_Name, scanFilenameToProcess );

		//  Write a file to uploadScanFileTempKey_Dir with info on file to be written to S3
		{
			UploadScanfileS3Location uploadScanfileS3Location = new UploadScanfileS3Location();
			uploadScanfileS3Location.setScanFilenameToProcess( scanFilenameToProcess );
			uploadScanfileS3Location.setS3_bucketName( bucketName );
			uploadScanfileS3Location.setS3_objectName( s3_Object_Key );
			uploadScanfileS3Location.setS3_region( s3_region );
			
			JAXBContext jaxbContext = JAXBContext.newInstance( UploadScanfileS3Location.class );
			Marshaller marshaller = jaxbContext.createMarshaller();
		
			File scanfileS3InfoFile = new File( uploadScanFileTempKey_Dir, UploadProcessing_InputScanfileS3InfoConstants.SCANFILE_S3_LOCATION_FILENAME );
			
			try ( OutputStream os = new FileOutputStream( scanfileS3InfoFile ) ) {
				marshaller.marshal( uploadScanfileS3Location, os );
			} catch (Exception e ) {
				String msg = "Failed to write uploadScanfileS3Location to scanfileS3InfoFile: " + scanfileS3InfoFile.getAbsolutePath();
				log.error( msg, e );
				throw new SpectralFileWebappInternalException( msg, e );
			}
		}		
		
		//  Transfer the file from the stream to an S3 object
		final AmazonS3 amazonS3 = 
				S3_AWS_InterfaceObjectHolder.getSingletonInstance().getS3_Client_PassInOptionalRegion( s3_region );

		byte[] uploadPartByteBuffer = new byte[ S3_MULIPART_UPLOAD_PART_SIZE ];

		int partNumber = 0; // Must start at 1, incremented at top of loop, max of 10,000
		int bytesRead = 0;
		
		try ( InputStream scanDataFileOnDiskIS = request.getInputStream() ) {

	        // Create a list of UploadPartResponse objects. You get one of these
	        // for each part upload.
	        List<PartETag> partETags = new ArrayList<>( 10001 ); // Init to max possible size

	    	InitiateMultipartUploadResult initResponse = null;
	    	
    		int uploadInitToS3_RetryCounter = 0;
    		
    		while ( true ) {
    			try {
    				// Step 1: Initialize.
    				InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest( bucketName, s3_Object_Key );
    				initResponse = amazonS3.initiateMultipartUpload(initRequest);

    				break; //  Exit while( true ) since S3 call succeeded

    			} catch ( Exception e ) {
    				uploadInitToS3_RetryCounter++;
    				if ( uploadInitToS3_RetryCounter > UPLOAD_SCAN_FILE_INIT_UPLOAD_TO_S3_RETRY_COUNT_MAX ) {
    					throw e;
    				}
    				Thread.sleep( UPLOAD_SCAN_FILE_INIT_UPLOAD_TO_S3_RETRY_DELAY );
    			}
    		}
    		
        	String uploadId = initResponse.getUploadId();
        
	        try {
	        	//  Step 2:  Upload parts.
	        	while ( ( bytesRead = populateBufferFromScanDataFile( scanDataFileOnDiskIS, uploadPartByteBuffer ) ) > 0 ) {

					compute_Hashes.updateHashesComputing( uploadPartByteBuffer, bytesRead );
					
	        		partNumber++;
	        		boolean lastPart = false;
	        		if ( bytesRead < uploadPartByteBuffer.length ) { // uploadPartByteBuffer not full so is at end of file
	        			lastPart = true;
	        		}
	        		
	        		int uploadPartToS3_RetryCounter = 0;
	        		
	        		while ( true ) {
	        			try {
	        				ByteArrayInputStream scanFilePartIS = new ByteArrayInputStream( uploadPartByteBuffer, 0 /* offset */, bytesRead /* length */ );
	        				UploadPartRequest uploadRequest = 
	        						new UploadPartRequest().withUploadId( uploadId )
	        						.withBucketName( bucketName )
	        						.withKey( s3_Object_Key )
	        						.withInputStream( scanFilePartIS )
	        						.withPartNumber( partNumber )
	        						.withPartSize( bytesRead )
	        						.withLastPart( lastPart );

	        				//   Consider computing MD5 on scanFilePartIS and add to uploadRequest
	        				//       S3 uses that for an integrity check

	        				UploadPartResult result =  amazonS3.uploadPart( uploadRequest );
	        				PartETag partETag = result.getPartETag();
	        				partETags.add( partETag );
	        				
	        				break; //  Exit while( true ) since S3 call succeeded

	        			} catch ( Exception e ) {
	        				uploadPartToS3_RetryCounter++;
	        				if ( uploadPartToS3_RetryCounter > UPLOAD_SCAN_FILE_PART_TO_S3_RETRY_COUNT_MAX ) {
	        					throw e;
	        				}
	        				Thread.sleep( UPLOAD_SCAN_FILE_PART_TO_S3_RETRY_DELAY );
	        			}
	        		}
	        		
	        		if ( bytesRead < uploadPartByteBuffer.length ) { // uploadPartByteBuffer not full so is at end of file
	        			break; // exit loop since at last part
	        		}
	        	}

        		int uploadPartToS3_RetryCounter = 0;
        		
        		while ( true ) {
        			try {
        				// Step 3: Complete.
        				CompleteMultipartUploadRequest compRequest = new 
        						CompleteMultipartUploadRequest(
        								bucketName, 
        								s3_Object_Key, 
        								uploadId,
        								partETags);

        				amazonS3.completeMultipartUpload( compRequest );

        				break; //  Exit while( true ) since S3 call succeeded

        			} catch ( Exception e ) {
        				uploadPartToS3_RetryCounter++;
        				if ( uploadPartToS3_RetryCounter > UPLOAD_SCAN_FILE_COMPLETE_UPLOAD_TO_S3_RETRY_COUNT_MAX ) {
        					throw e;
        				}
        				Thread.sleep( UPLOAD_SCAN_FILE_COMPLETE_UPLOAD_TO_S3_RETRY_DELAY );
        			}
        		}
	        } catch (Exception e) {
	        	log.error( "Exception transfering uploaded Scan file from request.inputstream to S3. amazonS3.abortMultipartUpload(...) will be called next " );
	        	amazonS3.abortMultipartUpload( new AbortMultipartUploadRequest( bucketName, s3_Object_Key, uploadId ) );
	        	throw e;
	        }
		}
		

		writeAPIKeyToFile( compute_Hashes, uploadScanFileTempKey_Dir );
	}
	
	/**
	 * @param scanDataFileIS
	 * @param uploadPartByteBuffer
	 * @return number of bytes read into uploadPartByteBuffer.  If < uploadPartByteBuffer.length, at last buffer for file
	 * @throws IOException 
	 */
	private int populateBufferFromScanDataFile( InputStream scanDataFileIS, byte[] uploadPartByteBuffer ) throws IOException {
		
		int byteBufferLength = uploadPartByteBuffer.length;
		
		int bytesRead = 0;
		int byteBufferIndex = 0;
		
		while ( ( bytesRead = 
				scanDataFileIS.read( uploadPartByteBuffer, byteBufferIndex, byteBufferLength - byteBufferIndex) ) != -1 ) {
			byteBufferIndex += bytesRead;
			if ( byteBufferIndex >= byteBufferLength ) {
				break;
			}
		}
		
		return byteBufferIndex;
	}
	
	/**
	 * @param request
	 * @param response
	 * @param scanFilenameToProcess
	 * @param uploadScanFileTempKey_Dir
	 * @throws Exception 
	 */
	private void saveUploadedScanFileToLocalDiskFile(HttpServletRequest request, HttpServletResponse response,
			String scanFilenameToProcess, File uploadScanFileTempKey_Dir) throws Exception {
		
		//  Compute the API key on the fly as the scan file data comes in: 
		Compute_Hashes compute_Hashes = Compute_Hashes.getNewInstance();
		
		//  File to write the incoming data to:
		File uploadedFileOnDisk = new File( uploadScanFileTempKey_Dir, scanFilenameToProcess );
				
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
				
				compute_Hashes.updateHashesComputing( byteBuffer, bytesRead );
				
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
		
		writeAPIKeyToFile( compute_Hashes, uploadScanFileTempKey_Dir );
	}
	
	/**
	 * @param compute_Hashes
	 * @param uploadScanFileTempKey_Dir
	 * @throws Exception 
	 */
	private void writeAPIKeyToFile( Compute_Hashes compute_Hashes, File uploadScanFileTempKey_Dir ) throws Exception {
		
		String apiKey_String = 
				ScanFileAPIKey_ComputeFromScanFileContentHashes.getInstance()
				.scanFileAPIKey_ComputeFromScanFileContentHashes( compute_Hashes );
		
		ScanFileAPIKey_ToFileReadWrite.getInstance().writeScanFileHashToInProcessFileInDir( apiKey_String, uploadScanFileTempKey_Dir );
	}
	
	
	
	/**
	 * 
	 *
	 */
	private static class FailResponseSentException extends Exception {

		private static final long serialVersionUID = 1L;
	}
}
