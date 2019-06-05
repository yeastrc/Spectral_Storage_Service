package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.data_file.reader_writer;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.accum_scan_summary_data.AccumulateSummaryDataPerScanLevel;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.DataOrIndexFileFullyWrittenConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.SpectralStorage_Filename_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.file_contents_hash_processing.Compute_File_Hashes;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.file_contents_hash_processing.Compute_Hashes;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.file_contents_hash_processing.Compute_Hashes.Compute_Hashes_Result;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_rt_mz_binned.Accumulate_RT_MZ_Binned_ScanLevel_1;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_rt_mz_binned.ScanLevel_1_RT_MZ_Binned_WriteFile;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_rt_mz_binned.ScanLevel_1_RT_MZ_Binned_WriteFile_JSON_GZIP_NoIntensities;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_Header_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScanPeak_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScan_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Writer__IF;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.CreateSpectralStorageFilenames;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.StorageFile_Version_003_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.scans_lvl_gt_1_partial.reader_writer.SpectralFile_ScansLvlGt1Partial_File_Writer_V_003;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_004.index_file.from_data_file_writer_objects.SpectralFile_Index_FDFW_FileContents_Root_V_004;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_004.index_file.from_data_file_writer_objects.SpectralFile_Index_FDFW_SingleScan_V_004;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_004.index_file.reader_writer.SpectralFile_Index_File_Writer_V_004;

/**
 * V 003
 * 
 * Uses matching SpectralFile_Index_File_Writer_V_004
 * 
 * Uses GZIPOutputStream for compression
 *
 */
public class SpectralFile_Writer_GZIP_V_003 implements SpectralFile_Writer__IF  {

	private static final Logger log = LoggerFactory.getLogger(SpectralFile_Writer_GZIP_V_003.class);
	
	private static final short FILE_VERSION = StorageFile_Version_003_Constants.FILE_VERSION;
	
	private static final int TEMP_OUTPUT_STREAM_INITIAL_SIZE = 100 * 1024;

	private static final String FILE_MODE_READ_WRITE = "rw"; // Used in RandomAccessFile constructor below

	private static final int FILE_WRITE_BUFFER_SIZE = 4096 * 8;

	
	private enum HeaderFirstBytesInitialWriteUpdateAfterClose { FIRST_WRITE, UPDATE_AFTER_CLOSE }
	
	
	/**
	 * private constructor
	 */
	private SpectralFile_Writer_GZIP_V_003(){}
	public static SpectralFile_Writer__IF getInstance( ) throws Exception {
		SpectralFile_Writer__IF instance = new SpectralFile_Writer_GZIP_V_003();
		return instance;
	}
	
	private boolean closeCalled;
	private boolean openCalled;
	private boolean writeHeaderCalled;
	private boolean writeScanCalled;
	
	private Set<Integer> scanNumbersInFile = new HashSet<>();
	
	private String hash_String;
	private File subDirForStorageFiles;
	
	private File outputFile_MainDataFileWhileWriting;
	
	private File outputFile_MainDataFileFinal;
	
	private BufferedOutputStream outputStream_MainDataFileWhileWriting;
	
	private ByteArrayOutputStream tempOutputStream = new ByteArrayOutputStream( TEMP_OUTPUT_STREAM_INITIAL_SIZE );
	private ByteArrayOutputStream tempScansCompressedOutputStream = new ByteArrayOutputStream( TEMP_OUTPUT_STREAM_INITIAL_SIZE );

	private long totalBytesForAllSingleScans = 0;
	
	private long nextScanIndex_InBytes = 0;
	
	private long scanPeaksTotalBytes = 0;
	private long scanPeaksTotalCount = 0;
	
	private List<SpectralFile_Index_FDFW_SingleScan_V_004> indexScanEntries = new ArrayList<>();
	
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
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.writer.SpectralFile_Writer__IF#close()
	 */
	@Override
	public void close() throws Exception {
		
		if ( outputStream_MainDataFileWhileWriting != null ) {
			outputStream_MainDataFileWhileWriting.close();
			
			updateDataFileAfterFullCloseMainWriter();
			
			
			//  Rename data file to final filename:
			
			if ( ! outputFile_MainDataFileWhileWriting.renameTo( outputFile_MainDataFileFinal ) ) {
				log.error( "Error renaming data file to final filename. Renaming from: "
						+ outputFile_MainDataFileWhileWriting.getAbsolutePath() 
						+ ", renaming to: "
						+ outputFile_MainDataFileFinal.getAbsolutePath() );
			}
			
			
			//  Output the .index file and .scnlvlgt1p file

			{
				SpectralFile_Index_FDFW_FileContents_Root_V_004 spectralFile_Index_FDFW_FileContents_Root = new SpectralFile_Index_FDFW_FileContents_Root_V_004();

				spectralFile_Index_FDFW_FileContents_Root.setTotalBytesForAllSingleScans( totalBytesForAllSingleScans );
				
				spectralFile_Index_FDFW_FileContents_Root.setIndexScanEntries( indexScanEntries );

				SpectralFile_Index_File_Writer_V_004.getInstance().writeIndexFile( hash_String, subDirForStorageFiles, spectralFile_Index_FDFW_FileContents_Root, accumulateSummaryDataPerScanLevel );
				
				SpectralFile_ScansLvlGt1Partial_File_Writer_V_003.getInstance().write_ScansLvlGt1Partial_File( hash_String, subDirForStorageFiles, spectralFile_Index_FDFW_FileContents_Root );
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
		
		outputStream_MainDataFileWhileWriting = null;

		closeCalled = true;

	}
	
	
	/* (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.writer.SpectralFile_Writer__IF#open(org.yeastrc.spectral_storage.spectral_file_common.spectral_file.dto.SpectralFile_File)
	 */
	@Override
	public void open( String hash_String, File subDirForStorageFiles, SpectralFile_Header_Common spectralFile_Header_Common ) throws Exception {

		this.hash_String = hash_String;
		this.subDirForStorageFiles = subDirForStorageFiles;
		
		accumulateSummaryDataPerScanLevel = AccumulateSummaryDataPerScanLevel.getInstance();
		
		accumulate_RT_MZ_Binned_ScanLevel_1 = Accumulate_RT_MZ_Binned_ScanLevel_1.getInstance();
		
		//  Create Files Started file as first step
		{
			String dataIndexSpectralFilesStartedFilename =
					CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Data_Index_Files_Started_Filename( hash_String );

			File dataIndexSpectralFilesStartedFile = new File( subDirForStorageFiles, dataIndexSpectralFilesStartedFilename );

			dataIndexSpectralFilesStartedFile.createNewFile();
		}
		
		String spectralDataFilename =
				CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Data_Filename( hash_String );

		String outputSpectralDataFilenameWhileWriting =
				spectralDataFilename
				+ SpectralStorage_Filename_Constants.IN_PROGRESS_FILENAME_SUFFIX_SUFFIX;

		File dataFileWhileWriting = new File( subDirForStorageFiles, outputSpectralDataFilenameWhileWriting );
		
		File dataFileFinal = new File( subDirForStorageFiles, spectralDataFilename );
		
		outputFile_MainDataFileWhileWriting = dataFileWhileWriting;
		
		outputFile_MainDataFileFinal = dataFileFinal;
		
		outputStream_MainDataFileWhileWriting = new BufferedOutputStream( new FileOutputStream( outputFile_MainDataFileWhileWriting ), FILE_WRITE_BUFFER_SIZE );
		
		writeHeader( spectralFile_Header_Common );
		
		openCalled = true;

	}
	

	/* (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.writer.SpectralFile_Writer__IF#writeHeader(org.yeastrc.spectral_storage.spectral_file_common.spectral_file.dto.SpectralFile_Header)
	 */
	private void writeHeader( SpectralFile_Header_Common header ) throws Exception {

		byte[] headerFirstBytes = getHeaderFirstBytes( HeaderFirstBytesInitialWriteUpdateAfterClose.FIRST_WRITE );
		
		outputStream_MainDataFileWhileWriting.write( headerFirstBytes );
		
		outputStream_MainDataFileWhileWriting.flush();
		
		int numBytesWrittenOtherThanVersion = writeHeaderMainPart( header );

		long numBytesWritten = headerFirstBytes.length + numBytesWrittenOtherThanVersion;
		
		nextScanIndex_InBytes = numBytesWritten;
		
		writeHeaderCalled = true;
	}
	
	/**
	 * @throws Exception
	 */
	private void updateDataFileAfterFullCloseMainWriter() throws Exception {

		//  Write to start of file after closing file
		byte[] headerFirstBytes = getHeaderFirstBytes( HeaderFirstBytesInitialWriteUpdateAfterClose.UPDATE_AFTER_CLOSE );

		try ( RandomAccessFile spectalFile = new RandomAccessFile( outputFile_MainDataFileWhileWriting, FILE_MODE_READ_WRITE ) ) {
		
//			spectalFile.getFilePointer();
//			spectalFile.length();
			
			spectalFile.seek( 0 );
			
			spectalFile.write( headerFirstBytes );
		}
				
	}

	
	/**
	 * @param headerFirstBytesInitialWriteUpdateAfterClose
	 * @return
	 * @throws IOException
	 */
	private byte[] getHeaderFirstBytes( HeaderFirstBytesInitialWriteUpdateAfterClose headerFirstBytesInitialWriteUpdateAfterClose ) throws IOException {

		short fileVersion = FILE_VERSION;
		byte fileFullyWrittenValue = DataOrIndexFileFullyWrittenConstants.FILE_FULLY_WRITTEN_NO;
		long dataFileSize = 0;
		
		if ( headerFirstBytesInitialWriteUpdateAfterClose
				== HeaderFirstBytesInitialWriteUpdateAfterClose.UPDATE_AFTER_CLOSE ) {
			
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
		
		dataOutputStream.flush();

		tempOutputStream.close();
		
		byte[] results = tempOutputStream.toByteArray();
		
		return results;
	}

	
	/**
	 * @param header
	 * @return
	 * @throws Exception
	 */
	private int writeHeaderMainPart( SpectralFile_Header_Common header ) throws Exception {

		//  Contents of 'tempOutputStream' are written to file AFTER the length of 'tempOutputStream' 
		//    at the bottom of this method
		
		tempOutputStream.reset();
		
		//  Surround tempOutputStream
		DataOutputStream dataOutputStream = new DataOutputStream( tempOutputStream );

		//  Write scan file length
		
		dataOutputStream.writeLong( header.getScanFileLength_InBytes() );

		//  Write Main Hash

		//      Write Main hash length and then the bytes
		
		dataOutputStream.writeShort( header.getMainHash().length );
		dataOutputStream.write( header.getMainHash() );

		//  Write Alt Hash SHA512

		//      Write Alt Hash SHA512 length and then the bytes
		
		dataOutputStream.writeShort( header.getAltHashSHA512().length );
		dataOutputStream.write( header.getAltHashSHA512() );

		//  Write Alt Hash SHA1

		//      Write Alt Hash SHA1 length and then the bytes
		
		dataOutputStream.writeShort( header.getAltHashSHA1().length );
		dataOutputStream.write( header.getAltHashSHA1() );
		
		dataOutputStream.flush();

		int headerMainPartSize = tempOutputStream.size();

		
		//  Write Length of main header data to output file BEFORE writing header data (excludes VERSION and this length)
		
		//  New Output Byte Stream just for writing length of main header
		ByteArrayOutputStream baos_headerMainPartSize = new ByteArrayOutputStream( 500 );
		DataOutputStream dataOutputStream_headerMainPartSize = new DataOutputStream( baos_headerMainPartSize );

		dataOutputStream_headerMainPartSize.writeShort( headerMainPartSize );
		dataOutputStream_headerMainPartSize.close();
		
		baos_headerMainPartSize.writeTo( outputStream_MainDataFileWhileWriting );		
		

		tempOutputStream.writeTo( outputStream_MainDataFileWhileWriting );
		
		
		outputStream_MainDataFileWhileWriting.flush();
		
		int numBytesWritten = baos_headerMainPartSize.size() + headerMainPartSize; 
		
		return numBytesWritten;
	}
	
	
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
		
		
		SpectralFile_Index_FDFW_SingleScan_V_004 spectralFile_Index_SingleScan_DTO = new SpectralFile_Index_FDFW_SingleScan_V_004();

		spectralFile_Index_SingleScan_DTO.setScanNumber( spectralFile_SingleScan.getScanNumber() );
		spectralFile_Index_SingleScan_DTO.setLevel( spectralFile_SingleScan.getLevel() );
		spectralFile_Index_SingleScan_DTO.setRetentionTime( spectralFile_SingleScan.getRetentionTime() );
		
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
		
		/////
		
		tempOutputStream.reset();
		
		//  Surround tempOutputStream
		DataOutputStream dataOutputStream = new DataOutputStream( tempOutputStream );
		
		dataOutputStream.writeByte( spectralFile_SingleScan.getLevel() );
		dataOutputStream.writeInt( spectralFile_SingleScan.getScanNumber() );
		dataOutputStream.writeFloat( spectralFile_SingleScan.getRetentionTime() );
		dataOutputStream.writeByte( spectralFile_SingleScan.getIsCentroid() );
		
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
		
		ByteArrayOutputStream scanPeaksAsBAOS = encodePeaksAsCompressedBytes( spectralFile_SingleScan.getScanPeaksAsObjectArray() );

		int scanPeaksAsBAOS_Size = scanPeaksAsBAOS.size();
		
		// Length of compressed scan data
		dataOutputStream.writeInt( scanPeaksAsBAOS_Size );

		dataOutputStream.flush();
		

		tempOutputStream.writeTo( outputStream_MainDataFileWhileWriting );
		
		scanPeaksAsBAOS.writeTo( outputStream_MainDataFileWhileWriting );
			
		
		outputStream_MainDataFileWhileWriting.flush();
		
		int scanWithoutPeaksSize = tempOutputStream.size();
		
		
		int numBytesWritten = scanWithoutPeaksSize + scanPeaksAsBAOS_Size;
		
		return numBytesWritten;
		
//		private byte level;
//		private int scanNumber;
//		private float retentionTime;
//		private byte isCentroid;
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
	 * @param scanPeaksAsObjectArray
	 * @return
	 * @throws Exception
	 */
	public ByteArrayOutputStream encodePeaksAsCompressedBytes( List<SpectralFile_SingleScanPeak_Common> scanPeaksAsObjectArray ) throws Exception {
		
		tempScansCompressedOutputStream.reset();
		
		GZIPOutputStream gZIPOutputStream = 
				new GZIPOutputStream( tempScansCompressedOutputStream, true /* syncFlush */ );
		// syncFlush - if true invocation of the inherited flush() method of this instance flushes the compressor with flush mode Deflater.SYNC_FLUSH before flushing the output stream, otherwise only flushes the output strea

		ByteArrayOutputStream singlePeakOutputStream = new ByteArrayOutputStream( 8 ); 

		DataOutputStream dataOutputStream = new DataOutputStream( singlePeakOutputStream );
			
		for ( SpectralFile_SingleScanPeak_Common peak : scanPeaksAsObjectArray ) {
			
			singlePeakOutputStream.reset();
			
			dataOutputStream.writeDouble( peak.getM_over_Z() );
			dataOutputStream.writeFloat( peak.getIntensity() );
			dataOutputStream.flush();
			
			scanPeaksTotalBytes += singlePeakOutputStream.size();
			
			scanPeaksTotalCount++;
			
			singlePeakOutputStream.writeTo( gZIPOutputStream );

//			singlePeakOutputStream.writeTo( tempScansCompressedOutputStream );
			
//			int tempScansCompressedOutputStreamSize = tempScansCompressedOutputStream.size();
//			
//			int z = 0;
		}
		

//		private long scanPeaksTotalBytes = 0;
//		private long scanPeaksCompressedTotalBytes = 0;
		
		
		
//		gZIPOutputStream.finish();  //  Forces gZIPOutputStream to compress cached data and flush
//		
		//  This works to forces gZIPOutputStream to compress cached data and flush
		//    ONLY IF  syncFlush on constructor is set to true.
//		gZIPOutputStream.flush(); 
		
		
		//  Needed to force gZIPOutputStream to compress cached data and flush, 
		//    if gZIPOutputStream.finish();  is not called.
		gZIPOutputStream.close();  //  This will have no effect on ByteArrayOutputStream tempScansCompressedOutputStream
		
		tempScansCompressedOutputStream.flush();
		
//		compareCompressedScanPeaksToScanPeaks( scanPeaksAsObjectArray, scanPeaksAsByteArray );
		
		return tempScansCompressedOutputStream;

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
