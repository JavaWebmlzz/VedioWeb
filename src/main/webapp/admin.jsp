<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>后台管理 - 极简视频网</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        form { background:white; padding:20px; border-radius:8px; margin:20px 0; box-shadow:0 2px 10px rgba(0,0,0,0.1); }
        input[type=text], select, input[type=file] { width:100%; padding:10px; margin:10px 0; border:1px solid #ccc; border-radius:4px; }
        button { padding:10px 20px; background:#007bff; color:white; border:none; border-radius:4px; cursor:pointer; }
        .msg { padding:10px; margin:10px 0; border-radius:4px; }
        .success { background:#d4edda; color:#155724; }
        .error { background:#f8d7da; color:#721c24; }
        table { width:100%; border-collapse:collapse; margin-top:20px; }
        th, td { border:1px solid #ddd; padding:10px; text-align:left; }
        th { background:#f8f9fa; }
    </style>
</head>
<body>
<div class="container">
    <h1>后台管理</h1>
    <a href="${pageContext.request.contextPath}/">← 返回前台</a>

    <c:if test="${not empty message}">
        <div class="msg success">${message}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="msg error">${error}</div>
    </c:if>

    <h2>添加新视频</h2>
    <form method="post">
        <input type="hidden" name="action" value="add">

        <label>标题：</label>
        <input type="text" name="title" required>

        <label>分类：</label>
        <select name="category" required>
            <c:forEach items="${categories}" var="c">
                <option value="${c.id}">${c.name}</option>
            </c:forEach>
        </select>

        <label>视频 URL（B站页面、直链mp4等）：</label>
        <input type="text" name="videoUrl" placeholder="https://www.bilibili.com/video/BV..." required style="width:100%;padding:10px;margin:10px 0;">

        <label>封面图（本地文件名或网络URL）：</label>
        <input type="text" name="thumb" placeholder="7.jpg 或 https://img.example.com/cover.jpg" required style="width:100%;padding:10px;margin:10px 0;">

        <button type="submit">添加视频</button>
    </form>

    <h2>已有视频（共 ${videos.size()} 个）</h2>
    <table>
        <tr>
            <th>ID</th>
            <th>标题</th>
            <th>分类</th>
            <th>视频文件</th>
            <th>封面</th>
        </tr>
        <c:forEach items="${videos}" var="v">
            <tr>
                <td>${v.id}</td>
                <td>${v.title}</td>
                <td>${v.categoryId}</td>
                <td>${v.videoUrl}</td>
                <td>${v.thumb}</td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>