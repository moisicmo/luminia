-- CreateEnum
CREATE TYPE "EquipmentCategory" AS ENUM ('CARDIO', 'STRENGTH', 'FREE_WEIGHTS', 'FUNCTIONAL', 'STRETCHING', 'OTHER');

-- CreateEnum
CREATE TYPE "EquipmentStatus" AS ENUM ('ACTIVE', 'MAINTENANCE', 'OUT_OF_SERVICE');

-- CreateEnum
CREATE TYPE "ScheduleStatus" AS ENUM ('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED');

-- CreateEnum
CREATE TYPE "BookingStatus" AS ENUM ('RESERVED', 'ATTENDED', 'CANCELLED', 'NO_SHOW');

-- CreateEnum
CREATE TYPE "MuscleGroup" AS ENUM ('CHEST', 'BACK', 'SHOULDERS', 'BICEPS', 'TRICEPS', 'FOREARMS', 'ABS', 'GLUTES', 'QUADRICEPS', 'HAMSTRINGS', 'CALVES', 'FULL_BODY', 'CARDIO');

-- CreateEnum
CREATE TYPE "FitnessLevel" AS ENUM ('BEGINNER', 'INTERMEDIATE', 'ADVANCED');

-- CreateEnum
CREATE TYPE "BodyPart" AS ENUM ('CHEST', 'WAIST', 'HIPS', 'LEFT_ARM', 'RIGHT_ARM', 'LEFT_THIGH', 'RIGHT_THIGH', 'LEFT_CALF', 'RIGHT_CALF', 'NECK', 'SHOULDERS');

-- CreateEnum
CREATE TYPE "HabitCategory" AS ENUM ('HYDRATION', 'SLEEP', 'EXERCISE', 'NUTRITION', 'MINDFULNESS', 'MOBILITY', 'CUSTOM');

-- CreateTable
CREATE TABLE "gym_profiles" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "business_id" UUID NOT NULL,
    "description" TEXT,
    "cover_image" VARCHAR(1000),
    "amenities" TEXT[],
    "rating" DECIMAL(3,2) NOT NULL DEFAULT 0,
    "review_count" INTEGER NOT NULL DEFAULT 0,
    "discoverable" BOOLEAN NOT NULL DEFAULT true,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,

    CONSTRAINT "gym_profiles_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "gym_branch_profiles" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "gym_profile_id" UUID NOT NULL,
    "branch_id" UUID NOT NULL,
    "description" TEXT,
    "capacity" INTEGER,
    "photos" TEXT[],
    "amenities" TEXT[],
    "active" BOOLEAN NOT NULL DEFAULT true,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,

    CONSTRAINT "gym_branch_profiles_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "gym_reviews" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "gym_profile_id" UUID NOT NULL,
    "member_id" UUID NOT NULL,
    "rating" INTEGER NOT NULL,
    "comment" TEXT,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "gym_reviews_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "equipment" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "branch_profile_id" UUID NOT NULL,
    "name" VARCHAR(150) NOT NULL,
    "category" "EquipmentCategory" NOT NULL,
    "brand" VARCHAR(100),
    "quantity" INTEGER NOT NULL DEFAULT 1,
    "status" "EquipmentStatus" NOT NULL DEFAULT 'ACTIVE',
    "image_url" VARCHAR(1000),
    "notes" VARCHAR,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,

    CONSTRAINT "equipment_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "gym_classes" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "branch_profile_id" UUID NOT NULL,
    "name" VARCHAR(100) NOT NULL,
    "description" TEXT,
    "duration_min" INTEGER NOT NULL,
    "capacity" INTEGER,
    "image_url" VARCHAR(1000),
    "active" BOOLEAN NOT NULL DEFAULT true,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,

    CONSTRAINT "gym_classes_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "class_schedules" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "gym_class_id" UUID NOT NULL,
    "instructor_id" UUID,
    "start_at" TIMESTAMP(3) NOT NULL,
    "end_at" TIMESTAMP(3) NOT NULL,
    "location" VARCHAR(100),
    "status" "ScheduleStatus" NOT NULL DEFAULT 'SCHEDULED',
    "cancel_reason" VARCHAR,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,

    CONSTRAINT "class_schedules_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "class_bookings" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "schedule_id" UUID NOT NULL,
    "member_id" UUID NOT NULL,
    "status" "BookingStatus" NOT NULL DEFAULT 'RESERVED',
    "attended_at" TIMESTAMP(3),
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "class_bookings_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "exercises" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "business_id" UUID,
    "name" VARCHAR(150) NOT NULL,
    "muscle_group" "MuscleGroup" NOT NULL,
    "equipment" VARCHAR(100),
    "description" TEXT,
    "video_url" VARCHAR(1000),
    "image_url" VARCHAR(1000),
    "active" BOOLEAN NOT NULL DEFAULT true,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,

    CONSTRAINT "exercises_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "routines" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "business_id" UUID NOT NULL,
    "name" VARCHAR(150) NOT NULL,
    "goal" VARCHAR(100),
    "level" "FitnessLevel" NOT NULL DEFAULT 'BEGINNER',
    "duration_weeks" INTEGER,
    "description" TEXT,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,

    CONSTRAINT "routines_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "routine_exercises" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "routine_id" UUID NOT NULL,
    "exercise_id" UUID NOT NULL,
    "day_number" INTEGER NOT NULL,
    "order" INTEGER NOT NULL,
    "sets" INTEGER,
    "reps" VARCHAR(20),
    "rest_seconds" INTEGER,
    "notes" VARCHAR,

    CONSTRAINT "routine_exercises_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "member_routines" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "member_id" UUID NOT NULL,
    "business_id" UUID NOT NULL,
    "routine_id" UUID NOT NULL,
    "assigned_by" UUID NOT NULL,
    "start_date" DATE NOT NULL,
    "end_date" DATE,
    "active" BOOLEAN NOT NULL DEFAULT true,
    "notes" VARCHAR,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "member_routines_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "member_physicals" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "member_id" UUID NOT NULL,
    "business_id" UUID NOT NULL,
    "recorded_at" DATE NOT NULL,
    "weight_kg" DECIMAL(5,2),
    "height_cm" DECIMAL(5,2),
    "body_fat_pct" DECIMAL(5,2),
    "muscle_kg" DECIMAL(5,2),
    "bmi" DECIMAL(5,2),
    "notes" VARCHAR,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,

    CONSTRAINT "member_physicals_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "body_measurements" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "member_physical_id" UUID NOT NULL,
    "part" "BodyPart" NOT NULL,
    "value_cm" DECIMAL(5,2) NOT NULL,

    CONSTRAINT "body_measurements_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "habits" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "business_id" UUID,
    "name" VARCHAR(150) NOT NULL,
    "description" TEXT,
    "category" "HabitCategory" NOT NULL,
    "icon" VARCHAR(100),
    "unit" VARCHAR(50),
    "target_value" DECIMAL(8,2),
    "active" BOOLEAN NOT NULL DEFAULT true,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_by" UUID NOT NULL,

    CONSTRAINT "habits_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "user_habits" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "user_id" UUID NOT NULL,
    "habit_id" UUID NOT NULL,
    "target_value" DECIMAL(8,2),
    "reminder_time" VARCHAR(5),
    "active" BOOLEAN NOT NULL DEFAULT true,
    "started_at" DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "created_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "user_habits_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "habit_logs" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "user_habit_id" UUID NOT NULL,
    "date" DATE NOT NULL,
    "completed" BOOLEAN NOT NULL DEFAULT false,
    "value" DECIMAL(8,2),
    "notes" VARCHAR,
    "logged_at" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "habit_logs_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "gym_profiles_business_id_key" ON "gym_profiles"("business_id");

-- CreateIndex
CREATE UNIQUE INDEX "gym_branch_profiles_branch_id_key" ON "gym_branch_profiles"("branch_id");

-- CreateIndex
CREATE UNIQUE INDEX "gym_reviews_gym_profile_id_member_id_key" ON "gym_reviews"("gym_profile_id", "member_id");

-- CreateIndex
CREATE INDEX "class_schedules_gym_class_id_start_at_idx" ON "class_schedules"("gym_class_id", "start_at");

-- CreateIndex
CREATE INDEX "class_bookings_member_id_idx" ON "class_bookings"("member_id");

-- CreateIndex
CREATE UNIQUE INDEX "class_bookings_schedule_id_member_id_key" ON "class_bookings"("schedule_id", "member_id");

-- CreateIndex
CREATE UNIQUE INDEX "routine_exercises_routine_id_exercise_id_day_number_order_key" ON "routine_exercises"("routine_id", "exercise_id", "day_number", "order");

-- CreateIndex
CREATE INDEX "member_routines_member_id_active_idx" ON "member_routines"("member_id", "active");

-- CreateIndex
CREATE INDEX "member_physicals_member_id_recorded_at_idx" ON "member_physicals"("member_id", "recorded_at");

-- CreateIndex
CREATE UNIQUE INDEX "body_measurements_member_physical_id_part_key" ON "body_measurements"("member_physical_id", "part");

-- CreateIndex
CREATE UNIQUE INDEX "user_habits_user_id_habit_id_key" ON "user_habits"("user_id", "habit_id");

-- CreateIndex
CREATE UNIQUE INDEX "habit_logs_user_habit_id_date_key" ON "habit_logs"("user_habit_id", "date");

-- AddForeignKey
ALTER TABLE "gym_branch_profiles" ADD CONSTRAINT "gym_branch_profiles_gym_profile_id_fkey" FOREIGN KEY ("gym_profile_id") REFERENCES "gym_profiles"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "gym_reviews" ADD CONSTRAINT "gym_reviews_gym_profile_id_fkey" FOREIGN KEY ("gym_profile_id") REFERENCES "gym_profiles"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "equipment" ADD CONSTRAINT "equipment_branch_profile_id_fkey" FOREIGN KEY ("branch_profile_id") REFERENCES "gym_branch_profiles"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "gym_classes" ADD CONSTRAINT "gym_classes_branch_profile_id_fkey" FOREIGN KEY ("branch_profile_id") REFERENCES "gym_branch_profiles"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "class_schedules" ADD CONSTRAINT "class_schedules_gym_class_id_fkey" FOREIGN KEY ("gym_class_id") REFERENCES "gym_classes"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "class_bookings" ADD CONSTRAINT "class_bookings_schedule_id_fkey" FOREIGN KEY ("schedule_id") REFERENCES "class_schedules"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "routine_exercises" ADD CONSTRAINT "routine_exercises_routine_id_fkey" FOREIGN KEY ("routine_id") REFERENCES "routines"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "routine_exercises" ADD CONSTRAINT "routine_exercises_exercise_id_fkey" FOREIGN KEY ("exercise_id") REFERENCES "exercises"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "member_routines" ADD CONSTRAINT "member_routines_routine_id_fkey" FOREIGN KEY ("routine_id") REFERENCES "routines"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "body_measurements" ADD CONSTRAINT "body_measurements_member_physical_id_fkey" FOREIGN KEY ("member_physical_id") REFERENCES "member_physicals"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "user_habits" ADD CONSTRAINT "user_habits_habit_id_fkey" FOREIGN KEY ("habit_id") REFERENCES "habits"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "habit_logs" ADD CONSTRAINT "habit_logs_user_habit_id_fkey" FOREIGN KEY ("user_habit_id") REFERENCES "user_habits"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
