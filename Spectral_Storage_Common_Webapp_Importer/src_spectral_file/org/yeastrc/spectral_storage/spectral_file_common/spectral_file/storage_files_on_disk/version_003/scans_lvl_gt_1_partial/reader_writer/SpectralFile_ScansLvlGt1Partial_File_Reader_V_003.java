package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.scans_lvl_gt_1_partial.reader_writer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.DataOrIndexFileFullyWrittenConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralFileDataFileNotFullyWrittenException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.index_file.SpectralFile_Index_FileContents_Root_IF;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.index_file_root_data_object_cache.IndexFileRootDataObjectCache;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.CreateSpectralStorageFilenames;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.StorageFile_Version_003_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_004.index_file.to_data_file_reader_objects.SpectralFile_Index_TDFR_FileContents_Root_V_004;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_004.index_file.to_data_file_reader_objects.SpectralFile_Index_TDFR_SingleScan_V_004;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.scans_lvl_gt_1_partial.constants.SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.scans_lvl_gt_1_partial.to_data_file_reader_objects.SpectralFile_ScansLvlGt1Partial_TDFR_FileContents_Root_V_003;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.scans_lvl_gt_1_partial.to_data_file_reader_objects.SpectralFile_ScansLvlGt1Partial_TDFR_SingleScan_V_003;

/**
 * 
 *
 */
public class SpectralFile_ScansLvlGt1Partial_File_Reader_V_003 {

	private static final short FILE_VERSION = StorageFile_Version_003_Constants.FILE_VERSION;
	
	//  File version Zero also allowed since had bug in V003 File Writer that stored zero in version
	private static final short FILE_VERSION_ZERO = 0;  
	
	private static final int BUFFERED_READER_BUFFER_SIZE = 1 * 1024 * 1024; // 1MB

	private static final Logger log = Logger.getLogger(SpectralFile_ScansLvlGt1Partial_File_Reader_V_003.class);
	
	/**
	 * private constructor
	 */
	private SpectralFile_ScansLvlGt1Partial_File_Reader_V_003(){}
	public static SpectralFile_ScansLvlGt1Partial_File_Reader_V_003 getInstance( ) throws Exception {
		SpectralFile_ScansLvlGt1Partial_File_Reader_V_003 instance = new SpectralFile_ScansLvlGt1Partial_File_Reader_V_003();
		return instance;
	}
	
	/**
	 * @param hash_String
	 * @param commonReader_File_And_S3
	 * @return
	 * @throws Exception
	 */
	public SpectralFile_ScansLvlGt1Partial_TDFR_FileContents_Root_V_003 readScansLvlGt1PartialFile( 
			String hash_String, 
			CommonReader_File_And_S3 commonReader_File_And_S3 ) throws Exception {

		SpectralFile_ScansLvlGt1Partial_TDFR_FileContents_Root_V_003 spectralFile_Index_FileContents = new SpectralFile_ScansLvlGt1Partial_TDFR_FileContents_Root_V_003();

		try {
			List<SpectralFile_Index_TDFR_SingleScan_V_004> indexScanLevelGt1List =
					get_indexScanEntries_OnlyLevelGt1InIndexFileOrder( hash_String );
			
			//  Next read file for this data
			
			String spectralScans_Level_Gt_1_PartialFilename =
					CreateSpectralStorageFilenames.getInstance().
					createSpectraStorage_Scans_Level_Gt_1_Partial_Filename( hash_String );
			
			try ( DataInputStream dataInputStream_IndexFile = 
					new DataInputStream( 
							new BufferedInputStream( 
									commonReader_File_And_S3.getInputStreamForScanStorageItem( 
											spectralScans_Level_Gt_1_PartialFilename, 
											hash_String ), 
									BUFFERED_READER_BUFFER_SIZE ) )
					) {

				short fileVersionInFile = dataInputStream_IndexFile.readShort();

				if ( fileVersionInFile != FILE_VERSION 
						&& fileVersionInFile != FILE_VERSION_ZERO
						) {
					String msg = "File version does not match programatic version.  File Version: " + fileVersionInFile
							+ ", programatic version: " + FILE_VERSION
							+ " 'programatic version also accepts version of " + FILE_VERSION_ZERO
							+ ".  spectralScans_Level_Gt_1_PartialFilename: " + spectralScans_Level_Gt_1_PartialFilename;
					log.error( msg );
					throw new SpectralStorageProcessingException( msg );
				}

				byte fileFullWrittenIndicator = dataInputStream_IndexFile.readByte();

				if ( fileFullWrittenIndicator != DataOrIndexFileFullyWrittenConstants.FILE_FULLY_WRITTEN_YES ) {
					
					String msg = "Index File not fully written.  First byte is not 1.  spectralScans_Level_Gt_1_PartialFilename: " + spectralScans_Level_Gt_1_PartialFilename;
					log.error( msg );
					throw new SpectralFileDataFileNotFullyWrittenException(msg);
				}
				

				spectralFile_Index_FileContents.setVersion( fileVersionInFile );

				byte parentScanNumberOffsetType = dataInputStream_IndexFile.readByte();

				List<PerScanFromFileHolder> perScanFromFileHolderList = new ArrayList<>();
				
				//  Process single scans in index
				PerScanFromFileHolder perScanFromFileHolder = null;
				do {
					perScanFromFileHolder =
							readSingleIndexScanEntry( 
									dataInputStream_IndexFile,
									parentScanNumberOffsetType );

					if ( perScanFromFileHolder != null ) { // null returned if at eof.  Assumes that single scans go to end of file
						perScanFromFileHolderList.add( perScanFromFileHolder );
					}
					
				} while ( perScanFromFileHolder != null );
				
				
				if ( indexScanLevelGt1List.size() != perScanFromFileHolderList.size() ) {
					String msg = "Number of entries in Index file for scan level > 1 is not same number of entries in this file (partial data for scans where level > 1.  Scan file: " 
							+ spectralScans_Level_Gt_1_PartialFilename;
					log.error( msg );
					throw new SpectralStorageProcessingException(msg);
				}

				List<SpectralFile_ScansLvlGt1Partial_TDFR_SingleScan_V_003> scanDataEntries = new ArrayList<>( perScanFromFileHolderList.size() );
				spectralFile_Index_FileContents.setScanDataEntries( scanDataEntries );

				Iterator<SpectralFile_Index_TDFR_SingleScan_V_004> indexScanLevelGt1Iter = indexScanLevelGt1List.iterator();
				
				for ( PerScanFromFileHolder perScanFromFileHolderItem : perScanFromFileHolderList ) {
					SpectralFile_Index_TDFR_SingleScan_V_004 indexScanLevelGt1Item = indexScanLevelGt1Iter.next();

					SpectralFile_ScansLvlGt1Partial_TDFR_SingleScan_V_003 spectralFile_Index_TDFR_SingleScan = new SpectralFile_ScansLvlGt1Partial_TDFR_SingleScan_V_003();
					scanDataEntries.add( spectralFile_Index_TDFR_SingleScan );
					
					spectralFile_Index_TDFR_SingleScan.setScanNumber( indexScanLevelGt1Item.getScanNumber() );
					
					int parentScanNumber = indexScanLevelGt1Item.getScanNumber() + perScanFromFileHolderItem.parentScanNumberOffset;
					spectralFile_Index_TDFR_SingleScan.setParentScanNumber( parentScanNumber );
					
					spectralFile_Index_TDFR_SingleScan.setPrecursorCharge( perScanFromFileHolderItem.precursorCharge );
					spectralFile_Index_TDFR_SingleScan.setPrecursor_M_Over_Z( perScanFromFileHolderItem.precursor_M_Over_Z );
				}
				
				//  Sort on Scan Number, Comparable implemented on SpectralFile_Index_SingleScan_DTO
				Collections.sort( scanDataEntries );

				
			} catch( Exception e ) {
				log.error( "Error reading from spectralScans_Level_Gt_1_PartialFilename file: " + spectralScans_Level_Gt_1_PartialFilename, e );
				throw e;
			}

			return spectralFile_Index_FileContents;
			

		} catch ( Exception e ) {
			
			log.error( "readIndexFile(...): threw exception for hash_String: " + hash_String, e );
			throw e;
		}
	}
	
	/**
	 * @param dataInputStream_ScansLvlGt1PartialFile
	 * @return
	 * @throws IOException
	 * @throws SpectralStorageProcessingException 
	 */
	private PerScanFromFileHolder readSingleIndexScanEntry( 
			DataInputStream dataInputStream_ScansLvlGt1PartialFile,
			byte parentScanNumberOffsetType

			 ) throws IOException, SpectralStorageProcessingException {


		PerScanFromFileHolder perScanFromFileHolder = new PerScanFromFileHolder();
		
		//  Read parent scan number offset
		
		int parentScanNumberOffset = 0;
		try {

			parentScanNumberOffset = SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.FIRST_SINGLE_SCAN_PARENT_SCAN_NUMBER_NOT_SET;

			if ( parentScanNumberOffsetType == SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.PARENT_SCAN_NUMBER_OFFSET_TYPE_NONE__PARENT_SCAN_NUMBER_OFFSET_ALWAYS_DEFAULT_NEGATIVE_1 ) {
				//  Default is -1 and is not in file per scan. 
				parentScanNumberOffset = SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.PARENT_SCAN_NUMBER_OFFSET_DEFAULT_NEGATIVE_1;
				
			} else if ( parentScanNumberOffsetType == SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.PARENT_SCAN_NUMBER_OFFSET_TYPE_INT ) {
				parentScanNumberOffset = dataInputStream_ScansLvlGt1PartialFile.readInt();
			} else if ( parentScanNumberOffsetType == SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.PARENT_SCAN_NUMBER_OFFSET_TYPE_SHORT ) {
				parentScanNumberOffset = dataInputStream_ScansLvlGt1PartialFile.readShort();
			} else if ( parentScanNumberOffsetType == SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.PARENT_SCAN_NUMBER_OFFSET_TYPE_BYTE ) {
				parentScanNumberOffset = dataInputStream_ScansLvlGt1PartialFile.readByte();
			} else {
				String msg = "Unknown value for parentScanNumberOffsetType: " + parentScanNumberOffsetType;
				log.error( msg );
				throw new SpectralStorageProcessingException( msg );
			}

		} catch( EOFException eofException ) {
			//  Assume that normal end of file reached
			
			//  null returned if at eof.  Assumes that single scans go to end of file
			
			return null;  //  EARLY RETURN
		}

		perScanFromFileHolder.parentScanNumberOffset = parentScanNumberOffset;
		
		//  read precursor charge
		
		//  Also need to check for EOF on read precursorCharge since in some conditions the parent scan number offset is not read

		try {
			perScanFromFileHolder.precursorCharge = dataInputStream_ScansLvlGt1PartialFile.readByte();
			
		} catch( EOFException eofException ) {

			if ( parentScanNumberOffsetType == SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants.PARENT_SCAN_NUMBER_OFFSET_TYPE_NONE__PARENT_SCAN_NUMBER_OFFSET_ALWAYS_DEFAULT_NEGATIVE_1 ) {
				//  Assume that normal end of file reached

				//  null returned if at eof.  Assumes that single scans go to end of file

				return null;  //  EARLY RETURN
			}
		}
		
		//  read precursor M/Z

		perScanFromFileHolder.precursor_M_Over_Z = dataInputStream_ScansLvlGt1PartialFile.readDouble();

		return perScanFromFileHolder;
		
	}
	
	/**
	 * 
	 *
	 */
	private static class PerScanFromFileHolder {
		
		int parentScanNumberOffset;
		byte precursorCharge;
		double precursor_M_Over_Z;
	}
	
	/**
	 * @param hash_String
	 * @return
	 * @throws Exception
	 */
	private List<SpectralFile_Index_TDFR_SingleScan_V_004> get_indexScanEntries_OnlyLevelGt1InIndexFileOrder( String hash_String ) throws Exception {
				
		//  First get Index File Data

		SpectralFile_Index_FileContents_Root_IF spectralFile_Index_FileContents_Root_IF =
				IndexFileRootDataObjectCache.getSingletonInstance().getSpectralFile_Index_FileContents_Root_IF( hash_String );

		if ( spectralFile_Index_FileContents_Root_IF == null ) {
			String msg = "Failed to read index file for hash: " + hash_String;
			log.error(msg);
			throw new SpectralStorageProcessingException(msg);
		}
		
		SpectralFile_Index_TDFR_FileContents_Root_V_004 spectralFile_Index_FileContents_Root = null;
		try {
			spectralFile_Index_FileContents_Root = (SpectralFile_Index_TDFR_FileContents_Root_V_004) spectralFile_Index_FileContents_Root_IF;
		} catch ( Exception e ) {
			String msg = "Failed to cast index file data object to correct class for hash: " + hash_String
					+ ", index object .getVersion(): "
					+ spectralFile_Index_FileContents_Root_IF.getVersion() 
					+ ", index object class name: "
					+ spectralFile_Index_FileContents_Root_IF.getClass().getName();
			log.error( msg, e );
			throw new SpectralStorageProcessingException( msg, e );
		}

		List<SpectralFile_Index_TDFR_SingleScan_V_004> indexScanEntriesAll = spectralFile_Index_FileContents_Root.getIndexScanEntries();

		List<SpectralFile_Index_TDFR_SingleScan_V_004> indexScanEntriesLevelGt1 = new ArrayList<>( indexScanEntriesAll.size() );
		
		for ( SpectralFile_Index_TDFR_SingleScan_V_004 item : indexScanEntriesAll ) {
			if ( item.getLevel() > 1 ) {
				indexScanEntriesLevelGt1.add( item );
			}
		}

		//  Sort on index set in order that they were read from index file
		Collections.sort( indexScanEntriesLevelGt1, new Comparator<SpectralFile_Index_TDFR_SingleScan_V_004>() {
			@Override
			public int compare(SpectralFile_Index_TDFR_SingleScan_V_004 o1,
					SpectralFile_Index_TDFR_SingleScan_V_004 o2) {
				if ( o1.getIndexFile_IndexPosition() < o2.getIndexFile_IndexPosition() ) {
					return -1;
				}
				if ( o1.getIndexFile_IndexPosition() > o2.getIndexFile_IndexPosition() ) {
					return 1;
				}
				return 0;
			}
		});

		return indexScanEntriesLevelGt1;
	}

}
