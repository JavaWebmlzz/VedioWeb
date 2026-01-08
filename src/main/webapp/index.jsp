<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>MK视频 - ${
            currentCat == 'tech' ? '科技' :
                    currentCat == 'edu' ? '教育' :
                            currentCat == 'sport' ? '体育' :
                                    currentCat == 'entertainment' ? '娱乐' : '首页'
            }</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<header class="site-header">
    <div class="container header-content">
        <a href="${pageContext.request.contextPath}/" class="logo">MK视频</a>

        <div class="user-panel">
            <c:choose>
                <c:when test="${not empty sessionScope.user}">
                    <span>${sessionScope.user.username}</span>
                    <a href="${pageContext.request.contextPath}/user?action=logout">退出</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login.jsp" class="btn-primary">登录</a>
                    <a href="${pageContext.request.contextPath}/register.jsp">注册</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</header>

<div class="container">
    <div class="nav-wrapper">
        <nav class="nav">
            <c:forEach items="${categories}" var="c">
                <a href="?cat=${c.id}" class="${currentCat == c.id ? 'active' : ''}">${c.name}</a>
            </c:forEach>
        </nav>
    </div>

    <div class="video-grid">
        <c:forEach items="${videos}" var="v">
            <div class="video-item">
                <a href="${pageContext.request.contextPath}/play?id=${v.id}">
                    <div class="video-card-cover">
                        <!-- 移除了 onerror，交给 global.js 处理 -->
                        <img src="${v.thumb}" alt="${v.title}">
                    </div>
                    <div class="video-info">
                        <p class="video-title">${v.title}</p>
                    </div>
                </a>
            </div>
        </c:forEach>
    </div>
</div>

<!-- 引入全局 JS 处理图片错误 -->
<script src="${pageContext.request.contextPath}/js/global.js"></script>

</body>
</html>
