package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataNotFoundException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_read_file_version_number_at_file_start.Common_Read_FileVersionNumber_AtFileStart;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3_Holder;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.CreateSpectralStorageFilenames;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.StorageFile_Version_003_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.data_file.reader_writer.SpectralFile_Reader_GZIP_V_003;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.StorageFile_Version_005_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.data_file.reader_writer.SpectralFile_Reader_GZIP_V_005;

/**
 * Create a SpectralFile_Reader__IF of the version based on the data files for the hash
 *
 */
public class SpectralFile_Reader_Factory {

	private static final Logger log = LoggerFactory.getLogger(SpectralFile_Reader_Factory.class);
	
	/**
	 * private constructor
	 */
	private SpectralFile_Reader_Factory(){}
	public static SpectralFile_Reader_Factory getInstance( ) throws Exception {
		SpectralFile_Reader_Factory instance = new SpectralFile_Reader_Factory();
		return instance;
	}
	
	/**
	 * Create a SpectralFile_Reader__IF of the version based on the data files for the hash
	 * 
	 * @return
	 * @throws SpectralStorageProcessingException
	 * @throws Exception
	 * @throws SpectralStorageDataNotFoundException
	 */
	public SpectralFile_Reader__IF getSpectralFile_Reader_ForHash( String hashKey, CommonReader_File_And_S3 commonReader_File_And_S3 ) 
			throws Exception, SpectralStorageProcessingException {

		//  !!  SKIP THIS IDEA: Add a cache of API Key to version so don't have to repeatedly open and read the file.
		//    Skip since the version can change if the scan file is uploaded again and a newer version of the file is created.
		
		
		//  Read version number of data file.

		String spectralDataFilename =
				CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Data_Filename( hashKey );
		
		short fileVersionInFile = Common_Read_FileVersionNumber_AtFileStart.getInstance()
				.common_Read_FileVersionNumber_AtFileStart( spectralDataFilename, hashKey, commonReader_File_And_S3 );
		
		SpectralFile_Reader__IF spectralFile_Reader__IF = null;
		
		if ( fileVersionInFile == StorageFile_Version_003_Constants.FILE_VERSION ) {
			
			spectralFile_Reader__IF = SpectralFile_Reader_GZIP_V_003.getInstance();

			//  This check is redundant for now but will be more applicable when there is more than one version
			if ( ! spectralFile_Reader__IF.isVersionSupported( StorageFile_Version_003_Constants.FILE_VERSION ) ) {
				String msg = "Created spectralFile_Reader__IF does not support version for hash.  Version for hash: "
						+ StorageFile_Version_003_Constants.FILE_VERSION;
				log.error( msg );
				throw new SpectralStorageProcessingException( msg );
			}
				
		} else if ( fileVersionInFile == StorageFile_Version_005_Constants.FILE_VERSION ) {
			
			spectralFile_Reader__IF = SpectralFile_Reader_GZIP_V_005.getInstance();

			//  This check is redundant for now but will be more applicable when there is more than one version
			if ( ! spectralFile_Reader__IF.isVersionSupported( StorageFile_Version_005_Constants.FILE_VERSION ) ) {
				String msg = "Created spectralFile_Reader__IF does not support version for hash.  Version for hash: "
						+ StorageFile_Version_005_Constants.FILE_VERSION;
				log.error( msg );
				throw new SpectralStorageProcessingException( msg );
			}
			
		} else {

			String msg = "fileVersionInFile read from data file not a supported version. fileVersionInFile: " + fileVersionInFile
					+ ", hashKey: " + hashKey
					+ ", spectralDataFilename: " + spectralDataFilename;
			log.error(msg);
			throw new SpectralStorageProcessingException( msg );
		}
		
		
		spectralFile_Reader__IF.init( hashKey, CommonReader_File_And_S3_Holder.getSingletonInstance().getCommonReader_File_And_S3() );
		
		return spectralFile_Reader__IF;
	}
}
