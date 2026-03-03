# 项目部署文档

本文档详细说明如何将租赁平台项目部署到新的环境中。

## 1. 环境要求

在目标机器上，需要安装以下软件环境：

*   **Java Development Kit (JDK)**: 版本 17 或更高
*   **Node.js**: 版本 16+ (建议使用 LTS 版本)
*   **MySQL**: 版本 8.0+
*   **Maven**: 版本 3.6+ (构建后端需要)
*   **Git** (可选，用于拉取代码)

## 2. 数据库准备

1.  安装并启动 MySQL 服务。
2.  确保 MySQL 的 root 用户密码为 `root`（或者您可以修改后端配置以匹配您的密码）。
3.  创建一个名为 `rental_db` 的空数据库：
    ```sql
    CREATE DATABASE rental_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
    ```
4.  **注意**: 项目后端启动时会自动读取 `src/main/resources/db` 下的 `schema.sql` 和 `data.sql` 进行表结构初始化和数据填充。

## 3. 后端部署 (Spring Boot)

### 3.1 修改配置 (如果需要)

如果您的 MySQL 密码不是 `root`，或者想要修改端口，请编辑 `backend/src/main/resources/application.yml` 文件：

```yaml
server:
  port: 8080  # 后端服务端口

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/rental_db?...
    username: root  # 修改为您的数据库用户名
    password: root  # 修改为您的数据库密码
```

### 3.2 编译打包

在 `backend` 目录下打开终端/命令行：

```bash
# 进入后端目录
cd backend

# 使用 Maven 打包 (跳过测试以加快速度)
mvn clean package -DskipTests
```

打包成功后，会在 `backend/target` 目录下生成一个 jar 包，例如 `rental-platform-0.0.1-SNAPSHOT.jar`。

### 3.3 启动服务

```bash
java -jar target/rental-platform-0.0.1-SNAPSHOT.jar
```

启动成功后，后端将在 `http://localhost:8080` 运行。

## 4. 前端部署 (Vue 3)

### 4.1 安装依赖

在 `frontend` 目录下打开终端/命令行：

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install
```

### 4.2 部署方式一：开发/测试模式 (推荐用于快速预览)

如果您只是想在新电脑上运行起来查看效果，可以使用开发模式：

```bash
npm run dev
```

启动后，访问终端显示的地址（通常是 `http://localhost:5173`）。
*注意：此模式下前端会通过 Vite 代理将 `/api` 请求转发到本地的 8080 端口，请确保后端已启动。*

### 4.3 部署方式二：生产环境构建 (推荐用于正式部署)

1.  **构建静态文件**:

    ```bash
    npm run build
    ```

    构建完成后，会在 `frontend` 目录下生成 `dist` 文件夹，里面包含了所有的静态资源 (HTML, CSS, JS)。

2.  **使用 Web 服务器托管 (以 Nginx 为例)**:

    将 `dist` 目录下的所有文件复制到 Nginx 的 `html` 目录或您指定的站点目录。

    配置 Nginx (`nginx.conf`) 以处理静态文件和反向代理 API 请求：

    ```nginx
    server {
        listen       80;
        server_name  localhost;

        # 前端静态文件
        location / {
            root   /path/to/your/dist; # 替换为 dist 文件夹的实际绝对路径
            index  index.html index.htm;
            try_files $uri $uri/ /index.html; # 解决 Vue Router history 模式刷新 404 问题
        }

        # 后端 API 代理
        location /api/ {
            proxy_pass http://localhost:8080/; # 转发到后端端口
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
        }
    }
    ```

## 5. 常见问题

1.  **数据库连接失败**: 请检查 `application.yml` 中的数据库 URL、用户名和密码是否正确，以及 MySQL 服务是否已启动。
2.  **端口冲突**: 如果 8080 或 5173 端口被占用，请在配置文件中修改端口号。
3.  **跨域问题**: 开发模式下 `vite.config.js` 已配置代理。生产环境下请确保 Nginx 正确配置了 `location /api/` 的反向代理。
