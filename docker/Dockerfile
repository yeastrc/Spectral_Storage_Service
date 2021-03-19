FROM tomcat:jdk11-corretto
MAINTAINER Michael Riffle <mriffle@uw.edu>


ADD ../WebService_Web_App_Accept_Import/build/libs/spectral_storage_accept_import.war /usr/local/tomcat/webapps
ADD ../WebService_Web_App_Get_Data/build/libs/spectral_storage_get_data.war /usr/local/tomcat/webapps
ADD ../download_zip_file/Scan_File_Processor_Importer/build/libs/spectralStorage_ProcessScanFile.jar /data/spectralStorage_ProcessScanFile.jar

RUN mkdir /data/working
RUN chmod 777 /data/working
