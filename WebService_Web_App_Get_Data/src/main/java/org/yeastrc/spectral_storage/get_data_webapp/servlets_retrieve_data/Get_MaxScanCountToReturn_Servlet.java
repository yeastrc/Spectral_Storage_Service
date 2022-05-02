package org.yeastrc.spectral_storage.get_data_webapp.servlets_retrieve_data;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.get_data_webapp.config.ConfigData_ScanDataLocation_InWorkDirectory;
import org.yeastrc.spectral_storage.get_data_webapp.constants_enums.MaxNumberScansReturnConstants;
import org.yeastrc.spectral_storage.get_data_webapp.constants_enums.ServetResponseFormatEnum;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileBadRequestToServletException;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileDeserializeRequestException;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.GetRequestObjectFromInputStream;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.Get_ServletResultDataFormat_FromServletInitParam;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.WriteResponseObjectToOutputStream;
import org.yeastrc.spectral_storage.get_data_webapp.servlets_common.WriteResponseStringToOutputStream;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_MaxScanCountToReturn_Request;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_MaxScanCountToReturn_Response;

/**
 * Return Max Scan Count to Return - from config file or default
 * 
 * Same Max Scan Count to Return used in Servlets:  GetScanDataFromScanNumbers_Servlet, GetScansDataFromRetentionTimeRange_Servlet
 *
 */
public class Get_MaxScanCountToReturn_Servlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger( Get_MaxScanCountToReturn_Servlet.class );

	private static final long serialVersionUID = 1L;

	private ServetResponseFormatEnum servetResponseFormat;
	
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config)
	          throws ServletException {
		
		super.init(config); //  Must call this first

		servetResponseFormat = 
				Get_ServletResultDataFormat_FromServletInitParam.getInstance()
				.get_ServletResultDataFormat_FromServletInitParam( config );

		log.warn( "INFO: servetResponseFormat: " + servetResponseFormat );
		
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Get_MaxScanCountToReturn_Request get_MaxScanCountToReturn_Request = null;

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
				get_MaxScanCountToReturn_Request = (Get_MaxScanCountToReturn_Request) requestObj;
			} catch (Exception e) {
				String msg = "Failed to cast requestObj to Get_MaxScanCountToReturn_Request";
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
		
		processRequest( get_MaxScanCountToReturn_Request, request, response );
	}
	
	/**
	 * @param get_MaxScanCountToReturn_Request
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processRequest( 
			Get_MaxScanCountToReturn_Request get_MaxScanCountToReturn_Request,
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
	
		try {
			Get_MaxScanCountToReturn_Response webserviceResponse = new Get_MaxScanCountToReturn_Response();

			//  maxNumberScansReturn

			Integer maxNumberScansReturn = ConfigData_ScanDataLocation_InWorkDirectory.getSingletonInstance().getMaxNumberScansReturn();
			
			if ( maxNumberScansReturn == null ) {
				//  Nothing in config so use default
				
				maxNumberScansReturn = MaxNumberScansReturnConstants.MAX_NUMBER_SCANS_RETURN_FOR_IMMEDIATE_WEBSERVICES__DEFAULT;
			}
			
			/////
			
			webserviceResponse.setMaxScanCountToReturn( maxNumberScansReturn );
			
			WriteResponseObjectToOutputStream.getSingletonInstance()
			.writeResponseObjectToOutputStream( webserviceResponse, servetResponseFormat, response );

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
