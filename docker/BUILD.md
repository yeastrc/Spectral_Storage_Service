
Build the project:
--------------------------
```
git pull
cd .. # ensure you're in root directory for repo (Spectral_Storage_Service)
ant -f ant_build_all_create_download_zip_file.xml
```

Unzip the build zip
----------------------------
```
unzip download_zip_file/spectral_storage_service_deploy.zip
```

Copy the config files
--------------------------
```
directions for copying config files
```

Build the docker image
----------------------------
```
cd docker
sudo docker image build -t mriffle/spectr ./
```

How to run the docker image
------------------------------
```
sudo docker run -itd -v /data/spectr/upload:/data/upload -v /data/spectr/storage:/data/storage --rm -p 8888:8080 --name spectr mriffle/spectr
```
