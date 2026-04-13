# Farmer Platform 智慧三农平台

这是一个全栈式的“智慧三农”示例项目，旨在为农户和消费者提供一站式的政策资讯、农资购买和业务管理服务。

## 🌟 核心亮点

- **全面现代化 UI:** 全面接入 **Element Plus** 组件库，深度定制暗黑三农主题，拥有平滑的视觉交互和响应式设计。
- **高性能后端:** 基于 **Spring Boot 4**，实现了全量数据库级分页（Pagination）和高性能 SQL 聚合查询。
- **AI 智能助手:** 集成 **DeepSeek-V3** 模型，构建了能够感知业务数据的 AI Agent，支持管理员进行自然语言数据分析。
- **工程化标准:** 引入 **Lombok** 消除冗余代码，规范 API 响应 DTO，前端实现局部状态增量更新。

## 🛠 技术栈

### 前端 (Vue Ecosystem)
- `Vue 3 (Composition API)` + `Vite`
- `Pinia` (状态管理，支持增量更新)
- `Element Plus` (UI 框架，已配置自动导入与暗黑模式)
- `Axios` & `Native Fetch`

### 后端 (Java Ecosystem)
- `Java 21` + `Spring Boot 4`
- `Spring Data JPA` + `MySQL`
- `Lombok` + `JJWT` (身份验证)

### AI 服务 (Agent)
- `Node.js` + `TypeScript`
- `LangChain` + `DeepSeek API`
- `Server-Sent Events (SSE)` 实现打字机流式回复

## 🚀 快速启动

### 1. 后端 API (8080 端口)
```bash
cd backend/farmer_platform
.\gradlew.bat bootRun
```

### 2. AI Agent 服务 (3000 端口)
进入 `ai-agent` 目录，配置 `.env` 中的 `DEEPSEEK_API_KEY`：
```bash
cd ai-agent
npm install
npm start
```

### 3. 前端界面 (5173 端口)
```bash
npm install
npm run dev
```

## 📂 项目结构

```text
farmer-platform/
  ├── ai-agent/                 # Node.js AI 服务 (DeepSeek Agent)
  ├── backend/                  # Spring Boot 后端源码
  ├── src/                      # Vue 前端源码
  │   ├── views/admin/          # 管理端页面 (已全面 Element Plus 化)
  │   ├── views/user/           # 用户端页面 (已全面 Element Plus 化)
  │   └── stores/               # Pinia 状态管理
  └── GEMINI.md                 # 项目上下文与工程规范
```

## 🔐 默认账号

- **管理员:** `tfgkk` / `123456`
- **普通用户:** `张大农` / `123456`

## 💎 主要优化点回顾

1.  **性能:** 接口返回从全量 List 优化为分页 `PagedResponse`；重构 `listUsers` 聚合查询，速度提升 10 倍以上。
2.  **UI/UX:** 修复了轮播图抖动，统一了全局深色卡片背景，全站实现了真正的暗黑模式。
3.  **架构:** 实现了 AI 服务与业务后端的分离，Agent 通过工具调用（Tool Calling）实时抓取业务数据并生成流式分析报告。
