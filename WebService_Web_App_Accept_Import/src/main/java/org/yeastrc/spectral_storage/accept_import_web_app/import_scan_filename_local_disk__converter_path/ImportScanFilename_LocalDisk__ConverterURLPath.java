package org.yeastrc.spectral_storage.accept_import_web_app.import_scan_filename_local_disk__converter_path;

import java.io.File;
import java.util.List;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_ScanFilenameSuffix_To_ConverterMapping;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_ScanFilenameSuffix_To_ConverterMapping.ConfigData_ScanFilenameSuffix_To_ConverterMapping_SingleEntry;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileFileUploadInternalException;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileWebappInternalException;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;

/**
 * Get Import Scan Filename in either Temp Upload Dir or Processing Dir (Dir provided)
 * 
 * For when store on local disk either the scan file or a soft link to the file (when path to disk file submitted)
 *
 */
public class ImportScanFilename_LocalDisk__ConverterURLPath {

	private static final Logger log = LoggerFactory.getLogger( ImportScanFilename_LocalDisk__ConverterURLPath.class );

	//  private constructor
	private ImportScanFilename_LocalDisk__ConverterURLPath() { }
	
	/**
	 * @return newly created instance
	 */
	public static ImportScanFilename_LocalDisk__ConverterURLPath getInstance() { 
		return new ImportScanFilename_LocalDisk__ConverterURLPath(); 
	}
	
	/**
	 * 
	 *
	 */
	public static class ImportScanFilename_LocalDisk__ConverterURLPath__Result {
		
		private String scanFilename;
		private ConfigData_ScanFilenameSuffix_To_ConverterMapping_SingleEntry configData_ScanFilenameSuffix_To_ConverterMapping_SingleEntry;
		public String getScanFilename() {
			return scanFilename;
		}
		public ConfigData_ScanFilenameSuffix_To_ConverterMapping_SingleEntry getConfigData_ScanFilenameSuffix_To_ConverterMapping_SingleEntry() {
			return configData_ScanFilenameSuffix_To_ConverterMapping_SingleEntry;
		}
	}
	
	/**
	 * @param dir
	 * @return
	 * @throws SpectralFileFileUploadInternalException
	 * @throws SpectralFileWebappInternalException 
	 */
	public ImportScanFilename_LocalDisk__ConverterURLPath__Result getImportScanFilename_LocalDisk__ConverterURLPath( File dir ) throws SpectralFileFileUploadInternalException, SpectralFileWebappInternalException {
		
		ImportScanFilename_LocalDisk__ConverterURLPath__Result result = null;

		List<ConfigData_ScanFilenameSuffix_To_ConverterMapping_SingleEntry> scanfilename_suffix_to_converter_base_url_mapping_List = 
				ConfigData_ScanFilenameSuffix_To_ConverterMapping.getSingletonInstance().getEntries();
		
		
		File[] uploadFileTempDir_Files = dir.listFiles();

		for ( File dirEntry : uploadFileTempDir_Files ) {
			
			String dirEntryFilename = dirEntry.getName();
			
			if ( dirEntryFilename.startsWith( ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX ) ) {

				for ( ConfigData_ScanFilenameSuffix_To_ConverterMapping_SingleEntry entry : scanfilename_suffix_to_converter_base_url_mapping_List ) {

					if ( dirEntryFilename.endsWith( entry.getScan_filename_suffix() ) ) {
						if ( result != null ) {
							String msg = "Found more than one scan file. Previous filename: " + result.scanFilename
									+ ", current filename: " + dirEntry.getName();
							log.error( msg );
							throw new SpectralFileFileUploadInternalException(msg);
						}

						result = new ImportScanFilename_LocalDisk__ConverterURLPath__Result();

						result.scanFilename = dirEntry.getName();
						result.configData_ScanFilenameSuffix_To_ConverterMapping_SingleEntry = entry;
					}
				}
			}
		}
		if ( result == null ) {
			
			String suffixes = null;

			for ( ConfigData_ScanFilenameSuffix_To_ConverterMapping_SingleEntry entry : scanfilename_suffix_to_converter_base_url_mapping_List ) {
				
				if ( suffixes == null ) {
					suffixes = entry.getScan_filename_suffix();
				} else {
					suffixes += ", " + entry.getScan_filename_suffix();
				}
			}
			
			
			String msg = "No Scan file Found. Allowed filename prefix: " 
					+ ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX 
					+ ", allowed filename suffixes: "
					+ suffixes
					;
			log.warn( msg );
			
			return null;  //  EARLY EXIT
		}
		
		return result;
	}
}
