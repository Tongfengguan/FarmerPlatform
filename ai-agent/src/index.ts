import { ChatOpenAI } from '@langchain/openai'
import { tool } from '@langchain/core/tools'
import { z } from 'zod'
import * as dotenv from 'dotenv'
import axios from 'axios'
import express, { Request, Response } from 'express'
import cors from 'cors'

// 1. 加载配置
dotenv.config()

const app = express()
app.use(cors())
app.use(express.json())

const PLATFORM_BASE_URL = 'http://localhost:8080/api'
const DEEPSEEK_API_KEY = process.env.DEEPSEEK_API_KEY

if (!DEEPSEEK_API_KEY) {
  console.error('❌ 错误: 请在 .env 文件中设置 DEEPSEEK_API_KEY')
  process.exit(1)
}

// --- 工具函数 ---

/**
 * 强力清理文本中的所有 XML/DSML 标签
 */
const sanitizeContent = (text: string): string => {
  return text
    .replace(/<[^>]*?>[\s\S]*?<\/[^>]*?>/g, '') // 匹配并删除完整的闭合块
    .replace(/[<｜|]DSML[｜|][^>]*>?/g, '') // 匹配并删除 DSML 标签碎片
    .replace(/function_calls>?/g, '') // 清理可能的残留关键字
    .replace(/invoke[^>]*>?/g, '')
    .replace(/<[^>]*?>/g, '') // 清理所有剩余的尖括号标签
    .trim()
}

/**
 * 后端 API 直连配置 (绕过代理)
 */
const apiClient = axios.create({
  baseURL: PLATFORM_BASE_URL,
  timeout: 5000,
  proxy: false,
})

// --- AI 配置 ---

const baseConfig = {
  modelName: 'deepseek-chat',
  openAIApiKey: DEEPSEEK_API_KEY,
  configuration: { baseURL: 'https://api.deepseek.com' },
  temperature: 0.3,
}

const llm = new ChatOpenAI({ ...baseConfig, streaming: false })
const streamingLlm = new ChatOpenAI({ ...baseConfig, streaming: true })

// 禁用 tiktoken 警告
llm.getNumTokens = async () => 0
streamingLlm.getNumTokens = async () => 0

// --- 业务工具 ---

const getPlatformProductsTool = tool(
  async () => {
    console.log(`[Tool] Fetching products...`)
    try {
      const response = await apiClient.get('/platform/products', { params: { page: 0, size: 100 } })
      if (response.data.success) {
        const products = response.data.data.content.map((p: any) => ({
          name: p.name,
          price: p.price,
          stock: p.stock,
          sales: p.salesCount,
          category: `${p.categoryL1} > ${p.categoryL2}`,
        }))
        return JSON.stringify(products)
      }
      return '获取数据失败。'
    } catch (error: any) {
      return `连接后端失败: ${error.message}`
    }
  },
  {
    name: 'get_products',
    description: '获取农资商城所有商品的实时库存、价格和销量数据',
    schema: z.object({}),
  },
)

const tools = [getPlatformProductsTool]
const modelWithTools = llm.bindTools(tools)

// --- 路由 ---

app.get('/api/analyze', (req, res) => {
  res.send('AI Agent Service is running.')
})

app.post('/api/analyze', async (req: Request, res: Response) => {
  const { question } = req.body
  console.log(`[Request] ${question}`)

  res.setHeader('Content-Type', 'text/event-stream')
  res.setHeader('Cache-Control', 'no-cache')
  res.setHeader('Connection', 'keep-alive')

  const sendChunk = (content: string) => {
    res.write(`data: ${JSON.stringify({ content })}\n\n`)
  }

  try {
    // 阶段 1: 意图识别与工具调用
    const initialMessages: any[] = [
      { role: 'system', content: '你是一个智慧三农专家助手。请调用工具获取实时数据。' },
      { role: 'user', content: question },
    ]

    const response = await modelWithTools.invoke(initialMessages)

    let allToolResults = ''
    if (response.tool_calls && response.tool_calls.length > 0) {
      console.log(`[Agent] Calling ${response.tool_calls.length} tools...`)
      for (const toolCall of response.tool_calls) {
        const toolInstance = tools.find((t) => t.name === toolCall.name)
        if (toolInstance) {
          const result = await (toolInstance as any).invoke(toolCall.args)
          allToolResults += `\n[工具结果]: ${result}\n`
        }
      }
    }

    // 阶段 2: 干净总结 (Safe Summary)
    const summaryMessages = [
      {
        role: 'system',
        content:
          '你是一个高效的数据分析师。请根据提供的业务数据给出精简的分析。要求：3-4个要点，每个点分段输出，总数200字内，严禁输出任何标签或思维链。',
      },
      {
        role: 'user',
        content: `问题：${question}\n\n数据：${allToolResults || '未获取到外部数据'}\n\n请总结：`,
      },
    ]

    const stream = await streamingLlm.stream(summaryMessages)
    for await (const chunk of stream) {
      if (chunk.content) {
        const cleanChunk = sanitizeContent(chunk.content as string)
        if (cleanChunk) sendChunk(cleanChunk)
      }
    }

    res.write('data: [DONE]\n\n')
    res.end()
  } catch (error: any) {
    console.error('[Error]', error.message)
    res.write(`data: ${JSON.stringify({ error: error.message })}\n\n`)
    res.end()
  }
})

const PORT = 3000
app.listen(PORT, () => {
  console.log(`🚀 AI Agent refactored and running on port ${PORT}`)
})
