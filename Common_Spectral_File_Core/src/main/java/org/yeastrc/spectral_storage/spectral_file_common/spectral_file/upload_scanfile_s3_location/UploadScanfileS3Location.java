package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.upload_scanfile_s3_location;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Object form of info passed from web app to importer for where in S3 the scan file to process is located.
 * 
 * Matching filename is in UploadProcessing_InputScanfileS3InfoConstants.SCANFILE_S3_LOCATION_FILENAME
 *
 */
@XmlRootElement(name="uploadScanfileS3Location")
@XmlAccessorType(XmlAccessType.FIELD)
public class UploadScanfileS3Location {

	/**
	 * Was the S3 info on the scan file location provided by a remote system.
	 * 
	 * If false, then a scan file was received and written to S3 by this Spectral Storage system.
	 */
	@XmlAttribute // attribute name is property name
	private boolean s3_infoFrom_RemoteSystem;
	
	/**
	 * Optional AWS region received from remote system
	 */
	@XmlAttribute // attribute name is property name
	private String s3_region;
	
	@XmlAttribute // attribute name is property name
	private String s3_bucketName;
	@XmlAttribute // attribute name is property name
	private String s3_objectName;

	
	/**
	 * "Scan Filename to process" : In quotes since it is fake set in this web app from passed in suffix
	 */
	@XmlAttribute // attribute name is property name
	private String scanFilenameToProcess;
	
	/**
	 * Copy this object
	 * @param item
	 * @return
	 */
	public UploadScanfileS3Location clone() {
		UploadScanfileS3Location clone = new UploadScanfileS3Location();
		clone.s3_region = this.s3_region;
		clone.scanFilenameToProcess = this.scanFilenameToProcess;
		clone.s3_bucketName = this.s3_bucketName;
		clone.s3_objectName = this.s3_objectName;
		return clone;
	}

	/**
	 * Was the S3 info on the scan file location provided by a remote system.
	 * 
	 * If false, then a scan file was received and written to S3 by this Spectral Storage system.
	 * @return
	 */
	public boolean isS3_infoFrom_RemoteSystem() {
		return s3_infoFrom_RemoteSystem;
	}

	/**
	 * Was the S3 info on the scan file location provided by a remote system.
	 * 
	 * If false, then a scan file was received and written to S3 by this Spectral Storage system.
	 * @param s3_infoFrom_RemoteSystem
	 */
	public void setS3_infoFrom_RemoteSystem(boolean s3_infoFrom_RemoteSystem) {
		this.s3_infoFrom_RemoteSystem = s3_infoFrom_RemoteSystem;
	}

	/**
	 * "Scan Filename to process" : In quotes since it is fake set in this web app from passed in suffix
	 * @return
	 */
	public String getScanFilenameToProcess() {
		return scanFilenameToProcess;
	}

	/**
	 * "Scan Filename to process" : In quotes since it is fake set in this web app from passed in suffix
	 * @param scanFilenameToProcess
	 */
	public void setScanFilenameToProcess(String scanFilenameToProcess) {
		this.scanFilenameToProcess = scanFilenameToProcess;
	}

	/**
	 * Optional AWS region received from remote system
	 * @return
	 */
	public String getS3_region() {
		return s3_region;
	}

	/**
	 * Optional AWS region received from remote system
	 * @param s3_region
	 */
	public void setS3_region(String s3_region) {
		this.s3_region = s3_region;
	}
	
	public String getS3_bucketName() {
		return s3_bucketName;
	}
	public void setS3_bucketName(String s3_bucketName) {
		this.s3_bucketName = s3_bucketName;
	}
	public String getS3_objectName() {
		return s3_objectName;
	}
	public void setS3_objectName(String s3_objectName) {
		this.s3_objectName = s3_objectName;
	}


}
