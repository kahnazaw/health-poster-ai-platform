// Prisma 7.2.0 configuration for migrations
// This file is used by Prisma Migrate to connect to the database
export default {
  datasource: {
    url: process.env.DATABASE_URL,
    directUrl: process.env.DIRECT_URL,
  },
}

