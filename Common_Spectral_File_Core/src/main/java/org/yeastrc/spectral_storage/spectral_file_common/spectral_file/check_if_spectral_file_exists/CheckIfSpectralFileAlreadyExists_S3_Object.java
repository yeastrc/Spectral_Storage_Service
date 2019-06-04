package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.check_if_spectral_file_exists;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.SpectralStorage_DataFiles_S3_Prefix_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.s3_aws_interface.S3_AWS_InterfaceObjectHolder;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.CreateSpectralStorageFilenames;

import com.amazonaws.services.s3.AmazonS3;

/**
 * Check if the spectral storage file already exists for this hash key
 * 
 * For when stored on local file system
 *
 */
public class CheckIfSpectralFileAlreadyExists_S3_Object {

	private static final Logger log = Logger.getLogger(CheckIfSpectralFileAlreadyExists_S3_Object.class);
	/**
	 * private constructor
	 */
	private CheckIfSpectralFileAlreadyExists_S3_Object(){}
	public static CheckIfSpectralFileAlreadyExists_S3_Object getInstance( ) throws Exception {
		CheckIfSpectralFileAlreadyExists_S3_Object instance = new CheckIfSpectralFileAlreadyExists_S3_Object();
		return instance;
	}
	
	/**
	 * @param s3_bucketName
	 * @param hash_String
	 * @return
	 * @throws Exception
	 */
	public boolean doesSpectralFileAlreadyExist( 
			String s3_bucketName, 
			String hash_String ) throws Exception {
		
		String dataFilename =
				CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Data_Filename( hash_String );
		
		String dataIndexSpectralFilesCompleteFilename =
				CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Data_Index_Files_Complete_Filename( hash_String );

		String dataFile_ObjectName = SpectralStorage_DataFiles_S3_Prefix_Constants.S3_PREFIX_DATA_FILES + dataFilename;
		String dataIndexSpectralFilesCompleteFile_ObjectName = 
				SpectralStorage_DataFiles_S3_Prefix_Constants.S3_PREFIX_DATA_FILES + dataIndexSpectralFilesCompleteFilename;

		final AmazonS3 s3 = S3_AWS_InterfaceObjectHolder.getSingletonInstance().getS3_Client_Output();

		boolean dataFile_ObjectName_Exists = s3.doesObjectExist( s3_bucketName, dataFile_ObjectName );
		boolean dataIndexSpectralFilesCompleteFile_ObjectName_Exists = 
				s3.doesObjectExist( s3_bucketName, dataIndexSpectralFilesCompleteFile_ObjectName );

		if ( dataFile_ObjectName_Exists ) {
			System.out.println( "dataFile_Object DOES exist in in S3. bucket: "
					+ s3_bucketName 
					+ ", objectKey: " 
					+ dataFile_ObjectName );
		} else {
			System.out.println( "dataFile_Object does NOT exist in in S3. bucket: "
					+ s3_bucketName 
					+ ", objectKey: " 
					+ dataFile_ObjectName );
		}

		if ( dataIndexSpectralFilesCompleteFile_ObjectName_Exists ) {
			System.out.println( "dataIndexSpectralFilesCompleteFile_Object DOES exist in in S3. bucket: "
					+ s3_bucketName 
					+ ", objectKey: " 
					+ dataIndexSpectralFilesCompleteFile_ObjectName );
		} else {
			System.out.println( "dataIndexSpectralFilesCompleteFile_Object does NOT exist in in S3. bucket: "
					+ s3_bucketName 
					+ ", objectKey: " 
					+ dataIndexSpectralFilesCompleteFile_ObjectName );
		}
		
		if ( dataIndexSpectralFilesCompleteFile_ObjectName_Exists ) {
			
			//  dataIndexSpectralFilesCompleteFile is created after all the other files so if it exists,
			//  processing completed successfully
			
			if ( ! dataFile_ObjectName_Exists ) {
			
				String msg = "...Complete S3 object exists but Data object does not exist.  "
						+ "complete object name: " + dataIndexSpectralFilesCompleteFile_ObjectName
						+ ", data object name: " + dataFile_ObjectName;
				log.error( msg );
				throw new SpectralStorageProcessingException(msg);
			}
			
			return true;
		}
		
		if ( dataFile_ObjectName_Exists ) {

			String msg = "...complete S3 object does NOT exist but Data object DOES exist."
					+ " Must have been a failure that needs cleaning up.  "
					+ "complete object name: " + dataIndexSpectralFilesCompleteFile_ObjectName
					+ ", data object name: " + dataFile_ObjectName;
			log.error( msg );
			throw new SpectralStorageProcessingException(msg);
		}
		
		return false;
	}
}
