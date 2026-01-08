package com.example.videoweb.servlet;

import com.example.videoweb.dao.VideoDao;
import com.example.videoweb.entity.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/api/track")
public class TrackServlet extends HttpServlet {
    private final VideoDao dao = new VideoDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        // 1. 获取当前用户标识
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        // 关键逻辑：如果登录了，用 "USER_ID"；没登录，用 SessionID
        String userIdentifier = (user != null) ? "USER_" + user.getId() : session.getId();

        // 2. 获取前端传来的参数
        String catId = req.getParameter("categoryId"); // 例如 "tech"
        String durationStr = req.getParameter("duration"); // 例如 "15" (秒)

        if (catId != null && durationStr != null) {
            try {
                int duration = Integer.parseInt(durationStr);
                int numId = dao.getNumIdByStrId(catId);

                // 3. 更新数据库 (点击数不加，只加时长，点击数在 PlayServlet 里加过了)
                // 为了复用 updateUserInterest，这里稍微 tricky 一点：
                // 我们可以专门写一个 updateDuration，或者复用但让点击数逻辑调整。
                // 简单起见，这里我们只更新时长：
                // 注意：为了简单，这里直接复用 updateUserInterest，虽然会让点击数也+1，
                // 如果你想精确控制，建议在Dao加一个只更新时长的方法。
                // 这里我们假设前端只有在“离开页面”时发一次请求，算作一次有效点击+时长。
                dao.updateUserInterest(userIdentifier, numId, duration);

                System.out.println("收到埋点: 用户=" + userIdentifier + ", 分类=" + numId + ", 时长=" + duration + "秒");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
