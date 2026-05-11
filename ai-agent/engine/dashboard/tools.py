import json
import httpx
from langchain_core.tools import tool
from engine.context import PLATFORM_BASE_URL, get_headers

@tool
async def get_dashboard_stats():
    """获取全站经营统计数据，包括本月销售额、待处理订单数、今日访问量和已发布文章数"""
    print("[Tool] Fetching dashboard stats...")
    async with httpx.AsyncClient(timeout=5.0) as client:
        try:
            response = await client.get(f"{PLATFORM_BASE_URL}/platform/bootstrap")
            data = response.json()
            if data.get("success"):
                return json.dumps(data["data"]["dashboard"], ensure_ascii=False)
            return "获取统计数据失败。"
        except Exception as e:
            return f"连接后端失败: {str(e)}"
