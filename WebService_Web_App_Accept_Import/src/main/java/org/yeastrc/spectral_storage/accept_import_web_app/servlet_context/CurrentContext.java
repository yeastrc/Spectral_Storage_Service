package org.yeastrc.spectral_storage.accept_import_web_app.servlet_context;

public class CurrentContext {

	private static String currentWebAppContext;

	/**
	 * @return Current context of web app with leading "/"
	 */
	public static String getCurrentWebAppContext() {
		return currentWebAppContext;
	}

	
	/**
	 * Package Private Set method
	 * @param currentWebAppContext
	 */
	static void setCurrentWebAppContext(String currentWebAppContext) {
		CurrentContext.currentWebAppContext = currentWebAppContext;
	}
}
