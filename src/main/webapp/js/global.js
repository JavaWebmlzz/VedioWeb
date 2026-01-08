/**
 * global.js - 全局通用逻辑
 * 处理图片加载失败等问题
 */
document.addEventListener('DOMContentLoaded', function() {
    // 获取所有视频封面图
    const images = document.querySelectorAll('.video-card-cover img');

    images.forEach(img => {
        img.addEventListener('error', function() {
            // 如果加载失败，替换为默认图
            // 防止死循环：如果默认图也挂了，就不再替换
            if (!this.src.includes('default.jpg')) {
                this.src = '/images/default.jpg';
            }
        });
    });
});
