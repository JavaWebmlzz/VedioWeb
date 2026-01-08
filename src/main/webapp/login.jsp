<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>登录 - MK视频</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="login-container">
    <div class="login-box">
        <div class="login-header">
            <h2>登录 MK视频</h2>
        </div>

        <c:if test="${not empty error}">
            <div class="error-msg">${error}</div>
        </c:if>

        <form action="user" method="post">
            <input type="hidden" name="action" value="login">

            <div class="form-group">
                <input type="text" name="username" class="form-input" placeholder="账号/邮箱" required>
            </div>

            <div class="form-group">
                <input type="password" name="password" class="form-input" placeholder="密码" required>
            </div>

            <button type="submit" class="btn-submit">登录</button>
        </form>

        <div class="auth-links">
            还没有账号？ <a href="register.jsp">立即注册</a>
            <br><br>
            <a href="${pageContext.request.contextPath}/">返回首页</a>
        </div>
    </div>
</div>

</body>
</html>

