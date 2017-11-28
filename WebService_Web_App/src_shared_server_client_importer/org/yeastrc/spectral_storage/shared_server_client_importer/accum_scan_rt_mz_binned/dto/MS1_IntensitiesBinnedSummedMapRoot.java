package org.yeastrc.spectral_storage.shared_server_client_importer.accum_scan_rt_mz_binned.dto;

import java.util.Map;

public class MS1_IntensitiesBinnedSummedMapRoot {
	
	private String jsonContents;
	public String getJsonContents() {
		return jsonContents;
	}

	public void setJsonContents(String jsonContents) {
		this.jsonContents = jsonContents;
	}

	private MS1_IntensitiesBinnedSummed_Summary_DataRoot summaryData;
	
	/**
	 * for ms 1 scans: Map<RetentionTime_BinStart,Map<MZ_BinStart,SummedIntensity>
	 * 
	 * "_BinStart" means the starting value of the bin.
	 * There may be some minor variance as to which bin a value ends up in due to rounding
	 */
	Map<Double, Map<Double, Double>> ms1_IntensitiesBinnedSummedMap;
	
	

	public MS1_IntensitiesBinnedSummed_Summary_DataRoot getSummaryData() {
		return summaryData;
	}

	public void setSummaryData(MS1_IntensitiesBinnedSummed_Summary_DataRoot summaryData) {
		this.summaryData = summaryData;
	}

	public Map<Double, Map<Double, Double>> getMs1_IntensitiesBinnedSummedMap() {
		return ms1_IntensitiesBinnedSummedMap;
	}

	public void setMs1_IntensitiesBinnedSummedMap(Map<Double, Map<Double, Double>> ms1_IntensitiesBinnedSummedMap) {
		this.ms1_IntensitiesBinnedSummedMap = ms1_IntensitiesBinnedSummedMap;
	}

}
