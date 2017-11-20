package org.yeastrc.spectral_storage.scan_file_processor.check_if_spectral_file_exists;

import java.io.File;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.CreateSpectralStorageFilenames;

/**
 * Check if the spectral storage file already exists for this hash key
 *
 */
public class CheckIfSpectralFileAlreadyExists {

	private static final Logger log = Logger.getLogger(CheckIfSpectralFileAlreadyExists.class);
	/**
	 * private constructor
	 */
	private CheckIfSpectralFileAlreadyExists(){}
	public static CheckIfSpectralFileAlreadyExists getInstance( ) throws Exception {
		CheckIfSpectralFileAlreadyExists instance = new CheckIfSpectralFileAlreadyExists();
		return instance;
	}

	
	/**
	 * @param scanFile
	 * @param uploadedScanFilename
	 * @param subDirForStorageFiles
	 * @param hash_String
	 * @param hash_Bytes
	 * @return
	 * @throws Exception
	 */
	public boolean doesSpectralFileAlreadyExist( 
			File scanFile, 
//			String uploadedScanFilename, 
			File subDirForStorageFiles, 
			String hash_String, 
			byte[] hash_Bytes ) throws Exception {
		
		//  TODO  maybe validate the uploadedScanFilename in the data file

		String dataFilename =
				CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Data_Filename( hash_String );
		File dataFile = new File( subDirForStorageFiles, dataFilename );

		String dataIndexSpectralFilesCompleteFilename =
				CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Data_Index_Files_Complete_Filename( hash_String );
		File dataIndexSpectralFilesCompleteFile = new File( subDirForStorageFiles, dataIndexSpectralFilesCompleteFilename );
		
		if ( dataIndexSpectralFilesCompleteFile.exists() ) {
			
			//  dataIndexSpectralFilesCompleteFile is created after all the other files so if it exists,
			//  processing completed successfully
			
			if ( ! dataFile.exists() ) {
				String msg = "...Complete file exists but Data File does not exist.  "
						+ "complete file: " + dataIndexSpectralFilesCompleteFile.getCanonicalPath()
						+ ", data file: " + dataFile.getCanonicalPath();
				log.error( msg );
				throw new SpectralStorageProcessingException(msg);
			}
			
			return true;
		}
		
		if ( dataFile.exists() ) {
			String msg = "...complete file does NOT exist but Data File DOES exist."
					+ " Must have been a failure that needs cleaning up.  "
					+ "complete file: " + dataIndexSpectralFilesCompleteFile.getCanonicalPath()
					+ ", data file: " + dataFile.getCanonicalPath();
			log.error( msg );
			throw new SpectralStorageProcessingException(msg);
		}
		
		return false;
	}
}
