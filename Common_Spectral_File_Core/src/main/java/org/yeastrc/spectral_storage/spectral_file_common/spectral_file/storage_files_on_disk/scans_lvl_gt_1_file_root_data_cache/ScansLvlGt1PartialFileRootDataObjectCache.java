package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.scans_lvl_gt_1_file_root_data_cache;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataNotFoundException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.scans_lvl_gt_1_partial.SpectralFile_ScansLvlGt1Partial_FileContents_Root_IF;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3_Holder;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.StorageFile_Version_003_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.scans_lvl_gt_1_partial.reader_writer.SpectralFile_ScansLvlGt1Partial_File_Reader_V_003;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.StorageFile_Version_005_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.scans_lvl_gt_1_partial.reader_writer.SpectralFile_ScansLvlGt1Partial_File_Reader_V_005;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Cache the Contents objects from the ScansLvlGt1Partial File Reader
 *
 * Singleton Object
 */
public class ScansLvlGt1PartialFileRootDataObjectCache {

	private static final Logger log = LoggerFactory.getLogger(ScansLvlGt1PartialFileRootDataObjectCache.class);
	
	private static final int CACHE_MAX_SIZE_FULL_SIZE = 100;
//	private static final int CACHE_MAX_SIZE_SMALL = 10;

	//  Keep in memory always so don't specify a timeout
//	private static final int CACHE_TIMEOUT_FULL_SIZE = 20; // in days
//	private static final int CACHE_TIMEOUT_SMALL = 1; // in days


	private static final AtomicLong cacheGetCount = new AtomicLong();
	private static final AtomicLong cacheDBRetrievalCount = new AtomicLong();
	
	private static volatile int prevDayOfYear = -1;

	private static boolean debugLogLevelEnabled = false;
	
	/**
	 * Static singleton instance
	 */
	private static ScansLvlGt1PartialFileRootDataObjectCache _instance = null; //  Delay creating until first getInstance() call

	/**
	 * Static get singleton instance
	 * @return
	 * @throws Exception 
	 */
	public static synchronized ScansLvlGt1PartialFileRootDataObjectCache getSingletonInstance() throws Exception {

		if ( _instance == null ) {
			_instance = new ScansLvlGt1PartialFileRootDataObjectCache();
		}
		return _instance; 
	}
	
	/**
	 * constructor
	 */
	private ScansLvlGt1PartialFileRootDataObjectCache() {
		if ( log.isDebugEnabled() ) {
			debugLogLevelEnabled = true;
			log.debug( "debug log level enabled" );
		}

		cacheHolderInternal = new CacheHolderInternal( this );

		//  Register this class with the centralized Cached Data Registry, to support centralized cache clearing
//		CachedDataCentralRegistry.getInstance().register( this );
	}
	
	private CacheHolderInternal cacheHolderInternal;
	
	
//	@Override
//	public CacheCurrentSizeMaxSizeResult getCurrentCacheSizeAndMax() throws Exception {
//		return cacheHolderInternal.getCurrentCacheSizeAndMax();
//	}
//	
//
//	@Override
//	public void clearCacheData() throws Exception {
//		clearCache();
//	}

	/**
	 * Recreate the cache using current config values, if they exist, or else defaults
	 * @throws Exception 
	 */
	public void clearCache() throws Exception {
		printPrevCacheHitCounts( true /* forcePrintNow */ );
		cacheHolderInternal.invalidate();
	}

	/**
	 * @param hashKey
	 * @return - retrieved from File or null if not found
	 * @throws Exception
	 */
	public SpectralFile_ScansLvlGt1Partial_FileContents_Root_IF getSpectralFile_ScansLvlGt1Partial_FileContents_Root_IF( 
			String hashKey, int fileVersionNumber ) throws Exception {
		
		printPrevCacheHitCounts( false /* forcePrintNow */ );
		
		if ( debugLogLevelEnabled ) {
			cacheGetCount.incrementAndGet();
		}

		try {
			LoadingCache<CacheKey, SpectralFile_ScansLvlGt1Partial_FileContents_Root_IF> cache = cacheHolderInternal.getCache();

			CacheKey cacheKey = new CacheKey();
			
			cacheKey.hashKey = hashKey;
			cacheKey.fileVersionNumber = fileVersionNumber;
			
			if ( cache != null ) {
				SpectralFile_ScansLvlGt1Partial_FileContents_Root_IF spectralFile_ScansLvlGt1Partial_FileContents_Root_IF = cache.get( cacheKey );
				return spectralFile_ScansLvlGt1Partial_FileContents_Root_IF; // EARLY return
			}

			SpectralFile_ScansLvlGt1Partial_FileContents_Root_IF spectralFile_ScansLvlGt1Partial_FileContents_Root_IF = 
					cacheHolderInternal.loadFromScansLvlGt1PartialFile( hashKey, fileVersionNumber );
			return spectralFile_ScansLvlGt1Partial_FileContents_Root_IF;
			
		} catch ( ExecutionException e ) {
			//  caught from LoadingCache when loadFromDB throws SpectralStorageDataNotFoundException
			if ( e.getCause() instanceof SpectralStorageDataNotFoundException ) {
				//  DB query returned null so return null here
				return null;
			}
			throw e;
		} catch ( SpectralStorageDataNotFoundException e ) {
			//  DB query returned null so return null here
			return null;
		}
	}

	/**
	 * Key to Cache
	 * 
	 */
	private static class CacheKey {
		
		String hashKey;
		int fileVersionNumber;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + fileVersionNumber;
			result = prime * result + ((hashKey == null) ? 0 : hashKey.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CacheKey other = (CacheKey) obj;
			if (fileVersionNumber != other.fileVersionNumber)
				return false;
			if (hashKey == null) {
				if (other.hashKey != null)
					return false;
			} else if (!hashKey.equals(other.hashKey))
				return false;
			return true;
		}
	}


	/**
	 * Class to hold and create the cache object
	 *
	 */
	private static class CacheHolderInternal {

		private ScansLvlGt1PartialFileRootDataObjectCache parentObject;
		
		private CacheHolderInternal( ScansLvlGt1PartialFileRootDataObjectCache parentObject ) {
			this.parentObject = parentObject;
		}
		
		private boolean cacheDataInitialized;
		
		/**
		 * cached data, left null if no caching
		 */
		private LoadingCache<CacheKey, SpectralFile_ScansLvlGt1Partial_FileContents_Root_IF> fileDataCache = null;
		
		private int cacheMaxSize;

		/**
		 * @return
		 * @throws Exception
		 */
//		public synchronized CacheCurrentSizeMaxSizeResult getCurrentCacheSizeAndMax() throws Exception {
//			CacheCurrentSizeMaxSizeResult result = new CacheCurrentSizeMaxSizeResult();
//			if ( dbRecordsDataCache != null ) {
//				result.setCurrentSize( dbRecordsDataCache.size() );
//				result.setMaxSize( cacheMaxSize );
//			}
//			return result;
//		}
		
		/**
		 * @throws Exception
		 */
		@SuppressWarnings("static-access")
		private synchronized LoadingCache<CacheKey, SpectralFile_ScansLvlGt1Partial_FileContents_Root_IF> getCache(  ) throws Exception {
			if ( ! cacheDataInitialized ) { 
//				CachedDataSizeOptions cachedDataSizeOptions = 
//						CachedDataCentralConfigStorageAndProcessing.getInstance().getCurrentSizeConfigValue();
				
//				if ( cachedDataSizeOptions == CachedDataSizeOptions.FEW ) {
//					//  No Cache, just mark initialized, dbRecordsDataCache already set to null;
//					cacheDataInitialized = true;
//					return dbRecordsDataCache;  //  EARLY RETURN
//				}
				
//				int cacheTimeout = CACHE_TIMEOUT_FULL_SIZE;
				cacheMaxSize = ScansLvlGt1PartialFileRootDataObjectCache.CACHE_MAX_SIZE_FULL_SIZE;
//				if ( cachedDataSizeOptions == CachedDataSizeOptions.HALF ) {
////					cacheMaxSize = cacheMaxSize / 2;
//				} else if ( cachedDataSizeOptions == CachedDataSizeOptions.SMALL ) {
////					cacheMaxSize = parentObject.CACHE_MAX_SIZE_SMALL;
////					cacheTimeout = CACHE_TIMEOUT_SMALL;
//				}
				
				fileDataCache = CacheBuilder.newBuilder()
//						.expireAfterAccess( cacheTimeout, TimeUnit.DAYS ) // always in cache
						.maximumSize( cacheMaxSize )
						.build(
								new CacheLoader<CacheKey, SpectralFile_ScansLvlGt1Partial_FileContents_Root_IF>() {
									@Override
									public SpectralFile_ScansLvlGt1Partial_FileContents_Root_IF load( CacheKey cacheKey ) throws Exception {

										String hashKey = cacheKey.hashKey;
										int fileVersionNumber = cacheKey.fileVersionNumber;
										
										//   WARNING  cannot return null.  
										//   If would return null, throw SpectralStorageDataNotFoundException and catch at the .get(...)
										
										//  value is NOT in cache so get it and return it
										return loadFromScansLvlGt1PartialFile( hashKey, fileVersionNumber );
									}
								});
			//			    .build(); // no CacheLoader
				cacheDataInitialized = true;
			}
			return fileDataCache;
		}

		private synchronized void invalidate() {
			fileDataCache = null;
			cacheDataInitialized = false;
		}
		

		/**
		 * @return
		 * @throws Exception
		 */
		private SpectralFile_ScansLvlGt1Partial_FileContents_Root_IF loadFromScansLvlGt1PartialFile( String hashKey, int fileVersionNumber ) throws Exception {
			
			//   WARNING  cannot return null.  
			//   If would return null, throw SpectralStorageDataNotFoundException and catch at the .get(...)
			
			//  value is NOT in cache so get it and return it
			if ( debugLogLevelEnabled ) {
				cacheDBRetrievalCount.incrementAndGet();
			}
			
			try {
				SpectralFile_ScansLvlGt1Partial_FileContents_Root_IF spectralFile_ScansLvlGt1Partial_FileContents_Root_IF = null;

				if ( fileVersionNumber == StorageFile_Version_003_Constants.FILE_VERSION ) {
					
					spectralFile_ScansLvlGt1Partial_FileContents_Root_IF =
							SpectralFile_ScansLvlGt1Partial_File_Reader_V_003.getInstance()
							.readScansLvlGt1PartialFile( 
									hashKey, 
									CommonReader_File_And_S3_Holder.getSingletonInstance().getCommonReader_File_And_S3() );

				} else if ( fileVersionNumber == StorageFile_Version_005_Constants.FILE_VERSION ) {

					spectralFile_ScansLvlGt1Partial_FileContents_Root_IF =
							SpectralFile_ScansLvlGt1Partial_File_Reader_V_005.getInstance()
							.readScansLvlGt1PartialFile( 
									hashKey, 
									CommonReader_File_And_S3_Holder.getSingletonInstance().getCommonReader_File_And_S3() );

				} else {
					
					String msg = "fileVersionNumber not a supported value.  fileVersionNumber: " + fileVersionNumber;
					log.error(msg);
					throw new SpectralStorageProcessingException( msg );
				}
				
				if ( spectralFile_ScansLvlGt1Partial_FileContents_Root_IF == null ) {
					// Throw this exception since cannot return null to Cache
					throw new SpectralStorageDataNotFoundException();
				}
				return spectralFile_ScansLvlGt1Partial_FileContents_Root_IF;

			} catch ( Exception e ) {
				
				log.error( "loadFromScansLvlGt1PartialFile(...): readScansLvlGt1PartialFile(...) threw exception for hashKey: " 
						+ hashKey
						+ ", fileVersionNumber: " + fileVersionNumber
						+ ", Scan File Storage Location: " 
						+ CommonReader_File_And_S3_Holder.getSingletonInstance().getCommonReader_File_And_S3(), e );
				throw e;
			}
		}
 	}

	/**
	 * 
	 */
	private void printPrevCacheHitCounts( boolean forcePrintNow ) {
		
		Calendar now = Calendar.getInstance();
		
		int nowDayOfYear = now.get( Calendar.DAY_OF_YEAR );
		
		if ( prevDayOfYear != nowDayOfYear || forcePrintNow ) {

			if ( prevDayOfYear != -1 ) {
				if ( debugLogLevelEnabled ) {
					log.debug( "Cache total gets and db loads(misses) for previous day (or since last cache recreate): 'total gets': " + cacheGetCount.intValue() 
					+ ", misses: " + cacheDBRetrievalCount.intValue() );
				}
			}
			if ( forcePrintNow ) {
				if ( debugLogLevelEnabled ) {
					log.debug( "Cache total gets and db loads(misses) since last print: 'total gets': " + cacheGetCount.intValue() 
					+ ", misses: " + cacheDBRetrievalCount.intValue() );
				}
			}
			
			prevDayOfYear = nowDayOfYear;
			//  Reset cache hit and miss counters
			cacheGetCount.set(0);
			cacheDBRetrievalCount.set(0);
		}
		
	}


}
