package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.index_file.from_data_file_writer_objects;

import java.util.List;

/**
 * Data from Main Data File Writer - SpectralFile_Writer_GZIP_V_003
 * 
 * Root object
 *
 */
public class SpectralFile_Index_FDFW_FileContents_Root_V_003 {

	private short version;

	private long totalBytesForAllSingleScans;
	
	/**
	 * Is centroid values for the whole file
	 * 0 - false
	 * 1 - true
	 * 2 - both - both false and true found in the file
	 */
	private byte isCentroidWholeFile;

	private List<SpectralFile_Index_FDFW_SingleScan_V_003> indexScanEntries;

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

	public byte getIsCentroidWholeFile() {
		return isCentroidWholeFile;
	}

	public void setIsCentroidWholeFile(byte isCentroidWholeFile) {
		this.isCentroidWholeFile = isCentroidWholeFile;
	}

	public List<SpectralFile_Index_FDFW_SingleScan_V_003> getIndexScanEntries() {
		return indexScanEntries;
	}

	public void setIndexScanEntries(List<SpectralFile_Index_FDFW_SingleScan_V_003> indexScanEntries) {
		this.indexScanEntries = indexScanEntries;
	}


}
