import json
import httpx
from langchain_core.tools import tool
from engine.context import PLATFORM_BASE_URL, get_headers

@tool
async def get_users_status():
    """获取平台用户列表及状态，可用于分析用户构成和异常状态（如冻结用户），需要管理员权限"""
    print("[Tool] Fetching users status...")
    async with httpx.AsyncClient(timeout=5.0) as client:
        try:
            response = await client.get(f"{PLATFORM_BASE_URL}/admin/users", headers=get_headers())
            data = response.json()
            if data.get("success"):
                users = [
                    {
                        "id": u["id"],
                        "username": u["username"],
                        "role": u["role"],
                        "status": u["status"],
                        "createdAt": u["createdAt"]
                    }
                    for u in data["data"]
                ]
                return json.dumps(users, ensure_ascii=False)
            return f"获取用户数据失败: {data.get('message', '未知错误')}"
        except Exception as e:
            return f"连接后端失败: {str(e)}"
