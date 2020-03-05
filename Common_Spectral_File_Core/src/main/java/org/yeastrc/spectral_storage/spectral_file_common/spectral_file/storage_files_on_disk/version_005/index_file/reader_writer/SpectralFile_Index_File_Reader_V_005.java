package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.reader_writer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.ByteValuesFor_Boolean_TrueFalse_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.DataOrIndexFileFullyWrittenConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralFileDataFileNotFullyWrittenException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.CreateSpectralStorageFilenames;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.StorageFile_Version_005_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.constants.SpectralFile_Index_Header_DTO_V_005__Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.to_data_file_reader_objects.SpectralFile_Index_TDFR_FileContents_Root_V_005;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.to_data_file_reader_objects.SpectralFile_Index_TDFR_SingleScan_V_005;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.to_data_file_reader_objects.SpectralFile_Index_TDFR_SummaryDataPerScanLevel_V_005;

/**
 * 
 * 
 * Assumes single scans go to the end of the index file
 *
 */
public class SpectralFile_Index_File_Reader_V_005 {
	
	private static final short FILE_VERSION = StorageFile_Version_005_Constants.FILE_VERSION;
	
	private static final int BUFFERED_READER_BUFFER_SIZE = 1 * 1024 * 1024; // 1MB

	private static final Logger log = LoggerFactory.getLogger(SpectralFile_Index_File_Reader_V_005.class);
	
	/**
	 * private constructor
	 */
	private SpectralFile_Index_File_Reader_V_005(){}
	public static SpectralFile_Index_File_Reader_V_005 getInstance( ) throws Exception {
		SpectralFile_Index_File_Reader_V_005 instance = new SpectralFile_Index_File_Reader_V_005();
		return instance;
	}
	
	/**
	 * @param hash_String
	 * @param commonReader_File_And_S3
	 * @return
	 * @throws Exception
	 */
	public SpectralFile_Index_TDFR_FileContents_Root_V_005 readIndexFile( 
			String hash_String, 
			CommonReader_File_And_S3 commonReader_File_And_S3 ) throws Exception {

		SpectralFile_Index_TDFR_FileContents_Root_V_005 spectralFile_Index_FileContents = new SpectralFile_Index_TDFR_FileContents_Root_V_005();

		List<SpectralFile_Index_TDFR_SingleScan_V_005> indexScanEntries = new ArrayList<>();
		spectralFile_Index_FileContents.setIndexScanEntries( indexScanEntries );

		try {

			String spectralIndexFilename =
					CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Index_Filename( hash_String );

			//   SpectralFile_Index_File_Common.getInstance().getSpectralFile_Index_File( inputDataFile );

			try ( DataInputStream dataInputStream_IndexFile = 
					new DataInputStream( 
							new BufferedInputStream( 
									commonReader_File_And_S3.getInputStreamForScanStorageItem( 
											spectralIndexFilename, 
											hash_String ), 
									BUFFERED_READER_BUFFER_SIZE ) )
					) {

				short fileVersionInFile = dataInputStream_IndexFile.readShort();

				if ( fileVersionInFile != FILE_VERSION ) {
					String msg = "File version does not match programatic version.  File Version: " + fileVersionInFile
							+ ", programatic version: " + FILE_VERSION
							+ ".  spectralIndexFilename: " + spectralIndexFilename;
					log.error( msg );
					throw new SpectralStorageProcessingException( msg );
				}

				byte fileFullWrittenIndicator = dataInputStream_IndexFile.readByte();

				if ( fileFullWrittenIndicator != DataOrIndexFileFullyWrittenConstants.FILE_FULLY_WRITTEN_YES ) {
					
					String msg = "Index File not fully written.  First byte after Version (Short, 2 bytes) is not 1.  Index File: " + spectralIndexFilename;
					log.error( msg );
					throw new SpectralFileDataFileNotFullyWrittenException(msg);
				}
				

				spectralFile_Index_FileContents.setVersion( fileVersionInFile );
				
				

				{ //   Read Flag: totalIonCurrent_ForEachScan_ComputedFromScanPeaks
				
					byte flagValueByte = dataInputStream_IndexFile.readByte();
					
					if ( flagValueByte == ByteValuesFor_Boolean_TrueFalse_Constants.BYTE_VALUE_FOR_BOOLEAN_TRUE ) {
						spectralFile_Index_FileContents.setTotalIonCurrent_ForEachScan_ComputedFromScanPeaks( true );  //  Is Boolean so set to true or false
					} else {
						spectralFile_Index_FileContents.setTotalIonCurrent_ForEachScan_ComputedFromScanPeaks( false );  //  Is Boolean so set to true or false
					}
				}

				{ //   Read Flag: ionInjectionTime_NotPopulated

					byte flagValueByte = dataInputStream_IndexFile.readByte();
					
					if ( flagValueByte == ByteValuesFor_Boolean_TrueFalse_Constants.BYTE_VALUE_FOR_BOOLEAN_TRUE ) {
						spectralFile_Index_FileContents.setIonInjectionTime_NotPopulated( true );  //  Is Boolean so set to true or false
					} else {
						spectralFile_Index_FileContents.setIonInjectionTime_NotPopulated( false );  //  Is Boolean so set to true or false
					}
				}

				
				//  Read Summary data per distinct scan level
				
				byte distinctScanLevelValuesCount = dataInputStream_IndexFile.readByte();
				
				List<SpectralFile_Index_TDFR_SummaryDataPerScanLevel_V_005> summaryDataPerScanLevelList = new ArrayList<>( distinctScanLevelValuesCount );
				spectralFile_Index_FileContents.setSummaryDataPerScanLevelList( summaryDataPerScanLevelList );
				
				for ( byte scanLevelIndex = 0; scanLevelIndex < distinctScanLevelValuesCount; scanLevelIndex++ ) {
					SpectralFile_Index_TDFR_SummaryDataPerScanLevel_V_005 summaryDataPerScanLevel = new SpectralFile_Index_TDFR_SummaryDataPerScanLevel_V_005();
					summaryDataPerScanLevel.setScanLevel( dataInputStream_IndexFile.readByte() );
					summaryDataPerScanLevel.setNumberOfScans( dataInputStream_IndexFile.readInt() );
					summaryDataPerScanLevel.setIsCentroidScanLevel( dataInputStream_IndexFile.readByte() );
					summaryDataPerScanLevel.setIsIonInjectionTime_Set_ScanLevel( dataInputStream_IndexFile.readByte() );
					summaryDataPerScanLevel.setTotalIonCurrent( dataInputStream_IndexFile.readDouble() );
					summaryDataPerScanLevelList.add( summaryDataPerScanLevel );
				}
				
				
				//////
				
				byte scansAreInScanNumberOrder = dataInputStream_IndexFile.readByte();
				byte scansAreInRetentionTimeOrder = dataInputStream_IndexFile.readByte();
				
				spectralFile_Index_FileContents.setNumberOfScans( dataInputStream_IndexFile.readInt() );
				spectralFile_Index_FileContents.setTotalBytesForAllSingleScans( dataInputStream_IndexFile.readLong() );
				
				int first_SingleScan_ScanNumber = dataInputStream_IndexFile.readInt();
				
				long first_SingleScan_FileIndex = dataInputStream_IndexFile.readLong();
				

				byte scanNumberOffsetType = dataInputStream_IndexFile.readByte();
				byte scanSizeType = dataInputStream_IndexFile.readByte();

				int indexFile_IndexPosition = 0;
				
				//  Prev Single Scan Entry
				SpectralFile_Index_TDFR_SingleScan_V_005 spectralFile_Index_TDFR_SingleScan_PREV = null; // prev scan
				
				//  Process single scans in index
				SpectralFile_Index_TDFR_SingleScan_V_005 spectralFile_Index_TDFR_SingleScan = null;
				
				while ( true ) { // exit loop with 'break;'
					
					spectralFile_Index_TDFR_SingleScan =
							readSingleIndexScanEntry( 
									dataInputStream_IndexFile,
									spectralFile_Index_TDFR_SingleScan_PREV,
									first_SingleScan_ScanNumber,
									first_SingleScan_FileIndex,
									scanNumberOffsetType,
									scanSizeType );

					if ( spectralFile_Index_TDFR_SingleScan == null ) { // null returned if at eof.  Assumes that single scans go to end of file
						break; // EXIT LOOP
					}
					
					indexScanEntries.add( spectralFile_Index_TDFR_SingleScan );
					
					spectralFile_Index_TDFR_SingleScan.setIndexFile_IndexPosition( indexFile_IndexPosition );
					
					spectralFile_Index_TDFR_SingleScan_PREV = spectralFile_Index_TDFR_SingleScan;
					
					indexFile_IndexPosition++;
				}
				
				if ( scansAreInScanNumberOrder == 0 ) {
					//  Sort on Scan Number, Comparable implemented on SpectralFile_Index_SingleScan_DTO
					Collections.sort( indexScanEntries );
					spectralFile_Index_FileContents.setScansAreInScanNumberOrder(true);
				} else {
					spectralFile_Index_FileContents.setScansAreInScanNumberOrder(true);
				}
				
				if ( scansAreInRetentionTimeOrder != 0 ) {
					spectralFile_Index_FileContents.setScansAreInRetentionTimeOrder(true);
				}

			} catch( Exception e ) {
				log.error( "Error reading from index file: " + spectralIndexFilename, e );
				throw e;
			}

			return spectralFile_Index_FileContents;
			

		} catch ( Exception e ) {
			
			log.error( "readIndexFile(...): threw exception for hash_String: " + hash_String, e );
			throw e;
		}
	}
	
	/**
	 * @param dataInputStream_IndexFile
	 * @return
	 * @throws IOException
	 * @throws SpectralStorageProcessingException 
	 */
	private SpectralFile_Index_TDFR_SingleScan_V_005 readSingleIndexScanEntry( 
			DataInputStream dataInputStream_IndexFile,
			SpectralFile_Index_TDFR_SingleScan_V_005 spectralFile_Index_TDFR_SingleScan_PREV,
			int first_SingleScan_ScanNumber,
			long first_SingleScan_FileIndex,

			byte scanNumberOffsetType,
			byte scanSizeType

			 ) throws IOException, SpectralStorageProcessingException {

		//  Read scan size
		
		int scanSize_InDataFile_InBytes = 0;
		try {
			if ( scanSizeType == SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_SIZE_TYPE_INT ) {
				scanSize_InDataFile_InBytes = dataInputStream_IndexFile.readInt();
			} else if ( scanSizeType == SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_SIZE_TYPE_SHORT ) {
				scanSize_InDataFile_InBytes = dataInputStream_IndexFile.readShort();
			} else if ( scanSizeType == SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_SIZE_TYPE_BYTE ) {
				scanSize_InDataFile_InBytes = dataInputStream_IndexFile.readByte();
			} else {
				String msg = "Unknown value for scanSizeType: " + scanSizeType;
				log.error( msg );
				throw new SpectralStorageProcessingException( msg );
			}
		} catch( EOFException eofException ) {
			//  Assume that normal end of file reached
			
			//  null returned if at eof.  Assumes that single scans go to end of file
			
			return null;  //  EARLY RETURN
		}

		SpectralFile_Index_TDFR_SingleScan_V_005 spectralFile_Index_TDFR_SingleScan = new SpectralFile_Index_TDFR_SingleScan_V_005();

		spectralFile_Index_TDFR_SingleScan.setScanSize_InDataFile_InBytes( scanSize_InDataFile_InBytes );
		
		
		//  Read scan number

		int scanNumberOffset = SpectralFile_Index_Header_DTO_V_005__Constants.FIRST_SINGLE_SCAN_SCAN_NUMBER_NOT_SET;

		if ( scanNumberOffsetType == SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_NUMBER_OFFSET_TYPE_NONE__SCAN_NUMBER_OFFSET_ALWAYS_DEFAULT_1 ) {
			//  Default is 1 and is not in file per scan. 
			scanNumberOffset = SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_NUMBER_OFFSET_DEFAULT_1;
			
		} else if ( scanNumberOffsetType == SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_NUMBER_OFFSET_TYPE_INT ) {
			scanNumberOffset = dataInputStream_IndexFile.readInt();
		} else if ( scanNumberOffsetType == SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_NUMBER_OFFSET_TYPE_SHORT ) {
			scanNumberOffset = dataInputStream_IndexFile.readShort();
		} else if ( scanNumberOffsetType == SpectralFile_Index_Header_DTO_V_005__Constants.SCAN_NUMBER_OFFSET_TYPE_BYTE ) {
			scanNumberOffset = dataInputStream_IndexFile.readByte();
		} else {
			String msg = "Unknown value for scanNumberOffsetType: " + scanNumberOffsetType;
			log.error( msg );
			throw new SpectralStorageProcessingException( msg );
		}

		
		if ( spectralFile_Index_TDFR_SingleScan_PREV == null ) {
			//  First record
			spectralFile_Index_TDFR_SingleScan.setScanNumber( first_SingleScan_ScanNumber );
		} else {
			int scanNumber = spectralFile_Index_TDFR_SingleScan_PREV.getScanNumber() + scanNumberOffset;

			spectralFile_Index_TDFR_SingleScan.setScanNumber( scanNumber );
		}
		
		spectralFile_Index_TDFR_SingleScan.setLevel( dataInputStream_IndexFile.readByte() );
		
		spectralFile_Index_TDFR_SingleScan.setRetentionTime( dataInputStream_IndexFile.readFloat() );
		
		if ( spectralFile_Index_TDFR_SingleScan_PREV == null ) {
			//  First record
			spectralFile_Index_TDFR_SingleScan.setScanIndex_InDataFile_InBytes( first_SingleScan_FileIndex );
		} else {

			//  Compute index from prev record 
			long scanIndex_InDataFile_InBytes = 
					spectralFile_Index_TDFR_SingleScan_PREV.getScanIndex_InDataFile_InBytes()
					+ spectralFile_Index_TDFR_SingleScan_PREV.getScanSize_InDataFile_InBytes();

			spectralFile_Index_TDFR_SingleScan.setScanIndex_InDataFile_InBytes( scanIndex_InDataFile_InBytes );
		}
		
		return spectralFile_Index_TDFR_SingleScan;
		
	}
	
}
