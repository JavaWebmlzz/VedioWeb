package com.example.videoweb.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;

@WebServlet("/getAd")
public class AdServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String cat = req.getParameter("cat");
        if (cat == null || cat.isEmpty()) {
            cat = "default";
        }

        String adJson;

        switch (cat) {
            case "digital":
                adJson = "{\"adUrl\":\"/VideoWeb_war_exploded/videos/ad/test-ad.mp4\","
                        + "\"position\":\"middle\","
                        + "\"duration\":7,"
                        + "\"insertAt\":3}";
                break;
            case "edu":
                adJson = "{\"adUrl\":\"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4\","
                        + "\"position\":\"middle\","
                        + "\"duration\":10,"
                        + "\"insertAt\":45}";  // 在正片第45秒插入
                break;
            case "tech":
                adJson = "{\"adUrl\":\"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4\","
                        + "\"position\":\"end\","
                        + "\"duration\":20}";
                break;
            case "sport":
                adJson = "{\"adUrl\":\"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4\","
                        + "\"position\":\"start\","
                        + "\"duration\":12}";
                break;
            case "life":
                adJson = "{\"adUrl\":\"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4\","
                        + "\"position\":\"middle\","
                        + "\"duration\":18,"
                        + "\"insertAt\":60}";  // 在正片第60秒插入
                break;
            default:
                adJson = "{\"adUrl\":\"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4\","
                        + "\"position\":\"start\","
                        + "\"duration\":10}";
                break;
        }

        PrintWriter out = resp.getWriter();
        out.print(adJson);
        out.flush();
    }
}