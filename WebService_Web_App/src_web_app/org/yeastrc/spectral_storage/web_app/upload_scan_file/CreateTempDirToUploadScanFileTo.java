package org.yeastrc.spectral_storage.web_app.upload_scan_file;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.web_app.constants_enums.FileUploadConstants;
import org.yeastrc.spectral_storage.web_app.exceptions.SpectralFileFileUploadFileSystemException;

/**
 * 
 *
 */
public class CreateTempDirToUploadScanFileTo {

	private static final Logger log = Logger.getLogger(CreateTempDirToUploadScanFileTo.class);

	//  private constructor
	private CreateTempDirToUploadScanFileTo() { }
	
	/**
	 * @return newly created instance
	 */
	public static CreateTempDirToUploadScanFileTo getInstance() { 
		return new CreateTempDirToUploadScanFileTo(); 
	}
	
	/**
	 * @return
	 * @throws SpectralFileFileUploadFileSystemException 
	 * @throws IOException 
	 */
	public File createTempDirToUploadScanFileTo() throws SpectralFileFileUploadFileSystemException, IOException {
		
		File tempUploadBaseDir = ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getTempScanUploadBaseDirectory();
		
		//  Get the File object for the Base Subdir used to first store the files in this request 
		String uploadFileTempDirString = FileUploadConstants.UPLOAD_FILE_TEMP_BASE_DIR;
		File uploadFileTempDir = new File( tempUploadBaseDir, uploadFileTempDirString );
		if ( ! uploadFileTempDir.exists() ) {
//			boolean mkdirResult = 
			uploadFileTempDir.mkdir();
		}
		if ( ! uploadFileTempDir.exists() ) {
			String msg = "uploadFileTempDir does not exist after testing for it and attempting to create it.  uploadFileTempDir: " 
					+ uploadFileTempDir.getAbsolutePath();
			log.error( msg );
			throw new SpectralFileFileUploadFileSystemException(msg);
		}
		
		//  Create subdir for this specific file upload
		
		long uploadKey = System.currentTimeMillis();
		File createdSubDir = null;
		int retryCreateSubdirCount = 0;
		while ( createdSubDir == null ) {
			retryCreateSubdirCount++;
			if ( retryCreateSubdirCount > 4 ) {
				String msg = "Failed to create subdir after 4 attempts.";
				log.error( msg );
				throw new SpectralFileFileUploadFileSystemException(msg);
			}
			int uploadKeyIncrement = ( (int) ( Math.random() * 10 ) ) + 5;
			uploadKey += uploadKeyIncrement;
			createdSubDir =
					createSubDirForUploadFileTempDir( uploadKey, uploadFileTempDir );
		}
		
		return createdSubDir;
	}
	

	/**
	 * @param uploadKey
	 * @param uploadTempBase
	 * @return null if subdir already exists
	 * @throws SpectralFileFileUploadFileSystemException 
	 * @throws IOException 
	 */
	private File createSubDirForUploadFileTempDir( long uploadKey, File uploadTempBase ) throws SpectralFileFileUploadFileSystemException, IOException {
		
		File subdir = getSubDirForUploadFileTempDir( uploadKey, uploadTempBase );
		if ( subdir.exists() ) {
			//  Subdir already exists so need new uploadKey to create unique subdir
			return null;
		}
		if ( ! subdir.mkdir() ) {
			String msg = "Failed to make temp upload subdir: " + subdir.getCanonicalPath();
			log.error( msg );
			throw new SpectralFileFileUploadFileSystemException( msg );
		}
		return subdir;
	}

	/**
	 * @param uploadKey
	 * @param uploadTempBase
	 * @return
	 * @throws ProxlWebappFileUploadFileSystemException 
	 * @throws IOException 
	 */
	private File getSubDirForUploadFileTempDir( long uploadKey, File uploadTempBase ) {
		String subdirName = FileUploadConstants.UPLOAD_FILE_TEMP_SUB_DIR_PREFIX 
				+ uploadKey;
		File subdir = new File( uploadTempBase, subdirName );
		return subdir;
	}
	
}