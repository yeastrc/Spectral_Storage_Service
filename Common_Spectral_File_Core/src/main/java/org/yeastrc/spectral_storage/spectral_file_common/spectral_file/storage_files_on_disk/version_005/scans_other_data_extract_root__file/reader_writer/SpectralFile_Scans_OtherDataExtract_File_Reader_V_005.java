package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.scans_other_data_extract_root__file.reader_writer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.DataOrIndexFileFullyWrittenConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.IonInjectionTime_NotAvailable_OnDiskValue_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralFileDataFileNotFullyWrittenException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.index_file.SpectralFile_Index_FileContents_Root_IF;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.scans_other_data_extract.SpectralFile_Scans_OtherDataExtract_FileContents_Root_IF;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.index_file_root_data_object_cache.IndexFileRootDataObjectCache;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.CreateSpectralStorageFilenames;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.StorageFile_Version_005_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.to_data_file_reader_objects.SpectralFile_Index_TDFR_FileContents_Root_V_005;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.to_data_file_reader_objects.SpectralFile_Index_TDFR_SingleScan_V_005;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.scans_other_data_extract_root__file.constants.SpectralFile_Scans_OtherDataExtract_Header_V_005__Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.scans_other_data_extract_root__file.to_data_file_reader_objects.SpectralFile_Scans_OtherDataExtract_TDFR_FileContents_Root_V_005;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.scans_other_data_extract_root__file.to_data_file_reader_objects.SpectralFile_Scans_OtherDataExtract_TDFR_SingleScan_V_005;

/**
 * 
 *
 */
public class SpectralFile_Scans_OtherDataExtract_File_Reader_V_005 implements SpectralFile_Scans_OtherDataExtract_FileContents_Root_IF {

	private static final short FILE_VERSION = StorageFile_Version_005_Constants.FILE_VERSION;
	
	
	private static final int BUFFERED_READER_BUFFER_SIZE = 1 * 1024 * 1024; // 1MB

	private static final Logger log = LoggerFactory.getLogger(SpectralFile_Scans_OtherDataExtract_File_Reader_V_005.class);
	
	/**
	 * private constructor
	 */
	private SpectralFile_Scans_OtherDataExtract_File_Reader_V_005(){}
	public static SpectralFile_Scans_OtherDataExtract_File_Reader_V_005 getInstance( ) throws Exception {
		SpectralFile_Scans_OtherDataExtract_File_Reader_V_005 instance = new SpectralFile_Scans_OtherDataExtract_File_Reader_V_005();
		return instance;
	}
	
	/**
	 * @param hash_String
	 * @param commonReader_File_And_S3
	 * @return
	 * @throws Exception
	 */
	/**
	 * @param hash_String
	 * @param commonReader_File_And_S3
	 * @return
	 * @throws Exception
	 */
	public SpectralFile_Scans_OtherDataExtract_TDFR_FileContents_Root_V_005 readScans_OtherExtractData_File( 
			String hash_String, 
			CommonReader_File_And_S3 commonReader_File_And_S3 ) throws Exception {

		SpectralFile_Scans_OtherDataExtract_TDFR_FileContents_Root_V_005 spectralFile_OtherDataExtract_FileContents = new SpectralFile_Scans_OtherDataExtract_TDFR_FileContents_Root_V_005();

		try {
			List<SpectralFile_Index_TDFR_SingleScan_V_005> indexScanList =
					get_indexScanEntries_All_InIndexFileOrder( hash_String );
			
			//  Next read file for this data
			
			String spectralScans_OtherExtractData_Filename =
					CreateSpectralStorageFilenames.getInstance().
					createSpectraStorage_Scans_OtherExtractData_Filename( hash_String );
			
			try ( DataInputStream dataInputStream_ForFile = 
					new DataInputStream( 
							new BufferedInputStream( 
									commonReader_File_And_S3.getInputStreamForScanStorageItem( 
											spectralScans_OtherExtractData_Filename, 
											hash_String ), 
									BUFFERED_READER_BUFFER_SIZE ) )
					) {
				
				/////////////////
				
				//    Read Header Data:

				short fileVersionInFile = dataInputStream_ForFile.readShort();

				if ( fileVersionInFile != FILE_VERSION ) {
					String msg = "File version does not match programatic version.  File Version: " + fileVersionInFile
							+ ", programatic version: " + FILE_VERSION
							+ ".  spectralScans_OtherExtractDataFilename: " + spectralScans_OtherExtractData_Filename;
					log.error( msg );
					throw new SpectralStorageProcessingException( msg );
				}

				byte fileFullWrittenIndicator = dataInputStream_ForFile.readByte();

				if ( fileFullWrittenIndicator != DataOrIndexFileFullyWrittenConstants.FILE_FULLY_WRITTEN_YES ) {
					
					String msg = "Index File not fully written.  First byte is not 1.  spectralScans_OtherExtractDataFilename: " + spectralScans_OtherExtractData_Filename;
					log.error( msg );
					throw new SpectralFileDataFileNotFullyWrittenException(msg);
				}
				

				spectralFile_OtherDataExtract_FileContents.setVersion( fileVersionInFile );
				
				
				//  Only whether to read the Ion Injection Time from the file.  Need to test the value to tell if it is to be returned.
				byte ionInjectionTime_DataField_Written = dataInputStream_ForFile.readByte();
				
				boolean ionInjectionTime_DataField_Written_Boolean = false;
				
				if ( ionInjectionTime_DataField_Written == SpectralFile_Scans_OtherDataExtract_Header_V_005__Constants.ionInjectionTime_DataField_Written_YES ) {
					
					ionInjectionTime_DataField_Written_Boolean = true;
				}
				
				/////////////////////
				
				//   Read Per Scan Data:
				

				List<PerScanFromFileHolder> perScanFromFileHolderList = new ArrayList<>();
				
				//  Process single scans in index
				PerScanFromFileHolder perScanFromFileHolder = null;
				do {
					perScanFromFileHolder =
							readSingleScanEntry( 
									dataInputStream_ForFile,
									ionInjectionTime_DataField_Written_Boolean );

					if ( perScanFromFileHolder != null ) { // null returned if at eof.  Assumes that single scans go to end of file
						perScanFromFileHolderList.add( perScanFromFileHolder );
					}
					
				} while ( perScanFromFileHolder != null );
				
				
				if ( indexScanList.size() != perScanFromFileHolderList.size() ) {
					String msg = "Number of entries in Index file for scan level > 1 is not same number of entries in this file (partial data for scans where level > 1.  Scan file: " 
							+ spectralScans_OtherExtractData_Filename;
					log.error( msg );
					throw new SpectralStorageProcessingException(msg);
				}

				List<SpectralFile_Scans_OtherDataExtract_TDFR_SingleScan_V_005> scanDataEntries = new ArrayList<>( perScanFromFileHolderList.size() );
				spectralFile_OtherDataExtract_FileContents.setScanDataEntries( scanDataEntries );

				Iterator<SpectralFile_Index_TDFR_SingleScan_V_005> indexScanIter = indexScanList.iterator();
				
				for ( PerScanFromFileHolder perScanFromFileHolderItem : perScanFromFileHolderList ) {
					
					SpectralFile_Index_TDFR_SingleScan_V_005 indexScanItem = indexScanIter.next();

					SpectralFile_Scans_OtherDataExtract_TDFR_SingleScan_V_005 spectralFile_OtherDataExtract_TDFR_SingleScan = new SpectralFile_Scans_OtherDataExtract_TDFR_SingleScan_V_005();
					scanDataEntries.add( spectralFile_OtherDataExtract_TDFR_SingleScan );
					
					spectralFile_OtherDataExtract_TDFR_SingleScan.setScanNumber( indexScanItem.getScanNumber() );
					
					spectralFile_OtherDataExtract_TDFR_SingleScan.setIsCentroid( perScanFromFileHolderItem.isCentroid );
					spectralFile_OtherDataExtract_TDFR_SingleScan.setTotalIonCurrent( perScanFromFileHolderItem.totalIonCurrent );
					spectralFile_OtherDataExtract_TDFR_SingleScan.setIonInjectionTime( perScanFromFileHolderItem.ionInjectionTime );
				}
				
				//  Sort on Scan Number, Comparable implemented on SpectralFile_Index_SingleScan_DTO
				Collections.sort( scanDataEntries );

				
			} catch( Exception e ) {
				log.error( "Error reading from spectralScans_OtherExtractDataFilename file: " + spectralScans_OtherExtractData_Filename, e );
				throw e;
			}

			return spectralFile_OtherDataExtract_FileContents;
			

		} catch ( Exception e ) {
			
			log.error( "readIndexFile(...): threw exception for hash_String: " + hash_String, e );
			throw e;
		}
	}
	
	/**
	 * @param dataInputStream_ForFile
	 * @return
	 * @throws IOException
	 * @throws SpectralStorageProcessingException 
	 */
	private PerScanFromFileHolder readSingleScanEntry( 
			
			DataInputStream dataInputStream_ForFile,

			//  Only whether to read the Ion Injection Time from the file.  Need to test the value to tell if it is to be returned.
			boolean ionInjectionTime_DataField_Written_Boolean

			 ) throws IOException, SpectralStorageProcessingException {


		PerScanFromFileHolder perScanFromFileHolder = new PerScanFromFileHolder();
		
		//  Read is centroid
		
		try {
			perScanFromFileHolder.isCentroid = dataInputStream_ForFile.readByte();

		} catch( EOFException eofException ) {
			//  Assume that normal end of file reached
			
			//  null returned if at eof.  Assumes that single scans go to end of file
			
			return null;  //  EARLY RETURN
		}
		
		perScanFromFileHolder.totalIonCurrent = dataInputStream_ForFile.readFloat();

		//  read Ion Injection Time, if written

		if ( ionInjectionTime_DataField_Written_Boolean ) {

			float ionInjectionTime_InFile = dataInputStream_ForFile.readFloat();
			
			if ( ionInjectionTime_InFile != IonInjectionTime_NotAvailable_OnDiskValue_Constants.ION_INJECTION_TIME_NOT_AVAILABLE_ON_DISK_VALUE ) {
			
				perScanFromFileHolder.ionInjectionTime = ionInjectionTime_InFile;
			}
		}
		
		return perScanFromFileHolder;
		
	}
	
	/**
	 * 
	 *
	 */
	private static class PerScanFromFileHolder {
		
		private byte isCentroid;
		private float totalIonCurrent;
		private Float ionInjectionTime;
	}
	
	/**
	 * @param hash_String
	 * @return
	 * @throws Exception
	 */
	private List<SpectralFile_Index_TDFR_SingleScan_V_005> get_indexScanEntries_All_InIndexFileOrder( String hash_String ) throws Exception {
				
		//  First get Index File Data

		SpectralFile_Index_FileContents_Root_IF spectralFile_Index_FileContents_Root_IF =
				IndexFileRootDataObjectCache.getSingletonInstance().getSpectralFile_Index_FileContents_Root_IF( hash_String, StorageFile_Version_005_Constants.FILE_VERSION );

		if ( spectralFile_Index_FileContents_Root_IF == null ) {
			String msg = "Failed to read index file for hash: " + hash_String;
			log.error(msg);
			throw new SpectralStorageProcessingException(msg);
		}
		
		SpectralFile_Index_TDFR_FileContents_Root_V_005 spectralFile_Index_FileContents_Root = null;
		try {
			spectralFile_Index_FileContents_Root = (SpectralFile_Index_TDFR_FileContents_Root_V_005) spectralFile_Index_FileContents_Root_IF;
		} catch ( Exception e ) {
			String msg = "Failed to cast index file data object to correct class for hash: " + hash_String
					+ ", index object .getVersion(): "
					+ spectralFile_Index_FileContents_Root_IF.getVersion() 
					+ ", index object class name: "
					+ spectralFile_Index_FileContents_Root_IF.getClass().getName();
			log.error( msg, e );
			throw new SpectralStorageProcessingException( msg, e );
		}

		List<SpectralFile_Index_TDFR_SingleScan_V_005> indexScanEntriesAll = spectralFile_Index_FileContents_Root.getIndexScanEntries();

		//  Sort on index set in order that they were read from index file
//		Collections.sort( indexScanEntriesAll, new Comparator<SpectralFile_Index_TDFR_SingleScan_V_005>() {
//			@Override
//			public int compare(SpectralFile_Index_TDFR_SingleScan_V_005 o1,
//					SpectralFile_Index_TDFR_SingleScan_V_005 o2) {
//				if ( o1.getIndexFile_IndexPosition() < o2.getIndexFile_IndexPosition() ) {
//					return -1;
//				}
//				if ( o1.getIndexFile_IndexPosition() > o2.getIndexFile_IndexPosition() ) {
//					return 1;
//				}
//				return 0;
//			}
//		});

		return indexScanEntriesAll;
	}

}
