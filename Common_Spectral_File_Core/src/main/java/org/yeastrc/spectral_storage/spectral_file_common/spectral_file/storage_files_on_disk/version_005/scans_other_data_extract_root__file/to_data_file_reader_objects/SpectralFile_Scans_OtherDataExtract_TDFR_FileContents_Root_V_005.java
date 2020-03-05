package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.scans_other_data_extract_root__file.to_data_file_reader_objects;

import java.util.Collections;
import java.util.List;

import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.scans_other_data_extract.SpectralFile_Scans_OtherDataExtract_FileContents_Root_IF;

/**
 * Data for Main Data File Reader - SpectralFile_Reader_GZIP_V_005
 * 
 * Scans Other Extract Data File Root object
 *
 */
public class SpectralFile_Scans_OtherDataExtract_TDFR_FileContents_Root_V_005 implements SpectralFile_Scans_OtherDataExtract_FileContents_Root_IF {

	private short version;

	private List<SpectralFile_Scans_OtherDataExtract_TDFR_SingleScan_V_005> scanDataEntries;

	/**
	 * @param scanNumber
	 * @return
	 */
	public SpectralFile_Scans_OtherDataExtract_TDFR_SingleScan_V_005 get_SingleScan_ForScanNumber( int scanNumber ) {

		SpectralFile_Scans_OtherDataExtract_TDFR_SingleScan_V_005 searchKey = new SpectralFile_Scans_OtherDataExtract_TDFR_SingleScan_V_005();
		searchKey.setScanNumber( scanNumber );

		int searchKeyIndex = Collections.binarySearch( scanDataEntries, searchKey );
		if ( searchKeyIndex < 0 ) {
			//  Scan number not found in index
			return null;  //  EARLY RETURN
		}

		SpectralFile_Scans_OtherDataExtract_TDFR_SingleScan_V_005 indexEntry = scanDataEntries.get( searchKeyIndex );
		
		return indexEntry;
	}
	
	public short getVersion() {
		return version;
	}

	public void setVersion(short version) {
		this.version = version;
	}

	public List<SpectralFile_Scans_OtherDataExtract_TDFR_SingleScan_V_005> getScanDataEntries() {
		return scanDataEntries;
	}

	public void setScanDataEntries(List<SpectralFile_Scans_OtherDataExtract_TDFR_SingleScan_V_005> scanDataEntries) {
		this.scanDataEntries = scanDataEntries;
	}

}
