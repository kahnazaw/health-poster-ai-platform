/**
 * Production Readiness Check
 * 
 * Validates that the project is ready for Railway deployment
 * 
 * Usage: node scripts/check-production-ready.mjs
 */

import { readFileSync } from 'fs'
import { fileURLToPath } from 'url'
import { dirname, join } from 'path'

const __filename = fileURLToPath(import.meta.url)
const __dirname = dirname(__filename)
const rootDir = join(__dirname, '..')

let errors = []
let warnings = []
let success = []

console.log('🔍 Checking production readiness...\n')

// Check package.json
try {
  const packageJson = JSON.parse(readFileSync(join(rootDir, 'package.json'), 'utf-8'))
  
  // Check build script
  if (packageJson.scripts.build && packageJson.scripts.build.includes('npx next build')) {
    success.push('✅ Build script uses npx next build')
  } else {
    errors.push('❌ Build script should use "npx next build"')
  }
  
  // Check postinstall
  if (packageJson.scripts.postinstall && packageJson.scripts.postinstall.includes('SKIP_ENV_VALIDATION')) {
    success.push('✅ postinstall script has SKIP_ENV_VALIDATION')
  } else {
    warnings.push('⚠️  postinstall script should include SKIP_ENV_VALIDATION')
  }
  
  // Check db:generate
  if (packageJson.scripts['db:generate'] && packageJson.scripts['db:generate'].includes('SKIP_ENV_VALIDATION')) {
    success.push('✅ db:generate script has SKIP_ENV_VALIDATION')
  } else {
    warnings.push('⚠️  db:generate script should include SKIP_ENV_VALIDATION')
  }
  
  // Check db:migrate:deploy
  if (packageJson.scripts['db:migrate:deploy']) {
    success.push('✅ db:migrate:deploy script exists')
  } else {
    errors.push('❌ db:migrate:deploy script is missing')
  }
  
  // Check dependencies
  if (packageJson.dependencies.next) {
    success.push('✅ next is in dependencies (not devDependencies)')
  } else {
    errors.push('❌ next must be in dependencies')
  }
  
  if (packageJson.dependencies.react && packageJson.dependencies['react-dom']) {
    success.push('✅ react and react-dom are in dependencies')
  } else {
    errors.push('❌ react and react-dom must be in dependencies')
  }
  
} catch (error) {
  errors.push(`❌ Failed to read package.json: ${error.message}`)
}

// Check nixpacks.toml
try {
  const nixpacksContent = readFileSync(join(rootDir, 'nixpacks.toml'), 'utf-8')
  
  if (nixpacksContent.includes('SKIP_ENV_VALIDATION')) {
    success.push('✅ nixpacks.toml includes SKIP_ENV_VALIDATION')
  } else {
    warnings.push('⚠️  nixpacks.toml should include SKIP_ENV_VALIDATION in build phase')
  }
  
  if (nixpacksContent.includes('prisma migrate deploy')) {
    success.push('✅ nixpacks.toml includes migration in start command')
  } else {
    warnings.push('⚠️  nixpacks.toml should run migrations on start')
  }
  
} catch (error) {
  warnings.push(`⚠️  nixpacks.toml not found or unreadable: ${error.message}`)
}

// Check prisma schema
try {
  const schemaContent = readFileSync(join(rootDir, 'prisma', 'schema.prisma'), 'utf-8')
  
  if (schemaContent.includes('provider = "postgresql"')) {
    success.push('✅ Prisma schema uses PostgreSQL')
  } else {
    errors.push('❌ Prisma schema must use PostgreSQL provider')
  }
  
  if (schemaContent.includes('directUrl')) {
    success.push('✅ Prisma schema includes directUrl support')
  } else {
    warnings.push('⚠️  Consider adding directUrl for connection pooling support')
  }
  
} catch (error) {
  errors.push(`❌ Failed to read prisma/schema.prisma: ${error.message}`)
}

// Check lib/prisma.ts
try {
  const prismaContent = readFileSync(join(rootDir, 'lib', 'prisma.ts'), 'utf-8')
  
  if (prismaContent.includes('SKIP_ENV_VALIDATION')) {
    success.push('✅ lib/prisma.ts handles SKIP_ENV_VALIDATION')
  } else {
    warnings.push('⚠️  lib/prisma.ts should handle SKIP_ENV_VALIDATION')
  }
  
  if (prismaContent.includes('directUrl')) {
    success.push('✅ lib/prisma.ts supports directUrl')
  } else {
    warnings.push('⚠️  lib/prisma.ts should support directUrl for connection pooling')
  }
  
} catch (error) {
  warnings.push(`⚠️  Failed to read lib/prisma.ts: ${error.message}`)
}

// Check RAILWAY_SETUP.md
try {
  const setupContent = readFileSync(join(rootDir, 'RAILWAY_SETUP.md'), 'utf-8')
  if (setupContent.length > 1000) {
    success.push('✅ RAILWAY_SETUP.md exists and has content')
  }
} catch (error) {
  warnings.push('⚠️  RAILWAY_SETUP.md not found - consider creating deployment documentation')
}

// Print results
console.log('📊 Results:\n')

if (success.length > 0) {
  console.log('✅ Success:')
  success.forEach(msg => console.log(`   ${msg}`))
  console.log('')
}

if (warnings.length > 0) {
  console.log('⚠️  Warnings:')
  warnings.forEach(msg => console.log(`   ${msg}`))
  console.log('')
}

if (errors.length > 0) {
  console.log('❌ Errors:')
  errors.forEach(msg => console.log(`   ${msg}`))
  console.log('')
}

// Summary
const total = success.length + warnings.length + errors.length
console.log(`\n📈 Summary: ${success.length}/${total} checks passed`)

if (errors.length > 0) {
  console.log('\n❌ Project is NOT production ready. Please fix the errors above.')
  process.exit(1)
} else if (warnings.length > 0) {
  console.log('\n⚠️  Project is mostly ready, but consider addressing the warnings.')
  process.exit(0)
} else {
  console.log('\n✅ Project is production ready!')
  process.exit(0)
}

