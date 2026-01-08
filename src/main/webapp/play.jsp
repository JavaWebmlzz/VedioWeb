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
        <!-- 播放器部分保持不变 -->
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
    // ==========================================
    //  前端只负责取值，不负责计算，逻辑都在 Servlet 里
    // ==========================================

    // 1. 直接用 EL 表达式获取 Servlet 传过来的推荐ID
    // 如果为空，兜底值为 2
    //const recommendNumId = "${recommendNumId != null ? recommendNumId : 2}";
    const recommendNumId = "2";

    // 2. 广告服务器地址
    const adServerUrl = "http://10.100.164.13:8080/api/ads/randomByPrefix";

    console.log("准备请求广告，推荐算法计算出的 ID:", recommendNumId);

    // 3. 发送请求：注意参数改成 prefix 和 limit

    // ✅ 传统写法
    // 3. 发送请求
    fetch(adServerUrl + "?prefix=" + recommendNumId + "&limit=1")
        .then(res => {
            // 第一步：检查网络状态
            if (!res.ok) throw new Error("HTTP " + res.status);
            // 直接解析为 JSON，不要中间读 text，否则流会被消耗
            return res.json();
        })
        .then(data => {
            console.log("收到广告原始数据:", data);

            // 4. 解析数据
            // 【关键】截图证明返回的是数组 [...]，必须取第一个元素
            let adItem = null;

            // 严谨判断：如果是数组且不为空，取第0个；如果是对象，直接用
            if (Array.isArray(data) && data.length > 0) {
                adItem = data[0];
            } else if (data && !Array.isArray(data)) {
                adItem = data;
            }

            if (adItem) {
                adData = {
                    // 【字段映射】对方叫 videoFullUrl -> 我们叫 adUrl
                    adUrl: adItem.videoFullUrl,

                    // 【时长映射】
                    duration: adItem.duration || 15,

                    // 【位置】强制设为片头
                    position: 'start'
                };

                // 校验 URL 是否有效
                if (adData.adUrl && adData.adUrl.trim() !== "") {
                    console.log("广告解析成功，准备播放:", adData.adUrl);
                    handleAd();
                } else {
                    console.warn("广告数据存在，但 videoFullUrl 字段为空");
                }
            } else {
                console.warn("未获取到广告数据 (返回了空数组或null)");
            }
        })
        .catch(err => {
            console.error("广告请求失败:", err);
        });




    // ----------------------------------------------------
    // 下面是播放器的控制逻辑 (与之前保持一致，无需 Java 代码)
    // ----------------------------------------------------
    const player = document.getElementById('player');
    const adInfo = document.getElementById('adInfo');
    const skipBtn = document.getElementById('skipBtn');
    const mainUrl = "${video.videoUrl}";
    let adData = null;
    let mainTime = 0;
    let adPlayed = false;
    let adTimer = null;
    let skipTimer = null;

    function handleAd() {
        const pos = (adData.position || 'start').toLowerCase();
        const duration = adData.duration;

        if (pos === 'start') {
            playAdNow(duration);
        } else if (pos === 'end') {
            player.onended = () => {
                // 只有正片结束才播广告
                if(player.src.includes(mainUrl)) {
                    playAdNow(duration);
                    // 广告播完后的逻辑
                    setTimeout(() => {
                        player.pause();
                        hideAdUI();
                    }, duration * 1000);
                }
            };
        }
        // ... 中插广告逻辑同理 ...
    }

    function playAdNow(duration) {
        showAdUI();
        player.src = adData.adUrl;
        player.play();

        // 倒计时切回正片
        clearTimeout(adTimer);
        adTimer = setTimeout(switchToMain, duration * 1000);

        // 跳过按钮逻辑
        if (duration > 5) {
            clearTimeout(skipTimer);
            skipTimer = setTimeout(() => {
                skipBtn.style.display = 'block';
                skipBtn.onclick = switchToMain;
            }, 5000);
        }
    }

    function switchToMain() {
        hideAdUI();
        clearTimeout(adTimer);
        clearTimeout(skipTimer);
        player.src = mainUrl;
        // 如果是中插，需要恢复进度
        if (adData && adData.position === 'middle') {
            player.currentTime = mainTime;
        }
        player.play();
        // 清除一次性事件监听
        player.onended = null;
    }

    function showAdUI() {
        adInfo.style.display = 'block';
        skipBtn.textContent = "5秒后可跳过";
    }

    function hideAdUI() {
        adInfo.style.display = 'none';
        skipBtn.style.display = 'none';
    }
</script>
</body>
</html>
