# Farmer Platform

智慧三农平台示例项目，包含：

- 前端：`Vue 3 + Vite + Pinia + Vue Router`
- 后端：`Spring Boot 4 + JPA + MySQL`
- 认证：账号密码登录、注册、忘记密码、JWT 登录态
- 业务：资讯、商品、地址、订单、管理端基础运营数据

## 当前完成情况

已经接入真实后端的数据模块：

- 登录 / 注册 / 忘记密码
- JWT 鉴权与当前用户信息恢复
- 资讯列表 / 详情 / 阅读量更新
- 商品列表 / 商品详情
- 地址列表 / 新增地址
- 购物车下单 / 立即购买
- 我的订单 / 支付 / 取消 / 确认收货
- 管理端数据概览
- 管理端资讯管理
- 管理端商品管理
- 管理端订单管理
- 管理端用户管理

## 默认账号

- 用户：`张大农`
- 管理员：`tfgkk`
- 默认密码：`123456`

## 项目结构

```text
farmer-platform/
  src/                          Vue 前端
    layouts/                    前后台布局
    router/                     路由与守卫
    stores/                     Pinia 状态
    views/                      页面
    utils/http.js               前端请求封装

  backend/farmer_platform/      Spring Boot 后端
    src/main/java/com/tfgkk/farmer_platform/
      auth/                     登录注册、JWT
      platform/                 资讯、商品、地址、订单、后台接口
      config/                   CORS、密码、初始化数据
      user/                     用户实体与仓库
```

## 前端启动

```bash
npm install
npm run dev
```

默认前端地址：

- 用户端：`http://localhost:5173/`
- 登录页：`http://localhost:5173/auth`
- 管理端：`http://localhost:5173/admin/dashboard`

## 后端启动

进入后端目录：

```bash
cd backend/farmer_platform
```

启动项目：

```bash
.\gradlew.bat bootRun
```

默认后端地址：

- `http://localhost:8080`

## 数据库配置

当前后端默认连接本地 MySQL：

- 数据库：`farmer_platform`
- 用户名：`root`
- 密码：`z236244462`

后端会自动创建数据库和表。

对应配置文件：

- [application.properties](/c:/Users/13161/Desktop/Vue_learn/farmer-platform/backend/farmer_platform/src/main/resources/application.properties)

## 已验证命令

前端构建：

```bash
npm run build
```

后端测试与构建：

```bash
cd backend/farmer_platform
.\gradlew.bat clean test build
```

## 主要接口

认证：

- `POST /api/auth/login`
- `POST /api/auth/register`
- `POST /api/auth/reset-password`
- `GET /api/auth/me`

平台：

- `GET /api/platform/bootstrap`
- `GET /api/platform/addresses`
- `POST /api/platform/addresses`
- `GET /api/platform/orders`
- `POST /api/platform/orders`
- `PATCH /api/platform/orders/{id}/pay`
- `PATCH /api/platform/orders/{id}/cancel`
- `PATCH /api/platform/orders/{id}/confirm`
- `PATCH /api/platform/articles/{id}/view`

管理端：

- `GET /api/admin/bootstrap`
- `POST /api/admin/articles`
- `PUT /api/admin/articles/{id}`
- `DELETE /api/admin/articles/{id}`
- `PATCH /api/admin/articles/{id}/toggle-status`
- `POST /api/admin/products`
- `PUT /api/admin/products/{id}`
- `PATCH /api/admin/products/{id}/toggle-status`
- `GET /api/admin/users`
- `PATCH /api/admin/users/{id}/toggle-status`
- `GET /api/admin/orders`
- `PATCH /api/admin/orders/{id}/ship`
- `PATCH /api/admin/orders/{id}/refund`

## 说明

- 前端保留了部分演示型本地状态，例如购物车本地勾选状态。
- 资讯、商品、地址、订单、管理端数据已经迁移到 Spring Boot 后端。
- `index.html` 如果还有你本地自己的改动，我没有覆盖它。
