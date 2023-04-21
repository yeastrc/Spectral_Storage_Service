package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.s3_aws_interface;

import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * Singleton Instance
 *
 */
public class S3_AWS_InterfaceObjectHolder {

	private static final Logger log = LoggerFactory.getLogger( S3_AWS_InterfaceObjectHolder.class );
			
	private S3_AWS_InterfaceObjectHolder() {}
	public static S3_AWS_InterfaceObjectHolder getSingletonInstance() {
		return instance;
	}
	
	private final static S3_AWS_InterfaceObjectHolder instance = new S3_AWS_InterfaceObjectHolder();
	
	private AmazonS3 s3_Client_Output;
	private boolean initialized = false;

	public void setS3_OutputRegion( String region ) {
		s3_Client_Output = AmazonS3ClientBuilder.standard().withRegion( region ).build(); // "us-west-2"
	}

	public void init() {
		if ( s3_Client_Output == null ) {
			s3_Client_Output = AmazonS3ClientBuilder.defaultClient();
		}
		initialized = true;
	}

	/**
	 * @return
	 */
	public AmazonS3 getS3_Client_Output() {
		if ( ! initialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		return s3_Client_Output;
	}

	/**
	 * @param region_Optional
	 * @return
	 */
	public AmazonS3 getS3_Client_PassInOptionalRegion( String region_Optional ) {
		
		AmazonS3 s3_Client = null;
		
		if ( StringUtils.isNotEmpty( region_Optional ) ) {
		
			s3_Client = AmazonS3ClientBuilder.standard().withRegion( region_Optional ).build(); // ie: "us-west-2"
		} else {
			s3_Client = AmazonS3ClientBuilder.defaultClient();
		}
		
		return s3_Client;
	}
	
}
