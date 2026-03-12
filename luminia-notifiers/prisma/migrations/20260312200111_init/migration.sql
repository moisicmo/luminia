-- CreateEnum
CREATE TYPE "Channel" AS ENUM ('EMAIL', 'SMS', 'WHATSAPP', 'PUSH');

-- CreateEnum
CREATE TYPE "ProviderType" AS ENUM ('SENDGRID', 'MAILGUN', 'SES', 'SMTP', 'TWILIO', 'INFOBIP', 'WHATSAPP_BUSINESS', 'TWILIO_WHATSAPP', 'FCM', 'APNS');

-- CreateEnum
CREATE TYPE "NotifEvent" AS ENUM ('REGISTER_WELCOME', 'VERIFY_EMAIL', 'VERIFY_PHONE', 'FORGOT_PASSWORD', 'LOGIN_ALERT', 'ACCOUNT_BLOCKED', 'ORDER_CONFIRMED', 'ORDER_DELIVERED', 'ORDER_CANCELLED', 'RECEIPT', 'INVOICE', 'PAYMENT_RECEIVED', 'PAYMENT_FAILED', 'DEBT_REMINDER', 'DEBT_OVERDUE', 'SUBSCRIPTION_CREATED', 'SUBSCRIPTION_EXPIRING', 'SUBSCRIPTION_EXPIRED', 'SUBSCRIPTION_RENEWED', 'CLASS_BOOKING_CONFIRMED', 'CLASS_REMINDER', 'CLASS_CANCELLED', 'CUSTOM');

-- CreateEnum
CREATE TYPE "NotifStatus" AS ENUM ('PENDING', 'SENDING', 'SENT', 'DELIVERED', 'READ', 'FAILED', 'CANCELLED');

-- CreateTable
CREATE TABLE "notifier_providers" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "business_id" UUID,
    "channel" "Channel" NOT NULL,
    "provider" "ProviderType" NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "credentials" JSONB NOT NULL,
    "is_default" BOOLEAN NOT NULL DEFAULT false,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "notifier_providers_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "notification_templates" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "business_id" UUID,
    "channel" "Channel" NOT NULL,
    "event" "NotifEvent" NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "subject" VARCHAR(200),
    "body" TEXT NOT NULL,
    "variables" TEXT[],
    "active" BOOLEAN NOT NULL DEFAULT true,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,
    "updated_by" UUID,

    CONSTRAINT "notification_templates_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "notifications" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "business_id" UUID,
    "provider_id" UUID,
    "template_id" UUID,
    "channel" "Channel" NOT NULL,
    "event" "NotifEvent" NOT NULL,
    "recipient_id" UUID,
    "recipient_address" VARCHAR(200) NOT NULL,
    "subject" VARCHAR(200),
    "rendered_body" TEXT NOT NULL,
    "payload" JSONB,
    "status" "NotifStatus" NOT NULL DEFAULT 'PENDING',
    "provider_ref" VARCHAR(200),
    "provider_response" JSONB,
    "failure_reason" VARCHAR,
    "attempt_count" INTEGER NOT NULL DEFAULT 0,
    "last_attempt_at" TIMESTAMP(3),
    "sent_at" TIMESTAMP(3),
    "delivered_at" TIMESTAMP(3),
    "read_at" TIMESTAMP(3),
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "notifications_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "notifier_providers_business_id_channel_provider_name_key" ON "notifier_providers"("business_id", "channel", "provider", "name");

-- CreateIndex
CREATE UNIQUE INDEX "notification_templates_business_id_channel_event_key" ON "notification_templates"("business_id", "channel", "event");

-- CreateIndex
CREATE INDEX "notifications_recipient_id_channel_idx" ON "notifications"("recipient_id", "channel");

-- CreateIndex
CREATE INDEX "notifications_business_id_event_status_idx" ON "notifications"("business_id", "event", "status");

-- CreateIndex
CREATE INDEX "notifications_status_attempt_count_idx" ON "notifications"("status", "attempt_count");

-- AddForeignKey
ALTER TABLE "notifications" ADD CONSTRAINT "notifications_provider_id_fkey" FOREIGN KEY ("provider_id") REFERENCES "notifier_providers"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "notifications" ADD CONSTRAINT "notifications_template_id_fkey" FOREIGN KEY ("template_id") REFERENCES "notification_templates"("id") ON DELETE SET NULL ON UPDATE CASCADE;
