# Spectral_Storage_Service
Storage of Spectra


Documentation:
https://spectr.readthedocs.io/en/latest/

Build the docker image
----------------------------
```
sudo docker image build -t mriffle/spectr ./
```

How to run the docker image
------------------------------
```
sudo docker run -itd -v <MACHINE DIRECTORY>:/data/upload -v <MACHINE DIRECTORY>:/data/storage --rm -p 8888:8080 --name spectr mriffle/spectr

E.g.:
sudo docker run -itd -v /data/spectr/upload:/data/upload -v /data/spectr/storage:/data/storage --rm -p 8888:8080 --name spectr mriffle/spectr
```
