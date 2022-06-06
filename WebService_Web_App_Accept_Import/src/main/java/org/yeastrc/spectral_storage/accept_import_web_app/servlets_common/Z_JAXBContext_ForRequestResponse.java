package org.yeastrc.spectral_storage.accept_import_web_app.servlets_common;

import javax.xml.bind.JAXBContext;

import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main.*;

/**
 * JAXB Context for requests and responses
 *
 */
public class Z_JAXBContext_ForRequestResponse {

	private static Z_JAXBContext_ForRequestResponse instance = null;

	//  private constructor
	private Z_JAXBContext_ForRequestResponse() { }
	
	/**
	 * @return Singleton instance
	 */
	public synchronized static Z_JAXBContext_ForRequestResponse getSingletonInstance() throws Exception {
		if ( instance == null ) {
			instance = new Z_JAXBContext_ForRequestResponse();
			instance.init();
		}
		return instance; 
	}
	
	
	private JAXBContext jaxbContext;

	
	/**
	 * @throws Exception
	 */
	private void init() throws Exception {

		jaxbContext = 
				JAXBContext.newInstance( 
						UploadScanFile_Init_Request.class,
						UploadScanFile_Init_Response.class,
						Get_Supported_ScanFilename_Suffixes_Request.class,
						Get_Supported_ScanFilename_Suffixes_Response.class,
						UploadScanFile_AddScanFileInS3Bucket_Request.class,
						UploadScanFile_AddScanFileInS3Bucket_Response.class,
						UploadScanFile_AddScanFileFromFilenameAndPath_Request.class,
						UploadScanFile_AddScanFileFromFilenameAndPath_Response.class,
						UploadScanFile_UploadScanFile_Response.class,
						UploadScanFile_Submit_Request.class,
						UploadScanFile_Submit_Response.class,
						Get_UploadedScanFileInfo_Request.class, 
						Get_UploadedScanFileInfo_Response.class,
						UploadScanFile_Delete_For_ScanProcessStatusKey_Request.class,
						UploadScanFile_Delete_For_ScanProcessStatusKey_Response.class
						);
	}
	
	/**
	 * @return
	 */
	public JAXBContext getJAXBContext() {
		return jaxbContext;
	}
}
