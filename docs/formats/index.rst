===========================================
Spectr's Main Index File
===========================================

The index file used to rapidly find relevant scans and
the location of specific scans in the binary scan data file. This
file is, itself, binary, to save space. All data necessary to regenerate
this index file are present in the data file.

Purpose
-----------------------
Note that the spectr binary data formats are not meant to be used as a generalized format for
representing mass spectrometry data. It is designed specifically to serve as a backend storage format
for use by spectr, and all access to the data should be done via the web services provided
by spectr. The documentation of these formats are purely informational.

File Name / AWS S3 Object Name
---------------------------------------------------------
Spectr may be installed on a server with locally attached storage or use Amazon AWS S3 for storage.
In either case, the filename or object name for the index file is the SHA-384 has of the uploaded file plus ".index". E.g., ``98c11ffdfdd540676b1a137cb1a22b2a70350c9a44171d6b1180c6be5cbb2ee3f79d532c8a1dd9ef2e8e08e752a3babb.index``.
Since this is the same hash string used to query the data, spectr can rapidly find the required file in the configured storage directory or S3
bucket. 

Endianness
-----------------------
All shorts, integers, and longs are written high byte first (big-endian). All floats and doubles are represented as ints or longs
according to IEEE 754 floating-point "double format" bit layout, and written as ints and longs. See ``writeByte``, 
``writeShort``, ``writeInt``, ``writeLong``, ``writeFloat``, ``writeDouble`` at https://docs.oracle.com/javase/8/docs/api/java/io/DataOutputStream.html for more information.



File Version
----------------------------------------------------------
The latest version is 5.
All newly created files will be that version.
There were versions 3 and 4.
An existing installation of Spectral Storage Service may have files with those versions.

File Format
----------------------------------------------------------

File Header
----------------------------------------------------------
This section appears once at the beginning of the file and contains information describing this file.

Header sections:

	+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	| Name                 | Data Type | Bytes | Description                                                                                |
	+======================+===========+=======+============================================================================================+
	| Version              | short     | 2     | The file format version for this file. Currently, there is only one version (3).           |
	+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	| Full write indicator | byte      | 1     | Is the binary file fully written? 0 = no, 1 = yes, 2 = undefined (always 2 in S3)          |
	+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	| Centroided?          | byte      | 1     | A whole-file designation for centroidedness. 0 = only no, 1 = only yes, and 2 = mixed.     |
	+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	| Count of scan levels | byte      | 1     | Number of scan levels. E.g., 2 for ms1 and ms2.                                            |
	+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	
New Text

Field:  	  
Version  

Description:
Version 5

Field:
Full write indicator
	
Description:	
NO Change	

Remove Field: Centroided? from this table.  Moved to per scan level section

Field:
totalIonCurrent_ForEachScan_ComputedFromScanPeaks  byte

Description:
Original Scan File did NOT have Total Ion Current Per Scan so it is computed in Spectral Storage Service Importer from Scan Peaks.
Not populated for Data File Version < 5.
 0 = no, 1 = yes

Field:
ionInjectionTime_NotPopulated  byte

Description:
*	Original Scan File did NOT have Ion Injection Time Per Scan.
	 * Not populated for Data File Version < 5.
	 * @return - null if not stored in data file (Old Version of data file)


Field:
Count of scan levels

Description:	
NO Change	

Then for each scan level:

	+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	| Name                 | Data Type | Bytes | Description                                                                                |
	+======================+===========+=======+============================================================================================+
	| Scan level           | byte      | 1     | The scan level. E.g., 1 for ms1 or 2 for ms2.                                              |
	+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	| Number of scans      | integer   | 4     | Number of scan for this scan level                                                         |
	+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	| Total ion current    | double    | 8     | Total ion current for this scan level (sum of intensity of all peaks)                      |
	+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+


New Text for section: Then for each scan level:


Field:
Count of scan levels

Description:	
NO Change	

Field:
Number of scans

Description:	
NO Change	

Field:
 Centroided?  Moved From above but for this scan level


Field:
isIonInjectionTime_Set_ScanLevel  byte

Description:
All scans at this can level have Ion Injection Time populated.  0 = no, 1 = yes, 2 = some no, some yes.
		/**
		 * Is the IonInjectionTime Set values for this scan level
		 * 0 - false - IonInjectionTime == null
		 * 1 - true - IonInjectionTime != null
		 * 2 - both - both IonInjectionTime == null and IonInjectionTime != null
		 *            ScanHasIonInjectionTimeConstants.SCAN_HAS_ION_INJECTION_TIME_VALUES_IN_FILE_BOTH
		 */

Field:
totalIonCurrent_SumOf_TotalIonCurrent_OfScans  double

Description:
		/**
		 * This is either ( Based On Value of totalIonCurrent_ForEachScan_ComputedFromScanPeaks ):
		 * A) Sum of TotalIonCurrent for all scans with this scan level ( totalIonCurrent_ForEachScan_ComputedFromScanPeaks == 0 )
		 * B) Same as totalIonCurrent_SumOf_IntensityOfPeaks ( totalIonCurrent_ForEachScan_ComputedFromScanPeaks == 1 )
		 */
		 
Field: 
totalIonCurrent_SumOf_IntensityOfPeaks   double

Description:
Sum of intensity of all peaks for all scans with this scan level


*************

Then continuing:

	+-------------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	| Name                    | Data Type | Bytes | Description                                                                                |
	+=========================+===========+=======+============================================================================================+
	| Scan number sorted?     | byte      | 1     | 0 if not sorted by scan number. 1 if sorted by scan number.                                |
	+-------------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	| Ret. time sorted?       | byte      | 1     | 0 if not sorted by retention time. 1 if sorted by retention time.                          |
	+-------------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	| Scan count              | integer   | 4     | Total number of scans in file                                                              |
	+-------------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	| Total scan data size    | long      | 8     | Total size of data file, excluding header.                                                 |
	+-------------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	| First scan number       | integer   | 4     | First scan number in file                                                                  |
	+-------------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	| First scan location     | long      | 8     | Byte location in data file of first scan                                                   |
	+-------------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	| Scan number offset type | byte      | 1     | The data type used to store the offset between scan numbers below:                         |
	|                         |           |       |  * 1 = byte                                                                                |
	|                         |           |       |  * 2 = short                                                                               |
	|                         |           |       |  * 3 = integer                                                                             |
	|                         |           |       |  * 8 = none. There is no offset stored. Assumed that offset between scans is 1.            |
	+-------------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	| Scan size type          | byte      | 1     | The data type used to store the scan size below:                                           |
	|                         |           |       |  * 1 = byte                                                                                |
	|                         |           |       |  * 2 = short                                                                               |
	|                         |           |       |  * 3 = integer                                                                             |
	+-------------------------+-----------+-------+--------------------------------------------------------------------------------------------+

New Text:

Field:
Scan number sorted?
 
This is incorrect.  It should be:  Scan numbers sequential
 
Description:
The scan numbers are sequential (1,2,3,...,n-1,n)  1 = yes, 0 = no

Rest is Unchanged


Then for each scan:

	+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	| Name                 | Data Type | Bytes | Description                                                                                |
	+======================+===========+=======+============================================================================================+
	| Scan size            | See above | *     | The number of bytes for this scan in the data file (including header).                     |
	+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	| Scan number offset   | See above | *     | Offset from previous scan number (ie: scan number - previous scan number).                 |
	|                      |           |       | Not present in type above is 8, which assumes all offsets are 1                            |
	+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	| Scan level           | byte      | 1     | The scan level. E.g., 1 for ms1 or 2 for ms2.                                              |
	+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	| Retention time       | float     | 4     | Retention time for this scan.                                                              |
	+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+

Table data unchanged
