package org.yeastrc.spectral_storage.scan_file_processor.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;


//  import com.amazonaws.services.s3.AmazonS3;
//  import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
//  import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
//  import com.amazonaws.services.s3.model.DeleteObjectRequest;
//  import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
//  import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
//  import com.amazonaws.services.s3.model.PartETag;
//  import com.amazonaws.services.s3.model.UploadPartRequest;
//  import com.amazonaws.services.s3.model.UploadPartResult;
//  import com.amazonaws.AmazonClientException;
//  import com.amazonaws.AmazonServiceException;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.constants_enums.ImporterTempSubDirNameConstants;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.Parse_ScanFile_Pass_ScanBatch_To_Processing_Thread;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.Parse_ScanFile_ScanBatch_Queue;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.Process_Scans_In_ScanBatch_From_ScanFileParser_Thread;
import org.yeastrc.spectral_storage.scan_file_processor.program.Scan_File_Processor_MainProgram_Params;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.check_if_spectral_file_exists_and_is_latest_version.CheckIfSpectralFile_AlreadyExists_And_IsLatestVersion__LocalFilesystem;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.SpectralStorage_Filename_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.file_contents_hash_processing.Compute_File_Hashes;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.file_contents_hash_processing.Compute_Hashes;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.file_contents_hash_processing.Compute_Hashes.Compute_Hashes_Result;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_api_key_processing.ScanFileAPIKey_ComputeFromScanFileContentHashes;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_api_key_processing.ScanFileAPIKey_ToFileReadWrite;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_Header_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3_Builder;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Writer_CurrentFormat_Factory;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Writer__IF;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Writer__NotifyOnProcessingCompleteOrException__IF;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.GetOrCreateSpectralStorageSubPath;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_999_latest.StorageFile_Version_999_LATEST_Constants;

/**
 * 
 *
 */
public class ProcessUploadedScanFileRequest implements SpectralFile_Writer__NotifyOnProcessingCompleteOrException__IF {

	private static final Logger log = LoggerFactory.getLogger(ProcessUploadedScanFileRequest.class);

//	private static final int RETRY_UPLOAD_SCAN_DATA_OTHER_FILES_MAX = 10;
//	private static final int RETRY_UPLOAD_SCAN_DATA_OTHER_FILES_DELAY = 500;  // milliseconds
//
//	private static final int RETRY_UPLOAD_SCAN_DATA_MAIN_FILE_INIT_MAX = 10;
//	private static final int RETRY_UPLOAD_SCAN_DATA_MAIN_FILE_INIT_DELAY = 500;  // milliseconds
//
//	private static final int RETRY_UPLOAD_SCAN_DATA_MAIN_FILE_UPLOAD_PART_MAX = 10;
//	private static final int RETRY_UPLOAD_SCAN_DATA_MAIN_FILE_UPLOAD_PART_DELAY = 500;  // milliseconds
//
//	private static final int RETRY_UPLOAD_SCAN_DATA_MAIN_FILE_COMPLETE_MAX = 10;
//	private static final int RETRY_UPLOAD_SCAN_DATA_MAIN_FILE_COMPLETE_DELAY = 500;  // milliseconds
//
//	private static final int RETRY_DELETE_SCAN_FILE_MAX = 10;
//	private static final int RETRY_DELETE_SCAN_FILE_DELAY = 500;  // milliseconds

	
	private static final ProcessUploadedScanFileRequest instance = new ProcessUploadedScanFileRequest();
	
	/**
	 * private constructor
	 */
	private ProcessUploadedScanFileRequest(){}
	public static ProcessUploadedScanFileRequest getSingletonInstance() {
		return instance;
	}
	
	/**
	 * Awaken from "wait()" call - Awaken the main thread of the Importer
	 */
	public void awaken() {

		synchronized (this) {

			notify();
		}
	}

	/* 
	 * Called when Processing is complete or there is an exception in the Write Spectral Storage Data Files
	 */
	@Override
	public void notifyOnProcessingCompleteOrException() {
		
		this.awaken();
	}

	/**
	 * @param pgmParams
	 * @throws Throwable 
	 */
	public void processUploadedScanFileRequest( Scan_File_Processor_MainProgram_Params pgmParams ) throws Throwable {

		// AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.
		
		//  If have file with info on scan file in S3, copy that scan file to local dir in standard scan filename
//		GetScanFileFrom_S3_IfHave_S3_Info_File.getInstance().getScanFileFrom_S3_IfHave_S3_Info_File();
		
		File inputScanFile = pgmParams.getInputScanFile();
		
		System.out.println( "Input scan File Absolute Path: " + inputScanFile.getAbsolutePath() );
		System.out.println( "Input scan File Canonical Path: " + inputScanFile.getCanonicalPath() );
				
		System.out.println( "Starting Compute hashes for scan file.  Now: " + new Date() );
		
		Compute_Hashes compute_Hashes =
				Compute_File_Hashes.getInstance().compute_File_Hashes( inputScanFile );
		
		System.out.println( "Finished Compute hashes for scan file.  Now: " + new Date() );

		System.out.println( "Processing scan file.  Now: " + new Date() );
		
		processInputFileWithComputedHash(
				pgmParams,
				compute_Hashes );

		//  Don't get here if here is a failure since an exception will be thrown.  
		if ( pgmParams.isDeleteScanFileOnSuccess() ) {
			
			System.out.println( "INFO: pgmParams.isDeleteScanFileOnSuccess() is true so removing input scan file  Now: " + new Date() );
			
			cleanupInputScanFile( inputScanFile );

			// AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.
			
//			deleteUploadedScanFileIn_S3_Object();
		}
	}
	
	/**
	 * @param pgmParams
	 * @param inputScanFile
	 * @param compute_Hashes
	 * @return
	 * @throws Throwable 
	 */
	public  String processInputFileWithComputedHash(
			Scan_File_Processor_MainProgram_Params pgmParams,
			Compute_Hashes compute_Hashes ) throws Throwable {

		//  String of the API Key for this scan file, based on the hash of the file contents
		String apiKey = 
				ScanFileAPIKey_ComputeFromScanFileContentHashes.getInstance()
				.scanFileAPIKey_ComputeFromScanFileContentHashes( compute_Hashes );
		
		System.out.println( "API Key (Computed from Hash): " + apiKey );
		
		ScanFileAPIKey_ToFileReadWrite.getInstance().writeScanFileHashToInProcessFileInCurrentDir( apiKey );
		

		CommonReader_File_And_S3_Builder commonReader_File_And_S3_Builder = CommonReader_File_And_S3_Builder.newBuilder();

		// AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.
				
//		if ( StringUtils.isNotEmpty( pgmParams.getS3_OutputBucket() ) ) {
//
//			commonReader_File_And_S3_Builder.setS3_Bucket( pgmParams.getS3_OutputBucket() );
//				//			commonReader_File_And_S3_Builder.setS3_Region( pgmParams.get );
//		
//		} else {
			commonReader_File_And_S3_Builder.setSubDirForStorageFiles( pgmParams.getOutputBaseDir() );
//		}
		
		CommonReader_File_And_S3 commonReader_File_And_S3 = commonReader_File_And_S3_Builder.build();

		// AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.
		
//		if ( StringUtils.isNotEmpty( pgmParams.getS3_OutputBucket() ) ) {
//			
//			if ( CheckIfSpectralFileAlreadyExists_And_IsLatestVersion__S3_Object.getInstance()
//					.doesSpectralFileAlreadyExist( 
//							pgmParams.getS3_OutputBucket(), 
//							commonReader_File_And_S3, 
//							apiKey ) ) {
//
//				System.out.println( "Data object in S3 already exists and is latest version so no processing needed");
//
//				return apiKey;
//			}
//
//		} else {
		if ( CheckIfSpectralFile_AlreadyExists_And_IsLatestVersion__LocalFilesystem.getInstance()
				.doesSpectralFileAlreadyExist( 
						pgmParams.getOutputBaseDir(), 
						commonReader_File_And_S3,
						apiKey ) ) {

			System.out.println( "Data File already exists and is latest version so no processing needed");

			return apiKey;
		}
//		}

			// AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.
			
//		if ( StringUtils.isNotEmpty( pgmParams.getS3_OutputBucket() ) ) {
//			System.out.println( "Scan Data File does NOT already exist in S3 or is NOT latest Version so STARTING processing the scan file.  Now: " + new Date() );
//		} else {
		System.out.println( "Scan Data File does NOT already exist on Local Filesystem or is NOT latest Version so STARTING processing the scan file.  Now: " + new Date() );
//		}

		{
			File scanFile = pgmParams.getInputScanFile();
			
			if ( ! scanFile.exists() ) {
				String msg = "Input scan file does not exist: " + scanFile.getAbsolutePath();
				log.error( msg );
				throw new IllegalArgumentException( msg );
			}
			
			System.out.println( "Starting Processing input file: "  + scanFile.getAbsolutePath() 
				+ ", Now: " + new Date() );
		}
		
		File tempOutputDir = null;
				
		if ( pgmParams.getTempOutputBaseDir() != null ) {
			
			//  Temp Output Dir specified in config for Accept Webapp so use it;
			
			tempOutputDir = pgmParams.getTempOutputBaseDir();
			
		} else {

			//  Temp Output Dir NOT specified in config for Accept Webapp SO Create/Use Subdir under  OutputBaseDir
			
			//  Create Temp dir if not exist
			
			tempOutputDir = new File( pgmParams.getOutputBaseDir(), ImporterTempSubDirNameConstants.IMPORTER_TEMP_SUBDIR_NAME );
			
			if ( ! tempOutputDir.exists() ) {
				if ( ! tempOutputDir.mkdir() ) {
					String msg = "Failed to make temp dir: " + tempOutputDir.getAbsolutePath();
					log.error( msg );
					throw new SpectralStorageProcessingException(msg);
				}
			}
		}
		
		// Empty temp output dir
		{
			File[] tempOutputDirItems = tempOutputDir.listFiles();
			for ( File tempOutputDirItem : tempOutputDirItems ) {
				if ( ! tempOutputDirItem.delete() ) {
					String msg = "Failed to delete temp dir item: " + tempOutputDirItem.getAbsolutePath();
					log.error( msg );
					throw new SpectralStorageProcessingException(msg);
				} else {
					String msg = "INFO: deleted temp dir item Before start writing to temp dir: " + tempOutputDirItem.getAbsolutePath();
					log.warn( msg );
				}
			}
		}
		
		//  Write the Spectral Storage Service data files to the temp dir for this Input Scan File
		
		SpectralFile_Writer__IF spectralFile_Writer = null;
		
		Parse_ScanFile_Pass_ScanBatch_To_Processing_Thread parse_ScanFile_Pass_ScanBatch_To_Processing_Thread = null;
		
		Process_Scans_In_ScanBatch_From_ScanFileParser_Thread process_Scans_In_ScanBatch_From_ScanFileParser_Thread = null;

		try {
			File scanFile = pgmParams.getInputScanFile();
					
			Compute_Hashes_Result compute_Hashes_Result = compute_Hashes.compute_Hashes();
			
			long scanFileLength_InBytes = scanFile.length();
			
			SpectralFile_Header_Common spectralFile_Header_Common = new SpectralFile_Header_Common();
			
			spectralFile_Header_Common.setScanFileLength_InBytes( scanFileLength_InBytes );
			spectralFile_Header_Common.setMainHash( compute_Hashes_Result.getSha_384_Hash() );
			spectralFile_Header_Common.setAltHashSHA512( compute_Hashes_Result.getSha_512_Hash() );
			spectralFile_Header_Common.setAltHashSHA1( compute_Hashes_Result.getSha_1_Hash() );
			
			
			//  spectralFile_Writer is the Writer for the Latest Version of the Spectral File Format
			
			spectralFile_Writer = SpectralFile_Writer_CurrentFormat_Factory.getInstance().getSpectralFile_Writer_LatestVersion();
			
			if ( StorageFile_Version_999_LATEST_Constants.FILE_VERSION != spectralFile_Writer.getVersion() ) {
				
				log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
				String msg = "ERROR: Call to SpectralFile_Writer_CurrentFormat_Factory.getInstance().getSpectralFile_Writer_LatestVersion() did not return latest version. StorageFile_Version_999_LATEST_Constants.FILE_VERSION != spectralFile_Writer.getVersion(). "
							+ " StorageFile_Version_999_LATEST_Constants.FILE_VERSION: " + StorageFile_Version_999_LATEST_Constants.FILE_VERSION
							+ "spectralFile_Writer.getVersion(): " + spectralFile_Writer.getVersion();
				log.error( msg );
				log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
				
				System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
				System.err.println( msg );
				System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
			}
			
			//   Open Output File: spectralFile_Writer is the Writer for the Latest Version of the Spectral File Format
			
			spectralFile_Writer.initialize( apiKey, tempOutputDir, spectralFile_Header_Common, this );
			
			Parse_ScanFile_ScanBatch_Queue parse_ScanFile_ScanBatch_Queue = Parse_ScanFile_ScanBatch_Queue.getNewInstance();
			
			process_Scans_In_ScanBatch_From_ScanFileParser_Thread = Process_Scans_In_ScanBatch_From_ScanFileParser_Thread.getNewInstance(pgmParams, parse_ScanFile_ScanBatch_Queue, spectralFile_Writer);
			
			process_Scans_In_ScanBatch_From_ScanFileParser_Thread.start();
			
			parse_ScanFile_Pass_ScanBatch_To_Processing_Thread = Parse_ScanFile_Pass_ScanBatch_To_Processing_Thread.getNewInstance(pgmParams, parse_ScanFile_ScanBatch_Queue);
			
			parse_ScanFile_Pass_ScanBatch_To_Processing_Thread.start();
			
			while (true) {  //  loop until 'break' inside the loop
			
				synchronized (this) {

					//  wait for Spectral Storage Files to be written to the temp dir for this Input Scan File

					wait( 2 * 60 * 1000 );  // Wait max of 2 minutes to ensure do not get stuck here

					//  TESTING
//					wait( 1000 );  // Wait max of 2 minutes to ensure do not get stuck here
					
					//  notify()  [ in method awaken() ] called in 3 circumstances:

					//    1)   The Write Spectral Storage Files completes successfully
					//    2)   The Write Spectral Storage Files gets an exception
					//    3)   The Parse Scan File thread gets an exception
				}


				//  wait() exit so evaluate parse_ScanFile_Pass_ScanBatch_To_Processing_Thread and spectralFile_Writer

				if ( spectralFile_Writer.isProcessingIs_Successfull_And_Complete() ) {

					//  The spectralFile_Writer has reached the "Close" main data file and completed all other processing successfully

					break; //  EARLY LOOP EXIT
				}

				if ( spectralFile_Writer.getException() != null ) {

					//  Write Spectral Storage Files has an exception

					log.error( "In Main Processing: Write Spectral Storage Files has an exception so rethrow exception", spectralFile_Writer.getException() );

					throw spectralFile_Writer.getException();
				}
				
				if ( parse_ScanFile_Pass_ScanBatch_To_Processing_Thread.getThrowable_Caught_Main_run_method() != null ) {

					//  Parse Scan File has an exception

					log.error( "In Main Processing: Parsing Scan File has an exception so rethrow exception", parse_ScanFile_Pass_ScanBatch_To_Processing_Thread.getThrowable_Caught_Main_run_method() );
					throw parse_ScanFile_Pass_ScanBatch_To_Processing_Thread.getThrowable_Caught_Main_run_method();
				}

				if ( process_Scans_In_ScanBatch_From_ScanFileParser_Thread.getThrowable_Caught_Main_run_method() != null ) {

					//  Parse Scan File has an exception

					log.error( "In Main Processing: Processing Scan Batch has an exception so rethrow exception", process_Scans_In_ScanBatch_From_ScanFileParser_Thread.getThrowable_Caught_Main_run_method() );
					throw process_Scans_In_ScanBatch_From_ScanFileParser_Thread.getThrowable_Caught_Main_run_method();
				}
				

			}
			
		} catch ( SpectralStorageDataException e ) {
			
			//  Data error so write messasge to data error file
			
			log.error( "Caught SpectralStorageDataException: " + e.getMessage(), e );
			processSpectralStorageDataException( e );
			throw e;
			
		} catch ( Throwable e) {

			log.error( "Failed to process scan file: " + pgmParams.getInputScanFile().getAbsolutePath(), e );
			throw e;
		} finally {

			try {
				parse_ScanFile_Pass_ScanBatch_To_Processing_Thread.close_ScanFile_Parser();
			} catch ( Throwable t ) {
				log.warn( "EXCEPTION IGNORED:: Exception closing Scan File Parser. ", t );
			}
		}


		// AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.
		
//		if ( StringUtils.isNotEmpty( pgmParams.getS3_OutputBucket() ) ) {
//			
//			//   Copy files in temp dir to S3 and remove from temp dir
//			copyFilesTo_S3_and_remove_from_temp_dir( pgmParams, tempOutputDir );
//
//		} else {
			//  Move files in temp dir to final output dir
			moveFilesToFinalSubdirLocalFilesytem( pgmParams, apiKey, tempOutputDir );
//		}
		
		System.out.println( "DONE Successfully processing the scan file.  Now: " + new Date() );
		
		return apiKey;
	}

	// AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.
	
//	/**
//	 * @param pgmParams
//	 * @param tempOutputDir
//	 * @throws Exception
//	 * @throws SpectralStorageProcessingException
//	 */
//	private void copyFilesTo_S3_and_remove_from_temp_dir(
//			Scan_File_Processor_MainProgram_Params pgmParams, 
//			File tempOutputDir) throws Exception, SpectralStorageProcessingException {
//
//		{
//			final AmazonS3 amazonS3 = S3_AWS_InterfaceObjectHolder.getSingletonInstance().getS3_Client_Output();
//
//			String dataFileObjectKey = null;
//			
////				This says  s3.putObject only handle up to 5GB
////				https://docs.aws.amazon.com/AmazonS3/latest/dev/UploadingObjects.html
//			
//			//  First copy all but complete file:
//			File[] tempOutputDirItems = tempOutputDir.listFiles();
//			for ( File tempOutputDirItem : tempOutputDirItems ) {
//				String filename = tempOutputDirItem.getName();
//				if ( ! filename.endsWith( SpectralStorage_Filename_Constants.DATA_INDEX_FILES_COMPLETE_FILENAME_SUFFIX ) ) {
//					String objectKey = SpectralStorage_DataFiles_S3_Prefix_Constants.S3_PREFIX_DATA_FILES + filename;
//
//					if ( filename.endsWith( SpectralStorage_Filename_Constants.DATA_FILENAME_SUFFIX ) ) {
//
//						// Use MultiPart Upload since scan file could be > 5GB 
//
//						dataFileObjectKey = objectKey;
//
//						copyScanDataFileTo_S3( tempOutputDirItem, objectKey, pgmParams.getS3_OutputBucket(), amazonS3 );
//
//					} else {
//						// Not scan file so use standard amazonS3.putObject(...)
//
//						int retryCount = 0;
//
//						while ( true ) {
//							try {
//								amazonS3.putObject( pgmParams.getS3_OutputBucket(), objectKey, tempOutputDirItem );
//
//								break;  // exit on success
//
//							} catch ( AmazonServiceException amazonServiceException ) {
//								retryCount++;
//								if ( retryCount > RETRY_UPLOAD_SCAN_DATA_OTHER_FILES_MAX ) {
//									String msg = "Failed to copy temp dir item to S3."
//											+ " Status Code: " + amazonServiceException.getStatusCode()
//											+ ", temp dir item: " 
//											+ tempOutputDirItem.getAbsolutePath();
//									log.error( msg, amazonServiceException );
//									throw new SpectralStorageProcessingException( msg, amazonServiceException );
//								}
//							} catch ( AmazonClientException amazonClientException ) {
//								retryCount++;
//								if ( retryCount > RETRY_UPLOAD_SCAN_DATA_OTHER_FILES_MAX ) {
//									String msg = "Failed to copy temp dir item to S3. temp dir item: " 
//											+ tempOutputDirItem.getAbsolutePath();
//									log.error( msg, amazonClientException );
//									throw new SpectralStorageProcessingException( msg, amazonClientException );
//								}
//							} catch ( Exception e ) {
//								retryCount++;
//								if ( retryCount > RETRY_UPLOAD_SCAN_DATA_OTHER_FILES_MAX ) {
//									String msg = "Failed to copy temp dir item to S3. temp dir item: " 
//											+ tempOutputDirItem.getAbsolutePath();
//									log.error( msg, e );
//									throw new SpectralStorageProcessingException( msg, e );
//								}
//							}
//							
//							Thread.sleep( RETRY_UPLOAD_SCAN_DATA_OTHER_FILES_DELAY );
//						}
//					}
//				}
//			}
//			
//			//  Second copy complete file:
//			for ( File tempOutputDirItem : tempOutputDirItems ) {
//				String filename = tempOutputDirItem.getName();
//				if ( filename.endsWith( SpectralStorage_Filename_Constants.DATA_INDEX_FILES_COMPLETE_FILENAME_SUFFIX ) ) {
//					String objectKey = SpectralStorage_DataFiles_S3_Prefix_Constants.S3_PREFIX_DATA_FILES + filename;
//
//					int retryCount = 0;
//
//					while ( true ) {
//						try {
//							amazonS3.putObject( pgmParams.getS3_OutputBucket(), objectKey, tempOutputDirItem );
//
//							break;  // exit on success
//
//						} catch ( AmazonServiceException amazonServiceException ) {
//							retryCount++;
//							if ( retryCount > RETRY_UPLOAD_SCAN_DATA_OTHER_FILES_MAX ) {
//								String msg = "Failed to copy temp dir item to S3."
//										+ " Status Code: " + amazonServiceException.getStatusCode()
//										+ ", temp dir item: " 
//										+ tempOutputDirItem.getAbsolutePath();
//								log.error( msg, amazonServiceException );
//								throw new SpectralStorageProcessingException( msg, amazonServiceException );
//							}
//						} catch (AmazonClientException amazonClientException) {
//							retryCount++;
//							if ( retryCount > RETRY_UPLOAD_SCAN_DATA_OTHER_FILES_MAX ) {
//								String msg = "Failed to copy temp dir item to S3. temp dir item: " 
//										+ tempOutputDirItem.getAbsolutePath();
//								log.error( msg, amazonClientException );
//								throw new SpectralStorageProcessingException( msg, amazonClientException );
//							}
//						}
//					}
//				}
//			}
//						
//			System.out.println( "data files copied to S3 Bucket: " 
//					+ pgmParams.getS3_OutputBucket()
//					+ ", dataFileObjectKey: " + dataFileObjectKey );
//			
//			writeAssocStorageDirInCurrentDir( pgmParams.getS3_OutputBucket() );
//		}
//		
//		// Empty temp dir
//		File[] tempOutputDirItems = tempOutputDir.listFiles();
//		for ( File tempOutputDirItem : tempOutputDirItems ) {
//			if ( ! tempOutputDirItem.delete() ) {
//				String msg = "Failed to delete temp dir item: " + tempOutputDirItem.getAbsolutePath();
//				log.error( msg );
//				throw new SpectralStorageProcessingException(msg);
//			}
//		}
//	}
//	
//	/**
//	 * Copy Scan Data file to S3 using Multipart upload
//	 * @param scanDataFile
//	 * @param objectKey
//	 * @param bucketName
//	 * @param amazonS3
//	 * @throws IOException 
//	 * @throws FileNotFoundException 
//	 */
//	private void copyScanDataFileTo_S3( File scanDataFile, String objectKey, String bucketName, AmazonS3 amazonS3 ) throws Exception {
//
//		byte[] uploadPartByteBuffer = new byte[ 40 * 1000 * 1000 ]; // each part 40 MB
//
//		int partNumber = 0; // Must start at 1, incremented at top of loop, max of 10,000
//		int bytesRead = 0;
//		
//		try ( BufferedInputStream scanDataFileOnDiskIS = new BufferedInputStream( new FileInputStream( scanDataFile ) ) ) {
//
//	        // Create a list of UploadPartResponse objects. You get one of these
//	        // for each part upload.
//	        List<PartETag> partETags = new ArrayList<>( 10001 ); // Init to max possible size
//
//	        InitiateMultipartUploadResult initResponse = null;
//	        
//	        {
//	        	int retryCount = 0;
//	        	
//	        	while ( true ) {
//	        		try {
//	        			// Step 1: Initialize.
//	        			InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest( bucketName, objectKey);
//	        			initResponse = 
//	        					amazonS3.initiateMultipartUpload(initRequest);
//	        			
//	        			break;  //  exit loop on success
//	        			
//	        		} catch ( AmazonServiceException e ) {
//	        			retryCount++;
//	        			if ( retryCount > RETRY_UPLOAD_SCAN_DATA_MAIN_FILE_INIT_MAX ) {
//	        				String msg = "Failed to initialize upload of Scan Data File to S3.  "
//	        						+ "bucketName: " + bucketName
//	        						+ ", objectKey: " + objectKey
//	        						+ ", Scan Data File: " + scanDataFile.getAbsolutePath();
//	        				log.error( msg, e );
//	        				throw e;
//	        			}
//
//	        		} catch ( AmazonClientException e ) {
//	        			retryCount++;
//	        			if ( retryCount > RETRY_UPLOAD_SCAN_DATA_MAIN_FILE_INIT_MAX ) {
//	        				String msg = "Failed to initialize upload of Scan Data File to S3.  "
//	        						+ "bucketName: " + bucketName
//	        						+ ", objectKey: " + objectKey
//	        						+ ", Scan Data File: " + scanDataFile.getAbsolutePath();
//	        				log.error( msg, e );
//	        				throw e;
//	        			}
//	        		} catch ( Exception e ) {
//	        			retryCount++;
//	        			if ( retryCount > RETRY_UPLOAD_SCAN_DATA_MAIN_FILE_INIT_MAX ) {
//	        				String msg = "Failed to initialize upload of Scan Data File to S3.  "
//	        						+ "bucketName: " + bucketName
//	        						+ ", objectKey: " + objectKey
//	        						+ ", Scan Data File: " + scanDataFile.getAbsolutePath();
//	        				log.error( msg, e );
//	        				throw e;
//	        			}
//	        		}
//	        		
//	        		Thread.sleep( RETRY_UPLOAD_SCAN_DATA_MAIN_FILE_INIT_DELAY );
//	        	}
//	        }
//	        	
//
//        	String uploadId = initResponse.getUploadId();
//        
//	        try {
//	        	//  Step 2:  Upload parts.
//
//	        	while ( ( bytesRead = populateBufferFromScanDataFile( scanDataFileOnDiskIS, uploadPartByteBuffer ) ) > 0 ) {
//	        		partNumber++;
//	        		boolean lastPart = false;
//	        		if ( bytesRead < uploadPartByteBuffer.length ) { // uploadPartByteBuffer not full so is at end of file
//	        			lastPart = true;
//	        		}
//
//	        		int retryCount = 0;
//	        		
//	        		while ( true ) {
//	        			try {
//	        				ByteArrayInputStream scanFilePartIS = new ByteArrayInputStream( uploadPartByteBuffer, 0 /* offset */, bytesRead /* length */ );
//	        				UploadPartRequest uploadRequest = 
//	        						new UploadPartRequest().withUploadId( uploadId )
//	        						.withBucketName( bucketName )
//	        						.withKey( objectKey )
//	        						.withInputStream( scanFilePartIS )
//	        						.withPartNumber( partNumber )
//	        						.withPartSize( bytesRead )
//	        						.withLastPart( lastPart );
//
//	        				//   Consider computing MD5 on scanFilePartIS and add to uploadRequest
//	        				//       S3 uses that for an integrity check
//
//	        				UploadPartResult result =  amazonS3.uploadPart( uploadRequest );
//	        				PartETag partETag = result.getPartETag();
//	        				partETags.add( partETag );
//	        				
//	        				break;  //  exit loop on success
//
//		        		} catch ( AmazonServiceException e ) {
//		        			retryCount++;
//		        			if ( retryCount > RETRY_UPLOAD_SCAN_DATA_MAIN_FILE_UPLOAD_PART_MAX ) {
//		        				String msg = "Failed to upload part of Scan Data File to S3.  "
//		        						+ "bucketName: " + bucketName
//		        						+ ", objectKey: " + objectKey
//		        						+ ", partNumber: " + partNumber
//		        						+ ", Scan Data File: " + scanDataFile.getAbsolutePath();
//		        				log.error( msg, e );
//		        				throw e;
//		        			}
//
//		        		} catch ( AmazonClientException e ) {
//		        			retryCount++;
//		        			if ( retryCount > RETRY_UPLOAD_SCAN_DATA_MAIN_FILE_UPLOAD_PART_MAX ) {
//		        				String msg = "Failed to upload part of Scan Data File to S3.  "
//		        						+ "bucketName: " + bucketName
//		        						+ ", objectKey: " + objectKey
//		        						+ ", partNumber: " + partNumber
//		        						+ ", Scan Data File: " + scanDataFile.getAbsolutePath();
//		        				log.error( msg, e );
//		        				throw e;
//		        			}
//		        		} catch ( Exception e ) {
//		        			retryCount++;
//		        			if ( retryCount > RETRY_UPLOAD_SCAN_DATA_MAIN_FILE_UPLOAD_PART_MAX ) {
//		        				String msg = "Failed to upload part of Scan Data File to S3.  "
//		        						+ "bucketName: " + bucketName
//		        						+ ", objectKey: " + objectKey
//		        						+ ", partNumber: " + partNumber
//		        						+ ", Scan Data File: " + scanDataFile.getAbsolutePath();
//		        				log.error( msg, e );
//		        				throw e;
//		        			}
//		        		}
//		        		
//		        		Thread.sleep( RETRY_UPLOAD_SCAN_DATA_MAIN_FILE_UPLOAD_PART_DELAY );	        
//	        		}
//	        		
//	        		if ( bytesRead < uploadPartByteBuffer.length ) { // uploadPartByteBuffer not full so is at end of file
//	        			break;
//	        		}
//	        	}
//
//	        	{
//
//	        		int retryCount = 0;
//	        		
//	        		while ( true ) {
//	        			try {
//	        				// Step 3: Complete.
//	        				CompleteMultipartUploadRequest compRequest = new 
//	        						CompleteMultipartUploadRequest(
//	        								bucketName, 
//	        								objectKey, 
//	        								uploadId,
//	        								partETags);
//
//	        				amazonS3.completeMultipartUpload( compRequest );
//
//	        				break;  //  exit loop on success
//
//	        			} catch ( AmazonServiceException e ) {
//	        				retryCount++;
//	        				if ( retryCount > RETRY_UPLOAD_SCAN_DATA_MAIN_FILE_COMPLETE_MAX ) {
//	        					String msg = "Failed to upload part of Scan Data File to S3.  "
//	        							+ "bucketName: " + bucketName
//	        							+ ", objectKey: " + objectKey
//	        							+ ", partNumber: " + partNumber
//	        							+ ", Scan Data File: " + scanDataFile.getAbsolutePath();
//	        					log.error( msg, e );
//	        					throw e;
//	        				}
//
//	        			} catch ( AmazonClientException e ) {
//	        				retryCount++;
//	        				if ( retryCount > RETRY_UPLOAD_SCAN_DATA_MAIN_FILE_COMPLETE_MAX ) {
//	        					String msg = "Failed to upload part of Scan Data File to S3.  "
//	        							+ "bucketName: " + bucketName
//	        							+ ", objectKey: " + objectKey
//	        							+ ", partNumber: " + partNumber
//	        							+ ", Scan Data File: " + scanDataFile.getAbsolutePath();
//	        					log.error( msg, e );
//	        					throw e;
//	        				}
//	        			} catch ( Exception e ) {
//	        				retryCount++;
//	        				if ( retryCount > RETRY_UPLOAD_SCAN_DATA_MAIN_FILE_COMPLETE_MAX ) {
//	        					String msg = "Failed to upload part of Scan Data File to S3.  "
//	        							+ "bucketName: " + bucketName
//	        							+ ", objectKey: " + objectKey
//	        							+ ", partNumber: " + partNumber
//	        							+ ", Scan Data File: " + scanDataFile.getAbsolutePath();
//	        					log.error( msg, e );
//	        					throw e;
//	        				}
//	        			}
//
//	        			Thread.sleep( RETRY_UPLOAD_SCAN_DATA_MAIN_FILE_COMPLETE_DELAY );	        
//	        		}
//	        	}
//	        } catch (Exception e) {
//	        	
//	        	amazonS3.abortMultipartUpload( new AbortMultipartUploadRequest( bucketName, objectKey, uploadId ) );
//	        	throw e;
//	        }
//		}
//	}
//	
//	/**
//	 * @param scanDataFileIS
//	 * @param uploadPartByteBuffer
//	 * @return number of bytes read into uploadPartByteBuffer.  If < uploadPartByteBuffer.length, at last buffer for file
//	 * @throws IOException 
//	 */
//	private int populateBufferFromScanDataFile( InputStream scanDataFileIS, byte[] uploadPartByteBuffer ) throws IOException {
//		
//		int byteBufferLength = uploadPartByteBuffer.length;
//		
//		int bytesRead = 0;
//		int byteBufferIndex = 0;
//		
//		while ( ( bytesRead = 
//				scanDataFileIS.read( uploadPartByteBuffer, byteBufferIndex, byteBufferLength - byteBufferIndex) ) != -1 ) {
//			byteBufferIndex += bytesRead;
//			if ( byteBufferIndex >= byteBufferLength ) {
//				break;
//			}
//		}
//		
//		return byteBufferIndex;
//	}
	
	/**
	 * @param pgmParams
	 * @param apiKey
	 * @param tempOutputDir
	 * @throws Exception
	 * @throws SpectralStorageProcessingException
	 */
	private void moveFilesToFinalSubdirLocalFilesytem(Scan_File_Processor_MainProgram_Params pgmParams, String apiKey,
			File tempOutputDir) throws Exception, SpectralStorageProcessingException {
		
		//  Backup existing files if configured to do so
		moveExistingFilesTo_OLD_Subdir_LocalFilesytem( pgmParams, apiKey );
		
		File subDirToMoveFilesTo =
				GetOrCreateSpectralStorageSubPath.getInstance()
				.createDirsForHashIfNotExists( apiKey, pgmParams.getOutputBaseDir() );
		
		//  First move all but complete file:
		File[] tempOutputDirItems = tempOutputDir.listFiles();
		for ( File tempOutputDirItem : tempOutputDirItems ) {
			String filename = tempOutputDirItem.getName();
			if ( ! filename.endsWith( SpectralStorage_Filename_Constants.DATA_INDEX_FILES_COMPLETE_FILENAME_SUFFIX ) ) {
				File fileRenameTo = new File( subDirToMoveFilesTo, filename );
				if ( ! tempOutputDirItem.renameTo( fileRenameTo ) ) {
					String msg = "Failed to move temp dir item: " 
							+ tempOutputDirItem.getAbsolutePath()
							+ ", to " + fileRenameTo.getAbsolutePath();
					log.error( msg );
					throw new SpectralStorageProcessingException(msg);
				}
			}
		}

		//  Second move complete file:
		for ( File tempOutputDirItem : tempOutputDirItems ) {
			String filename = tempOutputDirItem.getName();
			if ( filename.endsWith( SpectralStorage_Filename_Constants.DATA_INDEX_FILES_COMPLETE_FILENAME_SUFFIX ) ) {
				File fileRenameTo = new File( subDirToMoveFilesTo, filename );
				if ( ! tempOutputDirItem.renameTo( fileRenameTo ) ) {
					String msg = "Failed to move temp dir item: " 
							+ tempOutputDirItem.getAbsolutePath()
							+ ", to " + fileRenameTo.getAbsolutePath();
					log.error( msg );
					throw new SpectralStorageProcessingException(msg);
				}
			}
		}
		
		System.out.println( "data files moved to to: " + subDirToMoveFilesTo.getAbsolutePath() );
		writeAssocStorageDirInCurrentDir( subDirToMoveFilesTo.getAbsolutePath() );
	}

	/**
	 * "Backup" the existing files if a "Backup Old" Directory is configured.
	 * 
	 * @param pgmParams
	 * @param apiKey
	 * @throws Exception
	 * @throws SpectralStorageProcessingException
	 */
	private void moveExistingFilesTo_OLD_Subdir_LocalFilesytem(
			
			Scan_File_Processor_MainProgram_Params pgmParams, 
			String apiKey ) throws Exception, SpectralStorageProcessingException {
		
		if ( pgmParams.getBackupOldBaseDir() == null ) {
			
			//  Not Configured so skip
			
			return; // EARLY RETURN
		}
		
		File subDirToMoveFilesFrom =
				GetOrCreateSpectralStorageSubPath.getInstance()
				.createDirsForHashIfNotExists( apiKey, pgmParams.getOutputBaseDir() );
		

		File subDirToMoveFiles_To_BackupOldDir =
				GetOrCreateSpectralStorageSubPath.getInstance()
				.createDirsForHashIfNotExists( apiKey, pgmParams.getBackupOldBaseDir() );
		

		File[] tempOutputDirItems = subDirToMoveFilesFrom.listFiles();
		
		//  First move complete file:
		for ( File tempOutputDirItem : tempOutputDirItems ) {
			
			String filename = tempOutputDirItem.getName();

			if ( filename.startsWith( apiKey ) ) { //  Only move files for this API Key

				if ( filename.endsWith( SpectralStorage_Filename_Constants.DATA_INDEX_FILES_COMPLETE_FILENAME_SUFFIX ) ) {

					File fileRenameTo = new File( subDirToMoveFiles_To_BackupOldDir, filename );
					if ( ! tempOutputDirItem.renameTo( fileRenameTo ) ) {
						String msg = "Failed to move data dir item to Backup Old Dir: " 
								+ tempOutputDirItem.getAbsolutePath()
								+ ", to " + fileRenameTo.getAbsolutePath();
						log.error( msg );
						throw new SpectralStorageProcessingException(msg);
					}
				}
			}
		}
		
		//  Second move all but complete file:
		for ( File tempOutputDirItem : tempOutputDirItems ) {
			
			String filename = tempOutputDirItem.getName();
			
			if ( filename.startsWith( apiKey ) ) { //  Only move files for this API Key
				
				if ( ! filename.endsWith( SpectralStorage_Filename_Constants.DATA_INDEX_FILES_COMPLETE_FILENAME_SUFFIX ) ) {
					File fileRenameTo = new File( subDirToMoveFiles_To_BackupOldDir, filename );
					if ( ! tempOutputDirItem.renameTo( fileRenameTo ) ) {
						String msg = "Failed to move data dir item to Backup Old Dir: " 
								+ tempOutputDirItem.getAbsolutePath()
								+ ", to " + fileRenameTo.getAbsolutePath();
						log.error( msg );
						throw new SpectralStorageProcessingException(msg);
					}
				}
			}
		}

		System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
		System.out.println( "OLD data files moved to to: " + subDirToMoveFiles_To_BackupOldDir.getAbsolutePath() );
		System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
	}
	
	
	/**
	 * @param inputScanFile
	 */
	private void cleanupInputScanFile( File inputScanFile ) {

		System.out.println( "Import was successful and delete scan file on successful import is set in config file so deleting uploaded scan file: "
				+ inputScanFile.getAbsolutePath() );
		
		File localDir = new File( "" );

		if ( ! inputScanFile.getAbsolutePath().startsWith( localDir.getAbsolutePath() ) ) {
			
			System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
			System.out.println( "inputScanFile not in current directory so don't delete it.  Current directory: " 
					+ localDir.getAbsolutePath()
					+ ", inputScanFile: "
					+ inputScanFile.getAbsolutePath() );
			System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
		} else {
			if ( ! inputScanFile.delete() ) {
				System.err.println( "Failed to delete input scan file: " + inputScanFile.getAbsolutePath() );
			} else {
				System.out.println( "Deleted input scan file: " + inputScanFile.getAbsolutePath() );
			}
		}
		
	}

	// AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.
	
//	/**
//	 * @throws Exception
//	 */
//	private void deleteUploadedScanFileIn_S3_Object() throws Exception {
//
//		File scanFile_S3_LocationFile = new File( UploadProcessing_InputScanfileS3InfoConstants.SCANFILE_S3_LOCATION_FILENAME );
//		
//		if ( ! scanFile_S3_LocationFile.exists() ) {
//			//  No file with info on scan file location in S3, so must be local file
//			return;  //  EARLY EXIT
//		}
//		
//		UploadScanfileS3Location uploadScanfileS3Location = null;
//		
//		JAXBContext jaxbContext = JAXBContext.newInstance( UploadScanfileS3Location.class ); 
//		
//		try ( InputStream is = new FileInputStream( scanFile_S3_LocationFile ) ) {
//
//			XMLInputFactory xmlInputFactory = Create_XMLInputFactory_XXE_Safe.create_XMLInputFactory_XXE_Safe();
//			XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader( new StreamSource( is ) );
//			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//			Object uploadScanfileS3LocationAsObject = unmarshaller.unmarshal( xmlStreamReader );
//
//			if ( uploadScanfileS3LocationAsObject instanceof UploadScanfileS3Location ) {
//				uploadScanfileS3Location = ( UploadScanfileS3Location ) uploadScanfileS3LocationAsObject;
//			} else {
//				String msg = "Failed to deserialize data in " + scanFile_S3_LocationFile.getAbsolutePath();
//				log.error( msg );
//				throw new SpectralStorageProcessingException(msg);
//			}
//		}
//
//		if ( uploadScanfileS3Location.isS3_infoFrom_RemoteSystem() ) {
//			//  Scan File S3 object came from external system so that system is responsible for deleting it
//			return;  //  EARLY EXIT
//		}
//		
//		String s3_bucketName = uploadScanfileS3Location.getS3_bucketName();
//		String s3_objectName = uploadScanfileS3Location.getS3_objectName();
//		
//		final AmazonS3 amazonS3client = S3_AWS_InterfaceObjectHolder.getSingletonInstance().getS3_Client_Input();
//		
//		int retryDeleteScanFileCount = 0;
//		
//		while ( true ) {
//			try {
//				amazonS3client.deleteObject( new DeleteObjectRequest(
//						uploadScanfileS3Location.getS3_bucketName(), uploadScanfileS3Location.getS3_objectName() ));
//
//				break;  // Exit loop on success
//
//			} catch (AmazonServiceException e) {
//				String msg = "Error deleting scan file on S3. s3_bucketName: " + s3_bucketName
//						+ " s3_objectName: " + s3_objectName;
//				log.error( msg );
//
//				retryDeleteScanFileCount++;
//				if ( retryDeleteScanFileCount > RETRY_DELETE_SCAN_FILE_MAX ) {
//					throw new SpectralStorageProcessingException(msg, e);
//				}
//			} catch (Exception e) {
//				String msg = "Error deleting scan file on S3. s3_bucketName: " + s3_bucketName
//						+ " s3_objectName: " + s3_objectName;
//				log.error( msg );
//
//				retryDeleteScanFileCount++;
//				if ( retryDeleteScanFileCount > RETRY_DELETE_SCAN_FILE_MAX ) {
//					throw new SpectralStorageProcessingException(msg, e);
//				}
//			}
//			
//			Thread.sleep( RETRY_DELETE_SCAN_FILE_DELAY );
//		}
//
//		// the same object name as scan file but with ".submitted" on the end
//		String objectKey_SubmittedObject = uploadScanfileS3Location.getS3_objectName()
//				+ ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_SUBMITTED_FILE_SUFFIX;
//		
//
//		int retryDeleteScanFileSubmittedCount = 0;
//
//		while ( true ) {
//			try {
//				amazonS3client.deleteObject( new DeleteObjectRequest(
//						uploadScanfileS3Location.getS3_bucketName(), objectKey_SubmittedObject ) );
//
//				break;  // Exit loop on success
//
//			} catch (AmazonServiceException e) {
//				String msg = "Error deleting scan file on S3. s3_bucketName: " + s3_bucketName
//						+ " s3_objectName: " + s3_objectName;
//				log.error( msg );
//
//				retryDeleteScanFileSubmittedCount++;
//				if ( retryDeleteScanFileSubmittedCount > RETRY_DELETE_SCAN_FILE_MAX ) {
//					throw new SpectralStorageProcessingException(msg, e);
//				}
//			} catch (Exception e) {
//				String msg = "Error deleting scan file on S3. s3_bucketName: " + s3_bucketName
//						+ " s3_objectName: " + s3_objectName;
//				log.error( msg );
//
//				retryDeleteScanFileSubmittedCount++;
//				if ( retryDeleteScanFileSubmittedCount > RETRY_DELETE_SCAN_FILE_MAX ) {
//					throw new SpectralStorageProcessingException(msg, e);
//				}
//			}
//
//			Thread.sleep( RETRY_DELETE_SCAN_FILE_DELAY );
//		}
//	}
	
	/**
	 * @param hashString
	 * @throws Exception 
	 */
	private void writeAssocStorageDirInCurrentDir( String storageDirPath ) throws Exception {

		File hashFile = new File( ScanFileToProcessConstants.ASSOCIATED_STORAGE_DIR__FILENAME );

		try ( BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(new FileOutputStream( hashFile ), StandardCharsets.UTF_8 ) ) ) {
			writer.write( storageDirPath );
		} catch ( Exception e ) {
			String msg = "Failed to write hash to file: " + hashFile.getAbsolutePath();
			log.error( msg );
			throw new SpectralStorageProcessingException(msg, e);
		}
	}
	
	/**
	 * @param exceptionToWriteToFile
	 * @throws Exception
	 */
	private void processSpectralStorageDataException ( SpectralStorageDataException exceptionToWriteToFile ) throws Exception {

		File errorMsgFile = new File( ScanFileToProcessConstants.DATA_ERROR_HUMAN_READABLE_FILENAME );

		try ( BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(new FileOutputStream( errorMsgFile ), StandardCharsets.UTF_8 ) ) ) {
			writer.write( exceptionToWriteToFile.getMessage() );
		} catch ( Exception e ) {
			String msg = "Failed to write error msg to file: " + errorMsgFile.getAbsolutePath();
			log.error( msg );
			throw new SpectralStorageProcessingException(msg, e);
		}

	}
}
