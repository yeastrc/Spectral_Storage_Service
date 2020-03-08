package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.file_contents_hash_processing;

import java.io.File;
import java.io.FileInputStream;

import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.slf4j.Logger;

/**
 * 
 *
 */
public class Compute_File_Hashes {

	private static final Logger log = LoggerFactory.getLogger(Compute_File_Hashes.class);
	
	/**
	 * private constructor
	 */
	private Compute_File_Hashes(){}
	public static Compute_File_Hashes getInstance( ) throws Exception {
		Compute_File_Hashes instance = new Compute_File_Hashes();
		return instance;
	}

	/**
	 * @param inputFile
	 * @return
	 * @throws Exception 
	 */
	public Compute_Hashes compute_File_Hashes( File inputFile ) throws Exception {

		if ( log.isInfoEnabled() ) {
			log.info( "Computing File Hashes for file: " + inputFile.getAbsolutePath() );
		}
		
		if ( ! inputFile.exists() ) {
			String msg = "Input file for compute_File_Hashes does not exist.  inputFile Absolute Path: " 
					+ inputFile.getAbsolutePath()
					+ ", inputFile Canonical Path: " 
					+ inputFile.getCanonicalPath();
			log.error( msg );
			throw new SpectralStorageProcessingException( msg );
		}
		
		Compute_Hashes compute_Hashes = Compute_Hashes.getNewInstance();

		try ( FileInputStream fis = new FileInputStream( inputFile ); ) {
			
			byte[] dataBytes = new byte[4096];

			int numberBytesRead = 0; 

			while ( ( numberBytesRead = fis.read( dataBytes ) ) != -1 ) {
				compute_Hashes.updateHashesComputing( dataBytes, numberBytesRead );
			}
		}

	    return compute_Hashes;
	}
	
}
