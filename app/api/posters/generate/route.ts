import { NextRequest, NextResponse } from 'next/server'
import { getServerSession } from 'next-auth'
import { authOptions } from '@/lib/auth'
import { prisma } from '@/lib/prisma'
import { z } from 'zod'
import { generateHealthPosterContent } from '@/lib/ai'

const generatePosterSchema = z.object({
  topic: z.string().min(1, 'الموضوع مطلوب'),
  useAI: z.boolean().optional().default(false),
  targetAudience: z.string().optional(),
  tone: z.enum(['formal', 'friendly']).optional().default('formal'),
  length: z.enum(['short', 'medium', 'long']).optional().default('medium'),
})

// محاكاة توليد النص (يمكن استبدالها بـ OpenAI API)
function generateHealthContent(topic: string) {
  const contentTemplates: Record<string, { title: string; points: string[]; closing: string }> = {
    'نظافة الأسنان': {
      title: 'نظافة الأسنان: أساس الصحة الفموية',
      points: [
        'اغسل أسنانك مرتين يومياً على الأقل باستخدام معجون أسنان يحتوي على الفلورايد',
        'استخدم خيط الأسنان يومياً لإزالة بقايا الطعام من بين الأسنان',
        'استبدل فرشاة الأسنان كل 3-4 أشهر أو عند تلف شعيراتها',
        'تجنب الإكثار من السكريات والمشروبات الغازية التي تضر بالأسنان',
        'قم بزيارة طبيب الأسنان بانتظام للفحص الدوري والتنظيف',
      ],
      closing: 'تذكر: العناية الجيدة بأسنانك تحافظ على صحتها وتحميك من المشاكل المستقبلية',
    },
    'التغذية الصحية': {
      title: 'التغذية الصحية: مفتاح العافية',
      points: [
        'تناول وجبات متنوعة تحتوي على جميع المجموعات الغذائية (بروتينات، كربوهيدرات، دهون صحية)',
        'أكثر من تناول الخضروات والفواكه الطازجة الغنية بالفيتامينات والمعادن',
        'اشرب كميات كافية من الماء يومياً (8-10 أكواب)',
        'قلل من تناول الأطعمة المصنعة والوجبات السريعة الغنية بالدهون المشبعة',
        'احرص على تناول وجبة الإفطار يومياً لتمد جسمك بالطاقة',
      ],
      closing: 'نصيحة: التغذية المتوازنة هي أساس الصحة الجيدة والعافية المستمرة',
    },
    'الرياضة': {
      title: 'الرياضة: نشاط يومي للصحة',
      points: [
        'مارس النشاط البدني لمدة 30 دقيقة على الأقل يومياً',
        'اختر نوع الرياضة المناسب لعمرك وحالتك الصحية',
        'ابدأ بتمارين خفيفة ثم زد الشدة تدريجياً',
        'مارس تمارين القوة مرتين أسبوعياً لبناء العضلات',
        'لا تنسَ الإحماء قبل التمرين والتمدد بعده',
      ],
      closing: 'تذكر: الحركة هي الحياة، والنشاط البدني يحافظ على صحتك البدنية والعقلية',
    },
    'النوم': {
      title: 'النوم الكافي: راحة للجسم والعقل',
      points: [
        'احرص على النوم 7-9 ساعات يومياً للبالغين',
        'حافظ على جدول نوم منتظم حتى في عطلة نهاية الأسبوع',
        'تجنب استخدام الأجهزة الإلكترونية قبل النوم بساعة على الأقل',
        'اجعل غرفة النوم مظلمة وهادئة وباردة',
        'تجنب تناول الكافيين والوجبات الثقيلة قبل النوم',
      ],
      closing: 'صحتك تاج على رأسك، والنوم الكافي هو أساس الصحة الجيدة',
    },
  }

  const defaultContent = {
    title: `التوعية الصحية: ${topic}`,
    points: [
      'اتباع نظام غذائي متوازن يحتوي على جميع العناصر الغذائية الضرورية',
      'ممارسة النشاط البدني بانتظام لتعزيز الصحة العامة',
      'الحصول على قسط كافٍ من النوم والراحة',
      'الاهتمام بالنظافة الشخصية والبيئية',
      'الفحص الدوري والوقاية من الأمراض',
    ],
    closing: 'نصيحة: استشر طبيبك المختص للحصول على المشورة الصحية المناسبة',
  }

  const template = contentTemplates[topic]
  if (template) {
    // اختيار 3-5 نقاط عشوائياً من القالب
    const selectedPoints = template.points
      .sort(() => Math.random() - 0.5)
      .slice(0, Math.floor(Math.random() * 3) + 3)
    return {
      title: template.title,
      points: selectedPoints,
      closing: template.closing,
    }
  }

  // للمواضيع غير المعرفة، نستخدم المحتوى الافتراضي
  const selectedPoints = defaultContent.points
    .sort(() => Math.random() - 0.5)
    .slice(0, Math.floor(Math.random() * 3) + 3)
  
  return {
    title: defaultContent.title,
    points: selectedPoints,
    closing: defaultContent.closing,
  }
}

export async function POST(req: NextRequest) {
  try {
    const session = await getServerSession(authOptions)
    
    if (!session) {
      return NextResponse.json({ error: 'غير مصرح' }, { status: 401 })
    }

    const body = await req.json()
    const { topic, useAI, targetAudience, tone, length } = generatePosterSchema.parse(body)

    // Try AI generation first if requested and available
    let content: { title: string; points: string[]; closing: string }
    
    if (useAI) {
      const aiContent = await generateHealthPosterContent({
        topic,
        targetAudience,
        tone,
        length,
      })

      if (aiContent) {
        // Use AI-generated content
        content = {
          title: aiContent.title,
          points: aiContent.bulletPoints,
          closing: aiContent.closing,
        }
      } else {
        // Fallback to template-based generation
        content = generateHealthContent(topic)
      }
    } else {
      // Use template-based generation
      content = generateHealthContent(topic)
    }

    // حفظ البوستر في قاعدة البيانات
    const poster = await prisma.poster.create({
      data: {
        title: content.title,
        topic: topic,
        content: JSON.stringify(content),
        userId: (session.user as any).id,
      },
    })

    return NextResponse.json({
      id: poster.id,
      content: content,
    })
  } catch (error) {
    if (error instanceof z.ZodError) {
      return NextResponse.json({ error: error.errors[0].message }, { status: 400 })
    }
    console.error('Error generating poster')
    return NextResponse.json({ error: 'خطأ في الخادم' }, { status: 500 })
  }
}

