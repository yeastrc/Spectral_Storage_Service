package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.data_file.on_disk_dto;


/**
 * Data for Single Scan
 * 
 * Includes blob containing peaks which is written immediately after this data
 * 
 * Data is written in the order listed in this class
 *
 */
public class SpectralFile_SingleScan_V_005 {

	private byte level;
	private int scanNumber;
	private float retentionTime;
	
	/**
	 * Is centroid value
	 * 0 - false
	 * 1 - true
	 */
	private byte isCentroid;

	/**
	 * Value per Scan, retrieved from file, not computed: mzML: <cvParam cvRef="MS" accession="MS:1000285" name="total ion current" value="5.0278541e05"/>
	 * Otherwise computed from scan peaks
	 */
	private float totalIonCurrent;
	
	/**
	 * Set to Float.NEGATIVE_INFINITY in disk file if not available.  Set to null in Java objects.
	 */
	private Float ionInjectionTime;

	
	//  Only applicable where level > 1
	
	private int parentScanNumber;
	private byte precursorCharge;
	private double precursor_M_Over_Z;
	
	/**
	 * Number of Scan Peaks
	 */
	private int numberScanPeaks;
	
	/**
	 * Length of scan Peaks which is written immediately after the data in this class.
	 */
	private int scanPeaksDataLength;
	
	
	
	
	/////////////////////////////////////////////////
	/////////////////////////////////////////////////
	/////////////////////////////////////////////////
	
	//    blob containing Scan Peaks which is written immediately after this data
	
	//           Always write Scan Peaks Last
	
	/**
	 * Scan Peaks as byte array to write to or read from disk.
	 * 
	 * Serialized and GZIPped List<SpectralFile_SingleScanPeak_V_005> scanPeaksAsObjectArray;
	 */
	private byte[] scanPeaksAsByteArray;
		
}
