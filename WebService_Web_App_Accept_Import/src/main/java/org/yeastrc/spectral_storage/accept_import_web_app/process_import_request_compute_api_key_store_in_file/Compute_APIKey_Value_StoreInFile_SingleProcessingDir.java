package org.yeastrc.spectral_storage.accept_import_web_app.process_import_request_compute_api_key_store_in_file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.UploadProcessingStatusFileConstants;
import org.yeastrc.spectral_storage.accept_import_web_app.import_processing_status_file__read_write.UploadProcessingWriteOrUpdateStatusFile;
import org.yeastrc.spectral_storage.accept_import_web_app.import_scan_filename_local_disk.ImportScanFilename_LocalDisk;
import org.yeastrc.spectral_storage.accept_import_web_app.process_import_request_api_key_value_in_file.ProcessImportRequest_APIKey_Value_InFile;
import org.yeastrc.spectral_storage.shared_server_importer.create__xml_input_factory__xxe_safe.Create_XMLInputFactory_XXE_Safe;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessing_InputScanfileS3InfoConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.file_contents_hash_processing.Compute_File_Hashes;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.file_contents_hash_processing.Compute_Hashes;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.s3_aws_interface.S3_AWS_InterfaceObjectHolder;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_api_key_processing.ScanFileAPIKey_ComputeFromScanFileContentHashes;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_api_key_processing.ScanFileAPIKey_ToFileReadWrite;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.upload_scanfile_s3_location.UploadScanfileS3Location;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;

/**
 * Compute the API Key for the Scan File and store in the file
 *
 */
public class Compute_APIKey_Value_StoreInFile_SingleProcessingDir {

	private static final Logger log = LoggerFactory.getLogger( Compute_APIKey_Value_StoreInFile_SingleProcessingDir.class );

	//  private constructor
	private Compute_APIKey_Value_StoreInFile_SingleProcessingDir() { }
	
	/**
	 * @return newly created instance
	 */
	public static Compute_APIKey_Value_StoreInFile_SingleProcessingDir getInstance() { 
		return new Compute_APIKey_Value_StoreInFile_SingleProcessingDir(); 
	}
	
	private volatile boolean shutdownReceived = false;
	
	private volatile Compute_File_Hashes compute_File_Hashes = null;
	
	/**
	 * 
	 */
	public void shutdown() {
		
		shutdownReceived = true;
		
		if ( compute_File_Hashes != null ) {
			
			try {
				compute_File_Hashes.shutdown();
			} catch ( NullPointerException e ) {
				
			}
		}
	}
	
	/**
	 * @param scanFileProcessingDir
	 * @throws Exception
	 */
	public void compute_APIKey_Value_StoreInFile_SingleProcessingDir( File scanFileProcessingDir ) throws Exception {
				
		compute_APIKey_Value_ScanFileLocalDisk( scanFileProcessingDir );

		//  API Key computed
		
		ProcessImportRequest_APIKey_Value_InFile.getInstance().
		processImportRequest_APIKey_Value_InFile( scanFileProcessingDir );
	}
	

	/**
	 * 
	 * @param scanFileProcessingDir
	 * @throws Exception
	 */
	private void compute_APIKey_Value_ScanFileLocalDisk( File scanFileProcessingDir ) throws Exception {

		try {
			//  Create status file for Compute API Key In Progress
			UploadProcessingWriteOrUpdateStatusFile.getInstance()
			.uploadProcessingWriteOrUpdateStatusFile( 
					UploadProcessingStatusFileConstants.STATUS_COMPUTE_API_KEY_IN_PROGRESS, 
					scanFileProcessingDir,
					UploadProcessingStatusFileConstants.STATUS_PROCESSING_CALLER_LABEL__ACCEPT_IMPORT_WEBAPP );
		} catch ( Exception e ) {
			String msg = "Failed to create status file, scanFileProcessingDir: " + scanFileProcessingDir.getAbsolutePath();
			log.error( msg, e );
			throw new SpectralStorageProcessingException( msg, e );
		}

		compute_File_Hashes = Compute_File_Hashes.getInstance();
		
		Compute_Hashes compute_Hashes = null;
		
		File scanFile_S3_LocationFile = new File( scanFileProcessingDir, UploadProcessing_InputScanfileS3InfoConstants.SCANFILE_S3_LOCATION_FILENAME );
		
		if ( scanFile_S3_LocationFile.exists() ) {
	
			//  Compute for scan file in S3
			
			compute_Hashes = this.get_Compute_Hashes_ForScanFile_On_S3(scanFileProcessingDir);
			
		} else {
			
			//  Compute for scan file on local disk
			
			compute_Hashes = this.get_Compute_Hashes_ForScanFile_On_LocalDisk(scanFileProcessingDir);
		}
		
		if ( shutdownReceived ) {

			//  Reset to Status Compute API Key
			try {
				//  Create status file for Compute API Key In Progress
				UploadProcessingWriteOrUpdateStatusFile.getInstance()
				.uploadProcessingWriteOrUpdateStatusFile( 
						UploadProcessingStatusFileConstants.STATUS_COMPUTE_API_KEY, 
						scanFileProcessingDir,
						UploadProcessingStatusFileConstants.STATUS_PROCESSING_CALLER_LABEL__ACCEPT_IMPORT_WEBAPP );
			} catch ( Exception e ) {
				String msg = "Failed to create status file, scanFileProcessingDir: " + scanFileProcessingDir.getAbsolutePath();
				log.error( msg, e );
				throw new SpectralStorageProcessingException( msg, e );
			}
			
			return; // EARLY RETURN
		}
		if ( compute_Hashes == null ) {

			//  Reset to Status Compute API Key
			try {
				//  Create status file for Compute API Key In Progress
				UploadProcessingWriteOrUpdateStatusFile.getInstance()
				.uploadProcessingWriteOrUpdateStatusFile( 
						UploadProcessingStatusFileConstants.STATUS_COMPUTE_API_KEY, 
						scanFileProcessingDir,
						UploadProcessingStatusFileConstants.STATUS_PROCESSING_CALLER_LABEL__ACCEPT_IMPORT_WEBAPP );
			} catch ( Exception e ) {
				String msg = "Failed to create status file, scanFileProcessingDir: " + scanFileProcessingDir.getAbsolutePath();
				log.error( msg, e );
				throw new SpectralStorageProcessingException( msg, e );
			}
			
			//  compute_Hashes null when compute_Hashes.shutdown(); is called
			return; // EARLY RETURN
		}

		String apiKey_String = 
				ScanFileAPIKey_ComputeFromScanFileContentHashes.getInstance()
				.scanFileAPIKey_ComputeFromScanFileContentHashes( compute_Hashes );

		if ( shutdownReceived ) {

			//  Reset to Status Compute API Key
			try {
				//  Create status file for Compute API Key In Progress
				UploadProcessingWriteOrUpdateStatusFile.getInstance()
				.uploadProcessingWriteOrUpdateStatusFile( 
						UploadProcessingStatusFileConstants.STATUS_COMPUTE_API_KEY, 
						scanFileProcessingDir,
						UploadProcessingStatusFileConstants.STATUS_PROCESSING_CALLER_LABEL__ACCEPT_IMPORT_WEBAPP );
			} catch ( Exception e ) {
				String msg = "Failed to create status file, scanFileProcessingDir: " + scanFileProcessingDir.getAbsolutePath();
				log.error( msg, e );
				throw new SpectralStorageProcessingException( msg, e );
			}
			
			return; // EARLY RETURN
		}
		
		//  If get here and shutdown() called, have 0.5 seconds of join() on this thread to write the file properly to disk

		ScanFileAPIKey_ToFileReadWrite.getInstance().writeScanFileHashToInProcessFileInDir( apiKey_String, scanFileProcessingDir );

	}
	
	/**
	 * ScanFile_On_LocalDisk
	 * 
	 * @param scanFileProcessingDir
	 * @return
	 * @throws Exception 
	 */
	private Compute_Hashes get_Compute_Hashes_ForScanFile_On_LocalDisk( File scanFileProcessingDir ) throws Exception {

		//  Find the scan file 
		String scanFilename = ImportScanFilename_LocalDisk.getInstance().getImportScanFilename_LocalDisk( scanFileProcessingDir );
		
		File scanFile = new File( scanFileProcessingDir, scanFilename );

		Compute_Hashes compute_Hashes = compute_File_Hashes.compute_File_Hashes( scanFile );
		
		return compute_Hashes;
	}
	
	/**
	 * ScanFile_On_S3
	 * 
	 * @param scanFileProcessingDir
	 * @return
	 * @throws Exception 
	 */
	private Compute_Hashes get_Compute_Hashes_ForScanFile_On_S3( File scanFileProcessingDir ) throws Exception {

		File scanFile_S3_LocationFile = new File( scanFileProcessingDir, UploadProcessing_InputScanfileS3InfoConstants.SCANFILE_S3_LOCATION_FILENAME );

		UploadScanfileS3Location uploadScanfileS3Location = null;
		
		{
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
		}
		
		String s3_bucketName = uploadScanfileS3Location.getS3_bucketName();
		String s3_objectName = uploadScanfileS3Location.getS3_objectName();

		if ( StringUtils.isEmpty( s3_bucketName ) ) {
			String msg = "s3_bucketName is empty in " + scanFile_S3_LocationFile.getAbsolutePath();
			log.error( msg );
			throw new SpectralStorageProcessingException(msg);
		}
		if ( StringUtils.isEmpty( s3_objectName ) ) {
			String msg = "s3_objectName is empty in " + scanFile_S3_LocationFile.getAbsolutePath();
			log.error( msg );
			throw new SpectralStorageProcessingException(msg);
		}
		
		Compute_Hashes compute_Hashes = null;

		final AmazonS3 s3 = S3_AWS_InterfaceObjectHolder.getSingletonInstance().getS3_Client_Input();
		try {
			S3Object s3Object = s3.getObject( s3_bucketName, s3_objectName );
			try ( InputStream isS3Object = s3Object.getObjectContent() ) {

				compute_Hashes = compute_File_Hashes.compute_File_Hashes_ForInputStream(isS3Object);
			}
		} catch (AmazonServiceException e) {
			String msg = "Error retrieving scan file from S3. s3_bucketName: " + s3_bucketName
					+ " s3_objectName: " + s3_objectName;
			log.error( msg );
			throw new SpectralStorageProcessingException(msg, e);
		} catch (FileNotFoundException e) {
			String msg = "Error retrieving scan file from S3. s3_bucketName: " + s3_bucketName
					+ " s3_objectName: " + s3_objectName;
			log.error( msg );
			throw new SpectralStorageProcessingException(msg, e);
		} catch (Exception e) {
			String msg = "Error retrieving scan file from S3. s3_bucketName: " + s3_bucketName
					+ " s3_objectName: " + s3_objectName;
			log.error( msg );
			throw new SpectralStorageProcessingException(msg, e);
		}
		
		return compute_Hashes;
	}
}
