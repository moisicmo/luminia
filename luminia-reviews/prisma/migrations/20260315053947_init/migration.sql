-- CreateEnum
CREATE TYPE "TargetType" AS ENUM ('BUSINESS', 'BRANCH', 'PRODUCT', 'SERVICE', 'TRIP', 'PROVIDER');

-- CreateEnum
CREATE TYPE "RefType" AS ENUM ('ORDER', 'BOOKING', 'TICKET', 'SUBSCRIPTION');

-- CreateTable
CREATE TABLE "reviews" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "target_type" "TargetType" NOT NULL,
    "target_id" UUID NOT NULL,
    "author_id" UUID NOT NULL,
    "author_name" VARCHAR(200) NOT NULL,
    "rating" INTEGER NOT NULL,
    "title" VARCHAR(200),
    "comment" VARCHAR(2000),
    "verified" BOOLEAN NOT NULL DEFAULT false,
    "visible" BOOLEAN NOT NULL DEFAULT true,
    "reference_type" "RefType",
    "reference_id" UUID,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "reviews_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "review_responses" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "review_id" UUID NOT NULL,
    "author_id" UUID NOT NULL,
    "comment" VARCHAR(2000) NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "review_responses_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "rating_summaries" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "target_type" "TargetType" NOT NULL,
    "target_id" UUID NOT NULL,
    "total_count" INTEGER NOT NULL DEFAULT 0,
    "average_rating" DECIMAL(3,2) NOT NULL DEFAULT 0,
    "rating_sum" INTEGER NOT NULL DEFAULT 0,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "rating_summaries_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE INDEX "reviews_target_type_target_id_idx" ON "reviews"("target_type", "target_id");

-- CreateIndex
CREATE UNIQUE INDEX "reviews_target_type_target_id_author_id_key" ON "reviews"("target_type", "target_id", "author_id");

-- CreateIndex
CREATE UNIQUE INDEX "review_responses_review_id_key" ON "review_responses"("review_id");

-- CreateIndex
CREATE UNIQUE INDEX "rating_summaries_target_type_target_id_key" ON "rating_summaries"("target_type", "target_id");

-- AddForeignKey
ALTER TABLE "review_responses" ADD CONSTRAINT "review_responses_review_id_fkey" FOREIGN KEY ("review_id") REFERENCES "reviews"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
