<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>

<head>
	<title>Twintiment Login</title>

	<!--  jQuery  -->
	<script type="text/javascript"
		src="<c:url value="/resources/bower_components/jquery/dist/jquery.min.js" />"></script>

	<!-- Bootstrap -->
	<link rel="stylesheet"	
		href="<c:url value="/resources/bower_components/bootstrap/dist/css/bootstrap.min.css"/>" />
	<link rel="stylesheet"
		href="<c:url value="/resources/bower_components/bootstrap/dist/css/bootstrap-theme.min.css"/>" />
	<script
		src="<c:url value="/resources/bower_components/bootstrap/dist/js/bootstrap.min.js"/> "></script>
	<link rel="stylesheet"
		href="<c:url value="/resources/bower_components/bootstrap-social/bootstrap-social.css"/>" />

	

	<link rel="stylesheet"
		href="<c:url value="/resources/bower_components/font-awesome/css/font-awesome.min.css"/>" />

</head>	

<body>
	<div class="container">
		<div class="jumbotron span12 text-center">
			<h1>Twintiment</h1>
			<a class="btn btn-social btn-twitter" href="<%=request.getAttribute("authUrl") %>" role="button">
				<i class="fa fa-twitter"></i> Sign in with Twitter
			</a>
			
		</div>
	</div>
</body>

</html>
