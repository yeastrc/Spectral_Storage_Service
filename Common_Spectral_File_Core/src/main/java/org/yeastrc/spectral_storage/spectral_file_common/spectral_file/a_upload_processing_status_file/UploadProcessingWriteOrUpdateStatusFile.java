package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.a_upload_processing_status_file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessingStatusFileConstants;

/**
 * Create or update the upload_processing_status.txt file in the uploaded scan file dir
 * 
 * Uses  UploadProcessingStatusFileConstants
 *
 */
public class UploadProcessingWriteOrUpdateStatusFile {

	private static final Logger log = LoggerFactory.getLogger(UploadProcessingWriteOrUpdateStatusFile.class);
	/**
	 * private constructor
	 */
	private UploadProcessingWriteOrUpdateStatusFile(){}
	public static UploadProcessingWriteOrUpdateStatusFile getInstance( ) throws Exception {
		UploadProcessingWriteOrUpdateStatusFile instance = new UploadProcessingWriteOrUpdateStatusFile();
		return instance;
	}

	/**
	 * @param newStatus
	 * @throws Exception
	 */
	public void uploadProcessingWriteOrUpdateStatusFileInLocalDir( String newStatus ) throws Exception {
		
		uploadProcessingWriteOrUpdateStatusFileLocal( newStatus, null, null );
	}


	/**
	 * @param newStatus
	 * @param subDir
	 * @throws Exception
	 */
	public void uploadProcessingWriteOrUpdateStatusFile( String newStatus, File subDir ) throws Exception {
		
		uploadProcessingWriteOrUpdateStatusFileLocal( newStatus, subDir, null );
	}

	/**
	 * @param newStatus
	 * @param subDir
	 * @param callerLabel
	 * @throws Exception
	 */
	public void uploadProcessingWriteOrUpdateStatusFile( String newStatus, File subDir, String callerLabel ) throws Exception {
		
		uploadProcessingWriteOrUpdateStatusFileLocal( newStatus, subDir, callerLabel );
	}

	/**
	 * @param newStatus
	 * @param subDir
	 * @param callerLabel TODO
	 * @throws Exception
	 */
	private void uploadProcessingWriteOrUpdateStatusFileLocal( String newStatus, File subDir, String callerLabel ) throws Exception {
		
		File mainStatusFile = null;
		
		if ( subDir == null ) {
			mainStatusFile = new File( UploadProcessingStatusFileConstants.STATUS_FILENAME );
		} else {
			mainStatusFile = new File( subDir, UploadProcessingStatusFileConstants.STATUS_FILENAME );
		}
		
		try ( BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(new FileOutputStream( mainStatusFile ), StandardCharsets.UTF_8 ) ) ) {
			writer.write( newStatus );
		} catch ( Exception e ) {
			String msg = "Failed to write status to file: " + mainStatusFile.getAbsolutePath();
			log.error( msg );
			throw new Exception(msg);
		}
		
		{
			String statusFileForStatusFilename = UploadProcessingStatusFileConstants.STATUS_FILENAME + newStatus;

			File statusFileForStatus = null;

			if ( subDir == null ) {
				statusFileForStatus = new File( statusFileForStatusFilename );
			} else {
				statusFileForStatus = new File( subDir, statusFileForStatusFilename );
			}

			try ( BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(new FileOutputStream( statusFileForStatus ), StandardCharsets.UTF_8 ) ) ) {
				writer.write( newStatus );
			} catch ( Exception e ) {
				String msg = "Failed to write status to file: " + statusFileForStatus.getAbsolutePath();
				log.error( msg );
				throw new Exception(msg);
			}
		}
		
		if ( StringUtils.isNotEmpty( callerLabel ) ) {
			

			String statusFileForStatusFilename = UploadProcessingStatusFileConstants.STATUS_FILENAME + newStatus + callerLabel;

			File statusFileForStatus = null;
			
			if ( subDir == null ) {
				statusFileForStatus = new File( statusFileForStatusFilename );
			} else {
				statusFileForStatus = new File( subDir, statusFileForStatusFilename );
			}
			
			try ( BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(new FileOutputStream( statusFileForStatus ), StandardCharsets.UTF_8 ) ) ) {
				writer.write( newStatus );
			} catch ( Exception e ) {
				String msg = "Failed to write status to file: " + statusFileForStatus.getAbsolutePath();
				log.error( msg );
				throw new Exception(msg);
			}
		}
	}
}
