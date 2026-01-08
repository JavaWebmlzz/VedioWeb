<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>注册 - MK视频</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="login-container">
    <div class="login-box">
        <div class="login-header">
            <h2>新用户注册</h2>
        </div>

        <c:if test="${not empty error}">
            <div class="error-msg">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/user" method="post">
            <input type="hidden" name="action" value="register">

            <div class="form-group">
                <input type="text" name="username" class="form-input" placeholder="设置用户名" required>
            </div>

            <div class="form-group">
                <input type="password" name="password" class="form-input" placeholder="设置密码 (6位以上)" required>
            </div>

            <button type="submit" class="btn-submit">立即注册</button>
        </form>

        <div class="auth-links">
            已有账号？ <a href="login.jsp">直接登录</a>
            <br><br>
            <a href="${pageContext.request.contextPath}/">返回首页</a>
        </div>
    </div>
</div>

</body>
</html>
