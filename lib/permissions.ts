/**
 * Permission System
 * 
 * Centralized role-based access control (RBAC)
 * Defines permissions for each role in the system
 */

export type Role = 'SUPER_ADMIN' | 'ADMIN' | 'CONTENT_MANAGER' | 'REVIEWER' | 'USER'

export interface Permissions {
  // Content Management
  createContent: boolean
  editOwnContent: boolean
  editAllContent: boolean
  deleteOwnContent: boolean
  deleteAllContent: boolean
  
  // Templates
  createTemplate: boolean
  editTemplate: boolean
  deleteTemplate: boolean
  useTemplate: boolean
  
  // Review & Approval
  reviewContent: boolean
  approveContent: boolean
  rejectContent: boolean
  
  // User Management
  viewUsers: boolean
  createUsers: boolean
  editUsers: boolean
  deleteUsers: boolean
  changeUserRoles: boolean
  
  // Analytics
  viewAnalytics: boolean
  viewAllAnalytics: boolean
  
  // Organization
  manageOrganization: boolean
}

const rolePermissions: Record<Role, Permissions> = {
  SUPER_ADMIN: {
    createContent: true,
    editOwnContent: true,
    editAllContent: true,
    deleteOwnContent: true,
    deleteAllContent: true,
    createTemplate: true,
    editTemplate: true,
    deleteTemplate: true,
    useTemplate: true,
    reviewContent: true,
    approveContent: true,
    rejectContent: true,
    viewUsers: true,
    createUsers: true,
    editUsers: true,
    deleteUsers: true,
    changeUserRoles: true,
    viewAnalytics: true,
    viewAllAnalytics: true,
    manageOrganization: true,
  },
  ADMIN: {
    createContent: true,
    editOwnContent: true,
    editAllContent: true,
    deleteOwnContent: true,
    deleteAllContent: true,
    createTemplate: true,
    editTemplate: true,
    deleteTemplate: true,
    useTemplate: true,
    reviewContent: true,
    approveContent: true,
    rejectContent: true,
    viewUsers: true,
    createUsers: true,
    editUsers: true,
    deleteUsers: true,
    changeUserRoles: true,
    viewAnalytics: true,
    viewAllAnalytics: true,
    manageOrganization: false,
  },
  CONTENT_MANAGER: {
    createContent: true,
    editOwnContent: true,
    editAllContent: true,
    deleteOwnContent: true,
    deleteAllContent: false,
    createTemplate: true,
    editTemplate: true,
    deleteTemplate: false,
    useTemplate: true,
    reviewContent: true,
    approveContent: true,
    rejectContent: true,
    viewUsers: true,
    createUsers: false,
    editUsers: false,
    deleteUsers: false,
    changeUserRoles: false,
    viewAnalytics: true,
    viewAllAnalytics: false,
    manageOrganization: false,
  },
  REVIEWER: {
    createContent: true,
    editOwnContent: true,
    editAllContent: false,
    deleteOwnContent: true,
    deleteAllContent: false,
    createTemplate: false,
    editTemplate: false,
    deleteTemplate: false,
    useTemplate: true,
    reviewContent: true,
    approveContent: true,
    rejectContent: true,
    viewUsers: false,
    createUsers: false,
    editUsers: false,
    deleteUsers: false,
    changeUserRoles: false,
    viewAnalytics: false,
    viewAllAnalytics: false,
    manageOrganization: false,
  },
  USER: {
    createContent: true,
    editOwnContent: true,
    editAllContent: false,
    deleteOwnContent: true,
    deleteAllContent: false,
    createTemplate: false,
    editTemplate: false,
    deleteTemplate: false,
    useTemplate: true,
    reviewContent: false,
    approveContent: false,
    rejectContent: false,
    viewUsers: false,
    createUsers: false,
    editUsers: false,
    deleteUsers: false,
    changeUserRoles: false,
    viewAnalytics: false,
    viewAllAnalytics: false,
    manageOrganization: false,
  },
}

/**
 * Get permissions for a role
 */
export function getPermissions(role: string): Permissions {
  const normalizedRole = role.toUpperCase() as Role
  return rolePermissions[normalizedRole] || rolePermissions.USER
}

/**
 * Check if user has a specific permission
 */
export function hasPermission(role: string, permission: keyof Permissions): boolean {
  const permissions = getPermissions(role)
  return permissions[permission] || false
}

/**
 * Check if user can edit a specific poster
 */
export function canEditPoster(role: string, posterUserId: string, currentUserId: string): boolean {
  const permissions = getPermissions(role)
  if (permissions.editAllContent) return true
  if (permissions.editOwnContent && posterUserId === currentUserId) return true
  return false
}

/**
 * Check if user can delete a specific poster
 */
export function canDeletePoster(role: string, posterUserId: string, currentUserId: string): boolean {
  const permissions = getPermissions(role)
  if (permissions.deleteAllContent) return true
  if (permissions.deleteOwnContent && posterUserId === currentUserId) return true
  return false
}

/**
 * Check if user can review content
 */
export function canReview(role: string): boolean {
  return hasPermission(role, 'reviewContent')
}

/**
 * Check if user can approve content
 */
export function canApprove(role: string): boolean {
  return hasPermission(role, 'approveContent')
}

