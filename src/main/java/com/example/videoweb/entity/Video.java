package com.example.videoweb.entity;

public class Video {
    private int id;
    private String title;
    private String categoryId;
    private String url;      // 新增：完整视频 URL
    private String thumb;    // 封面图路径或 URL

    public Video(int id, String title, String categoryId, String url, String thumb) {
        this.id = id;
        this.title = title;
        this.categoryId = categoryId;
        this.url = url;
        this.thumb = thumb;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCategoryId() { return categoryId; }
    public String getVideoUrl() { return url; }  // 直接返回数据库存的 URL
    public String getThumb() { // 如果是 http 开头（网络图），或者 / 开头（已经是完整路径了），直接返回
        if (thumb != null && (thumb.startsWith("http") || thumb.startsWith("/"))) {
            return thumb;
        }
        // 否则才拼接 /images/
        return "/images/" + thumb;
    }
}