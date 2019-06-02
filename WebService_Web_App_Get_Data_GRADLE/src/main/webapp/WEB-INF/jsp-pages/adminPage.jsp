<%@page import="org.yeastrc.spectral_storage.get_data_webapp.constants_enums.AdminPageConstants"%>
<html>
<head>
 <title>Admin Page</title>
</head>
<body>

<div >
  <a href="admin/reloadConfig?<%= AdminPageConstants.ADMIN_KEY_QUERY_STRING_PARAMETER_NAME %>=${ adminKey }" 
  	target="_blank">Reload Config</a>
</div>

</body>

</html>