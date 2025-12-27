/**
 * AI Service Layer
 * 
 * Provider-agnostic AI content generation service.
 * Currently supports OpenAI, but can be extended to support other providers.
 */

export interface AIGenerationOptions {
  topic: string
  targetAudience?: string
  tone?: 'formal' | 'friendly'
  length?: 'short' | 'medium' | 'long'
}

export interface AIGeneratedContent {
  title: string
  mainMessage: string
  bulletPoints: string[]
  closing: string
}

/**
 * Generate health poster content using AI
 * 
 * @param options - Generation options
 * @returns Generated content or null if AI is not configured
 */
export async function generateHealthPosterContent(
  options: AIGenerationOptions
): Promise<AIGeneratedContent | null> {
  const apiKey = process.env.OPENAI_API_KEY

  // If no API key is configured, return null (fallback to templates)
  if (!apiKey) {
    return null
  }

  try {
    const prompt = buildPrompt(options)
    
    const response = await fetch('https://api.openai.com/v1/chat/completions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${apiKey}`,
      },
      body: JSON.stringify({
        model: 'gpt-3.5-turbo',
        messages: [
          {
            role: 'system',
            content: 'أنت مساعد متخصص في كتابة محتوى توعية صحية باللغة العربية. اكتب محتوى علمي، رسمي، ومفيد للتوعية الصحية العامة. لا تقدم تشخيصات أو علاجات طبية.',
          },
          {
            role: 'user',
            content: prompt,
          },
        ],
        temperature: 0.7,
        max_tokens: 800,
      }),
    })

    if (!response.ok) {
      console.error('OpenAI API error:', response.status)
      return null
    }

    const data = await response.json()
    const content = data.choices[0]?.message?.content

    if (!content) {
      return null
    }

    return parseAIResponse(content)
  } catch (error) {
    // Don't log sensitive error details
    console.error('AI generation error')
    return null
  }
}

/**
 * Build prompt for AI generation
 */
function buildPrompt(options: AIGenerationOptions): string {
  const { topic, targetAudience = 'عامة', tone = 'formal', length = 'medium' } = options

  const toneText = tone === 'formal' ? 'رسمي ومؤسسي' : 'ودود وواضح'
  const lengthText = 
    length === 'short' ? 'مختصر (3 نقاط)' : 
    length === 'long' ? 'مفصل (5 نقاط)' : 
    'متوسط (4 نقاط)'

  return `أنشئ محتوى توعية صحية باللغة العربية حول موضوع: "${topic}"

المتطلبات:
- الجمهور المستهدف: ${targetAudience}
- الأسلوب: ${toneText}
- الطول: ${lengthText}

المطلوب:
1. عنوان جذاب ومهني (سطر واحد)
2. رسالة رئيسية مختصرة (2-3 جمل)
3. ${length === 'short' ? '3' : length === 'long' ? '5' : '4'} نقاط رئيسية واضحة ومفيدة
4. جملة ختامية تحفيزية

المهم: المحتوى للتوعية فقط، بدون تشخيص أو علاج.`
}

/**
 * Parse AI response into structured content
 */
function parseAIResponse(response: string): AIGeneratedContent {
  // Try to extract structured content from AI response
  // This is a simple parser - can be improved based on AI response format
  
  const lines = response.split('\n').filter(line => line.trim())
  
  let title = ''
  let mainMessage = ''
  const bulletPoints: string[] = []
  let closing = ''

  let currentSection = 'title'
  
  for (const line of lines) {
    const trimmed = line.trim()
    
    // Skip empty lines and section headers
    if (!trimmed || trimmed.startsWith('##') || trimmed.startsWith('**')) {
      continue
    }

    // Detect title (usually first non-empty line or marked as title)
    if (currentSection === 'title' && trimmed && !trimmed.match(/^[\d\-•]/)) {
      title = trimmed.replace(/^[:\-]\s*/, '').replace(/^عنوان[:\-]\s*/i, '')
      currentSection = 'message'
      continue
    }

    // Detect bullet points
    if (trimmed.match(/^[\d\-•*]\s+/) || trimmed.match(/^\d+[\.\)]\s+/)) {
      const point = trimmed.replace(/^[\d\-•*\.\)]\s+/, '').trim()
      if (point) {
        bulletPoints.push(point)
      }
      continue
    }

    // Detect closing (usually at the end)
    if (trimmed.toLowerCase().includes('تذكر') || 
        trimmed.toLowerCase().includes('نصيحة') ||
        trimmed.toLowerCase().includes('ختام')) {
      closing = trimmed
      continue
    }

    // Main message (if not a bullet point and not title)
    if (currentSection === 'message' && !bulletPoints.length && trimmed.length > 20) {
      mainMessage = trimmed
      currentSection = 'points'
    }
  }

  // Fallback: if parsing failed, use simple split
  if (!title && lines.length > 0) {
    title = lines[0].replace(/^[:\-]\s*/, '').trim()
  }

  if (!bulletPoints.length && lines.length > 1) {
    // Try to extract points from remaining lines
    for (let i = 1; i < Math.min(lines.length, 6); i++) {
      const point = lines[i].replace(/^[\d\-•*\.\)]\s+/, '').trim()
      if (point && point.length > 10) {
        bulletPoints.push(point)
      }
    }
  }

  if (!closing && lines.length > 0) {
    closing = lines[lines.length - 1].trim()
  }

  // Ensure we have at least basic content
  if (!title) {
    title = 'التوعية الصحية'
  }

  if (bulletPoints.length === 0) {
    bulletPoints.push('اتباع نظام غذائي متوازن')
    bulletPoints.push('ممارسة النشاط البدني بانتظام')
    bulletPoints.push('الحصول على قسط كافٍ من النوم')
  }

  if (!closing) {
    closing = 'تذكر: صحتك تاج على رأسك، اعتن بها'
  }

  if (!mainMessage) {
    mainMessage = 'نصائح مهمة للعناية بصحتك'
  }

  return {
    title,
    mainMessage,
    bulletPoints: bulletPoints.slice(0, 5), // Max 5 points
    closing,
  }
}

