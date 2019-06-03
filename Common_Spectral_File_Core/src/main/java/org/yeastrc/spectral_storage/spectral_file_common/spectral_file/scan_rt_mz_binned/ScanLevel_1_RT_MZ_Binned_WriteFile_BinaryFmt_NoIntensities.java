package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_rt_mz_binned;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

/**
 * !!!!!  WARNING !!!!!!!!!       NOT currently used.
 * The output format to file may not be complete.  
 * Would need to try to create reader to confirm that format is complete.
 * 
 * Write Scan Level 1 data 
 * binned on Retention Time and M/Z - No Summed Intensities
 * as Binary Fmt GZIP
 * 
 * 
 */
public class ScanLevel_1_RT_MZ_Binned_WriteFile_BinaryFmt_NoIntensities {

	private static final Logger log = Logger.getLogger(ScanLevel_1_RT_MZ_Binned_WriteFile_BinaryFmt_NoIntensities.class);
	private ScanLevel_1_RT_MZ_Binned_WriteFile_BinaryFmt_NoIntensities() { }
	public static ScanLevel_1_RT_MZ_Binned_WriteFile_BinaryFmt_NoIntensities getInstance() { return new ScanLevel_1_RT_MZ_Binned_WriteFile_BinaryFmt_NoIntensities(); }

	/**
	 * @param summaryData
	 * @param hash_String
	 * @param subDirForStorageFiles
	 * @throws Exception
	 */
	public void writeScanLevel_1_RT_MZ_Binned_File_BINARY_NoIntensities( 
			Accumulate_RT_MZ_Binned_ScanLevel_1 accumulate_RT_MZ_Binned_ScanLevel_1,
			String hash_String,
			File subDirForStorageFiles ) throws Exception {

		MS1_IntensitiesBinnedSummedMapRoot summedDataRoot = accumulate_RT_MZ_Binned_ScanLevel_1.getSummedObject();
		MS1_IntensitiesBinnedSummed_Summary_DataRoot summaryData = summedDataRoot.getSummaryData();
		
		String filenameFinal =
				CreateSpectralStorageFilenames.getInstance()
				.createSpectraStorage_ScanBinnedIntensityOn_RT_MZ__Binary_NoIntensities_Filename(
						hash_String,
						summaryData.getRtBinSizeInSeconds(),
						summaryData.getMzBinSizeInMZ() );

		String filenameWhileWriting = filenameFinal + SpectralStorage_Filename_Constants.IN_PROGRESS_FILENAME_SUFFIX_SUFFIX;
		
		File dataFileFinal = new File( subDirForStorageFiles, filenameFinal );
		File dataFileWhileWriting = new File( subDirForStorageFiles, filenameWhileWriting );
		
		//  Copy map entries to list so can sort list:
		
		//  for ms 1 scans: Map<RetentionTime_BinStart,Map<MZ_BinStart,SummedIntensity>
		Map<Long, Map<Long, Double>> ms1_IntensitiesBinnedSummedMap = summedDataRoot.getMs1_IntensitiesBinnedSummedMap();
		
		List<RetentionTimeBinnedHolder> retentionTimeBinnedHolderList = convert_ms1_IntensitiesBinnedSummedMap_To_SortedLists( ms1_IntensitiesBinnedSummedMap );
		
		output_ScanLevel_1_RT_MZ_Binned_File_BINARY( retentionTimeBinnedHolderList, dataFileWhileWriting );

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
	private List<RetentionTimeBinnedHolder> convert_ms1_IntensitiesBinnedSummedMap_To_SortedLists ( Map<Long, Map<Long, Double>> ms1_IntensitiesBinnedSummedMap ) {
		
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
				mzBinnedHolder.summedIntensity = entry_Key_MZ.getValue();
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
	private void output_ScanLevel_1_RT_MZ_Binned_File_BINARY( 
			List<RetentionTimeBinnedHolder> retentionTimeBinnedHolderList, File dataFileWhileWriting ) throws Exception {
		
		//  Find largest offset between retention time bin values and MZ bin values

		//  Get largest retention time bin Offset from prev 
		long largestRetentionTimeBinOffsetFromPrev = 0;

		//  Get largest MZ bin first value 
		long largestMZOffsetFromPrev = 0;
		//  Get largest MZ bin Offset from prev 
		long largestMZfirstValue = 0;

		byte retentionTimeBinOffsetType = ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.RETENTION_TIME_TYPE_LONG;
		byte mzBinValueOffsetType = ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.MZ_TYPE_LONG;
		byte mzfirstValueType = ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.MZ_TYPE_LONG;
		
		if ( ! retentionTimeBinnedHolderList.isEmpty() ) {
			RetentionTimeBinnedHolder prevRetentionTimeBinnedHolder = null;
			for ( RetentionTimeBinnedHolder retentionTimeBinnedHolder : retentionTimeBinnedHolderList ) {
				if ( prevRetentionTimeBinnedHolder != null ) {
					long retentionTimeOffset = retentionTimeBinnedHolder.retentionTimeBinValue - prevRetentionTimeBinnedHolder.retentionTimeBinValue; 
					if ( retentionTimeOffset > largestRetentionTimeBinOffsetFromPrev ) {
						largestRetentionTimeBinOffsetFromPrev = retentionTimeOffset;
					}
				}
				List<MZBinnedHolder> mzBinnedHolderList = retentionTimeBinnedHolder.mzBinnedHolderList;
				MZBinnedHolder prevMZBinnedHolder = null;
				for ( MZBinnedHolder mzBinnedHolder : mzBinnedHolderList ) {
					if ( prevMZBinnedHolder == null ) {
						// first entry
						if ( mzBinnedHolder.mzBinValue > largestMZfirstValue ) {
							largestMZfirstValue = mzBinnedHolder.mzBinValue;
						}
					} else {
						// Not first entry
						long mzBinValueOffset = mzBinnedHolder.mzBinValue - prevMZBinnedHolder.mzBinValue; 
						if ( mzBinValueOffset > largestMZOffsetFromPrev ) {
							largestMZOffsetFromPrev = mzBinValueOffset;
						}
					}
					prevMZBinnedHolder = mzBinnedHolder;
				}
				prevRetentionTimeBinnedHolder = retentionTimeBinnedHolder;
			}

			if ( largestRetentionTimeBinOffsetFromPrev < Byte.MAX_VALUE ) {
				retentionTimeBinOffsetType = ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.RETENTION_TIME_TYPE_BYTE;
			} else if ( largestRetentionTimeBinOffsetFromPrev < Short.MAX_VALUE ) {
				retentionTimeBinOffsetType = ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.RETENTION_TIME_TYPE_SHORT;
			} else if ( largestRetentionTimeBinOffsetFromPrev < Integer.MAX_VALUE ) {
				retentionTimeBinOffsetType = ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.RETENTION_TIME_TYPE_INT;
			}

			if ( largestMZOffsetFromPrev < Byte.MAX_VALUE ) {
				mzBinValueOffsetType = ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.MZ_TYPE_BYTE;
			} else if ( largestMZOffsetFromPrev < Short.MAX_VALUE ) {
				mzBinValueOffsetType = ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.MZ_TYPE_SHORT;
			} else if ( largestMZOffsetFromPrev < Integer.MAX_VALUE ) {
				mzBinValueOffsetType = ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.MZ_TYPE_INT;
			}

			if ( largestMZfirstValue < Byte.MAX_VALUE ) {
				mzfirstValueType = ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.MZ_TYPE_BYTE;
			} else if ( largestMZfirstValue < Short.MAX_VALUE ) {
				mzfirstValueType = ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.MZ_TYPE_SHORT;
			} else if ( largestMZfirstValue < Integer.MAX_VALUE ) {
				mzfirstValueType = ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.MZ_TYPE_INT;
			}
		}

		
		int z = 0;
		
		RetentionTimeBinnedHolder firstRetentionTimeBinnedHolder = null;
		long first_RetentionTimeBin = ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.FIRST_RETENTION_TIME_NOT_SET;

		if ( ! retentionTimeBinnedHolderList.isEmpty() ) {
			firstRetentionTimeBinnedHolder = retentionTimeBinnedHolderList.iterator().next();
			first_RetentionTimeBin = firstRetentionTimeBinnedHolder.retentionTimeBinValue;
		}

		//  GZIP yields significant space savings.  
		//  Example file: uncompressed 7.7MB  GZIP compressed 1.1MB
		
		try ( DataOutputStream dataOutputStream_IndexFile = 
				new DataOutputStream( new BufferedOutputStream( new FileOutputStream( dataFileWhileWriting ) ) )
				) {
//		try ( DataOutputStream dataOutputStream_IndexFile = 
//				new DataOutputStream( new BufferedOutputStream( new GZIPOutputStream( new FileOutputStream( dataFileWhileWriting ) ) ) )
//				) {

			dataOutputStream_IndexFile.writeByte( retentionTimeBinOffsetType );
			dataOutputStream_IndexFile.writeByte( mzBinValueOffsetType );
			dataOutputStream_IndexFile.writeByte( mzfirstValueType );
			

			//  Write length of list of rt
			dataOutputStream_IndexFile.writeInt( retentionTimeBinnedHolderList.size() );
			
			//  Write first rt
			dataOutputStream_IndexFile.writeLong( first_RetentionTimeBin );
			{
				RetentionTimeBinnedHolder prevRetentionTimeBinnedHolder = firstRetentionTimeBinnedHolder; // init to first entry
				
				for ( RetentionTimeBinnedHolder retentionTimeBinnedHolder : retentionTimeBinnedHolderList ) {

					long retentionTimeBin_Offset = retentionTimeBinnedHolder.retentionTimeBinValue - prevRetentionTimeBinnedHolder.retentionTimeBinValue;
					
					//  Write rt offset
					if ( retentionTimeBinOffsetType == ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.RETENTION_TIME_TYPE_BYTE ) {
						dataOutputStream_IndexFile.writeLong( retentionTimeBin_Offset );
					} else {
						int retentionTimeBin_Offset_Int = (int)retentionTimeBin_Offset;
						if ( retentionTimeBinOffsetType == ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.RETENTION_TIME_TYPE_BYTE ) {
							dataOutputStream_IndexFile.writeByte( retentionTimeBin_Offset_Int );
						} else if ( retentionTimeBinOffsetType == ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.RETENTION_TIME_TYPE_SHORT ) {
							dataOutputStream_IndexFile.writeShort( retentionTimeBin_Offset_Int );
						} else {
							dataOutputStream_IndexFile.writeInt( retentionTimeBin_Offset_Int );
						}
					}
					
					List<MZBinnedHolder> mzBinnedHolderList = retentionTimeBinnedHolder.mzBinnedHolderList;
					if ( mzBinnedHolderList == null || mzBinnedHolderList.isEmpty() ) {
						throw new SpectralStorageProcessingException( "mzBinnedHolderList == null || mzBinnedHolderList.isEmpty()" );
					}
					
					//  Write length of sub list of mz and summed intensities
					dataOutputStream_IndexFile.writeInt( mzBinnedHolderList.size() );
					
					MZBinnedHolder first_MZBinnedHolder = mzBinnedHolderList.iterator().next();
					long first_mzBinValue = first_MZBinnedHolder.mzBinValue;
					
					//  Write mz value of first entry of sub list of mz and summed intensities
					if ( mzfirstValueType == ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.MZ_TYPE_LONG ) {
						dataOutputStream_IndexFile.writeLong( first_mzBinValue );
					} else {
						int first_mzBinValue_Int = (int)first_mzBinValue;
						if ( mzfirstValueType == ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.MZ_TYPE_BYTE ) {
							dataOutputStream_IndexFile.writeByte( first_mzBinValue_Int );
						} else if ( mzfirstValueType == ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.MZ_TYPE_SHORT ) {
							dataOutputStream_IndexFile.writeShort( first_mzBinValue_Int );
						} else {
							dataOutputStream_IndexFile.writeInt( first_mzBinValue_Int );
						}
					}
					MZBinnedHolder prevMZBinnedHolder = first_MZBinnedHolder;  // init to first entry
					
					for ( MZBinnedHolder mzBinnedHolder : mzBinnedHolderList ) {

						//  Write mz value offset of entry of sub list of mz and summed intensities
						
						long mzBin_Offset = mzBinnedHolder.mzBinValue - prevMZBinnedHolder.mzBinValue;
						if ( mzBinValueOffsetType == ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.MZ_TYPE_LONG ) {
							dataOutputStream_IndexFile.writeLong( mzBin_Offset );
						} else {
							int mzBin_Offset_Int = (int)mzBin_Offset;
							if ( mzBinValueOffsetType == ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.MZ_TYPE_BYTE ) {
								dataOutputStream_IndexFile.writeByte( mzBin_Offset_Int );
							} else if ( mzBinValueOffsetType == ScanLevel_1_RT_MZ_Binned_File_BinaryFmt_Constants.MZ_TYPE_SHORT ) {
								dataOutputStream_IndexFile.writeShort( mzBin_Offset_Int );
							} else {
								dataOutputStream_IndexFile.writeInt( mzBin_Offset_Int );
							}
						}

						//  Skip write of summed intensities
						
						//  Write summed intensity value offset of entry of sub list of mz and summed intensities
//						dataOutputStream_IndexFile.writeDouble( mzBinnedHolder.summedIntensity );
						
						prevMZBinnedHolder = mzBinnedHolder;
					}
					prevRetentionTimeBinnedHolder = retentionTimeBinnedHolder;
				}
			}
		} catch ( Exception e ) {
			String msg = "Failed to write to ScanLevel_1_RT_MZ_Binned_Binary_File file: " + dataFileWhileWriting.getAbsolutePath();
			log.error( msg, e );
			throw e;
		}
		

	}
	
	// Internal classes
	
	private static class RetentionTimeBinnedHolder {
		long retentionTimeBinValue;
		List<MZBinnedHolder> mzBinnedHolderList;
	}
	private static class MZBinnedHolder {
		long mzBinValue;
		double summedIntensity;
	}
}
