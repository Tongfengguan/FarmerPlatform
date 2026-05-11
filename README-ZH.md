# 智慧三农平台 (Farmer Platform): 万物生长

这是一个现代化的全栈“智慧三农”平台，旨在通过技术手段连接土地与心灵。项目为农户和消费者提供了一个集政策资讯、农资商城、业务管理于一体的无缝生态系统。

## 🌿 “万物生长 (The Harvest)” 设计系统

我们对平台进行了彻底的视觉重构，摒弃了传统的死板布局，引入了 **“以人为本”** 的自然有机设计哲学：

-   **生物感配色:** 采用清新活力的 **祖母绿 (Emerald Green)** 作为主色调，辅以象征收获的 **琥珀金 (Amber Gold)** 和如清晨露水般的 **板岩灰 (Slate)**。
-   **悬浮岛屿布局:** 导航栏和内容容器采用了“悬浮岛屿”和“便当盒 (Bento Grid)”模式，营造出通透、现代且富有空间感的视觉体验。
-   **有机曲线:** 大圆角设计与分层自然阴影模仿了自然界中的柔和形态，有效缓解长时间操作带来的视觉疲劳。
-   **现代交互:** 流体过渡动画、毛玻璃特效以及为管理员精心设计的响应式“悬浮侧边栏”。

## 🌟 核心特性

-   **智能 AI 助手:** 基于 **DeepSeek-V3** 模型，通过 LangChain 构建。AI Agent 能够实时感知业务数据，提供自然语言分析报告和自动化决策建议。
-   **高性能后端:** 基于 **Spring Boot 3+**，实现数据库级分页、优化后的 SQL 聚合查询以及稳健的基于 DTO 的 API 设计。
-   **响应式前端:** 采用 **Vue 3 (Composition API)** 构建的高响应界面，使用 **Pinia** 进行状态管理，并深度定制了 **Element Plus** 组件库。
-   **真实数据驱动:** 严格的真实数据驱动架构，零 Mock 依赖，确保项目具备生产级的可扩展性。

## 🛠 技术栈

### 前端 (Vue 生态)
- `Vue 3 (Composition API)` + `Vite`
- `Pinia` (支持增量更新的状态管理)
- `Element Plus` (已适配“万物生长”设计系统)
- `Axios` & `Native Fetch`

### 后端 (Java 生态)
- `Java 21` + `Spring Boot 3`
- `Spring Data JPA` + `MySQL`
- `Lombok` + `JJWT` (安全身份验证)

### AI 服务 (Agent)
- `Node.js` + `TypeScript`
- `LangChain` + `DeepSeek API`
- `Server-Sent Events (SSE)` 实现打字机流式分析回复

## 🚀 快速启动

### 1. 后端 API (端口 8080)
```bash
cd backend/farmer_platform
.\gradlew.bat bootRun
```

### 2. AI Agent 服务 (端口 3000)
进入 `ai-agent` 目录，在 `.env` 中配置 `DEEPSEEK_API_KEY`：
```bash
cd ai-agent
npm install
npm start
```

### 3. 前端界面 (端口 5173)
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
  │   ├── views/admin/          # 管理端页面 (现代悬浮布局)
  │   ├── views/user/           # 用户端页面 (岛屿导航架构)
  │   └── stores/               # Pinia 状态管理
  └── GEMINI.md                 # 项目上下文与工程规范
```

## 🔐 默认账号

-   **管理员:** `tfgkk` / `123456`
-   **普通用户:** `张大农` / `123456`

---
*用科技连接土地与心灵，开启智慧农业新篇章。*
