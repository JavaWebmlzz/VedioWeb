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
                // 1. 设置基本视频信息
                req.setAttribute("video", video);
                req.setAttribute("categoryId", video.getCategoryId());

                // ---------------- 核心推荐逻辑开始 ----------------
                // 2. 获取用户唯一标识 (这里用 SessionID 作为示例，无需登录也能用)
                String userIdentifier = req.getSession().getId();

                // 3. 获取当前视频的数字ID
                int currentNumId = dao.getNumIdByStrId(video.getCategoryId());

                // 4. 写入数据库：点击次数+1
                dao.incrementClickCount(userIdentifier, currentNumId);

                // 5. 从数据库查：当前最感兴趣的 num_id
                int recommendNumId = dao.getBestCategoryNumId(userIdentifier);

                // 6. 放入 request 作用域，供 JSP 直接使用 EL 表达式读取
                req.setAttribute("recommendNumId", recommendNumId);
                // ---------------- 核心推荐逻辑结束 ----------------
            }
        }
        req.getRequestDispatcher("/play.jsp").forward(req, resp);
    }
}
