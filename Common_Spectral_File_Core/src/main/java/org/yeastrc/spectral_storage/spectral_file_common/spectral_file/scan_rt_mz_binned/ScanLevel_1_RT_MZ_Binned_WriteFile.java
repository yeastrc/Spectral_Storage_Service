package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_rt_mz_binned;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.shared_server_client_importer.accum_scan_rt_mz_binned.dto.MS1_IntensitiesBinnedSummedMapRoot;
import org.yeastrc.spectral_storage.shared_server_client_importer.accum_scan_rt_mz_binned.dto.MS1_IntensitiesBinnedSummed_Summary_DataRoot;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.SpectralStorage_Filename_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.CreateSpectralStorageFilenames;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Write Scan Level 1 data 
 * binned on Retention Time and M/Z 
 * as JSON GZIP
 */
public class ScanLevel_1_RT_MZ_Binned_WriteFile {

	private static final Logger log = LoggerFactory.getLogger(ScanLevel_1_RT_MZ_Binned_WriteFile.class);
	private ScanLevel_1_RT_MZ_Binned_WriteFile() { }
	public static ScanLevel_1_RT_MZ_Binned_WriteFile getInstance() { return new ScanLevel_1_RT_MZ_Binned_WriteFile(); }

	/**
	 * @param accumulate_RT_MZ_Binned_ScanLevel_1
	 * @param hash_String
	 * @param subDirForStorageFiles
	 * @throws Exception
	 */
	public void writeScanLevel_1_RT_MZ_Binned_File( 
			Accumulate_RT_MZ_Binned_ScanLevel_1 accumulate_RT_MZ_Binned_ScanLevel_1,
			String hash_String,
			File subDirForStorageFiles ) throws Exception {
		
		writeScanLevel_1_RT_MZ_Binned_File_JSON_GZIP( accumulate_RT_MZ_Binned_ScanLevel_1, hash_String, subDirForStorageFiles );
	}
	
	/**
	 * @param summaryData
	 * @param hash_String
	 * @param subDirForStorageFiles
	 * @throws Exception
	 */
	private void writeScanLevel_1_RT_MZ_Binned_File_JSON_GZIP( 
			Accumulate_RT_MZ_Binned_ScanLevel_1 accumulate_RT_MZ_Binned_ScanLevel_1,
			String hash_String,
			File subDirForStorageFiles ) throws Exception {

		MS1_IntensitiesBinnedSummedMapRoot summedDataRoot = accumulate_RT_MZ_Binned_ScanLevel_1.getSummedObject();
		MS1_IntensitiesBinnedSummed_Summary_DataRoot summaryData = summedDataRoot.getSummaryData();
		
		String filenameFinal =
				CreateSpectralStorageFilenames.getInstance()
				.createSpectraStorage_ScanBinnedIntensityOn_RT_MZ__JSON_GZIP_Filename( 
						hash_String,
						summaryData.getRtBinSizeInSeconds(),
						summaryData.getMzBinSizeInMZ() );

		String filenameWhileWriting = filenameFinal + SpectralStorage_Filename_Constants.IN_PROGRESS_FILENAME_SUFFIX_SUFFIX;
		
		File dataFileFinal = new File( subDirForStorageFiles, filenameFinal );
		File dataFileWhileWriting = new File( subDirForStorageFiles, filenameWhileWriting );
		
		log.warn("START: Writing file: " + filenameWhileWriting );
		
		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
		
		try ( OutputStream outputStream = new BufferedOutputStream( new GZIPOutputStream( new FileOutputStream( dataFileWhileWriting ) ) ) ) {
			//  Serialize intensitiesMapToJSONRoot to JSON, and write to file
			jacksonJSON_Mapper.writeValue( outputStream, summedDataRoot );
		} catch ( Throwable e ) {
			String msg = "Failed to write to ScanLevel_1_RT_MZ_Binned_File file: " + dataFileWhileWriting.getAbsolutePath();
			log.error( msg, e );
			throw e;
		}

		log.warn("FINISHED: Writing file: " + filenameWhileWriting );

		log.warn("START: Renaming file: " + dataFileWhileWriting.getAbsolutePath() + " TO " + dataFileFinal.getAbsolutePath() );

		if ( ! dataFileWhileWriting.renameTo( dataFileFinal ) ) {
			String msg = "Failed to rename ScanLevel_1_RT_MZ_Binned_File file: " 
					+ dataFileWhileWriting.getAbsolutePath()
					+ ", to file: "
					+ dataFileFinal.getAbsolutePath();
			log.error( msg );
			throw new SpectralStorageProcessingException( msg );
		}
		
		log.warn("FINISHED: Renaming file: " + dataFileWhileWriting.getAbsolutePath() + " TO " + dataFileFinal.getAbsolutePath() );
	}
}
