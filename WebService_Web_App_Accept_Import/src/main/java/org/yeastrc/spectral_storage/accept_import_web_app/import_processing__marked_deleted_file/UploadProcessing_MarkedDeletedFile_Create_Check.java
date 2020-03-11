package org.yeastrc.spectral_storage.accept_import_web_app.import_processing__marked_deleted_file;

import java.io.File;
import java.io.IOException;

import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.UploadProcessingMarkedDeletedFileConstants;
import org.slf4j.Logger;

/**
 * Create or 
 *
 */
public class UploadProcessing_MarkedDeletedFile_Create_Check {

	private static final Logger log = LoggerFactory.getLogger(UploadProcessing_MarkedDeletedFile_Create_Check.class);
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
