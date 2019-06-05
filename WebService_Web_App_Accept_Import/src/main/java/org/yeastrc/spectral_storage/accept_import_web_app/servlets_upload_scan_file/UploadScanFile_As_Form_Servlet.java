package org.yeastrc.spectral_storage.accept_import_web_app.servlets_upload_scan_file;

//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
//import javax.servlet.ServletConfig;
//import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.commons.fileupload.FileItem;
//import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
//import org.apache.commons.fileupload.disk.DiskFileItemFactory;
//import org.apache.commons.fileupload.servlet.ServletFileUpload;
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.output.FileWriterWithEncoding;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
//import org.yeastrc.spectral_storage.shared_server_client.constants.WebserviceSpectralStorageScanFileAllowedSuffixesConstants;
//import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.UploadScanFile_Response;
//import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.a_upload_processing_status_file.UploadProcessingWriteOrUpdateStatusFile;
//import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.ScanFileToProcessConstants;
//import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessingStatusFileConstants;
//import org.yeastrc.spectral_storage.web_app.background_thread.ProcessScanFileThread;
//import org.yeastrc.spectral_storage.web_app.constants_enums.FileUploadConstants;
//import org.yeastrc.spectral_storage.web_app.constants_enums.ServetResponseFormatEnum;
//import org.yeastrc.spectral_storage.web_app.servlets_common.Get_ServletResultDataFormat_FromServletInitParam;
//import org.yeastrc.spectral_storage.web_app.servlets_common.WriteResponseObjectToOutputStream;
//import org.yeastrc.spectral_storage.web_app.upload_scan_file.CreateProcessScanFileDir;
//import org.yeastrc.spectral_storage.web_app.upload_scan_file.CreateTempDirToUploadScanFileTo;


/**
 * Receives Uploaded scan file in HTTP Form - NOT currently used, IS out of date
 * 
 *  This Servlet should be considered a webservice as it returns JSON or XML
 */
public class UploadScanFile_As_Form_Servlet extends HttpServlet {

//	private static final Logger log = LoggerFactory.getLogger( UploadScanFile_As_Form_Servlet.class );
//
//	private static final long serialVersionUID = 1L;
//	
//	private ServetResponseFormatEnum servetResponseFormat;
//	
//	/* (non-Javadoc)
//	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
//	 */
//	@Override
//	public void init(ServletConfig config)
//	          throws ServletException {
//		
//		super.init(config); //  Must call this first
//
//		servetResponseFormat = 
//				Get_ServletResultDataFormat_FromServletInitParam.getInstance()
//				.get_ServletResultDataFormat_FromServletInitParam( config );
//
//		log.warn( "INFO: servetResponseFormat: " + servetResponseFormat );
//		
//	}
//	
//	/* (non-Javadoc)
//	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
//	 */
//	@Override
//	protected void doPost(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//
//		
//		log.warn( "INFO:  doPost called");
//		
//		//  For multipart forms (which is what is passed to this servlet), 
//		//  	request.getParameter(...) only comes from the query string
////		String uploadType = request.getParameter( "uploadTypeQueryString" ); 
//		
//		File uploadedFileOnDisk = null;
//		
//		String scanFilenameToProcess = null;
//
//
//		try {
//			String requestURL = request.getRequestURL().toString();
//			
////			String filename = request.getParameter( "filename" );
////
////			if ( StringUtils.isEmpty( filename ) ) {
////				log.error( "'filename' query parameter is not sent or is empty" );
////				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
////				throw new FailResponseSentException();
////			}
//
//			File uploadFileTempDir = CreateTempDirToUploadScanFileTo.getInstance().createTempDirToUploadScanFileTo();
//			
//			DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
////					
////					In DiskFileItemFactory, if temp directory is not specified, it uses 
////					tempDir = new File(System.getProperty("java.io.tmpdir"));
////					
////					which on one Tomcat installation is
////					/data/webtools/apache-tomcat-7.0.53/temp
//
//			if ( log.isInfoEnabled() ) {
//				log.info( "DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD: " + DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD );
//			}
//
//			if ( diskFileItemFactory.getRepository() == null ) {
//
//				if ( log.isInfoEnabled() ) {
//					log.info( "diskFileItemFactory.getRepository() == null" );
//				}
//			} else {
//				if ( log.isInfoEnabled() ) {
//					log.info( "diskFileItemFactory.getRepository().getAbsolutePath(): '" 
//							+ diskFileItemFactory.getRepository().getAbsolutePath() + "'" );
//				}
//			}
//			if ( log.isInfoEnabled() ) {
//				log.info( "diskFileItemFactory.getSizeThreshold(): '" 
//						+ diskFileItemFactory.getSizeThreshold() + "'" );
//			}
//			
//			File diskFileItemFactoryRepository = uploadFileTempDir; // Put diskFileItemFactory temp files in subdirectory directory
//			
//			diskFileItemFactory.setRepository( diskFileItemFactoryRepository );
//
//			ServletFileUpload servletFileUpload = new ServletFileUpload( diskFileItemFactory );
//			
//		       // file upload size limit
//			servletFileUpload.setFileSizeMax( FileUploadConstants.MAX_FILE_UPLOAD_SIZE );
//						
//			int filesUploadedCount = 0;
//			
//			List<FileItem> multiparts = servletFileUpload.parseRequest( request );
//
//			if ( log.isInfoEnabled() ) {
//				log.info( "multiparts size " + multiparts.size() );
//			}
//
//			for ( FileItem item : multiparts ) {
//				
//			    if ( item.isFormField() ) {
//			    	
//			    	//  form field that is NOT a file upload
//			    	
//			    	// No other form fields
//			    	
////			    	String fieldName = item.getFieldName();
////			    	String fieldValue = item.getString();
////			    	
////			    	if ( "fastaDescription".equals( fieldName ) ) {
////			    		fastaDescription = fieldValue;
////			    	}
//			        
//			    } else {
//			    	
//			    	//  form field that IS a file upload
//			    	
//					String fieldName = item.getFieldName();
//					
//					if ( ! FileUploadConstants.UPLOAD_SCAN_FILE_FIELD_NAME.equals( fieldName ) ) {
//						
//						//  Field name is not the expected field name so error
//						
//						log.error( "File uploaded using field name other than allowed field name. " 
//								+ "Allowed field name: " + FileUploadConstants.UPLOAD_SCAN_FILE_FIELD_NAME
//								+ ", received field name: " + fieldName );
//						
//						response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
//
//						UploadScanFile_Response uploadResponse = new UploadScanFile_Response();
//						uploadResponse.setStatusSuccess(false);
//						uploadResponse.setUploadFile_fieldNameInvalid(true);
//
//						WriteResponseObjectToOutputStream.getSingletonInstance()
//						.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
//
//						throw new FailResponseSentException();
//					}
//					
//					filesUploadedCount++;
//
//					//  Only allow one file to be uploaded in the request
//					
//					if ( filesUploadedCount > 1 ) {
//						
//						log.error( "More than one file uploaded in the request." );
//						
//						response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
//						
//						UploadScanFile_Response uploadResponse = new UploadScanFile_Response();
//						uploadResponse.setStatusSuccess(false);
//						uploadResponse.setMoreThanOneuploadedFile( true );
//
//						WriteResponseObjectToOutputStream.getSingletonInstance()
//						.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
//						
//						throw new FailResponseSentException();
//					}
//					
//				    String fileNameFromFormObjectFileUploadItem = item.getName();
//				    String contentType = item.getContentType();
//				    boolean isInMemory = item.isInMemory();
//				    long sizeInBytes = item.getSize();
//				    
//				    if ( StringUtils.isEmpty( fileNameFromFormObjectFileUploadItem ) ) {
//
//						log.error( "No Filename provided in file upload item." );
//						
//						response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
//						
//						UploadScanFile_Response uploadResponse = new UploadScanFile_Response();
//						uploadResponse.setStatusSuccess(false);
//						uploadResponse.setUploadedFileHasNoFilename( true );
//
//						WriteResponseObjectToOutputStream.getSingletonInstance()
//						.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
//						
//						throw new FailResponseSentException();
//				    }
//				    
//				    String filename = fileNameFromFormObjectFileUploadItem;  // re-assign filename to the filename from the form
//				    
//				    if ( log.isInfoEnabled() ) {
//				    	log.info( "started Upload for filename " + filename );
//
//				    	log.info( "item.getSize(): " + item.getSize() );
//				    }
//				    
//					String tempFilename = FileUploadConstants.UPLOAD_SCAN_FILE_TEMP_FILENAME_PREFIX;
//					
//					scanFilenameToProcess = ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX;
//
//				    if ( filename.endsWith( WebserviceSpectralStorageScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML ) ) {
//				    	
//				    	tempFilename += WebserviceSpectralStorageScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML;
//				    	scanFilenameToProcess += WebserviceSpectralStorageScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML;
//				    
//				    } else if ( filename.endsWith( WebserviceSpectralStorageScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML ) ) {
//
//				    	tempFilename += WebserviceSpectralStorageScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML;
//				    	scanFilenameToProcess += WebserviceSpectralStorageScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML;
//				    	
//				    } else {
//
//						log.error( "Filename provided does NOT have valid suffix: " + filename );
//						
//						response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
//						
//						UploadScanFile_Response uploadResponse = new UploadScanFile_Response();
//						uploadResponse.setStatusSuccess(false);
//						uploadResponse.setUploadedFileSuffixNotValid( true );
//
//						WriteResponseObjectToOutputStream.getSingletonInstance()
//						.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
//						
//						throw new FailResponseSentException();
//				    }
//					
//					File tempDir = uploadFileTempDir;
//
//					uploadedFileOnDisk = new File( tempDir, tempFilename );
//					
//					item.write( uploadedFileOnDisk );
//					
////					uploadedFilenameContainerFile = new File( tempDir, ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_UPLOADED_FILENAME_FILE_FILENAME );
////					
////					try ( BufferedWriter writer = new BufferedWriter( new FileWriterWithEncoding( uploadedFilenameContainerFile, StandardCharsets.UTF_8 ) ) ) {
////						writer.write( filename );
////					} catch ( Exception e ) {
////						String msg = "Failed to write incoming filename to file: " + uploadedFilenameContainerFile.getAbsolutePath();
////						log.error( msg );
////						throw new Exception(msg);
////					}
//
//					if ( log.isInfoEnabled() ) {
//						log.info( "Completed transfer to server for user uploaded filename " + filename );
//					}
//				}
//
//			}
//
//			
//			if ( uploadedFileOnDisk == null ) {
//				
//				log.error( "No file uploaded." );
//				
//				response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
//				
//				UploadScanFile_Response uploadResponse = new UploadScanFile_Response();
//				uploadResponse.setStatusSuccess(false);
//				uploadResponse.setNoUploadedFile(true);
//				
//				WriteResponseObjectToOutputStream.getSingletonInstance()
//				.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
//				
//				throw new FailResponseSentException();
//			}
//			
//			
//			
//			
//			File dirToProcessScanFile =
//					CreateProcessScanFileDir.getInstance().createDirToProcessScanFile();
//			
//			String scanProcessStatusKey = dirToProcessScanFile.getName();
//			
//
//			///   move the uploaded file into work dir.
//			{
//				File uploadedFile_In_dirForProcessingScan = new File( dirToProcessScanFile, scanFilenameToProcess );
//
//				try {
//					FileUtils.moveFile( uploadedFileOnDisk, uploadedFile_In_dirForProcessingScan );
//
//				} catch ( Exception e ) {
//
//					String msg = "Failed to move uploaded file to dirToProcessScanFile.  Src file: " + uploadedFileOnDisk.getAbsolutePath()
//					+ ", dest file: " + uploadedFile_In_dirForProcessingScan.getAbsolutePath();
//					log.error( msg, e );
//					throw new Exception(msg, e);
//				}
//			}
//			
//			//  Empty temp upload directory uploadFileTempDir
//			
//			try {
//				if ( ! uploadFileTempDir.delete() ) {
//					String msg = "Failed to delete temp dir for uploaded file, uploadFileTempDir: " + uploadFileTempDir.getAbsolutePath();
//					log.error( msg );
//					throw new Exception( msg );
//				}
//			} catch ( Exception e ) {
//				String msg = "Failed to delete temp dir for uploaded file, uploadFileTempDir: " + uploadFileTempDir.getAbsolutePath();
//				log.error( msg, e );
//				throw new Exception(msg, e);
//			}
//			
//			// Key returned to client
//			
//			{
//				File scanProcessStatusKeyFile = new File( dirToProcessScanFile, ScanFileToProcessConstants.SCAN_PROCESS_STATUS_KEY_FILENAME );
//
//				try ( BufferedWriter writer = new BufferedWriter( new FileWriterWithEncoding( scanProcessStatusKeyFile, StandardCharsets.UTF_8 ) ) ) {
//					writer.write( scanProcessStatusKey );
//				} catch ( Exception e ) {
//					String msg = "Failed to write scanProcessStatusKey to file: " + scanProcessStatusKeyFile.getAbsolutePath();
//					log.error( msg );
//					throw new Exception(msg);
//				}
//			}
//			
//			
//			try {
//				//  Create status file for pending
//				UploadProcessingWriteOrUpdateStatusFile.getInstance()
//				.uploadProcessingWriteOrUpdateStatusFile( UploadProcessingStatusFileConstants.STATUS_PENDING, dirToProcessScanFile );
//			} catch ( Exception e ) {
//				String msg = "Failed to create status file, dirToProcessScanFile: " + dirToProcessScanFile.getAbsolutePath();
//				log.error( msg, e );
//				throw new Exception(msg, e);
//			}
//
//			ProcessScanFileThread.getInstance().awaken();
//			
//			UploadScanFile_Response uploadResponse = new UploadScanFile_Response();
//			
//			uploadResponse.setStatusSuccess(true);
//			
//			uploadResponse.setScanProcessStatusKey( scanProcessStatusKey );
//
//			WriteResponseObjectToOutputStream.getSingletonInstance()
//			.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
//			
//			log.info( "Completed processing Upload");
//			
//		} catch ( FailResponseSentException e ) {
//			
//			cleanupOnError( uploadedFileOnDisk );
//			
//		} catch (FileSizeLimitExceededException ex ) {
//
//			long actualSize = ex.getActualSize();
//			
//			long permittedSize = ex.getPermittedSize();
//			
//			log.error( "FileSizeLimitExceededException: " + ex.toString(), ex );
//			
//			response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
//			
//			UploadScanFile_Response uploadResponse = new UploadScanFile_Response();
//			
//			uploadResponse.setStatusSuccess(false);
//			
//			uploadResponse.setFileSizeLimitExceeded(true);
//			uploadResponse.setMaxSize( ex.getPermittedSize() );
//			uploadResponse.setMaxSizeFormatted( FileUploadConstants.MAX_FILE_UPLOAD_SIZE_FORMATTED );
//
//			try {
//				WriteResponseObjectToOutputStream.getSingletonInstance()
//				.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
//			} catch ( Exception e ) {
//				throw new ServletException( e );
//			} finally {
//				cleanupOnError( uploadedFileOnDisk );
//			}
//			
//			//  response.sendError  sends a HTML page so don't use here since return JSON instead
//			
////			response.sendError( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );
////			response.sendError( HttpServletResponse.SC_BAD_REQUEST /* 400  */, responseJSONString );
////			throw new ServletException( "SizeLimitExceeded: ", ex );
//
//		} catch (Throwable ex){
//
//			log.error( "Exception: " + ex.toString(), ex );
//
//			response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR /* 500  */ );
//
//			UploadScanFile_Response uploadResponse = new UploadScanFile_Response();
//			uploadResponse.setStatusSuccess(false);
//			
//			try {
//				WriteResponseObjectToOutputStream.getSingletonInstance()
//				.writeResponseObjectToOutputStream( uploadResponse, servetResponseFormat, response );
//			} catch ( Exception e ) {
//				throw new ServletException( e );
//			} finally {
//				cleanupOnError( uploadedFileOnDisk );
//			}
//			
//			//  response.sendError  sends a HTML page so don't use here since return JSON instead
//			
////			response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR /* 500  */ );
//			
////			response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR /* 500  */, responseJSONString );
//			
////			throw new ServletException( ex );
//		}
//
//	}
//	
//
//	/**
//	 * @param uploadedFileOnDisk
//	 */
//	private void cleanupOnError( File uploadedFileOnDisk ) {
//		
//		if ( uploadedFileOnDisk != null && uploadedFileOnDisk.exists() ) {
//			uploadedFileOnDisk.delete();
//		}
//		
//	}
//	
//	
//	/**
//	 * 
//	 *
//	 */
//	private static class FailResponseSentException extends Exception {
//
//		private static final long serialVersionUID = 1L;
//	}
}
