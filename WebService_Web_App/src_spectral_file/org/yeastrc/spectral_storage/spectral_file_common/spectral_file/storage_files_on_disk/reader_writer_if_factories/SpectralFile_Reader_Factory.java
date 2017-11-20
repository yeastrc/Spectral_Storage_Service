package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories;

import java.io.File;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.GetOrCreateSpectralStorageSubPath;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.data_file.reader_writer.SpectralFile_Reader_GZIP_V_003;

/**
 * Create a SpectralFile_Reader__IF of the version based on the data files for the hash
 *
 */
public class SpectralFile_Reader_Factory {

	private static final Logger log = Logger.getLogger(SpectralFile_Reader_Factory.class);
	
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
	 * @throws Exception
	 */
	public SpectralFile_Reader__IF getSpectralFile_Writer_ForHash( String hashKey, File scanStorageBaseDirectoryFile ) throws Exception {

		SpectralFile_Reader__IF spectralFile_Reader__IF = null;

		//  null returned if directory does not exist
		File subDirForStorageFiles = 
				GetOrCreateSpectralStorageSubPath.getInstance().getDirsForHash( hashKey, scanStorageBaseDirectoryFile );
		
		if ( subDirForStorageFiles == null ) {
			
		}
		
//		
//		fileVersionInFile = dataInputStream.readShort();
		
		//  TODO For Now hard code to version 3
		
		spectralFile_Reader__IF = SpectralFile_Reader_GZIP_V_003.getInstance();
		
		spectralFile_Reader__IF.init( hashKey, scanStorageBaseDirectoryFile );
		
		return spectralFile_Reader__IF;
	}
}
