package com.example.videoweb.servlet;

import com.example.videoweb.dao.VideoDao;
import com.example.videoweb.entity.User;
import com.example.videoweb.entity.Video;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
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
            try {
                int id = Integer.parseInt(idStr);
                Video video = dao.getVideoById(id);

                if (video != null) {
                    // 1. 设置视频基本信息
                    req.setAttribute("video", video);
                    req.setAttribute("categoryId", video.getCategoryId());

                    // ================= 推荐算法逻辑 =================

                    // 2. 识别用户身份 (登录用户用 USER_ID，游客用 SessionID)
                    HttpSession session = req.getSession();
                    User user = (User) session.getAttribute("user");
                    String userIdentifier = (user != null) ? "USER_" + user.getId() : session.getId();

                    // 3. 调用 DAO 的推荐算法，获取 num_id
                    // (算法：根据点击次数和时长计算权重，新用户默认返回 1-教育)
                    int recommendNumId = dao.getBestCategoryNumId(userIdentifier);

                    // 4. 将推荐结果传给 JSP，用于请求广告
                    req.setAttribute("recommendNumId", recommendNumId);

                    // Debug 日志 (方便你在服务器后台看效果)
                    System.out.println("用户 " + userIdentifier + " 进入播放页，推荐广告分类ID: " + recommendNumId);

                    // ===============================================
                }
            } catch (NumberFormatException e) {
                // 防止 id 不是数字导致的报错
                e.printStackTrace();
            }
        }
        req.getRequestDispatcher("/play.jsp").forward(req, resp);
    }
}
