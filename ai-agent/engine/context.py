from contextvars import ContextVar
import os

# Store the JWT token for the duration of a request
request_token: ContextVar[str] = ContextVar("request_token", default="")
PLATFORM_BASE_URL = os.getenv("PLATFORM_BASE_URL", "http://localhost:8080/api")

def get_headers():
    token = request_token.get()
    headers = {}
    if token:
        headers["Authorization"] = token
    return headers
