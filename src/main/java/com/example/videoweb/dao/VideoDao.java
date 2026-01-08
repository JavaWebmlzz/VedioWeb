package com.example.videoweb.dao;

import com.example.videoweb.entity.Category;
import com.example.videoweb.entity.Video;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VideoDao {
    // 数据库连接配置 (建议改为读取配置文件，这里为了方便直接写了)
    private static final String URL = "jdbc:mysql://10.100.164.12:3306/videoweb" +
            "?useUnicode=true&characterEncoding=utf8" +
            "&useSSL=false" +
            "&serverTimezone=Asia/Shanghai" +
            "&allowPublicKeyRetrieval=true" +
            "&rewriteBatchedStatements=true";
    private static final String USER = "root";
    private static final String PASSWORD = "Zmk040906!"; // 记得确认你的密码

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // ================= 原有基础方法 =================

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        // 按 num_id 排序，保证导航栏顺序正确 (教育->科技->体育->娱乐)
        String sql = "SELECT id, name FROM category ORDER BY num_id ASC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Category(rs.getString("id"), rs.getString("name")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Video> getVideosByCategory(String categoryId) {
        // 默认改为 edu，防止空指针
        if (categoryId == null || categoryId.isEmpty()) categoryId = "edu";
        List<Video> list = new ArrayList<>();
        String sql = "SELECT id, title, category_id, url, thumb FROM video WHERE category_id = ? ORDER BY id";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Video(rs.getInt("id"), rs.getString("title"), rs.getString("category_id"), rs.getString("url"), rs.getString("thumb")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Video getVideoById(int id) {
        String sql = "SELECT id, title, category_id, url, thumb FROM video WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Video(rs.getInt("id"), rs.getString("title"), rs.getString("category_id"), rs.getString("url"), rs.getString("thumb"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Video> getAllVideos() {
        List<Video> list = new ArrayList<>();
        String sql = "SELECT id, title, category_id, url, thumb FROM video ORDER BY id DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Video(rs.getInt("id"), rs.getString("title"), rs.getString("category_id"), rs.getString("url"), rs.getString("thumb")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void insertVideo(String title, String categoryId, String url, String thumb) throws SQLException {
        String sql = "INSERT INTO video (title, category_id, url, thumb) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, categoryId);
            ps.setString(3, url);
            ps.setString(4, thumb);
            ps.executeUpdate();
        }
    }

    // ================= 【新功能】用户兴趣与推荐算法 =================

    /**
     * 更新用户的兴趣数据
     * @param userIdentifier 用户标识 (SessionID 或 USER_ID)
     * @param categoryNumId 分类数字ID (1=教育, 2=科技...)
     * @param durationSeconds 本次观看时长 (秒)
     */
    public void updateUserInterest(String userIdentifier, int categoryNumId, int durationSeconds) {
        // 核心 SQL：
        // 如果记录不存在 -> 插入 (点击=1, 时长=N)
        // 如果记录已存在 -> 更新 (点击+1, 时长+N)
        String sql = "INSERT INTO user_interest (user_identifier, category_num_id, click_count, total_seconds) " +
                "VALUES (?, ?, 1, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "click_count = click_count + 1, " +
                "total_seconds = total_seconds + VALUES(total_seconds)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userIdentifier);
            ps.setInt(2, categoryNumId);
            ps.setInt(3, durationSeconds);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 【推荐算法核心】获取该用户最感兴趣的分类 ID
     * 算法逻辑：得分 = 点击次数 * 10 + 总观看秒数
     * @param userIdentifier 用户标识
     * @return 推荐的 category_num_id (默认返回 1-教育)
     */
    public int getBestCategoryNumId(String userIdentifier) {
        // 按计算后的得分倒序排列，取第1个
        String sql = "SELECT category_num_id FROM user_interest " +
                "WHERE user_identifier = ? " +
                "ORDER BY (click_count * 10 + total_seconds) DESC LIMIT 1";

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
        // 如果该用户没有任何记录（新用户），默认推送教育类广告 (ID=1)
        return 1;
    }

    /**
     * 辅助工具：把字符串分类ID转为数字ID
     * 对应数据库里的 num_id
     */
    public int getNumIdByStrId(String strId) {
        if ("edu".equals(strId)) return 1;
        if ("tech".equals(strId)) return 2;
        if ("sport".equals(strId)) return 3;
        if ("entertainment".equals(strId)) return 4;
        return 2; // 默认返回科技
    }
}
