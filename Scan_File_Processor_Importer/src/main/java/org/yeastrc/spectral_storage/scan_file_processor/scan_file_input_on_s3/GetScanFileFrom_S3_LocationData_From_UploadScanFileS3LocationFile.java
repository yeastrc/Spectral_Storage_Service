package org.yeastrc.spectral_storage.scan_file_processor.scan_file_input_on_s3;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.yeastrc.spectral_storage.shared_server_importer.create__xml_input_factory__xxe_safe.Create_XMLInputFactory_XXE_Safe;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessing_InputScanfileS3InfoConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.upload_scanfile_s3_location.UploadScanfileS3Location;

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
public class GetScanFileFrom_S3_LocationData_From_UploadScanFileS3LocationFile {

	private static final Logger log = LoggerFactory.getLogger( GetScanFileFrom_S3_LocationData_From_UploadScanFileS3LocationFile.class );
			
	private GetScanFileFrom_S3_LocationData_From_UploadScanFileS3LocationFile() {}
	public static GetScanFileFrom_S3_LocationData_From_UploadScanFileS3LocationFile getInstance() {
		return new GetScanFileFrom_S3_LocationData_From_UploadScanFileS3LocationFile();
	}
	
	/**
	 * @return
	 * @throws Exception 
	 */
	public UploadScanfileS3Location getScanFileFrom_S3_LocationData_From_UploadScanFileS3LocationFile() throws Exception {
		
		File scanFile_S3_LocationFile = new File( UploadProcessing_InputScanfileS3InfoConstants.SCANFILE_S3_LOCATION_FILENAME );
		
		if ( ! scanFile_S3_LocationFile.exists() ) {
			//  No file with info on scan file location in S3, so must be local file
			return null;  //  EARLY EXIT
		}
		
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

		if ( StringUtils.isEmpty( uploadScanfileS3Location.getS3_bucketName() ) ) {
			String msg = "s3_bucketName is empty in " + scanFile_S3_LocationFile.getAbsolutePath();
			log.error( msg );
			throw new SpectralStorageProcessingException(msg);
		}
		if ( StringUtils.isEmpty( uploadScanfileS3Location.getS3_objectName() ) ) {
			String msg = "s3_objectName is empty in " + scanFile_S3_LocationFile.getAbsolutePath();
			log.error( msg );
			throw new SpectralStorageProcessingException(msg);
		}

		return uploadScanfileS3Location;
	}
	
}
