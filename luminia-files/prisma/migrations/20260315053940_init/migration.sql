-- CreateEnum
CREATE TYPE "FileCategory" AS ENUM ('IMAGE', 'DOCUMENT', 'SPREADSHEET', 'REPORT', 'ATTACHMENT', 'OTHER');

-- CreateEnum
CREATE TYPE "Visibility" AS ENUM ('PUBLIC', 'PRIVATE');

-- CreateEnum
CREATE TYPE "OwnerType" AS ENUM ('BUSINESS', 'PERSON', 'SYSTEM');

-- CreateEnum
CREATE TYPE "DocumentType" AS ENUM ('ORDER_RECEIPT', 'INVOICE', 'SUBSCRIPTION_BILL', 'DEBT_STATEMENT', 'INVENTORY_REPORT', 'SALES_REPORT', 'CUSTOM_REPORT');

-- CreateEnum
CREATE TYPE "DocumentStatus" AS ENUM ('PENDING', 'GENERATING', 'DONE', 'FAILED');

-- CreateEnum
CREATE TYPE "DocRefType" AS ENUM ('ORDER', 'INVOICE', 'SUBSCRIPTION', 'DEBT', 'REPORT');

-- CreateTable
CREATE TABLE "files" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "business_id" UUID,
    "owner_id" UUID,
    "owner_type" "OwnerType",
    "category" "FileCategory" NOT NULL,
    "mime_type" VARCHAR(100) NOT NULL,
    "original_name" VARCHAR(255) NOT NULL,
    "storage_key" VARCHAR(500) NOT NULL,
    "url" VARCHAR(1000) NOT NULL,
    "size_bytes" INTEGER NOT NULL,
    "visibility" "Visibility" NOT NULL DEFAULT 'PRIVATE',
    "expires_at" TIMESTAMP(3),
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "deleted_at" TIMESTAMP(3),

    CONSTRAINT "files_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "documents" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "business_id" UUID,
    "type" "DocumentType" NOT NULL,
    "status" "DocumentStatus" NOT NULL DEFAULT 'PENDING',
    "ref_type" "DocRefType",
    "ref_id" UUID,
    "payload" JSONB NOT NULL,
    "file_id" UUID,
    "file_url" VARCHAR(1000),
    "failure_reason" VARCHAR,
    "generated_at" TIMESTAMP(3),
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,

    CONSTRAINT "documents_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "files_storage_key_key" ON "files"("storage_key");

-- CreateIndex
CREATE INDEX "files_business_id_category_idx" ON "files"("business_id", "category");

-- CreateIndex
CREATE INDEX "files_owner_id_owner_type_idx" ON "files"("owner_id", "owner_type");

-- CreateIndex
CREATE UNIQUE INDEX "documents_file_id_key" ON "documents"("file_id");

-- CreateIndex
CREATE INDEX "documents_business_id_type_idx" ON "documents"("business_id", "type");

-- CreateIndex
CREATE INDEX "documents_ref_type_ref_id_idx" ON "documents"("ref_type", "ref_id");

-- CreateIndex
CREATE INDEX "documents_status_idx" ON "documents"("status");
