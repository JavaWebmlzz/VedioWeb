<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>${video.title} - 正在播放</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <h1>${video.title}</h1>
    <a href="javascript:history.back()">← 返回</a>

    <div class="play-container">
        <video id="player" controls autoplay
               poster="${pageContext.request.contextPath}/images/${video.thumb}">
            <source src="${video.videoUrl}" type="video/mp4">
            您的浏览器不支持视频播放。
        </video>

        <div id="adInfo" class="ad-info">广告播放中...</div>
        <button id="skipBtn" class="skip-btn">5秒后可跳过</button>
    </div>
</div>

<script>
    const player = document.getElementById('player');
    const adInfo = document.getElementById('adInfo');
    const skipBtn = document.getElementById('skipBtn');

    const mainUrl = "${video.videoUrl}";
    const categoryId = "${categoryId}";
    const videoId = "${video.id}";

    let adData = null;
    let mainTime = 0;
    let adPlayed = false;
    let adTimer = null;
    let skipTimer = null;

    console.log("开始请求广告，分类:", categoryId, "视频ID:", videoId);

    fetch(`${pageContext.request.contextPath}/getAd?cat=${categoryId}&videoId=${videoId}`)
        .then(res => {
            if (!res.ok) throw new Error("HTTP " + res.status);
            return res.json();
        })
        .then(data => {
            console.log("收到广告数据:", data);
            if (data && data.adUrl && data.adUrl.trim() !== '') {
                adData = data;
                handleAd();
            }
        })
        .catch(err => {
            console.error("广告请求失败:", err);
        });

    function handleAd() {
        const pos = (adData.position || 'start').toLowerCase();
        const duration = adData.duration || 15;
        const insertAt = adData.insertAt || Math.floor(player.duration / 2);

        if (pos === 'start') {
            showAdUI();
            player.src = adData.adUrl;
            player.play();

            clearTimeout(adTimer);
            adTimer = setTimeout(switchToMain, duration * 1000);

            if (duration > 5) {
                clearTimeout(skipTimer);
                skipTimer = setTimeout(() => {
                    skipBtn.style.display = 'block';
                    skipBtn.onclick = switchToMain;
                }, 5000);
            }
            return;
        }

        if (pos === 'end') {
            player.onended = () => {
                showAdUI();
                player.src = adData.adUrl;
                player.play();

                clearTimeout(adTimer);
                adTimer = setTimeout(() => {
                    hideAdUI();
                    player.pause(); // 结尾广告结束，视频结束
                }, duration * 1000);

                if (duration > 5) {
                    clearTimeout(skipTimer);
                    skipTimer = setTimeout(() => {
                        skipBtn.style.display = 'block';
                        skipBtn.onclick = () => player.pause();
                    }, 5000);
                }
            };
            return;
        }

        if (pos === 'middle') {
            player.ontimeupdate = () => {
                if (!adPlayed && player.currentTime >= insertAt) {
                    adPlayed = true;
                    mainTime = player.currentTime;
                    player.pause();

                    showAdUI();
                    player.src = adData.adUrl;
                    player.play();

                    clearTimeout(adTimer);
                    adTimer = setTimeout(switchToMain, duration * 1000);

                    if (duration > 5) {
                        clearTimeout(skipTimer);
                        skipTimer = setTimeout(() => {
                            skipBtn.style.display = 'block';
                            skipBtn.onclick = switchToMain;
                        }, 5000);
                    }
                }
            };
            return;
        }
    }

    // 统一切换回正片
    function switchToMain() {
        hideAdUI();
        clearTimeout(adTimer);
        clearTimeout(skipTimer);

        player.src = mainUrl;
        if (adData && adData.position === 'middle') {
            player.currentTime = mainTime;
        }
        player.play();

        // 恢复正片进度条监听
        player.removeEventListener('loadedmetadata', updateAdProgress);
        player.removeEventListener('timeupdate', updateAdProgress);
    }

    function showAdUI() {
        adInfo.style.display = 'block';
        skipBtn.style.display = 'none';
        skipBtn.textContent = "5秒后可跳过";
    }

    function hideAdUI() {
        adInfo.style.display = 'none';
        skipBtn.style.display = 'none';
    }
</script>
</body>
</html>