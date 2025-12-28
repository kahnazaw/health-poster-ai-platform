// Prisma 7.2.0 configuration file
// This file provides DATABASE_URL for Prisma Migrate and db push commands

const config = {
  datasource: {
    url: process.env.DATABASE_URL || '',
  },
}

export default config

