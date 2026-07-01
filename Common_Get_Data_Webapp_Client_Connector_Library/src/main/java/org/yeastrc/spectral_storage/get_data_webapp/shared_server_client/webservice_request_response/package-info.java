/**
 * Web service request/response DTOs shared between the spectr server and its clients
 * (serialized to XML and JSON).
 *
 * <p><b>Retention time unit convention:</b> all retention-time values in this API &mdash; scan
 * retention times in responses, and retention-time range bounds / bin sizes in requests &mdash;
 * are in <b>SECONDS</b> (UO:0000010).  spectr normalizes minute-&gt;second at ingest (see
 * {@code MLScanAndHeaderParser}), so stored and returned retention times are always seconds.
 * When writing scan files (mzML/ms1/ms2) from these values, tag the retention time as seconds.
 *
 * <p><b>Wire compatibility:</b> these classes use {@code @XmlAccessorType(FIELD)} with bare
 * {@code @XmlAttribute}, so the Java field name IS the XML attribute name (and the JSON property
 * name).  Do NOT rename fields or accessors &mdash; that renames the wire format and breaks
 * existing clients.  Document units and semantics in JavaDoc instead.
 */
package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response;
