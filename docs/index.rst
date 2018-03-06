.. spectr documentation master file

Welcome to spectr's documentation!
====================================

Spectr is a system for non-redundantly storing and quickly retrieving spectral data
from mass spectrometry proteomics experiments (e.g., mzML or mzXML files).

Brief Overview
----------------

Data files are submitted to spectr via a web service and a unique temporary ID is
returned that can be used to query the status of the upload. This ID can be used
to retrieve the final ID for the uploaded file after it has been processed.

The final ID for an uploaded file is the hex conversion of the SHA-384 hash of the
uploaded file's contents (e.g., ``98c11ffdfdd540676b1a137cb1a22b2a70350c9a44171d6b1180c6be5cbb2ee3f79d532c8a1dd9ef2e8e08e752a3babb``.
If a file has already been uploaded, the hash will already exist, and that hash
will be return for the uploaded file with no processing performed. This ensures a
given file will only ever be stored once.

No meta data are stored in spectr, such as experimental parameters or mass spectrometer settings--only scan numbers,
peak lists, and precursor m/z, charge, and retention time. No user-level authentication, other than knowledge of the hash for the
file, is required to retrieve data. Note that the hash code is virtually impossible to guess.

Data may be queried from a specific scan file by providing a hash for the file and
information about which scans are desired (e.g., scan number, retention time range, and/or m/z range).


Deeper Dive
----------------

Use the links below to get more specific information about how spectr stores data and
what interfaces are available via web services to retrieve the data.


.. toctree::
   :maxdepth: 1
   :caption: Web Services

   services/download   
   services/upload

.. toctree::
   :maxdepth: 1
   :caption: File Formats
   
   formats/index
   formats/data

.. toctree::
   :maxdepth: 1
   :caption: Administration
   
   admin/install
 
   