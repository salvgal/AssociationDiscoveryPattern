<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Upload File Response</title>
</head>
<body>
	<h3>Test = Asso - (ChiSquare/2n) | Accepted when Test > 0 and JointProb > threshold, otherwise Rejected | Threshold = ${requestScope.threshold}</h3>
	<%-- Using JSP EL to get message attribute value from request scope --%>
    <p>${requestScope.message}</p>
</body>
</html>