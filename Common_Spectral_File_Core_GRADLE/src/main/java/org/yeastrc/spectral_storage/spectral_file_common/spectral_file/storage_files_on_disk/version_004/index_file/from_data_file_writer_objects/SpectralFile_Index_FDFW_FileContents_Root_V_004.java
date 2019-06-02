package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_004.index_file.from_data_file_writer_objects;

import java.util.List;

/**
 * Data from Main Data File Writer - SpectralFile_Writer_GZIP_V_003
 * 
 * Root object
 *
 */
public class SpectralFile_Index_FDFW_FileContents_Root_V_004 {

	private short version;

	private long totalBytesForAllSingleScans;
	
	private List<SpectralFile_Index_FDFW_SingleScan_V_004> indexScanEntries;

	public short getVersion() {
		return version;
	}

	public void setVersion(short version) {
		this.version = version;
	}

	public long getTotalBytesForAllSingleScans() {
		return totalBytesForAllSingleScans;
	}

	public void setTotalBytesForAllSingleScans(long totalBytesForAllSingleScans) {
		this.totalBytesForAllSingleScans = totalBytesForAllSingleScans;
	}

	public List<SpectralFile_Index_FDFW_SingleScan_V_004> getIndexScanEntries() {
		return indexScanEntries;
	}

	public void setIndexScanEntries(List<SpectralFile_Index_FDFW_SingleScan_V_004> indexScanEntries) {
		this.indexScanEntries = indexScanEntries;
	}


}
