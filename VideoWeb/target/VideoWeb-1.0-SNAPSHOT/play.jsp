<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>${video.title} - 正在播放</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>video {width: 90%; max-width: 1000px; margin: 20px auto; display: block;}</style>
</head>
<body>
<div class="container">
    <h1>${video.title}</h1>
    <a href="javascript:history.back()">← 返回</a>
    <video controls autoplay>
        <source src="${pageContext.request.contextPath}${video.videoUrl}" type="video/mp4">
        您的浏览器不支持视频播放
    </video>
</div>
</body>
</html>