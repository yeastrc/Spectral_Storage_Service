package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.scans_lvl_gt_1_partial.reader_writer;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.DataOrIndexFileFullyWrittenConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.SpectralStorage_Filename_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.CreateSpectralStorageFilenames;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.scans_lvl_gt_1_partial.constants.SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.index_file.from_data_file_writer_objects.SpectralFile_Index_FDFW_FileContents_Root_V_003;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.index_file.from_data_file_writer_objects.SpectralFile_Index_FDFW_SingleScan_V_003;

/**
 * Any changes to data written require a NEW VERSION and a change to the corresponding SpectralFile_ScansLvlGt1Partial_File_Reader_V_###
 *
 */
public class SpectralFile_ScansLvlGt1Partial_File_Writer_V_003 {

	private static final Logger log = Logger.getLogger(SpectralFile_ScansLvlGt1Partial_File_Writer_V_003.class);

	private static final String FILE_MODE_READ_WRITE = "rw"; // Used in RandomAccessFile constructor below
	
	/**
	 * private constructor
	 */
	private SpectralFile_ScansLvlGt1Partial_File_Writer_V_003(){}
	public static SpectralFile_ScansLvlGt1Partial_File_Writer_V_003 getInstance( ) throws Exception {
		SpectralFile_ScansLvlGt1Partial_File_Writer_V_003 instance = new SpectralFile_ScansLvlGt1Partial_File_Writer_V_003();
		return instance;
	}
	

	/**
	 * @param hash_String
	 * @param subDirForStorageFiles
	 * @param spectralFile_Index_FDFW_FileContents_Root_V_003
	 * @throws Exception
	 */
	public void write_ScansLvlGt1Partial_File( 
			String hash_String,
			File subDirForStorageFiles,
			SpectralFile_Index_FDFW_FileContents_Root_V_003 spectralFile_Index_FDFW_FileContents_Root_V_003 ) throws Exception {

		String outputSpectralScansLvlGt1PartialFilename =
				CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Scans_Level_Gt_1_Partial_Filename( hash_String );
		
		String outputSpectralScansLvlGt1PartialFilenameWhileWriting =
				outputSpectralScansLvlGt1PartialFilename
				+ SpectralStorage_Filename_Constants.IN_PROGRESS_FILENAME_SUFFIX_SUFFIX;

		File scansLvlGt1PartialFileWhileWriting = new File( subDirForStorageFiles, outputSpectralScansLvlGt1PartialFilenameWhileWriting );
		
		File scansLvlGt1PartialFileFinal = new File( subDirForStorageFiles, outputSpectralScansLvlGt1PartialFilename );
		
		List<SpectralFile_Index_FDFW_SingleScan_V_003> indexScanEntries = spectralFile_Index_FDFW_FileContents_Root_V_003.getIndexScanEntries();
				
		//  WARNING, do not sort the records here. 
		//   That allows the reader to determine the index of each scan 
		//       by using the length of each scan in the file
		//   Also, if the scans in the scan file are all in sequential order
		//       the scan numbers are not stored in the index file.

		

//		int first_SingleScan_ScanNumber = SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.FIRST_SINGLE_SCAN_SCAN_NUMBER_NOT_SET;
//		
//		byte scanNumberOffsetType = SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.SCAN_NUMBER_OFFSET_TYPE_INT;
		
//		boolean scanNumberOffsetAlways_One = true;
		
		byte parentScanNumberOffsetType = SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.PARENT_SCAN_NUMBER_OFFSET_TYPE_INT;
		
		boolean parentScanNumberOffsetAlways_Negative_One = true;
		
		boolean foundScanLevelGt1 = false;

		if ( ! indexScanEntries.isEmpty() ) {

//			//  Get largest scan Number Offset from prev scan number
//			int largestScanNumberOffsetFromPrev = 0;
//
//			//  Don't expect to ever be Negative, just for protection
//			//  Get most negative scan Number Offset from prev scan number.
//			int mostNegativeScanNumberOffsetFromPrev = 0;

			//  Don't expect to ever be positive, just for protection
			//  Get most positive parent scan Number Offset from scan number
			int mostPositiveParentScanNumberOffsetFromScanNumber = 0;

			//  Get most negative parent scan Number Offset from scan number
			int mostNegativeParentScanNumberOffsetFromScanNumber = 0;

			{
				SpectralFile_Index_FDFW_SingleScan_V_003 prevScan = null;
				for ( SpectralFile_Index_FDFW_SingleScan_V_003 indexScanEntry : indexScanEntries ) {
					if ( indexScanEntry.getLevel() == 1 ) {
						continue;  // do not process level 1 scans
					}
					foundScanLevelGt1 = true;
					
					// save off first scan number for scan level != 1
//					if ( first_SingleScan_ScanNumber == SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.FIRST_SINGLE_SCAN_SCAN_NUMBER_NOT_SET ) {
//						first_SingleScan_ScanNumber = indexScanEntry.getScanNumber();
//					}
					
					// Scan Number Offset
//					if ( prevScan != null ) {
//						int scanNumberOffset = indexScanEntry.getScanNumber() - prevScan.getScanNumber(); 
//						if ( scanNumberOffset > largestScanNumberOffsetFromPrev ) {
//							largestScanNumberOffsetFromPrev = scanNumberOffset;
//						}
//						if ( scanNumberOffset < mostNegativeScanNumberOffsetFromPrev ) {
//							mostNegativeScanNumberOffsetFromPrev = scanNumberOffset;
//						}
//						if ( scanNumberOffset != 1 ) {
//							scanNumberOffsetAlways_One = false;
////							System.out.println( "scanNumberOffset != 1: scanNumberOffset: " + scanNumberOffset
////									+ ", indexScanEntry.getScanNumber(): " + indexScanEntry.getScanNumber() );
//						}
//					}
					//  Parent Scan Number Offset
					int parentScanNumberOffset = indexScanEntry.getParentScanNumber() - indexScanEntry.getScanNumber();
					if ( parentScanNumberOffset > mostPositiveParentScanNumberOffsetFromScanNumber ) {
						mostPositiveParentScanNumberOffsetFromScanNumber = parentScanNumberOffset;
					}
					if ( parentScanNumberOffset < mostNegativeParentScanNumberOffsetFromScanNumber ) {
						mostNegativeParentScanNumberOffsetFromScanNumber = parentScanNumberOffset;
					}
					if ( parentScanNumberOffset != -1 ) {
						parentScanNumberOffsetAlways_Negative_One = false;
//						System.out.println( "parentScanNumberOffset != -1: parentScanNumberOffset: " + parentScanNumberOffset
//								+ ", indexScanEntry.getScanNumber(): " + indexScanEntry.getScanNumber() );
					}
					prevScan = indexScanEntry;
				}
			}

//			if ( scanNumberOffsetAlways_One ) {
//				//  scanNumberOffset is always 1 so not needed per scan
//				scanNumberOffsetType = SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.SCAN_NUMBER_OFFSET_TYPE_NONE__SCAN_NUMBER_OFFSET_ALWAYS_DEFAULT_1;
//			} else if ( largestScanNumberOffsetFromPrev <= Byte.MAX_VALUE && mostNegativeScanNumberOffsetFromPrev >= Byte.MIN_VALUE ) {
//				scanNumberOffsetType = SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.SCAN_NUMBER_OFFSET_TYPE_BYTE;
//			} else if ( largestScanNumberOffsetFromPrev <= Short.MAX_VALUE && mostNegativeScanNumberOffsetFromPrev >= Short.MIN_VALUE ) {
//				scanNumberOffsetType = SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.SCAN_NUMBER_OFFSET_TYPE_SHORT;
//			}
			
			if ( parentScanNumberOffsetAlways_Negative_One ) {
				//  scanNumberOffset is always 1 so not needed per scan
				parentScanNumberOffsetType = SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.PARENT_SCAN_NUMBER_OFFSET_TYPE_NONE__PARENT_SCAN_NUMBER_OFFSET_ALWAYS_DEFAULT_NEGATIVE_1;
			} else if ( mostPositiveParentScanNumberOffsetFromScanNumber <= Byte.MAX_VALUE && mostNegativeParentScanNumberOffsetFromScanNumber >= Byte.MIN_VALUE ) {
				parentScanNumberOffsetType = SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.PARENT_SCAN_NUMBER_OFFSET_TYPE_BYTE;
			} else if ( mostPositiveParentScanNumberOffsetFromScanNumber <= Short.MAX_VALUE && mostNegativeParentScanNumberOffsetFromScanNumber >= Short.MIN_VALUE ) {
				parentScanNumberOffsetType = SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.PARENT_SCAN_NUMBER_OFFSET_TYPE_SHORT;
			}
		}
		
		if ( ! foundScanLevelGt1 ) {
			//  No scans with level != 1 found
			return;  // EARLY EXIT
		}


		//  Save disk space and read time by not including scan number offset, compute from index file
		
		try ( DataOutputStream dataOutputStream_IndexFile = 
				new DataOutputStream( new BufferedOutputStream( new FileOutputStream( scansLvlGt1PartialFileWhileWriting ) ) )
				) {

			//  Write Version - ALWAYS FIRST

			dataOutputStream_IndexFile.writeShort( spectralFile_Index_FDFW_FileContents_Root_V_003.getVersion() );
			
			//  Write File Fully Written Indicator - ALWAYS SECOND
			
			dataOutputStream_IndexFile.writeByte( DataOrIndexFileFullyWrittenConstants.FILE_FULLY_WRITTEN_NO );

//			dataOutputStream_IndexFile.writeByte( scanNumberOffsetType );
			
			dataOutputStream_IndexFile.writeByte( parentScanNumberOffsetType );

//			dataOutputStream_IndexFile.writeInt( first_SingleScan_ScanNumber );
			
//			int prev_scanNumber = first_SingleScan_ScanNumber;
			
			for ( SpectralFile_Index_FDFW_SingleScan_V_003 indexScanEntry : indexScanEntries ) {

				if ( indexScanEntry.getLevel() == 1 ) {
					continue;  // do not process level 1 scans
				}
				
//				//  write scan number offset
//
//				if ( scanNumberOffsetType ==
//						SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.SCAN_NUMBER_OFFSET_TYPE_NONE__SCAN_NUMBER_OFFSET_ALWAYS_DEFAULT_1 ) {
//					// do not write scanNumber_Offset value to file since always 1
//				} else {
//					int scanNumber_Offset = indexScanEntry.getScanNumber() - prev_scanNumber;
//
//					if ( scanNumberOffsetType == 
//							SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.SCAN_NUMBER_OFFSET_TYPE_BYTE ) {
//						dataOutputStream_IndexFile.writeByte( scanNumber_Offset );
//					} else if ( scanNumberOffsetType == 
//							SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.SCAN_NUMBER_OFFSET_TYPE_SHORT ) {
//						dataOutputStream_IndexFile.writeShort( scanNumber_Offset );
//					} else {
//						dataOutputStream_IndexFile.writeInt( scanNumber_Offset );
//					}
//
//					prev_scanNumber = indexScanEntry.getScanNumber();
//				}
				
				//  write parent scan number offset

				if ( parentScanNumberOffsetType ==
						SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.PARENT_SCAN_NUMBER_OFFSET_TYPE_NONE__PARENT_SCAN_NUMBER_OFFSET_ALWAYS_DEFAULT_NEGATIVE_1 ) {
					// do not write scanNumber_Offset value to file since always -1
				} else {
					int parentScanNumberOffset = indexScanEntry.getParentScanNumber() - indexScanEntry.getScanNumber();

					if ( parentScanNumberOffsetType == 
							SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.PARENT_SCAN_NUMBER_OFFSET_TYPE_BYTE ) {
						dataOutputStream_IndexFile.writeByte( parentScanNumberOffset );
					} else if ( parentScanNumberOffsetType == 
							SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.PARENT_SCAN_NUMBER_OFFSET_TYPE_SHORT ) {
						dataOutputStream_IndexFile.writeShort( parentScanNumberOffset );
					} else {
						dataOutputStream_IndexFile.writeInt( parentScanNumberOffset );
					}
				}
				
				//  write precursor charge and M/Z

				dataOutputStream_IndexFile.writeByte( indexScanEntry.getPrecursorCharge() );
				dataOutputStream_IndexFile.writeDouble( indexScanEntry.getPrecursor_M_Over_Z() );
				
			}
		} catch( Exception e ) {
			log.error( "Error writing to index file: " + scansLvlGt1PartialFileWhileWriting.getCanonicalPath() );
			throw e;
		} finally {

		}
		
		
		updateFileFullyWrittenIndicatorUpdateAfterFullCloseMainWriter( scansLvlGt1PartialFileWhileWriting, spectralFile_Index_FDFW_FileContents_Root_V_003 );

		//  Rename index file to final filename:
		
		if ( ! scansLvlGt1PartialFileWhileWriting.renameTo( scansLvlGt1PartialFileFinal ) ) {
			log.error( "Error renaming Scans Level > 1 Partial file to final filename. Renaming from: "
					+ scansLvlGt1PartialFileWhileWriting.getAbsolutePath() 
					+ ", renaming to: "
					+ scansLvlGt1PartialFileFinal.getAbsolutePath() );
		}
		
		System.out.println( "***************************************" );
		System.out.println( "Scans Level > 1 Partial File Writing stats:");
		

		//  report scan number offset size used

//		if ( scanNumberOffsetType ==
//				SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.SCAN_NUMBER_OFFSET_TYPE_NONE__SCAN_NUMBER_OFFSET_ALWAYS_DEFAULT_1 ) {
//			System.out.println( "Scan Number Offset Type used: None since Always Default of 1" );
//		} else {
//			
//
//			if ( scanNumberOffsetType == 
//					SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.SCAN_NUMBER_OFFSET_TYPE_BYTE ) {
//				System.out.println( "Scan Number Offset Type used: Byte" );
//			} else if ( scanNumberOffsetType == 
//					SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.SCAN_NUMBER_OFFSET_TYPE_SHORT ) {
//				System.out.println( "Scan Number Offset Type used: Short" );
//			} else {
//				System.out.println( "Scan Number Offset Type used: Int" );
//			}
//		}
		
		
		//  report parent scan number offset size used

		if ( parentScanNumberOffsetType ==
				SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.PARENT_SCAN_NUMBER_OFFSET_TYPE_NONE__PARENT_SCAN_NUMBER_OFFSET_ALWAYS_DEFAULT_NEGATIVE_1 ) {
			System.out.println( "Parent Scan Number Offset Type used: None since Always Default of -1" );
		} else {
			

			if ( parentScanNumberOffsetType == 
					SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.PARENT_SCAN_NUMBER_OFFSET_TYPE_BYTE ) {
				System.out.println( "Parent Scan Number Offset Type used: Byte" );
			} else if ( parentScanNumberOffsetType == 
					SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.PARENT_SCAN_NUMBER_OFFSET_TYPE_SHORT ) {
				System.out.println( "Parent Scan Number Offset Type used: Short" );
			} else {
				System.out.println( "Parent Scan Number Offset Type used: Int" );
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
