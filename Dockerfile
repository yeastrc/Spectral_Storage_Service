FROM mriffle/build-spectr:latest AS builder
MAINTAINER Michael Riffle <mriffle@uw.edu>

COPY . /app
WORKDIR /app

COPY docker/config-files/get-data/spectral_storage_get_data_config.properties WebService_Web_App_Get_Data/src/main/resources/
COPY docker/config-files/import/spectral_server_accept_import_config.properties WebService_Web_App_Accept_Import/src/main/resources/

RUN ant -f ant_build_all_create_download_zip_file.xml
RUN cd download_zip_file && unzip spectral_storage_service_deploy.zip


FROM tomcat:9-jdk11-corretto
MAINTAINER Michael Riffle <mriffle@uw.edu>

COPY --from=builder /app/download_zip_file/WebService_Web_App_Accept_Import/build/libs/spectral_storage_accept_import.war /usr/local/tomcat/webapps
COPY --from=builder /app/download_zip_file/WebService_Web_App_Get_Data/build/libs/spectral_storage_get_data.war /usr/local/tomcat/webapps
COPY --from=builder /app/download_zip_file/Scan_File_Processor_Importer/build/libs/spectralStorage_ProcessScanFile.jar /data/spectralStorage_ProcessScanFile.jar

RUN mkdir /data/config && chmod 777 /data/config

COPY --from=builder /app/docker/config-files/get-data/spectral_storage_get_data_config_allowed_remotes.properties /data/config
COPY --from=builder /app/docker/config-files/get-data/spectral_storage_get_data_scan_data_location.properties /data/config
COPY --from=builder /app/docker/config-files/import/spectral_server_accept_import_config_allowed_remotes.properties /data/config
COPY --from=builder /app/docker/config-files/import/spectral_server_accept_import_config_dirs_process_cmd.properties /data/config
