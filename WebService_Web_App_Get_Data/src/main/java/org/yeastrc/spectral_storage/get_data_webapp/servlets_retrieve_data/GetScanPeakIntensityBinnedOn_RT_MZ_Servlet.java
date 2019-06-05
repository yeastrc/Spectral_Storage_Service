package org.yeastrc.spectral_storage.get_data_webapp.servlets_retrieve_data;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileBadRequestToServletException;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileDeserializeRequestException;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.GetRequestObjectFromInputStream;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.WriteResponseStringToOutputStream;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanPeakIntensityBinnedOn_RT_MZ_Request;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataNotFoundException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_rt_mz_binned.ScanLevel_1_RT_MZ_Binned_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3_Holder;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.CreateSpectralStorageFilenames;

/**
 * Get Scan Peak intensity binned on Retention Time and m/z
 * for scanFileAPIKey (scan file hash code) 
 *   and  Retention Time bin size and m/z bin size
 *   
 * Request is in XML
 * Response is JSON byte[] that has been GZipped.
 *    Response is directly reading the file <api key>.intbin__<RT bin size>_<MZ bin size>  ie: .intbin__1_1
 *   
 * Retention Time bin size is in seconds
 * m/z bin size is in m/z
 * 
 * !!!  WARNING:  Currently ONLY bin size of 1 for both RT and MZ is accepted since that is all that can be computed with current code.
 *          Any other bin sizes will be rejected with 400 status code.
 * 
 * !!!  A file for bin size of 1 for both RT and MZ is created when the scan file is processed.
 *        If other bin sizes are supported, the file will need to be created on import or on request.
 *  
 *
 */
public class GetScanPeakIntensityBinnedOn_RT_MZ_Servlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger( GetScanPeakIntensityBinnedOn_RT_MZ_Servlet.class );

	private static final long serialVersionUID = 1L;

//	private ServetResponseFormatEnum servetResponseFormat;
	
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config)
	          throws ServletException {
		
		super.init(config); //  Must call this first

//		servetResponseFormat = 
//				Get_ServletResultDataFormat_FromServletInitParam.getInstance()
//				.get_ServletResultDataFormat_FromServletInitParam( config );
//
//		log.warn( "INFO: servetResponseFormat: " + servetResponseFormat );
		
	}
	
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Get_ScanPeakIntensityBinnedOn_RT_MZ_Request get_ScanPeakIntensityBinnedOn_RT_MZ_Request = null;

		try {
			Object requestObj = null;

			try {
				requestObj = GetRequestObjectFromInputStream.getSingletonInstance().getRequestObjectFromStream( request );
			} catch ( SpectralFileDeserializeRequestException e ) {
				throw e;
			} catch (Exception e) {
				String msg = "Failed to deserialize request";
				log.error( msg, e );
				throw new SpectralFileBadRequestToServletException( e );
			}

			try {
				get_ScanPeakIntensityBinnedOn_RT_MZ_Request = (Get_ScanPeakIntensityBinnedOn_RT_MZ_Request) requestObj;
			} catch (Exception e) {
				String msg = "Failed to cast requestObj to Get_ScanPeakIntensityBinnedOn_RT_MZ_Request";
				log.error( msg, e );
				throw new SpectralFileBadRequestToServletException( e );
			}
		} catch (SpectralFileBadRequestToServletException e) {

			response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );

			if ( StringUtils.isNotEmpty( e.getMessage() ) ) {
				WriteResponseStringToOutputStream.getInstance()
				.writeResponseStringToOutputStream( e.getMessage(), response);
			}
			
			return;

		} catch (Throwable e) {
			String msg = "Failed to process request";
			log.error( msg, e );
			response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR /* 500  */ );
			
			return;
		}
		
		processRequest( get_ScanPeakIntensityBinnedOn_RT_MZ_Request, request, response );
	}
	
	/**
	 * @param get_ScanPeakIntensityBinnedOn_RT_MZ_Request
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processRequest( 
			Get_ScanPeakIntensityBinnedOn_RT_MZ_Request get_ScanPeakIntensityBinnedOn_RT_MZ_Request,
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
	
		try {
			String scanFileAPIKey = get_ScanPeakIntensityBinnedOn_RT_MZ_Request.getScanFileAPIKey();

			Long retentionTimeBinSize = get_ScanPeakIntensityBinnedOn_RT_MZ_Request.getRetentionTimeBinSize();
			Long mzBinSize = get_ScanPeakIntensityBinnedOn_RT_MZ_Request.getMzBinSize();
			
			if ( StringUtils.isEmpty( scanFileAPIKey ) ) {
				String msg = "missing scanFileAPIKey ";
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}

			if ( retentionTimeBinSize == null ) {
				String msg = "missing retentionTimeBinSize ";
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}

			if ( mzBinSize == null ) {
				String msg = "missing mzBinSize ";
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}
			
			if ( retentionTimeBinSize != ScanLevel_1_RT_MZ_Binned_Constants.RETENTION_TIME_BIN_SIZE_IN_SECONDS_1 ) {
				String msg = "retentionTimeBinSize is not supported value, is: " 
						+ retentionTimeBinSize
						+ ", only supported value is: "
						+ ScanLevel_1_RT_MZ_Binned_Constants.RETENTION_TIME_BIN_SIZE_IN_SECONDS_1;
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}

			if ( mzBinSize != ScanLevel_1_RT_MZ_Binned_Constants.MZ_BIN_SIZE_IN_MZ_1 ) {
				String msg = "mzBinSize is not supported value, is: " 
						+ mzBinSize
						+ ", only supported value is: "
						+ ScanLevel_1_RT_MZ_Binned_Constants.MZ_BIN_SIZE_IN_MZ_1;
				log.warn( msg );
				throw new SpectralFileBadRequestToServletException( msg );
			}

			CommonReader_File_And_S3 commonReader_File_And_S3 = CommonReader_File_And_S3_Holder.getSingletonInstance().getCommonReader_File_And_S3();

			String dataFilename =
					CreateSpectralStorageFilenames.getInstance()
					.createSpectraStorage_ScanBinnedIntensityOn_RT_MZ__JSON_GZIP_Filename(
							scanFileAPIKey,
							retentionTimeBinSize,
							mzBinSize );

			//  Copy dataFile to outputStream
			
			// Not readily available from S3 so skipping.
//			response.setContentLengthLong( dataFile.length() );
			
			InputStream inputStream = null;
			OutputStream outputStream = null;
			
			byte[] copyBuffer = new byte[ 16384 ];
			
			try {
				inputStream = new BufferedInputStream( commonReader_File_And_S3.getInputStreamForScanStorageItem( dataFilename, scanFileAPIKey ) );
				outputStream = response.getOutputStream();
				
				int bytesRead = 0;
				while ( ( bytesRead = inputStream.read( copyBuffer ) ) != -1 ) {
					outputStream.write( copyBuffer, 0, bytesRead );
				}
				
				
			} finally {
				if ( inputStream != null ) {
					try {
						inputStream.close();
					} catch ( Exception e ) {
						
					}
					inputStream = null;
				}
				if ( outputStream != null ) {
					try {
						outputStream.close();
					} catch ( Exception e ) {
						
					}
					outputStream = null;
				}
				
			}

		} catch ( SpectralStorageDataNotFoundException e ) {

			response.setStatus( HttpServletResponse.SC_NOT_FOUND /* 404  */ );
			
		} catch (SpectralFileBadRequestToServletException e) {

			response.setStatus( HttpServletResponse.SC_BAD_REQUEST /* 400  */ );

			if ( StringUtils.isNotEmpty( e.getMessage() ) ) {
				WriteResponseStringToOutputStream.getInstance()
				.writeResponseStringToOutputStream( e.getMessage(), response);
			}

		} catch (Throwable e) {
			String msg = "Failed to process request";
			log.error( msg, e );
			response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR /* 500  */ );
		}

	}
	
}
