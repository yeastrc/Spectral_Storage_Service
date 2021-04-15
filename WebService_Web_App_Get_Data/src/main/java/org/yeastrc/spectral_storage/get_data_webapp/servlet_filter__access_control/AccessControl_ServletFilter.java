package org.yeastrc.spectral_storage.get_data_webapp.servlet_filter__access_control;

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
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.get_data_webapp.config.ConfigData_Allowed_Remotes_InWorkDirectory;

/**
 * Access control on remote IPs
 * 
 * Applied to all URLs
 */
public class AccessControl_ServletFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger( AccessControl_ServletFilter.class );

	private static enum AccessControlType { OVERALL, ADMIN, QUERY }
	
	private static final String ACCESS_CONTROL_TYPE_INIT_PARAMETER_NAME = "access.control.type";
	
	private static final String ACCESS_CONTROL_TYPE_INIT_PARAMETER_VALUE_OVERALL = "overall";
	private static final String ACCESS_CONTROL_TYPE_INIT_PARAMETER_VALUE_ADMIN = "admin";
	private static final String ACCESS_CONTROL_TYPE_INIT_PARAMETER_VALUE_QUERY = "query";
	
	private static final String[] ACCESS_CONTROL_TYPE_INIT_PARAMETER_VALUES_ALLOWED = 
		{
				ACCESS_CONTROL_TYPE_INIT_PARAMETER_VALUE_OVERALL,
				ACCESS_CONTROL_TYPE_INIT_PARAMETER_VALUE_ADMIN,
				ACCESS_CONTROL_TYPE_INIT_PARAMETER_VALUE_QUERY
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
		} else if ( ACCESS_CONTROL_TYPE_INIT_PARAMETER_VALUE_QUERY.equals( accessControlTypeString ) ) {
			accessControlType = AccessControlType.QUERY;
		} else {
			String msg = "Init parameter '" + ACCESS_CONTROL_TYPE_INIT_PARAMETER_NAME + "' has invalid value of '"
					+ accessControlTypeString
					+ "'.  Valid values are: " + StringUtils.join( ACCESS_CONTROL_TYPE_INIT_PARAMETER_VALUES_ALLOWED, "," );
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

		if ( ( accessControlType == AccessControlType.QUERY || accessControlType == AccessControlType.OVERALL ) 
				&& ConfigData_Allowed_Remotes_InWorkDirectory.getSingletonInstance().isAccessAllowed_allRemoteIps_query() ) {
			
			//  ALL Remote IPs allowed for Query
			
		} else {
	
			String remoteAddr = request.getRemoteAddr();
			
			Set<String> allowedRemoteIPs = null;
			
			if ( accessControlType == AccessControlType.OVERALL ) {
				allowedRemoteIPs = ConfigData_Allowed_Remotes_InWorkDirectory.getSingletonInstance().getAllowedRemoteIPs_Overall();
			} else if ( accessControlType == AccessControlType.ADMIN ) {
				allowedRemoteIPs = ConfigData_Allowed_Remotes_InWorkDirectory.getSingletonInstance().getAllowedRemoteIPs_Admin();
			} else if ( accessControlType == AccessControlType.QUERY ) {
				allowedRemoteIPs = ConfigData_Allowed_Remotes_InWorkDirectory.getSingletonInstance().getAllowedRemoteIPs_Query();
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
		}
		
		chain.doFilter(request, response);
	}
	
}
