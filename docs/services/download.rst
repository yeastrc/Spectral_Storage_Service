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
|                      |  <get_ScanNumbers_Response>                                                                                            |
|                      |    <status_scanFileAPIKeyNotFound>NO</status_scanFileAPIKeyNotFound>                                                   |
|                      |    <scanNumbers>                                                                                                       |
|                      |        <scanNumber>1</scanNumber>                                                                                      |
|                      |        <scanNumber>2</scanNumber>                                                                                      |
|                      |        <scanNumber>3</scanNumber>                                                                                      |
|                      |        <scanNumber>4</scanNumber>                                                                                      |
|                      |   </scanNumbers>                                                                                                       |
|                      |  </get_ScanNumbers_Response>                                                                                           |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Response Elements    |  * <status_scanFileAPIKeyNotFound> - If YES, API key was not found.                                                    |
|                      |  * <scanNumbers> - Collection of ``scanNumber`` elements.                                                              |
|                      |  * <scanNumber> - A scan number from the original spectral file.                                                       |
+----------------------+------------------------------------------------------------------------------------------------------------------------+


Get Scan Data From File (for specific scan numbers)
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
|                      |  <get_ScanDataFromScanNumbers_Response>                                                                                |
|                      |    <status_scanFileAPIKeyNotFound>NO</status_scanFileAPIKeyNotFound>                                                   |
|                      |    <scans>                                                                                                             |
|                      |        <!-- scan list, see top of document for more information -->                                                    |
|                      |   </scans>                                                                                                             |
|                      |  </uploadScanFile_Submit_Response>                                                                                     |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Response attributes  |  * tooManyScansToReturn - if present and "true", number of scans exceeded max number of scans that can be returned     |
|                      |  * MaxScanNumbersAllowed - only present if above is "true". The max number of scans that can be returned               |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Response Elements    |  * <status_scanFileAPIKeyNotFound> - If YES, API key was not found.                                                    |
|                      |  * <scans>  - The scan data. See top of page.                                                                          |
+----------------------+------------------------------------------------------------------------------------------------------------------------+



Get Scan Data From File
------------------------------------------------------------
Get the scan data for a retention time window, m/z range, and scan level.

+----------------------+------------------------------------------------------------------------------------------------------------------------+
| **URI**              | /query/getScansDataFromRetentionTimeRange_XML                                                                          |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Method               | POST                                                                                                                   |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| URL Params           | None                                                                                                                   |
| (GET)                |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| POST data            | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |      <get_ScanNumbersFromRetentionTimeRange_Request                                                                    |
|                      |        scanFileAPIKey="">                                                                                              |
|                      |        excludeReturnScanPeakData="">                                                                                   |
|                      |        retentionTimeStart="">                                                                                          |
|                      |        retentionTimeEnd="">                                                                                            |
|                      |        mzLowCutoff="">                                                                                                 |
|                      |        mzHighCutoff="">                                                                                                |
|                      |        scanLevel ="">                                                                                                  |
|                      |      </get_ScanNumbersFromRetentionTimeRange_Request>                                                                  |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Post attributes      |  * scanFileAPIKey -  API Key for scan data                                                                             |
|                      |  * excludeReturnScanPeakData - (Optional) Default: no. Allowed values: "no", "yes". If yes, do not return peak data    |
|                      |  * retentionTimeStart - (Required) Lower end of retention time window                                                  |
|                      |  * retentionTimeEnd - (Required) Upper end of retention time window                                                    |
|                      |  * mzLowCutoff - (Optional) do not return any peaks with mz below this cutoff.                                         |
|                      |  * mzHighCutoff - (Optional) do not return any peaks with mz above this cutoff.                                        |
|                      |  * scanLevel - (Optional) Only return data for scans with this scan level.                                             |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Success Response     | Success (200 OK)                                                                                                       |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Error Response       | Bad Request (400), Unauthorized (401)                                                                                  |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Sample Response      | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |  <get_ScansDataFromRetentionTimeRange_Response>                                                                        |
|                      |    <status_scanFileAPIKeyNotFound>NO</status_scanFileAPIKeyNotFound>                                                   |
|                      |    <scans>                                                                                                             |
|                      |        <!-- scan list, see top of document for more information -->                                                    |
|                      |   </scans>                                                                                                             |
|                      |  </get_ScansDataFromRetentionTimeRange_Response>                                                                       |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Response attributes  |  * tooManyScansToReturn - if present and "true", number of scans exceeded max number of scans that can be returned     |
|                      |  * MaxScanNumbersAllowed - only present if above is "true". The max number of scans that can be returned               |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Response Elements    |  * <status_scanFileAPIKeyNotFound> - If YES, API key was not found.                                                    |
|                      |  * <scans>  - The scan data. See top of page.                                                                          |
+----------------------+------------------------------------------------------------------------------------------------------------------------+




Get Scan Retention Times
---------------------------------------------------------
Get retention times for one or more scans.

+----------------------+------------------------------------------------------------------------------------------------------------------------+
| **URI**              | /query/getScanRetentionTimes_XML                                                                                       |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Method               | POST                                                                                                                   |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| URL Params           | None                                                                                                                   |
| (GET)                |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| POST data            | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |      <get_ScanRetentionTimes_Request scanFileAPIKey="">                                                                |
|                      |        <scanNumbers>                                                                                                   |
|                      |            <scanNumber>1</scanNumber>                                                                                  |
|                      |            <scanNumber>2</scanNumber>                                                                                  |
|                      |            <scanNumber>3</scanNumber>                                                                                  |
|                      |            <scanNumber>4</scanNumber>                                                                                  |
|                      |        </scanNumbers>                                                                                                  |
|                      |         <!-- below is only included if scan numbers are not included -->                                               |
|                      |         <scanLevelsToInclude>                                                                                          |
|                      |             <scanLevelToInclude>n</scanLevelToInclude>                                                                 |
|                      |             <!-- scanLevelToInclude element for each scan level to include -->                                         |
|                      |         </scanLevelsToInclude>                                                                                         |
|                      |         <scanLevelsToExclude>                                                                                          |
|                      |             <scanLevelToExclude>n</scanLevelToExclude>                                                                 |
|                      |             <!-- scanLevelToExclude element for each scan level to exclude -->                                         |
|                      |         </scanLevelsToExclude>                                                                                         |
|                      |      </get_ScanDataFromScanNumbers_Request>                                                                            |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Post attributes      |  * scanFileAPIKey -  API Key for scan data                                                                             |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Post elements        |  * scanNumbers - Collection of ``scanNumber`` elements.                                                                |
|                      |  * scanNumber - A scan number from the original spectral file.                                                         |
|                      |  * scanLevelsToInclude - (Optional) A collection of ``scanLevelToInclude`` elements.                                   |
|                      |  * scanLevelToInclude - A scan level to include. (1 for ms1, 2 for ms2, and so on)                                     |
|                      |  * scanLevelsToExclude - (Optional) A collection of ``scanLevelToExclude`` elements.                                   |
|                      |  * scanLevelToExclude - A scan level to exclude. (1 for ms1, 2 for ms2, and so on)                                     |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Notes                |  * If scan numbers are present, scanLevelsToInclude and scanLevelsToExclude should not be present.                     |
|                      |  * Otherwise, all scans will be returned, filtered on scanLevelsToInclude and scanLevelsToExclude.                     |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Success Response     | Success (200 OK)                                                                                                       |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Error Response       | Bad Request (400), Unauthorized (401)                                                                                  |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Sample Response      | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |  <get_ScanRetentionTimes_Response>                                                                                     |
|                      |    <status_scanFileAPIKeyNotFound>NO</status_scanFileAPIKeyNotFound>                                                   |
|                      |    <scanParts>                                                                                                         |
|                      |        <scanPart scanNumber="1" level="1" retentionTime="1.3346" />                                                    |
|                      |        <scanPart scanNumber="2" level="1" retentionTime="2.3346" />                                                    |
|                      |        <scanPart scanNumber="3" level="2" retentionTime="3.8346" />                                                    |
|                      |        <!-- repeat for all returned scans -->                                                                          |
|                      |   </scanParts>                                                                                                         |
|                      |  </get_ScanRetentionTimes_Response>                                                                                    |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Response Elements    |  * <status_scanFileAPIKeyNotFound> - If YES, API key was not found.                                                    |
|                      |  * <scanParts> - A collection of scanPart elements                                                                     |
|                      |  * <scanPart> - A report of the retention time for a given scan.                                                       |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| scanPart attributes  |  * scanNumber - The scan number being reported on                                                                      |
|                      |  * level - The level for this scan (e.g. "2" for ms2)                                                                  |
|                      |  * retentionTime - The retention time for this scan (in seconds)                                                       |
+----------------------+------------------------------------------------------------------------------------------------------------------------+



Get Scan Level Summary Data
---------------------------------------------------------
Get summary statistics for each scan level

+----------------------+------------------------------------------------------------------------------------------------------------------------+
| **URI**              | /query/getSummaryDataPerScanLevel_XML                                                                                  |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Method               | POST                                                                                                                   |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| URL Params           | None                                                                                                                   |
| (GET)                |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| POST data            | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |      <get_SummaryDataPerScanLevel_Request scanFileAPIKey="" />                                                         |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Post attributes      |  * scanFileAPIKey -  API Key for scan data                                                                             |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Success Response     | Success (200 OK)                                                                                                       |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Error Response       | Bad Request (400), Unauthorized (401)                                                                                  |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Sample Response      | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |  <get_SummaryDataPerScanLevel_Response>                                                                                |
|                      |    <status_scanFileAPIKeyNotFound>NO</status_scanFileAPIKeyNotFound>                                                   |
|                      |    <scanSummaryPerScanLevelList>                                                                                       |
|                      |        <scanSummaryPerScanLevel scanLevel="1" numberOfScans="37736" totalIonCurrent="28458447387.383" />               |
|                      |        <scanSummaryPerScanLevel scanLevel="2" numberOfScans="28473" totalIonCurrent="7643543763.293" />                |
|                      |        <!-- repeat for all scan levels -->                                                                             |
|                      |   </scanSummaryPerScanLevelList>                                                                                       |
|                      |  </get_SummaryDataPerScanLevel_Response>                                                                               |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Response Elements    |  * <status_scanFileAPIKeyNotFound> - If YES, API key was not found.                                                    |
|                      |  * <scanSummaryPerScanLevelList> - A collection of scanSummaryPerScanLevel elements                                    |
|                      |  * <scanSummaryPerScanLevel> - Summary statistics for a given scan level                                               |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| sc. level attributes |  * scanLevel - A scan level (e.g. "2" for ms2)                                                                         |
|                      |  * numberOfScans - Total number of scans at this scan level                                                            |
|                      |  * totalIonCurrent - Summed intensity of all peaks for all scans at this scan level                                    |
+----------------------+------------------------------------------------------------------------------------------------------------------------+


Get Binned MS1 Ion Intensity
---------------------------------------------------------
Get binned total ion intensity from MS1 scans. Note, unlike other web services, this one returned GZIP compressed JSON data.

+----------------------+------------------------------------------------------------------------------------------------------------------------+
| **URI**              | /query/getScanPeakIntensityBinnedOn_RT_MZ_JSON_GZIPPED                                                                 |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Method               | POST                                                                                                                   |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| URL Params           | None                                                                                                                   |
| (GET)                |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| POST data            | .. code-block:: xml                                                                                                    |
|                      |                                                                                                                        |
|                      |      <get_ScanPeakIntensityBinnedOn_RT_MZ_Request scanFileAPIKey="" />                                                 |
|                      |                                                                                                                        |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Post attributes      |  * scanFileAPIKey -  API Key for scan data                                                                             |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Success Response     | Success (200 OK)                                                                                                       |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Error Response       | Bad Request (400), Unauthorized (401)                                                                                  |
+----------------------+------------------------------------------------------------------------------------------------------------------------+
| Sample Response      | .. literalinclude:: binnedIonIntensities.json                                                                          |
|                      |    :language: javascript                                                                                               |
+----------------------+------------------------------------------------------------------------------------------------------------------------+


