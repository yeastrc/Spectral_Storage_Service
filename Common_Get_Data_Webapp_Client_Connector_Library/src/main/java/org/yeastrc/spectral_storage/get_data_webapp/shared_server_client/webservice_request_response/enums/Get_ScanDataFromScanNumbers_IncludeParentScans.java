package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Used in Get_ScanDataFromScanNumbers_Request
 * 
 * Indicates which if any parent scans to include in results
 *
 */
@XmlType(name = "include_parent_scans")
@XmlEnum
public enum Get_ScanDataFromScanNumbers_IncludeParentScans {

    @XmlEnumValue("no")
    NO("no"),

    @XmlEnumValue("immediate_parent")
    IMMEDIATE_PARENT("immediate_parent"),
    
    @XmlEnumValue("all_parents")
    ALL_PARENTS("all_parents");
    
    private final String value;

    Get_ScanDataFromScanNumbers_IncludeParentScans(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Get_ScanDataFromScanNumbers_IncludeParentScans fromValue(String v) {
        for (Get_ScanDataFromScanNumbers_IncludeParentScans c: Get_ScanDataFromScanNumbers_IncludeParentScans.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
