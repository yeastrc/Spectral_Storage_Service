package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Used in Get_ScansDataFromRetentionTimeRange_Request
 * 
 * Indicates which scans should be excluded from results if number of peaks (after filtering) is zero
 *
 */
@XmlType(name = "exclude_scans_without_peaks")
@XmlEnum
public enum Get_ScanData_ExcludeScansWithoutPeaks {

    @XmlEnumValue("no")
    NO("no"),

    @XmlEnumValue("yes")
    YES("yes");
    
    private final String value;

    Get_ScanData_ExcludeScansWithoutPeaks(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Get_ScanData_ExcludeScansWithoutPeaks fromValue(String v) {
        for (Get_ScanData_ExcludeScansWithoutPeaks c: Get_ScanData_ExcludeScansWithoutPeaks.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
