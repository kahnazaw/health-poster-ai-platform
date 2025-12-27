import { NextRequest, NextResponse } from 'next/server'
import { getServerSession } from 'next-auth'
import { authOptions } from '@/lib/auth'
import { prisma } from '@/lib/prisma'
import { hasPermission } from '@/lib/permissions'

export async function GET(req: NextRequest) {
  try {
    const session = await getServerSession(authOptions)
    
    if (!session) {
      return NextResponse.json({ error: 'غير مصرح' }, { status: 401 })
    }

    const role = (session.user as any).role
    const userId = (session.user as any).id

    if (!hasPermission(role, 'viewAnalytics')) {
      return NextResponse.json({ error: 'غير مصرح' }, { status: 403 })
    }

    const canViewAll = hasPermission(role, 'viewAllAnalytics')

    // Get statistics
    const whereClause = canViewAll ? {} : { userId }

    const [
      totalPosters,
      approvedPosters,
      draftPosters,
      underReviewPosters,
      rejectedPosters,
      totalDownloads,
      totalPrints,
      aiGeneratedCount,
      totalUsers,
      totalTemplates,
    ] = await Promise.all([
      prisma.poster.count({ where: whereClause }),
      prisma.poster.count({ where: { ...whereClause, status: 'APPROVED' } }),
      prisma.poster.count({ where: { ...whereClause, status: 'DRAFT' } }),
      prisma.poster.count({ where: { ...whereClause, status: 'UNDER_REVIEW' } }),
      prisma.poster.count({ where: { ...whereClause, status: 'REJECTED' } }),
      prisma.poster.aggregate({
        where: whereClause,
        _sum: { downloadCount: true },
      }),
      prisma.poster.aggregate({
        where: whereClause,
        _sum: { printCount: true },
      }),
      prisma.poster.count({ where: { ...whereClause, aiGenerated: true } }),
      canViewAll ? prisma.user.count() : Promise.resolve(0),
      canViewAll ? prisma.template.count({ where: { isActive: true } }) : Promise.resolve(0),
    ])

    // Get recent activity (last 30 days)
    const thirtyDaysAgo = new Date()
    thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30)

    const recentPosters = await prisma.poster.count({
      where: {
        ...whereClause,
        createdAt: { gte: thirtyDaysAgo },
      },
    })

    return NextResponse.json({
      overview: {
        totalPosters,
        approvedPosters,
        draftPosters,
        underReviewPosters,
        rejectedPosters,
        totalDownloads: totalDownloads._sum.downloadCount || 0,
        totalPrints: totalPrints._sum.printCount || 0,
        aiGeneratedCount,
        totalUsers,
        totalTemplates,
        recentPosters,
      },
      statusDistribution: {
        approved: approvedPosters,
        draft: draftPosters,
        underReview: underReviewPosters,
        rejected: rejectedPosters,
      },
    })
  } catch (error) {
    console.error('Error fetching analytics')
    return NextResponse.json({ error: 'خطأ في الخادم' }, { status: 500 })
  }
}

