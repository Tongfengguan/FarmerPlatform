# Farmer Platform 后端深度面试指南 (精简与进阶版)

本项目是典型的 **Java + Spring Boot + AI Agent** 架构。本指南旨在帮助你从“会写代码”升华为“理解架构”。

---

## 1. 架构选型与基础问答

### 📦 为什么引入 DTO？直接返回 Entity 不行吗？
*   **核心痛点**：Entity 与数据库结构强绑定。如果直接返回，Entity 的字段变化会直接破坏前端 API 协议。
*   **DTO 的价值**：
    *   **解耦**：API 协议（DTO）与存储模型（Entity）独立演进。
    *   **安全性**：防止 `passwordHash`、`internalStatus` 等字段意外泄露。
    *   **性能**：避免 Entity 的“循环引用”导致 JSON 序列化崩溃，且能通过 Record 类型实现不可变传输。

### 🛠️ 为什么 AI Agent 选用 LangChain 而非原生 API？
*   **统一抽象**：LangChain 提供了统一的接口，未来如果将 DeepSeek 切换为 GPT-4 或本地的 Llama 3，只需更改一行配置，业务代码零改动。
*   **组件化**：它提供了成熟的 `Tool`（工具调用）和 `Memory`（记忆）管理机制。
*   **本项目应用**：我们利用 LangChain 的 `AgentExecutor` 实现了“两步走”架构（获取数据 -> 干净总结），有效隔离了 AI 的中间思维过程。

### 🐍 AI 部分为什么用 TS 而不是 Python？
*   **Node.js 优势**：
    1.  **原生流式支持**：Node.js 对 SSE（服务器发送事件）的长连接处理非常成熟，适合 AI 这种“字接字”的流式输出。
    2.  **全栈效率**：本项目前端为 Vue (TS)，AI 层也采用 TS，可以实现**端到端的类型安全**（Type Safety），减少联调时的字段错误。
*   **Python 的劣势**：虽然 Python 生态强，但本项目更倾向于“工程落地”。如果引入 Python，需要额外部署一套环境，增加运维复杂度。

---

## 2. 数据库与 JPA 性能优化

### 🧩 如何排查并解决 JPA 的 N+1 问题？
*   **现象**：查询 10 个订单，后台发出了 1 条查订单 SQL + 10 条查用户名的 SQL。
*   **排查**：在 `application.properties` 中开启 `show-sql` 和 `format_sql`。
*   **本项目方案**：
    1.  **自定义 SQL 聚合**：在 `UserAccountRepository` 中，通过原生 SQL (`@Query`) 结合 `COUNT` 和 `SUM` 聚合函数，一次性查出用户统计数据。
    2.  **分页限制**：在 `ProductService` 中强制使用 `Pageable`，从根源上控制单次查询的数据量。

### 📉 为什么关闭 OSIV (Open Session In View)？
*   **设置**：`spring.jpa.open-in-view=false`。
*   **理由**：开启 OSIV 会导致数据库连接在渲染视图期间一直被占用，高并发下极易导致**连接池枯竭**。我们选择在 Service 层处理好所有数据加载。

---

## 3. 身份认证与安全 (Security)

### 🔐 详细描述你的 JWT 认证流程
1.  **签发**：登录成功，`JwtService` 将 `userId` 和 `role` 写入 Payload，使用 HS256 签名生成 Token。
2.  **验证**：`AuthInterceptor` 拦截所有 `/api/**` 请求。
3.  **漏洞修复 (亮点)**：修复了拦截器误拦 **OPTIONS 预检请求** 的问题。OPTIONS 请求不带 Token，如果不放行，浏览器会因 CORS 失败而无法发起正式请求。

---

## 4. 复杂故障排查 (Troubleshooting)

### 🛠️ 502 Bad Gateway 的真实案例分析
面试官最喜欢听“填坑”故事：
*   **背景**：AI 助理在本地调用后端时报错。
*   **排查过程**：
    1.  **网络层**：排查 Node.js 客户端是否受系统全局代理（VPN）干扰，最终通过 `axios` 禁用 `proxy` 解决。
    2.  **协议层**：定位 `localhost` 在 Windows 下解析为 IPv6 (`::1`) 导致与监听 IPv4 (`127.0.0.1`) 的后端无法通信，最终统一使用 `localhost`。
    3.  **进程层**：发现因异常崩溃留下的“僵尸进程”抢占端口，实现了精准清理脚本。

---

## 5. 面试加分回答技巧
*   **谈到一致性**：提到在 `OrderService.createOrder` 中使用 `@Transactional`，并强调这同时涉及到了**业务逻辑校验**（库存是否充足）和**数据持久化**。
*   **谈到规范**：强调项目使用了 `ApiResponse<T>` 统一结构，这体现了对**企业级接口规范**的遵循。
*   **谈到 AI**：重点介绍 **Safe Summary Mode**。面试官会看到你不仅会接入 AI，还能解决 AI 的“协议泄露”这种真实业务痛点。

---

**总结建议**：面试时不要只说“我写了 XXX 功能”，要说“我为了解决 XXX 问题，对比了 XXX 方案，最终选择了 XXX，并优化了 XXX”。
