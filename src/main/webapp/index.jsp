<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>极简视频网 - ${currentCat == 'digital' ? '数码' :
            currentCat == 'edu' ? '教育' :
                    currentCat == 'tech' ? '科技' :
                            currentCat == 'sport' ? '运动' : '生活'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <h1>极简视频网</h1>

    <!-- 分类导航栏 -->
    <div class="nav">
        <c:forEach items="${categories}" var="c">
            <a href="?cat=${c.id}"
               class="${currentCat == c.id ? 'active' : ''}">${c.name}</a>
        </c:forEach>
    </div>

    <!-- 视频列表 -->
    <div class="video-grid">
        <c:forEach items="${videos}" var="v">
            <div class="video-item">
                <a href="${pageContext.request.contextPath}/play?id=${v.id}">
                    <img src="${pageContext.request.contextPath}${v.thumb}"
                         alt="${v.title}" onerror="this.src='${pageContext.request.contextPath}/images/default.png'">
                    <p>${v.title}</p>
                </a>
            </div>
        </c:forEach>
    </div>
</div>
</body>
</html>