package com.example.videoweb.entity;

public class Video {
    private int id;
    private String title;
    private String categoryId;
    private String fileName;     // 视频文件名
    private String thumb;        // 封面图（images/1.jpg）

    public Video(int id, String title, String categoryId, String fileName, String thumb) {
        this.id = id;
        this.title = title;
        this.categoryId = categoryId;
        this.fileName = fileName;
        this.thumb = thumb;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCategoryId() { return categoryId; }
    public String getVideoUrl() { return "/videos/" + categoryId + "/" + fileName; }
    public String getThumb() { return "/images/" + thumb; }
}
