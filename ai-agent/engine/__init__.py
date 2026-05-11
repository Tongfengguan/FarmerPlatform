from typing import List
from langchain_core.tools import BaseTool
from .dashboard.tools import get_dashboard_stats
from .products.tools import get_products
from .articles.tools import get_latest_articles
from .orders.tools import get_abnormal_orders
from .users.tools import get_users_status

all_tools: List[BaseTool] = [
    get_dashboard_stats,
    get_products,
    get_latest_articles,
    get_abnormal_orders,
    get_users_status
]

def get_help_text():
    help_text = "目前我掌握以下技能：\n\n"
    for t in all_tools:
        # tool objects have a description attribute
        help_text += f"- **{t.name}**: {t.description}\n"
    help_text += "\n您可以直接向我提问，或者使用 /help 获取此列表。我会自动选择合适的技能为您服务。"
    return help_text
