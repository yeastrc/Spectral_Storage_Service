package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.accum_scan_summary_data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.ScanCentroidedConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScanPeak_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScan_Common;

/**
 * Accumulate Summary Data Per Scan Level
 *
 */
public class AccumulateSummaryDataPerScanLevel {
	
//	private static final Logger log = LoggerFactory.getLogger(AccumulateSummaryDataPerScanLevel.class);
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
		
		Byte isCentroidOfScan = scan.getIsCentroid();
		
		{ //  Update holder.isCentroidScanLevel
			if ( holder.isCentroidScanLevel == null ) {
				// Not set for level so add it
				holder.isCentroidScanLevel = isCentroidOfScan;
			} else if ( holder.isCentroidScanLevel.byteValue() == ScanCentroidedConstants.SCAN_CENTROIDED_VALUES_IN_FILE_BOTH ) {
				//  Already set to both values for level so just skip to next scan
			} else if ( holder.isCentroidScanLevel.byteValue() !=  isCentroidOfScan.byteValue() ) {
				// Have both values so set to both values in map for level
				holder.isCentroidScanLevel = ScanCentroidedConstants.SCAN_CENTROIDED_VALUES_IN_FILE_BOTH;
			}
		}
		
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
			Byte scanLevel = totalsPerScanLevelMapEntry.getKey();
			InternalPerScanLevelHolder internalPerScanLevelHolder = totalsPerScanLevelMapEntry.getValue();
			if ( internalPerScanLevelHolder.isCentroidScanLevel == null ) {
				throw new IllegalStateException( "internalPerScanLevelHolder.isCentroidScanLevelisCentroidScanLevel == null.  scanLevel: " + String.valueOf( scanLevel ) );
			}
			AccumulateSummaryDataPerScanLevelSingleLevelResult singleLevelResult = new AccumulateSummaryDataPerScanLevelSingleLevelResult();
			singleLevelResultList.add( singleLevelResult );
			singleLevelResult.setScanLevel( scanLevel );
			singleLevelResult.setNumberOfScans( internalPerScanLevelHolder.scanCount );
			singleLevelResult.setIsCentroidScanLevel( internalPerScanLevelHolder.isCentroidScanLevel ); 
			singleLevelResult.setTotalIonCurrent( internalPerScanLevelHolder.scanIntensitiesSummedForLevel );
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

		/**
		 * Is centroid values for this scan level
		 * 0 - false - ScanCentroidedConstants.SCAN_CENTROIDED_FALSE
		 * 1 - true - ScanCentroidedConstants.SCAN_CENTROIDED_TRUE
		 * 2 - both - both false and true found for this scan level the file
		 *            ScanCentroidedConstants.SCAN_CENTROIDED_VALUES_IN_FILE_BOTH
		 */
		Byte isCentroidScanLevel;
	}
	
}
