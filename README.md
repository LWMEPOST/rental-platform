# 租赁平台

## 项目介绍

本项目是一个租赁平台，包含 Spring Boot 后端和 Vue 前端，面向租赁商品展示、订单处理、用户管理和平台后台维护等业务场景。

## 技术栈

- Spring Boot 2.7
- MyBatis-Plus
- MySQL
- Knife4j/OpenAPI
- Vue 3
- Vite
- Element Plus

## 部署要求

- JDK 17
- Maven 3.x
- MySQL 8.0
- Node.js 16 或以上
- npm/pnpm

## 运行流程

1. 创建数据库并导入项目 SQL。
2. 修改 backend/src/main/resources/application.yml 数据库配置。
3. 进入 backend 执行 mvn spring-boot:run。
4. 进入 frontend 执行 npm install。
5. 执行 npm run dev 启动前端并配置接口地址。

## 项目结构

- backend：Spring Boot 后端
- frontend：Vue 前端
- tools：本地辅助工具（上传时过滤）
