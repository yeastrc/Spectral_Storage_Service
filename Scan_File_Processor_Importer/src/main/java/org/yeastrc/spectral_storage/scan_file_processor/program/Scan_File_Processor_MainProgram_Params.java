package org.yeastrc.spectral_storage.scan_file_processor.program;

import java.io.File;

/**
 * Command Line Params
 *
 */
public class Scan_File_Processor_MainProgram_Params {

	private File outputBaseDir;
	private File tempOutputBaseDir;
	private File backupOldBaseDir; 

	// AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.
	
	private String s3_OutputBucket;
	
	private boolean deleteScanFileOnSuccess;
	
	public File getOutputBaseDir() {
		return outputBaseDir;
	}
	public void setOutputBaseDir(File outputBaseDir) {
		this.outputBaseDir = outputBaseDir;
	}
	public String getS3_OutputBucket() {
		return s3_OutputBucket;
	}
	public void setS3_OutputBucket(String s3_OutputBucket) {
		this.s3_OutputBucket = s3_OutputBucket;
	}
	public boolean isDeleteScanFileOnSuccess() {
		return deleteScanFileOnSuccess;
	}
	public void setDeleteScanFileOnSuccess(boolean deleteScanFileOnSuccess) {
		this.deleteScanFileOnSuccess = deleteScanFileOnSuccess;
	}
	public File getBackupOldBaseDir() {
		return backupOldBaseDir;
	}
	public void setBackupOldBaseDir(File backupOldBaseDir) {
		this.backupOldBaseDir = backupOldBaseDir;
	}
	public File getTempOutputBaseDir() {
		return tempOutputBaseDir;
	}
	public void setTempOutputBaseDir(File tempOutputBaseDir) {
		this.tempOutputBaseDir = tempOutputBaseDir;
	}
}
