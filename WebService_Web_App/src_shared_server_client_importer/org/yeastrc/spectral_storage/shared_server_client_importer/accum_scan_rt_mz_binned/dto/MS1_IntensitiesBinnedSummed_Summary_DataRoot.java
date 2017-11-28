package org.yeastrc.spectral_storage.shared_server_client_importer.accum_scan_rt_mz_binned.dto;

public class MS1_IntensitiesBinnedSummed_Summary_DataRoot {

	private String jsonContents = " 'BinMax' props are max bin values.  bin values are the start of the bins. "
			+ " Have 'MaxPossibleValue' props since MaxPossibleValue is BinMax + Size of bin " ;
	
	private long binnedSummedIntensityCount;
	
	// All Retention Time time values are in seconds
	 
	private double rtBinSizeInSeconds;
	private double rtBinMinInSeconds;
	private double rtBinMaxInSeconds;
	private double rtMaxPossibleValueInSeconds;
	
	//  All M/Z values are in single m/z
	
	private double mzBinSizeInMZ;
	private double mzBinMinInMZ;
	private double mzBinMaxInMZ;
	private double mzMaxPossibleValueInMZ;

	public double getRtBinSizeInSeconds() {
		return rtBinSizeInSeconds;
	}
	public void setRtBinSizeInSeconds(double rtBinSizeInSeconds) {
		this.rtBinSizeInSeconds = rtBinSizeInSeconds;
	}
	public double getRtBinMinInSeconds() {
		return rtBinMinInSeconds;
	}
	public void setRtBinMinInSeconds(double rtBinMinInSeconds) {
		this.rtBinMinInSeconds = rtBinMinInSeconds;
	}
	public double getRtBinMaxInSeconds() {
		return rtBinMaxInSeconds;
	}
	public void setRtBinMaxInSeconds(double rtBinMaxInSeconds) {
		this.rtBinMaxInSeconds = rtBinMaxInSeconds;
	}
	public double getRtMaxPossibleValueInSeconds() {
		return rtMaxPossibleValueInSeconds;
	}
	public void setRtMaxPossibleValueInSeconds(double rtMaxPossibleValueInSeconds) {
		this.rtMaxPossibleValueInSeconds = rtMaxPossibleValueInSeconds;
	}
	public double getMzBinSizeInMZ() {
		return mzBinSizeInMZ;
	}
	public void setMzBinSizeInMZ(double mzBinSizeInMZ) {
		this.mzBinSizeInMZ = mzBinSizeInMZ;
	}
	public double getMzBinMinInMZ() {
		return mzBinMinInMZ;
	}
	public void setMzBinMinInMZ(double mzBinMinInMZ) {
		this.mzBinMinInMZ = mzBinMinInMZ;
	}
	public double getMzBinMaxInMZ() {
		return mzBinMaxInMZ;
	}
	public void setMzBinMaxInMZ(double mzBinMaxInMZ) {
		this.mzBinMaxInMZ = mzBinMaxInMZ;
	}
	public double getMzMaxPossibleValueInMZ() {
		return mzMaxPossibleValueInMZ;
	}
	public void setMzMaxPossibleValueInMZ(double mzMaxPossibleValueInMZ) {
		this.mzMaxPossibleValueInMZ = mzMaxPossibleValueInMZ;
	}
	private double intensityBinnedMin;
	private double intensityBinnedMax;
	
	
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
