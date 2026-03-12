-- CreateEnum
CREATE TYPE "GatewayType" AS ENUM ('CASH', 'CARD_POS', 'STRIPE', 'QR_SIMPLE', 'TIGO_MONEY', 'MERCADO_PAGO', 'BANK_TRANSFER', 'WALLET', 'OTHER');

-- CreateEnum
CREATE TYPE "TransactionDir" AS ENUM ('INBOUND', 'OUTBOUND');

-- CreateEnum
CREATE TYPE "TransactionStatus" AS ENUM ('PENDING', 'PROCESSING', 'PAID', 'FAILED', 'REFUNDED', 'PARTIAL_REFUND', 'CANCELLED');

-- CreateEnum
CREATE TYPE "ReferenceType" AS ENUM ('ORDER', 'SUBSCRIPTION', 'DEBT', 'WALLET_TOPUP', 'MANUAL');

-- CreateEnum
CREATE TYPE "DebtType" AS ENUM ('RECEIVABLE', 'PAYABLE');

-- CreateEnum
CREATE TYPE "DebtStatus" AS ENUM ('PENDING', 'PARTIAL', 'PAID', 'OVERDUE', 'CANCELLED');

-- CreateEnum
CREATE TYPE "PartyType" AS ENUM ('BUSINESS', 'CUSTOMER', 'SUPPLIER');

-- CreateEnum
CREATE TYPE "MovementType" AS ENUM ('CREDIT', 'DEBIT');

-- CreateTable
CREATE TABLE "payment_method_configs" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "business_id" UUID NOT NULL,
    "type" "GatewayType" NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "credentials" JSONB,
    "is_default" BOOLEAN NOT NULL DEFAULT false,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "payment_method_configs_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "transactions" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "business_id" UUID NOT NULL,
    "method_config_id" UUID,
    "direction" "TransactionDir" NOT NULL,
    "reference_type" "ReferenceType" NOT NULL,
    "reference_id" UUID,
    "amount" DECIMAL(15,4) NOT NULL,
    "fee" DECIMAL(15,4) NOT NULL DEFAULT 0,
    "net_amount" DECIMAL(15,4) NOT NULL,
    "currency" VARCHAR(3) NOT NULL DEFAULT 'BOB',
    "status" "TransactionStatus" NOT NULL DEFAULT 'PENDING',
    "gateway_ref" VARCHAR(200),
    "gateway_response" JSONB,
    "description" VARCHAR,
    "failure_reason" VARCHAR,
    "processed_at" TIMESTAMP(3),
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "transactions_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "debts" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "business_id" UUID NOT NULL,
    "type" "DebtType" NOT NULL,
    "debtor_id" UUID,
    "debtor_type" "PartyType",
    "debtor_name" VARCHAR(200) NOT NULL,
    "creditor_id" UUID,
    "creditor_type" "PartyType",
    "creditor_name" VARCHAR(200) NOT NULL,
    "reference_type" "ReferenceType",
    "reference_id" UUID,
    "original_amount" DECIMAL(15,4) NOT NULL,
    "paid_amount" DECIMAL(15,4) NOT NULL DEFAULT 0,
    "pending_amount" DECIMAL(15,4) NOT NULL,
    "currency" VARCHAR(3) NOT NULL DEFAULT 'BOB',
    "due_date" DATE,
    "status" "DebtStatus" NOT NULL DEFAULT 'PENDING',
    "notes" VARCHAR,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "debts_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "debt_payments" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "debt_id" UUID NOT NULL,
    "transaction_id" UUID NOT NULL,
    "amount" DECIMAL(15,4) NOT NULL,
    "paid_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "notes" VARCHAR,
    "created_by" UUID NOT NULL,

    CONSTRAINT "debt_payments_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "wallets" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "business_id" UUID NOT NULL,
    "owner_id" UUID NOT NULL,
    "owner_type" "PartyType" NOT NULL,
    "balance" DECIMAL(15,4) NOT NULL DEFAULT 0,
    "currency" VARCHAR(3) NOT NULL DEFAULT 'BOB',
    "active" BOOLEAN NOT NULL DEFAULT true,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "wallets_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "wallet_movements" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "wallet_id" UUID NOT NULL,
    "transaction_id" UUID,
    "type" "MovementType" NOT NULL,
    "amount" DECIMAL(15,4) NOT NULL,
    "balance_before" DECIMAL(15,4) NOT NULL,
    "balance_after" DECIMAL(15,4) NOT NULL,
    "description" VARCHAR,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,

    CONSTRAINT "wallet_movements_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "payment_method_configs_business_id_type_name_key" ON "payment_method_configs"("business_id", "type", "name");

-- CreateIndex
CREATE INDEX "transactions_business_id_status_idx" ON "transactions"("business_id", "status");

-- CreateIndex
CREATE INDEX "transactions_reference_type_reference_id_idx" ON "transactions"("reference_type", "reference_id");

-- CreateIndex
CREATE INDEX "debts_business_id_type_status_idx" ON "debts"("business_id", "type", "status");

-- CreateIndex
CREATE INDEX "debts_debtor_id_debtor_type_idx" ON "debts"("debtor_id", "debtor_type");

-- CreateIndex
CREATE UNIQUE INDEX "wallets_business_id_owner_id_owner_type_currency_key" ON "wallets"("business_id", "owner_id", "owner_type", "currency");

-- CreateIndex
CREATE UNIQUE INDEX "wallet_movements_transaction_id_key" ON "wallet_movements"("transaction_id");

-- CreateIndex
CREATE INDEX "wallet_movements_wallet_id_created_at_idx" ON "wallet_movements"("wallet_id", "created_at");

-- AddForeignKey
ALTER TABLE "transactions" ADD CONSTRAINT "transactions_method_config_id_fkey" FOREIGN KEY ("method_config_id") REFERENCES "payment_method_configs"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "debt_payments" ADD CONSTRAINT "debt_payments_debt_id_fkey" FOREIGN KEY ("debt_id") REFERENCES "debts"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "debt_payments" ADD CONSTRAINT "debt_payments_transaction_id_fkey" FOREIGN KEY ("transaction_id") REFERENCES "transactions"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "wallet_movements" ADD CONSTRAINT "wallet_movements_wallet_id_fkey" FOREIGN KEY ("wallet_id") REFERENCES "wallets"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "wallet_movements" ADD CONSTRAINT "wallet_movements_transaction_id_fkey" FOREIGN KEY ("transaction_id") REFERENCES "transactions"("id") ON DELETE SET NULL ON UPDATE CASCADE;
