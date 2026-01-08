package com.example.videoweb.servlet;

import com.example.videoweb.dao.UserDao;
import com.example.videoweb.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/user")
public class UserServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String u = req.getParameter("username");
        String p = req.getParameter("password");

        if ("login".equals(action)) {
            User user = userDao.login(u, p);
            if (user != null) {
                req.getSession().setAttribute("user", user);
                // ✅ 正确：跳到 HomeServlet (你的首页 Servlet 路径是 "" 或 "/home")
                // 这里用 "./" 或者 request.getContextPath() + "/" 都可以
                resp.sendRedirect(req.getContextPath() + "/");
            } else {
                req.setAttribute("error", "用户名或密码错误");
                req.getRequestDispatcher("login.jsp").forward(req, resp);
            }
        } else if ("register".equals(action)) {
            if (userDao.register(u, p)) {
                resp.sendRedirect("login.jsp?msg=reg_success");
            } else {
                req.setAttribute("error", "注册失败，用户名可能已存在");
                req.getRequestDispatcher("register.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 退出登录
        if ("logout".equals(req.getParameter("action"))) {
            req.getSession().removeAttribute("user");
            // ✅ 正确：退出后也跳回 Servlet，而不是 JSP
            resp.sendRedirect(req.getContextPath() + "/");
        }
    }
}

