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

    // 在 VideoDao 类中添加以下方法

    // 增加点击次数
    public void incrementClickCount(String userIdentifier, int categoryNumId) {
        // 使用 "INSERT ... ON DUPLICATE KEY UPDATE" 语句
        // 如果记录不存在就插入1，如果存在就+1
        String sql = "INSERT INTO user_interest (user_identifier, category_num_id, click_count) " +
                "VALUES (?, ?, 1) " +
                "ON DUPLICATE KEY UPDATE click_count = click_count + 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userIdentifier);
            ps.setInt(2, categoryNumId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 获取用户最感兴趣的 num_id
    public int getBestCategoryNumId(String userIdentifier) {
        // 查点击次数最多的那个 num_id
        String sql = "SELECT category_num_id FROM user_interest " +
                "WHERE user_identifier = ? " +
                "ORDER BY click_count DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userIdentifier);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("category_num_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 2; // 默认返回 2 (科技)，以防万一
    }

    // 辅助：根据字符串ID获取数字ID (这部分逻辑也可以查库，这里为了性能直接写死)
    public int getNumIdByStrId(String strId) {
        if ("edu".equals(strId)) return 1;
        if ("tech".equals(strId)) return 2;
        if ("sport".equals(strId)) return 3;
        if ("entertainment".equals(strId)) return 4;
        return 2;
    }

}

