package com.example.videoweb.servlet;

import com.example.videoweb.dao.VideoDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet({"", "/home"})  // 根路径和 /home 都指向这里
public class HomeServlet extends HttpServlet {
    private final VideoDao dao = new VideoDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String cat = req.getParameter("cat");
        req.setAttribute("categories", dao.getAllCategories());
        req.setAttribute("videos", dao.getVideosByCategory(cat));
        req.setAttribute("currentCat", cat == null ? "edu" : cat);
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
