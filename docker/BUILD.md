
Build the project:
--------------------------
```
git pull
cd .. # ensure you're in root directory for repo (Spectral_Storage_Service)
cp docker/config-files/get-data/spectral_storage_get_data_config.properties WebService_Web_App_Get_Data/src/main/resources/
cp docker/config-files/import/spectral_server_accept_import_config.properties WebService_Web_App_Accept_Import/src/main/resources/
ant -f ant_build_all_create_download_zip_file.xml
```

Build the docker image
----------------------------
```
cd download_zip_file
unzip spectral_storage_service_deploy.zip
cd ..

cp download_zip_file/WebService_Web_App_Accept_Import/build/libs/spectral_storage_accept_import.war docker/
cp download_zip_file/WebService_Web_App_Get_Data/build/libs/spectral_storage_get_data.war docker/
cp download_zip_file/Scan_File_Processor_Importer/build/libs/spectralStorage_ProcessScanFile.jar docker/
cd docker
sudo docker image build -t mriffle/spectr ./
```

How to run the docker image
------------------------------
```
sudo docker run -itd -v <MACHINE DIRECTORY>:/data/upload -v <MACHINE DIRECTORY>:/data/storage --rm -p 8888:8080 --name spectr mriffle/spectr

E.g.:
sudo docker run -itd -v /data/spectr/upload:/data/upload -v /data/spectr/storage:/data/storage --rm -p 8888:8080 --name spectr mriffle/spectr
```
