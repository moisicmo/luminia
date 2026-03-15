-- CreateEnum
CREATE TYPE "ChannelType" AS ENUM ('MOBILE_WALLET', 'BANK_TRANSFER', 'QR', 'CASH_PICKUP', 'CARD');

-- CreateEnum
CREATE TYPE "RemittanceStatus" AS ENUM ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED', 'REFUNDED');

-- CreateTable
CREATE TABLE "corridors" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "origin_country" VARCHAR(3) NOT NULL,
    "origin_currency" VARCHAR(3) NOT NULL,
    "dest_country" VARCHAR(3) NOT NULL,
    "dest_currency" VARCHAR(3) NOT NULL,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "corridors_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "exchange_rates" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "corridor_id" UUID NOT NULL,
    "rate" DECIMAL(15,6) NOT NULL,
    "spread_pct" DECIMAL(5,4) NOT NULL DEFAULT 0,
    "valid_from" TIMESTAMP(3) NOT NULL,
    "valid_until" TIMESTAMP(3),
    "active" BOOLEAN NOT NULL DEFAULT true,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "exchange_rates_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "payment_channels" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "country" VARCHAR(3) NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "channel_type" "ChannelType" NOT NULL,
    "config" TEXT,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "payment_channels_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "remittances" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "corridor_id" UUID NOT NULL,
    "sender_id" UUID NOT NULL,
    "sender_name" VARCHAR(200) NOT NULL,
    "recipient_id" UUID,
    "recipient_name" VARCHAR(200) NOT NULL,
    "recipient_account" VARCHAR(100),
    "recipient_phone" VARCHAR(20),
    "send_amount" DECIMAL(15,4) NOT NULL,
    "send_currency" VARCHAR(3) NOT NULL,
    "receive_amount" DECIMAL(15,4) NOT NULL,
    "receive_currency" VARCHAR(3) NOT NULL,
    "exchange_rate" DECIMAL(15,6) NOT NULL,
    "fee" DECIMAL(15,4) NOT NULL,
    "fee_currency" VARCHAR(3) NOT NULL,
    "status" "RemittanceStatus" NOT NULL DEFAULT 'PENDING',
    "send_channel" VARCHAR(100),
    "receive_channel" VARCHAR(100),
    "transaction_id" UUID,
    "purpose" VARCHAR(200),
    "completed_at" TIMESTAMP(3),
    "cancelled_at" TIMESTAMP(3),
    "cancel_reason" VARCHAR,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,

    CONSTRAINT "remittances_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "corridors_origin_country_dest_country_key" ON "corridors"("origin_country", "dest_country");

-- CreateIndex
CREATE INDEX "exchange_rates_corridor_id_active_idx" ON "exchange_rates"("corridor_id", "active");

-- CreateIndex
CREATE INDEX "remittances_sender_id_idx" ON "remittances"("sender_id");

-- CreateIndex
CREATE INDEX "remittances_status_idx" ON "remittances"("status");

-- AddForeignKey
ALTER TABLE "exchange_rates" ADD CONSTRAINT "exchange_rates_corridor_id_fkey" FOREIGN KEY ("corridor_id") REFERENCES "corridors"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "remittances" ADD CONSTRAINT "remittances_corridor_id_fkey" FOREIGN KEY ("corridor_id") REFERENCES "corridors"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
