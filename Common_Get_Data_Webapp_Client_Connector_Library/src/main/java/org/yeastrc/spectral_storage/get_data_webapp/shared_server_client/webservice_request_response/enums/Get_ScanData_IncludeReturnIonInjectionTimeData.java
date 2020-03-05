package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Used in Get_ScanDataFromScanNumbers_Request
 * 
 * Indicates Do populate Ion Injection Time SingleScan_SubResponse
 *
 * If null, assumed to be no
 */
@XmlType(name = "include_return_ion_injection_time_data")
@XmlEnum
public enum Get_ScanData_IncludeReturnIonInjectionTimeData {

    @XmlEnumValue("no")
    NO("no"),

    @XmlEnumValue("yes")
    YES("yes");
    
    private final String value;

    Get_ScanData_IncludeReturnIonInjectionTimeData(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Get_ScanData_IncludeReturnIonInjectionTimeData fromValue(String v) {
        for (Get_ScanData_IncludeReturnIonInjectionTimeData c: Get_ScanData_IncludeReturnIonInjectionTimeData.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
