import { ChatOpenAI } from "@langchain/openai";
import { tool } from "@langchain/core/tools";
import { z } from "zod";
import * as dotenv from "dotenv";
import axios from "axios";
import express, { Request, Response } from "express";
import cors from "cors";

// 1. 加载配置
dotenv.config();

const app = express();
app.use(cors());
app.use(express.json());

const PLATFORM_BASE_URL = "http://127.0.0.1:8080/api";
const DEEPSEEK_API_KEY = process.env.DEEPSEEK_API_KEY;

if (!DEEPSEEK_API_KEY) {
  console.error("❌ 错误: 请在 .env 文件中设置 DEEPSEEK_API_KEY");
  process.exit(1);
}

// 2. 定义分析工具 (保持不变)
const getPlatformProductsTool = tool(
  async () => {
    console.log(`\n[Agent 调用] 🔌 正在调取后端数据...`);
    try {
      const response = await axios.get(`${PLATFORM_BASE_URL}/platform/products`, {
        params: { page: 0, size: 100 },
        timeout: 5000,
      });
      if (response.data.success) {
        return JSON.stringify(response.data.data.content.map((p: any) => ({
          id: p.id,
          name: p.name,
          price: p.price,
          stock: p.stock,
          sales: p.salesCount,
          category: `${p.categoryL1} > ${p.categoryL2}`
        })));
      }
      return "获取数据失败。";
    } catch (error: any) {
      return `连接失败: ${error.message}`;
    }
  },
  {
    name: "get_products",
    description: "获取农资商城所有商品的实时库存、价格和销量数据",
    schema: z.object({}),
  }
);

// 3. AI 逻辑封装 (开启 streaming)
const llm = new ChatOpenAI({
  modelName: "deepseek-chat", 
  openAIApiKey: DEEPSEEK_API_KEY,
  configuration: { baseURL: "https://api.deepseek.com" },
  temperature: 0,
  streaming: true, // 开启流式
});

const tools = [getPlatformProductsTool];
const modelWithTools = llm.bindTools(tools);

// 4. 流式 API 接口
app.post("/api/analyze", async (req: Request, res: Response) => {
  const { question } = req.body;
  console.log(`\n👤 管理员提问: ${question}`);

  // 设置 SSE 响应头
  res.setHeader('Content-Type', 'text/event-stream');
  res.setHeader('Cache-Control', 'no-cache');
  res.setHeader('Connection', 'keep-alive');

  try {
    const messages: any[] = [
      ["system", "你是一个智慧三农管理系统的 AI 专家助手。请通过调用工具分析实时数据来回答管理员的问题。"],
      ["human", question]
    ];

    // 第一阶段：思考与工具调用 (这部分目前 DeepSeek 不支持工具调用的流式，我们先同步处理)
    const response = await modelWithTools.invoke(messages);
    messages.push(response);

    if (response.tool_calls && response.tool_calls.length > 0) {
      for (const toolCall of response.tool_calls) {
        const toolInstance = tools.find(t => t.name === toolCall.name);
        if (toolInstance) {
          const result = await (toolInstance as any).invoke(toolCall.args);
          messages.push({
            role: "tool",
            name: toolCall.name,
            content: result,
            tool_call_id: toolCall.id
          });
        }
      }
      
      // 第二阶段：生成最终分析报告 (启用流式输出)
      console.log("🧠 正在流式生成报告...");
      const stream = await llm.stream(messages);
      for await (const chunk of stream) {
        if (chunk.content) {
          res.write(`data: ${JSON.stringify({ content: chunk.content })}\n\n`);
        }
      }
    } else {
      // 无需工具，直接流式回答
      const stream = await llm.stream(messages);
      for await (const chunk of stream) {
        if (chunk.content) {
          res.write(`data: ${JSON.stringify({ content: chunk.content })}\n\n`);
        }
      }
    }
    
    res.write('data: [DONE]\n\n');
    res.end();
  } catch (error: any) {
    console.error("Agent 运行报错:", error.message);
    res.write(`data: ${JSON.stringify({ error: error.message })}\n\n`);
    res.end();
  }
});

const PORT = 3000;
app.listen(PORT, () => {
  console.log(`\n🚀 AI Agent 流式服务已启动!`);
  console.log(`📍 接口地址: http://127.0.0.1:${PORT}/api/analyze`);
});
