import json
import httpx
from langchain_core.tools import tool
from engine.context import PLATFORM_BASE_URL, get_headers

@tool
async def get_abnormal_orders():
    """获取全站订单列表，可用于监控异常订单（如超过一定时间未发货的订单），需要管理员权限"""
    print("[Tool] Fetching all orders for anomaly detection...")
    async with httpx.AsyncClient(timeout=5.0) as client:
        try:
            response = await client.get(f"{PLATFORM_BASE_URL}/admin/orders", params={"page": 0, "size": 50}, headers=get_headers())
            data = response.json()
            if data.get("success"):
                orders = [
                    {
                        "id": o["id"],
                        "status": o["status"],
                        "totalAmount": o["totalAmount"],
                        "createdAt": o["createdAt"],
                        "items": [{"name": i["productName"], "quantity": i["quantity"]} for i in o.get("items", [])]
                    }
                    for o in data["data"]["content"]
                ]
                return json.dumps(orders, ensure_ascii=False)
            return f"获取订单数据失败: {data.get('message', '未知错误')}"
        except Exception as e:
            return f"连接后端失败: {str(e)}"
