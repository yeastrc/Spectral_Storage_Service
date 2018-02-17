package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Used in Get_ScanDataFromScanNumbers_Request
 * 
 * Indicates do not populate peaks list in SingleScan_SubResponse
 *
 * If null, assumed to be no
 */
@XmlType(name = "exclude_return_scan_peak_data")
@XmlEnum
public enum Get_ScanData_ExcludeReturnScanPeakData {

    @XmlEnumValue("no")
    NO("no"),

    @XmlEnumValue("yes")
    YES("yes");
    
    private final String value;

    Get_ScanData_ExcludeReturnScanPeakData(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Get_ScanData_ExcludeReturnScanPeakData fromValue(String v) {
        for (Get_ScanData_ExcludeReturnScanPeakData c: Get_ScanData_ExcludeReturnScanPeakData.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
