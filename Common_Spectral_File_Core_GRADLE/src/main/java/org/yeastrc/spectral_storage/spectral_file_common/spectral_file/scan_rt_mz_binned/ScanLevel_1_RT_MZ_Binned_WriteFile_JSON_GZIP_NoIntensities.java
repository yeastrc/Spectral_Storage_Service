package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_rt_mz_binned;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.shared_server_client_importer.accum_scan_rt_mz_binned.dto.MS1_IntensitiesBinnedSummedMapRoot;
import org.yeastrc.spectral_storage.shared_server_client_importer.accum_scan_rt_mz_binned.dto.MS1_IntensitiesBinnedSummed_Summary_DataRoot;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.SpectralStorage_Filename_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.CreateSpectralStorageFilenames;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Write Scan Level 1 data 
 * binned on Retention Time and M/Z - No Summed Intensities
 * as JSON GZIP
 * 
 * Example code for deserialize is commented out below
 * 
 * File format:
 * 
 * At the top level it is a JSON array of pairs of elements where:
 *   the first element is the retention time bin (or offset)
 *   the second element is the array of mz bin (or offset)
 * 
 * To save space and increase compression, 
 * after the first 'retention time bin' or 'mz bin', the rest are listed as offset from the previous.
 * 
 * At top level:
 *   [<first retention time bin>,<mz bins associated with retention time bin array>,
 *    <second retention time bin: value in JSON is offset from first retention time bin>,<mz bin values associated with retention time bin array>
 *    <third retention time bin: value in JSON is offset from second retention time bin>,<mz bin values associated with retention time bin array>]
 *   
 * For '<mz bin array>':
 *    [<first mz bin>,
 *      <second mz bin: value in JSON is offset from first mz bin>,
 *      <third mz bin: value in JSON is offset from second mz bin>]
 *      
 *  example:
 *  
 *  Incoming values
 *  ["retentionTimeBin":6,["mzBin":10,"mzBin":11],"retentionTimeBin":7,["mzBin":5,"mzBin":8]]
 *  retention time bin: 6
 *  Associated mz bin values: 10, 11
 *  retention time bin: 7
 *  Associated mz bin values: 5, 8
 *  
 *  Stored JSON
 *  [6,[10,1],1,[5,3]]
 */
public class ScanLevel_1_RT_MZ_Binned_WriteFile_JSON_GZIP_NoIntensities {

	private static final Logger log = Logger.getLogger(ScanLevel_1_RT_MZ_Binned_WriteFile_JSON_GZIP_NoIntensities.class);
	private ScanLevel_1_RT_MZ_Binned_WriteFile_JSON_GZIP_NoIntensities() { }
	public static ScanLevel_1_RT_MZ_Binned_WriteFile_JSON_GZIP_NoIntensities getInstance() { return new ScanLevel_1_RT_MZ_Binned_WriteFile_JSON_GZIP_NoIntensities(); }

	/**
	 * @param summaryData
	 * @param hash_String
	 * @param subDirForStorageFiles
	 * @throws Exception
	 */
	public void writeScanLevel_1_RT_MZ_Binned_File_JSON_GZIP_NoIntensities( 
			Accumulate_RT_MZ_Binned_ScanLevel_1 accumulate_RT_MZ_Binned_ScanLevel_1,
			String hash_String,
			File subDirForStorageFiles ) throws Exception {

		MS1_IntensitiesBinnedSummedMapRoot summedDataRoot = accumulate_RT_MZ_Binned_ScanLevel_1.getSummedObject();
		MS1_IntensitiesBinnedSummed_Summary_DataRoot summaryData = summedDataRoot.getSummaryData();
		
		String filenameFinal =
				CreateSpectralStorageFilenames.getInstance()
				.createSpectraStorage_ScanBinnedIntensityOn_RT_MZ__JSON_GZIP_NoIntensities_Filename(
						hash_String,
						summaryData.getRtBinSizeInSeconds(),
						summaryData.getMzBinSizeInMZ() );

		String filenameWhileWriting = filenameFinal + SpectralStorage_Filename_Constants.IN_PROGRESS_FILENAME_SUFFIX_SUFFIX;
		
		File dataFileFinal = new File( subDirForStorageFiles, filenameFinal );
		File dataFileWhileWriting = new File( subDirForStorageFiles, filenameWhileWriting );
		
		//  Copy map entries to output data structure
		
		//  for ms 1 scans: Map<RetentionTime_BinStart,Map<MZ_BinStart,SummedIntensity>
		Map<Long, Map<Long, Double>> ms1_IntensitiesBinnedSummedMap = summedDataRoot.getMs1_IntensitiesBinnedSummedMap();
		
		List<RetentionTimeBinnedHolder> retentionTimeBinnedHolderList = convert_ms1_IntensitiesBinnedSummedMap_To_OUtputDataStructure( ms1_IntensitiesBinnedSummedMap );
		
		output_ScanLevel_1_RT_MZ_Binned_File_JSON( retentionTimeBinnedHolderList, dataFileWhileWriting );

		if ( ! dataFileWhileWriting.renameTo( dataFileFinal ) ) {
			String msg = "Failed to rename ScanLevel_1_RT_MZ_Binned_File file: " 
					+ dataFileWhileWriting.getAbsolutePath()
					+ ", to file: "
					+ dataFileFinal.getAbsolutePath();
			log.error( msg );
			throw new SpectralStorageProcessingException( msg );
		}
	}
	
	/**
	 * @param ms1_IntensitiesBinnedSummedMap
	 * @return
	 */
	private List<RetentionTimeBinnedHolder> convert_ms1_IntensitiesBinnedSummedMap_To_OUtputDataStructure ( Map<Long, Map<Long, Double>> ms1_IntensitiesBinnedSummedMap ) {
		
		List<RetentionTimeBinnedHolder> retentionTimeBinnedHolderList = new ArrayList<>( ms1_IntensitiesBinnedSummedMap.size() );
		
		// copy map to list of lists
		for ( Map.Entry<Long, Map<Long, Double>> ms1_IntensitiesBinnedSummedMap_Entry : ms1_IntensitiesBinnedSummedMap.entrySet() ) {
			RetentionTimeBinnedHolder retentionTimeBinnedHolder = new RetentionTimeBinnedHolder();
			retentionTimeBinnedHolder.retentionTimeBinValue = ms1_IntensitiesBinnedSummedMap_Entry.getKey();
			
			List<MZBinnedHolder> mzBinnedHolderList = new ArrayList<>( ms1_IntensitiesBinnedSummedMap_Entry.getValue().size() );
			retentionTimeBinnedHolder.mzBinnedHolderList = mzBinnedHolderList;
			
			for ( Map.Entry<Long, Double> entry_Key_MZ : ms1_IntensitiesBinnedSummedMap_Entry.getValue().entrySet() ) {
				MZBinnedHolder mzBinnedHolder = new MZBinnedHolder();
				mzBinnedHolderList.add( mzBinnedHolder );
				mzBinnedHolder.mzBinValue = entry_Key_MZ.getKey();
			}
			// sort on mz bin
			Collections.sort( mzBinnedHolderList, new Comparator<MZBinnedHolder>() {
				@Override
				public int compare(MZBinnedHolder o1, MZBinnedHolder o2) {
					if ( o1.mzBinValue < o2.mzBinValue ) {
						return -1;
					}
					if ( o1.mzBinValue > o2.mzBinValue ) {
						return 1;
					}
					return 0;
				}});
			if ( ! mzBinnedHolderList.isEmpty() ) {
				retentionTimeBinnedHolderList.add( retentionTimeBinnedHolder );
			}
		}
		// sort on retention time bin
		Collections.sort( retentionTimeBinnedHolderList, new Comparator<RetentionTimeBinnedHolder>() {
			@Override
			public int compare(RetentionTimeBinnedHolder o1, RetentionTimeBinnedHolder o2) {
				if ( o1.retentionTimeBinValue < o2.retentionTimeBinValue ) {
					return -1;
				}
				if ( o1.retentionTimeBinValue > o2.retentionTimeBinValue ) {
					return 1;
				}
				return 0;
			}});
		
		return retentionTimeBinnedHolderList;
	}

	/**
	 * @param retentionTimeBinnedHolderList
	 * @param dataFileWhileWriting
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private void output_ScanLevel_1_RT_MZ_Binned_File_JSON( 
			List<RetentionTimeBinnedHolder> retentionTimeBinnedHolderList, File dataFileWhileWriting ) throws Exception {
		
		List<Object> outputSerializeToJSON = new ArrayList<Object>( 100000 );
		
		if ( ! retentionTimeBinnedHolderList.isEmpty() ) {
			RetentionTimeBinnedHolder prevRetentionTimeBinnedHolder = null;
			for ( RetentionTimeBinnedHolder retentionTimeBinnedHolder : retentionTimeBinnedHolderList ) {
				if ( prevRetentionTimeBinnedHolder == null ) {
					// first record
					//  Add retentionTimeBinValue
					outputSerializeToJSON.add( retentionTimeBinnedHolder.retentionTimeBinValue );
				} else {
					// Not first record
					//  Add retentionTimeOffset
					long retentionTimeOffset = retentionTimeBinnedHolder.retentionTimeBinValue - prevRetentionTimeBinnedHolder.retentionTimeBinValue; 
					outputSerializeToJSON.add( retentionTimeOffset );
				}
				// Add mzBinned for this retentionTimeBinValue
				
				List<MZBinnedHolder> mzBinnedHolderList = retentionTimeBinnedHolder.mzBinnedHolderList;
				List<Object> mzBinnedListForRetentionTime = new ArrayList<Object>(  ); // 100000
				outputSerializeToJSON.add( mzBinnedListForRetentionTime );
				MZBinnedHolder prevMZBinnedHolder = null;
				for ( MZBinnedHolder mzBinnedHolder : mzBinnedHolderList ) {
					if ( prevMZBinnedHolder == null ) {
						// first entry
						mzBinnedListForRetentionTime.add( mzBinnedHolder.mzBinValue );
					} else {
						// Not first entry
						long mzBinValueOffset = mzBinnedHolder.mzBinValue - prevMZBinnedHolder.mzBinValue; 
						mzBinnedListForRetentionTime.add( mzBinValueOffset );
					}
					prevMZBinnedHolder = mzBinnedHolder;
				}
				prevRetentionTimeBinnedHolder = retentionTimeBinnedHolder;
			}
		}
		

		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
		
		//  GZIP yields significant space savings.  
		//  Example file: uncompressed 7.8MB  GZIP compressed 1.1MB
		
		try ( OutputStream outputStream = 
				new BufferedOutputStream( new GZIPOutputStream( new FileOutputStream( dataFileWhileWriting ) ) ) ) {

			//  Serialize to JSON, and write to file
			jacksonJSON_Mapper.writeValue( outputStream, outputSerializeToJSON );
		} catch ( Exception e ) {
			String msg = "Failed to write to ScanLevel_1_RT_MZ_Binned_File file: " + dataFileWhileWriting.getAbsolutePath();
			log.error( msg, e );
			throw e;
		}
		
		//  How to put back into Java objects
//		{
//			Object readFromFile = null;
//			try ( InputStream inputStream = new GZIPInputStream( new FileInputStream(dataFileWhileWriting) ) ) {
//
//				readFromFile = jacksonJSON_Mapper.readValue( inputStream, List.class );
//		
//			} catch ( Exception e ) {
//				String msg = "Failed to read from ScanLevel_1_RT_MZ_Binned_File file: " + dataFileWhileWriting.getAbsolutePath();
//				log.error( msg, e );
//				throw e;
//			}
//		}
	}
	
	// Internal classes
	
	private static class RetentionTimeBinnedHolder {
		long retentionTimeBinValue;
		List<MZBinnedHolder> mzBinnedHolderList;
	}
	private static class MZBinnedHolder {
		long mzBinValue;
	}
}
