package com.example.videoweb.servlet;

import com.example.videoweb.dao.VideoDao;
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
        String id = req.getParameter("id");
        if (id != null) {
            req.setAttribute("video", dao.getVideoById(Integer.parseInt(id)));
        }
        req.getRequestDispatcher("/play.jsp").forward(req, resp);
    }
}
