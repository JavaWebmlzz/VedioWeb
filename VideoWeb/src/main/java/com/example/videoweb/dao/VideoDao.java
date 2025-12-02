package com.example.videoweb.dao;

import com.example.videoweb.entity.Category;
import com.example.videoweb.entity.Video;

import java.util.*;

public class VideoDao {
    private static final List<Category> categories = Arrays.asList(
            new Category("digital", "数码"),
            new Category("edu", "教育"),
            new Category("tech", "科技"),
            new Category("sport", "运动"),
            new Category("life", "生活")
    );

    private static final List<Video> videos = Arrays.asList(
            new Video(1, "iPhone 16 Pro 开箱", "digital", "iphone16.mp4", "1.jpg"),
            new Video(2, "MacBook Pro M4 评测", "digital", "macbook.mp4", "2.jpg"),
            new Video(3, "Java 从入门到入土", "edu", "java.mp4", "3.jpg"),
            new Video(4, "2025 黑科技盘点", "tech", "tech2025.mp4", "4.jpg"),
            new Video(5, "NBA 扣篮集锦", "sport", "nba.mp4", "5.jpg"),
            new Video(6, "治愈系猫咪日常", "life", "cat.mp4", "6.jpg")
            // 你可以继续加……
    );

    public List<Category> getAllCategories() { return categories; }

    public List<Video> getVideosByCategory(String categoryId) {
        if (categoryId == null || categoryId.isEmpty()) categoryId = "digital";
        String finalId = categoryId;
        return videos.stream()
                .filter(v -> v.getCategoryId().equals(finalId))
                .toList();
    }

    public Video getVideoById(int id) {
        return videos.stream().filter(v -> v.getId() == id).findFirst().orElse(null);
    }
}
