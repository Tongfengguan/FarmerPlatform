const authStorageKey = 'farmer-platform-auth'

/**
 * 安全解析 JSON 响应
 * @param {Response} response 
 */
const parseJsonSafely = async (response) => {
  const text = await response.text()
  if (!text) return null
  try {
    return JSON.parse(text)
  } catch {
    return null
  }
}

/**
 * 获取本地存储的 Token
 */
const getSessionToken = () => {
  const raw = localStorage.getItem(authStorageKey)
  if (!raw) return ''
  try {
    return JSON.parse(raw)?.token ?? ''
  } catch {
    return ''
  }
}

/**
 * 处理未授权请求（401）
 */
const handleUnauthorized = () => {
  localStorage.removeItem(authStorageKey)
  // 如果当前不是登录页，则重定向
  if (!window.location.pathname.includes('/auth')) {
    window.location.href = '/#/auth'
  }
}

/**
 * 通用 JSON 请求封装
 * @param {string} url 
 * @param {RequestInit} options 
 */
const requestJson = async (url, options = {}) => {
  const token = getSessionToken()
  const headers = {
    ...options.headers,
  }

  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  try {
    const response = await fetch(url, {
      ...options,
      headers,
    })

    // 处理 401 未授权
    if (response.status === 401) {
      handleUnauthorized()
      throw new Error('登录已过期，请重新登录')
    }

    const result = await parseJsonSafely(response)

    if (!response.ok || result?.success === false) {
      throw new Error(result?.message || '请求失败，请稍后重试')
    }

    return result?.data ?? null
  } catch (error) {
    // 向上抛出错误，供组件处理（显示 ElMessage 等）
    throw error
  }
}

/**
 * GET 请求
 */
export const getJson = async (url) => requestJson(url, { method: 'GET' })

/**
 * POST 请求
 */
export const postJson = async (url, payload) =>
  requestJson(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })

/**
 * PUT 请求
 */
export const putJson = async (url, payload) =>
  requestJson(url, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })

/**
 * PATCH 请求
 */
export const patchJson = async (url, payload) =>
  requestJson(url, {
    method: 'PATCH',
    headers: payload === undefined ? {} : { 'Content-Type': 'application/json' },
    body: payload === undefined ? undefined : JSON.stringify(payload),
  })

/**
 * DELETE 请求
 */
export const deleteJson = async (url) =>
  requestJson(url, {
    method: 'DELETE',
  })
