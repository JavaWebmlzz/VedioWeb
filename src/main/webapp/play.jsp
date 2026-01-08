<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>${video.title} - MK视频</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<header class="site-header">
    <div class="container header-content">
        <a href="${pageContext.request.contextPath}/" class="logo">MK视频</a>
    </div>
</header>

<div class="container play-page-container">
    <div class="video-header">
        <h1>${video.title}</h1>
    </div>

    <div class="play-wrapper">
        <!-- 1. 播放器 -->
        <video id="player" controls autoplay poster="${video.thumb}">
            <source src="${video.videoUrl}" type="video/mp4">
            您的浏览器不支持视频播放。
        </video>

        <!-- 2. 【关键缺失】广告UI元素，必须要有这两个 id -->
        <div id="adInfo" class="ad-info" style="display:none;">广告播放中...</div>
        <button id="skipBtn" class="skip-btn" style="display:none;">3秒后可跳过</button>
    </div>
</div>

<!-- 3. 配置对象 -->
<script>
    window.mkConfig = {
        contextPath: "${pageContext.request.contextPath}",
        videoUrl: "${video.videoUrl}",
        recommendNumId: "${recommendNumId != null ? recommendNumId : 2}",
        categoryId: "${categoryId}"
    };
</script>

<!-- 4. 引入 JS -->
<script src="${pageContext.request.contextPath}/js/play.js"></script>

</body>
</html>
