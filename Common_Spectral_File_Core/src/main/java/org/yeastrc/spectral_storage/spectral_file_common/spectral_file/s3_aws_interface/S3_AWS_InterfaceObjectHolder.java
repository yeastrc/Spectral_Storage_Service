package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.s3_aws_interface;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

//   AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.

/**
 *    AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.
 * 
 * 
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
	
	private AmazonS3 s3_Client_Input;
	private AmazonS3 s3_Client_Output;
	private boolean initialized = false;

	public void setS3_InputRegion( String region ) {
		s3_Client_Input = AmazonS3ClientBuilder.standard().withRegion( region ).build(); // "us-west-2"
	}
	public void setS3_OutputRegion( String region ) {
		s3_Client_Output = AmazonS3ClientBuilder.standard().withRegion( region ).build(); // "us-west-2"
	}

	public void init() {
		if ( s3_Client_Input == null ) {
			s3_Client_Input = AmazonS3ClientBuilder.defaultClient();
		}
		if ( s3_Client_Output == null ) {
			s3_Client_Output = AmazonS3ClientBuilder.defaultClient();
		}
		initialized = true;
	}
	public AmazonS3 getS3_Client_Input() {
		if ( ! initialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		return s3_Client_Input;
	}
	public AmazonS3 getS3_Client_Output() {
		if ( ! initialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		return s3_Client_Output;
	}
	
}
