# 🎬 MK Video Web
> 实现了用户登录、视频播放、**跨域广告精准投放**、**防拖拽广告系统**及**用户行为埋点**等核心功能。

---

## 📖 项目简介

MK 视频网是一个模仿 Bilibili 风格的在线视频平台。本项目旨在通过原生 Servlet/JSP 技术深入理解 Web 底层原理（MVC模式、Session机制、HTTP协议），并创新性地实现了一套**基于用户行为画像的跨域广告推荐系统**。

**核心亮点：**
1.  **千人千面广告**：后端根据用户点击和观看时长实时计算兴趣权重，向前端推送个性化广告。
2.  **强制广告系统**：前端实现了一套防跳过机制，支持**中插广告**、**禁止拖拽快进**、**断点续播**。
3.  **动静分离**：视频/图片资源存储在物理磁盘（非 WAR 包内），通过 Tomcat 虚拟路径映射访问，支持大文件上传。
4.  **数据埋点**：利用 `navigator.sendBeacon` 技术实现无感知的用户行为（观看时长）上报。

---

## 🛠️ 技术栈

*   **后端框架**: Java Servlet, JSP, JDBC (原生开发，无 Spring)
*   **前端技术**: HTML5, CSS3 (B站风格), JavaScript (ES6+), Fetch API
*   **数据库**: MySQL 8.0
*   **构建工具**: Maven
*   **服务器**: Apache Tomcat 10/11 (支持 Jakarta EE)
*   **开发环境**: IntelliJ IDEA

---

## 📂 项目结构 (MVC 分层)

项目严格遵循 MVC 设计模式，实现了前后端逻辑分离。

```text
src/main/java/com/example/videoweb
├── dao             // 数据访问层 (Model)
│   ├── VideoDao.java     // 视频CRUD及推荐算法核心逻辑
│   └── UserDao.java      // 用户登录注册逻辑
├── entity          // 实体类 (Model)
│   ├── User.java
│   ├── Video.java
│   └── Category.java
└── servlet         // 控制层 (Controller)
    ├── HomeServlet.java  // 首页数据加载
    ├── PlayServlet.java  // 播放页及广告策略下发
    ├── UserServlet.java  // 登录注册控制器
    ├── AdminServlet.java // 视频上传 (支持虚拟路径)
    └── TrackServlet.java // 埋点接口 (Ajax)

src/main/webapp     // 视图层 (View)
├── css/style.css   // 全局样式表
├── js/play.js      // 播放器核心逻辑 (广告/防拖拽)
├── js/global.js    // 全局图片容错
├── index.jsp       // 首页
├── play.jsp        // 播放页
├── admin.jsp       // 后台管理
└── WEB-INF/web.xml // 路由配置
```

---

## 💾 数据库设计

请在 MySQL 中执行以下 SQL 语句初始化数据库：
SQL
CREATE DATABASE videoweb DEFAULT CHARSET utf8mb4;
USE videoweb;

1. 用户表
```
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL
);
```
2. 分类表 (num_id 用于推荐算法映射)
```
CREATE TABLE category (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    num_id INT
);
初始化分类数据
INSERT INTO category VALUES ('edu', '教育', 1), ('tech', '科技', 2), ('sport', '体育', 3), ('entertainment', '娱乐', 4);
```

3. 视频表
```
CREATE TABLE video (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    category_id VARCHAR(50),
    url TEXT,
    thumb TEXT,
    FOREIGN KEY (category_id) REFERENCES category(id)
);
```
4. 用户兴趣画像表 (核心表)
```
CREATE TABLE user_interest (
    user_identifier VARCHAR(100) NOT NULL, -- 用户ID或SessionID
    category_num_id INT NOT NULL,
    click_count INT DEFAULT 0,             -- 点击次数
    total_seconds INT DEFAULT 0,           -- 观看时长(秒)
    PRIMARY KEY (user_identifier, category_num_id)
);
```
---

## 🔧 部署指南
本地开发 (IDEA)
修改 VideoDao.java 中的数据库连接配置（URL, User, Password）。

在 D:/upload 创建 video 和 image 文件夹（用于存上传文件）。

在 IDEA 的 Tomcat 配置中，添加 Deployment (External Source)：

D:/upload/video -> /videos

D:/upload/image -> /images

运行 Tomcat 即可。

服务器部署 (Linux/Tomcat)
在服务器创建目录：mkdir -p /root/upload/video 和 /root/upload/image。

修改 Tomcat 的 conf/server.xml，在 <Host> 标签内添加：

XML
<Context docBase="/root/upload/video" path="/videos" reloadable="false" />
<Context docBase="/root/upload/image" path="/images" reloadable="false" />
使用 Maven 打包：mvn clean package。

将生成的 .war 文件重命名为 VideoWeb.war，上传至 webapps 目录。

重启 Tomcat。


