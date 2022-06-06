package org.yeastrc.spectral_storage.accept_import_web_app.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_ScanFilenameSuffix_To_ConverterMapping.ConfigData_ScanFilenameSuffix_To_ConverterMapping_SingleEntry;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileWebappConfigException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * 
 *
 */
public class ConfigData_ScanFilenameSuffix_To_ConverterMapping_Reader {

	private static final Logger log = LoggerFactory.getLogger( ConfigData_ScanFilenameSuffix_To_ConverterMapping_Reader.class );

	private static String CONFIG_FILENAME = "spectral_server_accept_import_config_scanfilename_suffix_to_converter_base_url.yaml";
	
	//  private constructor
	private ConfigData_ScanFilenameSuffix_To_ConverterMapping_Reader() { }
	
	/**
	 * @return newly created instance
	 */
	public static ConfigData_ScanFilenameSuffix_To_ConverterMapping_Reader getInstance() { 
		return new ConfigData_ScanFilenameSuffix_To_ConverterMapping_Reader(); 
	}
	
	/**
	 * @throws Exception
	 */
	public void readConfigDataInWebApp() throws Exception {
		
		
		//  Create Sample Yaml file using same Java Classes
//		{
//			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
//			
//			List<YamlFile_Data_SingleEntry> scanfilename_suffix_to_converter_base_url_mapping = new ArrayList<>();
//			
//			{
//				List<String> scan_filename_suffixes = new ArrayList<>();
//				
//				scan_filename_suffixes.add( "sfx_a1");
//				scan_filename_suffixes.add( "sfx_b1");
//				
//				YamlFile_Data_SingleEntry entry = new YamlFile_Data_SingleEntry();
//				entry.setConverter_base_url("converter_base_url_a");
//				entry.setScan_filename_suffixes(scan_filename_suffixes);
//				
//				scanfilename_suffix_to_converter_base_url_mapping.add(entry);
//			}
//			{
//				List<String> scan_filename_suffixes = new ArrayList<>();
//				
//				scan_filename_suffixes.add( "sfx_a2");
//				scan_filename_suffixes.add( "sfx_b2");
//				
//				YamlFile_Data_SingleEntry entry = new YamlFile_Data_SingleEntry();
//				entry.setConverter_base_url("converter_base_url_b");
//				entry.setScan_filename_suffixes(scan_filename_suffixes);
//				
//				scanfilename_suffix_to_converter_base_url_mapping.add(entry);
//			}
//			
//			YamlFile_Data_Root configData = new YamlFile_Data_Root();
//			
//			mapper.writeValue(new File("/data/SpectrConfig.yaml"), configData);
//		}
		
		//  Get config file from Work Directory

		File workDirectory = ConfigDataInWebApp.getSingletonInstance().getWebappWorkDirectory();

		//  Already tested but test here to be extra safe
		if ( workDirectory == null ) {
			String msg = "work directory in config is empty or missing";
			log.error( msg );
			throw new SpectralFileWebappConfigException( msg );
		}

		File configFile = new File( workDirectory, CONFIG_FILENAME );
		if ( ! ( configFile.exists() && configFile.isFile() && configFile.canRead() ) ) {
						
			String msg = "Config file '" + CONFIG_FILENAME
					+ "' does not exist, is not  a file, or is not readable."
					+ "  Config file with path: " + configFile.getCanonicalPath();
			log.error( msg );
			throw new SpectralFileWebappConfigException( msg );
		}

		ConfigData_ScanFilenameSuffix_To_ConverterMapping configData = new ConfigData_ScanFilenameSuffix_To_ConverterMapping();

		boolean foundAtLeastOneEntry = false;

		try ( InputStream configFileAsInputStream = new FileInputStream( configFile ) ) {

			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

			YamlFile_Data_Root yamlFileContents = mapper.readValue(configFileAsInputStream, YamlFile_Data_Root.class);

			log.warn( "INFO:: Contents of '" + CONFIG_FILENAME + "': " + yamlFileContents );

			////  Convert to internal representation
			
			if ( yamlFileContents.scanfilename_suffix_to_converter_base_url_mapping != null && ( ! yamlFileContents.scanfilename_suffix_to_converter_base_url_mapping.isEmpty() ) ) {

				for ( YamlFile_Data_SingleEntry yamlFile_Data_SingleEntry : yamlFileContents.scanfilename_suffix_to_converter_base_url_mapping ) {

					String converter_base_url = yamlFile_Data_SingleEntry.converter_base_url;
					
					if ( StringUtils.isEmpty( converter_base_url ) ) {
						String msg = "converter_base_url does not exist or is empty string in config file: " + CONFIG_FILENAME;
						log.error(msg);
						throw new SpectralFileWebappConfigException(msg);
					}

					if ( yamlFile_Data_SingleEntry.scan_filename_suffixes != null && ( ! yamlFile_Data_SingleEntry.scan_filename_suffixes.isEmpty() ) ) {

						for ( String scan_filename_suffix : yamlFile_Data_SingleEntry.scan_filename_suffixes ) {

							if ( StringUtils.isEmpty( converter_base_url ) ) {
								String msg = "scan_filename_suffix does not exist or is empty string in config file: " + CONFIG_FILENAME;
								log.error(msg);
								throw new SpectralFileWebappConfigException(msg);
							}
							
							if ( ! scan_filename_suffix.startsWith( "." ) ) {
								
								//  Add starting "."
								
								scan_filename_suffix = "." + scan_filename_suffix;
							}

							ConfigData_ScanFilenameSuffix_To_ConverterMapping_SingleEntry configEntry = new ConfigData_ScanFilenameSuffix_To_ConverterMapping_SingleEntry();

							configEntry.setConverter_base_url(converter_base_url);
							configEntry.setScan_filename_suffix(scan_filename_suffix);

							configData.addSingleEntry(configEntry, CONFIG_FILENAME);
							
							foundAtLeastOneEntry = true;
						}
					}
				}
			}
		}
		
		if ( ! foundAtLeastOneEntry ) {

			String msg = "NO converter_base_url and scan filename suffix entries in config file: " + CONFIG_FILENAME;
			log.error(msg);
			throw new SpectralFileWebappConfigException(msg);
		}
		
		configData.setConfigurationComplete();
				
		ConfigData_ScanFilenameSuffix_To_ConverterMapping.setInstance(configData);
		
		log.warn( "INFO:: Contents of '" + CONFIG_FILENAME + "': Internal Representation: " + configData.getEntries() );
	}
	
	//////////////
	
	//  Classes the Config file is parsed into

	/**
	 * 
	 *
	 */
	private static class YamlFile_Data_Root {

		private List<YamlFile_Data_SingleEntry> scanfilename_suffix_to_converter_base_url_mapping;

		@Override
		public String toString() {
			return "YamlFile_Data_Root [scanfilename_suffix_to_converter_base_url_mapping="
					+ scanfilename_suffix_to_converter_base_url_mapping + "]";
		}
		
		@SuppressWarnings("unused")
		private List<YamlFile_Data_SingleEntry> getScanfilename_suffix_to_converter_base_url_mapping() {
			return scanfilename_suffix_to_converter_base_url_mapping;
		}
		@SuppressWarnings("unused")
		private void setScanfilename_suffix_to_converter_base_url_mapping(
				List<YamlFile_Data_SingleEntry> scanfilename_suffix_to_converter_base_url_mapping) {
			this.scanfilename_suffix_to_converter_base_url_mapping = scanfilename_suffix_to_converter_base_url_mapping;
		}
	}

	/**
	 * 
	 *
	 */
	private static class YamlFile_Data_SingleEntry {
		
		private List<String> scan_filename_suffixes;
		private String converter_base_url;

		@Override
		public String toString() {
			return "YamlFile_Data_SingleEntry [scan_filename_suffixes="
					+ scan_filename_suffixes + ", converter_base_url=" + converter_base_url + "]";
		}
		
		@SuppressWarnings("unused")
		private String getConverter_base_url() {
			return converter_base_url;
		}
		@SuppressWarnings("unused")
		private void setConverter_base_url(String converter_base_url) {
			this.converter_base_url = converter_base_url;
		}
		@SuppressWarnings("unused")
		private List<String> getScan_filename_suffixes() {
			return scan_filename_suffixes;
		}
		@SuppressWarnings("unused")
		private void setScan_filename_suffixes(List<String> scan_filename_suffixes) {
			this.scan_filename_suffixes = scan_filename_suffixes;
		}
	}
}
