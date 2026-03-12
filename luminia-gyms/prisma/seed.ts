import 'dotenv/config';
import { prisma } from '@/lib/prisma';

// UUID fijo del administrador raíz — debe coincidir con luminia-users seed
const LUMINIA_PERSON_ID = '00000000-0000-0000-0000-000000000001';

// ─── Ejercicios globales (businessId = null → disponibles para todos los gyms) ─

const EXERCISES = [
  // CHEST
  { name: 'Press de banca', muscleGroup: 'CHEST', equipment: 'Barra' },
  { name: 'Press de banca con mancuernas', muscleGroup: 'CHEST', equipment: 'Mancuernas' },
  { name: 'Aperturas con mancuernas', muscleGroup: 'CHEST', equipment: 'Mancuernas' },
  { name: 'Fondos en paralelas', muscleGroup: 'CHEST', equipment: 'Peso corporal' },
  { name: 'Cruce de poleas', muscleGroup: 'CHEST', equipment: 'Polea' },
  // BACK
  { name: 'Jalón al pecho', muscleGroup: 'BACK', equipment: 'Máquina' },
  { name: 'Remo con barra', muscleGroup: 'BACK', equipment: 'Barra' },
  { name: 'Remo con mancuerna', muscleGroup: 'BACK', equipment: 'Mancuernas' },
  { name: 'Dominadas', muscleGroup: 'BACK', equipment: 'Peso corporal' },
  { name: 'Peso muerto', muscleGroup: 'BACK', equipment: 'Barra' },
  { name: 'Remo en máquina', muscleGroup: 'BACK', equipment: 'Máquina' },
  // SHOULDERS
  { name: 'Press militar con barra', muscleGroup: 'SHOULDERS', equipment: 'Barra' },
  { name: 'Press con mancuernas', muscleGroup: 'SHOULDERS', equipment: 'Mancuernas' },
  { name: 'Elevaciones laterales', muscleGroup: 'SHOULDERS', equipment: 'Mancuernas' },
  { name: 'Pájaros con mancuernas', muscleGroup: 'SHOULDERS', equipment: 'Mancuernas' },
  { name: 'Elevaciones frontales', muscleGroup: 'SHOULDERS', equipment: 'Mancuernas' },
  // BICEPS
  { name: 'Curl con barra', muscleGroup: 'BICEPS', equipment: 'Barra' },
  { name: 'Curl con mancuernas', muscleGroup: 'BICEPS', equipment: 'Mancuernas' },
  { name: 'Curl martillo', muscleGroup: 'BICEPS', equipment: 'Mancuernas' },
  { name: 'Curl en polea baja', muscleGroup: 'BICEPS', equipment: 'Polea' },
  // TRICEPS
  { name: 'Extensiones en polea alta', muscleGroup: 'TRICEPS', equipment: 'Polea' },
  { name: 'Fondos en banco', muscleGroup: 'TRICEPS', equipment: 'Peso corporal' },
  { name: 'Press francés', muscleGroup: 'TRICEPS', equipment: 'Barra' },
  { name: 'Extensión de tríceps con mancuerna', muscleGroup: 'TRICEPS', equipment: 'Mancuernas' },
  // ABS
  { name: 'Crunch', muscleGroup: 'ABS', equipment: 'Peso corporal' },
  { name: 'Plancha', muscleGroup: 'ABS', equipment: 'Peso corporal' },
  { name: 'Elevación de piernas', muscleGroup: 'ABS', equipment: 'Peso corporal' },
  { name: 'Crunch en polea', muscleGroup: 'ABS', equipment: 'Polea' },
  { name: 'Rueda abdominal', muscleGroup: 'ABS', equipment: 'Máquina' },
  // GLUTES
  { name: 'Hip thrust', muscleGroup: 'GLUTES', equipment: 'Barra' },
  { name: 'Patada trasera en máquina', muscleGroup: 'GLUTES', equipment: 'Máquina' },
  { name: 'Abducción en máquina', muscleGroup: 'GLUTES', equipment: 'Máquina' },
  // QUADRICEPS
  { name: 'Sentadilla con barra', muscleGroup: 'QUADRICEPS', equipment: 'Barra' },
  { name: 'Prensa de piernas', muscleGroup: 'QUADRICEPS', equipment: 'Máquina' },
  { name: 'Extensiones de cuádriceps', muscleGroup: 'QUADRICEPS', equipment: 'Máquina' },
  { name: 'Zancadas', muscleGroup: 'QUADRICEPS', equipment: 'Peso corporal' },
  // HAMSTRINGS
  { name: 'Curl femoral acostado', muscleGroup: 'HAMSTRINGS', equipment: 'Máquina' },
  { name: 'Peso muerto rumano', muscleGroup: 'HAMSTRINGS', equipment: 'Barra' },
  { name: 'Curl femoral sentado', muscleGroup: 'HAMSTRINGS', equipment: 'Máquina' },
  // CALVES
  { name: 'Elevación de talones de pie', muscleGroup: 'CALVES', equipment: 'Máquina' },
  { name: 'Elevación de talones sentado', muscleGroup: 'CALVES', equipment: 'Máquina' },
  // FULL_BODY
  { name: 'Burpees', muscleGroup: 'FULL_BODY', equipment: 'Peso corporal' },
  { name: 'Thruster con barra', muscleGroup: 'FULL_BODY', equipment: 'Barra' },
  { name: 'Kettlebell swing', muscleGroup: 'FULL_BODY', equipment: 'Kettlebell' },
  // CARDIO
  { name: 'Carrera en caminadora', muscleGroup: 'CARDIO', equipment: 'Caminadora' },
  { name: 'Bicicleta estática', muscleGroup: 'CARDIO', equipment: 'Bicicleta estática' },
  { name: 'Elíptica', muscleGroup: 'CARDIO', equipment: 'Elíptica' },
  { name: 'Remo en máquina', muscleGroup: 'CARDIO', equipment: 'Máquina de remo' },
  { name: 'Saltar la cuerda', muscleGroup: 'CARDIO', equipment: 'Cuerda' },
] as const;

// ─── Hábitos globales (businessId = null → disponibles para todos los usuarios) ─

const HABITS = [
  {
    name: 'Beber 8 vasos de agua',
    description: 'Mantén una hidratación óptima bebiendo al menos 8 vasos de agua al día.',
    category: 'HYDRATION',
    icon: '💧',
    unit: 'vasos',
    targetValue: 8,
  },
  {
    name: 'Dormir 8 horas',
    description: 'Un descanso adecuado es fundamental para la recuperación muscular y la salud general.',
    category: 'SLEEP',
    icon: '😴',
    unit: 'horas',
    targetValue: 8,
  },
  {
    name: 'Cardio diario',
    description: 'Al menos 30 minutos de actividad cardiovascular para mejorar la resistencia y quemar calorías.',
    category: 'EXERCISE',
    icon: '🏃',
    unit: 'minutos',
    targetValue: 30,
  },
  {
    name: 'Comer frutas y verduras',
    description: 'Incluye al menos 5 porciones de frutas y verduras en tu dieta diaria.',
    category: 'NUTRITION',
    icon: '🥗',
    unit: 'porciones',
    targetValue: 5,
  },
  {
    name: 'Meditación diaria',
    description: 'Dedica al menos 10 minutos al día a la meditación para reducir el estrés y mejorar el enfoque.',
    category: 'MINDFULNESS',
    icon: '🧘',
    unit: 'minutos',
    targetValue: 10,
  },
  {
    name: 'Estiramiento diario',
    description: 'Realiza al menos 15 minutos de estiramiento para mejorar la flexibilidad y prevenir lesiones.',
    category: 'MOBILITY',
    icon: '🤸',
    unit: 'minutos',
    targetValue: 15,
  },
  {
    name: 'Pasos diarios',
    description: 'Camina al menos 10.000 pasos al día para mantenerte activo.',
    category: 'EXERCISE',
    icon: '👟',
    unit: 'pasos',
    targetValue: 10000,
  },
  {
    name: 'Sin azúcar añadida',
    description: 'Evita el consumo de bebidas y alimentos con azúcar añadida.',
    category: 'NUTRITION',
    icon: '🚫🍬',
    unit: 'días cumplidos',
    targetValue: 1,
  },
] as const;

async function main() {
  console.log('Seeding luminia-gyms...');

  // 1. Ejercicios globales
  let exerciseCount = 0;
  for (const ex of EXERCISES) {
    await prisma.exercise.upsert({
      where: {
        // businessId = null + name único
        id: `00000000-0000-0000-0002-${String(EXERCISES.indexOf(ex) + 1).padStart(12, '0')}`,
      },
      update: {},
      create: {
        id: `00000000-0000-0000-0002-${String(EXERCISES.indexOf(ex) + 1).padStart(12, '0')}`,
        businessId: null,
        name: ex.name,
        muscleGroup: ex.muscleGroup,
        equipment: ex.equipment,
        active: true,
        createdBy: LUMINIA_PERSON_ID,
      },
    });
    exerciseCount++;
  }
  console.log(`  Exercises: ${exerciseCount} ejercicios globales`);

  // 2. Hábitos globales
  let habitCount = 0;
  for (const h of HABITS) {
    await prisma.habit.upsert({
      where: {
        id: `00000000-0000-0000-0003-${String(HABITS.indexOf(h) + 1).padStart(12, '0')}`,
      },
      update: {},
      create: {
        id: `00000000-0000-0000-0003-${String(HABITS.indexOf(h) + 1).padStart(12, '0')}`,
        businessId: null,
        name: h.name,
        description: h.description,
        category: h.category,
        icon: h.icon,
        unit: h.unit,
        targetValue: h.targetValue,
        active: true,
        createdBy: LUMINIA_PERSON_ID,
      },
    });
    habitCount++;
  }
  console.log(`  Habits: ${habitCount} hábitos globales`);

  console.log('Done!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(() => prisma.$disconnect());
