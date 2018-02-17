package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.index_file.reader_writer;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.accum_scan_summary_data.AccumulateSummaryDataPerScanLevel;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.accum_scan_summary_data.AccumulateSummaryDataPerScanLevelResult;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.accum_scan_summary_data.AccumulateSummaryDataPerScanLevelSingleLevelResult;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.DataOrIndexFileFullyWrittenConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.SpectralStorage_Filename_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.CreateSpectralStorageFilenames;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.index_file.constants.SpectralFile_Index_Header_DTO_V_003__Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.index_file.from_data_file_writer_objects.SpectralFile_Index_FDFW_FileContents_Root_V_003;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.index_file.from_data_file_writer_objects.SpectralFile_Index_FDFW_SingleScan_V_003;

/**
 * Any changes to data written require a NEW VERSION and a change to the corresponding SpectralFile_Index_File_Reader_V_###
 *
 */
public class SpectralFile_Index_File_Writer_V_003 {

	private static final Logger log = Logger.getLogger(SpectralFile_Index_File_Writer_V_003.class);

	private static final String FILE_MODE_READ_WRITE = "rw"; // Used in RandomAccessFile constructor below
	
	/**
	 * private constructor
	 */
	private SpectralFile_Index_File_Writer_V_003(){}
	public static SpectralFile_Index_File_Writer_V_003 getInstance( ) throws Exception {
		SpectralFile_Index_File_Writer_V_003 instance = new SpectralFile_Index_File_Writer_V_003();
		return instance;
	}
	

	/**
	 * @param hash_String
	 * @param subDirForStorageFiles
	 * @param spectralFile_Index_FDFW_FileContents_Root_V_003
	 * @throws Exception
	 */
	public void writeIndexFile( 
			String hash_String,
			File subDirForStorageFiles,
			SpectralFile_Index_FDFW_FileContents_Root_V_003 spectralFile_Index_FDFW_FileContents_Root_V_003,
			AccumulateSummaryDataPerScanLevel accumulateSummaryDataPerScanLevel ) throws Exception {

		String outputSpectralIndexFilename =
				CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Index_Filename( hash_String );
		
		String outputSpectralIndexFilenameWhileWriting =
				outputSpectralIndexFilename
				+ SpectralStorage_Filename_Constants.IN_PROGRESS_FILENAME_SUFFIX_SUFFIX;

		File indexFileWhileWriting = new File( subDirForStorageFiles, outputSpectralIndexFilenameWhileWriting );
		
		File indexFileFinal = new File( subDirForStorageFiles, outputSpectralIndexFilename );
		
		List<SpectralFile_Index_FDFW_SingleScan_V_003> indexScanEntries = spectralFile_Index_FDFW_FileContents_Root_V_003.getIndexScanEntries();
				
		AccumulateSummaryDataPerScanLevelResult accumulateSummaryDataPerScanLevelResult = accumulateSummaryDataPerScanLevel.getAccumResult();
		List<AccumulateSummaryDataPerScanLevelSingleLevelResult> summaryDataPerScanLevelList = accumulateSummaryDataPerScanLevelResult.getSummaryDataPerScanLevelList();

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
			for ( SpectralFile_Index_FDFW_SingleScan_V_003 indexScanEntry : indexScanEntries ) {
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
			for ( SpectralFile_Index_FDFW_SingleScan_V_003 indexScanEntry : indexScanEntries ) {
				if ( indexScanEntry.getRetentionTime() < prevRetentionTime ) {
					retentionTimesInOrder = false;
					break;
				}
				prevRetentionTime = indexScanEntry.getRetentionTime();
			}
		}
		

		byte scanNumberOffsetType = SpectralFile_Index_Header_DTO_V_003__Constants.SCAN_NUMBER_OFFSET_TYPE_INT;
		byte scanSizeType = SpectralFile_Index_Header_DTO_V_003__Constants.SCAN_SIZE_TYPE_INT;
		
		boolean scanNumberOffsetAlways_One = true;

		if ( ! indexScanEntries.isEmpty() ) {
			
			//  Get largest scan Number Offset from prev scan number
			int largestScanNumberOffsetFromPrev = 0;

			//  Get largest scan size in bytes
			int largestScanSizeInBytes = 0;

			{
				SpectralFile_Index_FDFW_SingleScan_V_003 prevScan = null;
				for ( SpectralFile_Index_FDFW_SingleScan_V_003 indexScanEntry : indexScanEntries ) {
					if ( prevScan != null ) {
						int scanNumberOffset = indexScanEntry.getScanNumber() - prevScan.getScanNumber(); 
						if ( scanNumberOffset > largestScanNumberOffsetFromPrev ) {
							largestScanNumberOffsetFromPrev = scanNumberOffset;
						}
						if ( scanNumberOffset != 1 ) {
							scanNumberOffsetAlways_One = false;
							System.out.println( "scanNumberOffset != 1: scanNumberOffset: " + scanNumberOffset
									+ ", indexScanEntry.getScanNumber(): " + indexScanEntry.getScanNumber() );
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
				scanNumberOffsetType = SpectralFile_Index_Header_DTO_V_003__Constants.SCAN_NUMBER_OFFSET_TYPE_NONE__SCAN_NUMBER_OFFSET_ALWAYS_DEFAULT_1;
			} else if ( largestScanNumberOffsetFromPrev < Byte.MAX_VALUE ) {
				scanNumberOffsetType = SpectralFile_Index_Header_DTO_V_003__Constants.SCAN_NUMBER_OFFSET_TYPE_BYTE;
			} else if ( largestScanNumberOffsetFromPrev < Short.MAX_VALUE ) {
				scanNumberOffsetType = SpectralFile_Index_Header_DTO_V_003__Constants.SCAN_NUMBER_OFFSET_TYPE_SHORT;
			}
			
			if ( largestScanSizeInBytes < Byte.MAX_VALUE ) {
				scanSizeType = SpectralFile_Index_Header_DTO_V_003__Constants.SCAN_SIZE_TYPE_BYTE;
			} else if ( largestScanSizeInBytes < Short.MAX_VALUE ) {
				scanSizeType = SpectralFile_Index_Header_DTO_V_003__Constants.SCAN_SIZE_TYPE_SHORT;
			}
			
		}

		int first_SingleScan_ScanNumber = SpectralFile_Index_Header_DTO_V_003__Constants.FIRST_SINGLE_SCAN_SCAN_NUMBER_NOT_SET;
		
		long first_SingleScan_FileIndex = SpectralFile_Index_Header_DTO_V_003__Constants.FIRST_SINGLE_SCAN_FILE_INDEX_NOT_SET;

		SpectralFile_Index_FDFW_SingleScan_V_003 firstScan = null;
		
		if ( ! indexScanEntries.isEmpty() ) {
			firstScan = indexScanEntries.iterator().next();
			first_SingleScan_ScanNumber = firstScan.getScanNumber();
			first_SingleScan_FileIndex = firstScan.getScanIndex_InDataFile_InBytes();
		}
		
		try ( DataOutputStream dataOutputStream_IndexFile = 
				new DataOutputStream( new BufferedOutputStream( new FileOutputStream( indexFileWhileWriting ) ) )
				) {

			//  Write Version - ALWAYS FIRST

			dataOutputStream_IndexFile.writeShort( spectralFile_Index_FDFW_FileContents_Root_V_003.getVersion() );
			
			//  Write File Fully Written Indicator - ALWAYS SECOND
			
			dataOutputStream_IndexFile.writeByte( DataOrIndexFileFullyWrittenConstants.FILE_FULLY_WRITTEN_NO );

			// IsCentroidWholeFile
			dataOutputStream_IndexFile.writeByte( spectralFile_Index_FDFW_FileContents_Root_V_003.getIsCentroidWholeFile() );
			
			//  Write Summary data per distinct scan level
			if ( summaryDataPerScanLevelList.size() > Byte.MAX_VALUE ) {
				String msg = "summaryDataPerScanLevelList.size() > Byte.MAX_VALUE.  summaryDataPerScanLevelList.size(): " + summaryDataPerScanLevelList.size();
				log.error( msg );
				throw new SpectralStorageProcessingException( msg );
			}
			dataOutputStream_IndexFile.writeByte( summaryDataPerScanLevelList.size() );

			System.out.println( "Writing Accumulated scan totals per scan Level to index file:");
			for ( AccumulateSummaryDataPerScanLevelSingleLevelResult summaryDataPerScanLevelEntry : summaryDataPerScanLevelList ) {
				dataOutputStream_IndexFile.writeByte( summaryDataPerScanLevelEntry.getScanLevel() );
				dataOutputStream_IndexFile.writeInt( summaryDataPerScanLevelEntry.getNumberOfScans() );
				dataOutputStream_IndexFile.writeDouble( summaryDataPerScanLevelEntry.getTotalIonCurrent() );
				
				System.out.println( "scan Level: " 
						+ summaryDataPerScanLevelEntry.getScanLevel() 
						+ ", number of scans: " 
						+ summaryDataPerScanLevelEntry.getNumberOfScans()
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
			dataOutputStream_IndexFile.writeLong( spectralFile_Index_FDFW_FileContents_Root_V_003.getTotalBytesForAllSingleScans() );

			dataOutputStream_IndexFile.writeInt( first_SingleScan_ScanNumber );
			dataOutputStream_IndexFile.writeLong( first_SingleScan_FileIndex );
			
			dataOutputStream_IndexFile.writeByte( scanNumberOffsetType );
			dataOutputStream_IndexFile.writeByte( scanSizeType );

			int prev_scanNumber = first_SingleScan_ScanNumber;
			
			for ( SpectralFile_Index_FDFW_SingleScan_V_003 indexScanEntry : indexScanEntries ) {

				//  write scan size
				
				int scanSize_InDataFile_InBytes = indexScanEntry.getScanSize_InDataFile_InBytes();
				
				if ( scanSizeType == SpectralFile_Index_Header_DTO_V_003__Constants.SCAN_SIZE_TYPE_BYTE ) {
					dataOutputStream_IndexFile.writeByte( scanSize_InDataFile_InBytes );
				} else if ( scanSizeType == SpectralFile_Index_Header_DTO_V_003__Constants.SCAN_SIZE_TYPE_SHORT ) {
					dataOutputStream_IndexFile.writeShort( scanSize_InDataFile_InBytes );
				} else {
					dataOutputStream_IndexFile.writeInt( scanSize_InDataFile_InBytes );
				}
				
				//  write scan number offset

				if ( scanNumberOffsetType ==
						SpectralFile_Index_Header_DTO_V_003__Constants.SCAN_NUMBER_OFFSET_TYPE_NONE__SCAN_NUMBER_OFFSET_ALWAYS_DEFAULT_1 ) {
					// do not write scanNumber_Offset value to file since always 1
				} else {
					int scanNumber_Offset = indexScanEntry.getScanNumber() - prev_scanNumber;

					if ( scanNumberOffsetType == 
							SpectralFile_Index_Header_DTO_V_003__Constants.SCAN_NUMBER_OFFSET_TYPE_BYTE ) {
						dataOutputStream_IndexFile.writeByte( scanNumber_Offset );
					} else if ( scanNumberOffsetType == 
							SpectralFile_Index_Header_DTO_V_003__Constants.SCAN_NUMBER_OFFSET_TYPE_SHORT ) {
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
		
		
		updateFileFullyWrittenIndicatorUpdateAfterFullCloseMainWriter( indexFileWhileWriting, spectralFile_Index_FDFW_FileContents_Root_V_003 );

		//  Rename index file to final filename:
		
		if ( ! indexFileWhileWriting.renameTo( indexFileFinal ) ) {
			log.error( "Error renaming index file to final filename. Renaming from: "
					+ indexFileWhileWriting.getAbsolutePath() 
					+ ", renaming to: "
					+ indexFileFinal.getAbsolutePath() );
		}
		
		System.out.println( "***************************************" );
		System.out.println( "Index File Writing stats:");

		if ( scanSizeType == SpectralFile_Index_Header_DTO_V_003__Constants.SCAN_SIZE_TYPE_BYTE ) {
			System.out.println( "Scan Size Type used: Byte" );
		} else if ( scanSizeType == SpectralFile_Index_Header_DTO_V_003__Constants.SCAN_SIZE_TYPE_SHORT ) {
			System.out.println( "Scan Size Type used: Short" );
		} else {
			System.out.println( "Scan Size Type used: Int" );
		}
		
		//  write scan number offset

		if ( scanNumberOffsetType ==
				SpectralFile_Index_Header_DTO_V_003__Constants.SCAN_NUMBER_OFFSET_TYPE_NONE__SCAN_NUMBER_OFFSET_ALWAYS_DEFAULT_1 ) {
			System.out.println( "Scan Number Offset Type used: None since Always Default of 1" );
		} else {
			

			if ( scanNumberOffsetType == 
					SpectralFile_Index_Header_DTO_V_003__Constants.SCAN_NUMBER_OFFSET_TYPE_BYTE ) {
				System.out.println( "Scan Number Offset Type used: Byte" );
			} else if ( scanNumberOffsetType == 
					SpectralFile_Index_Header_DTO_V_003__Constants.SCAN_NUMBER_OFFSET_TYPE_SHORT ) {
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
			File indexFile, 
			SpectralFile_Index_FDFW_FileContents_Root_V_003 spectralFile_Index_FDFW_FileContents_Root_V_003 ) throws Exception {

		ByteArrayOutputStream tempLocalOutputStream = new ByteArrayOutputStream( 20 );
		
		//  Surround tempOutputStream
		DataOutputStream dataOutputStream = new DataOutputStream( tempLocalOutputStream );


		//  Write Version - ALWAYS FIRST

		dataOutputStream.writeShort( spectralFile_Index_FDFW_FileContents_Root_V_003.getVersion() );
		
		//  Write File Fully Written Indicator - ALWAYS SECOND
		
		dataOutputStream.writeByte( DataOrIndexFileFullyWrittenConstants.FILE_FULLY_WRITTEN_YES );

		dataOutputStream.flush();

		int numBytesWritten = tempLocalOutputStream.size();
		
		byte[] bytesToWrite = tempLocalOutputStream.toByteArray();

		try ( RandomAccessFile spectalFile = new RandomAccessFile( indexFile, FILE_MODE_READ_WRITE ) ) {
		
//			spectalFile.getFilePointer();
//			spectalFile.length();
			
			spectalFile.seek( 0 );
			
			spectalFile.write( bytesToWrite );
		}
				
	}
}
