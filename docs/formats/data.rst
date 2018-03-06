===========================================
Spectr's Main Binary Data
===========================================

Internally, spectr stores all data in a proprietary binary format, which is documented here. The format is designed
to be space efficient, while storing enough information to re-generate the index file if necessary. 

Purpose
-----------------------
Note that the spectr binary data formats are not meant to be used as a generalized format for
representing mass spectrometry data. It is designed specifically to serve as a backend storage format
for use by spectr, and all access to the data should be done via the web services provided
by spectr. The documentation of these formats are purely informational.

File Name / AWS S3 Object Name
---------------------------------------------------------
Spectr may be installed on a server with locally attached storage or use Amazon AWS S3 for storage.
In either case, the filename or object name for a data file is the SHA-384 has of the uploaded file plus ".data". E.g., ``98c11ffdfdd540676b1a137cb1a22b2a70350c9a44171d6b1180c6be5cbb2ee3f79d532c8a1dd9ef2e8e08e752a3babb.data``.
Since this is the same hash string used to query the data, spectr can rapidly find the required file in the configured storage directory or S3
bucket. 

Endianness
-----------------------
All shorts, integers, and longs are written high byte first (big-endian). All floats and doubles are represented as ints or longs
according to IEEE 754 floating-point "double format" bit layout, and written as ints and longs. See ``writeByte``, 
``writeShort``, ``writeInt``, ``writeLong``, ``writeFloat``, ``writeDouble`` at https://docs.oracle.com/javase/8/docs/api/java/io/DataOutputStream.html for more information.

File Header
----------------------------------------------------------
This section appears once at the beginning of the file and contains information describing this file. Most of this
information is related to ensuring data integrity by providing multiple avenues to verify stored data has
expected lengths or hashes.

Header sections:


+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
| Name                 | Data Type | Bytes | Description                                                                                |
+======================+===========+=======+============================================================================================+
| Version              | short     | 2     | The file format version for this file. Currently, there is only one version (3).           |
+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
| Full write indicator | byte      | 1     | Whether or not this file is fully written. 0 = no, 1 = yes, 2 = undefined (always 2 in S3) |
+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
| Length of this file  | long      | 8     | Length of this binary file (not present when using S3)                                     |
+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
| Header length        | short     | 2     | Length of the header (in bytes), up to an excluding this value.                            |
+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
| Scan file length     | long      | 8     | Length of scan file used to generate this file.                                            |
+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
| SHA 384 hash length  | short     | 2     | Length of SHA 384 hash, in bytes.                                                          |
+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
| SHA 384 hash bytes   | byte[]    |       | A byte array with a length equal to above.                                                 |
+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
| SHA 512 hash length  | short     | 2     | Length of SHA 512 hash, in bytes.                                                          |
+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
| SHA 512 hash bytes   | byte[]    |       | A byte array with a length equal to above.                                                 |
+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
| SHA-1 hash length    | short     | 2     | Length of SHA-1 hash, in bytes.                                                            |
+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+
| SHA-1 has bytes      | byte[]    |       | A byte array with a length equal to above.                                                 |
+----------------------+-----------+-------+--------------------------------------------------------------------------------------------+


Scan Data
----------------------------------------------------------
Each scan appears sequentially following the header above. Each scan contains the following data:

Scan Header:
^^^^^^^^^^^^^^^^^^^^^^
Each scan contains the following information preceding the peak list.

+----------------------+-----------+-------+----------------------------------------------------------------+
| Name                 | Data Type | Bytes | Description                                                    |
+======================+===========+=======+================================================================+
| Scan level           | byte      | 1     | The scan level. Commonly 1 (ms1) or 2 (ms2).                   |
+----------------------+-----------+-------+----------------------------------------------------------------+
| Scan number          | integer   | 4     | Scan number for this scan from original file.                  |
+----------------------+-----------+-------+----------------------------------------------------------------+
| Retention time       | float     | 4     | Retention time in seconds                                      |
+----------------------+-----------+-------+----------------------------------------------------------------+
| Centroid?            | byte      | 1     | 0 if not centroided, 1 if centroided                           |
+----------------------+-----------+-------+----------------------------------------------------------------+
| Parent scan number   | integer   | 4     | Scan number of parent scan (only present for ms2 and up)       |
+----------------------+-----------+-------+----------------------------------------------------------------+
| Precursor charge     | byte      | 1     | Reported charge of precursor ion (only present for ms2 and up) |
+----------------------+-----------+-------+----------------------------------------------------------------+
| Precursor m/z        | double    | 8     | Reported m/z of precursor ion (only present for ms2 and up)    |
+----------------------+-----------+-------+----------------------------------------------------------------+
| Peak count           | integer   | 4     | Number of peaks in scan                                        |
+----------------------+-----------+-------+----------------------------------------------------------------+
| Scan data length     | integer   | 4     | Length in bytes of compressed scan data                        |
+----------------------+-----------+-------+----------------------------------------------------------------+
| Compressed scan data | byte[]    |       | A byte array compressed via GZIP. (See below)                  |
+----------------------+-----------+-------+----------------------------------------------------------------+

Peak Byte Array
^^^^^^^^^^^^^^^^^^^^^^
When uncompressed, the compressed scan data is an array of bytes, where each chunk of 12 bytes contains the following:

+----------------------+-----------+-------+----------------------------------------------------------------+
| Name                 | Data Type | Bytes | Description                                                    |
+======================+===========+=======+================================================================+
| m/z                  | double    | 8     | M/Z of peak                                                    |
+----------------------+-----------+-------+----------------------------------------------------------------+
| Intensity            | float     | 4     | Intensity of peak                                              |
+----------------------+-----------+-------+----------------------------------------------------------------+

