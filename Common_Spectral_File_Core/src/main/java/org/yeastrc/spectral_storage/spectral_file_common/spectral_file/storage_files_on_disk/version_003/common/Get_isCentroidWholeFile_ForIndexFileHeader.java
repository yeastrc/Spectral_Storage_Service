package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.common;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.ScanCentroidedConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataException;

/**
 * Get isCentroidWholeFile Value For Index File Header
 *
 */
public class Get_isCentroidWholeFile_ForIndexFileHeader {

	private static final Logger log = LoggerFactory.getLogger(Get_isCentroidWholeFile_ForIndexFileHeader.class);
	
	/**
	 * private constructor
	 */
	private Get_isCentroidWholeFile_ForIndexFileHeader(){}
	public static Get_isCentroidWholeFile_ForIndexFileHeader getInstance( ) throws Exception {
		Get_isCentroidWholeFile_ForIndexFileHeader instance = new Get_isCentroidWholeFile_ForIndexFileHeader();
		return instance;
	}
	
	/**
	 * Get isCentroidWholeFile Value For Index File Header
	 * 
	 * @param isCentroidUniqueValuesInScans
	 * @return
	 * @throws SpectralStorageDataException 
	 */
	public byte get_isCentroidWholeFile_ForIndexFileHeader( Set<Byte> isCentroidUniqueValuesInScans ) throws SpectralStorageDataException {
		
		if ( isCentroidUniqueValuesInScans.contains( ScanCentroidedConstants.SCAN_CENTROIDED_TRUE )
				&& isCentroidUniqueValuesInScans.contains( ScanCentroidedConstants.SCAN_CENTROIDED_FALSE ) ) {
			
			return ScanCentroidedConstants.SCAN_CENTROIDED_VALUES_IN_FILE_BOTH; 
		
		} else if ( isCentroidUniqueValuesInScans.contains( ScanCentroidedConstants.SCAN_CENTROIDED_TRUE ) ) {
			
			return ScanCentroidedConstants.SCAN_CENTROIDED_TRUE;

		} else if ( isCentroidUniqueValuesInScans.contains( ScanCentroidedConstants.SCAN_CENTROIDED_FALSE ) ) {
			
			return ScanCentroidedConstants.SCAN_CENTROIDED_FALSE;
			
		}
			
		String msg = "Unknown values in param isCentroidUniqueValuesInScans: " + StringUtils.join( isCentroidUniqueValuesInScans );
		log.error( msg );
		throw new SpectralStorageDataException(msg);
	}
	
}
