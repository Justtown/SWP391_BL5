<%--
  Created by IntelliJ IDEA.
  User: pc
  Date: 12/3/2025
  Time: 10:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>List Role</title>
    <style>
        table { border-collapse: collapse; width: 80%; margin: 20px auto; }
        th, td { border: 1px solid #ccc; padding: 10px; }
        button { padding: 5px 10px; }
    </style>
</head>
<body>
<h2 style="text-align:center;">List role</h2>

<form method="get" action="role-list" style="text-align:center;">
    <input type="text" name="key" placeholder="Enter keyword to search" />
    <button type="submit">Search</button>
</form>

<br>
<table>
    <tr>
        <th>STT</th>
        <th>Name</th>
        <th>Description</th>
        <th>Status</th>
        <th></th>
    </tr>
</table>
</body>
</html>
