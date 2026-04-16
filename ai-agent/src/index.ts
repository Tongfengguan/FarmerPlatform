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

const PLATFORM_BASE_URL = "http://localhost:8080/api";
const DEEPSEEK_API_KEY = process.env.DEEPSEEK_API_KEY;

if (!DEEPSEEK_API_KEY) {
  console.error("❌ 错误: 请在 .env 文件中设置 DEEPSEEK_API_KEY");
  process.exit(1);
}

// 2. 定义分析工具
const getPlatformProductsTool = tool(
  async () => {
    const targetUrl = `${PLATFORM_BASE_URL}/platform/products`;
    console.log(`[Tool] Fetching: ${targetUrl}`);
    try {
      const response = await axios.get(targetUrl, {
        params: { page: 0, size: 100 },
        timeout: 5000,
        proxy: false, // 禁用系统代理，防止 502
      });
      if (response.data.success) {
        const products = response.data.data.content.map((p: any) => ({
          name: p.name,
          price: p.price,
          stock: p.stock,
          sales: p.salesCount,
          category: `${p.categoryL1} > ${p.categoryL2}`
        }));
        console.log(`[Tool] Successfully fetched ${products.length} products.`);
        return JSON.stringify(products);
      }
      return "获取后端数据失败，接口返回 success 为 false。";
    } catch (error: any) {
      console.error(`[Tool Error] ${error.message}`);
      return `无法连接到后端 API (${PLATFORM_BASE_URL})。请检查后端服务是否正在运行。错误信息: ${error.message}`;
    }
  },
  {
    name: "get_products",
    description: "获取农资商城所有商品的实时库存、价格和销量数据",
    schema: z.object({}),
  }
);

// 3. AI 逻辑封装
const baseConfig = {
  modelName: "deepseek-chat", 
  openAIApiKey: DEEPSEEK_API_KEY,
  configuration: { baseURL: "https://api.deepseek.com" },
  temperature: 0,
};

const llm = new ChatOpenAI({ ...baseConfig, streaming: false });
const streamingLlm = new ChatOpenAI({ ...baseConfig, streaming: true });

// 屏蔽报错
llm.getNumTokens = async () => 0;
streamingLlm.getNumTokens = async () => 0;

const tools = [getPlatformProductsTool];
const modelWithTools = llm.bindTools(tools);

// 4. API 接口
app.post("/api/analyze", async (req: Request, res: Response) => {
  const { question } = req.body;
  console.log(`[User Request] ${question}`);

  res.setHeader('Content-Type', 'text/event-stream');
  res.setHeader('Cache-Control', 'no-cache');
  res.setHeader('Connection', 'keep-alive');

  try {
    const messages: any[] = [
      { role: "system", content: "你是一个智慧三农系统的 AI 专家。请使用工具获取数据并回答问题。" },
      { role: "user", content: question }
    ];

    // 第一阶段：判定并调用工具
    const response = await modelWithTools.invoke(messages);
    
    if (response.tool_calls && response.tool_calls.length > 0) {
      console.log(`[Agent] Executing tools...`);
      let allToolResults = "";
      for (const toolCall of response.tool_calls) {
        const toolInstance = tools.find(t => t.name === toolCall.name);
        if (toolInstance) {
          const result = await (toolInstance as any).invoke(toolCall.args);
          allToolResults += `\n[${toolCall.name} 结果]: ${result}\n`;
        }
      }
      
      // 第二阶段：生成报告 (Safe Summary)
      console.log("[Agent] Streaming concise report to UI...");
      const summaryMessages = [
        { role: "system", content: "你是一个高效的数据分析助理。请根据数据给出极其精简、直击重点的分析。使用 3-4 个要点说明，总字数控制在 200 字以内。严禁输出任何标签，直接给结论。" },
        { role: "user", content: `用户问题：${question}\n\n实时业务数据：\n${allToolResults}\n\n请简要分析并给出建议：` }
      ];

      const stream = await streamingLlm.stream(summaryMessages);
      for await (const chunk of stream) {
        if (chunk.content) {
          res.write(`data: ${JSON.stringify({ content: chunk.content })}\n\n`);
        }
      }
    } else {
      console.log("[Agent] No tools needed, replying directly.");
      const stream = await streamingLlm.stream(messages);
      for await (const chunk of stream) {
        if (chunk.content) {
          res.write(`data: ${JSON.stringify({ content: chunk.content })}\n\n`);
        }
      }
    }
    
    res.write('data: [DONE]\n\n');
    res.end();
  } catch (error: any) {
    console.error("[Fatal Error] ", error.message);
    res.write(`data: ${JSON.stringify({ error: error.message })}\n\n`);
    res.end();
  }
});

const PORT = 3000;
app.listen(PORT, () => {
  console.log(`🚀 AI Agent is ready on http://127.0.0.1:${PORT}`);
});
