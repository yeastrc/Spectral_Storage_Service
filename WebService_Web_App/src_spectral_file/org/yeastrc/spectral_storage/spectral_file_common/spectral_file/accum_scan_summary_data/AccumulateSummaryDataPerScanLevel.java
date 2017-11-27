package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.accum_scan_summary_data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScanPeak_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScan_Common;

/**
 * Accumulate Summary Data Per Scan Level
 *
 */
public class AccumulateSummaryDataPerScanLevel {
	
//	private static final Logger log = Logger.getLogger(AccumulateSummaryDataPerScanLevel.class);
	private AccumulateSummaryDataPerScanLevel() { }
	public static AccumulateSummaryDataPerScanLevel getInstance() { return new AccumulateSummaryDataPerScanLevel(); }

	private Map<Byte, InternalPerScanLevelHolder> totalsPerScanLevelMap = new HashMap<>();
	
	/**
	 * Add Scan to accumulated totals
	 * @param scan
	 */
	public void addScanToAccum( SpectralFile_SingleScan_Common scan ) {

        double scanIntensitiesSummedForScan = 0;
        
		List<SpectralFile_SingleScanPeak_Common> peakList = scan.getScanPeaksAsObjectArray();
		for ( SpectralFile_SingleScanPeak_Common peak : peakList ) {
        	scanIntensitiesSummedForScan += peak.getIntensity();
		}
		
		Byte scanLevel = scan.getLevel();
		
		InternalPerScanLevelHolder holder = totalsPerScanLevelMap.get( scanLevel );
		if ( holder == null ) {
			holder = new InternalPerScanLevelHolder();
			totalsPerScanLevelMap.put( scanLevel, holder );
		}
		
		holder.scanCount++;
		holder.scanIntensitiesSummedForLevel += scanIntensitiesSummedForScan;
	}
	
	/**
	 * @return
	 */
	public AccumulateSummaryDataPerScanLevelResult getAccumResult() {
		
		//  First put in list and sort by scan level
		List<Map.Entry<Byte, InternalPerScanLevelHolder>> totalsPerScanLevelMap_List = new ArrayList<>( totalsPerScanLevelMap.size() );

		for ( Map.Entry<Byte, InternalPerScanLevelHolder> entry : totalsPerScanLevelMap.entrySet() ) {
			totalsPerScanLevelMap_List.add(entry);
		}
		// sort by scan level
		Collections.sort( totalsPerScanLevelMap_List, new Comparator<Map.Entry<Byte, InternalPerScanLevelHolder>>() {
			@Override
			public int compare(Entry<Byte, InternalPerScanLevelHolder> o1, Entry<Byte, InternalPerScanLevelHolder> o2) {
				if ( o1.getKey() < o2.getKey() ) {
					return -1;
				}
				if ( o1.getKey() > o2.getKey() ) {
					return 1;
				}
				return 0;
			}
		});
		
		AccumulateSummaryDataPerScanLevelResult result = new AccumulateSummaryDataPerScanLevelResult();
		List<AccumulateSummaryDataPerScanLevelSingleLevelResult> singleLevelResultList = new ArrayList<>( totalsPerScanLevelMap.size() );
		result.setSummaryDataPerScanLevelList( singleLevelResultList );
		
		for ( Map.Entry<Byte, InternalPerScanLevelHolder> totalsPerScanLevelMapEntry : totalsPerScanLevelMap_List ) {
			AccumulateSummaryDataPerScanLevelSingleLevelResult singleLevelResult = new AccumulateSummaryDataPerScanLevelSingleLevelResult();
			singleLevelResultList.add( singleLevelResult );
			singleLevelResult.setScanLevel( totalsPerScanLevelMapEntry.getKey() );
			singleLevelResult.setNumberOfScans( totalsPerScanLevelMapEntry.getValue().scanCount );
			singleLevelResult.setTotalIonCurrent( totalsPerScanLevelMapEntry.getValue().scanIntensitiesSummedForLevel );
		}
		
		return result;
	}
	
	/**
	 * 
	 *
	 */
	private static class InternalPerScanLevelHolder {
		
		int scanCount;
		double scanIntensitiesSummedForLevel;
	}
	
	
	
	
}
