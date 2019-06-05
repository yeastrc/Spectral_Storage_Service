package org.yeastrc.spectral_storage.scan_file_processor.process_scan_file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.shared_server_importer.create__xml_input_factory__xxe_safe.Create_XMLInputFactory_XXE_Safe;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessing_InputScanfileS3InfoConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.s3_aws_interface.S3_AWS_InterfaceObjectHolder;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.upload_scanfile_s3_location.UploadScanfileS3Location;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;

/**
 * If have info on file in S3, 
 *   Validate that object name ends with ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML
 *     or ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML
 * 
 * Then copy it to local dir with name prefix 
 *   ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX
 *   
 * and name suffix ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML
 *              or ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML
 *
 */
public class GetScanFileFrom_S3_IfHave_S3_Info_File {

	private static final Logger log = LoggerFactory.getLogger( GetScanFileFrom_S3_IfHave_S3_Info_File.class );
			
	private GetScanFileFrom_S3_IfHave_S3_Info_File() {}
	public static GetScanFileFrom_S3_IfHave_S3_Info_File getInstance() {
		return new GetScanFileFrom_S3_IfHave_S3_Info_File();
	}
	
	/**
	 * @return
	 * @throws Exception 
	 */
	public void getScanFileFrom_S3_IfHave_S3_Info_File() throws Exception {
		
		File scanFile_S3_LocationFile = new File( UploadProcessing_InputScanfileS3InfoConstants.SCANFILE_S3_LOCATION_FILENAME );
		
		if ( ! scanFile_S3_LocationFile.exists() ) {
			//  No file with info on scan file location in S3, so must be local file
			return;  //  EARLY EXIT
		}
		
		//  Remove any existing scan files since will copy it from S3
		cleanLocalDirOfScanFiles();
		
		UploadScanfileS3Location uploadScanfileS3Location = null;
		
		JAXBContext jaxbContext = JAXBContext.newInstance( UploadScanfileS3Location.class ); 
		
		//  Test code to create a file
//		uploadScanfileS3Location = new UploadScanfileS3Location();
//		uploadScanfileS3Location.setS3_bucketName( "spl_bkt" );
//		uploadScanfileS3Location.setS3_objectName( "scan_fileInput.mzML" );
//		
//		try ( OutputStream os = new FileOutputStream(scanFile_S3_LocationFile) ) {
//			Marshaller marshaller = jaxbContext.createMarshaller();
//			marshaller.marshal( uploadScanfileS3Location, os );
//		}
		
		try ( InputStream is = new FileInputStream( scanFile_S3_LocationFile ) ) {

			XMLInputFactory xmlInputFactory = Create_XMLInputFactory_XXE_Safe.create_XMLInputFactory_XXE_Safe();
			XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader( new StreamSource( is ) );
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			Object uploadScanfileS3LocationAsObject = unmarshaller.unmarshal( xmlStreamReader );

			if ( uploadScanfileS3LocationAsObject instanceof UploadScanfileS3Location ) {
				uploadScanfileS3Location = ( UploadScanfileS3Location ) uploadScanfileS3LocationAsObject;
			} else {
				String msg = "Failed to deserialize data in " + scanFile_S3_LocationFile.getAbsolutePath();
				log.error( msg );
				throw new SpectralStorageProcessingException(msg);
			}
		}
		
		String s3_bucketName = uploadScanfileS3Location.getS3_bucketName();
		String s3_objectName = uploadScanfileS3Location.getS3_objectName();
		
		if ( StringUtils.isEmpty( s3_bucketName ) ) {
			String msg = "s3_bucketName is empty in " + scanFile_S3_LocationFile.getAbsolutePath();
			log.error( msg );
			throw new SpectralStorageProcessingException(msg);
		}
		if ( StringUtils.isEmpty( s3_objectName ) ) {
			String msg = "s3_objectName is empty in " + scanFile_S3_LocationFile.getAbsolutePath();
			log.error( msg );
			throw new SpectralStorageProcessingException(msg);
		}

		String objectName_Suffix = null;

		if ( s3_objectName.endsWith( ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML ) ) {
			objectName_Suffix = ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML;
		} else if ( s3_objectName.endsWith( ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML ) ) {
			objectName_Suffix = ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML;
		} else {
			String msg = "s3_objectName must end in '" 
					+ ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML
					+ "' or '"
					+ ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML
					+ "'.  s3_objectName: " + s3_objectName;
			log.error( msg );
			throw new SpectralStorageProcessingException(msg);
		}
		
		String localScanFilename = ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX
				+ objectName_Suffix;
		
		final AmazonS3 s3 = S3_AWS_InterfaceObjectHolder.getSingletonInstance().getS3_Client_Input();
		try {
	    	try ( FileOutputStream fos = new FileOutputStream( new File( localScanFilename ) ) ) {
	    		S3Object s3Object = s3.getObject( s3_bucketName, s3_objectName );
	    		try ( InputStream isS3Object = s3Object.getObjectContent() ) {
	    			byte[] read_buf = new byte[ 8096 ];
	    			int read_len = 0;
	    			while ((read_len = isS3Object.read(read_buf)) > 0) {
	    				fos.write(read_buf, 0, read_len);
	    			}
	    		}
	    	}
		} catch (AmazonServiceException e) {
			String msg = "Error retrieving scan file from S3. s3_bucketName: " + s3_bucketName
					+ " s3_objectName: " + s3_objectName;
			log.error( msg );
			throw new SpectralStorageProcessingException(msg, e);
		} catch (FileNotFoundException e) {
			String msg = "Error retrieving scan file from S3. s3_bucketName: " + s3_bucketName
					+ " s3_objectName: " + s3_objectName;
			log.error( msg );
			throw new SpectralStorageProcessingException(msg, e);
		} catch (Exception e) {
			String msg = "Error retrieving scan file from S3. s3_bucketName: " + s3_bucketName
					+ " s3_objectName: " + s3_objectName;
			log.error( msg );
			throw new SpectralStorageProcessingException(msg, e);
		}
	}
	
	/**
	 * @throws SpectralStorageProcessingException
	 */
	private void cleanLocalDirOfScanFiles() throws SpectralStorageProcessingException {

		File currentDir = new File( "." );
		
		File[] dirContents = currentDir.listFiles();
		
		for ( File dirEntry : dirContents ) {
			String dirEntryFilename = dirEntry.getName();
			if ( dirEntryFilename.startsWith( ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX )
					&& ( dirEntryFilename.endsWith( ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML ) 
							|| dirEntryFilename.endsWith( ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML ) ) ) {
				if ( ! dirEntry.delete() ) {
					String msg = "Failed to delete file: " + dirEntry.getAbsolutePath();
					log.error(msg);
					throw new SpectralStorageProcessingException(msg);
				}
			}
		}
	}
}
