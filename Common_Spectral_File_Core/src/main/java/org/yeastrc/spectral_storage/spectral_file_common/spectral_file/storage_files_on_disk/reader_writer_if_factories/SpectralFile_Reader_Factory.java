package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories;

import java.io.File;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3_Holder;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.StorageFile_Version_003_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.data_file.reader_writer.SpectralFile_Reader_GZIP_V_003;

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
	 * @return - null if hashKey not found in system
	 * @throws SpectralStorageProcessingException
	 * @throws Exception
	 */
	public SpectralFile_Reader__IF getSpectralFile_Reader_ForHash( String hashKey, File scanStorageBaseDirectoryFile ) 
			throws Exception, SpectralStorageProcessingException {

		//  TODO  Remove this check , replace with something else maybe
		
		//  null returned if directory does not exist
//		File subDirForStorageFiles = 
//				GetOrCreateSpectralStorageSubPath.getInstance().getDirsForHash( hashKey, scanStorageBaseDirectoryFile );
//		
//		if ( subDirForStorageFiles == null ) {
//			return null;
//		}
		
//		
//		fileVersionInFile = dataInputStream.readShort();
		
		//  Add a cache of API Key to version so don't have to repeatedly open and read the file. 
		
		//  TODO For Now hard code to version 3
		
		SpectralFile_Reader__IF spectralFile_Reader__IF = SpectralFile_Reader_GZIP_V_003.getInstance();

		//  This check is redundant for now but will be more applicable when there is more than one version
		if ( ! spectralFile_Reader__IF.isVersionSupported( StorageFile_Version_003_Constants.FILE_VERSION ) ) {
			String msg = "Created spectralFile_Reader__IF does not support version for hash.  Version for hash: "
					+ StorageFile_Version_003_Constants.FILE_VERSION;
			log.error( msg );
			throw new SpectralStorageProcessingException( msg );
		}
		
		spectralFile_Reader__IF.init( hashKey, CommonReader_File_And_S3_Holder.getSingletonInstance().getCommonReader_File_And_S3() );
		
		return spectralFile_Reader__IF;
	}
}
