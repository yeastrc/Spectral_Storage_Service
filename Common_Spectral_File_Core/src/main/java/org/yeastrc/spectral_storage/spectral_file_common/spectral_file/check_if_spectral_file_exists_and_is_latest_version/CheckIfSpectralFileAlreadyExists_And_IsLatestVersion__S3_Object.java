package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.check_if_spectral_file_exists_and_is_latest_version;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.SpectralStorage_DataFiles_S3_Prefix_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataNotFoundException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.s3_aws_interface.S3_AWS_InterfaceObjectHolder;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_read_file_version_number_at_file_start.Common_Read_FileVersionNumber_AtFileStart;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.CreateSpectralStorageFilenames;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_999_latest.StorageFile_Version_999_LATEST_Constants;

import com.amazonaws.services.s3.AmazonS3;


//   AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.

/**
 * AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.
 * 
 * 
 * Check if the spectral storage file already exists for this hash key
 * 
 * For when stored in AWS S3 bucket
 *
 */
public class CheckIfSpectralFileAlreadyExists_And_IsLatestVersion__S3_Object {

	private static final Logger log = LoggerFactory.getLogger(CheckIfSpectralFileAlreadyExists_And_IsLatestVersion__S3_Object.class);
	/**
	 * private constructor
	 */
	private CheckIfSpectralFileAlreadyExists_And_IsLatestVersion__S3_Object(){}
	public static CheckIfSpectralFileAlreadyExists_And_IsLatestVersion__S3_Object getInstance( ) throws Exception {
		CheckIfSpectralFileAlreadyExists_And_IsLatestVersion__S3_Object instance = new CheckIfSpectralFileAlreadyExists_And_IsLatestVersion__S3_Object();
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
			CommonReader_File_And_S3 commonReader_File_And_S3,
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

		if ( log.isDebugEnabled() ) {
			if ( dataFile_ObjectName_Exists ) {
				log.debug( "dataFile_Object DOES exist in in S3. bucket: "
						+ s3_bucketName 
						+ ", objectKey: " 
						+ dataFile_ObjectName );
			} else {
				log.debug( "dataFile_Object does NOT exist in in S3. bucket: "
						+ s3_bucketName 
						+ ", objectKey: " 
						+ dataFile_ObjectName );
			}

			if ( dataIndexSpectralFilesCompleteFile_ObjectName_Exists ) {
				log.debug( "dataIndexSpectralFilesCompleteFile_Object DOES exist in in S3. bucket: "
						+ s3_bucketName 
						+ ", objectKey: " 
						+ dataIndexSpectralFilesCompleteFile_ObjectName );
			} else {
				log.debug( "dataIndexSpectralFilesCompleteFile_Object does NOT exist in in S3. bucket: "
						+ s3_bucketName 
						+ ", objectKey: " 
						+ dataIndexSpectralFilesCompleteFile_ObjectName );
			}
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

			try {
				short fileVersionInFile = Common_Read_FileVersionNumber_AtFileStart.getInstance()
						.common_Read_FileVersionNumber_AtFileStart( dataFilename, hash_String, commonReader_File_And_S3 );
				
				if ( fileVersionInFile != StorageFile_Version_999_LATEST_Constants.FILE_VERSION ) {
					return false;
				}
			} catch ( SpectralStorageDataNotFoundException e ) {

				String msg = "...Complete S3 object exists. Data object does exist. Common_Read_FileVersionNumber_AtFileStart.getInstance():common_Read_FileVersionNumber_AtFileStart(...) throws SpectralStorageDataNotFoundException."
						+ ", data object name: " + dataFile_ObjectName;
				log.error( msg );
				throw new SpectralStorageProcessingException( msg, e );
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
