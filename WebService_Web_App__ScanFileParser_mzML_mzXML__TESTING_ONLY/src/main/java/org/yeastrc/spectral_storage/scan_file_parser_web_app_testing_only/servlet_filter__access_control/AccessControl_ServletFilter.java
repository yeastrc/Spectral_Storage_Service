package org.yeastrc.spectral_storage.scan_file_parser_web_app_testing_only.servlet_filter__access_control;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;

/**
 * Access control on remote IPs
 * 
 * Applied to all URLs
 */
public class AccessControl_ServletFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger( AccessControl_ServletFilter.class );

	@Override
	public void init(FilterConfig filterConfig ) throws ServletException {
		
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

		//  The allowed IP addresses are: Loopback IP:   127.0.0.1 and 0:0:0:0:0:0:0:1 (0:0:0:0:0:0:0:1 is IP V6 Loopback like 127.0.0.1)

		if ( ( ! remoteAddr.equals( "127.0.0.1" ) ) && ( ! remoteAddr.equals( "0:0:0:0:0:0:0:1" ) ) ) {

			//  IP not allowed 

			httpResponse.setStatus( 401 ); //  return 401 error
			httpResponse.setContentType( "text" );
			httpResponse.getWriter().print( "not_authorized" );

			return;

		}
		
		chain.doFilter(request, response);
	}
	
}
