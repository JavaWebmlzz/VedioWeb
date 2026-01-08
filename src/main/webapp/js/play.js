/**
 * play.js - 终极修复版
 * 包含：自动抓取URL、强制超时跳转、拖动拦截
 */
document.addEventListener('DOMContentLoaded', function () {
    const config = window.mkConfig || {};
    const player = document.getElementById('player');
    const adInfo = document.getElementById('adInfo');
    const skipBtn = document.getElementById('skipBtn');

    // 【修复1】如果 config 里没取到 url，尝试从 DOM 自动获取，防止 undefined
    let mainUrl = config.videoUrl;
    if (!mainUrl || mainUrl.trim() === "") {
        const sourceTag = player.querySelector('source');
        if (sourceTag) mainUrl = sourceTag.src;
    }
    console.log("正片地址:", mainUrl);

    const recommendNumId = config.recommendNumId || 2;
    const adServerUrl = "http://10.100.164.13:8080/api/ads/randomByPrefix";
    const trackApiUrl = config.contextPath + "/api/track";
    const categoryId = config.categoryId;

    let adData = null;
    let adPlayed = false;
    let lastTime = 0;
    let resumeTime = 0;
    let targetSeekTime = null;

    // 定时器变量
    let adForceTimer = null; // 【新增】强制结束定时器
    let skipTimer = null;

    // 1. 请求广告
    fetch(adServerUrl + "?prefix=" + recommendNumId + "&limit=1")
        .then(res => res.json())
        .then(data => {
            console.log("收到广告:", data);
            let adItem = null;
            if (Array.isArray(data) && data.length > 0) adItem = data[0];
            else if (data && !Array.isArray(data)) adItem = data;

            if (adItem) {
                adData = {
                    adUrl: adItem.videoFullUrl,
                    duration: adItem.duration || 15,
                    insertAt: 5 // 测试用：第5秒插入
                };

                if (adData.adUrl) {
                    console.log(`广告就绪，将在第 ${adData.insertAt} 秒插入`);
                    initAdListener();
                }
            }
        })
        .catch(e => console.error("广告请求失败", e));

    function initAdListener() {
        player.addEventListener('timeupdate', handleTimeUpdate);
        player.addEventListener('seeking', handleSeeking);
    }

    function handleTimeUpdate() {
        if (player.src.includes(adData.adUrl)) return;

        // 触发广告
        if (!adPlayed && Math.abs(player.currentTime - adData.insertAt) < 0.5) {
            console.log("正常触发广告");
            resumeTime = player.currentTime;
            playAd();
            return;
        }

        if (!player.seeking) {
            lastTime = player.currentTime;
        }
    }

    function handleSeeking() {
        if (adPlayed || player.src.includes(adData.adUrl)) return;

        const currentTime = player.currentTime;
        const insertAt = adData.insertAt;

        if (lastTime < insertAt && currentTime > insertAt) {
            console.log("拦截拖动！");
            targetSeekTime = currentTime;
            playAd();
        }
    }

    function playAd() {
        adPlayed = true;

        // 移除监听
        player.removeEventListener('timeupdate', handleTimeUpdate);
        player.removeEventListener('seeking', handleSeeking);

        // 播放广告
        player.src = adData.adUrl;
        showAdUI();
        player.play().catch(e => console.error("广告自动播放失败:", e));

        // 监听结束
        player.onended = switchToMain;

        // 【修复2】增加“强制超时”机制
        // 万一 onended 没触发，(广告时长 + 1秒) 后强制切回
        clearTimeout(adForceTimer);
        adForceTimer = setTimeout(() => {
            console.warn("广告超时强制切回");
            switchToMain();
        }, (adData.duration + 1) * 1000);

        // 跳过按钮
        if (adData.duration > 3) {
            clearTimeout(skipTimer);
            skipTimer = setTimeout(() => {
                skipBtn.style.display = 'block';
                skipBtn.onclick = switchToMain;
            }, 3000);
        }
    }

    function switchToMain() {
        console.log("正在切回正片...");

        // 清理现场
        hideAdUI();
        clearTimeout(skipTimer);
        clearTimeout(adForceTimer); // 清除强制定时器
        player.onended = null;      // 移除结束监听

        // 检查 mainUrl 是否有效
        if (!mainUrl) {
            alert("正片地址丢失，请刷新页面");
            return;
        }

        player.src = mainUrl;

        // 决定恢复到哪里
        const seekTo = (targetSeekTime !== null) ? targetSeekTime : (resumeTime + 0.1);
        console.log("目标时间:", seekTo);

        // 恢复播放函数
        const resumePlay = () => {
            player.currentTime = seekTo;
            player.play().catch(e => console.error(e));

            // 重新挂载监听
            player.addEventListener('timeupdate', function() {
                if (!player.seeking && !player.src.includes(adData.adUrl)) {
                    lastTime = player.currentTime;
                }
            });
            // 如果希望下次还能拦截拖动，可以解除注释下面这行
            // player.addEventListener('seeking', handleSeeking);
        };

        // 等待元数据加载
        if (player.readyState >= 1) {
            resumePlay();
        } else {
            player.addEventListener('loadedmetadata', resumePlay, { once: true });
        }
    }

    function showAdUI() {
        adInfo.style.display = 'block';
        skipBtn.textContent = "3秒后可跳过";
    }

    function hideAdUI() {
        adInfo.style.display = 'none';
        skipBtn.style.display = 'none';
    }

    // 埋点保持不变
    let startWatchTime = Date.now();
    window.addEventListener('beforeunload', function () {
        const endTime = Date.now();
        const duration = Math.floor((endTime - startWatchTime) / 1000);
        if (duration > 1) {
            const fd = new URLSearchParams();
            fd.append('categoryId', categoryId);
            fd.append('duration', duration);
            navigator.sendBeacon(trackApiUrl, fd);
        }
    });
});
