<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>${video.title} - 正在播放</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <style>
        .player-container { width: 90%; max-width: 1000px; margin: 20px auto; }
        .video-player { width: 100%; aspect-ratio: 16/9; background: black; }
        iframe { width: 100%; height: 100%; border: none; }
    </style>
</head>
<body>
<div class="container">
    <h1>${video.title}</h1>
    <a href="javascript:history.back()">← 返回</a>

    <div class="player-container">
        <c:choose>
            <c:when test="${fn:contains(video.videoUrl, 'bilibili.com') ||
                    fn:contains(video.videoUrl, 'youtube.com') ||
                    fn:contains(video.videoUrl, 'youtu.be')}">
                <!-- 外部平台，用 iframe -->
                <iframe class="video-player" src="${video.videoUrl}" allowfullscreen></iframe>
            </c:when>
            <c:otherwise>
                <!-- 直链 mp4，用 video 标签 -->
                <video class="video-player" controls autoplay
                       poster="${video.thumb.startsWith('http') ? video.thumb : pageContext.request.contextPath.concat('/images/').concat(video.thumb)}">
                    <source src="${video.videoUrl}" type="video/mp4">
                    您的浏览器不支持视频播放。
                </video>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>