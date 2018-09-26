<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Upload File Response</title>
</head>
<body>
	<%-- Using JSP EL to get message attribute value from request scope --%>
<h2>${requestScope.message}</h2>
<form action="upload" method="post" enctype="multipart/form-data">
Enter the number of variables:
<input type="text" name="variables">
<br>
Enter the number of data-points:
<input type="text" name="datapoints">
<br>
Enter threshold in decimal for test:
<input type="text" name="threshold">
<br>
Upload your data
    <input type="file" name="file" />
    <input type="submit" />
</form>
</body>
</html>