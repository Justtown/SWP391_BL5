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
    <title>Role Detail</title>
    <style>
        .card { width: 400px; margin: auto; border: 1px solid #ccc; padding: 20px; }
        button { padding: 5px 10px; }
    </style>
</head>
<body>
<h2 style="text-align:center;">Role detail</h2>

<div class="card">

    <form action="role-detail" method="post">

        <input type="hidden" name="role_id" >

        Name:
        <input type="text" name="role_name"  /><br><br>

        Description:
        <input type="text" name="description" /><br><br>

        Status:
        <select name="status">
            <option value="true" >Active</option>
            <option value="false" >Inactive</option>
        </select>
        <br><br>

        <button type="submit">Save</button>
    </form>

</div>
</body>
</html>
