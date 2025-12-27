'use client'

import { motion } from 'framer-motion'
import { LucideIcon } from 'lucide-react'
import { forwardRef } from 'react'

interface ButtonProps {
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost' | 'danger'
  size?: 'sm' | 'md' | 'lg'
  icon?: LucideIcon
  iconPosition?: 'left' | 'right'
  loading?: boolean
  disabled?: boolean
  className?: string
  type?: 'button' | 'submit' | 'reset'
  onClick?: () => void
  children?: React.ReactNode
}

const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  ({ 
    children, 
    variant = 'primary', 
    size = 'md', 
    icon: Icon,
    iconPosition = 'left',
    loading = false,
    disabled,
    className = '',
    type = 'button',
    onClick,
    ...props 
  }, ref) => {
    const baseStyles = 'inline-flex items-center justify-center font-semibold rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed'
    
    const variants = {
      primary: 'bg-gradient-to-r from-primary-500 to-sky-500 hover:from-primary-600 hover:to-sky-600 text-white shadow-lg hover:shadow-xl',
      secondary: 'bg-gray-100 hover:bg-gray-200 text-gray-900 shadow-md hover:shadow-lg',
      outline: 'border-2 border-primary-500 text-primary-600 hover:bg-primary-50',
      ghost: 'text-gray-700 hover:bg-gray-100',
      danger: 'bg-red-500 hover:bg-red-600 text-white shadow-lg hover:shadow-xl',
    }
    
    const sizes = {
      sm: 'px-3 py-1.5 text-sm',
      md: 'px-4 py-2.5 text-base',
      lg: 'px-6 py-3 text-lg',
    }
    
    return (
      <motion.button
        ref={ref}
        type={type}
        whileHover={{ scale: disabled || loading ? 1 : 1.02 }}
        whileTap={{ scale: disabled || loading ? 1 : 0.98 }}
        className={`${baseStyles} ${variants[variant]} ${sizes[size]} ${className}`}
        disabled={disabled || loading}
        onClick={onClick}
        {...props}
      >
        {loading ? (
          <motion.div
            animate={{ rotate: 360 }}
            transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
            className="w-4 h-4 border-2 border-current border-t-transparent rounded-full ml-2"
          />
        ) : Icon && iconPosition === 'left' ? (
          <Icon className="w-4 h-4 ml-2" />
        ) : null}
        {children}
        {Icon && iconPosition === 'right' && !loading && (
          <Icon className="w-4 h-4 mr-2" />
        )}
      </motion.button>
    )
  }
)

Button.displayName = 'Button'

export default Button
