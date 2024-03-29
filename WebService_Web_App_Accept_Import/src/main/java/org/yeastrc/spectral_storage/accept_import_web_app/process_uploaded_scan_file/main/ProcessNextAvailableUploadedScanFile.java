package org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.UploadProcessingStatusFileConstants;
import org.yeastrc.spectral_storage.accept_import_web_app.import_processing_status_file__read_write.UploadProcessingReadStatusFile;
import org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main.ProcessNextUploadedScanFile.ProcessingSuccessFailKilled;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.shared_server_importer.create__xml_input_factory__xxe_safe.Create_XMLInputFactory_XXE_Safe;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessing_InputScanfileS3InfoConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.s3_aws_interface.S3_AWS_InterfaceObjectHolder;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_api_key_processing.ScanFileAPIKey_ToFileReadWrite;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.upload_scanfile_s3_location.UploadScanfileS3Location;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;

/**
 * Pro
 *
 */
public class ProcessNextAvailableUploadedScanFile {

	private static final Logger log = LoggerFactory.getLogger(ProcessNextAvailableUploadedScanFile.class);

	private static final int RETRY_DELETE_SCAN_FILE_MAX = 10;
	private static final int RETRY_DELETE_SCAN_FILE_DELAY = 500;  // milliseconds

	/**
	 * NOT USED
	 * 
	 * complete file must be created within this time limit of now.
	 */
//	private static final long COMPLETE_FILE_CREATE_TIME_LIMIT_SECONDS = 30;  
	
//	private static enum ProcessUploadedFilesState {
//		
//		IDLE, PROCESSING
//	}
	
	
	private volatile ProcessNextUploadedScanFile processNextUploadedScanFile = null;
	
//	private volatile ProcessUploadedFilesState processUploadedFilesState = ProcessUploadedFilesState.IDLE;

//	private volatile boolean keepRunning = true;
	

	private ProcessNextAvailableUploadedScanFile() { }
	public static ProcessNextAvailableUploadedScanFile getInstance() { 
		return new ProcessNextAvailableUploadedScanFile(); 
	}
	

	/**
	 * awaken thread to process request, calls "notify()"
	 */
	public void awaken() {

		if ( log.isDebugEnabled() ) {
			log.debug("awaken() called:  " );
		}

		synchronized (this) {
			notify();
		}
	}

	/**
	 * shutdown was received from the operating system.  This is called on a different thread.
	 */
	public void shutdown() {
		log.info("shutdown() called");
//		synchronized (this) {
//			this.keepRunning = false;
//		}
		
		if ( processNextUploadedScanFile != null ) {
			processNextUploadedScanFile.shutdown();
		}
		//  awaken this thread if it is in 'wait' state ( not currently processing a job )
		this.awaken();
	}
	

	
	/**
	 * Process the next uploaded scan file that has not been fully processed
	 * 
	 * @return true if process a scan file (success or fail or killed)
	 */
	public boolean processNextAvailableUploadedScanFile() {

		String scanFileDirString = null;
		
		try {
			//  Process next Scan file

//			ConfigData_Directories_ProcessUploadInfo_InWorkDirectory configData_Directories_ProcessUploadInfo_InWorkDirectory = ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance();

			//  null if not local file system (is probably s3 then)
//			File scanFileStorageBaseDir = 
//					configData_Directories_ProcessUploadInfo_InWorkDirectory.getScanStorageBaseDirectory();

			File scanFileDir = null;
			try {
				scanFileDir = GetNextScanFileDirToProcessForStatus.getInstance().getNextScanFileDirToProcessForStatus( UploadProcessingStatusFileConstants.STATUS_PENDING );

			} catch ( Exception e ) {
				log.error( "Exception getting next Scan file to process", e );
				throw e;
			}

			if ( scanFileDir == null ) {

				return false;   //  EARLY LOOP EXIT

			} else {
				
				if ( log.isInfoEnabled() ) {
					log.info( "processNextAvailableUploadedScanFile(): START Processing Scan File in Directory [calling processNextUploadedScanFile(...): " + scanFileDir.getAbsolutePath() );
				}
				
				scanFileDirString = scanFileDir.getAbsolutePath();
				
				ProcessingSuccessFailKilled processingSuccessFailKilled_Result = null;
				
				try {

					processNextUploadedScanFile = ProcessNextUploadedScanFile.getInstance();

					//				processUploadedFilesState = ProcessUploadedFilesState.PROCESSING;

					processingSuccessFailKilled_Result = processNextUploadedScanFile.processNextUploadedScanFile( scanFileDir );

				} catch ( Throwable e ) {
					
					//  Do Move here since rethrow this exception
					MoveProcessingDirectoryToOneof_Processed_Directories.getInstance().moveProcessingDirectoryToOneof_Processed_Directories( scanFileDir, ProcessingSuccessFailKilled.FAIL );
					
					throw e;
				}

				

				String scanFileHashKey_API_Key = null;
				
				if ( processingSuccessFailKilled_Result == ProcessingSuccessFailKilled.SUCCESS ) {
						
					try {
						scanFileHashKey_API_Key =
								ScanFileAPIKey_ToFileReadWrite.getInstance()
								.readScanFileHashFromInProcessFile( scanFileDir );
					} catch ( Exception e ) {
						String msg = "Failed Call to ScanFileAPIKey_ToFileReadWrite.getInstance(). readScanFileHashFromInProcessFile( scanFileDir ); scanFileDir: " + scanFileDir.getAbsolutePath();
						log.error( msg );
					}
				}
				
				//  This next commented out code is not usable as is.  
				//  It Has ISSUES since the import is already flagged successful by the Importer Program.
				//   Would need a plan to change to failed and accept web app may have already returned success to calling application.
				//   Also, at this point the files are out in the storage directory.

//				if ( processingSuccessFailKilled_Result == ProcessingSuccessFailKilled.SUCCESS ) {
//					
//					if ( scanFileStorageBaseDir != null ) {
//
//						long completeFileLastModified = 0;
//						
//						File dataIndexSpectralFilesCompleteFile = null;
//						
//						try {
//
//							String dataFilename =
//									CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Data_Filename( scanFileHashKey_API_Key );
//
//							String dataIndexSpectralFilesCompleteFilename =
//									CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Data_Index_Files_Complete_Filename( scanFileHashKey_API_Key );
//
//							File subDir =
//									GetOrCreateSpectralStorageSubPath.getInstance()
//									.createDirsForHashIfNotExists( scanFileHashKey_API_Key, scanFileStorageBaseDir );
//
//							dataIndexSpectralFilesCompleteFile = new File( subDir, dataIndexSpectralFilesCompleteFilename );
//
//							completeFileLastModified = dataIndexSpectralFilesCompleteFile.lastModified();
//
//
//						} catch ( Exception e ) {
//							String msg = "Failed Call to ScanFileAPIKey_ToFileReadWrite.getInstance(). readScanFileHashFromInProcessFile( scanFileDir ); scanFileDir: " + scanFileDir.getAbsolutePath();
//							log.error( msg );
//
//							processingSuccessFailKilled_Result = ProcessingSuccessFailKilled.FAIL;
//
//							//  Do in code further down since not rethrow this exception
//							
////							MoveProcessingDirectoryToOneof_Processed_Directories.getInstance().moveProcessingDirectoryToOneof_Processed_Directories( scanFileDir, ProcessingSuccessFailKilled.FAIL );
//						}
//
//						if ( processingSuccessFailKilled_Result == ProcessingSuccessFailKilled.SUCCESS ) {
//							
//							long lastModified_NotBefore = System.currentTimeMillis() - ( COMPLETE_FILE_CREATE_TIME_LIMIT_SECONDS * 1000 );
//
//							log.error( "lastModified_NotBefore: " + lastModified_NotBefore + ", completeFileLastModified: " + completeFileLastModified );
//							
//							if ( completeFileLastModified < lastModified_NotBefore ) {
//								long created_MilliSecondsAgo = ( System.currentTimeMillis() - completeFileLastModified );
//								String msg = "Complete file created more than '" + COMPLETE_FILE_CREATE_TIME_LIMIT_SECONDS + "' ago.  created " 
//										+ created_MilliSecondsAgo + " milliseconds ago. Complete file: " + dataIndexSpectralFilesCompleteFile.getAbsolutePath();
//								log.error( msg );
//
//								processingSuccessFailKilled_Result = ProcessingSuccessFailKilled.FAIL;
//
//								MoveProcessingDirectoryToOneof_Processed_Directories.getInstance().moveProcessingDirectoryToOneof_Processed_Directories( scanFileDir, ProcessingSuccessFailKilled.FAIL );
//							}
//						}
//					}
//				}
				
				
				if ( processingSuccessFailKilled_Result != ProcessingSuccessFailKilled.KILLED ) {
					
					//  Only move if not Killed
				
					//  Move Processing directory to 'after processing' directory under base directory  

					MoveProcessingDirectoryToOneof_Processed_Directories.getInstance().moveProcessingDirectoryToOneof_Processed_Directories( scanFileDir, processingSuccessFailKilled_Result );
				}
				
				if ( log.isInfoEnabled() ) {
					log.info( "processNextAvailableUploadedScanFile(): END Processing Scan File in Directory [after calling processNextUploadedScanFile(...): " + scanFileDir.getAbsolutePath() );
				}
				
				if ( processingSuccessFailKilled_Result == ProcessingSuccessFailKilled.SUCCESS && StringUtils.isNotEmpty( scanFileHashKey_API_Key ) ) {
					
					if ( log.isInfoEnabled() ) {
						log.info( "processNextAvailableUploadedScanFile(): Processing Scan File in Directory Successful so update other Pending processing dirs with same scan file key to Success: " + scanFileDir.getAbsolutePath() );
					}
					
					if ( StringUtils.isNotEmpty( scanFileHashKey_API_Key ) ) {
					
						updateOtherProcessingDirsPendingToSuccessForSameAPIKey( scanFileHashKey_API_Key );
					}
				}
				
			}

		} catch ( Throwable e ) {
			
			String scanFileDirInfo = "";
			if ( scanFileDirString != null ) {
				scanFileDirInfo = ", scanFileDir: " + scanFileDirString;
			}

			log.error( "Exception Processing Scan file" + scanFileDirInfo, e );

			synchronized (this) {
				try {
					wait( 10000 );  //  sleep 10 seconds so don't quickly and repeatedly generate system errors.
				} catch (InterruptedException e2) {
					log.info("wait() interrupted with InterruptedException");
				}
			}

			//	throw e;

		} finally {
			processNextUploadedScanFile = null;

//			processUploadedFilesState = ProcessUploadedFilesState.IDLE;
		}

		return true;
	}
	


	/**
	 * @param scanFileHashKey_API_Key
	 * @throws Exception
	 */
	private void updateOtherProcessingDirsPendingToSuccessForSameAPIKey( String scanFileHashKey_API_Key ) throws Exception {
		
		//  Processing is successful
		
		//  Update other processing subdirectories with same hash key / API key to Success since the API Key is now in Spectr
		
		File tempScanUploadBaseDirectoryFile =
				ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getTempScanUploadBaseDirectory();
		
		//  Get the File object for the Base Subdir used to store the scan file for processing 
		String scanFilesToProcessBaseDirString = ScanFileToProcessConstants.SCAN_FILES_TO_PROCESS_BASE_DIR;
		
		File scanFilesToProcessBaseDir = new File( tempScanUploadBaseDirectoryFile, scanFilesToProcessBaseDirString );
		if ( ! scanFilesToProcessBaseDir.exists() ) {
			//  Scan Files Uploaded Base Dir does not exist.  Nothing uploaded yet
			return;  //  EARLY EXIT
		}
		
		//  Get oldest directory with a scan file to process 
		
		File[] scanFilesToProcessBaseDirContents = scanFilesToProcessBaseDir.listFiles();
		
		//  sort oldest to newest
		Arrays.sort( scanFilesToProcessBaseDirContents, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				if (  o1.lastModified() < o2.lastModified() ) {
					return -1;
				}
				if (  o1.lastModified() > o2.lastModified() ) {
					return 1;
				}
				return 0;
			}
		});
		
		for ( File scanFileDir : scanFilesToProcessBaseDirContents ) {
			//  Process each Subdir Scan File Processing dir
			processProcessDir( scanFileHashKey_API_Key, scanFileDir );
		}
		
	}
	
	
	/**
	 * @param scanFilesToProcessBaseDirContent
	 * @param statusValueToFind
	 * @return
	 * @throws Exception 
	 */
	private void processProcessDir( String scanFileHashKey_API_Key_InSuccessProcessedDir, File scanFileDir ) throws Exception {
		
		if ( ! scanFileDir.isDirectory() ) {
			//  Must be a directory
			return;  //  EARLY EXIT
		}
		
		String status =
				UploadProcessingReadStatusFile.getInstance().uploadProcessingReadStatusFile( scanFileDir );
		
		if ( status == null ) {
			//  No status file
			return;  //  EARLY EXIT
		}
		
		if ( ! UploadProcessingStatusFileConstants.STATUS_PENDING.equals( status ) ) {
			//  status is NOT pending so return
			return;  //  EARLY EXIT
		}
		

		String scanFileHashKey_API_Key_InCurrent_scanFileDir =
				ScanFileAPIKey_ToFileReadWrite.getInstance()
				.readScanFileHashFromInProcessFile( scanFileDir );
		
		if ( ! scanFileHashKey_API_Key_InSuccessProcessedDir.equals( scanFileHashKey_API_Key_InCurrent_scanFileDir ) ) {
			//  Not same API Key or no API key in current dir
			return;  //  EARLY EXIT
		}

		//  Update other processing subdirectories with same hash key / API key to Success since the API Key is now in Spectr

		ProcessUploadedScanFile_Final_OnSuccess.getInstance()
		.processUploadedScanFile_Final_OnSuccess( scanFileHashKey_API_Key_InCurrent_scanFileDir, scanFileDir );
		

		//  Move Processing directory to 'after processing' directory under base directory  
	
		MoveProcessingDirectoryToOneof_Processed_Directories.getInstance()
		.moveProcessingDirectoryToOneof_Processed_Directories( scanFileDir, ProcessingSuccessFailKilled.SUCCESS );
		
		
		//   Delete Scan File:  Local file or on AWS S3

		//  First delete uploaded scan file in S3 bucket if exists
		deleteUploadedScanFileIn_S3_Object(scanFileDir);
		
		cleanupInputScanFile(scanFileDir);
		
	}

	
	/**
	 * @param scanFileDir
	 */
	private void cleanupInputScanFile( File scanFileDir ) {

		File scanFile = null;
		
		File[] dirContents = scanFileDir.listFiles();
		
		for ( File dirEntry : dirContents ) {
			String dirEntryFilename = dirEntry.getName();
			if ( dirEntryFilename.startsWith( ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX )
					&& ( dirEntryFilename.endsWith( ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML ) 
							|| dirEntryFilename.endsWith( ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML ) ) ) {
				
				scanFile = dirEntry;
				
				break; // EARLY EXIT
			}
		}
		
	
		if ( ! scanFile.delete() ) {
			log.error( "Failed to delete input scan file: " + scanFileDir.getAbsolutePath() );
		} else {
			log.info( "Deleted input scan file: " + scanFileDir.getAbsolutePath() );
		}
		
	}

	/**
	 * @throws Exception
	 */
	private void deleteUploadedScanFileIn_S3_Object(File scanFileDir) throws Exception {
		

		File scanFile_S3_LocationFile = new File( scanFileDir, UploadProcessing_InputScanfileS3InfoConstants.SCANFILE_S3_LOCATION_FILENAME );
		
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
				String msg = "Failed to deserialize data in " + scanFile_S3_LocationFile.getAbsolutePath();
				log.error( msg );
				throw new SpectralStorageProcessingException(msg);
			}
		}

		if ( uploadScanfileS3Location.isS3_infoFrom_RemoteSystem() ) {
			//  Scan File S3 object came from external system so that system is responsible for deleting it
			return;  //  EARLY EXIT
		}
		
		if ( uploadScanfileS3Location.isS3_infoFrom_RemoteSystem() ) {
			//  Scan File S3 object came from external system so that system is responsible for deleting it
			return;  //  EARLY EXIT
		}
		
		String s3_bucketName = uploadScanfileS3Location.getS3_bucketName();
		String s3_objectName = uploadScanfileS3Location.getS3_objectName();
		
		final AmazonS3 amazonS3client = 
				S3_AWS_InterfaceObjectHolder.getSingletonInstance()
				.getS3_Client_PassInOptionalRegion(uploadScanfileS3Location.getS3_region());
		
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
