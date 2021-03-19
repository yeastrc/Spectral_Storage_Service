FROM tomcat:jdk11-corretto
MAINTAINER Michael Riffle <mriffle@uw.edu>


ADD spectral_storage_accept_import.war /usr/local/tomcat/webapps
ADD spectral_storage_get_data.war /usr/local/tomcat/webapps
ADD spectralStorage_ProcessScanFile.jar /data/spectralStorage_ProcessScanFile.jar

RUN mkdir /data/working
RUN chmod 777 /data/working
