import json
import httpx
from langchain_core.tools import tool
from engine.context import PLATFORM_BASE_URL, get_headers

@tool
async def get_latest_articles():
    """获取平台最新发布的农业资讯和文章列表，可用于了解平台最近更新的资讯"""
    print("[Tool] Fetching latest articles...")
    async with httpx.AsyncClient(timeout=5.0) as client:
        try:
            response = await client.get(f"{PLATFORM_BASE_URL}/platform/articles", params={"page": 0, "size": 10})
            data = response.json()
            if data.get("success"):
                articles = [
                    {
                        "id": a["id"],
                        "title": a["title"],
                        "category": a["category"],
                        "viewCount": a["viewCount"],
                        "publishedAt": a["publishedAt"]
                    }
                    for a in data["data"]["content"]
                ]
                return json.dumps(articles, ensure_ascii=False)
            return "获取文章数据失败。"
        except Exception as e:
            return f"连接后端失败: {str(e)}"
