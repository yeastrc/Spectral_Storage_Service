package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.check_if_spectral_file_exists;

import java.io.File;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.CreateSpectralStorageFilenames;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.GetOrCreateSpectralStorageSubPath;

/**
 * Check if the spectral storage file already exists for this hash key
 * 
 * For when stored on local file system
 *
 */
public class CheckIfSpectralFileAlreadyExists_LocalFilesystem {

	private static final Logger log = LoggerFactory.getLogger(CheckIfSpectralFileAlreadyExists_LocalFilesystem.class);
	/**
	 * private constructor
	 */
	private CheckIfSpectralFileAlreadyExists_LocalFilesystem(){}
	public static CheckIfSpectralFileAlreadyExists_LocalFilesystem getInstance( ) throws Exception {
		CheckIfSpectralFileAlreadyExists_LocalFilesystem instance = new CheckIfSpectralFileAlreadyExists_LocalFilesystem();
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

		if ( dataFile.exists() ) {
			System.out.println( "dataFile DOES exist: "
					+ dataFile.getAbsolutePath() );
		} else {
			System.out.println( "dataFile does NOT exist: "
					+ dataFile.getAbsolutePath() );
		}
		
		if ( dataIndexSpectralFilesCompleteFile.exists() ) {
			System.out.println( "dataIndexSpectralFilesCompleteFile DOES exist: "
					+ dataIndexSpectralFilesCompleteFile.getAbsolutePath() );
		} else {
			System.out.println( "dataIndexSpectralFilesCompleteFile does NOT exist: "
					+ dataIndexSpectralFilesCompleteFile.getAbsolutePath() );
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
