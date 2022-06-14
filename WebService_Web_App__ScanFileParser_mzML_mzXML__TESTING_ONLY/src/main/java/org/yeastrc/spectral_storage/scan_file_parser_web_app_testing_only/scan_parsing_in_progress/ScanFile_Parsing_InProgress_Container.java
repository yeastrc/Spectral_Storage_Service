package org.yeastrc.spectral_storage.scan_file_parser_web_app_testing_only.scan_parsing_in_progress;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanFile_Parsing_InProgress_Container {

	private static final Logger log = LoggerFactory.getLogger(ScanFile_Parsing_InProgress_Container.class);
	
	private static ScanFile_Parsing_InProgress_Container singletonInstance = new ScanFile_Parsing_InProgress_Container();
	
	public static ScanFile_Parsing_InProgress_Container get_SingletonInstance() {
		return singletonInstance;
	}
	
	// Private constructor
	private ScanFile_Parsing_InProgress_Container() {}
	
	
	private ConcurrentMap<String, ScanFile_Parsing_InProgress_Item> scanFile_Parsing_InProgress_Item_Map = new ConcurrentHashMap<>();
	
	
	/**
	 * @param identifier
	 * @param item
	 * @return - true if put succeeded
	 */
	public boolean putItem_IfIdentifierNotInMap( String identifier, ScanFile_Parsing_InProgress_Item item ) {
		
		ScanFile_Parsing_InProgress_Item existingValue = scanFile_Parsing_InProgress_Item_Map.putIfAbsent(identifier, item);
		if ( existingValue == null ) {
			return true;
		}
		return false;
	}

	public ScanFile_Parsing_InProgress_Item getItem( String identifier ) {
		
		return scanFile_Parsing_InProgress_Item_Map.get(identifier);
	}
	
	public void removeItem( String identifier ) {
		
		scanFile_Parsing_InProgress_Item_Map.remove(identifier);
	}
}
