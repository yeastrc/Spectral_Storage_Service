package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.data_file.reader_writer;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.DataOrIndexFileFullyWrittenConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralFileDataFileNotFullyWrittenException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_Header_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScanPeak_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScan_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.index_file.SpectralFile_Index_FileContents_Root_IF;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_request_results.SpectralFile_Result_RetentionTime_ScanNumber;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.index_file_root_data_object_cache.IndexFileRootDataObjectCache;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader__IF;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.CreateSpectralStorageFilenames;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.GetOrCreateSpectralStorageSubPath;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.StorageFile_Version_003_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.index_file.to_data_file_reader_objects.SpectralFile_Index_TDFR_FileContents_Root_V_003;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.index_file.to_data_file_reader_objects.SpectralFile_Index_TDFR_SingleScan_V_003;

/**
 * Special code to read whole file start to end at the bottom of this class
 *
 */
public class SpectralFile_Reader_GZIP_V_003 implements SpectralFile_Reader__IF {
	
	
	private static final short FILE_VERSION = StorageFile_Version_003_Constants.FILE_VERSION;
	
	
	private static final Logger log = Logger.getLogger(SpectralFile_Reader_GZIP_V_003.class);
	
	private static final String FILE_MODE_READ = "r"; // Used in RandomAccessFile constructor below
	
	/**
	 * private constructor
	 */
	private SpectralFile_Reader_GZIP_V_003(){}
	public static SpectralFile_Reader__IF getInstance( ) throws Exception {
		SpectralFile_Reader__IF instance = new SpectralFile_Reader_GZIP_V_003();
		return instance;
	}
	
	/* (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader__IF#isVersionSupported(short)
	 */
	@Override
	public boolean isVersionSupported(short version) {
		if ( FILE_VERSION == version ) {
			return true;
		}
		return false;
	}


	String hash_String;
	private File subDirForStorageFiles;
	
	
	private File inputFile_MainFile;
	
	private SpectralFile_Index_TDFR_FileContents_Root_V_003 spectralFile_Index_FileContents_Root;
	
	
	@Override
	public void close() throws Exception {

		
	}

	@Override
	public void init( String hash_String, File scanStorageBaseDirectoryFile ) throws Exception {

		this.hash_String = hash_String;
		
		String spectralDataFilename =
				CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Data_Filename( hash_String );

		subDirForStorageFiles = 
				GetOrCreateSpectralStorageSubPath.getInstance().getDirsForHash( hash_String, scanStorageBaseDirectoryFile );
		
		File dataFile = new File( subDirForStorageFiles, spectralDataFilename );
		
		inputFile_MainFile = dataFile;
		
		validateVersion_FileFullyWritten();
		
		SpectralFile_Index_FileContents_Root_IF spectralFile_Index_FileContents_Root_IF =
				IndexFileRootDataObjectCache.getInstance().getSpectralFile_Index_FileContents_Root_IF( hash_String );

		if ( spectralFile_Index_FileContents_Root_IF == null ) {
			String msg = "Failed to read index file for hash: " + hash_String;
			log.error(msg);
			throw new SpectralStorageProcessingException(msg);
		}
		try {
			spectralFile_Index_FileContents_Root = (SpectralFile_Index_TDFR_FileContents_Root_V_003) spectralFile_Index_FileContents_Root_IF;
		} catch ( Exception e ) {
			String msg = "Failed to cast index file data object to correct class for hash: " + hash_String;
			log.error( msg, e );
			throw new SpectralStorageProcessingException( msg, e );
		}
		
	}
	
	/**
	 * @throws Exception
	 */
	private void validateVersion_FileFullyWritten() throws Exception {
		
		short fileVersionInFile = -1;
		
		try ( FileInputStream spectalFileInputSteram = new FileInputStream( inputFile_MainFile ) ) {
			
			try ( DataInputStream dataInputStream = new DataInputStream( spectalFileInputSteram ) ) {

				
				fileVersionInFile = dataInputStream.readShort();

				byte fileFullWrittenIndicator = dataInputStream.readByte();

				if ( fileFullWrittenIndicator != DataOrIndexFileFullyWrittenConstants.FILE_FULLY_WRITTEN_YES ) {
					
					String msg = "Data File not fully written.  Data File fully written indicator not 1.  Data File: " + inputFile_MainFile.getAbsolutePath();
					log.error( msg );
					throw new SpectralFileDataFileNotFullyWrittenException(msg);
				}
			}
		}
		
		if ( fileVersionInFile != FILE_VERSION ) {
			String msg = "File version does not match programatic version.  File Version: " + fileVersionInFile
					+ ", programatic version: " + FILE_VERSION
					+ ".  File: " + inputFile_MainFile.getCanonicalPath();
			log.error( msg );
			throw new SpectralStorageProcessingException( msg );
		}
	
	}


	/* (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader__IF#getHeader()
	 */
	@Override
	public SpectralFile_Header_Common getHeader() throws Exception {

		FileInputStream spectalFileInputStream = null;
		try {
			spectalFileInputStream = new FileInputStream( inputFile_MainFile );
					
			try ( DataInputStream dataInputStream = new DataInputStream( spectalFileInputStream ) ) {
				
				return readHeaderFromDataInputStream( dataInputStream );
			}
		} catch ( Exception e ) {
			String msg = "Error reading header for file: " + inputFile_MainFile.getCanonicalPath();
			log.error( msg, e );
			throw e;
		} finally {
			if ( spectalFileInputStream != null ) {
				spectalFileInputStream.close();
			}
		}
	}
	
	/**
	 * @param dataInputStream
	 * @return
	 * @throws IOException
	 * @throws SpectralFileDataFileNotFullyWrittenException 
	 * @throws SpectralStorageProcessingException 
	 */
	private SpectralFile_Header_Common readHeaderFromDataInputStream( DataInputStream dataInputStream ) throws IOException, SpectralFileDataFileNotFullyWrittenException, SpectralStorageProcessingException {
		
		SpectralFile_Header_Common spectralFile_Header_Common = new SpectralFile_Header_Common();

		//  Read version
		spectralFile_Header_Common.setVersion( dataInputStream.readShort() );

		byte fileFullWrittenIndicator = dataInputStream.readByte();
		
		if ( fileFullWrittenIndicator != DataOrIndexFileFullyWrittenConstants.FILE_FULLY_WRITTEN_YES ) {

			String msg = "Data File not fully written.  First byte is not 1.  Data File: " + inputFile_MainFile.getAbsolutePath();
			log.error( msg );
			throw new SpectralFileDataFileNotFullyWrittenException(msg);
		}

		// Length of this file
		long spectralStorageDataFileLength_InBytes  = dataInputStream.readLong();
		
		if ( spectralStorageDataFileLength_InBytes != inputFile_MainFile.length() ) {
			String msg = "Length stored in data file is not same as actual length of data file.  "
					+ "Length stored in data file: " + spectralStorageDataFileLength_InBytes
					+ ", actual length of data file: " + inputFile_MainFile.length();
			log.error( msg );
			throw new SpectralStorageProcessingException(msg);
		}
		
		
		//  Read mainHeaderLength - not used
		short mainHeaderLength = dataInputStream.readShort();
		
		//  Read Scan File Length
		spectralFile_Header_Common.setScanFileLength_InBytes( dataInputStream.readLong() );
		
		//  Read Scan File Hashes
		spectralFile_Header_Common.setMainHash( readSingleHashFromFile( dataInputStream ) );
		spectralFile_Header_Common.setAltHashSHA512( readSingleHashFromFile( dataInputStream ) );
		spectralFile_Header_Common.setAltHashSHA1( readSingleHashFromFile( dataInputStream ) );
		
		return spectralFile_Header_Common;
	}
	
	/**
	 * Read Single Hash from file
	 * 
	 * @param dataInputStream
	 * @return
	 * @throws IOException 
	 */
	private byte[] readSingleHashFromFile( DataInputStream dataInputStream ) throws IOException {
		
		//  Read Scan File Hash
		short hashLength = dataInputStream.readShort();
		byte[] hash = new byte[ hashLength ];
		{
			int offset = 0;
			int bytesRead = 0;
			while ( ( bytesRead = dataInputStream.read( hash, offset, hashLength - bytesRead ) ) != -1 ) {
				offset += bytesRead;
				if ( offset >= hashLength ) {
					break;
				}
			}
		}
		return hash;
	}

	/* (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader__IF#getScanForScanNumber(int)
	 */
	@Override
	public SpectralFile_SingleScan_Common getScanForScanNumber(int scanNumber) throws Exception {
		
		//  First get entry from Index for scan number
		
		SpectralFile_Index_TDFR_SingleScan_V_003 indexEntry = 
				spectralFile_Index_FileContents_Root.get_SingleScan_ForScanNumber( scanNumber );
		
		if ( indexEntry == null ) {
			//  No Index entry
			return null;  // EARLY RETURN
		}
		
		//  Have Index Entry, now read scan using byte index in main spectral data file
		
		return readScanForScanNumber_IndexEntry( indexEntry );
	}

	/* (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader__IF#getScanLevelForScanNumber(int)
	 */
	@Override
	public Byte getScanLevelForScanNumber(int scanNumber) throws Exception {
		
		//  First get entry from Index for scan number
		
		SpectralFile_Index_TDFR_SingleScan_V_003 indexEntry = 
				spectralFile_Index_FileContents_Root.get_SingleScan_ForScanNumber( scanNumber );
		
		if ( indexEntry == null ) {
			//  No Index entry
			return null;  // EARLY RETURN
		}
		
		//  Have Index Entry, return scan level
		
		return indexEntry.getLevel();
	}


	/* (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader__IF#getScanRetentionTimeForScanNumber(int)
	 */
	@Override
	public SpectralFile_Result_RetentionTime_ScanNumber getScanRetentionTimeForScanNumber( int scanNumber )
			throws Exception {

		//  First get entry from Index for scan number
		
		SpectralFile_Index_TDFR_SingleScan_V_003 indexEntry = 
				spectralFile_Index_FileContents_Root.get_SingleScan_ForScanNumber( scanNumber );

		if ( indexEntry == null ) {
			//  No Index entry
			return null;  // EARLY RETURN
		}
		
		SpectralFile_Result_RetentionTime_ScanNumber response = new SpectralFile_Result_RetentionTime_ScanNumber();
		response.setScanNumber( indexEntry.getScanNumber() );
		response.setLevel( indexEntry.getLevel() );
		response.setRetentionTime( indexEntry.getRetentionTime() );
		return response;
	}
	
	/* (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader__IF#gScanRetentionTimes_All()
	 */
	@Override
	public List<SpectralFile_Result_RetentionTime_ScanNumber> getScanRetentionTimes_All() throws Exception {
		
		List<SpectralFile_Index_TDFR_SingleScan_V_003> indexScanEntries = spectralFile_Index_FileContents_Root.getIndexScanEntries();
		List<SpectralFile_Result_RetentionTime_ScanNumber> results = new ArrayList<>( indexScanEntries.size() ); 

		for ( SpectralFile_Index_TDFR_SingleScan_V_003 indexScanEntry : indexScanEntries ) {
			SpectralFile_Result_RetentionTime_ScanNumber result = new SpectralFile_Result_RetentionTime_ScanNumber();
			result.setScanNumber( indexScanEntry.getScanNumber() );
			result.setLevel( indexScanEntry.getLevel() );
			result.setRetentionTime( indexScanEntry.getRetentionTime() );
			results.add( result );
		}
		return results;
	}
	

	/* (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader__IF#getScanNumbersForRetentionTimeRange(float, float)
	 */
	@Override
	public List<Integer> getScanNumbersForRetentionTimeRange( float retentionTimeStart, float retentionTimeEnd ) throws Exception {
		
		List<Integer> scanNumbersInRange = 
				spectralFile_Index_FileContents_Root.getScanNumbersForRetentionTimeRange( retentionTimeStart, retentionTimeEnd, null /* Optional scanLevel */ );
		
		return scanNumbersInRange;
	}
	
	@Override
	public List<Integer> getScanNumbersForRetentionTimeRangeScanLevel(float retentionTimeStart, float retentionTimeEnd, byte scanLevel) throws Exception {

		List<Integer> scanNumbersInRange = 
				spectralFile_Index_FileContents_Root.getScanNumbersForRetentionTimeRange( retentionTimeStart, retentionTimeEnd, scanLevel );
		
		return scanNumbersInRange;
	}


	/**
	 * @param indexEntry
	 * @return
	 */
	private SpectralFile_SingleScan_Common readScanForScanNumber_IndexEntry( SpectralFile_Index_TDFR_SingleScan_V_003 indexEntry ) throws Exception {

		DataInputStream dataInputStream_ScanData = null;
		try {
			byte[] scanBytes = readBytesOnDiskForScanNumber_IndexEntry( indexEntry );
			
			//  Process byte[] scanBytes containing whole scan
			
			ByteArrayInputStream scanInputStream = new ByteArrayInputStream( scanBytes );

			dataInputStream_ScanData = new DataInputStream( scanInputStream );
			
			return readScan_FromDataInputStream( dataInputStream_ScanData );
			
		} catch ( Throwable e ) {
			String msg = "Failed to read scan for Scan Number: " + indexEntry.getScanNumber()
			+ ", indexEntry.getScanSize_InDataFile_InBytes(): " + indexEntry.getScanSize_InDataFile_InBytes()
			+ ", File: " + inputFile_MainFile.getAbsolutePath();
			log.error( msg );
			System.err.println( msg );
			throw e;
		} finally {
			try {
				dataInputStream_ScanData.close();
			} catch ( Throwable t ) {
				
			}
		}
	}
	
	/**
	 * @param indexEntry
	 * @return
	 */
	private byte[] readBytesOnDiskForScanNumber_IndexEntry( SpectralFile_Index_TDFR_SingleScan_V_003 indexEntry ) throws Exception {

		if ( indexEntry.getScanSize_InDataFile_InBytes() > Integer.MAX_VALUE ) {
			String msg = "Processing Error:  indexEntry.getScanSize_InDataFile_InBytes() > Integer.MAX_VALUE.  Unable to allocate byte[]."
					+ " Scan Number: " + indexEntry.getScanNumber()
					+ ", indexEntry.getScanSize_InDataFile_InBytes(): " + indexEntry.getScanSize_InDataFile_InBytes()
					+ ", File: " + inputFile_MainFile.getAbsolutePath();
			log.error( msg );
			throw new Exception(msg);
		}
		
		//  Read all bytes in file for scan into byte[] scanBytes
		
		byte[] scanBytes = new byte[ (int) indexEntry.getScanSize_InDataFile_InBytes() ];
		
		try ( RandomAccessFile spectalFile = new RandomAccessFile( inputFile_MainFile, FILE_MODE_READ ) ) {
		
//			spectalFile.getFilePointer();
//			spectalFile.length();
			
			spectalFile.seek( indexEntry.getScanIndex_InDataFile_InBytes() );
			
			int totalBytesRead = 0;
			int numBytesRead = -1;
			
			do {
				numBytesRead = spectalFile.read( scanBytes, totalBytesRead, scanBytes.length - totalBytesRead );
				totalBytesRead += numBytesRead;
				
			} while ( numBytesRead != -1 && totalBytesRead < scanBytes.length );
			
			if ( totalBytesRead != scanBytes.length ) {
				String msg = "Number of bytes read is not number of bytes needed. totalBytesRead: " + totalBytesRead
						+ ", scanBytes.length: " + scanBytes.length
						+ ", scanNumber: " + indexEntry.getScanNumber()
						+ ", File: " + inputFile_MainFile.getAbsolutePath();
				log.error( msg );
				throw new Exception(msg);
			}
		}
		
		return scanBytes;
	}
	

	/**
	 * @param indexEntry
	 * @return
	 */
	private SpectralFile_SingleScan_Common readScan_FromDataInputStream( DataInputStream dataInputStream_ScanData  ) throws Exception {

		SpectralFile_SingleScan_Common spectralFile_SingleScan = new SpectralFile_SingleScan_Common();
		
		try {
			spectralFile_SingleScan.setLevel( dataInputStream_ScanData.readByte() );
		} catch ( EOFException e ) {
			//  Reached end of file so return null
			return null;  //  EARLY EXIT
		}
		spectralFile_SingleScan.setScanNumber( dataInputStream_ScanData.readInt() );
		spectralFile_SingleScan.setRetentionTime( dataInputStream_ScanData.readFloat() );
		spectralFile_SingleScan.setIsCentroid( dataInputStream_ScanData.readByte() );
		
		if ( spectralFile_SingleScan.getLevel() > 1 ) {

			//  Only applicable where level > 1
			
			spectralFile_SingleScan.setParentScanNumber( dataInputStream_ScanData.readInt() );
			spectralFile_SingleScan.setPrecursorCharge( dataInputStream_ScanData.readByte() );
			spectralFile_SingleScan.setPrecursor_M_Over_Z( dataInputStream_ScanData.readFloat() );
		}
		
		spectralFile_SingleScan.setNumberScanPeaks( dataInputStream_ScanData.readInt() );
		
		List<SpectralFile_SingleScanPeak_Common> scanPeaksAsObjectArray = 
				getScanPeaks( dataInputStream_ScanData );
		
		spectralFile_SingleScan.setScanPeaksAsObjectArray( scanPeaksAsObjectArray );
		
		return spectralFile_SingleScan;
	}
	
	/**
	 * @param scanBAIS
	 * @param dataInputStream_ScanData
	 * @return
	 * @throws Exception
	 */
	private List<SpectralFile_SingleScanPeak_Common> getScanPeaks( DataInputStream dataInputStream_ScanData ) throws Exception {

		List<SpectralFile_SingleScanPeak_Common> scanPeaksAsObjectArray = new ArrayList<>();
		
		//  Get Scan Peaks from ByteArrayInputStream scanBAIS
		
		//  Scan peaks is a GZIP compressed block of bytes preceeded by the length of that block.
		
		
		// Length of compressed scan peaks data
		int scanPeaksAsCompressedBytes_Size = dataInputStream_ScanData.readInt();
		
		byte[] scanPeaksAsCompressedBytes = new byte[ scanPeaksAsCompressedBytes_Size ];
		
		//  Read scanPeaksAsCompressedBytes from overall Scan data
//		scanInputStream.read( scanPeaksAsCompressedBytes );
		
		//  Read scanPeaksAsCompressedBytes from overall Scan data
		{
			int totalBytesRead = 0;
			int numBytesRead = -1;
			
			do {
				numBytesRead = dataInputStream_ScanData.read( scanPeaksAsCompressedBytes, totalBytesRead, scanPeaksAsCompressedBytes.length - totalBytesRead );
				totalBytesRead += numBytesRead;
				
			} while ( numBytesRead != -1 && totalBytesRead < scanPeaksAsCompressedBytes.length );
			
		}
		
		//  Read scanPeaksAsCompressedBytes as GZIP bytes as DataInputStream
		
		ByteArrayInputStream scanPeaksBAIS = new ByteArrayInputStream( scanPeaksAsCompressedBytes );
		
		try ( GZIPInputStream scanPeaksGZIP_Input = new GZIPInputStream( scanPeaksBAIS ) ) {

			DataInputStream scanPeaks_dataInputStream = new DataInputStream( scanPeaksGZIP_Input );


			SpectralFile_SingleScanPeak_Common spectralFile_SingleScanPeak = null;
			do {
				spectralFile_SingleScanPeak =
						readSingleScanPeakEntry( scanPeaks_dataInputStream );

				if ( spectralFile_SingleScanPeak != null ) { 
					// null returned if at EOF.  EOF at end of scan peaks
					
					scanPeaksAsObjectArray.add( spectralFile_SingleScanPeak );
				}
			} while ( spectralFile_SingleScanPeak != null );
			
		}
		
		return scanPeaksAsObjectArray;
	}
	
	/**
	 * @param scanPeaks_dataInputStream
	 * @return
	 * @throws IOException 
	 */
	private SpectralFile_SingleScanPeak_Common readSingleScanPeakEntry( DataInputStream scanPeaks_dataInputStream ) throws IOException {
		
		SpectralFile_SingleScanPeak_Common spectralFile_SingleScanPeak = new SpectralFile_SingleScanPeak_Common();
		
		//  Read a single Scan Peak from scanPeaks_dataInputStream
		
		//  If get EOF while reading MZ value, assume that normal end of data is reached on the stream.
		
		try {
			spectralFile_SingleScanPeak.setM_over_Z( scanPeaks_dataInputStream.readFloat() );
		} catch( EOFException eofException ) {
			//  Assume that normal end of data reached
			
			//  null returned if at end of stream [EOF (End Of File) ].  EOF at end of scan peaks.
			
			return null;  //  EARLY RETURN
		}
		
		spectralFile_SingleScanPeak.setIntensity( scanPeaks_dataInputStream.readFloat() );

		return spectralFile_SingleScanPeak;
	}
	
	
	private FileInputStream readWholeDataFile_fileInputStream = null;
	private DataInputStream readWholeDataFile_dataInputStream = null;
	
	/**
	 * readWholeDataFile processing - INIT - Open file
	 * @throws FileNotFoundException 
	 */
	public void readWholeDataFile_Open_Init() throws FileNotFoundException {
		
		readWholeDataFile_fileInputStream = new FileInputStream( inputFile_MainFile );
			
		readWholeDataFile_dataInputStream = new DataInputStream( new BufferedInputStream( readWholeDataFile_fileInputStream ) );
	}

	/**
	 * readWholeDataFile processing - Read Header
	 * @throws IOException 
	 * @throws SpectralFileDataFileNotFullyWrittenException 
	 * @throws SpectralStorageProcessingException 
	 * @throws FileNotFoundException 
	 */
	public SpectralFile_Header_Common readWholeDataFile_ReadHeader() throws IOException, SpectralFileDataFileNotFullyWrittenException, SpectralStorageProcessingException {
		
		return readHeaderFromDataInputStream( readWholeDataFile_dataInputStream );
	}

	/**
	 * readWholeDataFile processing - Read Scan
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	public SpectralFile_SingleScan_Common readWholeDataFile_ReadScan() throws Exception {

		return readScan_FromDataInputStream( readWholeDataFile_dataInputStream );
	}

	/**
	 * readWholeDataFile processing - Close
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void readWholeDataFile_Close() throws IOException {
		
		try {
			readWholeDataFile_dataInputStream.close();
		} finally {
			readWholeDataFile_fileInputStream.close();
		}
	}


}

