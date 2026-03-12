-- CreateEnum
CREATE TYPE "Vertical" AS ENUM ('FITNESS', 'PETS', 'TRAVEL', 'TRANSPORT', 'HEALTH', 'BEAUTY', 'EDUCATION', 'FOOD', 'RETAIL', 'REAL_ESTATE', 'HOSPITALITY');

-- CreateTable
CREATE TABLE "public_profiles" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "business_id" UUID NOT NULL,
    "branch_id" UUID,
    "vertical" "Vertical" NOT NULL,
    "name" VARCHAR(200) NOT NULL,
    "slug" VARCHAR(100) NOT NULL,
    "description" VARCHAR(2000),
    "short_desc" VARCHAR(300),
    "logo_url" VARCHAR,
    "cover_url" VARCHAR,
    "city" VARCHAR(100),
    "region" VARCHAR(100),
    "country" VARCHAR(3) NOT NULL DEFAULT 'BO',
    "address" VARCHAR,
    "latitude" DOUBLE PRECISION,
    "longitude" DOUBLE PRECISION,
    "phone" VARCHAR(20),
    "whatsapp" VARCHAR(20),
    "email" VARCHAR(100),
    "website" VARCHAR,
    "avg_rating" DECIMAL(3,2) NOT NULL DEFAULT 0,
    "review_count" INTEGER NOT NULL DEFAULT 0,
    "featured" BOOLEAN NOT NULL DEFAULT false,
    "verified" BOOLEAN NOT NULL DEFAULT false,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "public_profiles_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "profile_tags" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "profile_id" UUID NOT NULL,
    "tag" VARCHAR(50) NOT NULL,

    CONSTRAINT "profile_tags_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "profile_amenities" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "profile_id" UUID NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "icon" VARCHAR(50),

    CONSTRAINT "profile_amenities_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "profile_images" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "profile_id" UUID NOT NULL,
    "url" VARCHAR NOT NULL,
    "caption" VARCHAR(200),
    "order" INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT "profile_images_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "profile_hours" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "profile_id" UUID NOT NULL,
    "day_of_week" INTEGER NOT NULL,
    "open_time" VARCHAR(5) NOT NULL,
    "close_time" VARCHAR(5) NOT NULL,

    CONSTRAINT "profile_hours_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE INDEX "public_profiles_vertical_city_active_idx" ON "public_profiles"("vertical", "city", "active");

-- CreateIndex
CREATE INDEX "public_profiles_business_id_idx" ON "public_profiles"("business_id");

-- CreateIndex
CREATE UNIQUE INDEX "public_profiles_vertical_slug_key" ON "public_profiles"("vertical", "slug");

-- CreateIndex
CREATE UNIQUE INDEX "profile_tags_profile_id_tag_key" ON "profile_tags"("profile_id", "tag");

-- CreateIndex
CREATE UNIQUE INDEX "profile_hours_profile_id_day_of_week_key" ON "profile_hours"("profile_id", "day_of_week");

-- AddForeignKey
ALTER TABLE "profile_tags" ADD CONSTRAINT "profile_tags_profile_id_fkey" FOREIGN KEY ("profile_id") REFERENCES "public_profiles"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "profile_amenities" ADD CONSTRAINT "profile_amenities_profile_id_fkey" FOREIGN KEY ("profile_id") REFERENCES "public_profiles"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "profile_images" ADD CONSTRAINT "profile_images_profile_id_fkey" FOREIGN KEY ("profile_id") REFERENCES "public_profiles"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "profile_hours" ADD CONSTRAINT "profile_hours_profile_id_fkey" FOREIGN KEY ("profile_id") REFERENCES "public_profiles"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
