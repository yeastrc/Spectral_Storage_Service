===========================================
Spectr Configuration and Architecture
===========================================

Spectr currently runs was two separate web applications: one
containing web services for uploading and submitting data to spectr;
and the other containing web services for downloading data.

The web services are currently implemented as servlets running in a
Java servlet container, such as Apache Tomcat. These may be rewritten
in the near future as either Jersey-based web services (Java) or as
services implemented in another language, entirely (such as node.js or GO).


Local Disk vs/ Amazon AWS S3
---------------------------------------------------------
Spectr can be configured to store and access files on a locally
accessible disk or to Amazon AWS S3.


Configuring Spectr Upload Web App
----------------------------------

Sample files are in ``WebService_Web_App_Accept_Import/Config_Sample_Files`` of the
web application.

spectral_server_accept_import_config_allowed_remotes.properties
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
Configures allowed IPs for IP filtering.


spectral_server_accept_import_config_dirs_process_cmd.properties
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
asdf
