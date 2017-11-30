package org.yeastrc.spectral_storage.shared_server_client_importer.accum_scan_rt_mz_binned.dto;

public class MS1_IntensitiesBinnedSummed_Summary_DataRoot {

	private String jsonContents = " 'BinMax' props are max bin values.  bin values are the start of the bins. "
			+ " Have 'MaxPossibleValue' props since MaxPossibleValue is BinMax + Size of bin " ;
	
	private long binnedSummedIntensityCount;
	
	// All Retention Time time values are in seconds
	 
	private long rtBinSizeInSeconds;
	private long rtBinMinInSeconds;
	private long rtBinMaxInSeconds;
	private long rtMaxPossibleValueInSeconds;
	
	//  All M/Z values are in single m/z
	
	private long mzBinSizeInMZ;
	private long mzBinMinInMZ;
	private long mzBinMaxInMZ;
	private long mzMaxPossibleValueInMZ;

	private double intensityBinnedMin;
	private double intensityBinnedMax;
	
	
	public long getRtBinSizeInSeconds() {
		return rtBinSizeInSeconds;
	}
	public void setRtBinSizeInSeconds(long rtBinSizeInSeconds) {
		this.rtBinSizeInSeconds = rtBinSizeInSeconds;
	}
	public long getRtBinMinInSeconds() {
		return rtBinMinInSeconds;
	}
	public void setRtBinMinInSeconds(long rtBinMinInSeconds) {
		this.rtBinMinInSeconds = rtBinMinInSeconds;
	}
	public long getRtBinMaxInSeconds() {
		return rtBinMaxInSeconds;
	}
	public void setRtBinMaxInSeconds(long rtBinMaxInSeconds) {
		this.rtBinMaxInSeconds = rtBinMaxInSeconds;
	}
	public long getRtMaxPossibleValueInSeconds() {
		return rtMaxPossibleValueInSeconds;
	}
	public void setRtMaxPossibleValueInSeconds(long rtMaxPossibleValueInSeconds) {
		this.rtMaxPossibleValueInSeconds = rtMaxPossibleValueInSeconds;
	}
	public long getMzBinSizeInMZ() {
		return mzBinSizeInMZ;
	}
	public void setMzBinSizeInMZ(long mzBinSizeInMZ) {
		this.mzBinSizeInMZ = mzBinSizeInMZ;
	}
	public long getMzBinMinInMZ() {
		return mzBinMinInMZ;
	}
	public void setMzBinMinInMZ(long mzBinMinInMZ) {
		this.mzBinMinInMZ = mzBinMinInMZ;
	}
	public long getMzBinMaxInMZ() {
		return mzBinMaxInMZ;
	}
	public void setMzBinMaxInMZ(long mzBinMaxInMZ) {
		this.mzBinMaxInMZ = mzBinMaxInMZ;
	}
	public long getMzMaxPossibleValueInMZ() {
		return mzMaxPossibleValueInMZ;
	}
	public void setMzMaxPossibleValueInMZ(long mzMaxPossibleValueInMZ) {
		this.mzMaxPossibleValueInMZ = mzMaxPossibleValueInMZ;
	}

	
	public String getJsonContents() {
		return jsonContents;
	}
	public void setJsonContents(String jsonContents) {
		this.jsonContents = jsonContents;
	}
	public long getBinnedSummedIntensityCount() {
		return binnedSummedIntensityCount;
	}
	public void setBinnedSummedIntensityCount(long binnedSummedIntensityCount) {
		this.binnedSummedIntensityCount = binnedSummedIntensityCount;
	}

	public double getIntensityBinnedMin() {
		return intensityBinnedMin;
	}
	public void setIntensityBinnedMin(double intensityBinnedMin) {
		this.intensityBinnedMin = intensityBinnedMin;
	}
	public double getIntensityBinnedMax() {
		return intensityBinnedMax;
	}
	public void setIntensityBinnedMax(double intensityBinnedMax) {
		this.intensityBinnedMax = intensityBinnedMax;
	}
	
}
