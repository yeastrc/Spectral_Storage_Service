package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.data_file.reader_writer;

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
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.ByteValuesFor_Boolean_TrueFalse_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.DataOrIndexFileFullyWrittenConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.IonInjectionTime_NotAvailable_OnDiskValue_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.SpectralStorage_Filename_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.file_contents_hash_processing.Compute_File_Hashes;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.file_contents_hash_processing.Compute_Hashes;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.file_contents_hash_processing.Compute_Hashes.Compute_Hashes_Result;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_rt_mz_binned.Accumulate_RT_MZ_Binned_ScanLevel_1;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_rt_mz_binned.ScanLevel_1_RT_MZ_Binned_WriteFile;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_rt_mz_binned.ScanLevel_1_RT_MZ_Binned_WriteFile_JSON_GZIP_NoIntensities;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_CloseWriter_Data_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_Header_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScan_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Writer__IF;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.CreateSpectralStorageFilenames;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.from_data_file_writer_objects.SpectralFile_Index_FDFW_FileContents_Root_V_005;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.from_data_file_writer_objects.SpectralFile_Index_FDFW_SingleScan_V_005;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.reader_writer.SpectralFile_Index_File_Writer_V_005;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.StorageFile_Version_005_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.data_file.reader_writer.SpectralFile_Writer__EncodeScanPeaksToByteArray_GZIP_V_005.SpectralFile_Writer__EncodeScanPeaksToByteArray_GZIP_V_005__MethodResult;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.scans_lvl_gt_1_partial.reader_writer.SpectralFile_ScansLvlGt1Partial_File_Writer_V_005;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.scans_other_data_extract_root__file.reader_writer.SpectralFile_Scans_OtherDataExtract_File_Writer_V_005;

/**
 * V 005
 * 
 * Uses matching SpectralFile_Index_File_Writer_V_005
 * 
 * Uses GZIPOutputStream for compression
 *
 */
public class SpectralFile_Writer_GZIP_V_005 implements SpectralFile_Writer__IF  {

	private static final Logger log = LoggerFactory.getLogger(SpectralFile_Writer_GZIP_V_005.class);
	
	private static final short FILE_VERSION = StorageFile_Version_005_Constants.FILE_VERSION;
	
	private static final int TEMP_OUTPUT_STREAM_INITIAL_SIZE = 100 * 1024;

	private static final String FILE_MODE_READ_WRITE = "rw"; // Used in RandomAccessFile constructor below

	private static final int FILE_WRITE_BUFFER_SIZE = 4096 * 8;

	
	private enum HeaderInitialWriteUpdateAfterClose { FIRST_WRITE, UPDATE_AFTER_CLOSE }
	
	
	/**
	 * private constructor
	 */
	private SpectralFile_Writer_GZIP_V_005(){}
	public static SpectralFile_Writer__IF getInstance( ) throws Exception {
		SpectralFile_Writer__IF instance = new SpectralFile_Writer_GZIP_V_005();
		return instance;
	}
	
	private boolean closeCalled;
	private boolean openCalled;
	private boolean writeHeaderCalled;
	private boolean writeScanCalled;
	
	private Set<Integer> scanNumbersInFile = new HashSet<>();
	
	private String hash_String;
	private File subDirForStorageFiles;
	private SpectralFile_Header_Common spectralFile_Header_Common;
	
	
	private String spectralDataFilename;
	
	private File outputFile_MainDataFileWhileWriting;
	
	private File outputFile_MainDataFileFinal;
	
	private BufferedOutputStream outputStream_MainDataFileWhileWriting;
	
	private ByteArrayOutputStream tempOutputStream = new ByteArrayOutputStream( TEMP_OUTPUT_STREAM_INITIAL_SIZE );

	private long totalBytesForAllSingleScans = 0;
	
	private long nextScanIndex_InBytes = 0;
	
	private long scanPeaksTotalBytes = 0;
	private long scanPeaksTotalCount = 0;
	
	private List<SpectralFile_Index_FDFW_SingleScan_V_005> indexScanEntries = new ArrayList<>();
	
	private Set<Byte> isCentroidUniqueValuesInScans = new HashSet<>();
	
	/**
	 * Accumulate statistics
	 */
	AccumulateSummaryDataPerScanLevel accumulateSummaryDataPerScanLevel;
	
	/**
	 * Accumulate scan level 1 data binned for RT and MZ
	 */
	Accumulate_RT_MZ_Binned_ScanLevel_1 accumulate_RT_MZ_Binned_ScanLevel_1;
	

	
	/* (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Writer__IF#getVersion()
	 */
	@Override
	public int getVersion() {

		return StorageFile_Version_005_Constants.FILE_VERSION;
	}
	
	/* 
	 * Close Main Data File
	 * 
	 * Write Index file and other files
	 * 
	 * 
	 * (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.writer.SpectralFile_Writer__IF#close()
	 */
	@Override
	public void close(SpectralFile_CloseWriter_Data_Common spectralFile_CloseWriter_Data_Common) throws Exception {

		if ( closeCalled ) {
			String msg = "In Writer, close(...) cannot be called more than once";
			log.error(msg);
			throw new SpectralStorageProcessingException(msg);
		}
		
		if ( outputStream_MainDataFileWhileWriting != null ) {
			
			outputStream_MainDataFileWhileWriting.close();
			
			
			
			if ( ! spectralFile_CloseWriter_Data_Common.isExceptionEncounteredProcessingScanFile() ) {

				
				updateDataFileAfterFullCloseMainWriter(spectralFile_CloseWriter_Data_Common);


				//  Rename data file to final filename:

				if ( ! outputFile_MainDataFileWhileWriting.renameTo( outputFile_MainDataFileFinal ) ) {
					log.error( "Error renaming data file to final filename. Renaming from: "
							+ outputFile_MainDataFileWhileWriting.getAbsolutePath() 
							+ ", renaming to: "
							+ outputFile_MainDataFileFinal.getAbsolutePath() );
				}

				//  Output File with spectralDataFilename suffix with version of Data File Format

				{
					String spectralDataFilename_With_FormatSuffixString = 
							CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Data_Filename_FileFormatVersion_Suffix( hash_String, FILE_VERSION );

					File spectralDataFilename_With_FormatSuffixFile = new File( subDirForStorageFiles, spectralDataFilename_With_FormatSuffixString );

					try ( BufferedWriter writer = new BufferedWriter( new FileWriter(spectralDataFilename_With_FormatSuffixFile)) ) {

						writer.write( String.valueOf( FILE_VERSION ) );
					}
				}


				//  Output the .index file and .scnlvlgt1p file

				{
					SpectralFile_Index_FDFW_FileContents_Root_V_005 spectralFile_Index_FDFW_FileContents_Root = new SpectralFile_Index_FDFW_FileContents_Root_V_005();

					spectralFile_Index_FDFW_FileContents_Root.setTotalBytesForAllSingleScans( totalBytesForAllSingleScans );

					spectralFile_Index_FDFW_FileContents_Root.setIndexScanEntries( indexScanEntries );

					SpectralFile_Index_File_Writer_V_005.getInstance().writeIndexFile( 
							hash_String, 
							subDirForStorageFiles, 
							spectralFile_Index_FDFW_FileContents_Root,
							spectralFile_Header_Common,
							spectralFile_CloseWriter_Data_Common,
							accumulateSummaryDataPerScanLevel );

					SpectralFile_ScansLvlGt1Partial_File_Writer_V_005.getInstance().write_ScansLvlGt1Partial_File( hash_String, subDirForStorageFiles, spectralFile_Index_FDFW_FileContents_Root );

					SpectralFile_Scans_OtherDataExtract_File_Writer_V_005.getInstance().write_Scans_OtherDataExtract_File( hash_String, subDirForStorageFiles, spectralFile_Index_FDFW_FileContents_Root );
				}

				//  Write data in accumulate_RT_MZ_Binned_ScanLevel_1 to file as GZIP JSON
				ScanLevel_1_RT_MZ_Binned_WriteFile.getInstance()
				.writeScanLevel_1_RT_MZ_Binned_File( 
						accumulate_RT_MZ_Binned_ScanLevel_1,
						hash_String, 
						subDirForStorageFiles );

				//  Write data in accumulate_RT_MZ_Binned_ScanLevel_1 to file as JSON GZIP - No Summed Intensities
				ScanLevel_1_RT_MZ_Binned_WriteFile_JSON_GZIP_NoIntensities.getInstance()
				.writeScanLevel_1_RT_MZ_Binned_File_JSON_GZIP_NoIntensities(
						accumulate_RT_MZ_Binned_ScanLevel_1, hash_String, subDirForStorageFiles);

				//  Create Files Complete file as last step
				{
					createFilesCompleteFile();
				}

				//  Remove Files Started file that was created in first step
				{
					String dataIndexSpectralFilesStartedFilename =
							CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Data_Index_Files_Started_Filename( hash_String );

					File dataIndexSpectralFilesStartedFile = new File( subDirForStorageFiles, dataIndexSpectralFilesStartedFilename );

					if ( ! dataIndexSpectralFilesStartedFile.delete() ) {
						String msg = "Failed to delete " + dataIndexSpectralFilesStartedFile.getAbsolutePath();
						log.error( msg );
						throw new SpectralStorageProcessingException(msg);
					}
				}
			}
		}
		
		outputStream_MainDataFileWhileWriting = null;

		closeCalled = true;

	}
	
	
	/* (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.writer.SpectralFile_Writer__IF#open(org.yeastrc.spectral_storage.spectral_file_common.spectral_file.dto.SpectralFile_File)
	 */
	@Override
	public void open( String hash_String, File subDirForStorageFiles, SpectralFile_Header_Common spectralFile_Header_Common ) throws Exception {
		
		if ( openCalled ) {
			String msg = "In Writer, open(...) cannot be called more than once";
			log.error(msg);
			throw new SpectralStorageProcessingException(msg);
		}
		
		if ( spectralFile_Header_Common.getTotalIonCurrent_ForEachScan_ComputedFromScanPeaks() != null ) {
			String msg = "In Writer, cannot be not null: spectralFile_Header_Common.getTotalIonCurrent_ForEachScan_ComputedFromScanPeaks()";
			log.error(msg);
			throw new SpectralStorageProcessingException(msg);
		}
		if ( spectralFile_Header_Common.getIonInjectionTime_NotPopulated() != null ) {
			String msg = "In Writer, cannot be not null: spectralFile_Header_Common.getIonInjectionTime_NotPopulated()";
			log.error(msg);
			throw new SpectralStorageProcessingException(msg);
		}

		this.hash_String = hash_String;
		this.subDirForStorageFiles = subDirForStorageFiles;
		this.spectralFile_Header_Common = spectralFile_Header_Common;
		
		accumulateSummaryDataPerScanLevel = AccumulateSummaryDataPerScanLevel.getInstance();
		
		accumulate_RT_MZ_Binned_ScanLevel_1 = Accumulate_RT_MZ_Binned_ScanLevel_1.getInstance();
		
		//  Create Files Started file as first step
		{
			String dataIndexSpectralFilesStartedFilename =
					CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Data_Index_Files_Started_Filename( hash_String );

			File dataIndexSpectralFilesStartedFile = new File( subDirForStorageFiles, dataIndexSpectralFilesStartedFilename );

			dataIndexSpectralFilesStartedFile.createNewFile();
		}
		
		spectralDataFilename =
				CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Data_Filename( hash_String );

		String outputSpectralDataFilenameWhileWriting =
				spectralDataFilename
				+ SpectralStorage_Filename_Constants.IN_PROGRESS_FILENAME_SUFFIX_SUFFIX;

		File dataFileWhileWriting = new File( subDirForStorageFiles, outputSpectralDataFilenameWhileWriting );
		
		File dataFileFinal = new File( subDirForStorageFiles, spectralDataFilename );
		
		outputFile_MainDataFileWhileWriting = dataFileWhileWriting;
		
		outputFile_MainDataFileFinal = dataFileFinal;
		
		outputStream_MainDataFileWhileWriting = new BufferedOutputStream( new FileOutputStream( outputFile_MainDataFileWhileWriting ), FILE_WRITE_BUFFER_SIZE );
		
		writeHeader();
		
		openCalled = true;

	}
	

	/* (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.writer.SpectralFile_Writer__IF#writeHeader(org.yeastrc.spectral_storage.spectral_file_common.spectral_file.dto.SpectralFile_Header)
	 */
	private void writeHeader() throws Exception {

		byte[] headerBytes = getHeaderBytes( null /* spectralFile_CloseWriter_Data_Common */, HeaderInitialWriteUpdateAfterClose.FIRST_WRITE );
		
		outputStream_MainDataFileWhileWriting.write( headerBytes );
		
		outputStream_MainDataFileWhileWriting.flush();
		

		long numBytesWritten = headerBytes.length;
		
		nextScanIndex_InBytes = numBytesWritten;
		
		writeHeaderCalled = true;
	}
	
	/**
	 * @throws Exception
	 */
	private void updateDataFileAfterFullCloseMainWriter(SpectralFile_CloseWriter_Data_Common spectralFile_CloseWriter_Data_Common) throws Exception {

		//  Write to start of file after closing file
		
		//  Get Header Bytes that will overlay existing header with updates
		
		byte[] headerBytes = getHeaderBytes( spectralFile_CloseWriter_Data_Common, HeaderInitialWriteUpdateAfterClose.UPDATE_AFTER_CLOSE );

		try ( RandomAccessFile spectalFile = new RandomAccessFile( outputFile_MainDataFileWhileWriting, FILE_MODE_READ_WRITE ) ) {
		
//			spectalFile.getFilePointer();
//			spectalFile.length();
			
			spectalFile.seek( 0 );  // Set to start of file
			
			spectalFile.write( headerBytes );
		}
				
	}

	
	/**
	 * @param headerInitialWriteUpdateAfterClose
	 * @return
	 * @throws Exception 
	 */
	private byte[] getHeaderBytes( SpectralFile_CloseWriter_Data_Common spectralFile_CloseWriter_Data_Common, HeaderInitialWriteUpdateAfterClose headerInitialWriteUpdateAfterClose ) throws Exception {

		short fileVersion = FILE_VERSION;
		byte fileFullyWrittenValue = DataOrIndexFileFullyWrittenConstants.FILE_FULLY_WRITTEN_NO;
		long dataFileSize = 0;
		
		if ( headerInitialWriteUpdateAfterClose
				== HeaderInitialWriteUpdateAfterClose.UPDATE_AFTER_CLOSE ) {
			
			fileFullyWrittenValue = DataOrIndexFileFullyWrittenConstants.FILE_FULLY_WRITTEN_YES;
			dataFileSize = outputFile_MainDataFileWhileWriting.length();
		}
		
		
		tempOutputStream.reset();
		
		//  Surround tempOutputStream
		DataOutputStream dataOutputStream = new DataOutputStream( tempOutputStream );

		//  Write Version - ALWAYS FIRST
		
		dataOutputStream.writeShort( fileVersion );
		
		//  Write File Fully Written Indicator - ALWAYS SECOND
		
		dataOutputStream.writeByte( fileFullyWrittenValue );

		//  Write Data File Size (Size of file this is written to) - ALWAYS THIRD
		
		dataOutputStream.writeLong( dataFileSize );

		////////

		//  Get headerMainPart here so can write the length of it before writing it 
		
		byte[] headerMainPart = getMainHeaderContents_ByteArray_Minus_MainHeaderLength(spectralFile_CloseWriter_Data_Common);

		int headerMainPartSize = headerMainPart.length;

		
		//  Write Length of main header data to output file BEFORE writing header data (excludes VERSION and this length)
		
		dataOutputStream.writeShort( headerMainPartSize );


		dataOutputStream.flush();  //  Flush dataOutputStream before write byte[] headerMainPart to tempOutputStream

		tempOutputStream.write( headerMainPart );  //  write byte[] headerMainPart to tempOutputStream
		

		tempOutputStream.close();
		
		byte[] results = tempOutputStream.toByteArray();
		
		return results;
	}
	
	/**
	 * @param header_Common
	 * @return
	 * @throws Exception 
	 */
	private byte[] getMainHeaderContents_ByteArray_Minus_MainHeaderLength(SpectralFile_CloseWriter_Data_Common spectralFile_CloseWriter_Data_Common) throws Exception {

		//////////////////////////////////
		
		//////   !!!!!!!!   Only Add new Items to End of Header
		
		ByteArrayOutputStream local_ByteArrayOutputStream = new ByteArrayOutputStream( 1000000 );

		//  Surround tempOutputStream
		DataOutputStream dataOutputStream = new DataOutputStream( local_ByteArrayOutputStream );

		
		//  Write scan file length
		
		dataOutputStream.writeLong( this.spectralFile_Header_Common.getScanFileLength_InBytes() );
		

		//  Write Main Hash

		//      Write Main hash length and then the bytes
		
		dataOutputStream.writeShort( this.spectralFile_Header_Common.getMainHash().length );
		dataOutputStream.write( this.spectralFile_Header_Common.getMainHash() );

		//  Write Alt Hash SHA512

		//      Write Alt Hash SHA512 length and then the bytes
		
		dataOutputStream.writeShort( this.spectralFile_Header_Common.getAltHashSHA512().length );
		dataOutputStream.write( this.spectralFile_Header_Common.getAltHashSHA512() );

		//  Write Alt Hash SHA1

		//      Write Alt Hash SHA1 length and then the bytes
		
		dataOutputStream.writeShort( this.spectralFile_Header_Common.getAltHashSHA1().length );
		dataOutputStream.write( this.spectralFile_Header_Common.getAltHashSHA1() );
		
		//////////////////////////////////
		
		//////   !!!!!!!!   Only Add new Items to End of Header
		

		
		{ //   Write Flag: totalIonCurrent_ForEachScan_ComputedFromScanPeaks
		
			byte flagValueByte = ByteValuesFor_Boolean_TrueFalse_Constants.BYTE_VALUE_FOR_BOOLEAN_FALSE;
			
			if ( spectralFile_CloseWriter_Data_Common != null ) {

				if ( spectralFile_CloseWriter_Data_Common.getTotalIonCurrent_ForEachScan_ComputedFromScanPeaks() == null ) {
					String msg = "this.spectralFile_Header_Common.getTotalIonCurrent_ForEachScan_ComputedFromScanPeaks() == null";
					log.error(msg);
					throw new SpectralStorageProcessingException(msg);
				}

				if ( spectralFile_CloseWriter_Data_Common.getTotalIonCurrent_ForEachScan_ComputedFromScanPeaks().booleanValue() ) {

					flagValueByte = ByteValuesFor_Boolean_TrueFalse_Constants.BYTE_VALUE_FOR_BOOLEAN_TRUE;
				}
			}
		
			dataOutputStream.writeByte(flagValueByte);
		}

		{ //   Write Flag: ionInjectionTime_NotPopulated
		
			byte flagValueByte = ByteValuesFor_Boolean_TrueFalse_Constants.BYTE_VALUE_FOR_BOOLEAN_FALSE;

			if ( spectralFile_CloseWriter_Data_Common != null ) {

				if ( spectralFile_CloseWriter_Data_Common.getIonInjectionTime_NotPopulated() == null ) {
					String msg = "spectralFile_CloseWriter_Data_Common.getIonInjectionTime_NotPopulated() == null";
					log.error(msg);
					throw new SpectralStorageProcessingException(msg);
				}

				if ( spectralFile_CloseWriter_Data_Common.getIonInjectionTime_NotPopulated().booleanValue() ) {

					flagValueByte = ByteValuesFor_Boolean_TrueFalse_Constants.BYTE_VALUE_FOR_BOOLEAN_TRUE;
				}
			}
		
			dataOutputStream.writeByte(flagValueByte);
		}

		
		
		///////////////////////////////////
		///////////////////////////////////
		
		//   Done writing to dataOutputStream
		
		//   Now get byte[] to return
		
		
		dataOutputStream.flush();

		local_ByteArrayOutputStream.close();
		
		byte[] results = local_ByteArrayOutputStream.toByteArray();
		
		return results;
	}

	
	//   END Create/Write HEADER CODE
	
	
	//////////////////////////
	//////////////////////////
	//////////////////////////
	
	
	/* (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.writer.SpectralFile_Writer__IF#writeScan(org.yeastrc.spectral_storage.spectral_file_common.spectral_file.dto.SpectralFile_SingleScan)
	 */
	@Override
	public void writeScan( SpectralFile_SingleScan_Common spectralFile_SingleScan ) throws Exception {
		
		if ( ! writeHeaderCalled ) {
			String msg = "Must call writeHeader before call writeScan";
			log.error( msg );
			throw new SpectralStorageProcessingException( msg );
		}

		int scanSize_InDataFile_InBytes = writeScanTo_Main_DataFile( spectralFile_SingleScan );
		
		//  Save off all values for other files that are data that also exists in main data file
		
		SpectralFile_Index_FDFW_SingleScan_V_005 spectralFile_Index_SingleScan_DTO = new SpectralFile_Index_FDFW_SingleScan_V_005();

		spectralFile_Index_SingleScan_DTO.setScanNumber( spectralFile_SingleScan.getScanNumber() );
		spectralFile_Index_SingleScan_DTO.setLevel( spectralFile_SingleScan.getLevel() );
		spectralFile_Index_SingleScan_DTO.setRetentionTime( spectralFile_SingleScan.getRetentionTime() );
		spectralFile_Index_SingleScan_DTO.setIsCentroid( spectralFile_SingleScan.getIsCentroid() );
		spectralFile_Index_SingleScan_DTO.setTotalIonCurrent( spectralFile_SingleScan.getTotalIonCurrent() );
		
		spectralFile_Index_SingleScan_DTO.setIonInjectionTime( spectralFile_SingleScan.getIonInjectionTime() );
		
		if ( spectralFile_SingleScan.getLevel() > 1 ) {
			spectralFile_Index_SingleScan_DTO.setParentScanNumber( spectralFile_SingleScan.getParentScanNumber() );
			spectralFile_Index_SingleScan_DTO.setPrecursorCharge( spectralFile_SingleScan.getPrecursorCharge() );
			spectralFile_Index_SingleScan_DTO.setPrecursor_M_Over_Z( spectralFile_SingleScan.getPrecursor_M_Over_Z() );
		}
		
		spectralFile_Index_SingleScan_DTO.setScanSize_InDataFile_InBytes( scanSize_InDataFile_InBytes );
		spectralFile_Index_SingleScan_DTO.setScanIndex_InDataFile_InBytes( nextScanIndex_InBytes );
		
		indexScanEntries.add( spectralFile_Index_SingleScan_DTO );

		//  add amount of bytes written for this scan to nextScanIndex_InBytes

		nextScanIndex_InBytes += scanSize_InDataFile_InBytes;
		
		//  add amount of bytes written for this scan to totalBytesForAllSingleScans
		
		totalBytesForAllSingleScans += scanSize_InDataFile_InBytes;
		
		accumulateSummaryDataPerScanLevel.addScanToAccum( spectralFile_SingleScan );
		
		accumulate_RT_MZ_Binned_ScanLevel_1.processScanForAccum( spectralFile_SingleScan );
		
		writeScanCalled = true;

	}

	
	/**
	 * @param spectralFile_SingleScan
	 * @return
	 * @throws Exception
	 */
	private int writeScanTo_Main_DataFile( SpectralFile_SingleScan_Common spectralFile_SingleScan ) throws Exception {
		
		//  Preprocess Scan
		
		//  Ensure no scan number is duplicated
		
		int scanNumber = spectralFile_SingleScan.getScanNumber();
		
		if ( ( ! scanNumbersInFile.add( scanNumber ) ) ) {
			String msg = "Duplicate scan number in file: " + scanNumber;
			log.error( msg );
			throw new SpectralStorageDataException( msg );
		}

		//  Save off isCentroid value
		
		isCentroidUniqueValuesInScans.add( spectralFile_SingleScan.getIsCentroid() );
		
		//  Get ionInjectionTime For Writing to File
		
		float ionInjectionTime_For_Writing_to_File = IonInjectionTime_NotAvailable_OnDiskValue_Constants.ION_INJECTION_TIME_NOT_AVAILABLE_ON_DISK_VALUE;
		
		if ( spectralFile_SingleScan.getIonInjectionTime() != null ) {

			if ( spectralFile_SingleScan.getIonInjectionTime() == IonInjectionTime_NotAvailable_OnDiskValue_Constants.ION_INJECTION_TIME_NOT_AVAILABLE_ON_DISK_VALUE ) {
				String msg = "IonInjectionTime from scan file is == flag value for NO IonInjectionTime.  Value in spectralFile_SingleScan.getIonInjectionTime() == IonInjectionTime_NotAvailable_OnDiskValue_Constants.ION_INJECTION_TIME_NOT_AVAILABLE_ON_DISK_VALUE which is Float.NEGATIVE_INFINITY";
				log.error( msg );
				throw new SpectralStorageDataException(msg);
			}

			ionInjectionTime_For_Writing_to_File = spectralFile_SingleScan.getIonInjectionTime();
		}

		/////
		
		tempOutputStream.reset();
		
		//  Surround tempOutputStream
		DataOutputStream dataOutputStream = new DataOutputStream( tempOutputStream );
		
		dataOutputStream.writeByte( spectralFile_SingleScan.getLevel() );
		dataOutputStream.writeInt( spectralFile_SingleScan.getScanNumber() );
		dataOutputStream.writeFloat( spectralFile_SingleScan.getRetentionTime() );
		dataOutputStream.writeByte( spectralFile_SingleScan.getIsCentroid() );
		dataOutputStream.writeFloat( spectralFile_SingleScan.getTotalIonCurrent() );
		dataOutputStream.writeFloat( ionInjectionTime_For_Writing_to_File );
		
		if ( spectralFile_SingleScan.getLevel() > 1 ) {

			//  Only applicable where level > 1
			
			dataOutputStream.writeInt( spectralFile_SingleScan.getParentScanNumber() );
			dataOutputStream.writeByte( spectralFile_SingleScan.getPrecursorCharge() );
			dataOutputStream.writeDouble( spectralFile_SingleScan.getPrecursor_M_Over_Z() );
		}
		
		int numberScanPeaks = 0;
		if ( spectralFile_SingleScan.getScanPeaksAsObjectArray() != null ) {
			numberScanPeaks = spectralFile_SingleScan.getScanPeaksAsObjectArray().size();
		}
		
		dataOutputStream.writeInt( numberScanPeaks );
		
		//  Scan Peaks
		
		SpectralFile_Writer__EncodeScanPeaksToByteArray_GZIP_V_005 spectralFile_Writer__EncodeScanPeaksToByteArray_GZIP_V_005 = new SpectralFile_Writer__EncodeScanPeaksToByteArray_GZIP_V_005();

		SpectralFile_Writer__EncodeScanPeaksToByteArray_GZIP_V_005__MethodResult encodePeaksAsCompressedBytes_Result =
				spectralFile_Writer__EncodeScanPeaksToByteArray_GZIP_V_005.encodePeaksAsCompressedBytes( spectralFile_SingleScan.getScanPeaksAsObjectArray() );
		
		byte[] encodedScanPeaks_ByteArray = encodePeaksAsCompressedBytes_Result.getEncodedScanPeaks_ByteArray();

		int scanPeaksAsBAOS_Size = encodedScanPeaks_ByteArray.length;
		
		// Length of compressed scan data
		dataOutputStream.writeInt( scanPeaksAsBAOS_Size );

		dataOutputStream.flush();
		

		tempOutputStream.writeTo( outputStream_MainDataFileWhileWriting );
		
		outputStream_MainDataFileWhileWriting.write( encodedScanPeaks_ByteArray );
			
		
		outputStream_MainDataFileWhileWriting.flush();
		
		int scanWithoutPeaksSize = tempOutputStream.size();
		
		
		int numBytesWritten = scanWithoutPeaksSize + scanPeaksAsBAOS_Size;
		
		return numBytesWritten;
		
//		private byte level;
//		private int scanNumber;
//		private float retentionTime;
//		private byte isCentroid;
//		private float ionInjectionTime
//		
//		//  Only applicable where level > 1
//		
//		private int parentScanNumber;
//		private byte precursorCharge;
//		private float precursor_M_Over_Z;
//		
//		/**
//		 * Length of scan Peaks which is written immediately after the data in this class.
//		 */
//		private int scanPeaksDataLength;

	}
	

	/**
	 * Create Files Complete file as last step
	 * @throws Exception
	 */
	private void createFilesCompleteFile(  ) throws Exception {
		
		String dataIndexSpectralFilesCompleteFilename =
				CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Data_Index_Files_Complete_Filename( hash_String );

		File dataIndexSpectralFilesCompleteFile = new File( subDirForStorageFiles, dataIndexSpectralFilesCompleteFilename );

		//  Compute hashes on just created Spectral Storage Data File
		Compute_Hashes compute_Hashes =
				Compute_File_Hashes.getInstance().compute_File_Hashes( outputFile_MainDataFileFinal );
		
		Compute_Hashes_Result compute_Hashes_Result = compute_Hashes.compute_Hashes();
		
		try ( BufferedOutputStream filesCompleteOutput = new BufferedOutputStream( new FileOutputStream( dataIndexSpectralFilesCompleteFile) ) ) {

			//  Surround tempOutputStream
			DataOutputStream dataOutputStream = new DataOutputStream( filesCompleteOutput );

			//  Write Main/SHA384 Hash

			//      Write Main hash length and then the bytes

			dataOutputStream.writeShort( compute_Hashes_Result.getSha_384_Hash().length );
			dataOutputStream.write( compute_Hashes_Result.getSha_384_Hash() );

			//  Write Alt Hash SHA512

			//      Write Alt Hash SHA512 length and then the bytes

			dataOutputStream.writeShort( compute_Hashes_Result.getSha_512_Hash().length );
			dataOutputStream.write( compute_Hashes_Result.getSha_512_Hash());

			//  Write Alt Hash SHA1

			//      Write Alt Hash SHA1 length and then the bytes

			dataOutputStream.writeShort( compute_Hashes_Result.getSha_1_Hash().length );
			dataOutputStream.write( compute_Hashes_Result.getSha_1_Hash() );

			dataOutputStream.flush();
			
		} catch (Exception e ) {
			String msg = "Failed to write to Files Complete File: " + dataIndexSpectralFilesCompleteFile.getAbsolutePath();
			log.error( msg, e );
			throw e;
		}

	}
	
	/* (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.writer.SpectralFile_Writer__IF#getScanPeaksTotalBytes()
	 */
	@Override
	public long getScanPeaksTotalBytes() {
		return scanPeaksTotalBytes;
	}
	/* (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.writer.SpectralFile_Writer__IF#getScanPeaksCompressedTotalBytes()
	 */
	@Override
	public long getScanPeaksCompressedTotalBytes() {
		return nextScanIndex_InBytes;
	}
	@Override
	public long getScanPeaksTotalCount() {
		return scanPeaksTotalCount;
	}
	
}
