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
	
	+----------------------------------------------+-----------+-------+-------------------------------------------------------------------------------------------------+
	| Name                                         | Data Type | Bytes | Description                                                                                     |
	+==============================================+===========+=======+=================================================================================================+
	| Version                                      | short     | 2     | The file format version for this file. Version 5.                                               |
	+----------------------------------------------+-----------+-------+-------------------------------------------------------------------------------------------------+
	| Full write indicator                         | byte      | 1     | Indicates if the binary file is fully written:                                                  |
	|                                              |           |       | 0 = no, 1 = yes, 2 = undefined (always 2 in S3).                                                |
	+----------------------------------------------+-----------+-------+-------------------------------------------------------------------------------------------------+
	| All Scans: Total Ion Current Computed        | byte      | 1     | Whether the Total Ion Current per scan is computed by the Spectral Storage Service Importer:    |
	|                                              |           |       | 0 = no, 1 = yes. The original scan file did not have this value.                                |
	+----------------------------------------------+-----------+-------+-------------------------------------------------------------------------------------------------+
	| All Scans: Ion injection time NOT populated  | byte      | 1     | Indicates if the Ion Injection Time per scan is missing:                                        |
	|                                              |           |       | 0 = no, 1 = yes. The original scan file did not include this value.                             |
	+----------------------------------------------+-----------+-------+-------------------------------------------------------------------------------------------------+
	| Count of scan levels                         | byte      | 1     | The number of scan levels. For example, 2 for ms1 and ms2.                                      |
	+----------------------------------------------+-----------+-------+-------------------------------------------------------------------------------------------------+


Then for each scan level:
	+------------------------------+-----------+-------+------------------------------------------------------------------------------------------------+
	| Name                         | Data Type | Bytes | Description                                                                                    |
	+==============================+===========+=======+================================================================================================+
	| Scan level                   | byte      | 1     | The scan level. E.g., 1 for ms1 or 2 for ms2.                                                  |
	+------------------------------+-----------+-------+------------------------------------------------------------------------------------------------+
	| Number of scans              | integer   | 4     | Number of scans for this scan level.                                                           |
	+------------------------------+-----------+-------+------------------------------------------------------------------------------------------------+
	| Centroided?                  | byte      | 1     | Designation for centroidedness at this scan level: 0 = only no, 1 = only yes, and 2 = mixed.   |
	+------------------------------+-----------+-------+------------------------------------------------------------------------------------------------+
	| Ion injection time set?      | byte      | 1     | All scans at this scan level have Ion Injection Time populated:                                |
	|                              |           |       | 0 = no, 1 = yes, 2 = some no, some yes.                                                        |
	+------------------------------+-----------+-------+------------------------------------------------------------------------------------------------+
	| Total ion current            | double    | 8     | Total ion current for this scan level from one of the following:                               |
	|                              |           |       |  * Sum of TotalIonCurrent for all scans with this scan level (Total Ion Current Computed == 0) |
	|                              |           |       |  * Same as Total ion current sum of scan peaks (next field) (Total Ion Current Computed == 1)  |
	+------------------------------+-----------+-------+------------------------------------------------------------------------------------------------+
	| Total ion current sum of     | double    | 8     | Sum of intensity of all peaks for all scans with this scan level.                              |
	| scan peaks                   |           |       |                                                                                                |
	+------------------------------+-----------+-------+------------------------------------------------------------------------------------------------+

Then continuing:

	+-------------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	| Name                    | Data Type | Bytes | Description                                                                                |
	+=========================+===========+=======+============================================================================================+
	| Scan numbers sequential | byte      | 1     | Scan numbers are sequential (1,2,3,...,n-1,n). 0 = no, 1 = yes                             |
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
	|                         |           |       |  * 8 = none. There is no offset stored. Assumed that offset between scan numbers is 1.     |
	+-------------------------+-----------+-------+--------------------------------------------------------------------------------------------+
	| Scan size type          | byte      | 1     | The data type used to store the scan size below:                                           |
	|                         |           |       |  * 1 = byte                                                                                |
	|                         |           |       |  * 2 = short                                                                               |
	|                         |           |       |  * 3 = integer                                                                             |
	+-------------------------+-----------+-------+--------------------------------------------------------------------------------------------+

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
