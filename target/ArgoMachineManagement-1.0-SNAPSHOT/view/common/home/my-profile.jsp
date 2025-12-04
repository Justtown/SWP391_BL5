<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>My Profile</title>
    <style>
        body {
            background-color: #efe9e0;
            font-family: "Times New Roman", serif;
        }
        .profile-container {
            width: 700px;
            margin: 40px auto;
            padding: 40px 60px;
            background-color: #f3eee6;
            box-shadow: 2px 2px 6px rgba(0,0,0,0.2);
        }
        .profile-title {
            text-align: center;
            font-size: 32px;
            letter-spacing: 3px;
            margin-bottom: 40px;
        }
        .row {
            display: flex;
            align-items: center;
            margin-bottom: 18px;
        }
        .label {
            width: 120px;
            font-size: 18px;
        }
        .field {
            flex: 1;
        }
        .field input {
            width: 100%;
            padding: 6px 8px;
            font-size: 16px;
            border: 1px solid #ccc;
        }
        .avatar-wrapper {
            width: 100px;
            height: 130px;
            border-radius: 50%;
            background-color: #c7e3ff;
            border: 3px solid #666;
            margin-bottom: 8px;
        }
        .buttons {
            margin-top: 30px;
            display: flex;
            justify-content: space-between;
        }
        .buttons button {
            width: 120px;
            padding: 6px 0;
            font-size: 16px;
        }
        .msg {
            text-align: center;
            margin-bottom: 12px;
        }
        .msg.success {
            color: green;
        }
        .msg.error {
            color: red;
        }
    </style>
</head>
<body>
<div class="profile-container">
    <div class="profile-title">MY PROFILE</div>

    <c:if test="${not empty success}">
        <div class="msg success">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="msg error">${error}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/my-profile">
        <div class="row">
            <div class="label">Avatar</div>
            <div class="field">
                <div class="avatar-wrapper"></div>
                <input type="text" name="avatar" value="${account.avatar}" placeholder="Avatar URL (optional)">
            </div>
        </div>

        <div class="row">
            <div class="label">Name</div>
            <div class="field">
                <input type="text" name="fullName" value="${account.fullName}">
            </div>
        </div>

        <div class="row">
            <div class="label">Email</div>
            <div class="field">
                <input type="email" name="email" value="${account.email}">
            </div>
        </div>

        <div class="row">
            <div class="label">Phone</div>
            <div class="field">
                <input type="text" name="phone" value="${account.phone}">
            </div>
        </div>

        <div class="row">
            <div class="label">Address</div>
            <div class="field">
                <input type="text" name="address" value="${account.address}">
            </div>
        </div>

        <div class="row">
            <div class="label">Role</div>
            <div class="field">
                <span><c:out value="${account.roleName}"/></span>
            </div>
        </div>

        <div class="row">
            <div class="label">Birthdate</div>
            <div class="field">
                <input type="date" name="birthdate" value="${account.birthdate}">
            </div>
        </div>

        <div class="buttons">
            <button type="button" onclick="window.history.back()">close</button>
            <button type="submit">save</button>
            <button type="button"
                    onclick="window.location='${pageContext.request.contextPath}/change-password'">
                change password
            </button>
        </div>
    </form>
</div>
</body>
</html>
=