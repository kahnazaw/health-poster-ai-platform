// Prisma 7.2.0 configuration for migrations
// This file provides DATABASE_URL and DIRECT_URL for Prisma Migrate
// The schema.prisma file no longer supports url/directUrl properties

export default {
  datasource: {
    url: process.env.DATABASE_URL,
    directUrl: process.env.DIRECT_URL,
  },
}

