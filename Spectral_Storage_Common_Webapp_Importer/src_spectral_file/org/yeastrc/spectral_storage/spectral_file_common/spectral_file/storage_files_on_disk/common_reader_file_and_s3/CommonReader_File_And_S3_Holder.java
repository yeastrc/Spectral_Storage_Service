package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3;

/**
 * Holds instance of CommonReader_File_And_S3
 *
 * Singleton
 */
public class CommonReader_File_And_S3_Holder {

	private static CommonReader_File_And_S3_Holder instance = new CommonReader_File_And_S3_Holder();
	
	private CommonReader_File_And_S3_Holder() {}
	public static CommonReader_File_And_S3_Holder getSingletonInstance() {
		return instance;
	}
	
	private CommonReader_File_And_S3 commonReader_File_And_S3;

	public CommonReader_File_And_S3 getCommonReader_File_And_S3() {
		if ( commonReader_File_And_S3 == null ) {
			throw new IllegalStateException( "Not set: commonReader_File_And_S3" );
		}
		return commonReader_File_And_S3;
	}
	public void setCommonReader_File_And_S3(CommonReader_File_And_S3 commonReader_File_And_S3) {
		this.commonReader_File_And_S3 = commonReader_File_And_S3;
	}
}
