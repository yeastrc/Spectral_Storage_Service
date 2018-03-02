package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.scans_lvl_gt_1_partial.to_data_file_reader_objects;

import java.util.Collections;
import java.util.List;

import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.scans_lvl_gt_1_partial.SpectralFile_ScansLvlGt1Partial_FileContents_Root_IF;

/**
 * Data for Main Data File Reader - SpectralFile_Reader_GZIP_V_003
 * 
 * Scans Level > 1 Partial  File File Root object
 *
 */
public class SpectralFile_ScansLvlGt1Partial_TDFR_FileContents_Root_V_003 implements SpectralFile_ScansLvlGt1Partial_FileContents_Root_IF {

	private short version;

	private List<SpectralFile_ScansLvlGt1Partial_TDFR_SingleScan_V_003> scanDataEntries;

	/**
	 * @param scanNumber
	 * @return
	 */
	public SpectralFile_ScansLvlGt1Partial_TDFR_SingleScan_V_003 get_SingleScan_ForScanNumber( int scanNumber ) {

		SpectralFile_ScansLvlGt1Partial_TDFR_SingleScan_V_003 searchKey = new SpectralFile_ScansLvlGt1Partial_TDFR_SingleScan_V_003();
		searchKey.setScanNumber( scanNumber );

		int searchKeyIndex = Collections.binarySearch( scanDataEntries, searchKey );
		if ( searchKeyIndex < 0 ) {
			//  Scan number not found in index
			return null;  //  EARLY RETURN
		}

		SpectralFile_ScansLvlGt1Partial_TDFR_SingleScan_V_003 indexEntry = scanDataEntries.get( searchKeyIndex );
		
		return indexEntry;
	}
	
	public short getVersion() {
		return version;
	}

	public void setVersion(short version) {
		this.version = version;
	}

	public List<SpectralFile_ScansLvlGt1Partial_TDFR_SingleScan_V_003> getScanDataEntries() {
		return scanDataEntries;
	}

	public void setScanDataEntries(List<SpectralFile_ScansLvlGt1Partial_TDFR_SingleScan_V_003> scanDataEntries) {
		this.scanDataEntries = scanDataEntries;
	}
}
