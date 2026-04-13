<script setup>
import { ref, nextTick } from 'vue'
import { Promotion, Service, User, Loading } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const question = ref('')
const loading = ref(false)
const chatList = ref([
  {
    role: 'ai',
    text: '您好，管理员！我是您的 AI 数据助手。我可以帮您分析库存、销量、或者寻找潜在的业务风险。请问有什么可以帮您的？'
  }
])

const chatScroll = ref(null)

const scrollToBottom = async () => {
  await nextTick()
  if (chatScroll.value) {
    chatScroll.value.scrollTop = chatScroll.value.scrollHeight
  }
}

const handleSend = async () => {
  if (!question.value.trim() || loading.value) return

  const userMsg = question.value
  chatList.value.push({ role: 'user', text: userMsg })
  question.value = ''
  loading.value = true
  scrollToBottom()

  // 预先放一个空的 AI 消息，准备接收流
  const aiMsgIndex = chatList.value.length
  chatList.value.push({ role: 'ai', text: '' })

  try {
    const response = await fetch('http://127.0.0.1:3000/api/analyze', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ question: userMsg })
    })

    if (!response.ok) throw new Error('网络响应错误')

    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = '' // 引入缓冲区

    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      
      // 最后一行可能是不完整的，保留在 buffer 中
      buffer = lines.pop() || ''

      for (const line of lines) {
        const trimmedLine = line.trim()
        if (!trimmedLine || !trimmedLine.startsWith('data: ')) continue
        
        const dataStr = trimmedLine.replace('data: ', '')
        if (dataStr === '[DONE]') continue
        
        try {
          const data = JSON.parse(dataStr)
          if (data.content) {
            chatList.value[aiMsgIndex].text += data.content
            scrollToBottom()
          }
          if (data.error) throw new Error(data.error)
        } catch (e) {
          // 只有在确定不是残缺行的情况下才记录错误
          console.warn('流式解析跳过一行:', trimmedLine)
        }
      }
    }
  } catch (error) {
    ElMessage.error('AI 服务暂时不可用')
    chatList.value[aiMsgIndex].text = '抱歉，我现在无法连接到分析引擎，请稍后再试。'
  } finally {
    loading.value = false
    scrollToBottom()
  }
}
</script>

<template>
  <div class="ai-assistant-container">
    <div class="page-header">
      <h1 class="page-title">AI 智能助理 (实时流模式)</h1>
      <p class="page-subtitle">数据分析结果即时生成，为您提供毫秒级的业务洞察。</p>
    </div>

    <el-card shadow="never" class="chat-card">
      <div class="chat-window" ref="chatScroll">
        <div 
          v-for="(msg, index) in chatList" 
          :key="index" 
          :class="['chat-item', msg.role === 'ai' ? 'ai-msg' : 'user-msg']"
        >
          <el-avatar :icon="msg.role === 'ai' ? Service : User" :size="40" class="avatar" />
          <div class="msg-bubble">
            <div class="msg-content">{{ msg.text }}</div>
          </div>
        </div>
        
        <div v-if="loading && !chatList[chatList.length-1].text" class="chat-item ai-msg">
          <el-avatar :icon="Service" :size="40" class="avatar" />
          <div class="msg-bubble">
            <div class="loading-box">
              <el-icon class="is-loading"><Loading /></el-icon>
              <span>AI 正在思考数据...</span>
            </div>
          </div>
        </div>
      </div>

      <div class="chat-input-area">
        <el-input
          v-model="question"
          placeholder="输入业务指令，如：目前的库存健康吗？"
          size="large"
          @keyup.enter="handleSend"
          :disabled="loading"
        >
          <template #append>
            <el-button :icon="Promotion" type="primary" @click="handleSend" :loading="loading">
              发送
            </el-button>
          </template>
        </el-input>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.ai-assistant-container {
  max-width: 1000px;
  margin: 0 auto;
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
}

.page-header {
  margin-bottom: 20px;
}

.chat-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: var(--el-bg-color-overlay);
  border: none;
  overflow: hidden;
}

:deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0;
}

.chat-window {
  flex: 1;
  padding: 30px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.chat-item {
  display: flex;
  gap: 12px;
  max-width: 85%;
}

.ai-msg {
  align-self: flex-start;
}

.user-msg {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.msg-bubble {
  padding: 14px 18px;
  border-radius: 16px;
  line-height: 1.6;
  font-size: 15px;
  white-space: pre-wrap;
}

.ai-msg .msg-bubble {
  background-color: var(--el-fill-color-darker);
  color: var(--el-text-color-primary);
  border-bottom-left-radius: 4px;
}

.user-msg .msg-bubble {
  background-color: var(--el-color-primary);
  color: white;
  border-bottom-right-radius: 4px;
}

.loading-box {
  display: flex;
  align-items: center;
  gap: 10px;
}

.chat-input-area {
  padding: 24px 30px;
  background-color: var(--el-fill-color-blank);
  border-top: 1px solid var(--el-border-color-lighter);
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px;
}
</style>
