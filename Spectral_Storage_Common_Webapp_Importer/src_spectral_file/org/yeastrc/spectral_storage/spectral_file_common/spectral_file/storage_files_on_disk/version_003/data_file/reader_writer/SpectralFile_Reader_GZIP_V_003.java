package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.data_file.reader_writer;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.DataOrIndexFileFullyWrittenConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.ScanCentroidedConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralFileDataFileNotFullyWrittenException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_Header_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScanPeak_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScan_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.index_file.SpectralFile_Index_FileContents_Root_IF;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.scans_lvl_gt_1_partial.SpectralFile_ScansLvlGt1Partial_FileContents_Root_IF;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_request_results.SpectralFile_Result_RetentionTime_ScanNumber;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_request_results.SummaryDataPerScanLevel;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.index_file_root_data_object_cache.IndexFileRootDataObjectCache;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader__IF;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.scans_lvl_gt_1_file_root_data_cache.ScansLvlGt1PartialFileRootDataObjectCache;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.CreateSpectralStorageFilenames;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.StorageFile_Version_003_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.index_file.to_data_file_reader_objects.SpectralFile_Index_TDFR_FileContents_Root_V_003;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.index_file.to_data_file_reader_objects.SpectralFile_Index_TDFR_SingleScan_V_003;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.index_file.to_data_file_reader_objects.SpectralFile_Index_TDFR_SummaryDataPerScanLevel_V_003;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.scans_lvl_gt_1_partial.to_data_file_reader_objects.SpectralFile_ScansLvlGt1Partial_TDFR_FileContents_Root_V_003;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.scans_lvl_gt_1_partial.to_data_file_reader_objects.SpectralFile_ScansLvlGt1Partial_TDFR_SingleScan_V_003;

/**
 * Special code to read whole file start to end at the bottom of this class
 *
 */
public class SpectralFile_Reader_GZIP_V_003 implements SpectralFile_Reader__IF {
	
	private static final boolean OVERRIDE_SKIP_RETURN_SCAN_PEAKS = false;
	
	/**
	 * Special override to not return scan peaks when reading file sequentially
	 */
//	private static final boolean OVERRIDE_SKIP_RETURN_SCAN_PEAKS = true;
	
	private static final short FILE_VERSION = StorageFile_Version_003_Constants.FILE_VERSION;
	
	
	private static final Logger log = Logger.getLogger(SpectralFile_Reader_GZIP_V_003.class);
	
	private static final int SIZE_OF_SCAN_MINUS_SCAN_PEAKS = sizeOfScanMinusScanPeaks();
	
	/**
	 * can be overridden by OVERRIDE_SKIP_RETURN_SCAN_PEAKS = true to read the scan peaks but not return them
	 *
	 */
	private enum ReadScanPeaks { YES, NO }
	
	/**
	 * private constructor
	 */
	private SpectralFile_Reader_GZIP_V_003(){}
	public static SpectralFile_Reader__IF getInstance( ) throws Exception {
		
		if ( OVERRIDE_SKIP_RETURN_SCAN_PEAKS ) {
			log.warn( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
			log.warn( "" );
			log.warn( " SpectralFile_Reader_GZIP_V_003:  OVERRIDE_SKIP_RETURN_SCAN_PEAKS = true");
			log.warn( "" );
			log.warn( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
		}
		
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


	private String hash_String;
	private String spectralDataFilename;
	
	/**
	 * Read from header of file
	 */
	private long spectralStorageDataFileLength_InBytes;
	
	private CommonReader_File_And_S3 commonReader_File_And_S3;
		
	private SpectralFile_Index_TDFR_FileContents_Root_V_003 spectralFile_Index_FileContents_Root;
	
	
	@Override
	public void close() throws Exception {

		
	}

	@Override
	public void init( String hash_String, CommonReader_File_And_S3 commonReader_File_And_S3 ) throws Exception {

		this.hash_String = hash_String;
		
		this.commonReader_File_And_S3 = commonReader_File_And_S3;
		
		spectralDataFilename =
				CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Data_Filename( hash_String );

		validateVersion_FileFullyWritten();
		
		SpectralFile_Index_FileContents_Root_IF spectralFile_Index_FileContents_Root_IF =
				IndexFileRootDataObjectCache.getSingletonInstance().getSpectralFile_Index_FileContents_Root_IF( hash_String );

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
		
		try ( InputStream spectalFileInputStream = commonReader_File_And_S3.getInputStreamForScanStorageItem( spectralDataFilename, hash_String ) ) {
			
			try ( DataInputStream dataInputStream = new DataInputStream( spectalFileInputStream ) ) {

				fileVersionInFile = dataInputStream.readShort();

				byte fileFullWrittenIndicator = dataInputStream.readByte();

				if ( fileFullWrittenIndicator != DataOrIndexFileFullyWrittenConstants.FILE_FULLY_WRITTEN_YES ) {
					
					String msg = "Data File not fully written.  Data File fully written indicator not 1.  spectralDataFilename: " + spectralDataFilename;
					log.error( msg );
					throw new SpectralFileDataFileNotFullyWrittenException(msg);
				}
			}
		}
		
		if ( fileVersionInFile != FILE_VERSION ) {
			String msg = "File version does not match programatic version.  File Version: " + fileVersionInFile
					+ ", programatic version: " + FILE_VERSION
					+ ".  spectralDataFilename: " + spectralDataFilename;
			log.error( msg );
			throw new SpectralStorageProcessingException( msg );
		}
	
	}


	/* (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader__IF#getHeader()
	 */
	@Override
	public SpectralFile_Header_Common getHeader() throws Exception {

		try ( InputStream spectalFileInputStream = commonReader_File_And_S3.getInputStreamForScanStorageItem( spectralDataFilename, hash_String ) ) {
								
			try ( DataInputStream dataInputStream = new DataInputStream( spectalFileInputStream ) ) {
				
				return readHeaderFromDataInputStream( dataInputStream );
			}
		} catch ( Exception e ) {
			String msg = "Error reading header for spectralDataFilename: " + spectralDataFilename;
			log.error( msg, e );
			throw e;
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

		int headerTotalBytesInDataFile = 0;
		
		//  Read version
		spectralFile_Header_Common.setVersion( dataInputStream.readShort() );
		headerTotalBytesInDataFile += Short.BYTES;

		byte fileFullWrittenIndicator = dataInputStream.readByte();
		headerTotalBytesInDataFile += Byte.BYTES;
		
		if ( fileFullWrittenIndicator != DataOrIndexFileFullyWrittenConstants.FILE_FULLY_WRITTEN_YES ) {

			String msg = "Data File not fully written.  First byte is not 1.  spectralDataFilename: " + spectralDataFilename;
			log.error( msg );
			throw new SpectralFileDataFileNotFullyWrittenException(msg);
		}

		// Length of this file
		spectralStorageDataFileLength_InBytes  = dataInputStream.readLong();
		headerTotalBytesInDataFile += Long.BYTES;
		
//		if ( spectralStorageDataFileLength_InBytes != inputFile_MainFile.length() ) {
//			String msg = "Length stored in data file is not same as actual length of data file.  "
//					+ "Length stored in data file: " + spectralStorageDataFileLength_InBytes
//					+ ", actual length of data file: " + inputFile_MainFile.length();
//			log.error( msg );
//			throw new SpectralStorageProcessingException(msg);
//		}
		
		
		//  Read mainHeaderLength - not used
		short mainHeaderLength = dataInputStream.readShort();
		headerTotalBytesInDataFile += Short.BYTES;
		
		//  Rest of header is value in mainHeaderLength
		headerTotalBytesInDataFile += mainHeaderLength;
		
		//  Read Scan File Length
		spectralFile_Header_Common.setScanFileLength_InBytes( dataInputStream.readLong() );
		
		//  Read Scan File Hashes
		spectralFile_Header_Common.setMainHash( readSingleHashFromFile( dataInputStream ) );
		spectralFile_Header_Common.setAltHashSHA512( readSingleHashFromFile( dataInputStream ) );
		spectralFile_Header_Common.setAltHashSHA1( readSingleHashFromFile( dataInputStream ) );
		
		spectralFile_Header_Common.setHeaderTotalBytesInDataFile( headerTotalBytesInDataFile );
		
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
		
		return readScanForScanNumber_IndexEntry( indexEntry, ReadScanPeaks.YES );
	}

	/* (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader__IF#getScanForScanNumber(int)
	 */
	@Override
	public SpectralFile_SingleScan_Common getScanDataNoScanPeaksForScanNumber(int scanNumber) throws Exception {
		
		//  First get entry from Index for scan number
		
		SpectralFile_Index_TDFR_SingleScan_V_003 indexEntry = 
				spectralFile_Index_FileContents_Root.get_SingleScan_ForScanNumber( scanNumber );
		
		if ( indexEntry == null ) {
			//  No Index entry
			return null;  // EARLY RETURN
		}

		SpectralFile_SingleScan_Common spectralFile_SingleScan_Common = new SpectralFile_SingleScan_Common();
		
		spectralFile_SingleScan_Common.setScanNumber( scanNumber ); // from request
		
		// from index:
		spectralFile_SingleScan_Common.setLevel( indexEntry.getLevel() );
		spectralFile_SingleScan_Common.setRetentionTime( indexEntry.getRetentionTime() );

		if ( spectralFile_Index_FileContents_Root.getIsCentroidWholeFile() != ScanCentroidedConstants.SCAN_CENTROIDED_VALUES_IN_FILE_BOTH ) {
			//  Whole file is a single Centroid value so return it
			spectralFile_SingleScan_Common.setIsCentroid( spectralFile_Index_FileContents_Root.getIsCentroidWholeFile() );
		}
		
		if ( indexEntry.getLevel() > 1 ) {

			//  Scan level > 1 so read partial file contents and set in returned object

			SpectralFile_ScansLvlGt1Partial_TDFR_SingleScan_V_003 scansLvlGt1Partial_SingleScan = null;

			try {
				SpectralFile_ScansLvlGt1Partial_FileContents_Root_IF spectralFile_ScansLvlGt1Partial_FileContents_Root_IF=
						ScansLvlGt1PartialFileRootDataObjectCache.getSingletonInstance()
						.getSpectralFile_ScansLvlGt1Partial_FileContents_Root_IF( hash_String );

				if ( spectralFile_ScansLvlGt1Partial_FileContents_Root_IF == null ) {
					String msg = "Failed to read ScansLvlGt1Partial file for hash: " + hash_String;
					log.warn(msg);
					throw new SpectralStorageProcessingException(msg);
				}

				SpectralFile_ScansLvlGt1Partial_TDFR_FileContents_Root_V_003 scansLvlGt1Partial_FileContents_Root = null;
				try {
					scansLvlGt1Partial_FileContents_Root = (SpectralFile_ScansLvlGt1Partial_TDFR_FileContents_Root_V_003) spectralFile_ScansLvlGt1Partial_FileContents_Root_IF;
				} catch ( Exception e ) {
					String msg = "Failed to cast ScansLvlGt1Partial file data object to correct class for hash: " + hash_String;
					log.warn( msg, e );
					throw new SpectralStorageProcessingException( msg, e );
				}

				scansLvlGt1Partial_SingleScan =	scansLvlGt1Partial_FileContents_Root.get_SingleScan_ForScanNumber( scanNumber );
				if ( scansLvlGt1Partial_SingleScan == null ) {
					String msg = "Failed to read scan number from ScansLvlGt1Partial for hash.  scanNumber: " + scanNumber + ", hash: " + hash_String;
					log.warn(msg);
					throw new SpectralStorageProcessingException(msg);
				}
				
			} catch ( Exception e ) {
				
				//  TODO  For now just swallow exception since will then get from main data file
			}
			
			if ( scansLvlGt1Partial_SingleScan == null ) {
				//  Scan number not in ScansLvlGt1Partial file.  As backup get from main data file
				
				//  read scan using byte index in main spectral data file
				SpectralFile_SingleScan_Common spectralFile_SingleScan_Common_FromMainDataFile = readScanForScanNumber_IndexEntry( indexEntry, ReadScanPeaks.NO );
				return spectralFile_SingleScan_Common_FromMainDataFile;
			}
			
			spectralFile_SingleScan_Common.setParentScanNumber( scansLvlGt1Partial_SingleScan.getParentScanNumber() );
			spectralFile_SingleScan_Common.setPrecursorCharge( scansLvlGt1Partial_SingleScan.getPrecursorCharge() );
			spectralFile_SingleScan_Common.setPrecursor_M_Over_Z( scansLvlGt1Partial_SingleScan.getPrecursor_M_Over_Z() );
		}
		
		return spectralFile_SingleScan_Common;
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
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Reader__IF#getScanNumbersForScanLevelsToIncludeScanLevelsToExclude(java.util.List, java.util.List)
	 */
	@Override
	public List<Integer> getScanNumbersForScanLevelsToIncludeScanLevelsToExclude(
			List<Integer> scanLevelsToInclude,
			List<Integer> scanLevelsToExclude ) throws Exception {

		Set<Integer> scanLevelsToIncludeSet = null;
		Set<Integer> scanLevelsToExcludeSet = null;
		
		if ( scanLevelsToInclude != null ) {
			scanLevelsToIncludeSet = new HashSet<>( scanLevelsToInclude );
		}
		if ( scanLevelsToExclude != null ) {
			scanLevelsToExcludeSet = new HashSet<>( scanLevelsToExclude );
		}
		
		List<SpectralFile_Index_TDFR_SingleScan_V_003> indexScanEntries = spectralFile_Index_FileContents_Root.getIndexScanEntries();
		List<Integer> results = new ArrayList<>( indexScanEntries.size() ); 

		for ( SpectralFile_Index_TDFR_SingleScan_V_003 indexScanEntry : indexScanEntries ) {
			Integer scanLevel = (int) indexScanEntry.getLevel();
			if ( scanLevelsToIncludeSet != null 
					&& ( ! scanLevelsToIncludeSet.contains( scanLevel ) ) ) {
				continue;  // Skip entry
			}
			if ( scanLevelsToExcludeSet != null 
					&& ( scanLevelsToExcludeSet.contains( scanLevel ) ) ) {
				continue;  // Skip entry
			}
			results.add( indexScanEntry.getScanNumber() );
		}
		return results;
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

	@Override
	public List<SummaryDataPerScanLevel> getSummaryDataPerScanLevel_All()  throws Exception{
		
		List<SpectralFile_Index_TDFR_SummaryDataPerScanLevel_V_003> summaryDataPerScanLevel_InIndex_List =
				spectralFile_Index_FileContents_Root.getSummaryDataPerScanLevelList();
		
		if ( summaryDataPerScanLevel_InIndex_List == null ) {
			return null;
		}

		List<SummaryDataPerScanLevel> outputList = new ArrayList<>( summaryDataPerScanLevel_InIndex_List.size() );
		
		for ( SpectralFile_Index_TDFR_SummaryDataPerScanLevel_V_003 summaryInIndexItem : summaryDataPerScanLevel_InIndex_List ) {
			SummaryDataPerScanLevel outputEntry = new SummaryDataPerScanLevel();
			outputList.add( outputEntry );
			outputEntry.setScanLevel( summaryInIndexItem.getScanLevel() );
			outputEntry.setNumberOfScans( summaryInIndexItem.getNumberOfScans() );
			outputEntry.setTotalIonCurrent( summaryInIndexItem.getTotalIonCurrent() );
		}
		
		return outputList;
	}
	
	/**
	 * @param indexEntry
	 * @param readScanPeaks
	 * @return
	 * @throws Exception
	 */
	private SpectralFile_SingleScan_Common readScanForScanNumber_IndexEntry( 
			SpectralFile_Index_TDFR_SingleScan_V_003 indexEntry,
			ReadScanPeaks readScanPeaks ) throws Exception {

		DataInputStream dataInputStream_ScanData = null;
		try {
			byte[] scanBytes = null;

			if ( readScanPeaks != null && readScanPeaks == ReadScanPeaks.NO ) {
				//  byte[] scanBytes containing scan without peaks
				scanBytes = readBytesOnDiskForScanNumber_IndexEntry( indexEntry, SIZE_OF_SCAN_MINUS_SCAN_PEAKS );
			} else {
				//  create byte[] scanBytes containing whole scan
				scanBytes = readBytesOnDiskForScanNumber_IndexEntry( indexEntry, null /* numberOfBytesToReadOverride */ );
			}
			
			
			ByteArrayInputStream scanInputStream = new ByteArrayInputStream( scanBytes );

			dataInputStream_ScanData = new DataInputStream( scanInputStream );
			
			SpectralFile_SingleScan_Common result = readScan_FromDataInputStream( dataInputStream_ScanData, readScanPeaks );
			
			return result;
			
		} catch ( Throwable e ) {
			String msg = "Failed to read scan for Scan Number: " + indexEntry.getScanNumber()
			+ ", indexEntry.getScanSize_InDataFile_InBytes(): " + indexEntry.getScanSize_InDataFile_InBytes()
			+ ", spectralDataFilename: " + spectralDataFilename;
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
	private byte[] readBytesOnDiskForScanNumber_IndexEntry( 
			SpectralFile_Index_TDFR_SingleScan_V_003 indexEntry,
			Integer numberOfBytesToReadOverride ) throws Exception {
		
		if ( indexEntry.getScanSize_InDataFile_InBytes() > Integer.MAX_VALUE ) {
			String msg = "Processing Error:  indexEntry.getScanSize_InDataFile_InBytes() > Integer.MAX_VALUE.  Unable to allocate byte[]."
					+ " Scan Number: " + indexEntry.getScanNumber()
					+ ", indexEntry.getScanSize_InDataFile_InBytes(): " + indexEntry.getScanSize_InDataFile_InBytes()
					+ ", spectralDataFilename: " + spectralDataFilename;
			log.error( msg );
			throw new SpectralStorageProcessingException(msg);
		}
		
		//  Read all bytes in file for scan into byte[] scanBytes
		
		int byteArraySize = (int) indexEntry.getScanSize_InDataFile_InBytes();
		
		if ( numberOfBytesToReadOverride != null ) {
			//  Override to number of bytes requested
			byteArraySize = numberOfBytesToReadOverride;
			if ( byteArraySize + indexEntry.getScanIndex_InDataFile_InBytes() > spectralStorageDataFileLength_InBytes ) {
				//  Specified to read past end of file so set to read to end of file
				long byteArraySizeAsLong = spectralStorageDataFileLength_InBytes - indexEntry.getScanIndex_InDataFile_InBytes();
				if ( byteArraySizeAsLong > Integer.MAX_VALUE ) {
					String msg = "Computed bytes to read > Integer.MAX_VALUE: " + byteArraySizeAsLong
							+ ", spectralDataFilename: " + spectralDataFilename;
					log.error( msg );
					throw new SpectralStorageProcessingException( msg );
				}
			}
		}

		return commonReader_File_And_S3.getBytesFromScanStorageItem(
				spectralDataFilename, hash_String, indexEntry.getScanIndex_InDataFile_InBytes(), byteArraySize );
	}
	
	///////////////////////////////////////////////////
	
	//////    WARNING:  Keep sizeOfScanMinusScanPeaks in sync with following method readScan_FromDataInputStream(...)
	//                  for size of scan data
	
	/**
	 * @return in bytes the size of a scan minus the scan peaks (scan peaks assumed to be at end of scan data)
	 */
	private static int sizeOfScanMinusScanPeaks() {
		
		int scanTotalBytesInDataFile = 0;
		
//		spectralFile_SingleScan.setLevel( dataInputStream_ScanData.readByte() );
		scanTotalBytesInDataFile += Byte.BYTES;
		
//		spectralFile_SingleScan.setScanNumber( dataInputStream_ScanData.readInt() );
		scanTotalBytesInDataFile += Integer.BYTES;
//		spectralFile_SingleScan.setRetentionTime( dataInputStream_ScanData.readFloat() );
		scanTotalBytesInDataFile += Float.BYTES;
//		spectralFile_SingleScan.setIsCentroid( dataInputStream_ScanData.readByte() );
		scanTotalBytesInDataFile += Byte.BYTES;
		
//		if ( spectralFile_SingleScan.getLevel() > 1 ) {
//
//			//  Only applicable where level > 1
			
//			spectralFile_SingleScan.setParentScanNumber( dataInputStream_ScanData.readInt() );
			scanTotalBytesInDataFile += Integer.BYTES;
//			spectralFile_SingleScan.setPrecursorCharge( dataInputStream_ScanData.readByte() );
			scanTotalBytesInDataFile += Byte.BYTES;
//			spectralFile_SingleScan.setPrecursor_M_Over_Z( dataInputStream_ScanData.readDouble() );
			scanTotalBytesInDataFile += Double.BYTES;
//		}
		
//		spectralFile_SingleScan.setNumberScanPeaks( dataInputStream_ScanData.readInt() );
		scanTotalBytesInDataFile += Integer.BYTES;
		
		return scanTotalBytesInDataFile;
	}

	//////    WARNING:  Keep readScan_FromDataInputStream in sync
	//                  with previous method sizeOfScanMinusScanPeaks()
	//                  for size of scan data

	/**
	 * @param dataInputStream_ScanData
	 * @param readScanPeaks
	 * @return
	 * @throws Exception
	 */
	private SpectralFile_SingleScan_Common readScan_FromDataInputStream( 
			DataInputStream dataInputStream_ScanData,
			ReadScanPeaks readScanPeaks ) throws Exception {

		SpectralFile_SingleScan_Common spectralFile_SingleScan = new SpectralFile_SingleScan_Common();

		int scanTotalBytesInDataFile = 0;
		
		try {
			spectralFile_SingleScan.setLevel( dataInputStream_ScanData.readByte() );
			scanTotalBytesInDataFile += Byte.BYTES;
		} catch ( EOFException e ) {
			//  Reached end of file so return null
			return null;  //  EARLY EXIT
		}
		spectralFile_SingleScan.setScanNumber( dataInputStream_ScanData.readInt() );
		scanTotalBytesInDataFile += Integer.BYTES;
		spectralFile_SingleScan.setRetentionTime( dataInputStream_ScanData.readFloat() );
		scanTotalBytesInDataFile += Float.BYTES;
		spectralFile_SingleScan.setIsCentroid( dataInputStream_ScanData.readByte() );
		scanTotalBytesInDataFile += Byte.BYTES;
		
		if ( spectralFile_SingleScan.getLevel() > 1 ) {

			//  Only applicable where level > 1
			
			spectralFile_SingleScan.setParentScanNumber( dataInputStream_ScanData.readInt() );
			scanTotalBytesInDataFile += Integer.BYTES;
			spectralFile_SingleScan.setPrecursorCharge( dataInputStream_ScanData.readByte() );
			scanTotalBytesInDataFile += Byte.BYTES;
			spectralFile_SingleScan.setPrecursor_M_Over_Z( dataInputStream_ScanData.readDouble() );
			scanTotalBytesInDataFile += Double.BYTES;
		}
		
		spectralFile_SingleScan.setNumberScanPeaks( dataInputStream_ScanData.readInt() );
		scanTotalBytesInDataFile += Integer.BYTES;
		
		MutableInt scanPeaksTotalBytesInDataFile = new MutableInt( 0 );
		
		if ( readScanPeaks == ReadScanPeaks.YES ) {
			
			List<SpectralFile_SingleScanPeak_Common> scanPeaksAsObjectArray = 
					getScanPeaks( dataInputStream_ScanData, scanPeaksTotalBytesInDataFile );

			scanTotalBytesInDataFile += scanPeaksTotalBytesInDataFile.intValue();

			spectralFile_SingleScan.setScanPeaksAsObjectArray( scanPeaksAsObjectArray );

			// setScanTotalBytesInDataFile only set if read scan peaks
			spectralFile_SingleScan.setScanTotalBytesInDataFile( scanTotalBytesInDataFile );
		}
		
		return spectralFile_SingleScan;
	}
	
	/**
	 * @param scanBAIS
	 * @param dataInputStream_ScanData
	 * @return
	 * @throws Exception
	 */
	private List<SpectralFile_SingleScanPeak_Common> getScanPeaks( 
			DataInputStream dataInputStream_ScanData,
			MutableInt scanPeaksTotalBytesInDataFile ) throws Exception {

		List<SpectralFile_SingleScanPeak_Common> scanPeaksAsObjectArray = new ArrayList<>();
		
		//  Get Scan Peaks from ByteArrayInputStream scanBAIS
		
		//  Scan peaks is a GZIP compressed block of bytes preceeded by the length of that block.
		
		
		// Length of compressed scan peaks data
		int scanPeaksAsCompressedBytes_Size = dataInputStream_ScanData.readInt();
		scanPeaksTotalBytesInDataFile.add( Integer.BYTES );
		
		scanPeaksTotalBytesInDataFile.add( scanPeaksAsCompressedBytes_Size );
		
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
		
		if ( OVERRIDE_SKIP_RETURN_SCAN_PEAKS ) {
			return scanPeaksAsObjectArray;
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
			spectralFile_SingleScanPeak.setM_over_Z( scanPeaks_dataInputStream.readDouble() );
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
	 * 
	 * Only used when Read the whole file directly
	 * @param dataFile
	 * @throws Exception
	 */
	public void readWholeDataFile_Init_OpenFile( File dataFile, CommonReader_File_And_S3 commonReader_File_And_S3 ) throws Exception {

		spectralDataFilename = dataFile.getName();
		
		int dotIndex = spectralDataFilename.indexOf( '.' );
		
		this.hash_String = spectralDataFilename.substring(0, dotIndex);
		
		this.commonReader_File_And_S3 = commonReader_File_And_S3;
			
		validateVersion_FileFullyWritten();

		readWholeDataFile_fileInputStream = new FileInputStream( dataFile );
			
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

		return readScan_FromDataInputStream( readWholeDataFile_dataInputStream, ReadScanPeaks.YES );
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

