package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.a_upload_processing_marked_deleted_file;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessingMarkedDeletedFileConstants;

/**
 * Create or 
 *
 */
public class UploadProcessing_MarkedDeletedFile_Create_Check {

	private static final Logger log = Logger.getLogger(UploadProcessing_MarkedDeletedFile_Create_Check.class);
	/**
	 * private constructor
	 */
	private UploadProcessing_MarkedDeletedFile_Create_Check(){}
	public static UploadProcessing_MarkedDeletedFile_Create_Check getInstance( ) throws Exception {
		UploadProcessing_MarkedDeletedFile_Create_Check instance = new UploadProcessing_MarkedDeletedFile_Create_Check();
		return instance;
	}

	/**
	 * @param scanProcessStatusKeyDir
	 * @throws IOException 
	 */
	public void createMarkedDeleteFile( File scanProcessStatusKeyDir ) throws IOException {
		
		File markedDeletedFile = new File( scanProcessStatusKeyDir, UploadProcessingMarkedDeletedFileConstants.MARKED_DELETED_FILENAME );
		
		markedDeletedFile.createNewFile();
	}

	/**
	 * @param scanProcessStatusKeyDir
	 * @throws IOException 
	 */
	public boolean doesMarkedDeleteFileExist( File scanProcessStatusKeyDir ) throws IOException {
		
		File markedDeletedFile = new File( scanProcessStatusKeyDir, UploadProcessingMarkedDeletedFileConstants.MARKED_DELETED_FILENAME );
		
		if ( markedDeletedFile.exists() ) {
			return true;
		}
		return false;
	}
}
