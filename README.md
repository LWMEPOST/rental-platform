# 租赁平台项目 (Rental Platform)

这是一个基于 Spring Boot 和 Vue 3 开发的租赁管理平台。

## 📖 项目介绍

本项目旨在提供一个完整的设备租赁解决方案，包含用户端（租赁、归还、评论）和管理端（设备管理、订单管理、用户审核、数据统计）功能。

## ✨ 主要功能

### 用户端
*   **用户注册/登录**: 账号安全管理。
*   **实名认证**: 租赁前必须进行实名认证。
*   **设备浏览**: 查看可租赁设备列表及详情。
*   **在线租赁**: 下单租赁设备。
*   **归还设备**: 租赁结束后申请归还。
*   **评价系统**: 对租赁体验进行评价。

### 管理端
*   **仪表盘**: 查看核心运营数据（用户数、订单数、收入统计等）。
*   **设备管理**: 设备的增删改查、分类管理。
*   **订单管理**: 处理租赁订单、审核归还请求。
*   **用户管理**: 查看租客列表、审核实名认证。
*   **数据分析**: 运营数据的图表展示。

## 🛠️ 技术栈

### 后端
*   **核心框架**: Spring Boot 2.7
*   **ORM**: MyBatis-Plus
*   **数据库**: MySQL 8.0
*   **API 文档**: Knife4j (Swagger)

### 前端
*   **框架**: Vue 3 (Composition API)
*   **构建工具**: Vite
*   **UI 组件库**: Element Plus
*   **状态管理**: Pinia
*   **图表库**: ECharts

## 🚀 快速开始

详细的部署步骤请参考 [部署文档 (DEPLOY.md)](./DEPLOY.md)。

### 简要步骤

1.  **数据库**: 创建 `rental_db` 数据库（无需手动建表，后端启动自动初始化）。
2.  **后端**:
    ```bash
    cd backend
    mvn clean package
    java -jar target/rental-platform-0.0.1-SNAPSHOT.jar
    ```
3.  **前端**:
    ```bash
    cd frontend
    npm install
    npm run dev
    ```

## 📂 目录结构

```
rental-platform/
├── backend/            # 后端源码 (Spring Boot)
│   ├── src/main/java   # Java 代码
│   └── src/main/resources # 配置文件及 SQL 脚本
├── frontend/           # 前端源码 (Vue 3)
│   ├── src/views       # 页面组件
│   └── src/api         # API 接口封装
├── DEPLOY.md           # 详细部署文档
└── README.md           # 项目说明
```

## 📄 许可证

MIT License
