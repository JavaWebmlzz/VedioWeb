package com.example.videoweb.servlet;

import com.example.videoweb.dao.VideoDao;
import com.example.videoweb.entity.Video;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/play")
public class PlayServlet extends HttpServlet {
    private final VideoDao dao = new VideoDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr != null) {
            int id = Integer.parseInt(idStr);
            Video video = dao.getVideoById(id);
            if (video != null) {
                req.setAttribute("video", video);
                // 新增：把分类ID也传给JSP，用于请求广告
                req.setAttribute("categoryId", video.getCategoryId());
            }
        }
        req.getRequestDispatcher("/play.jsp").forward(req, resp);
    }
}
