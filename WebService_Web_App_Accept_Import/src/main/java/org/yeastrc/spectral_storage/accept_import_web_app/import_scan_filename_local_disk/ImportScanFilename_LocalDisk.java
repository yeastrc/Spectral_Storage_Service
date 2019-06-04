package org.yeastrc.spectral_storage.accept_import_web_app.import_scan_filename_local_disk;

import java.io.File;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileFileUploadInternalException;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;

/**
 * Get Import Scan Filename in either Temp Upload Dir or Processing Dir (Dir provided)
 * 
 * For when store on local disk either the scan file or a soft link to the file (when path to disk file submitted)
 *
 */
public class ImportScanFilename_LocalDisk {

	private static final Logger log = Logger.getLogger( ImportScanFilename_LocalDisk.class );

	//  private constructor
	private ImportScanFilename_LocalDisk() { }
	
	/**
	 * @return newly created instance
	 */
	public static ImportScanFilename_LocalDisk getInstance() { 
		return new ImportScanFilename_LocalDisk(); 
	}
	
	/**
	 * @param dir
	 * @return
	 * @throws SpectralFileFileUploadInternalException
	 */
	public String getImportScanFilename_LocalDisk( File dir ) throws SpectralFileFileUploadInternalException {
		
		//  Find the scan filename
		String scanFilenameToMove = null;
		
		File[] uploadFileTempDir_Files = dir.listFiles();

		for ( File dirEntry : uploadFileTempDir_Files ) {
			String dirEntryFilename = dirEntry.getName();
			if ( dirEntryFilename.startsWith( ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX )
					&& ( dirEntryFilename.endsWith( ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML ) 
							|| dirEntryFilename.endsWith( ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML ) ) ) {
				if ( scanFilenameToMove != null ) {
					String msg = "Found more than one scan file. Previous filename: " + scanFilenameToMove
							+ ", current filename: " + dirEntry.getName();
					log.error( msg );
					throw new SpectralFileFileUploadInternalException(msg);
				}
				scanFilenameToMove = dirEntry.getName();
			}
		}
		if ( scanFilenameToMove == null ) {
			String msg = "No Scan file Found. Allowed filenames are: " 
					+ ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX 
					+ ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML
					+ ", and "
					+ ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX 
					+ ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML
					;
			log.warn( msg );
			
			return null;  //  EARLY EXIT
		}
		
		return scanFilenameToMove;
	}
}
