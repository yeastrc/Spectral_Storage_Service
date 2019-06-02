package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3;

import java.io.File;

import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageConfigException;

/**
 * Build instance of CommonReader_File_And_S3
 *
 */
public class CommonReader_File_And_S3_Builder {

	/**
	 * @return
	 */
	public static CommonReader_File_And_S3_Builder newBuilder() {
		CommonReader_File_And_S3_Builder commonReader_File_And_S3_Builder = new CommonReader_File_And_S3_Builder();
		commonReader_File_And_S3_Builder.commonReader_File_And_S3 = new CommonReader_File_And_S3();
		return commonReader_File_And_S3_Builder;
	}
	
	private CommonReader_File_And_S3 commonReader_File_And_S3;
	
	//  Setters of underlying commonReader_File_And_S3
	
	public CommonReader_File_And_S3_Builder setSubDirForStorageFiles(File subDirForStorageFiles) {
		commonReader_File_And_S3.setSubDirForStorageFiles( subDirForStorageFiles );
		return this;
	}
	public CommonReader_File_And_S3_Builder setS3_Bucket(String s3_Bucket) {
		commonReader_File_And_S3.setS3_Bucket( s3_Bucket );
		return this;
	}
	public CommonReader_File_And_S3_Builder setS3_Region(String s3_Region) {
		commonReader_File_And_S3.setS3_Region( s3_Region );
		return this;
	}

	/**
	 * Final build
	 * @return
	 * @throws CommonReader_File_And_S3_Config_Exception 
	 */
	public CommonReader_File_And_S3 build() throws SpectralStorageConfigException {
		commonReader_File_And_S3.init(); // First validate the commonReader_File_And_S3
		return commonReader_File_And_S3;
	}
	
	
}
