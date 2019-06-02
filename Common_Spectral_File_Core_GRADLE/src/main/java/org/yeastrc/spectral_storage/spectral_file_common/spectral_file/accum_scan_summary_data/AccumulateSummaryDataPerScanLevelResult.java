package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.accum_scan_summary_data;

import java.util.List;

/**
 * Data from AccumulateSummaryDataPerScanLevel
 *
 */
public class AccumulateSummaryDataPerScanLevelResult {

	private List<AccumulateSummaryDataPerScanLevelSingleLevelResult> summaryDataPerScanLevelList;

	public List<AccumulateSummaryDataPerScanLevelSingleLevelResult> getSummaryDataPerScanLevelList() {
		return summaryDataPerScanLevelList;
	}

	public void setSummaryDataPerScanLevelList(
			List<AccumulateSummaryDataPerScanLevelSingleLevelResult> summaryDataPerScanLevelList) {
		this.summaryDataPerScanLevelList = summaryDataPerScanLevelList;
	}
}
