/*
* Original author: Daniel Jaschob <djaschob .at. uw.edu>
*                  
* Copyright 2018 University of Washington - Seattle, WA
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.yeastrc.spectral_storage.accept_import_web_app.delete_directory_and_contents;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *
 */
public class DeleteDirectoryAndContents {

	private static final Logger log = LoggerFactory.getLogger( DeleteDirectoryAndContents.class );

	private volatile boolean keepRunning = true;
	

	//  private constructor
	private DeleteDirectoryAndContents() { }
	
	/**
	 * @return newly created instance
	 */
	public static DeleteDirectoryAndContents getInstance() { 
		return new DeleteDirectoryAndContents(); 
	}

	/**
	 * awaken thread to process request, calls "notify()"
	 */
	public void awaken() {

		if ( log.isDebugEnabled() ) {
			log.debug("awaken() called:  " );
		}

		synchronized (this) {
			notify();
		}
	}

	/**
	 * shutdown was received from the operating system.  This is called on a different thread.
	 */
	public void shutdown() {
		log.info("shutdown() called");
		synchronized (this) {
			this.keepRunning = false;
		}
		
		this.awaken();
	}

	/**
	 * @param directoryToDelete
	 * @throws IOException 
	 */
	public void deleteDirectoryAndContents( File directoryToDelete ) {

		if ( ! directoryToDelete.isDirectory() ) {
			//  Must be a directory
			return;  //  EARLY RETURN
		}
		
		deleteDirectoryAndContentsInternal( directoryToDelete );
	}
	
	
	/**
	 * Recursively called to delete sub directories
	 * 
	 * @param directoryToDelete
	 * @throws IOException 
	 */
	private void deleteDirectoryAndContentsInternal( File directoryToDelete ) {
		
		File[] dirContents = directoryToDelete.listFiles();
		
		for ( File dirItem : dirContents ) {

			if ( ! keepRunning ) {
				//  shutdown() called
				return; // EARLY RETURN
			}
		
			if ( dirItem.isDirectory() ) {
				
				deleteDirectoryAndContentsInternal( dirItem );

				if ( ! dirItem.delete() ) {
					
					String msg = "Failed to delete directory: " + dirItem.getAbsolutePath();
					log.error( msg );
					
				}
			} else {
				
				if ( ! dirItem.delete() ) {
					
					String msg = "Failed to delete file: " + dirItem.getAbsolutePath();
					log.error( msg );
					
				}
			}
		}
		
		if ( ! directoryToDelete.delete() ) {
			
			String msg = "Failed to delete directory: " + directoryToDelete.getAbsolutePath();
			log.error( msg );
			
		}
	}

}
