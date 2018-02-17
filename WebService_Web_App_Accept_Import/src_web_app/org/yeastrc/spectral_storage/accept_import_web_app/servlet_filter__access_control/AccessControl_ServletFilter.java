package org.yeastrc.spectral_storage.accept_import_web_app.servlet_filter__access_control;

import java.io.IOException;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Allowed_Remotes_InWorkDirectory;

/**
 * Access control on remote IPs
 * 
 * Applied to all URLs
 */
public class AccessControl_ServletFilter implements Filter {

	private static final Logger log = Logger.getLogger( AccessControl_ServletFilter.class );

	private static enum AccessControlType { OVERALL, ADMIN, UPDATE }
	
	private static final String ACCESS_CONTROL_TYPE_INIT_PARAMETER_NAME = "access.control.type";
	
	private static final String ACCESS_CONTROL_TYPE_INIT_PARAMETER_VALUE_OVERALL = "overall";
	private static final String ACCESS_CONTROL_TYPE_INIT_PARAMETER_VALUE_ADMIN = "admin";
	private static final String ACCESS_CONTROL_TYPE_INIT_PARAMETER_VALUE_UPDATE = "update";
	
	private static final String[] ACCESS_CONTROL_TYPE_INIT_PARAMETER_VALUES_ALLOWED = 
		{
				ACCESS_CONTROL_TYPE_INIT_PARAMETER_VALUE_OVERALL,
				ACCESS_CONTROL_TYPE_INIT_PARAMETER_VALUE_ADMIN,
				ACCESS_CONTROL_TYPE_INIT_PARAMETER_VALUE_UPDATE
		};
	
	private AccessControlType accessControlType;
	
	@Override
	public void init(FilterConfig filterConfig ) throws ServletException {
		
		//  Determine which access control IP list to use
		
		String accessControlTypeString = filterConfig.getInitParameter( ACCESS_CONTROL_TYPE_INIT_PARAMETER_NAME );
		log.warn( "INFO: Init parameter '" + ACCESS_CONTROL_TYPE_INIT_PARAMETER_NAME + "' value: " + accessControlTypeString );
		
		if ( ACCESS_CONTROL_TYPE_INIT_PARAMETER_VALUE_OVERALL.equals( accessControlTypeString ) ) {
			accessControlType = AccessControlType.OVERALL;
		} else if ( ACCESS_CONTROL_TYPE_INIT_PARAMETER_VALUE_ADMIN.equals( accessControlTypeString ) ) {
			accessControlType = AccessControlType.ADMIN;
		} else if ( ACCESS_CONTROL_TYPE_INIT_PARAMETER_VALUE_UPDATE.equals( accessControlTypeString ) ) {
			accessControlType = AccessControlType.UPDATE;
		} else {
			String msg = "Init parameter '" + ACCESS_CONTROL_TYPE_INIT_PARAMETER_NAME + "' has invalid value of '"
					+ accessControlTypeString
					+ "'.  Valid values are: " + StringUtils.join( ACCESS_CONTROL_TYPE_INIT_PARAMETER_VALUES_ALLOWED );
			log.error( msg );
			throw new ServletException(msg);
		}
	}
	
	@Override
	public void destroy() {

	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
//		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String remoteAddr = request.getRemoteAddr();
		
		Set<String> allowedRemoteIPs = null;
		
		if ( accessControlType == AccessControlType.OVERALL ) {
			allowedRemoteIPs = ConfigData_Allowed_Remotes_InWorkDirectory.getSingletonInstance().getAllowedRemoteIPs_Overall();
		} else if ( accessControlType == AccessControlType.ADMIN ) {
			allowedRemoteIPs = ConfigData_Allowed_Remotes_InWorkDirectory.getSingletonInstance().getAllowedRemoteIPs_Admin();
		} else if ( accessControlType == AccessControlType.UPDATE ) {
			allowedRemoteIPs = ConfigData_Allowed_Remotes_InWorkDirectory.getSingletonInstance().getAllowedRemoteIPs_Update();
		} else {
			String msg = "Unknown value for accessControlType: " + accessControlType;
			log.error( msg );
			throw new ServletException( msg );
		}
		
		if ( allowedRemoteIPs != null && ( ! allowedRemoteIPs.isEmpty() ) ) {

			if ( ! allowedRemoteIPs.contains( remoteAddr ) ) {

				//  IP not in allowed list

				httpResponse.setStatus( 401 ); //  return 401 error
				httpResponse.setContentType( "text" );
				httpResponse.getWriter().print( "not_authorized" );

				return;
			}
		}
		
		chain.doFilter(request, response);
	}
	
}
