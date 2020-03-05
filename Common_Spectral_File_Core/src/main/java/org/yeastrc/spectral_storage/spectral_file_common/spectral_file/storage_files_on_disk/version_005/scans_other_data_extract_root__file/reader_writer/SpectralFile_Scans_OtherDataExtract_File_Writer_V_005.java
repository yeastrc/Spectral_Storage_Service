package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.scans_other_data_extract_root__file.reader_writer;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.DataOrIndexFileFullyWrittenConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.IonInjectionTime_NotAvailable_OnDiskValue_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.SpectralStorage_Filename_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.CreateSpectralStorageFilenames;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.StorageFile_Version_005_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.from_data_file_writer_objects.SpectralFile_Index_FDFW_FileContents_Root_V_005;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.from_data_file_writer_objects.SpectralFile_Index_FDFW_SingleScan_V_005;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.scans_other_data_extract_root__file.constants.SpectralFile_Scans_OtherDataExtract_Header_V_005__Constants;

/**
 * Any changes to data written require a NEW VERSION and a change to the corresponding SpectralFile_Scans_OtherDataExtract_File_Reader_V_###
 *
 */
public class SpectralFile_Scans_OtherDataExtract_File_Writer_V_005 {

	private static final Logger log = LoggerFactory.getLogger(SpectralFile_Scans_OtherDataExtract_File_Writer_V_005.class);
	

	private static final short FILE_VERSION = StorageFile_Version_005_Constants.FILE_VERSION;
	

	private static final String FILE_MODE_READ_WRITE = "rw"; // Used in RandomAccessFile constructor below
	
	/**
	 * private constructor
	 */
	private SpectralFile_Scans_OtherDataExtract_File_Writer_V_005(){}
	public static SpectralFile_Scans_OtherDataExtract_File_Writer_V_005 getInstance( ) throws Exception {
		SpectralFile_Scans_OtherDataExtract_File_Writer_V_005 instance = new SpectralFile_Scans_OtherDataExtract_File_Writer_V_005();
		return instance;
	}
	

	/**
	 * @param hash_String
	 * @param subDirForStorageFiles
	 * @param spectralFile_Index_FDFW_FileContents_Root_V_005
	 * @throws Exception
	 */
	public void write_Scans_OtherDataExtract_File( 
			String hash_String,
			File subDirForStorageFiles,
			SpectralFile_Index_FDFW_FileContents_Root_V_005 spectralFile_Index_FDFW_FileContents_Root_V_005 ) throws Exception {

		String outputSpectraStorage_Scans_OtherExtractData_Filename =
				CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Scans_OtherExtractData_Filename( hash_String );
		
		String outputSpectraStorage_Scans_OtherExtractData_FilenameWhileWriting =
				outputSpectraStorage_Scans_OtherExtractData_Filename
				+ SpectralStorage_Filename_Constants.IN_PROGRESS_FILENAME_SUFFIX_SUFFIX;

		File SpectraStorage_Scans_OtherExtractData_FileWhileWriting = new File( subDirForStorageFiles, outputSpectraStorage_Scans_OtherExtractData_FilenameWhileWriting );
		
		File SpectraStorage_Scans_OtherExtractData_FileFinal = new File( subDirForStorageFiles, outputSpectraStorage_Scans_OtherExtractData_Filename );
		
		List<SpectralFile_Index_FDFW_SingleScan_V_005> indexScanEntries = spectralFile_Index_FDFW_FileContents_Root_V_005.getIndexScanEntries();
				
		//  WARNING, do not sort the records here. 
		//   The data in this file will be in the same order as the index
		
		boolean ionInjectionTime_DataField_Written_Boolean = false; // true if write ionInjectionTime to file

		if ( ! indexScanEntries.isEmpty() ) {

			{
				for ( SpectralFile_Index_FDFW_SingleScan_V_005 indexScanEntry : indexScanEntries ) {
					
					if ( indexScanEntry.getIonInjectionTime() != null ) {
						ionInjectionTime_DataField_Written_Boolean = true;
						break;
					}
				}
			}
		}
		

		byte ionInjectionTime_DataField_Written = SpectralFile_Scans_OtherDataExtract_Header_V_005__Constants.ionInjectionTime_DataField_Written_NO;
		if ( ionInjectionTime_DataField_Written_Boolean ) {
			ionInjectionTime_DataField_Written = SpectralFile_Scans_OtherDataExtract_Header_V_005__Constants.ionInjectionTime_DataField_Written_YES;
		}

		try ( DataOutputStream dataOutputStream_ToOutputFile = 
				new DataOutputStream( new BufferedOutputStream( new FileOutputStream( SpectraStorage_Scans_OtherExtractData_FileWhileWriting ) ) )
				) {

			//  Write Version - ALWAYS FIRST

			dataOutputStream_ToOutputFile.writeShort( FILE_VERSION );
			
			//  Write File Fully Written Indicator - ALWAYS SECOND
			
			dataOutputStream_ToOutputFile.writeByte( DataOrIndexFileFullyWrittenConstants.FILE_FULLY_WRITTEN_NO );

			
			dataOutputStream_ToOutputFile.writeByte( ionInjectionTime_DataField_Written );

			for ( SpectralFile_Index_FDFW_SingleScan_V_005 indexScanEntry : indexScanEntries ) {
				
				//  write is centroid
				dataOutputStream_ToOutputFile.writeByte( indexScanEntry.getIsCentroid() );
				
				//  write is single scan total ion current 
				dataOutputStream_ToOutputFile.writeFloat( indexScanEntry.getTotalIonCurrent() );
				
				if ( ionInjectionTime_DataField_Written_Boolean ) {
					
					//  write ion injection time
					
					//  Value in Constant is written if the value passed is null
					
					float ionInjectionTime_OnDisk = IonInjectionTime_NotAvailable_OnDiskValue_Constants.ION_INJECTION_TIME_NOT_AVAILABLE_ON_DISK_VALUE;
					if (indexScanEntry.getIonInjectionTime() != null ) {
						ionInjectionTime_OnDisk = indexScanEntry.getIonInjectionTime();
					}
					dataOutputStream_ToOutputFile.writeFloat( ionInjectionTime_OnDisk );
				}
			}
		} catch( Exception e ) {
			log.error( "Error writing to Other Extract Data file: " + SpectraStorage_Scans_OtherExtractData_FileWhileWriting.getCanonicalPath() );
			throw e;
		} finally {

		}
		
		
		updateFileFullyWrittenIndicatorUpdateAfterFullCloseMainWriter( SpectraStorage_Scans_OtherExtractData_FileWhileWriting, spectralFile_Index_FDFW_FileContents_Root_V_005 );

		//  Rename file to final filename:
		
		if ( ! SpectraStorage_Scans_OtherExtractData_FileWhileWriting.renameTo( SpectraStorage_Scans_OtherExtractData_FileFinal ) ) {
			log.error( "Error renaming Scans Other Extract Data file to final filename. Renaming from: "
					+ SpectraStorage_Scans_OtherExtractData_FileWhileWriting.getAbsolutePath() 
					+ ", renaming to: "
					+ SpectraStorage_Scans_OtherExtractData_FileFinal.getAbsolutePath() );
		}
		
		System.out.println( "***************************************" );
		System.out.println( "Scans Other Extract Data File Writing stats:");
		
		//  report if wrote ion injection time used

		if ( ionInjectionTime_DataField_Written_Boolean ) {

			System.out.println( "Ion Injection Time was YES written to OtherExtract Data File" );
		} else {
			System.out.println( "Ion Injection Time was NOT written to OtherExtract Data File" );
		}
		
		System.out.println( "***************************************" );
		
	}

	/**
	 * @throws Exception
	 */
	private void updateFileFullyWrittenIndicatorUpdateAfterFullCloseMainWriter( 
			File indexFile, 
			SpectralFile_Index_FDFW_FileContents_Root_V_005 spectralFile_Index_FDFW_FileContents_Root_V_005 ) throws Exception {

		ByteArrayOutputStream tempLocalOutputStream = new ByteArrayOutputStream( 20 );
		
		//  Surround tempOutputStream
		DataOutputStream dataOutputStream = new DataOutputStream( tempLocalOutputStream );


		//  Write Version - ALWAYS FIRST

		dataOutputStream.writeShort( FILE_VERSION );
		
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
