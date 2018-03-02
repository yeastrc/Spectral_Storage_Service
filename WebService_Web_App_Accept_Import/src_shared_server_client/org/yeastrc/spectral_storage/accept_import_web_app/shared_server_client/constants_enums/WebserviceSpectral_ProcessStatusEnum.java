package org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.constants_enums;

/**
 * enum for Get_UploadedScanFileInfo_Response.status
 *
 */
public enum WebserviceSpectral_ProcessStatusEnum {

    PENDING("pending"),
    SUCCESS("success"),
    FAIL("fail"),
    /**
     * The client called delete so it is marked deleted.
     */
    DELETED("deleted");

    
    private final String value;

    
    
    /**
     * constructor:  Make private to hide 
     */
    private WebserviceSpectral_ProcessStatusEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    /**
     * Get the enum from the String value
     * 
     * @param value_
     * @return
     */
    public static WebserviceSpectral_ProcessStatusEnum fromValue( String value_ ) {
        for (WebserviceSpectral_ProcessStatusEnum c: WebserviceSpectral_ProcessStatusEnum.values()) {
            if (c.value.equals( value_ )) {
                return c;
            }
        }
        throw new IllegalArgumentException( "WebserviceSpectral_ProcessStatusEnum not valid for value: " + value_ );
    }

}
