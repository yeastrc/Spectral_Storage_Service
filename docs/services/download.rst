===========================================
Download Data
===========================================
This document contains documentation for the web services for downloading data from spectr.

Scan Data:
===============================
For all web services that return scan data, this is the XML format used:

.. code-block:: xml

 <scan level="2" scanNumber="3305" retentionTime="332.33993" isCentroid="0">
    <peaks>
       <peak mz="899.999484" intensity="19999338.33" />
       <peak mz="903.399883" intensity="88373.31" />
       <peak mz="1003.87368" intensity="7733.84" />
       <!-- ... peak element for every peak in the scan ... -->
    </peaks>
 </scan>

Scan attributes
------------------

 * level - Scan level (1 for ms1, 2 for ms2 and so on)
 * scanNumber - Scan number for this scan from the original spectral file
 * retentionTime - Retention time for this scan (in seconds)
 * isCentroid - Whether or not this scan is centroided (0 for false, 1 for true)
 * parentScanNumber - (Only for scan level > 1) Parent scan number
 * precursorCharge - (Only for scan level > 1) Charge of precursor ion
 * precursor_M_Over_Z - (Only for scan level > 1) m/z of precursor ion (double)

Peak attributes
--------------------

 * mz - The m/z of the peak (double)
 * intensity - The intensity of the peak (float)

APINotFound
=======================
All responses will contain the ``<status_scanFileAPIKeyNotFound>YES</status_scanFileAPIKeyNotFound>`` sub-element if
the supplied API key was invalid.


Web Services Details:
================================

Get Scan Numbers
---------------------------------------------------------
Get all scan numbers present in an uploaded file, by scan level

+----------------------+------------------------------------------------------------------------------------------------------------------------+
| **URI**              | /query/getScanNumbers_XML                                                                                              |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Method               | POST                                                                                                                   |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| URL Params           | None                                                                                                                   |
| (GET)                |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| POST data            | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |      <get_ScanNumbers_Request scanFileAPIKey="">                                                                       |
|                      |          <scanLevelsToInclude>                                                                                         |
|                      |              <scanLevelToInclude>n</scanLevelToInclude>                                                                |
|                      |              <!-- scanLevelToInclude element for each scan level to include -->                                        |
|                      |          </scanLevelsToInclude>                                                                                        |
|                      |          <scanLevelsToExclude>                                                                                         |
|                      |              <scanLevelToExclude>n</scanLevelToExclude>                                                                |
|                      |              <!-- scanLevelToExclude element for each scan level to exclude -->                                        |
|                      |          </scanLevelsToExclude>                                                                                        |
|                      |      </get_ScanNumbers_Request>                                                                                        |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Post attributes      |  * scanFileAPIKey -  API Key for scan data                                                                             |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Post elements        |  * scanLevelsToInclude - (Optional) A collection of ``scanLevelToInclude`` elements.                                   |
|                      |  * scanLevelToInclude - A scan level to include. (1 for ms1, 2 for ms2, and so on)                                     |
|                      |  * scanLevelsToExclude - (Optional) A collection of ``scanLevelToExclude`` elements.                                   |
|                      |  * scanLevelToExclude - A scan level to exclude. (1 for ms1, 2 for ms2, and so on)                                     |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Success Response     | Success (200 OK)                                                                                                       |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Error Response       | Bad Request (400), Unauthorized (401)                                                                                  |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Sample Response      | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |  <uploadScanFile_Submit_Response>                                                                                      |
|                      |    <status_scanFileAPIKeyNotFound>NO</status_scanFileAPIKeyNotFound>                                                   |
|                      |    <scanNumbers>                                                                                                       |
|                      |        <scanNumber>1</scanNumber>                                                                                      |
|                      |        <scanNumber>2</scanNumber>                                                                                      |
|                      |        <scanNumber>3</scanNumber>                                                                                      |
|                      |        <scanNumber>4</scanNumber>                                                                                      |
|                      |   </scanNumbers>                                                                                                       |
|                      |  </uploadScanFile_Submit_Response>                                                                                     |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Response Elements    |  * <status_scanFileAPIKeyNotFound> - If YES, API key was not found.                                                    |
|                      |  * <scanNumbers> - Collection of ``scanNumber`` elements.                                                              |
|                      |  * <scanNumber> - A scan number from the original spectral file.                                                       |
+----------------------+------------------------------------------------------------------------------------------------------------------------+


Get Scan Data From File
---------------------------------------------------------
Get the scan data for a list of scan numbers.

+----------------------+------------------------------------------------------------------------------------------------------------------------+
| **URI**              | /query/getScanDataFromScanNumbers_XML                                                                                  |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Method               | POST                                                                                                                   |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| URL Params           | None                                                                                                                   |
| (GET)                |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| POST data            | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |      <get_ScanDataFromScanNumbers_Request scanFileAPIKey="">                                                           |
|                      |        <scanNumbers>                                                                                                   |
|                      |            <scanNumber>1</scanNumber>                                                                                  |
|                      |            <scanNumber>2</scanNumber>                                                                                  |
|                      |            <scanNumber>3</scanNumber>                                                                                  |
|                      |            <scanNumber>4</scanNumber>                                                                                  |
|                      |        </scanNumbers>                                                                                                  |
|                      |      </get_ScanDataFromScanNumbers_Request>                                                                            |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Post attributes      |  * scanFileAPIKey -  API Key for scan data                                                                             |
|                      |  * includeParentScans - (Optional) allowed values: "no", "immediate_parent", "all_parents"                             |
|                      |  * excludeReturnScanPeakData - (Optional) Default: no. Allowed values: "no", "yes". If yes, do not return peak data    |
|                      |  * mzLowCutoff - (Optional) do not return any peaks with mz below this cutoff.                                         |
|                      |  * mzHighCutoff - (Optional) do not return any peaks with mz above this cutoff.                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Post elements        |  * <scanNumbers> - Collection of ``scanNumber`` elements.                                                              |
|                      |  * <scanNumber> - A scan number from the original spectral file.                                                       |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Success Response     | Success (200 OK)                                                                                                       |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Error Response       | Bad Request (400), Unauthorized (401)                                                                                  |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Sample Response      | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |  <uploadScanFile_Submit_Response>                                                                                      |
|                      |    <status_scanFileAPIKeyNotFound>NO</status_scanFileAPIKeyNotFound>                                                   |
|                      |    <scanNumbers>                                                                                                       |
|                      |        <scanNumber>1</scanNumber>                                                                                      |
|                      |        <scanNumber>2</scanNumber>                                                                                      |
|                      |        <scanNumber>3</scanNumber>                                                                                      |
|                      |        <scanNumber>4</scanNumber>                                                                                      |
|                      |   </scanNumbers>                                                                                                       |
|                      |  </uploadScanFile_Submit_Response>                                                                                     |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Response Elements    |  * <status_scanFileAPIKeyNotFound> - If YES, API key was not found.                                                    |
|                      |  * <scanNumbers> - Collection of ``scanNumber`` elements.                                                              |
|                      |  * <scanNumber> - A scan number from the original spectral file.                                                       |
+----------------------+------------------------------------------------------------------------------------------------------------------------+



Add Scan file in S3 Bucket
---------------------------------------------------------
Submit a file to spectr, if that file is already on Amazon S3 and spectr has read access to that object. Prevents needlessly sending file.

+----------------------+------------------------------------------------------------------------------------------------------------------------+
| **URI**              | /update/uploadScanFile_addScanFileInS3Bucket_XML                                                                       |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Method               | POST                                                                                                                   |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| URL Params           | None                                                                                                                   |
| (GET)                |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| POST data            | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |      <uploadScanFile_AddScanFileInS3Bucket_Request                                                                     |
|                      |          uploadScanFileTempKey=""                                                                                      |
|                      |          s3Bucket=""                                                                                                   | 
|                      |          s3ObjectKey=""                                                                                                |
|                      |          scanFilenameSuffix=""                                                                                         |
|                      |          s3Region=""                                                                                                   |
|                      |       />                                                                                                               |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Post attributes      |  * uploadScanFileTempKey - Value returned in <uploadScanFileTempKey> from call to /update/uploadScanFile_Init_XML      |
|                      |  * s3Bucket - S3 bucket name                                                                                           |
|                      |  * s3ObjectKey - S3 object key                                                                                         |
|                      |  * scanFilenameSuffix - (Optional) Suffix of scan file (e.g., mzML or mzXML)                                           |
|                      |  * s3Region - (Optional) AWS region of object                                                                          |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Success Response     | Success (200 OK)                                                                                                       |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Error Response       | Bad Request (400), Unauthorized (401)                                                                                  |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Sample Response      | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |  <uploadScanFile_AddScanFileInS3Bucket_Response                                                                        |
|                      |    statusSuccess="true"                                                                                                |
|                      |    uploadScanFileTempKey_NotFound="false"                                                                              |
|                      |    objectKeyOrFilenameSuffixNotValid="false"                                                                           |
|                      |    uploadScanFileS3BucketOrObjectKey_NotFound="false"                                                                  |
|                      |    uploadScanFileS3BucketOrObjectKey_PermissionError="false" />                                                        |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Response attributes  |  * statusSuccess - true if successful, false if not                                                                    |
|                      |  * uploadScanFileTempKey_NotFound - Scan file temp key was not valid                                                   |
|                      |  * objectKeyOrFilenameSuffixNotValid - Could not determine data file was a valid type (mzML or mzXML)                  |
|                      |  * uploadScanFileS3BucketOrObjectKey_NotFound - S3 object was not found                                                |
|                      |  * uploadScanFileS3BucketOrObjectKey_PermissionError - No permissions to read S3 object                                |
|                      |  * fileSizeLimitExceeded - (Optional) true if file size limit was exceeded                                             |
|                      |  * maxSize - (Optional) Integer, maximum allowed upload scan file size in bytes                                        |
|                      |  * maxSizeFormatted  - (Optional) Number above, but with commas                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+


Commit the upload of a scan file
---------------------------------------------------------
After the scan data have been sent (or a S3 object designated), this must be called to complete processing of the file

+----------------------+------------------------------------------------------------------------------------------------------------------------+
| **URI**              | /update/uploadScanFile_Submit_XML                                                                                      |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Method               | POST                                                                                                                   |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| URL Params           | None                                                                                                                   |
| (GET)                |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| POST data            | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |      <uploadScanFile_Submit_Request                                                                                    |
|                      |         uploadScanFileTempKey=""                                                                                       |
|                      |      />                                                                                                                |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Post attributes      |  * uploadScanFileTempKey - Value returned in <uploadScanFileTempKey> from call to /update/uploadScanFile_Init_XML      |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Success Response     | Success (200 OK)                                                                                                       |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Error Response       | Bad Request (400), Unauthorized (401)                                                                                  |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Sample Response      | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |  <uploadScanFile_Submit_Response                                                                                       |
|                      |    statusSuccess="true"                                                                                                |
|                      |    uploadScanFileTempKey_NotFound="false"                                                                              |
|                      |    noUploadedScanFile="false"                                                                                          |
|                      |    scanProcessStatusKey="dkdk39dkd93kdkd"                                                                              |
|                      |  />                                                                                                                    |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Response attributes  |  * statusSuccess - true if successful, false if not                                                                    |
|                      |  * uploadScanFileTempKey_NotFound - Scan file temp key was not valid                                                   |
|                      |  * noUploadedScanFile - Commit called without submitting scan file first                                               |
|                      |  * scanProcessStatusKey - Key to use to query the status of processing and obtain final key                            |
+----------------------+------------------------------------------------------------------------------------------------------------------------+


Get the final key (API key)
-----------------------------------------------------------------------------------------------
Get the final key for the uploaded scan file (used to query data from file later).

+----------------------+------------------------------------------------------------------------------------------------------------------------+
| **URI**              | /update/uploadedScanFile_Status_API_Key_XML                                                                            |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Method               | POST                                                                                                                   |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| URL Params           | None                                                                                                                   |
| (GET)                |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| POST data            | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |      <get_UploadedScanFileInfo_Request                                                                                 |
|                      |         scanProcessStatusKey=""                                                                                        |
|                      |      />                                                                                                                |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Post attributes      |  * scanProcessStatusKey - Key to use to query the status of processing and obtain final key                            |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Success Response     | Success (200 OK)                                                                                                       |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Error Response       | Bad Request (400), Unauthorized (401)                                                                                  |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Sample Response      | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |  <get_UploadedScanFileInfo_Response                                                                                    |
|                      |    scanFileAPIKey="98c11ffdfdd540676b1a137cb1a22b2a70350c9a44171d6b1180c6be5cbb2ee3f79d532c8a1dd9ef2e8e08e752a3babb"   |
|                      |    scanProcessStatusKey_NotFound="false"                                                                               |
|                      |    status="success"                                                                                                    |
|                      |    failMessage=""                                                                                                      |
|                      |  />                                                                                                                    |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Response attributes  |  * scanFileAPIKey - Final hash key used to query scan data from this file (only here if processing is complete)        |
|                      |  * scanProcessStatusKey_NotFound - Invalid scan processing status key                                                  |
|                      |  * status - "pending", "success", "fail", or "deleted"                                                                 |
|                      |  * failMessage - If failed, a message describing the reason for failure                                                |
+----------------------+------------------------------------------------------------------------------------------------------------------------+



Delete scan processing key
-----------------------------------------------------------------------------------------------
Mark a scan processing key as deleted. Ensures not accidentally used again. Note that these keys do age and are automatically deleted with time.

+----------------------+------------------------------------------------------------------------------------------------------------------------+
| **URI**              | /update/uploadedScanFile_Delete_For_ScanProcessStatusKey_XML                                                           |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Method               | POST                                                                                                                   |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| URL Params           | None                                                                                                                   |
| (GET)                |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| POST data            | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |      <get_UploadedScanFileInfo_Request                                                                                 |
|                      |         scanProcessStatusKey=""                                                                                        |
|                      |      />                                                                                                                |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Post attributes      |  * scanProcessStatusKey - Key to use to query the status of processing and obtain final key                            |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Success Response     | Success (200 OK)                                                                                                       |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Error Response       | Bad Request (400), Unauthorized (401)                                                                                  |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Sample Response      | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |  <uploadScanFile_Delete_For_ScanProcessStatusKey_Request                                                               |
|                      |    scanProcessStatusKey_NotFound="false"                                                                               |
|                      |    statusSuccess="true"                                                                                                |
|                      |  />                                                                                                                    |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Response attributes  |  * statusSuccess - Whether or not the request was successfull. "true" or "false"                                       |
|                      |  * scanProcessStatusKey_NotFound - Invalid scan processing status key                                                  |
+----------------------+------------------------------------------------------------------------------------------------------------------------+

