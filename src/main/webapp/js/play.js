/**
 * play.js - 广告防拖拽增强版
 * 核心逻辑：全局监听事件，根据当前视频源区分逻辑
 */
document.addEventListener('DOMContentLoaded', function () {
    const config = window.mkConfig || {};
    const player = document.getElementById('player');
    const adInfo = document.getElementById('adInfo');
    const skipBtn = document.getElementById('skipBtn');

    // 自动抓取正片 URL
    let mainUrl = config.videoUrl;
    if (!mainUrl || mainUrl.trim() === "") {
        const sourceTag = player.querySelector('source');
        if (sourceTag) mainUrl = sourceTag.src;
    }

    const recommendNumId = config.recommendNumId || 2;
    const adServerUrl = "http://10.100.164.13:8080/api/ads/randomByPrefix";
    const trackApiUrl = config.contextPath + "/api/track";
    const categoryId = config.categoryId;

    // 状态变量
    let adData = null;
    let adPlayed = false;
    let lastTime = 0;          // 实时记录播放时间 (用于回弹)
    let resumeTime = 0;        // 冻结的正片时间 (用于恢复)
    let targetSeekTime = null; // 拖动拦截的目标时间
    let adForceTimer = null;
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
                    // 启动全局监听
                    initGlobalListeners();
                }
            }
        })
        .catch(e => console.error("广告请求失败", e));

    // ==========================================
    // 核心逻辑：永久监听器 (不再反复移除/添加)
    // ==========================================
    function initGlobalListeners() {
        player.addEventListener('timeupdate', handleGlobalTimeUpdate);
        player.addEventListener('seeking', handleGlobalSeeking);
    }

    // 全局时间更新处理
    function handleGlobalTimeUpdate() {
        // 1. 始终更新 lastTime (除非正在拖动)
        // 这是实现“防拖拽回弹”的关键，记录上一秒在哪
        if (!player.seeking) {
            lastTime = player.currentTime;
        }

        // 2. 如果是广告模式，不执行触发逻辑
        if (isAdPlaying()) return;

        // 3. 正片模式：检查是否触发广告
        if (!adPlayed && Math.abs(player.currentTime - adData.insertAt) < 0.5) {
            console.log("触发广告播放");
            resumeTime = player.currentTime; // 冻结正片进度
            playAd();
        }
    }

    // 全局拖动处理
    function handleGlobalSeeking() {
        // 1. 【新增】广告模式：禁止拖拽！
        if (isAdPlaying()) {
            // 如果拖动幅度超过 1秒，强制弹回
            const delta = Math.abs(player.currentTime - lastTime);
            if (delta > 1) {
                console.warn("广告期间禁止拖动！自动回弹。");
                player.currentTime = lastTime; // 弹回上一刻的位置
            }
            return;
        }

        // 2. 正片模式：防跨越广告拖拽
        const currentTime = player.currentTime;
        const insertAt = adData.insertAt;

        if (!adPlayed && lastTime < insertAt && currentTime > insertAt) {
            console.log("检测到跨越广告拖动，拦截！");
            targetSeekTime = currentTime; // 记住用户想去哪
            playAd(); // 强制进广告
        }
    }

    // 辅助：判断当前是否在播广告
    function isAdPlaying() {
        return adData && player.src.includes(adData.adUrl);
    }

    // ==========================================
    // 播放控制
    // ==========================================
    function playAd() {
        adPlayed = true;

        // 切换视频源
        player.src = adData.adUrl;
        showAdUI();
        player.play().catch(e => console.error("广告自动播放被拦截:", e));

        player.onended = switchToMain;

        // 强制超时保护
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
        console.log("切回正片...");
        hideAdUI();
        clearTimeout(skipTimer);
        clearTimeout(adForceTimer);
        player.onended = null;

        if (!mainUrl) { alert("正片地址丢失"); return; }

        player.src = mainUrl;

        // 计算恢复点
        const seekTo = (targetSeekTime !== null) ? targetSeekTime : (resumeTime + 0.1);

        // 恢复播放函数
        const resumePlay = () => {
            console.log("跳回进度:", seekTo);
            player.currentTime = seekTo;
            targetSeekTime = null; // 清理

            player.play().catch(e => console.error(e));
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

    // 埋点
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
