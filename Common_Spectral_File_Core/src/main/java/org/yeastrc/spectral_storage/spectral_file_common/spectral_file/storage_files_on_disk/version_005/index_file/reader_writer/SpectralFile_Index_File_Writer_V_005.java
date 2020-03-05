package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.reader_writer;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.accum_scan_summary_data.AccumulateSummaryDataPerScanLevel;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.accum_scan_summary_data.AccumulateSummaryDataPerScanLevelResult;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.accum_scan_summary_data.AccumulateSummaryDataPerScanLevelSingleLevelResult;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.ByteValuesFor_Boolean_TrueFalse_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.DataOrIndexFileFullyWrittenConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.SpectralStorage_Filename_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_Header_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.CreateSpectralStorageFilenames;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.StorageFile_Version_005_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.constants.SpectralFile_Index_Header_DTO_V_005__Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.from_data_file_writer_objects.SpectralFile_Index_FDFW_FileContents_Root_V_005;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.from_data_file_writer_objects.SpectralFile_Index_FDFW_SingleScan_V_005;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.to_data_file_reader_objects.SpectralFile_Index_TDFR_FileContents_Root_V_005;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.to_data_file_reader_objects.SpectralFile_Index_TDFR_SingleScan_V_005;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.to_data_file_reader_objects.SpectralFile_Index_TDFR_SummaryDataPerScanLevel_V_005;

/**
 * Any changes to data written require a NEW VERSION and a change to the corresponding SpectralFile_Index_File_Reader_V_###
 *
 */
public class SpectralFile_Index_File_Writer_V_005 {

	private static final Logger log = LoggerFactory.getLogger(SpectralFile_Index_File_Writer_V_005.class);

	/**
	 * Version for Index File, written at beginning of the file
	 */
	public static final short FILE_VERSION = StorageFile_Version_005_Constants.FILE_VERSION;
	
	private static final String FILE_MODE_READ_WRITE = "rw"; // Used in RandomAccessFile constructor below
	
	/**
	 * private constructor
	 */
	private SpectralFile_Index_File_Writer_V_005(){}
	public static SpectralFile_Index_File_Writer_V_005 getInstance( ) throws Exception {
		SpectralFile_Index_File_Writer_V_005 instance = new SpectralFile_Index_File_Writer_V_005();
		return instance;
	}
	

	/**
	 * @param hash_String
	 * @param subDirForStorageFiles
	 * @param spectralFile_Index_FDFW_FileContents_Root_V_005
	 * @throws Exception
	 */
	public void writeIndexFile( 
			String hash_String,
			File subDirForStorageFiles,
			SpectralFile_Index_FDFW_FileContents_Root_V_005 spectralFile_Index_FDFW_FileContents_Root_V_005,
			SpectralFile_Header_Common header_MainDataFile_Common,
			AccumulateSummaryDataPerScanLevel accumulateSummaryDataPerScanLevel ) throws Exception {
		
		AccumulateSummaryDataPerScanLevelResult accumulateSummaryDataPerScanLevelResult = accumulateSummaryDataPerScanLevel.getAccumResult();
		List<SpectralFile_Index_TDFR_SummaryDataPerScanLevel_V_005> summaryDataPerScanLevelList = new ArrayList<>( accumulateSummaryDataPerScanLevelResult.getSummaryDataPerScanLevelList().size() );
		for ( AccumulateSummaryDataPerScanLevelSingleLevelResult summaryDataPerScanLevelEntry : accumulateSummaryDataPerScanLevelResult.getSummaryDataPerScanLevelList() ) {
			SpectralFile_Index_TDFR_SummaryDataPerScanLevel_V_005 tdfr_item = new SpectralFile_Index_TDFR_SummaryDataPerScanLevel_V_005();
			tdfr_item.setScanLevel( summaryDataPerScanLevelEntry.getScanLevel() );
			tdfr_item.setNumberOfScans( summaryDataPerScanLevelEntry.getNumberOfScans() );
			tdfr_item.setIsCentroidScanLevel( summaryDataPerScanLevelEntry.getIsCentroidScanLevel() );
			tdfr_item.setIsIonInjectionTime_Set_ScanLevel( summaryDataPerScanLevelEntry.getIsIonInjectionTime_Set_ScanLevel() );
			tdfr_item.setTotalIonCurrent( summaryDataPerScanLevelEntry.getTotalIonCurrent() );
			summaryDataPerScanLevelList.add( tdfr_item );
		}
		
		List<SpectralFile_Index_TDFR_SingleScan_V_005> indexScanEntries = new ArrayList<>( spectralFile_Index_FDFW_FileContents_Root_V_005.getIndexScanEntries().size() );
		for ( SpectralFile_Index_FDFW_SingleScan_V_005 fdf_SS : spectralFile_Index_FDFW_FileContents_Root_V_005.getIndexScanEntries() ) {
			SpectralFile_Index_TDFR_SingleScan_V_005 tdfr_SS = new SpectralFile_Index_TDFR_SingleScan_V_005();
			tdfr_SS.setScanNumber( fdf_SS.getScanNumber() );
			tdfr_SS.setLevel( fdf_SS.getLevel() );
			tdfr_SS.setRetentionTime( fdf_SS.getRetentionTime() );
			tdfr_SS.setScanIndex_InDataFile_InBytes( fdf_SS.getScanIndex_InDataFile_InBytes() );
			tdfr_SS.setScanSize_InDataFile_InBytes( fdf_SS.getScanSize_InDataFile_InBytes() );
			
			//  tdfr_SS.indexFile_IndexPosition not set but not used below
			
			indexScanEntries.add( tdfr_SS );
		}

		//  call writeIndexFile(...) with populated SpectralFile_Index_TDFR_FileContents_Root_V_005 object
		SpectralFile_Index_TDFR_FileContents_Root_V_005 spectralFile_Index_TDFR_FileContents_Root_V_005 = new SpectralFile_Index_TDFR_FileContents_Root_V_005();
		
		spectralFile_Index_TDFR_FileContents_Root_V_005.setTotalBytesForAllSingleScans( spectralFile_Index_FDFW_FileContents_Root_V_005.getTotalBytesForAllSingleScans() );
		spectralFile_Index_TDFR_FileContents_Root_V_005.setSummaryDataPerScanLevelList( summaryDataPerScanLevelList );
		spectralFile_Index_TDFR_FileContents_Root_V_005.setIndexScanEntries( indexScanEntries );

		writeIndexFile( hash_String, subDirForStorageFiles, spectralFile_Index_TDFR_FileContents_Root_V_005, header_MainDataFile_Common );
	}
	
	/**
	 * @param hash_String
	 * @param subDirForStorageFiles
	 * @param spectralFile_Index_TDFR_FileContents_Root_V_005
	 * @throws Exception
	 */
	public void writeIndexFile( 
			String hash_String,
			File subDirForStorageFiles,
			SpectralFile_Index_TDFR_FileContents_Root_V_005 spectralFile_Index_TDFR_FileContents_Root_V_005,
			SpectralFile_Header_Common header_MainDataFile_Common ) throws Exception {
		
		
		

		String outputSpectralIndexFilename =
				CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Index_Filename( hash_String );
		
		String outputSpectralIndexFilenameWhileWriting =
				outputSpectralIndexFilename
				+ SpectralStorage_Filename_Constants.IN_PROGRESS_FILENAME_SUFFIX_SUFFIX;

		File indexFileWhileWriting = new File( subDirForStorageFiles, outputSpectralIndexFilenameWhileWriting );
		
		File indexFileFinal = new File( subDirForStorageFiles, outputSpectralIndexFilename );
		
		List<SpectralFile_Index_TDFR_SingleScan_V_005> indexScanEntries = spectralFile_Index_TDFR_FileContents_Root_V_005.getIndexScanEntries();
				

		/**
		 * summary per distinct scan level
		 */
		List<SpectralFile_Index_TDFR_SummaryDataPerScanLevel_V_005> summaryDataPerScanLevelList = spectralFile_Index_TDFR_FileContents_Root_V_005.getSummaryDataPerScanLevelList();

		//  WARNING, do not sort the records here. 
		//   That allows the reader to determine the index of each scan 
		//       by using the length of each scan in the file
		//   Also, if the scans in the scan file are all in sequential order
		//       the scan numbers are not stored in the index file.

		
		
		//  Validate that each scan number only occurs once, track if all scan numbers are in order
		boolean scanNumbersAreInOrder = true;
		{
			Set<Integer> scanNumbers = new HashSet<>();
			int prevScanNumber = Integer.MIN_VALUE;
			for ( SpectralFile_Index_TDFR_SingleScan_V_005 indexScanEntry : indexScanEntries ) {
				if ( ( ! scanNumbers.add( indexScanEntry.getScanNumber() ) ) ) {
					String msg = "Duplicate scan number in file: " + indexScanEntry.getScanNumber();
					log.error( msg );
					throw new SpectralStorageDataException( msg );
				}
				if ( prevScanNumber > indexScanEntry.getScanNumber() ) {
					scanNumbersAreInOrder = false;
				}
				prevScanNumber = indexScanEntry.getScanNumber();
			}
		}
		
		
		//  Check if Retention Times are in order
		boolean retentionTimesInOrder = true;
		{
			float prevRetentionTime = Float.NEGATIVE_INFINITY;
			for ( SpectralFile_Index_TDFR_SingleScan_V_005 indexScanEntry : indexScanEntries ) {
				if ( indexScanEntry.getRetentionTime() < prevRetentionTime ) {
					retentionTimesInOrder = false;
					break;
				}
				prevRetentionTime = indexScanEntry.getRetentionTime();
			}
		}
		

		byte scanNumberOffsetType = SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_NUMBER_OFFSET_TYPE_INT;
		byte scanSizeType = SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_SIZE_TYPE_INT;
		
		boolean scanNumberOffsetAlways_One = true;

		if ( ! indexScanEntries.isEmpty() ) {
			
			//  Get largest scan Number Offset from prev scan number
			int largestScanNumberOffsetFromPrev = 0;

			//  Don't expect to ever be Negative, just for protection
			//  Get most negative scan Number Offset from prev scan number.
			int mostNegativeScanNumberOffsetFromPrev = 0;

			//  Get largest scan size in bytes
			int largestScanSizeInBytes = 0;

			{
				SpectralFile_Index_TDFR_SingleScan_V_005 prevScan = null;
				for ( SpectralFile_Index_TDFR_SingleScan_V_005 indexScanEntry : indexScanEntries ) {
					if ( prevScan != null ) {
						int scanNumberOffset = indexScanEntry.getScanNumber() - prevScan.getScanNumber(); 
						if ( scanNumberOffset > largestScanNumberOffsetFromPrev ) {
							largestScanNumberOffsetFromPrev = scanNumberOffset;
						}
						if ( scanNumberOffset < mostNegativeScanNumberOffsetFromPrev ) {
							mostNegativeScanNumberOffsetFromPrev = scanNumberOffset;
						}
						if ( scanNumberOffset != 1 ) {
							scanNumberOffsetAlways_One = false;
//							System.out.println( "scanNumberOffset != 1: scanNumberOffset: " + scanNumberOffset
//									+ ", indexScanEntry.getScanNumber(): " + indexScanEntry.getScanNumber() );
						}
					}
					if ( indexScanEntry.getScanSize_InDataFile_InBytes() > largestScanSizeInBytes ) {
						largestScanSizeInBytes = indexScanEntry.getScanSize_InDataFile_InBytes();
					}
					prevScan = indexScanEntry;
				}
			}
			
			if ( scanNumberOffsetAlways_One ) {
				//  scanNumberOffset is always 1 so not needed per scan
				scanNumberOffsetType = SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_NUMBER_OFFSET_TYPE_NONE__SCAN_NUMBER_OFFSET_ALWAYS_DEFAULT_1;
			} else if ( largestScanNumberOffsetFromPrev <= Byte.MAX_VALUE && mostNegativeScanNumberOffsetFromPrev >= Byte.MIN_VALUE ) {
				scanNumberOffsetType = SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_NUMBER_OFFSET_TYPE_BYTE;
			} else if ( largestScanNumberOffsetFromPrev <= Short.MAX_VALUE && mostNegativeScanNumberOffsetFromPrev >= Short.MIN_VALUE ) {
				scanNumberOffsetType = SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_NUMBER_OFFSET_TYPE_SHORT;
			}
			
			if ( largestScanSizeInBytes < Byte.MAX_VALUE ) {
				scanSizeType = SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_SIZE_TYPE_BYTE;
			} else if ( largestScanSizeInBytes < Short.MAX_VALUE ) {
				scanSizeType = SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_SIZE_TYPE_SHORT;
			}
			
		}

		int first_SingleScan_ScanNumber = SpectralFile_Index_Header_DTO_V_005__Constants.FIRST_SINGLE_SCAN_SCAN_NUMBER_NOT_SET;
		
		long first_SingleScan_FileIndex = SpectralFile_Index_Header_DTO_V_005__Constants.FIRST_SINGLE_SCAN_FILE_INDEX_NOT_SET;

		{
			SpectralFile_Index_TDFR_SingleScan_V_005 firstScan = null;

			if ( ! indexScanEntries.isEmpty() ) {
				firstScan = indexScanEntries.iterator().next();
				first_SingleScan_ScanNumber = firstScan.getScanNumber();
				first_SingleScan_FileIndex = firstScan.getScanIndex_InDataFile_InBytes();
			}
		}
		
		try ( DataOutputStream dataOutputStream_IndexFile = 
				new DataOutputStream( new BufferedOutputStream( new FileOutputStream( indexFileWhileWriting ) ) )
				) {

			//  Write Version - ALWAYS FIRST
			
			log.warn("INFO: Writing Index File: FILE_VERSION " + FILE_VERSION );

			dataOutputStream_IndexFile.writeShort( FILE_VERSION );
			
			//  Write File Fully Written Indicator - ALWAYS SECOND
			
			dataOutputStream_IndexFile.writeByte( DataOrIndexFileFullyWrittenConstants.FILE_FULLY_WRITTEN_NO );
			
			//  Write Flags for if Values are populated on each/every scan
			

			{ //   Write Flag: totalIonCurrent_ForEachScan_ComputedFromScanPeaks
			
				byte flagValueByte = ByteValuesFor_Boolean_TrueFalse_Constants.BYTE_VALUE_FOR_BOOLEAN_FALSE;
				
				if ( header_MainDataFile_Common.getTotalIonCurrent_ForEachScan_ComputedFromScanPeaks() == null ) {
					String msg = "header.getTotalIonCurrent_ForEachScan_ComputedFromScanPeaks() == null";
					log.error(msg);
					throw new SpectralStorageProcessingException(msg);
				}
				
				if ( header_MainDataFile_Common.getTotalIonCurrent_ForEachScan_ComputedFromScanPeaks().booleanValue() ) {
					
					flagValueByte = ByteValuesFor_Boolean_TrueFalse_Constants.BYTE_VALUE_FOR_BOOLEAN_TRUE;
				}
			
				dataOutputStream_IndexFile.writeByte(flagValueByte);
			}

			{ //   Write Flag: ionInjectionTime_NotPopulated
			
				byte flagValueByte = ByteValuesFor_Boolean_TrueFalse_Constants.BYTE_VALUE_FOR_BOOLEAN_FALSE;
				
				if ( header_MainDataFile_Common.getIonInjectionTime_NotPopulated() == null ) {
					String msg = "header.getIonInjectionTime_NotPopulated() == null";
					log.error(msg);
					throw new SpectralStorageProcessingException(msg);
				}
				
				if ( header_MainDataFile_Common.getIonInjectionTime_NotPopulated().booleanValue() ) {
					
					flagValueByte = ByteValuesFor_Boolean_TrueFalse_Constants.BYTE_VALUE_FOR_BOOLEAN_TRUE;
				}
			
				dataOutputStream_IndexFile.writeByte(flagValueByte);
			}



			//  Write Summary data per distinct scan level
			if ( summaryDataPerScanLevelList.size() > Byte.MAX_VALUE ) {
				String msg = "summaryDataPerScanLevelList.size() > Byte.MAX_VALUE.  summaryDataPerScanLevelList.size(): " + summaryDataPerScanLevelList.size();
				log.error( msg );
				throw new SpectralStorageProcessingException( msg );
			}
			dataOutputStream_IndexFile.writeByte( summaryDataPerScanLevelList.size() );

			System.out.println( "Writing Accumulated scan totals per scan Level to index file:");
			for ( SpectralFile_Index_TDFR_SummaryDataPerScanLevel_V_005 summaryDataPerScanLevelEntry : summaryDataPerScanLevelList ) {
				dataOutputStream_IndexFile.writeByte( summaryDataPerScanLevelEntry.getScanLevel() );
				dataOutputStream_IndexFile.writeInt( summaryDataPerScanLevelEntry.getNumberOfScans() );
				// IsCentroidForThisScanLevel
				dataOutputStream_IndexFile.writeByte( summaryDataPerScanLevelEntry.getIsCentroidScanLevel() );
				// IsIonInjectionTimeForThisScanLevel
				dataOutputStream_IndexFile.writeByte( summaryDataPerScanLevelEntry.getIsIonInjectionTime_Set_ScanLevel() );
				
				dataOutputStream_IndexFile.writeDouble( summaryDataPerScanLevelEntry.getTotalIonCurrent() );
				
				System.out.println( "scan Level: " 
						+ summaryDataPerScanLevelEntry.getScanLevel() 
						+ ", number of scans: " 
						+ summaryDataPerScanLevelEntry.getNumberOfScans()
						+ ", Is Centroid at this scan level (2 == both): " 
						+ String.valueOf( summaryDataPerScanLevelEntry.getIsCentroidScanLevel() )
						+ ", Is IonInjectionTime Set at this scan level (2 == both): "
						+ String.valueOf( summaryDataPerScanLevelEntry.getIsIonInjectionTime_Set_ScanLevel() )
						+ ", Total Ion Current: " 
						+ summaryDataPerScanLevelEntry.getTotalIonCurrent() );
			}
			
			// scanNumbersAreInOrder
			if ( scanNumbersAreInOrder ) {
				dataOutputStream_IndexFile.writeByte( 1 );
			} else {
				dataOutputStream_IndexFile.writeByte( 0 );
			}
			// retentionTimesInOrder
			if ( retentionTimesInOrder ) {
				dataOutputStream_IndexFile.writeByte( 1 );
			} else {
				dataOutputStream_IndexFile.writeByte( 0 );
			}
			
			dataOutputStream_IndexFile.writeInt( indexScanEntries.size() ); // number of scans
			dataOutputStream_IndexFile.writeLong( spectralFile_Index_TDFR_FileContents_Root_V_005.getTotalBytesForAllSingleScans() );

			dataOutputStream_IndexFile.writeInt( first_SingleScan_ScanNumber );
			dataOutputStream_IndexFile.writeLong( first_SingleScan_FileIndex );
			
			dataOutputStream_IndexFile.writeByte( scanNumberOffsetType );
			dataOutputStream_IndexFile.writeByte( scanSizeType );

			int prev_scanNumber = first_SingleScan_ScanNumber;
			
			for ( SpectralFile_Index_TDFR_SingleScan_V_005 indexScanEntry : indexScanEntries ) {

				//  write scan size
				
				int scanSize_InDataFile_InBytes = indexScanEntry.getScanSize_InDataFile_InBytes();
				
				if ( scanSizeType == SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_SIZE_TYPE_BYTE ) {
					dataOutputStream_IndexFile.writeByte( scanSize_InDataFile_InBytes );
				} else if ( scanSizeType == SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_SIZE_TYPE_SHORT ) {
					dataOutputStream_IndexFile.writeShort( scanSize_InDataFile_InBytes );
				} else {
					dataOutputStream_IndexFile.writeInt( scanSize_InDataFile_InBytes );
				}
				
				//  write scan number offset

				if ( scanNumberOffsetType ==
						SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_NUMBER_OFFSET_TYPE_NONE__SCAN_NUMBER_OFFSET_ALWAYS_DEFAULT_1 ) {
					// do not write scanNumber_Offset value to file since always 1
				} else {
					int scanNumber_Offset = indexScanEntry.getScanNumber() - prev_scanNumber;

					if ( scanNumberOffsetType == 
							SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_NUMBER_OFFSET_TYPE_BYTE ) {
						dataOutputStream_IndexFile.writeByte( scanNumber_Offset );
					} else if ( scanNumberOffsetType == 
							SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_NUMBER_OFFSET_TYPE_SHORT ) {
						dataOutputStream_IndexFile.writeShort( scanNumber_Offset );
					} else {
						dataOutputStream_IndexFile.writeInt( scanNumber_Offset );
					}

					prev_scanNumber = indexScanEntry.getScanNumber();
				}
				
				//  write scan level and retention time

				dataOutputStream_IndexFile.writeByte( indexScanEntry.getLevel() );
				dataOutputStream_IndexFile.writeFloat( indexScanEntry.getRetentionTime() );
				
			}
		} catch( Exception e ) {
			log.error( "Error writing to index file: " + indexFileWhileWriting.getCanonicalPath() );
			throw e;
		} finally {

		}
		
		
		updateFileFullyWrittenIndicatorUpdateAfterFullCloseMainWriter( indexFileWhileWriting );

		//  Rename index file to final filename:
		
		if ( ! indexFileWhileWriting.renameTo( indexFileFinal ) ) {
			log.error( "Error renaming index file to final filename. Renaming from: "
					+ indexFileWhileWriting.getAbsolutePath() 
					+ ", renaming to: "
					+ indexFileFinal.getAbsolutePath() );
		}
		

		//  Output File with spectralDataFilename suffix with version of Data File Format
		
		{
			String spectralIndexFilename_With_FormatSuffixString = 
					CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Index_Filename_FileFormatVersion_Suffix( hash_String, FILE_VERSION );
			
			File spectralIndexFilename_With_FormatSuffixFile = new File( subDirForStorageFiles, spectralIndexFilename_With_FormatSuffixString );
			
			try ( BufferedWriter writer = new BufferedWriter( new FileWriter(spectralIndexFilename_With_FormatSuffixFile)) ) {
				
				writer.write( String.valueOf( FILE_VERSION ) );
			}
		}
		
		
		System.out.println( "***************************************" );
		System.out.println( "Index File Writing stats:");

		if ( scanSizeType == SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_SIZE_TYPE_BYTE ) {
			System.out.println( "Scan Size Type used: Byte" );
		} else if ( scanSizeType == SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_SIZE_TYPE_SHORT ) {
			System.out.println( "Scan Size Type used: Short" );
		} else {
			System.out.println( "Scan Size Type used: Int" );
		}
		
		//  Print scan number offset

		if ( scanNumberOffsetType ==
				SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_NUMBER_OFFSET_TYPE_NONE__SCAN_NUMBER_OFFSET_ALWAYS_DEFAULT_1 ) {
			System.out.println( "Scan Number Offset Type used: None since Always Default of 1" );
		} else {
			

			if ( scanNumberOffsetType == 
					SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_NUMBER_OFFSET_TYPE_BYTE ) {
				System.out.println( "Scan Number Offset Type used: Byte" );
			} else if ( scanNumberOffsetType == 
					SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_NUMBER_OFFSET_TYPE_SHORT ) {
				System.out.println( "Scan Number Offset Type used: Short" );
			} else {
				System.out.println( "Scan Number Offset Type used: Int" );
			}
		}
		
		System.out.println( "***************************************" );
		
	}

	/**
	 * @throws Exception
	 */
	private void updateFileFullyWrittenIndicatorUpdateAfterFullCloseMainWriter( 
			File indexFile ) throws Exception {

		ByteArrayOutputStream tempLocalOutputStream = new ByteArrayOutputStream( 20 );
		
		//  Surround tempOutputStream
		DataOutputStream dataOutputStream = new DataOutputStream( tempLocalOutputStream );


		//  Write Version - ALWAYS FIRST

		dataOutputStream.writeShort( FILE_VERSION );
		
		//  Write File Fully Written Indicator - ALWAYS SECOND
		
		dataOutputStream.writeByte( DataOrIndexFileFullyWrittenConstants.FILE_FULLY_WRITTEN_YES );

		dataOutputStream.flush();

//		int numBytesWritten = tempLocalOutputStream.size();
		
		byte[] bytesToWrite = tempLocalOutputStream.toByteArray();

		try ( RandomAccessFile spectalFile = new RandomAccessFile( indexFile, FILE_MODE_READ_WRITE ) ) {
		
//			spectalFile.getFilePointer();
//			spectalFile.length();
			
			spectalFile.seek( 0 );
			
			spectalFile.write( bytesToWrite );
		}
				
	}
	
}
