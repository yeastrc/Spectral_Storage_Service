package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames;

import java.io.File;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataException;

/**
 * Get the Subdirectory path for the given hash value 
 * and return the subdirectory that the storage files will be in
 * 
 * Create (if not exist) the Subdirectory path for the given hash value 
 * and return the subdirectory that the storage files will be/go in
 */
public class GetOrCreateSpectralStorageSubPath {

	private static final Logger log = Logger.getLogger(GetOrCreateSpectralStorageSubPath.class);
	
	private enum CreateIfNotExist { YES, NO }
	
	/**
	 * private constructor
	 */
	private GetOrCreateSpectralStorageSubPath(){}
	public static GetOrCreateSpectralStorageSubPath getInstance( ) throws Exception {
		GetOrCreateSpectralStorageSubPath instance = new GetOrCreateSpectralStorageSubPath();
		return instance;
	}


	/**
	 * Get sub dirs 
	 * 
	 * @param hash
	 * @param outputBaseDir
	 * @return - subdirectory that the storage files will go in
	 * @throws Exception
	 */
	public File getDirsForHash( String hash, File outputBaseDir ) throws Exception {
		
		File checkHashForOSpathDelims = new File( hash );
		
		if ( ! checkHashForOSpathDelims.getName().equals( hash ) ) {
			
			//  Filename created from hash is not same as hash so return null.
			
			String msg = "Hash has OS Delimiters so throw error.  hash: " + hash;
			log.error( msg );
			throw new SpectralStorageDataException( msg );
		}
		
		return getOrCreateDirsForHash( hash, outputBaseDir, CreateIfNotExist.NO );
	}
	
	
	
	/**
	 * Create sub dirs if not exist
	 * 
	 * @param hash
	 * @param outputBaseDir
	 * @return - subdirectory that the storage files will go in
	 * @throws Exception
	 */
	public File createDirsForHashIfNotExists( String hash, File outputBaseDir ) throws Exception {
		
		return getOrCreateDirsForHash( hash, outputBaseDir, CreateIfNotExist.YES );
	}
	
	
	
	/**
	 * @param hash
	 * @param outputBaseDir
	 * @param createIfNotExist
	 * @return
	 * @throws Exception
	 */
	private File getOrCreateDirsForHash( String hash, File outputBaseDir, CreateIfNotExist createIfNotExist ) throws Exception {
		
		String subDirName_One = hash.substring( 0, 1 );
		File subDir_One = createDirIfNotExists( outputBaseDir, subDirName_One, createIfNotExist );
		
		if ( subDir_One == null ) {
			return null;
		}
		
		String subDirName_Two = hash.substring( 1, 2 );
		File subDir_Two = createDirIfNotExists( subDir_One, subDirName_Two, createIfNotExist );

		if ( subDir_Two == null ) {
			return null;
		}
		
		String subDirName_Three = hash.substring( 2, 3 );
		File subDir_Three = createDirIfNotExists( subDir_Two, subDirName_Three, createIfNotExist );

		if ( subDir_Three == null ) {
			return null;
		}
		
		return subDir_Three;
	}
	
	/**
	 * @param parentDir
	 * @param dirString
	 * @param createIfNotExist
	 * @return
	 * @throws Exception
	 */
	private File createDirIfNotExists( File parentDir, String dirString, CreateIfNotExist createIfNotExist ) throws Exception {

		File subDirForOutput = new File( parentDir, dirString );
		
		if ( ! subDirForOutput.exists() ) {
			if ( createIfNotExist == CreateIfNotExist.NO ) {
				return null;
			}
			subDirForOutput.mkdir();
		}
		
		if ( ! subDirForOutput.exists() ) {
			String msg = "Directory does not exist or failed to create it: " + subDirForOutput;
			log.error(msg);
			throw new Exception(msg);
		}
		
		return subDirForOutput;
	}
	
}
