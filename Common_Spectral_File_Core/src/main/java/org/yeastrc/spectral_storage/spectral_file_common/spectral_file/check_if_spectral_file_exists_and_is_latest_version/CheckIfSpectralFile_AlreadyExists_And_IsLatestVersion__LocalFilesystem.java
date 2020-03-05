package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.check_if_spectral_file_exists_and_is_latest_version;

import java.io.File;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataNotFoundException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_read_file_version_number_at_file_start.Common_Read_FileVersionNumber_AtFileStart;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.CreateSpectralStorageFilenames;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.GetOrCreateSpectralStorageSubPath;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_999_latest.StorageFile_Version_999_LATEST_Constants;

/**
 * Check if the spectral storage file already exists for this hash key
 * 
 * For when stored on local file system
 *
 */
public class CheckIfSpectralFile_AlreadyExists_And_IsLatestVersion__LocalFilesystem {

	private static final Logger log = LoggerFactory.getLogger(CheckIfSpectralFile_AlreadyExists_And_IsLatestVersion__LocalFilesystem.class);
	/**
	 * private constructor
	 */
	private CheckIfSpectralFile_AlreadyExists_And_IsLatestVersion__LocalFilesystem(){}
	public static CheckIfSpectralFile_AlreadyExists_And_IsLatestVersion__LocalFilesystem getInstance( ) throws Exception {
		CheckIfSpectralFile_AlreadyExists_And_IsLatestVersion__LocalFilesystem instance = new CheckIfSpectralFile_AlreadyExists_And_IsLatestVersion__LocalFilesystem();
		return instance;
	}
	
	/**
	 * @param outputBaseDir
	 * @param hash_String
	 * @return
	 * @throws Exception
	 */
	public boolean doesSpectralFileAlreadyExist( 
			File outputBaseDir, 
			CommonReader_File_And_S3 commonReader_File_And_S3,
			String hash_String ) throws Exception {
		
		String dataFilename =
				CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Data_Filename( hash_String );
		
		String dataIndexSpectralFilesCompleteFilename =
				CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Data_Index_Files_Complete_Filename( hash_String );

		File subDir =
				GetOrCreateSpectralStorageSubPath.getInstance()
				.createDirsForHashIfNotExists( hash_String, outputBaseDir );

		File dataFile = new File( subDir, dataFilename );
		File dataIndexSpectralFilesCompleteFile = new File( subDir, dataIndexSpectralFilesCompleteFilename );

		if ( log.isDebugEnabled() ) {
			if ( dataFile.exists() ) {
				log.debug( "dataFile DOES exist: "
						+ dataFile.getAbsolutePath() );
			} else {
				log.debug( "dataFile does NOT exist: "
						+ dataFile.getAbsolutePath() );
			}
			
			if ( dataIndexSpectralFilesCompleteFile.exists() ) {
				log.debug( "dataIndexSpectralFilesCompleteFile DOES exist: "
						+ dataIndexSpectralFilesCompleteFile.getAbsolutePath() );
			} else {
				log.debug( "dataIndexSpectralFilesCompleteFile does NOT exist: "
						+ dataIndexSpectralFilesCompleteFile.getAbsolutePath() );
			}
		}

		if ( dataIndexSpectralFilesCompleteFile.exists() ) {
			
			//  dataIndexSpectralFilesCompleteFile is created after all the other files so if it exists,
			//  processing completed successfully
			
			if ( ! dataFile.exists() ) {
			
				String msg = "...Complete file exists but Data File does not exist.  "
						+ "complete file: " + dataIndexSpectralFilesCompleteFile.getAbsolutePath()
						+ ", data file: " + dataFile.getAbsolutePath();
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

				String msg = "...Complete file exists. Data File does exist. Common_Read_FileVersionNumber_AtFileStart.getInstance():common_Read_FileVersionNumber_AtFileStart(...) throws SpectralStorageDataNotFoundException."
						+ ", data file: " + dataFile.getAbsolutePath();
				log.error( msg );
				throw new SpectralStorageProcessingException( msg, e );
			}
			
			return true;
		}
		
		if ( dataFile.exists() ) {

			String msg = "...complete file does NOT exist but Data File DOES exist."
					+ " Must have been a failure that needs cleaning up.  "
					+ "complete file: " + dataIndexSpectralFilesCompleteFile.getAbsolutePath()
					+ ", data file: " + dataFile.getAbsolutePath();
			log.error( msg );
			throw new SpectralStorageProcessingException(msg);
		}
		
		return false;
	}
}
