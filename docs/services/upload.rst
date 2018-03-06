===========================================
Spectr Upload Web Services
===========================================
This document contains documentation for the web services for uploading data to spectr.

Uploading a scan file to spectr us a multi-step affair. The steps are:

  1. Initiate upload of scan file with ``/update/uploadScanFile_Init_XML``
  2. Use the returned key to send data to spectr.
     Use ``/update/uploadScanFile_uploadScanFile_XML`` to send data from a local file or 
     ``/update/uploadScanFile_addScanFileInS3Bucket_XML`` to send data from S3.
  3. Commit the submission for processing with ``/update/uploadScanFile_Submit_XML``
  4. Use the returned key from 3 to query status and retrieve final hash key for uploaded file with ``/update/uploadedScanFile_Status_API_Key_XML``.

Web Services Details:
================================

Initiate upload of scan file
---------------------------------------------------------

+----------------------+------------------------------------------------------------------------------------------------------------------------+
| **URI**              | /update/uploadScanFile_Init_XML                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Method               | POST                                                                                                                   |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| URL Params           | None                                                                                                                   |
| (GET)                |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| POST data            | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |   <uploadScanFile_Init_Request></uploadScanFile_Init_Request>                                                          |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Success Response     | Success (200 OK)                                                                                                       |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Error Response       | Unauthorized (401)                                                                                                     |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Sample Response      | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |  <uploadScanFile_Init_Response>                                                                                        |
|                      |    <statusSuccess>true</statusSuccess>                                                                                 |
|                      |    <uploadScanFileTempKey>aekd838dkd</uploadScanFileTempKey>                                                           |
|                      |    <maxUploadFileSize>10000000000</maxUploadFileSize>                                                                  |
|                      |    <maxUploadFileSizeFormatted>10,000,000,000</maxUploadFileSizeFormatted>                                             |
|                      |  </uploadScanFile_Init_Response>                                                                                       |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Response Elements    |  * <statusSuccess> - contains "true" or "false"                                                                        |
|                      |  * <uploadScanFileTempKey> - String, identifier to use when submitting new file                                        |
|                      |  * <maxUploadFileSize> - Integer, maximum allowed upload scan file size in bytes                                       |
|                      |  * <maxUploadFileSizeFormatted> - String, number above, but with commas                                                |
+----------------------+------------------------------------------------------------------------------------------------------------------------+


Submit Data from Scan File
---------------------------------------------------------
Send the contents of a scan file, byte for byte.

+----------------------+------------------------------------------------------------------------------------------------------------------------+
| **URI**              | /update/uploadScanFile_uploadScanFile_XML                                                                              |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Method               | POST                                                                                                                   |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| URL Params           |   * uploadScanFileTempKey - Value returned in <uploadScanFileTempKey> from call to /update/uploadScanFile_Init_XML     |
| (GET)                |   * scan_filename_suffix - (Optional) The suffix of the scan filename - Allowed values are ".mzML" and ".mzXML"        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| POST data            |   * POST headers:  Content Length must be set                                                                          |
|                      |   * POST body is the scan file contents.  Chunked data is not allowed                                                  |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Success Response     | Success (200 OK)                                                                                                       |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Error Response       | Bad Request (400), Unauthorized (401)                                                                                  |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Sample Response      | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |    <UploadScanFile_UploadScanFile_Response>                                                                            |
|                      |       <statusSuccess>true</statusSuccess>                                                                              |
|                      |       <uploadScanFileTempKey_NotFound>false</uploadScanFileTempKey_NotFound>                                           |
|                      |       <uploadedFileHasNoFilename>false</uploadedFileHasNoFilename>                                                     |
|                      |       <uploadedFileSuffixNotValid>false</uploadedFileSuffixNotValid>                                                   |
|                      |    </UploadScanFile_UploadScanFile_Response>                                                                           |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Response Elements    |  * statusSuccess - true if successful, false if not                                                                    |
|                      |  * uploadScanFileTempKey_NotFound - Scan file temp key was not valid                                                   |
|                      |  * objectKeyOrFilenameSuffixNotValid - Could not determine data file was a valid type (mzML or mzXML)                  |
|                      |  * uploadedFileHasNoFilename - Uploaded file has no file name                                                          |
|                      |  * fileSizeLimitExceeded - (Optional) true if file size limit was exceeded                                             |
|                      |  * maxSize - (Optional) Integer, maximum allowed upload scan file size in bytes                                        |
|                      |  * maxSizeFormatted  - (Optional) Number above, but with commas                                                        |
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

