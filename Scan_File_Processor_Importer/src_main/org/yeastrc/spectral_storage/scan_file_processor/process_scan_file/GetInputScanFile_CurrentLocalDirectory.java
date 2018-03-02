package org.yeastrc.spectral_storage.scan_file_processor.process_scan_file;

import java.io.File;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataNotFoundException;

/**
 * 
 *
 */
public class GetInputScanFile_CurrentLocalDirectory {

	private static final Logger log = Logger.getLogger( GetInputScanFile_CurrentLocalDirectory.class );
			
	private GetInputScanFile_CurrentLocalDirectory() {}
	public static GetInputScanFile_CurrentLocalDirectory getInstance() {
		return new GetInputScanFile_CurrentLocalDirectory();
	}
	
	/**
	 * @return
	 * @throws Exception 
	 */
	public File getInputScanFile_CurrentLocalDirectory() throws Exception {
		
		File currentDir = new File( "." );
		
		File[] dirContents = currentDir.listFiles();
		
		for ( File dirEntry : dirContents ) {
			String dirEntryFilename = dirEntry.getName();
			if ( dirEntryFilename.startsWith( ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX )
					&& ( dirEntryFilename.endsWith( ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML ) 
							|| dirEntryFilename.endsWith( ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML ) ) ) {
				return dirEntry; // EARLY EXIT
			}
		}
		
		String msg = "No file found that starts with '" + ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX
				+ "' and ends with '" + ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML
				+ "' or '" + ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML
				+ "' in dir: " + currentDir.getCanonicalPath();
		log.error( msg );
		throw new SpectralStorageDataNotFoundException(msg);
		
//		return null;
		
	}

}
