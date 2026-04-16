/**
 * AI 文本助手：专门处理来自 DeepSeek 的原始流数据
 */

/**
 * 究极清理：实时剔除 AI 响应中夹杂的 XML/DSML 协议标签
 * 即使标签被 chunk 截断也能通过累积文本进行清理
 * @param {string} rawText - 累积的原始文本
 * @returns {string} 清理后的纯净文本
 */
export const sanitizeAiStream = (rawText) => {
  if (!rawText) return ''

  return rawText
    .replace(/<[^>]*?>[\s\S]*?<\/[^>]*?>/g, '') // 1. 匹配并删除完整的闭合标签块 (如 <thought>...</thought>)
    .replace(/[｜|]DSML[｜|][^>]*>?/g, '')       // 2. 匹配并删除丢失了左尖括号的 DSML 碎片
    .replace(/<[^>]*?DSML[^>]*?>/g, '')         // 3. 匹配并删除包含 DSML 的起始标签
    .replace(/<[^>]*>[\s\S]*/g, '')            // 4. 匹配并删除未闭合的标签及其后的所有内容 (防止泄露)
    .replace(/<\/[^>]*>/g, '')                  // 5. 清理孤立的闭合标签尾部
    .trim()
}
