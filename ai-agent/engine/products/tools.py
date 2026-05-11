import json
import httpx
from langchain_core.tools import tool
from engine.context import PLATFORM_BASE_URL, get_headers

@tool
async def get_products():
    """获取农资商城所有商品的实时库存、价格和销量数据，可用于分析积压库存与促销建议"""
    print("[Tool] Fetching products...")
    async with httpx.AsyncClient(timeout=5.0) as client:
        try:
            response = await client.get(f"{PLATFORM_BASE_URL}/platform/products", params={"page": 0, "size": 100})
            data = response.json()
            if data.get("success"):
                products = [
                    {
                        "name": p["name"],
                        "price": p["price"],
                        "stock": p["stock"],
                        "sales": p["salesCount"],
                        "category": f"{p['categoryL1']} > {p['categoryL2']}"
                    }
                    for p in data["data"]["content"]
                ]
                return json.dumps(products, ensure_ascii=False)
            return "获取数据失败。"
        except Exception as e:
            return f"连接后端失败: {str(e)}"
