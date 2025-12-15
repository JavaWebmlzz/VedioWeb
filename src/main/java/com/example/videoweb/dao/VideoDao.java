package com.example.videoweb.dao;

import com.example.videoweb.entity.Category;
import com.example.videoweb.entity.Video;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VideoDao {
    private static final String URL = "jdbc:mysql://10.100.164.12:3306/videoweb" +
            "?useUnicode=true&characterEncoding=utf8" +
            "&useSSL=false" +
            "&serverTimezone=Asia/Shanghai" +
            "&allowPublicKeyRetrieval=true" +
            "&rewriteBatchedStatements=true";

    private static final String USER = "root";                    // 你登录时用的用户名
    private static final String PASSWORD = "Zmk040906!";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT id, name FROM category ORDER BY id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Category(rs.getString("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Video> getVideosByCategory(String categoryId) {
        if (categoryId == null || categoryId.isEmpty()) categoryId = "digital";
        List<Video> list = new ArrayList<>();
        String sql = "SELECT id, title, category_id, url, thumb FROM video WHERE category_id = ? ORDER BY id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Video(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("category_id"),
                            rs.getString("url"),     // ← 这里就是你问的地方：新增 url
                            rs.getString("thumb")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Video getVideoById(int id) {
        String sql = "SELECT id, title, category_id, url, thumb FROM video WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Video(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("category_id"),
                            rs.getString("url"),     // 新增 url
                            rs.getString("thumb")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 获取所有视频（用于管理页面显示）
    public List<Video> getAllVideos() {
        List<Video> list = new ArrayList<>();
        String sql = "SELECT id, title, category_id, url, thumb FROM video ORDER BY id DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Video(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("category_id"),
                        rs.getString("url"),
                        rs.getString("thumb")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 插入新视频
    public void insertVideo(String title, String categoryId, String url, String thumb) throws SQLException {
        String sql = "INSERT INTO video (title, category_id, url, thumb) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, categoryId);
            ps.setString(3, url);      // 存完整 URL
            ps.setString(4, thumb);    // 封面文件名或完整 URL
            ps.executeUpdate();
        }
    }
}