/**
 * Activity Logging Utility
 * 
 * Centralized logging system for tracking user actions and system events
 */

import { prisma } from './prisma'

export interface LogEntry {
  action: string
  details: string
  userName: string
  userRole: string
}

/**
 * Log an activity to the database
 * 
 * @param entry - The log entry to record
 * @returns Promise that resolves when the log is saved
 */
export async function logActivity(entry: LogEntry): Promise<void> {
  try {
    await prisma.activityLog.create({
      data: {
        action: entry.action,
        details: entry.details,
        userName: entry.userName,
        userRole: entry.userRole,
      },
    })
  } catch (error) {
    // Don't throw errors - logging should never break the application
    // Only log to console in development
    if (process.env.NODE_ENV === 'development') {
      console.error('Failed to log activity:', error)
    }
  }
}

/**
 * Log a user login event
 */
export async function logUserLogin(userName: string, userRole: string, email: string): Promise<void> {
  await logActivity({
    action: 'User Login',
    details: `User logged in: ${email}`,
    userName,
    userRole,
  })
}

/**
 * Log a poster generation event
 */
export async function logPosterGenerated(
  userName: string,
  userRole: string,
  posterId: string,
  topic: string,
  aiGenerated: boolean = false
): Promise<void> {
  await logActivity({
    action: 'Poster Generated',
    details: `Poster created: ${topic} (ID: ${posterId})${aiGenerated ? ' [AI Generated]' : ''}`,
    userName,
    userRole,
  })
}

/**
 * Log a database error
 */
export async function logDatabaseError(
  userName: string,
  userRole: string,
  error: string,
  context?: string
): Promise<void> {
  await logActivity({
    action: 'Database Error',
    details: context ? `${context}: ${error}` : error,
    userName,
    userRole,
  })
}

/**
 * Log a poster generation error
 */
export async function logPosterGenerationError(
  userName: string,
  userRole: string,
  error: string,
  topic?: string
): Promise<void> {
  await logActivity({
    action: 'Poster Generation Error',
    details: topic ? `Failed to generate poster for topic: ${topic}. Error: ${error}` : `Error: ${error}`,
    userName,
    userRole,
  })
}

/**
 * Log a user action (generic)
 */
export async function logUserAction(
  userName: string,
  userRole: string,
  action: string,
  details: string
): Promise<void> {
  await logActivity({
    action,
    details,
    userName,
    userRole,
  })
}

