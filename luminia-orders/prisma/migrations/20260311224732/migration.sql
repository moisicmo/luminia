-- CreateEnum
CREATE TYPE "OrderType" AS ENUM ('SALE', 'RETURN', 'QUOTE', 'SERVICE');

-- CreateEnum
CREATE TYPE "OrderStatus" AS ENUM ('DRAFT', 'CONFIRMED', 'DELIVERED', 'CANCELLED', 'RETURNED');

-- CreateEnum
CREATE TYPE "ItemType" AS ENUM ('PRODUCT', 'CLASS', 'MEMBERSHIP', 'CUSTOM');

-- CreateEnum
CREATE TYPE "PaymentMethod" AS ENUM ('CASH', 'CARD', 'QR', 'BANK_TRANSFER', 'CREDIT', 'OTHER');

-- CreateEnum
CREATE TYPE "PaymentStatus" AS ENUM ('PENDING', 'PAID', 'FAILED', 'REFUNDED');

-- CreateTable
CREATE TABLE "orders" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "business_id" UUID NOT NULL,
    "branch_id" UUID,
    "customer_id" UUID,
    "customer_name" VARCHAR(200),
    "seller_id" UUID,
    "order_number" VARCHAR(20) NOT NULL,
    "type" "OrderType" NOT NULL DEFAULT 'SALE',
    "status" "OrderStatus" NOT NULL DEFAULT 'DRAFT',
    "currency" VARCHAR(3) NOT NULL DEFAULT 'PEN',
    "subtotal" DECIMAL(15,4) NOT NULL DEFAULT 0,
    "discount_amount" DECIMAL(15,4) NOT NULL DEFAULT 0,
    "tax_amount" DECIMAL(15,4) NOT NULL DEFAULT 0,
    "total" DECIMAL(15,4) NOT NULL DEFAULT 0,
    "amount_paid" DECIMAL(15,4) NOT NULL DEFAULT 0,
    "amount_due" DECIMAL(15,4) NOT NULL DEFAULT 0,
    "notes" VARCHAR,
    "store_output_id" UUID,
    "confirmed_at" TIMESTAMP(3),
    "delivered_at" TIMESTAMP(3),
    "cancelled_at" TIMESTAMP(3),
    "cancel_reason" VARCHAR,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "orders_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "order_items" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "order_id" UUID NOT NULL,
    "item_type" "ItemType" NOT NULL,
    "item_id" UUID,
    "name" VARCHAR(200) NOT NULL,
    "sku" VARCHAR(50),
    "unit_name" VARCHAR(50),
    "quantity" DECIMAL(15,4) NOT NULL,
    "unit_price" DECIMAL(15,4) NOT NULL,
    "discount" DECIMAL(15,4) NOT NULL DEFAULT 0,
    "tax_rate" DECIMAL(5,2),
    "tax_amount" DECIMAL(15,4) NOT NULL DEFAULT 0,
    "subtotal" DECIMAL(15,4) NOT NULL,
    "total" DECIMAL(15,4) NOT NULL,
    "notes" VARCHAR,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "order_items_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "order_payments" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "order_id" UUID NOT NULL,
    "payment_id" UUID,
    "method" "PaymentMethod" NOT NULL,
    "amount" DECIMAL(15,4) NOT NULL,
    "currency" VARCHAR(3) NOT NULL DEFAULT 'PEN',
    "status" "PaymentStatus" NOT NULL DEFAULT 'PENDING',
    "reference" VARCHAR(100),
    "paid_at" TIMESTAMP(3),
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,

    CONSTRAINT "order_payments_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE INDEX "orders_business_id_status_idx" ON "orders"("business_id", "status");

-- CreateIndex
CREATE INDEX "orders_business_id_customer_id_idx" ON "orders"("business_id", "customer_id");

-- CreateIndex
CREATE UNIQUE INDEX "orders_business_id_order_number_key" ON "orders"("business_id", "order_number");

-- CreateIndex
CREATE INDEX "order_payments_order_id_idx" ON "order_payments"("order_id");

-- AddForeignKey
ALTER TABLE "order_items" ADD CONSTRAINT "order_items_order_id_fkey" FOREIGN KEY ("order_id") REFERENCES "orders"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "order_payments" ADD CONSTRAINT "order_payments_order_id_fkey" FOREIGN KEY ("order_id") REFERENCES "orders"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
