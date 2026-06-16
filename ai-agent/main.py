import os
import json
import re
from typing import AsyncGenerator, Any, cast
from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import StreamingResponse
from pydantic import BaseModel
from dotenv import load_dotenv
from langchain_openai import ChatOpenAI
from langchain_core.messages import SystemMessage, HumanMessage

# 导入自定义引擎
from engine import all_tools, get_help_text
from engine.context import request_token

# 1. 加载配置
load_dotenv()

app = FastAPI()

# 2. 跨域配置
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

DEEPSEEK_API_KEY = os.getenv("DEEPSEEK_API_KEY")

if not DEEPSEEK_API_KEY:
    print("❌ 错误: 请在 .env 文件中设置 DEEPSEEK_API_KEY")
    exit(1)

# --- 工具函数 ---

def sanitize_content(text: Any) -> str:
    """清理文本中的所有 XML/DSML 标签"""
    if not isinstance(text, str):
        return ""
    text = re.sub(r'<[^>]*?>[\s\S]*?<\/[^>]*?>', '', text)
    text = re.sub(r'[<｜|]DSML[｜|][^>]*>?', '', text)
    text = re.sub(r'function_calls>?', '', text)
    text = re.sub(r'invoke[^>]*>?', '', text)
    text = re.sub(r'<[^>]*?>', '', text)
    return text.strip()

# --- AI 配置 ---

base_config = {
    "model": "deepseek-chat",
    "openai_api_key": DEEPSEEK_API_KEY,
    "base_url": "https://api.deepseek.com",
    "temperature": 0.3,
}

llm = ChatOpenAI(**base_config, streaming=False) # type: ignore
streaming_llm = ChatOpenAI(**base_config, streaming=True) # type: ignore
llm_with_tools = llm.bind_tools(all_tools)

# --- 路由 ---

@app.get("/api/analyze")
async def health_check():
    return {"status": "AI Agent Service is running (Modular Engine)."}

class AnalyzeRequest(BaseModel):
    question: str

@app.post("/api/analyze")
async def analyze(request: Request, body: AnalyzeRequest):
    question = body.question.strip()
    print(f"[Request] {question}")

    # 设置 ContextVar 供工具使用
    auth_header = request.headers.get("Authorization", "")
    request_token.set(auth_header)

    async def event_generator() -> AsyncGenerator[str, None]:
        # 拦截帮助指令
        if question.lower() in ["/h", "/help", "帮助", "指令"]:
            yield f"data: {json.dumps({'content': get_help_text()}, ensure_ascii=False)}\n\n"
            yield "data: [DONE]\n\n"
            return

        try:
            # 阶段 1: 意图识别与工具调用
            messages = [
                SystemMessage(content="你是一个智慧三农专家助手。请调用工具获取实时数据。"),
                HumanMessage(content=question),
            ]

            response = await llm_with_tools.ainvoke(messages) # type: ignore

            all_tool_results = ""
            if hasattr(response, 'tool_calls') and response.tool_calls:
                print(f"[Agent] Calling {len(response.tool_calls)} tools...")
                for tool_call in response.tool_calls:
                    tool_name = tool_call["name"]
                    tool_args = tool_call["args"]

                    selected_tool = next((t for t in all_tools if t.name == tool_name), None)
                    if selected_tool:
                        result = await selected_tool.ainvoke(tool_args)
                        all_tool_results += f"\n[工具结果]: {result}\n"

            # 阶段 2: 干净总结 (Safe Summary)
            summary_messages = [
                SystemMessage(content="你是一个高效的数据分析师。请根据提供的业务数据给出精简的分析。要求：3-4个要点，每个点分段输出，总数200字内，严禁输出任何标签或思维链。"),
                HumanMessage(content=f"问题：${question}\n\n数据：${all_tool_results if all_tool_results else '未获取到外部数据'}\n\n请总结："),
            ]

            async for chunk in streaming_llm.astream(summary_messages): # type: ignore
                if chunk.content:
                    clean_chunk = sanitize_content(chunk.content)
                    if clean_chunk:
                        yield f"data: {json.dumps({'content': clean_chunk}, ensure_ascii=False)}\n\n"

            yield "data: [DONE]\n\n"
        except Exception as e:
            print(f"[Error] {str(e)}")
            yield f"data: {json.dumps({'error': str(e)}, ensure_ascii=False)}\n\n"

    return StreamingResponse(event_generator(), media_type="text_event-stream")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=3000)
