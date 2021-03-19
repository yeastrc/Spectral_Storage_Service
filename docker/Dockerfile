FROM tomcat:jdk11-corretto
MAINTAINER Michael Riffle <mriffle@uw.edu>


ADD spectral_storage_accept_import.war /usr/local/tomcat/webapps
ADD spectral_storage_get_data.war /usr/local/tomcat/webapps
ADD spectralStorage_ProcessScanFile.jar /data/spectralStorage_ProcessScanFile.jar

RUN mkdir /data/config
RUN chmod 777 /data/config

ADD config-files/get-data/spectral_storage_get_data_config_allowed_remotes.properties /data/config
ADD config-files/get-data/spectral_storage_get_data_scan_data_location.properties /data/config
ADD config-files/import/spectral_server_accept_import_config_allowed_remotes.properties /data/config
ADD config-files/import/spectral_server_accept_import_config_dirs_process_cmd.properties /data/config
