package com.example.videoweb.servlet;

import com.example.videoweb.dao.VideoDao;
import com.example.videoweb.entity.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

@WebServlet("/admin")
@MultipartConfig(maxFileSize = 1024*1024*500)  // 单个文件最大500MB，可自行调整
public class AdminServlet extends HttpServlet {

    private final VideoDao dao = new VideoDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 显示管理页面：分类列表 + 现有视频列表
        req.setAttribute("categories", dao.getAllCategories());
        req.setAttribute("videos", dao.getAllVideos());  // 需要新增这个方法，稍后补上
        req.getRequestDispatcher("/admin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String action = req.getParameter("action");
        if ("add".equals(action)) {
            String title = req.getParameter("title");
            String categoryId = req.getParameter("category");
            String url = req.getParameter("videoUrl");      // 新增：视频 URL
            String thumb = req.getParameter("thumb");       // 封面文件名（如 7.jpg）

            if (title == null || categoryId == null || url == null || thumb == null ||
                    title.isBlank() || url.isBlank() || thumb.isBlank()) {
                req.setAttribute("error", "请填写完整信息");
            } else {
                try {
                    dao.insertVideo(title, categoryId, url, thumb);
                    req.setAttribute("message", "视频添加成功！");
                } catch (Exception e) {
                    e.printStackTrace();
                    req.setAttribute("error", "添加失败：" + e.getMessage());
                }
            }
        }

        // 刷新页面数据
        req.setAttribute("categories", dao.getAllCategories());
        req.setAttribute("videos", dao.getAllVideos());
        req.getRequestDispatcher("/admin.jsp").forward(req, resp);
    }
}