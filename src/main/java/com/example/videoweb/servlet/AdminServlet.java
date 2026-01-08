package com.example.videoweb.servlet;

import com.example.videoweb.dao.VideoDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@WebServlet("/admin")
// 必须加这个注解才能处理文件上传
// maxFileSize: 单个文件最大限制 (这里设为 500MB)
// maxRequestSize: 整个表单最大限制 (这里设为 1GB)
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 500,
        maxRequestSize = 1024 * 1024 * 1024)
public class AdminServlet extends HttpServlet {

    private final VideoDao dao = new VideoDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("categories", dao.getAllCategories());
        req.setAttribute("videos", dao.getAllVideos());
        req.getRequestDispatcher("/admin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); // 防止中文文件名乱码

        String action = req.getParameter("action");
        if ("add".equals(action)) {
            try {
                // 1. 获取普通文本字段
                String title = req.getParameter("title");
                String categoryId = req.getParameter("category");

                // 2. 获取上传的文件 Part
                Part videoPart = req.getPart("videoFile");
                Part imagePart = req.getPart("imageFile");

                // 3. 确定服务器上的物理存储路径
                // 如果是 Linux (你的服务器)，存到 /root/upload/
                // 如果是 Windows (你本地调试)，存到 D:/upload/ (你需要自己建这个文件夹)
                String os = System.getProperty("os.name").toLowerCase();
                String basePath;
                if (os.contains("win")) {
                    basePath = "D:/upload/";
                } else {
                    basePath = "/root/upload/";
                }

                String videoSavePath = basePath + "video/";
                String imageSavePath = basePath + "image/";

                // 确保文件夹存在
                new File(videoSavePath).mkdirs();
                new File(imageSavePath).mkdirs();

                // 4. 处理视频文件
                // 生成唯一文件名，防止重名覆盖 (如: 1234-5678.mp4)
                String videoFileName = UUID.randomUUID().toString() + getFileExtension(videoPart);
                // 保存到硬盘 (物理路径)
                videoPart.write(videoSavePath + videoFileName);
                // 生成数据库存的 URL (虚拟路径 /videos/...)
                String dbVideoUrl = "/videos/" + videoFileName;

                // 5. 处理图片文件
                String imageFileName = UUID.randomUUID().toString() + getFileExtension(imagePart);
                imagePart.write(imageSavePath + imageFileName);
                String dbThumbUrl = "/images/" + imageFileName;

                // 6. 存入数据库
                dao.insertVideo(title, categoryId, dbVideoUrl, dbThumbUrl);

                req.setAttribute("message", "上传成功！");

            } catch (Exception e) {
                e.printStackTrace();
                req.setAttribute("error", "上传失败：" + e.getMessage());
            }
        }

        // 刷新列表
        doGet(req, resp);
    }

    // 辅助方法：获取文件后缀名 (例如 .mp4)
    private String getFileExtension(Part part) {
        String submittedFileName = part.getSubmittedFileName();
        if (submittedFileName != null && submittedFileName.contains(".")) {
            return submittedFileName.substring(submittedFileName.lastIndexOf("."));
        }
        return "";
    }
}
