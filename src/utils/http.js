const authStorageKey = 'farmer-platform-auth'

const parseJsonSafely = async (response) => {
  const text = await response.text()
  if (!text) return null

  try {
    return JSON.parse(text)
  } catch {
    return null
  }
}

const getSessionToken = () => {
  const raw = localStorage.getItem(authStorageKey)
  if (!raw) return ''

  try {
    return JSON.parse(raw)?.token ?? ''
  } catch {
    return ''
  }
}

const requestJson = async (url, options = {}) => {
  const token = getSessionToken()
  const headers = {
    ...options.headers,
  }

  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  const response = await fetch(url, {
    ...options,
    headers,
  })

  const result = await parseJsonSafely(response)

  if (!response.ok || result?.success === false) {
    throw new Error(result?.message || '请求失败，请稍后重试')
  }

  return result?.data ?? null
}

export const getJson = async (url) => requestJson(url, { method: 'GET' })

export const postJson = async (url, payload) =>
  requestJson(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })

export const putJson = async (url, payload) =>
  requestJson(url, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })

export const patchJson = async (url, payload) =>
  requestJson(url, {
    method: 'PATCH',
    headers: payload === undefined ? {} : { 'Content-Type': 'application/json' },
    body: payload === undefined ? undefined : JSON.stringify(payload),
  })

export const deleteJson = async (url) =>
  requestJson(url, {
    method: 'DELETE',
  })
