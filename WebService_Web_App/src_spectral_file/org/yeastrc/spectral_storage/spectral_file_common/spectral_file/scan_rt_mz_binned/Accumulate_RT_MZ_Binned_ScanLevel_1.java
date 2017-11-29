package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_rt_mz_binned;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.yeastrc.spectral_storage.shared_server_client_importer.accum_scan_rt_mz_binned.dto.MS1_IntensitiesBinnedSummedMapRoot;
import org.yeastrc.spectral_storage.shared_server_client_importer.accum_scan_rt_mz_binned.dto.MS1_IntensitiesBinnedSummed_Summary_DataRoot;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScanPeak_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScan_Common;

/**
 * Accumulate Scan Level 1 data 
 * binned on Retention Time and M/Z
 * 
 * For now hard coded Retention Time bin at per second
 * For now hard coded M/Z bin at per m/z
 *
 * Would need more complicated math to bin at other values
 * 
 */
public class Accumulate_RT_MZ_Binned_ScanLevel_1 {

//	private static final Logger log = Logger.getLogger(Accumulate_RT_MZ_Binned_ScanLevel_1.class);
	private Accumulate_RT_MZ_Binned_ScanLevel_1() { }
	public static Accumulate_RT_MZ_Binned_ScanLevel_1 getInstance() { return new Accumulate_RT_MZ_Binned_ScanLevel_1(); }


	//  !!!!  Arbitrary values since the code is using "floor" by casting to long or int
	private static final double RETENTION_TIME_BIN_SIZE_IN_SECONDS = 1;
	private static final double MZ_BIN_SIZE_IN_MZ = 1;
	
	/**
	 * Significant digits for rounding the Total Ion Current for a single bin
	 */
	private static final int BINNED_SUMMED_INTENSITY_SIGNIFICANT_DIGITS = 5;
	
	private static final String MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot_JSON_CONTENTS_TEXT =
			" 'BinMax' props are max bin values.  bin values are 'floor' of actual values. "
				+ " Have 'MaxPossibleValue' props since MaxPossibleValue is BinMax + 1 " ;
	
	private static final String MS1_IntensitiesBinnedSummedMapToJSONRoot_JSON_CONTENTS_TEXT =
			"ms1_IntensitiesBinnedSummedMap outer key is RT, inner Key is m/z. "
				+ "Both have been binned using 'floor' to next smaller int."
				+ " Have 'MaxPossibleValue' props since MaxPossibleValue is BinMax + 1 " ;

	/**
	 * for ms 1 scans: Map<RetentionTime_Floor,Map<MZ_Floor,SummedIntensity>
	 * 
	 * "_Floor" means truncate any decimal fraction part
	 */
	private Map<Long, Map<Long, MutableDouble>> ms1_IntensitiesBinnedSummedMap = new HashMap<>();
	
	//  Optimization for when prev retentionTimeFloor is same as current retentionTimeFloor
	private long prevRetentionTimeFloor = 0;
	private Map<Long, MutableDouble> ms1_IntensitiesSummedMapForRetentionTime = null;

	private long ms1_RT_NotMatchesPrevRT_Counter = 0;
	private long ms1_MZ_NotMatchesPrevMZ_Counter = 0;
	
	/**
	 * @param scan
	 */
	public void processScanForAccum( SpectralFile_SingleScan_Common scan ) {

        if ( scan.getLevel() != 1)  {
        	//  Only Accum for Scan Level 1
        	return;  //  EARLY EXIT
        }
        	
		float retentionTime = scan.getRetentionTime();
        long retentionTimeFloor = (long)retentionTime;  // Truncate the decimal fraction

    	//  Optimization for when prev retentionTimeFloor is same as current retentionTimeFloor
		if ( ms1_IntensitiesSummedMapForRetentionTime == null || retentionTimeFloor != prevRetentionTimeFloor ) {
			ms1_RT_NotMatchesPrevRT_Counter++;
			ms1_IntensitiesSummedMapForRetentionTime = ms1_IntensitiesBinnedSummedMap.get( retentionTimeFloor );
			if ( ms1_IntensitiesSummedMapForRetentionTime == null ) {
				ms1_IntensitiesSummedMapForRetentionTime = new HashMap<>();
				ms1_IntensitiesBinnedSummedMap.put( retentionTimeFloor, ms1_IntensitiesSummedMapForRetentionTime );
			}
		}
		prevRetentionTimeFloor = retentionTimeFloor;
        
		//  Optimization for when prev mzFloor is same as current mzFloor
        long prevMZFloor = 0;
        MutableDouble intensitySummedForRT_MZ = null;
        
		List<SpectralFile_SingleScanPeak_Common> peakList = scan.getScanPeaksAsObjectArray();
		for ( SpectralFile_SingleScanPeak_Common peak : peakList ) {
			
			float peakIntensity = peak.getIntensity();

			long mzFloor = (long)peak.getM_over_Z();  // Truncate the decimal fraction
			//  Optimization for when prev mzFloor is same as current mzFloor
			if ( intensitySummedForRT_MZ == null ||  mzFloor != prevMZFloor ) {
				ms1_MZ_NotMatchesPrevMZ_Counter++;
				intensitySummedForRT_MZ = ms1_IntensitiesSummedMapForRetentionTime.get( mzFloor );
				if ( intensitySummedForRT_MZ == null ) {
					intensitySummedForRT_MZ = new MutableDouble();
					ms1_IntensitiesSummedMapForRetentionTime.put( mzFloor, intensitySummedForRT_MZ );
				}
			}
			intensitySummedForRT_MZ.add( peakIntensity );
			prevMZFloor = mzFloor;
		}
	}
	

	/**
	 * Process input parameter, rounding the summed intensity, generating a result map that is then serialized to JSON.
	 * Create a summary object, inserting that into main returned object and also serializing it to JSON.
	 * 
	 * @param ms1_IntensitiesBinnedSummedMap: for ms 1 scans: Map<RetentionTime_Floor,Map<MZ_Floor,SummedIntensity>
	 *        "_Floor" means truncate any decimal fraction part
	 * @return bytes for storage in DB, Map of data with rounded summed intensities, summary object
	 * @throws Exception 
	 */
	public MS1_IntensitiesBinnedSummedMapRoot getSummedObject() throws Exception {

		Map<Double, Map<Double, Double>> ms1_IntensitiesDoubleBinnedSummedMap = new HashMap<>();
		
		boolean firstRT = true;
		boolean firstMZ = true;
		double rtBinMin = 0;
		double rtBinMax = 0;
		double mzBinMin = 0;
		double mzBinMax = 0;
		double intensityBinnedMin = 0;
		double intensityBinnedMax = 0;
		
		 // Count # binned Summed Intensity values
		long binnedSummedIntensityCount = 0;
		
		MathContext mathContextSignificantDigits = new MathContext( BINNED_SUMMED_INTENSITY_SIGNIFICANT_DIGITS );
		
		for ( Map.Entry<Long, Map<Long, MutableDouble>> entryKeyedRT : ms1_IntensitiesBinnedSummedMap.entrySet() ) {
			double rtValue = entryKeyedRT.getKey();
			if ( firstRT ) {
				firstRT = false;
				rtBinMin = rtValue;
				rtBinMax = rtValue;
			} else {
				if ( rtValue < rtBinMin ) {
					rtBinMin = rtValue;
				}
				if ( rtValue > rtBinMax ) {
					rtBinMax = rtValue;
				}
			}
			Map<Double, Double> ms1_IntensitiesDoubleBinnedSummedMapKeyMZ = new HashMap<>();
			ms1_IntensitiesDoubleBinnedSummedMap.put( rtValue, ms1_IntensitiesDoubleBinnedSummedMapKeyMZ );
			for ( Map.Entry<Long, MutableDouble> entryKeyedMZ : entryKeyedRT.getValue().entrySet() ) {
				double mzValue = entryKeyedMZ.getKey();
				
				//  Round binnedIntensity
				double binnedIntensityNotRounded = entryKeyedMZ.getValue().getValue();
				BigDecimal binnedIntensityRoundedBD = new BigDecimal( binnedIntensityNotRounded, mathContextSignificantDigits );
				double binnedIntensityRounded = binnedIntensityRoundedBD.doubleValue();
						
				binnedSummedIntensityCount++; // Count # binned Summed Intensity values
				if ( firstMZ) {
					firstMZ = false;
					mzBinMin = mzValue;
					mzBinMax = mzValue;
					intensityBinnedMin = binnedIntensityRounded;
					intensityBinnedMax = binnedIntensityRounded;
				} else {
					if ( mzValue < mzBinMin ) {
						mzBinMin = mzValue;
					}
					if ( mzValue > mzBinMax ) {
						mzBinMax = mzValue;
					}
					if ( binnedIntensityRounded < intensityBinnedMin ) {
						intensityBinnedMin = binnedIntensityRounded;
					}
					if ( binnedIntensityRounded > intensityBinnedMax ) {
						intensityBinnedMax = binnedIntensityRounded;
					}
				}
				ms1_IntensitiesDoubleBinnedSummedMapKeyMZ.put( mzValue, binnedIntensityRounded );
			}
		}
		
		MS1_IntensitiesBinnedSummed_Summary_DataRoot summaryData = new MS1_IntensitiesBinnedSummed_Summary_DataRoot();
		summaryData.setJsonContents( MS1_IntensitiesBinnedSummed_Summary_Data_ToJSONRoot_JSON_CONTENTS_TEXT );
		summaryData.setBinnedSummedIntensityCount( binnedSummedIntensityCount );
		
		summaryData.setRtBinSizeInSeconds( RETENTION_TIME_BIN_SIZE_IN_SECONDS );
		summaryData.setRtBinMaxInSeconds( rtBinMax );
		summaryData.setRtBinMinInSeconds( rtBinMin );
		
		summaryData.setMzBinSizeInMZ( MZ_BIN_SIZE_IN_MZ );
		summaryData.setMzBinMaxInMZ( mzBinMax );
		summaryData.setMzBinMinInMZ( mzBinMin );
		
		summaryData.setIntensityBinnedMin( intensityBinnedMin );
		summaryData.setIntensityBinnedMax( intensityBinnedMax );
		
		// Have 'MaxPossibleValue' props since Max Possible Value is BinMax + 1 ( The Bin value is Floor(value) )
		summaryData.setRtMaxPossibleValueInSeconds( rtBinMax + 1 );
		summaryData.setMzMaxPossibleValueInMZ( mzBinMax + 1 );
		
		MS1_IntensitiesBinnedSummedMapRoot ms1_IntensitiesBinnedSummedMapRoot = new MS1_IntensitiesBinnedSummedMapRoot();
		ms1_IntensitiesBinnedSummedMapRoot.setJsonContents( MS1_IntensitiesBinnedSummedMapToJSONRoot_JSON_CONTENTS_TEXT );
		ms1_IntensitiesBinnedSummedMapRoot.setSummaryData( summaryData );
		ms1_IntensitiesBinnedSummedMapRoot.setMs1_IntensitiesBinnedSummedMap( ms1_IntensitiesDoubleBinnedSummedMap );

		return ms1_IntensitiesBinnedSummedMapRoot;
	}
	
	/**
	 * 
	 */
	public void printStats() {
		System.out.println( "**********************************************" );
		System.out.println( "AccumScanFileStatistics.printStats()");
		System.out.println();
		System.out.println( "ms1_RT_NotMatchesPrevRT_Counter: " + ms1_RT_NotMatchesPrevRT_Counter );
		System.out.println( "ms1_MZ_NotMatchesPrevMZ_Counter: " + ms1_MZ_NotMatchesPrevMZ_Counter );
		System.out.println();
		
		Set<Long> uniqueRT = new HashSet<>();
		Set<Long> uniqueMZ = new HashSet<>();

		double ms1_IntensitiesBinnedSummedMap_SummedIntensities = 0;
		double max_ms1_IntensitiesBinnedSummed = 0;
		double min_ms1_IntensitiesBinnedSummed = 0;
		long summedIntensityCount = 0;
		long largestPerMZMapSize = 0;
		boolean firstIntensityEntry = true;
		for ( Map.Entry<Long, Map<Long, MutableDouble>> entryKeyedRT : ms1_IntensitiesBinnedSummedMap.entrySet() ) {
//			System.out.println( "RetentionTime: " + entryKeyedRT.getKey() );
			uniqueRT.add( entryKeyedRT.getKey() );
			double ms1_IntensitiesBinnedSummedMap_SummedIntensitiesForRT = 0;
			for ( Map.Entry<Long, MutableDouble> entryKeyedMZ : entryKeyedRT.getValue().entrySet() ) {
				double intensity = entryKeyedMZ.getValue().doubleValue();
				uniqueMZ.add( entryKeyedMZ.getKey() );
				summedIntensityCount++;
				ms1_IntensitiesBinnedSummedMap_SummedIntensitiesForRT += intensity;
//				System.out.println( "         MZ: " + entryKeyedMZ.getKey() 
//				+ ", Binned Summed Intensity: " + entryKeyedMZ.getValue() );
				if ( firstIntensityEntry ) {
					firstIntensityEntry = false;
					max_ms1_IntensitiesBinnedSummed = intensity;
					min_ms1_IntensitiesBinnedSummed = intensity;
				}
				if ( intensity > max_ms1_IntensitiesBinnedSummed ) {
					max_ms1_IntensitiesBinnedSummed = intensity;
				}
				if ( intensity < min_ms1_IntensitiesBinnedSummed ) {
					min_ms1_IntensitiesBinnedSummed = intensity;
				}
			}
			ms1_IntensitiesBinnedSummedMap_SummedIntensities += ms1_IntensitiesBinnedSummedMap_SummedIntensitiesForRT;
			int perMZMapSize = entryKeyedRT.getValue().size();
			if ( perMZMapSize > largestPerMZMapSize ) {
				largestPerMZMapSize = perMZMapSize;
			}
		}
		
		System.out.println( "ms1_IntensitiesBinnedSummedMap_SummedIntensities: " + ms1_IntensitiesBinnedSummedMap_SummedIntensities );
		System.out.println( "summedIntensityCount: " + summedIntensityCount );
		System.out.println( "largestPerMZMapSize: " + largestPerMZMapSize );
		System.out.println( "ms1_IntensitiesBinnedSummedMap Size: " + ms1_IntensitiesBinnedSummedMap.size() );

		System.out.println( "uniqueRT Size: " + uniqueRT.size() );
		System.out.println( "uniqueMZ Size: " + uniqueMZ.size() );

		System.out.println( "**********************************************");
	}

}
