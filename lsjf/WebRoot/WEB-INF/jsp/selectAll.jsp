<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>查询用户</title>
</head>
<body>
	<form action="http://localhost:8080/lsjf/realNameVerPicUp.do" method="post" enctype="multipart/form-data">  
        选择文件:<input type="file" name="frontPhoto" width="120px">
		 <input type="file" name="backPhoto" width="120px">  
        <input type="submit" value="上传">  
    </form>  
    <hr>  
    <form action="http://localhost:8080/lsjf/reserveDown.do" method="post">  
        <input type="submit" value="下载">  
    </form>  
	<form action="list.do" method="post">
		<input type="submit" value="提交">
	</form>
</body>
</html>