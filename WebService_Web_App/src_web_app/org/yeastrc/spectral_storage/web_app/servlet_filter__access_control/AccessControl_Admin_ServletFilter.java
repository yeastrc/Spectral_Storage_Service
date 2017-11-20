package org.yeastrc.spectral_storage.web_app.servlet_filter__access_control;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.yeastrc.spectral_storage.web_app.config.ConfigData_Allowed_Remotes_InWorkDirectory;

/**
 * Access control for Admin servlets on remote IPs
 *
 */
public class AccessControl_Admin_ServletFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
//		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String remoteAddr = request.getRemoteAddr();
		
//		if ( true ) {
		if ( ! ConfigData_Allowed_Remotes_InWorkDirectory.getSingletonInstance().getAllowedAdminRemoteIPs().contains( remoteAddr ) ) {

			//  IP not in allowed list
			
			httpResponse.setStatus( 403 ); //  return 403 error
			httpResponse.setContentType( "text" );
			httpResponse.getWriter().print( "not_authorized" );
			
			return;
		}
		
		chain.doFilter(request, response);
	}
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
}
