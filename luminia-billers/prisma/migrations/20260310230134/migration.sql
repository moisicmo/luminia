-- CreateEnum
CREATE TYPE "InvoiceRefType" AS ENUM ('ORDER', 'SUBSCRIPTION', 'MANUAL');

-- CreateEnum
CREATE TYPE "ModalitySiat" AS ENUM ('UNIFIED', 'BILLING');

-- CreateEnum
CREATE TYPE "EnvironmentSiat" AS ENUM ('PRODUCTION', 'TEST');

-- CreateEnum
CREATE TYPE "InvoiceStatus" AS ENUM ('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED');

-- CreateEnum
CREATE TYPE "InvoiceRequestType" AS ENUM ('INVOICE_SEND', 'EVENT_SEND');

-- CreateEnum
CREATE TYPE "WrapperStatus" AS ENUM ('PENDING', 'SENT', 'ACCEPTED', 'REJECTED');

-- CreateEnum
CREATE TYPE "BatchStatus" AS ENUM ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED');

-- CreateEnum
CREATE TYPE "PendingAction" AS ENUM ('SEND', 'CANCEL', 'FIX');

-- CreateEnum
CREATE TYPE "PendingStatus" AS ENUM ('PENDING', 'PROCESSING', 'FAILED', 'COMPLETED');

-- CreateEnum
CREATE TYPE "SiatStatus" AS ENUM ('RECEIVED', 'ACCEPTED', 'REJECTED');

-- CreateTable
CREATE TABLE "siat_configs" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "business_id" UUID NOT NULL,
    "environment" "EnvironmentSiat" NOT NULL DEFAULT 'TEST',
    "nit" VARCHAR(20) NOT NULL,
    "social_reason" VARCHAR(500) NOT NULL,
    "main_activity_code" INTEGER NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "siat_configs_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "branch_offices" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "business_id" UUID NOT NULL,
    "branch_id" UUID,
    "branch_office_siat_id" INTEGER NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "description" VARCHAR NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "city" VARCHAR,
    "phone" VARCHAR(15),
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "branch_offices_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "point_sales" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "branch_office_id" UUID NOT NULL,
    "point_of_sale_id" UUID,
    "point_sale_siat_id" INTEGER NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    "description" VARCHAR NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "point_sale_type_id" UUID,

    CONSTRAINT "point_sales_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "cuis" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "code" VARCHAR(20) NOT NULL,
    "start_date" TIMESTAMP(3) NOT NULL,
    "end_date" TIMESTAMP(3) NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "point_sale_id" UUID NOT NULL,

    CONSTRAINT "cuis_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "cufds" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "code" VARCHAR(100) NOT NULL,
    "control_code" VARCHAR(20) NOT NULL,
    "address" VARCHAR NOT NULL,
    "start_date" TIMESTAMP(3) NOT NULL,
    "end_date" TIMESTAMP(3) NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "cuis_id" UUID NOT NULL,

    CONSTRAINT "cufds_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "invoices" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "business_id" UUID NOT NULL,
    "reference_type" "InvoiceRefType",
    "reference_id" UUID,
    "invoice_number" INTEGER NOT NULL,
    "cuf" VARCHAR(70) NOT NULL,
    "cafc" VARCHAR,
    "reception_code" VARCHAR(50),
    "broadcast_date" TIMESTAMP(3) NOT NULL,
    "issuer_nit" VARCHAR(20) NOT NULL,
    "issuer_name" VARCHAR(500) NOT NULL,
    "buyer_nit" VARCHAR(20) NOT NULL,
    "buyer_name" VARCHAR(500) NOT NULL,
    "buyer_complement" VARCHAR(5),
    "buyer_email" VARCHAR(200),
    "total_amount" DECIMAL(15,4) NOT NULL,
    "vatable_amount" DECIMAL(15,4) NOT NULL,
    "ice_amount" DECIMAL(15,4) NOT NULL DEFAULT 0,
    "iehd_amount" DECIMAL(15,4) NOT NULL DEFAULT 0,
    "additional_discount" DECIMAL(15,4) NOT NULL DEFAULT 0,
    "invoice_xml" TEXT NOT NULL,
    "invoice_hash" VARCHAR(70) NOT NULL,
    "invoice_json" TEXT NOT NULL,
    "status" "InvoiceStatus" NOT NULL DEFAULT 'PENDING',
    "siat_status" "SiatStatus",
    "modality" "ModalitySiat" NOT NULL,
    "cufd_id" UUID NOT NULL,
    "sector_document_type_id" UUID NOT NULL,
    "broadcast_type_id" UUID NOT NULL,
    "invoice_type_id" UUID NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "invoices_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "invoice_details" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "invoice_id" UUID NOT NULL,
    "economic_activity" VARCHAR(10) NOT NULL,
    "sin_product_code" INTEGER NOT NULL,
    "product_code" VARCHAR(20) NOT NULL,
    "description" VARCHAR(1000) NOT NULL,
    "quantity" DECIMAL(15,4) NOT NULL,
    "measurement_unit" INTEGER NOT NULL,
    "unit_price" DECIMAL(15,4) NOT NULL,
    "discount_amount" DECIMAL(15,4) NOT NULL DEFAULT 0,
    "subtotal" DECIMAL(15,4) NOT NULL,
    "imei_number" VARCHAR(20),
    "origin_country_code" INTEGER,

    CONSTRAINT "invoice_details_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "invoice_cancellations" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "invoice_id" UUID NOT NULL,
    "cancellation_reason_id" UUID NOT NULL,
    "reception_code" VARCHAR(50),
    "siat_status" "SiatStatus",
    "cancelled_at" TIMESTAMP(3) NOT NULL,
    "cancelled_by" UUID NOT NULL,
    "notes" VARCHAR,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "invoice_cancellations_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "invoice_requests" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "request_date" TIMESTAMP(3) NOT NULL,
    "response_date" TIMESTAMP(3),
    "business_id" UUID,
    "invoice_id" UUID,
    "request" TEXT NOT NULL,
    "cuf" VARCHAR(70),
    "observation" VARCHAR(500),
    "response" BOOLEAN,
    "elapsed_time" BIGINT,
    "type" "InvoiceRequestType" NOT NULL,
    "error_checked" BOOLEAN NOT NULL DEFAULT false,

    CONSTRAINT "invoice_requests_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "invoice_batches" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "invoice_number" BIGINT NOT NULL,
    "broadcast_date" TIMESTAMP(3) NOT NULL,
    "invoice_json" TEXT NOT NULL,
    "response_message" VARCHAR,
    "invoice_id" UUID,
    "batch_id" UUID,
    "sector_document_type_id" UUID,

    CONSTRAINT "invoice_batches_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "invoice_wrappers" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "file_number" INTEGER NOT NULL,
    "response_message" VARCHAR,
    "status" "WrapperStatus" NOT NULL,
    "invoice_batch_id" UUID,
    "wrapper_id" UUID,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "invoice_wrappers_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "invoice_wrapper_events" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "file_number" INTEGER NOT NULL,
    "response_message" VARCHAR,
    "status" "WrapperStatus" NOT NULL,
    "invoice_id" UUID,
    "wrapper_event_id" UUID,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "invoice_wrapper_events_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "pending_to_fixes" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "invoice_id" UUID NOT NULL,
    "business_id" UUID NOT NULL,
    "event_id" UUID,
    "action" "PendingAction" NOT NULL,
    "counter" INTEGER NOT NULL DEFAULT 0,
    "fail_reason" VARCHAR(2000),
    "status" "PendingStatus" NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "pending_to_fixes_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "automatic_invoice_fixes" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "invoice_id" UUID NOT NULL,
    "old_status" "InvoiceStatus" NOT NULL,
    "new_status" "InvoiceStatus" NOT NULL,
    "siat_status" "SiatStatus" NOT NULL,
    "siat_observation" VARCHAR NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "automatic_invoice_fixes_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "check_invoices" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "position" BIGINT NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "check_invoices_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "events" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "cufd_event" VARCHAR(100) NOT NULL,
    "start_date" TIMESTAMP(3) NOT NULL,
    "end_date" TIMESTAMP(3) NOT NULL,
    "reception_code" INTEGER,
    "description" VARCHAR(500) NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "cafc" VARCHAR,
    "branch_office_id" UUID NOT NULL,
    "significant_event_id" UUID NOT NULL,
    "point_sale_id" UUID NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "events_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "wrappers" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "cufd_event" VARCHAR(100) NOT NULL,
    "start_date" TIMESTAMP(3) NOT NULL,
    "end_date" TIMESTAMP(3) NOT NULL,
    "reception_code" VARCHAR NOT NULL,
    "status" "WrapperStatus" NOT NULL,
    "schedule_setting_id" UUID,
    "branch_office_id" UUID NOT NULL,
    "point_sale_id" UUID NOT NULL,
    "sector_document_type_id" UUID NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "wrappers_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "wrapper_events" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "reception_code" VARCHAR NOT NULL,
    "status" "WrapperStatus" NOT NULL,
    "event_id" UUID NOT NULL,
    "branch_office_id" UUID NOT NULL,
    "point_sale_id" UUID NOT NULL,
    "sector_document_type_id" UUID NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "wrapper_events_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "wrapper_logs" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "wrapper_type" INTEGER NOT NULL,
    "start_date" TIMESTAMP(3) NOT NULL,
    "end_date" TIMESTAMP(3) NOT NULL,
    "response" BOOLEAN,
    "description" VARCHAR,
    "business_id" UUID NOT NULL,

    CONSTRAINT "wrapper_logs_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "batches" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "reception_code" VARCHAR(50) NOT NULL,
    "date" TIMESTAMP(3) NOT NULL,
    "status" "BatchStatus" NOT NULL,
    "business_id" UUID NOT NULL,

    CONSTRAINT "batches_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "schedule_settings" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "cron_date" VARCHAR(50) NOT NULL,
    "description" VARCHAR NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "business_id" UUID NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "schedule_settings_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "sync_settings" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "sync_type" INTEGER NOT NULL,
    "cron_date" VARCHAR NOT NULL,
    "description" VARCHAR NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "business_id" UUID NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "sync_settings_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "sync_logs" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "sync_type" INTEGER NOT NULL,
    "start_date" TIMESTAMP(3),
    "end_date" TIMESTAMP(3),
    "response" BOOLEAN,
    "description" VARCHAR,
    "business_id" UUID NOT NULL,

    CONSTRAINT "sync_logs_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "signatures" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "certificate" TEXT NOT NULL,
    "private_key" TEXT NOT NULL,
    "start_date" TIMESTAMP(3),
    "end_date" TIMESTAMP(3),
    "active" BOOLEAN NOT NULL DEFAULT true,
    "business_id" UUID NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "signatures_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "product_services" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "siat_id" INTEGER NOT NULL,
    "activity_code" INTEGER NOT NULL,
    "description" VARCHAR(1000) NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "business_id" UUID NOT NULL,

    CONSTRAINT "product_services_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "approved_products" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "product_code" VARCHAR(20) NOT NULL,
    "description" VARCHAR NOT NULL,
    "business_id" UUID NOT NULL,
    "product_service_id" UUID NOT NULL,
    "measurement_unit_id" UUID NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "approved_products_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "activities" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "siat_id" INTEGER NOT NULL,
    "description" VARCHAR NOT NULL,
    "activity_type" VARCHAR(10) NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "business_id" UUID NOT NULL,

    CONSTRAINT "activities_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "broadcast_types" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "siat_id" INTEGER NOT NULL,
    "description" VARCHAR NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "business_id" UUID NOT NULL,

    CONSTRAINT "broadcast_types_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "invoice_types" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "siat_id" INTEGER NOT NULL,
    "description" VARCHAR NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "business_id" UUID NOT NULL,

    CONSTRAINT "invoice_types_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "invoice_legends" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "activity_code" INTEGER NOT NULL,
    "description" VARCHAR NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "business_id" UUID NOT NULL,

    CONSTRAINT "invoice_legends_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "service_messages" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "siat_id" INTEGER NOT NULL,
    "description" VARCHAR NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "business_id" UUID NOT NULL,

    CONSTRAINT "service_messages_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "cancellation_reasons" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "siat_id" INTEGER NOT NULL,
    "description" VARCHAR NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "business_id" UUID NOT NULL,

    CONSTRAINT "cancellation_reasons_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "origin_countries" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "siat_id" INTEGER NOT NULL,
    "description" VARCHAR NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "business_id" UUID NOT NULL,

    CONSTRAINT "origin_countries_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "sector_document_types" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "siat_id" INTEGER NOT NULL,
    "description" VARCHAR NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "business_id" UUID NOT NULL,

    CONSTRAINT "sector_document_types_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "significant_events" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "siat_id" INTEGER NOT NULL,
    "description" VARCHAR NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "business_id" UUID NOT NULL,

    CONSTRAINT "significant_events_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "room_types" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "siat_id" INTEGER NOT NULL,
    "description" VARCHAR NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "business_id" UUID NOT NULL,

    CONSTRAINT "room_types_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "point_sale_types" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "siat_id" INTEGER NOT NULL,
    "description" VARCHAR NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "business_id" UUID NOT NULL,

    CONSTRAINT "point_sale_types_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "payment_method_types" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "siat_id" INTEGER NOT NULL,
    "description" VARCHAR NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "business_id" UUID NOT NULL,

    CONSTRAINT "payment_method_types_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "currency_types" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "siat_id" INTEGER NOT NULL,
    "description" VARCHAR NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "business_id" UUID NOT NULL,

    CONSTRAINT "currency_types_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "measurement_units" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "siat_id" INTEGER NOT NULL,
    "description" VARCHAR NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "business_id" UUID NOT NULL,

    CONSTRAINT "measurement_units_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "document_sectors" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "siat_id" INTEGER NOT NULL,
    "activity_code" INTEGER NOT NULL,
    "document_sector_type" VARCHAR(10) NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "business_id" UUID NOT NULL,

    CONSTRAINT "document_sectors_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "identity_document_types" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "siat_id" INTEGER NOT NULL,
    "description" VARCHAR NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "business_id" UUID NOT NULL,

    CONSTRAINT "identity_document_types_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "valid_identity_document_types" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "siat_code" INTEGER NOT NULL,
    "document" VARCHAR(20) NOT NULL,
    "complement" VARCHAR(5),
    "social_reason" VARCHAR(100) NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "valid_identity_document_types_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "siat_configs_business_id_key" ON "siat_configs"("business_id");

-- CreateIndex
CREATE INDEX "invoices_business_id_status_idx" ON "invoices"("business_id", "status");

-- CreateIndex
CREATE INDEX "invoices_broadcast_date_status_idx" ON "invoices"("broadcast_date", "status");

-- CreateIndex
CREATE INDEX "invoices_reference_id_idx" ON "invoices"("reference_id");

-- CreateIndex
CREATE UNIQUE INDEX "invoice_cancellations_invoice_id_key" ON "invoice_cancellations"("invoice_id");

-- CreateIndex
CREATE UNIQUE INDEX "invoice_requests_invoice_id_key" ON "invoice_requests"("invoice_id");

-- CreateIndex
CREATE UNIQUE INDEX "pending_to_fixes_invoice_id_key" ON "pending_to_fixes"("invoice_id");

-- CreateIndex
CREATE UNIQUE INDEX "automatic_invoice_fixes_invoice_id_key" ON "automatic_invoice_fixes"("invoice_id");

-- AddForeignKey
ALTER TABLE "point_sales" ADD CONSTRAINT "point_sales_branch_office_id_fkey" FOREIGN KEY ("branch_office_id") REFERENCES "branch_offices"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "point_sales" ADD CONSTRAINT "point_sales_point_sale_type_id_fkey" FOREIGN KEY ("point_sale_type_id") REFERENCES "point_sale_types"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "cuis" ADD CONSTRAINT "cuis_point_sale_id_fkey" FOREIGN KEY ("point_sale_id") REFERENCES "point_sales"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "cufds" ADD CONSTRAINT "cufds_cuis_id_fkey" FOREIGN KEY ("cuis_id") REFERENCES "cuis"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "invoices" ADD CONSTRAINT "invoices_cufd_id_fkey" FOREIGN KEY ("cufd_id") REFERENCES "cufds"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "invoices" ADD CONSTRAINT "invoices_sector_document_type_id_fkey" FOREIGN KEY ("sector_document_type_id") REFERENCES "sector_document_types"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "invoices" ADD CONSTRAINT "invoices_broadcast_type_id_fkey" FOREIGN KEY ("broadcast_type_id") REFERENCES "broadcast_types"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "invoices" ADD CONSTRAINT "invoices_invoice_type_id_fkey" FOREIGN KEY ("invoice_type_id") REFERENCES "invoice_types"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "invoice_details" ADD CONSTRAINT "invoice_details_invoice_id_fkey" FOREIGN KEY ("invoice_id") REFERENCES "invoices"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "invoice_cancellations" ADD CONSTRAINT "invoice_cancellations_invoice_id_fkey" FOREIGN KEY ("invoice_id") REFERENCES "invoices"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "invoice_requests" ADD CONSTRAINT "invoice_requests_invoice_id_fkey" FOREIGN KEY ("invoice_id") REFERENCES "invoices"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "invoice_batches" ADD CONSTRAINT "invoice_batches_invoice_id_fkey" FOREIGN KEY ("invoice_id") REFERENCES "invoices"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "invoice_batches" ADD CONSTRAINT "invoice_batches_batch_id_fkey" FOREIGN KEY ("batch_id") REFERENCES "batches"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "invoice_wrappers" ADD CONSTRAINT "invoice_wrappers_invoice_batch_id_fkey" FOREIGN KEY ("invoice_batch_id") REFERENCES "invoice_batches"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "invoice_wrappers" ADD CONSTRAINT "invoice_wrappers_wrapper_id_fkey" FOREIGN KEY ("wrapper_id") REFERENCES "wrappers"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "invoice_wrapper_events" ADD CONSTRAINT "invoice_wrapper_events_invoice_id_fkey" FOREIGN KEY ("invoice_id") REFERENCES "invoices"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "invoice_wrapper_events" ADD CONSTRAINT "invoice_wrapper_events_wrapper_event_id_fkey" FOREIGN KEY ("wrapper_event_id") REFERENCES "wrapper_events"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "pending_to_fixes" ADD CONSTRAINT "pending_to_fixes_invoice_id_fkey" FOREIGN KEY ("invoice_id") REFERENCES "invoices"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "pending_to_fixes" ADD CONSTRAINT "pending_to_fixes_event_id_fkey" FOREIGN KEY ("event_id") REFERENCES "events"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "automatic_invoice_fixes" ADD CONSTRAINT "automatic_invoice_fixes_invoice_id_fkey" FOREIGN KEY ("invoice_id") REFERENCES "invoices"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "events" ADD CONSTRAINT "events_branch_office_id_fkey" FOREIGN KEY ("branch_office_id") REFERENCES "branch_offices"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "events" ADD CONSTRAINT "events_point_sale_id_fkey" FOREIGN KEY ("point_sale_id") REFERENCES "point_sales"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "events" ADD CONSTRAINT "events_significant_event_id_fkey" FOREIGN KEY ("significant_event_id") REFERENCES "significant_events"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "wrappers" ADD CONSTRAINT "wrappers_schedule_setting_id_fkey" FOREIGN KEY ("schedule_setting_id") REFERENCES "schedule_settings"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "wrappers" ADD CONSTRAINT "wrappers_branch_office_id_fkey" FOREIGN KEY ("branch_office_id") REFERENCES "branch_offices"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "wrappers" ADD CONSTRAINT "wrappers_point_sale_id_fkey" FOREIGN KEY ("point_sale_id") REFERENCES "point_sales"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "wrappers" ADD CONSTRAINT "wrappers_sector_document_type_id_fkey" FOREIGN KEY ("sector_document_type_id") REFERENCES "sector_document_types"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "wrapper_events" ADD CONSTRAINT "wrapper_events_event_id_fkey" FOREIGN KEY ("event_id") REFERENCES "events"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "wrapper_events" ADD CONSTRAINT "wrapper_events_branch_office_id_fkey" FOREIGN KEY ("branch_office_id") REFERENCES "branch_offices"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "wrapper_events" ADD CONSTRAINT "wrapper_events_point_sale_id_fkey" FOREIGN KEY ("point_sale_id") REFERENCES "point_sales"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "wrapper_events" ADD CONSTRAINT "wrapper_events_sector_document_type_id_fkey" FOREIGN KEY ("sector_document_type_id") REFERENCES "sector_document_types"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "approved_products" ADD CONSTRAINT "approved_products_product_service_id_fkey" FOREIGN KEY ("product_service_id") REFERENCES "product_services"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "approved_products" ADD CONSTRAINT "approved_products_measurement_unit_id_fkey" FOREIGN KEY ("measurement_unit_id") REFERENCES "measurement_units"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
