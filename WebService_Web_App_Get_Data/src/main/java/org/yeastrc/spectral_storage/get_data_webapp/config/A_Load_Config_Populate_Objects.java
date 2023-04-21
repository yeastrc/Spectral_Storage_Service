package org.yeastrc.spectral_storage.get_data_webapp.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileWebappConfigException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3_Builder;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3_Holder;

public class A_Load_Config_Populate_Objects {

	private static final Logger log = LoggerFactory.getLogger(A_Load_Config_Populate_Objects.class);

	//  private constructor
	private A_Load_Config_Populate_Objects() { }
	
	/**
	 * @return newly created instance
	 */
	public static A_Load_Config_Populate_Objects getInstance() { 
		return new A_Load_Config_Populate_Objects(); 
	}
	
	/**
	 * @throws Exception
	 */
	public void load_Config() throws Exception {
		
		ConfigDataInWebApp_Reader.getInstance().readConfigDataInWebApp();
		ConfigData_Allowed_Remotes_InWorkDirectory_Reader.getInstance().readConfigDataInWebApp();
		ConfigData_ScanDataLocation_InWorkDirectory_Reader.getInstance().readConfigDataInWebApp();

		try {
			ConfigData_ScanDataLocation_InWorkDirectory configData_ScanDataLocation_InWorkDirectory = ConfigData_ScanDataLocation_InWorkDirectory.getSingletonInstance();
			CommonReader_File_And_S3_Builder commonReader_File_And_S3_Builder = CommonReader_File_And_S3_Builder.newBuilder();

			if ( StringUtils.isNotEmpty( configData_ScanDataLocation_InWorkDirectory.getS3Bucket() ) ) {
				commonReader_File_And_S3_Builder.setS3_Bucket( configData_ScanDataLocation_InWorkDirectory.getS3Bucket() );
				if ( StringUtils.isNotEmpty( configData_ScanDataLocation_InWorkDirectory.getS3Region() ) ) {
					commonReader_File_And_S3_Builder.setS3_Region( configData_ScanDataLocation_InWorkDirectory.getS3Region() );
				}
			} else if ( configData_ScanDataLocation_InWorkDirectory.getScanStorageBaseDirectory() != null ) {
				commonReader_File_And_S3_Builder.setSubDirForStorageFiles( configData_ScanDataLocation_InWorkDirectory.getScanStorageBaseDirectory() );
			} else {
				String msg = "Scan Storage location in config invalid.  Must be directory or S3 bucket";
				log.error( msg );
				throw new SpectralFileWebappConfigException(msg);
			}
			
			CommonReader_File_And_S3_Holder.getSingletonInstance()
			.setCommonReader_File_And_S3( commonReader_File_And_S3_Builder.build() );
		} catch (Exception e) {
			String msg = "config probably invalid";
			log.error( msg, e );
			throw e;
		} 
		
		
	}
}
