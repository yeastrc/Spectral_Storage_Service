package org.yeastrc.spectral_storage.index_file_rebuild.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.index_file_rebuild.constants.DataFile_Version_BeingProcessed_Constants;
import org.yeastrc.spectral_storage.index_file_rebuild.constants.OldIndexFilesDirConstants;
import org.yeastrc.spectral_storage.index_file_rebuild.run_control.RunControlFile_Create_Read;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.accum_scan_summary_data.AccumulateSummaryDataPerScanLevel;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.SpectralStorage_Filename_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_rt_mz_binned.Accumulate_RT_MZ_Binned_ScanLevel_1;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_rt_mz_binned.ScanLevel_1_RT_MZ_Binned_WriteFile;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_Header_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScan_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.CreateSpectralStorageFilenames;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.common.Get_isCentroidWholeFile_ForIndexFileHeader;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.data_file.reader_writer.SpectralFile_Reader_GZIP_V_003;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.index_file.from_data_file_writer_objects.SpectralFile_Index_FDFW_FileContents_Root_V_003;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.index_file.from_data_file_writer_objects.SpectralFile_Index_FDFW_SingleScan_V_003;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.index_file.reader_writer.SpectralFile_Index_File_Writer_V_003;

/**
 * Index_File_Rebuild__File_Version_003
 *
 */
public class ProcessFileWithDataFileList {

	private static final Logger log = Logger.getLogger(ProcessFileWithDataFileList.class);
	/**
	 * private constructor
	 */
	private ProcessFileWithDataFileList(){}
	public static ProcessFileWithDataFileList getInstance( ) throws Exception {
		ProcessFileWithDataFileList instance = new ProcessFileWithDataFileList();
		return instance;
	}
	
	/**
	 * @param outputBaseDir
	 * @param fileContainingListOfDataFiles
	 * @throws Exception 
	 */
	public void processFileWithDataFileList(
			File outputBaseDir,
			File fileContainingListOfDataFiles,
			boolean createBinnedScanLevel_1_file
			) throws Exception {
		
		File oldIndexFilesDir = new File( outputBaseDir, OldIndexFilesDirConstants.OLD_INDEX_FILES_DIR_NAME );
		
		if ( ! oldIndexFilesDir.exists() ) {
			oldIndexFilesDir.mkdir();
		}
		if ( ! oldIndexFilesDir.exists() ) {
			String msg = "Failed to make dir '"
					+ oldIndexFilesDir.getAbsolutePath()
					+ "'";
			log.error( msg );
			throw new Exception( msg );
		}

		//  First rename all index files to old
		try ( BufferedReader reader_fileContainingListOfDataFiles = new BufferedReader( new FileReader( fileContainingListOfDataFiles )) ) {
			
			String dataFileString = null;
					
			while ( ( dataFileString = reader_fileContainingListOfDataFiles.readLine() ) != null ) {
				
				if ( RunControlFile_Create_Read.getInstance().runControlFile_Contains_StopRun() ) {
					System.out.println( "Stopping due to run control file contents before processing data file: " 
							+ dataFileString );
					return;
				}

				File dataFile = new File( dataFileString );
				if ( ! dataFile.exists() ) {
					String msg = "dataFile not exist: " + dataFileString;
					log.error( msg );
					throw new Exception( msg );
				}

				if ( ! isDataFileCorrectVersion( dataFile ) ) {
					continue; // EARLY CONTINUE
				}
				
				String dataFileName = dataFile.getName();
				
				File subDirForStorageFiles = dataFile.getParentFile();
				
				String hash_String = dataFileName.substring(0, dataFileName.length() - SpectralStorage_Filename_Constants.DATA_FILENAME_SUFFIX.length() );
				
//					Rename of existing index file
					
				String index_Filename =
						CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Index_Filename( hash_String );
				String index_Filename_RenameToOld = index_Filename + OldIndexFilesDirConstants.OLD_INDEX_FILE_NAME_SUFFIX; 
						
				File indexFileExisting = new File( subDirForStorageFiles, index_Filename );
				File indexFileExisting_RenameToOld = new File( subDirForStorageFiles, index_Filename_RenameToOld );
				
				if ( indexFileExisting.exists() ) {
					if ( ! indexFileExisting.renameTo( indexFileExisting_RenameToOld ) ) {
						String msg = "Failed to rename index file from '" 
								+ indexFileExisting.getAbsolutePath()
								+ "' to '"
								+ indexFileExisting_RenameToOld.getAbsolutePath()
								+ "'.";
						log.error( msg );
						throw new Exception(msg);
					}
				}
			}
			
		}
		
		//  Main Processing
		
		try ( BufferedReader reader_fileContainingListOfDataFiles = new BufferedReader( new FileReader( fileContainingListOfDataFiles )) ) {
			
			String dataFileString = null;
					
			while ( ( dataFileString = reader_fileContainingListOfDataFiles.readLine() ) != null ) {
				
				if ( RunControlFile_Create_Read.getInstance().runControlFile_Contains_StopRun() ) {
					System.out.println( "Stopping due to run control file contents before processing data file: " 
							+ dataFileString );
					return;
				}
				
				File dataFile = new File( dataFileString );
				if ( ! dataFile.exists() ) {
					String msg = "dataFile not exist: " + dataFileString;
					log.error( msg );
					throw new Exception( msg );
				}
				
				if ( isDataFileCorrectVersion( dataFile ) ) {
				
					processDataFile( dataFile, outputBaseDir, createBinnedScanLevel_1_file, oldIndexFilesDir );
				}
			}
		}
		
	}
	
	/**
	 * @param dataFile
	 * @return
	 * @throws Exception
	 */
	private boolean isDataFileCorrectVersion( File dataFile ) throws Exception {

		SpectralFile_Reader_GZIP_V_003 spectralFile_Reader_GZIP_V_003 = (SpectralFile_Reader_GZIP_V_003) SpectralFile_Reader_GZIP_V_003.getInstance();

		try {
			spectralFile_Reader_GZIP_V_003.readWholeDataFile_Init_OpenFile( dataFile );
			
			SpectralFile_Header_Common spectralFile_Header_Common = spectralFile_Reader_GZIP_V_003.readWholeDataFile_ReadHeader();

			if ( DataFile_Version_BeingProcessed_Constants.dataFile_Version_BeingProcessed != spectralFile_Header_Common.getVersion() ) {
				String msg = "INFO:  Data File will be skipped.  dataFile does not contain the data file version being processed. Data Version being processed: " 
						+ DataFile_Version_BeingProcessed_Constants.dataFile_Version_BeingProcessed
						+ ", data file version in data file: "
						+ spectralFile_Header_Common.getVersion()
						+ ", data file: "
						+ dataFile.getAbsolutePath();
				log.warn( msg );
				return false;  //  EARLY RETURN
			}
			return true;
			
		} finally {
			spectralFile_Reader_GZIP_V_003.close();
		}
		
	}
	
	/**
	 * @param dataFile
	 * @param outputBaseDir
	 * @throws Exception 
	 */
	private void processDataFile( 
			File dataFile, 
			File outputBaseDir,
			boolean createBinnedScanLevel_1_file,
			File oldIndexFilesDir ) throws Exception {
		
		System.out.println( "Start: Processing data file Now: " + new Date() );
		System.out.println( "Start: Processing data file: " + dataFile.getAbsolutePath() + ", Now: " + new Date() );

		
		String dataFileName = dataFile.getName();
		
		File subDirForStorageFiles = dataFile.getParentFile();
		
		String hash_String = dataFileName.substring(0, dataFileName.length() - SpectralStorage_Filename_Constants.DATA_FILENAME_SUFFIX.length() );
		
//			Rename of existing index file
			
		String index_Filename =
				CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Index_Filename( hash_String );
		String index_Filename_RenameToOld = index_Filename + OldIndexFilesDirConstants.OLD_INDEX_FILE_NAME_SUFFIX; 
				
		File indexFileExisting = new File( subDirForStorageFiles, index_Filename );
		File indexFileExisting_RenameToOld = new File( subDirForStorageFiles, index_Filename_RenameToOld );
		
		if ( indexFileExisting.exists() ) {
			if ( ! indexFileExisting.renameTo( indexFileExisting_RenameToOld ) ) {
				String msg = "Failed to rename index file from '" 
						+ indexFileExisting.getAbsolutePath()
						+ "' to '"
						+ indexFileExisting_RenameToOld.getAbsolutePath()
						+ "'.";
				log.error( msg );
				throw new Exception(msg);
			}
		}
		
		
		SpectralFile_Reader_GZIP_V_003 spectralFile_Reader_GZIP_V_003 = (SpectralFile_Reader_GZIP_V_003) SpectralFile_Reader_GZIP_V_003.getInstance();

		/**
		 * Accumulate scan level 1 data binned for RT and MZ
		 */
		Accumulate_RT_MZ_Binned_ScanLevel_1 accumulate_RT_MZ_Binned_ScanLevel_1 = null;
		
		accumulate_RT_MZ_Binned_ScanLevel_1 = Accumulate_RT_MZ_Binned_ScanLevel_1.getInstance();

		try {
			spectralFile_Reader_GZIP_V_003.readWholeDataFile_Init_OpenFile( dataFile );
			
			SpectralFile_Header_Common spectralFile_Header_Common = spectralFile_Reader_GZIP_V_003.readWholeDataFile_ReadHeader();

			if ( DataFile_Version_BeingProcessed_Constants.dataFile_Version_BeingProcessed != spectralFile_Header_Common.getVersion() ) {
				String msg = "INFO:  Data File will be skipped.  dataFile does not contain the data file version being processed. Data Version being processed: " 
						+ DataFile_Version_BeingProcessed_Constants.dataFile_Version_BeingProcessed
						+ ", data file version in data file: "
						+ spectralFile_Header_Common.getVersion()
						+ ", data file: "
						+ dataFile.getAbsolutePath();
				log.warn( msg );
				return;  //  EARLY RETURN
			}
			
			int headerTotalBytesInDataFile = spectralFile_Header_Common.getHeaderTotalBytesInDataFile();

			Set<Byte> isCentroidUniqueValuesInScans = new HashSet<>();
			AccumulateSummaryDataPerScanLevel accumulateSummaryDataPerScanLevel = AccumulateSummaryDataPerScanLevel.getInstance(); 

			MutableLong totalBytesForAllSingleScans = new MutableLong();

			List<SpectralFile_Index_FDFW_SingleScan_V_003> indexScanEntries = 
					processScans( 
							spectralFile_Reader_GZIP_V_003, 
							isCentroidUniqueValuesInScans, 
							headerTotalBytesInDataFile,
							accumulateSummaryDataPerScanLevel, 
							accumulate_RT_MZ_Binned_ScanLevel_1,
							totalBytesForAllSingleScans );
			
			SpectralFile_Index_FDFW_FileContents_Root_V_003 spectralFile_Index_FDFW_FileContents_Root = new SpectralFile_Index_FDFW_FileContents_Root_V_003();

			spectralFile_Index_FDFW_FileContents_Root.setVersion( spectralFile_Header_Common.getVersion() );
			spectralFile_Index_FDFW_FileContents_Root.setTotalBytesForAllSingleScans( totalBytesForAllSingleScans.longValue() );

			spectralFile_Index_FDFW_FileContents_Root.setIsCentroidWholeFile( 
					Get_isCentroidWholeFile_ForIndexFileHeader.getInstance()
					.get_isCentroidWholeFile_ForIndexFileHeader( isCentroidUniqueValuesInScans ) );
			
			spectralFile_Index_FDFW_FileContents_Root.setIndexScanEntries( indexScanEntries );

			SpectralFile_Index_File_Writer_V_003.getInstance().writeIndexFile( hash_String, subDirForStorageFiles, spectralFile_Index_FDFW_FileContents_Root, accumulateSummaryDataPerScanLevel );
			
			if ( accumulate_RT_MZ_Binned_ScanLevel_1 != null ) {

				//  Write data in accumulate_RT_MZ_Binned_ScanLevel_1 to file
				ScanLevel_1_RT_MZ_Binned_WriteFile.getInstance()
				.writeScanLevel_1_RT_MZ_Binned_File( 
						accumulate_RT_MZ_Binned_ScanLevel_1,
						hash_String, 
						subDirForStorageFiles );
			}
			
			
		} finally {
			spectralFile_Reader_GZIP_V_003.close();
		}
		

		// move existing index file (renamed) to OLD Index files dir under root dir

//		String index_Filename =
//				CreateSpectralStorageFilenames.getInstance().createSpectraStorage_Index_Filename( hash_String );
//		String index_Filename_RenameToOld = index_Filename + OldIndexFilesDirConstants.OLD_INDEX_FILE_NAME_SUFFIX; 
//				
//		File indexFileExisting = new File( subDirForStorageFiles, index_Filename );
//		File indexFileExisting_RenameToOld = new File( subDirForStorageFiles, index_Filename_RenameToOld );
				
		if ( indexFileExisting_RenameToOld.exists() ) {
			File indexFileExisting_RenameToOld_InOldIndexesDir = new File( oldIndexFilesDir, index_Filename_RenameToOld );
			if ( ! indexFileExisting_RenameToOld.renameTo( indexFileExisting_RenameToOld_InOldIndexesDir ) ) {
				String msg = "Failed to move/rename index file from '" 
						+ indexFileExisting_RenameToOld.getAbsolutePath()
						+ "' to '"
						+ indexFileExisting_RenameToOld_InOldIndexesDir.getAbsolutePath()
						+ "'.";
				log.error( msg );
				throw new Exception(msg);
			}
		}
		
		
		System.out.println( "End: Processing data file: " + dataFile.getAbsolutePath() + ", Now: " + new Date() );
		System.out.println( "End: Processing data file Now: " + new Date() );

		
	}
	
	private List<SpectralFile_Index_FDFW_SingleScan_V_003> processScans( 
			SpectralFile_Reader_GZIP_V_003 spectralFile_Reader_GZIP_V_003, 
			Set<Byte> isCentroidUniqueValuesInScans,
			int headerTotalBytesInDataFile,
			AccumulateSummaryDataPerScanLevel accumulateSummaryDataPerScanLevel,
			Accumulate_RT_MZ_Binned_ScanLevel_1 accumulate_RT_MZ_Binned_ScanLevel_1,
			MutableLong totalBytesForAllSingleScans ) throws Exception {
		
		long currentScanStartIndex = headerTotalBytesInDataFile;

		List<SpectralFile_Index_FDFW_SingleScan_V_003> indexScanEntries = new ArrayList<>();
		
		SpectralFile_SingleScan_Common spectralFile_SingleScan_Common = null;
		
		while ( ( spectralFile_SingleScan_Common = spectralFile_Reader_GZIP_V_003.readWholeDataFile_ReadScan() ) != null ) {
			
			isCentroidUniqueValuesInScans.add( spectralFile_SingleScan_Common.getIsCentroid() );
			
			SpectralFile_Index_FDFW_SingleScan_V_003 spectralFile_Index_FDFW_SingleScan_V_003 = new SpectralFile_Index_FDFW_SingleScan_V_003();
			indexScanEntries.add( spectralFile_Index_FDFW_SingleScan_V_003 );
			
			byte level = spectralFile_SingleScan_Common.getLevel();
			
			spectralFile_Index_FDFW_SingleScan_V_003.setLevel( level );
			spectralFile_Index_FDFW_SingleScan_V_003.setScanNumber( spectralFile_SingleScan_Common.getScanNumber() );
			spectralFile_Index_FDFW_SingleScan_V_003.setRetentionTime( spectralFile_SingleScan_Common.getRetentionTime() );
			
			spectralFile_Index_FDFW_SingleScan_V_003.setScanSize_InDataFile_InBytes( spectralFile_SingleScan_Common.getScanTotalBytesInDataFile() );
					
			spectralFile_Index_FDFW_SingleScan_V_003.setScanIndex_InDataFile_InBytes( currentScanStartIndex );
			
			currentScanStartIndex += spectralFile_SingleScan_Common.getScanTotalBytesInDataFile();
			
			totalBytesForAllSingleScans.add( spectralFile_SingleScan_Common.getScanTotalBytesInDataFile() );
			
			accumulateSummaryDataPerScanLevel.addScanToAccum( spectralFile_SingleScan_Common );
			
			if ( accumulate_RT_MZ_Binned_ScanLevel_1 != null ) {
				accumulate_RT_MZ_Binned_ScanLevel_1.processScanForAccum( spectralFile_SingleScan_Common );
			}
		}

		return indexScanEntries;
	}

	
	
}
