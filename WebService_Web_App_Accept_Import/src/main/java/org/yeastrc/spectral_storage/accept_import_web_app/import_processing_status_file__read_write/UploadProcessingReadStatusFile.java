package org.yeastrc.spectral_storage.accept_import_web_app.import_processing_status_file__read_write;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.UploadProcessingStatusFileConstants;
import org.slf4j.Logger;

/**
 * Read upload_processing_status.txt file in the uploaded scan file dir
 * 
 * Uses  UploadProcessingStatusFileConstants
 *
 */
public class UploadProcessingReadStatusFile {

	private static final Logger log = LoggerFactory.getLogger(UploadProcessingReadStatusFile.class);
	/**
	 * private constructor
	 */
	private UploadProcessingReadStatusFile(){}
	public static UploadProcessingReadStatusFile getInstance( ) throws Exception {
		UploadProcessingReadStatusFile instance = new UploadProcessingReadStatusFile();
		return instance;
	}

	/**
	 * @return null if status file not exist
	 * @throws Exception
	 */
	public String uploadProcessingReadStatusFileInLocalDir() throws Exception {
		
		return uploadProcessingReadStatusFileLocal( null );
	}


	/**
	 * @param subDir
	 * @return null if status file not exist
	 * @throws Exception
	 */
	public String uploadProcessingReadStatusFile( File subDir ) throws Exception {
		
		return uploadProcessingReadStatusFileLocal( subDir );
	}

	/**
	 * @param newStatus
	 * @param subDir
	 * @throws Exception
	 */
	private String uploadProcessingReadStatusFileLocal( File subDir ) throws Exception {
		
		File mainStatusFile = null;
		
		if ( subDir == null ) {
			mainStatusFile = new File( UploadProcessingStatusFileConstants.STATUS_FILENAME );
		} else {
			mainStatusFile = new File( subDir, UploadProcessingStatusFileConstants.STATUS_FILENAME );
		}
		
		String status = null;
		
		try ( BufferedReader reader = new BufferedReader( new InputStreamReader(new FileInputStream( mainStatusFile ), StandardCharsets.UTF_8 ) ) ) {
			status = reader.readLine();
		} catch ( Exception e ) {
			String msg = "Failed to read status from file: " + mainStatusFile.getAbsolutePath();
			log.error( msg );
			throw new Exception(msg);
		}
		
		return status;
	}
}
