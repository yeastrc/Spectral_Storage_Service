package org.yeastrc.spectral_storage.accept_import_web_app.amazon_s3_client_builder;

import org.apache.commons.lang3.StringUtils;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageConfigException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 *
 */
public class SpectralStorageWebappAmazonS3ClientBuilder {

	/**
	 * @return
	 */
	public static SpectralStorageWebappAmazonS3ClientBuilder newBuilder() {
		SpectralStorageWebappAmazonS3ClientBuilder spectralStorageWebappAmazonS3ClientBuilder = new SpectralStorageWebappAmazonS3ClientBuilder();
		return spectralStorageWebappAmazonS3ClientBuilder;
	}
	
	private String s3_Region;
	
	public SpectralStorageWebappAmazonS3ClientBuilder withRegion( String s3_Region ) {
		this.s3_Region = s3_Region;
		return this;
	}

	/**
	 * Final build
	 * @return
	 * @throws CommonReader_File_And_S3_Config_Exception 
	 */
	public AmazonS3 build() throws SpectralStorageConfigException {
		AmazonS3ClientBuilder amazonS3ClientBuilder = AmazonS3ClientBuilder.standard();
		if ( StringUtils.isNotEmpty( s3_Region ) ) {
			amazonS3ClientBuilder = amazonS3ClientBuilder.withRegion( s3_Region );
		} else if ( StringUtils.isNotEmpty( ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getS3Region() ) ) {
			amazonS3ClientBuilder = amazonS3ClientBuilder.withRegion( ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getS3Region() );
		}
		return amazonS3ClientBuilder.build();
	}

}
