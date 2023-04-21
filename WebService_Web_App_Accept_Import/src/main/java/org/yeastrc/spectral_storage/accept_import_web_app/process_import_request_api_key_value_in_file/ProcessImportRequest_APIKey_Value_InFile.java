package org.yeastrc.spectral_storage.accept_import_web_app.process_import_request_api_key_value_in_file;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.background_thread.A_BackgroundThreads_Containers_Manager;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.UploadProcessingStatusFileConstants;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileFileUploadInternalException;
import org.yeastrc.spectral_storage.accept_import_web_app.import_processing_status_file__read_write.UploadProcessingWriteOrUpdateStatusFile;
import org.yeastrc.spectral_storage.accept_import_web_app.import_scan_filename_local_disk.ImportScanFilename_LocalDisk;
import org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main.MoveProcessingDirectoryToOneof_Processed_Directories;
import org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main.ProcessUploadedScanFile_Final_OnSuccess;
import org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main.ProcessNextUploadedScanFile.ProcessingSuccessFailKilled;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.shared_server_importer.create__xml_input_factory__xxe_safe.Create_XMLInputFactory_XXE_Safe;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.check_if_spectral_file_exists_and_is_latest_version.CheckIfSpectralFileAlreadyExists_And_IsLatestVersion__S3_Object;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.check_if_spectral_file_exists_and_is_latest_version.CheckIfSpectralFile_AlreadyExists_And_IsLatestVersion__LocalFilesystem;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessing_InputScanfileS3InfoConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.s3_aws_interface.S3_AWS_InterfaceObjectHolder;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_api_key_processing.ScanFileAPIKey_ToFileReadWrite;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3_Holder;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.upload_scanfile_s3_location.UploadScanfileS3Location;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;

/**
 * Process the Import Request once the API Key value has been computed and stored in the file.
 * This is executed on the "Import Processing" directory, after the import has been submitted and the API Key has been computed.
 * 
 * This will check if the API Key already exists.
 *    1) If Yes, then sets values in Processing dir that it is complete.
 *    2) If No, it triggers the code that runs the Importer.
 * 
 * This is called from Submit Servlet when the API Key is computed as the Scan file contents are received.
 * 
 * This is called from XXX after the API Key has computed from the Scan File.  This is used when the scan file location is sent (disk or S3).
 *
 */
public class ProcessImportRequest_APIKey_Value_InFile {

	private static final Logger log = LoggerFactory.getLogger( ProcessImportRequest_APIKey_Value_InFile.class );

	private static final int RETRY_DELETE_SCAN_FILE_MAX = 10;
	private static final int RETRY_DELETE_SCAN_FILE_DELAY = 500;  // milliseconds

	//  private constructor
	private ProcessImportRequest_APIKey_Value_InFile() { }
	
	/**
	 * @return newly created instance
	 */
	public static ProcessImportRequest_APIKey_Value_InFile getInstance() { 
		return new ProcessImportRequest_APIKey_Value_InFile(); 
	}
	
	/**
	 * @param dirToProcessScanFile
	 * @throws Exception
	 */
	public void processImportRequest_APIKey_Value_InFile( File dirToProcessScanFile ) throws Exception {
		
		String apiKey = ScanFileAPIKey_ToFileReadWrite.getInstance().readScanFileHashFromInProcessFile( dirToProcessScanFile );
		
		if ( StringUtils.isEmpty( apiKey ) ) {
			String msg = "apiKey is empty. dirToProcessScanFile: " + dirToProcessScanFile.getAbsolutePath();
			log.error( msg );
			throw new SpectralStorageProcessingException(msg);
		}
		
		CommonReader_File_And_S3 commonReader_File_And_S3 = CommonReader_File_And_S3_Holder.getSingletonInstance().getCommonReader_File_And_S3();
		
		//  If API Key already exists in Storage Dir, only set values in dirToProcessScanFile and exit

		if ( StringUtils.isNotEmpty( ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getS3Bucket() ) ) {
			
			if ( CheckIfSpectralFileAlreadyExists_And_IsLatestVersion__S3_Object.getInstance()
					.doesSpectralFileAlreadyExist( 
							ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getS3Bucket(), 
							commonReader_File_And_S3,
							apiKey ) ) {

				log.warn( "INFO: apiKey already in S3 and is Latest version.  Request will be marked Successful and Processing Dir Cleaned Up. apiKey: " + apiKey 
						+ ", S3 Bucket: " + ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getS3Bucket() );

				//  Need to do same things that Importer would do when found API key already existing
				ProcessUploadedScanFile_Final_OnSuccess.getInstance().processUploadedScanFile_Final_OnSuccess( apiKey, dirToProcessScanFile );

				if ( ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().isDeleteUploadedScanFileOnSuccessfulImport() ) {
					try {
						//  Don't get here if here is a failure since an exception will be thrown.  
						if ( ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().isDeleteUploadedScanFileOnSuccessfulImport() ) {

							cleanupInputScanFile_OnS3( dirToProcessScanFile );
						}
					} catch ( Exception e ) {
						String msg = "Error removing scan file when delete is true.  dirToProcessScanFile: " + dirToProcessScanFile.getAbsolutePath();
						log.error( msg );
						//  Eat this exception
					}
				}

				//  Move Processing directory to 'after processing' directory under base directory  
			
				MoveProcessingDirectoryToOneof_Processed_Directories.getInstance()
				.moveProcessingDirectoryToOneof_Processed_Directories( dirToProcessScanFile, ProcessingSuccessFailKilled.SUCCESS );
				
				// API Key already exists in Storage Dir, only set values in dirToProcessScanFile and exit

				return;  //  EARLY RETURN
			}

		} else {
			File scanStorageBaseDirectory = ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getScanStorageBaseDirectory();
			
			if ( CheckIfSpectralFile_AlreadyExists_And_IsLatestVersion__LocalFilesystem.getInstance()
					.doesSpectralFileAlreadyExist( 
							scanStorageBaseDirectory, 
							commonReader_File_And_S3,
							apiKey ) ) {
				
				log.warn( "INFO: apiKey already in Scan Data Storage directory and is Latest version.  Request will be marked Successful and Processing Dir Cleaned Up. apiKey: " + apiKey 
						+ ", Scan File Processing Directory: " + dirToProcessScanFile.getAbsolutePath() );
				
				//  Need to do same things that Importer would do when found API key already existing
				ProcessUploadedScanFile_Final_OnSuccess.getInstance().processUploadedScanFile_Final_OnSuccess( apiKey, dirToProcessScanFile );

				if ( ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().isDeleteUploadedScanFileOnSuccessfulImport() ) {
					try {
						//  Don't get here if here is a failure since an exception will be thrown.  
						if ( ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().isDeleteUploadedScanFileOnSuccessfulImport() ) {
							
							cleanupInputScanFile( dirToProcessScanFile );
						}
					} catch ( Exception e ) {
						String msg = "Error removing scan file when delete is true.  dirToProcessScanFile: " + dirToProcessScanFile.getAbsolutePath();
						log.error( msg );
						//  Eat this exception
					}
				}

				//  Move Processing directory to 'after processing' directory under base directory  
			
				MoveProcessingDirectoryToOneof_Processed_Directories.getInstance()
				.moveProcessingDirectoryToOneof_Processed_Directories( dirToProcessScanFile, ProcessingSuccessFailKilled.SUCCESS );
				

//				System.out.println( "Data File already exists so no processing needed");
				
				// API Key already exists in Storage Dir, only set values in dirToProcessScanFile and exit

				return;  //  EARLY RETURN
			}
		}
		
		//  API Key NOT already exists in Storage Dir, process scan file 
		
		try {
			//  Create status file for pending
			UploadProcessingWriteOrUpdateStatusFile.getInstance()
			.uploadProcessingWriteOrUpdateStatusFile( 
					UploadProcessingStatusFileConstants.STATUS_PENDING, 
					dirToProcessScanFile,
					UploadProcessingStatusFileConstants.STATUS_PROCESSING_CALLER_LABEL__ACCEPT_IMPORT_WEBAPP );
		} catch ( Exception e ) {
			String msg = "Failed to create status file, dirToProcessScanFile: " + dirToProcessScanFile.getAbsolutePath();
			log.error( msg, e );
			throw new SpectralStorageProcessingException( msg, e );
		}
				
		//  Awaken the thread that will process the process scan file directory after determining that the Process Scan File process needs to be run
		A_BackgroundThreads_Containers_Manager.getSingletonInstance().getProcessScanFile_Thread_Container().awakenToProcessAScanFile();
	}

	/**
	 * @param dirToProcessScanFile
	 * @throws SpectralFileFileUploadInternalException 
	 */
	private void cleanupInputScanFile( File dirToProcessScanFile ) throws SpectralFileFileUploadInternalException {

		//  Find the scan file 
		String importScanFilename = ImportScanFilename_LocalDisk.getInstance().getImportScanFilename_LocalDisk( dirToProcessScanFile );
		
		if ( importScanFilename == null ) {
			//  Scan file not found
			log.warn( "cleanupInputScanFile(...): Scan File not found for deletion in dir: " + dirToProcessScanFile.getAbsolutePath() );
			return; // EARLY RETURN
		}

		File inputScanFile = new File( dirToProcessScanFile, importScanFilename );
		
		if ( ! inputScanFile.delete() ) {
			log.error( "Failed to delete input scan file: " + inputScanFile.getAbsolutePath() );
		} else {
			if ( log.isInfoEnabled() ) {
				log.info( "Deleted input scan file: " + inputScanFile.getAbsolutePath() );
			}
			log.warn( "INFO:  Deleted input scan file: " + inputScanFile.getAbsolutePath() );
		}
		
	}
	

	/**
	 * @param dirToProcessScanFile
	 * @throws SpectralFileFileUploadInternalException 
	 * @throws Exception 
	 */
	private void cleanupInputScanFile_OnS3( File dirToProcessScanFile ) throws SpectralFileFileUploadInternalException, Exception {
		
		File scanFile_S3_LocationFile = new File( dirToProcessScanFile, UploadProcessing_InputScanfileS3InfoConstants.SCANFILE_S3_LOCATION_FILENAME );
		
		if ( ! scanFile_S3_LocationFile.exists() ) {
			//  No file with info on scan file location in S3, so must be local file
			return;  //  EARLY EXIT
		}
		
		UploadScanfileS3Location uploadScanfileS3Location = null;
		
		JAXBContext jaxbContext = JAXBContext.newInstance( UploadScanfileS3Location.class ); 
		
		try ( InputStream is = new FileInputStream( scanFile_S3_LocationFile ) ) {

			XMLInputFactory xmlInputFactory = Create_XMLInputFactory_XXE_Safe.create_XMLInputFactory_XXE_Safe();
			XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader( new StreamSource( is ) );
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			Object uploadScanfileS3LocationAsObject = unmarshaller.unmarshal( xmlStreamReader );

			if ( uploadScanfileS3LocationAsObject instanceof UploadScanfileS3Location ) {
				uploadScanfileS3Location = ( UploadScanfileS3Location ) uploadScanfileS3LocationAsObject;
			} else {
				String msg = "Failed to deserialize data for Scan File S3 Location in file: " + scanFile_S3_LocationFile.getAbsolutePath();
				log.error( msg );
				throw new SpectralStorageProcessingException(msg);
			}
		}

		if ( uploadScanfileS3Location.isS3_infoFrom_RemoteSystem() ) {
			//  Scan File S3 object came from external system so that system is responsible for deleting it
			return;  //  EARLY EXIT
		}
		
		String s3_bucketName = uploadScanfileS3Location.getS3_bucketName();
		String s3_objectName = uploadScanfileS3Location.getS3_objectName();
		
		final AmazonS3 amazonS3client = S3_AWS_InterfaceObjectHolder.getSingletonInstance().getS3_Client_PassInOptionalRegion(uploadScanfileS3Location.getS3_region());
		
		int retryDeleteScanFileCount = 0;
		
		while ( true ) {
			try {
				amazonS3client.deleteObject( new DeleteObjectRequest(
						uploadScanfileS3Location.getS3_bucketName(), uploadScanfileS3Location.getS3_objectName() ));

				break;  // Exit loop on success

			} catch (AmazonServiceException e) {
				String msg = "Error deleting scan file on S3. s3_bucketName: " + s3_bucketName
						+ " s3_objectName: " + s3_objectName;
				log.error( msg );

				retryDeleteScanFileCount++;
				if ( retryDeleteScanFileCount > RETRY_DELETE_SCAN_FILE_MAX ) {
					throw new SpectralStorageProcessingException(msg, e);
				}
			} catch (Exception e) {
				String msg = "Error deleting scan file on S3. s3_bucketName: " + s3_bucketName
						+ " s3_objectName: " + s3_objectName;
				log.error( msg );

				retryDeleteScanFileCount++;
				if ( retryDeleteScanFileCount > RETRY_DELETE_SCAN_FILE_MAX ) {
					throw new SpectralStorageProcessingException(msg, e);
				}
			}
			
			Thread.sleep( RETRY_DELETE_SCAN_FILE_DELAY );
		}

		// the same object name as scan file but with ".submitted" on the end
		String objectKey_SubmittedObject = uploadScanfileS3Location.getS3_objectName()
				+ ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_SUBMITTED_FILE_SUFFIX;
		

		int retryDeleteScanFileSubmittedCount = 0;

		while ( true ) {
			try {
				amazonS3client.deleteObject( new DeleteObjectRequest(
						uploadScanfileS3Location.getS3_bucketName(), objectKey_SubmittedObject ) );

				break;  // Exit loop on success

			} catch (AmazonServiceException e) {
				String msg = "Error deleting scan file on S3. s3_bucketName: " + s3_bucketName
						+ " s3_objectName: " + s3_objectName;
				log.error( msg );

				retryDeleteScanFileSubmittedCount++;
				if ( retryDeleteScanFileSubmittedCount > RETRY_DELETE_SCAN_FILE_MAX ) {
					throw new SpectralStorageProcessingException(msg, e);
				}
			} catch (Exception e) {
				String msg = "Error deleting scan file on S3. s3_bucketName: " + s3_bucketName
						+ " s3_objectName: " + s3_objectName;
				log.error( msg );

				retryDeleteScanFileSubmittedCount++;
				if ( retryDeleteScanFileSubmittedCount > RETRY_DELETE_SCAN_FILE_MAX ) {
					throw new SpectralStorageProcessingException(msg, e);
				}
			}

			Thread.sleep( RETRY_DELETE_SCAN_FILE_DELAY );
		}
	}
	
	
}
