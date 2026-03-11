import { useDispatch, useSelector } from 'react-redux';
import type { RootState } from '@/store';
import {
  setBusinesses,
  setLoading,
  setSelectedType,
  setSelectedCity,
  setSearchQuery,
} from '@/store';
import { mallApi } from '@/services/mallApi';
import { BusinessType, type BusinessCategory, type BusinessModel } from '@/models';

// ─── Static categories (business types) ────────────────────────────────────────
export const BUSINESS_CATEGORIES: BusinessCategory[] = [
  { id: '1', label: 'Gimnasios',    type: BusinessType.GYM,         icon: '🏋️', color: 'bg-orange-100 text-orange-700' },
  { id: '2', label: 'Tiendas',      type: BusinessType.STORE,        icon: '👗', color: 'bg-pink-100 text-pink-700' },
  { id: '3', label: 'Restaurantes', type: BusinessType.RESTAURANT,   icon: '🍕', color: 'bg-red-100 text-red-700' },
  { id: '4', label: 'Servicios TI', type: BusinessType.IT_SERVICES,  icon: '💻', color: 'bg-blue-100 text-blue-700' },
  { id: '5', label: 'Farmacias',    type: BusinessType.PHARMACY,     icon: '💊', color: 'bg-green-100 text-green-700' },
  { id: '6', label: 'Librerías',    type: BusinessType.BOOKSTORE,    icon: '📚', color: 'bg-yellow-100 text-yellow-700' },
  { id: '7', label: 'Ferreterías',  type: BusinessType.HARDWARE,     icon: '🔧', color: 'bg-gray-100 text-gray-700' },
  { id: '8', label: 'Belleza',      type: BusinessType.BEAUTY,       icon: '💅', color: 'bg-purple-100 text-purple-700' },
  { id: '9', label: 'Educación',    type: BusinessType.EDUCATION,    icon: '🎓', color: 'bg-indigo-100 text-indigo-700' },
  { id: '10', label: 'Naturistas',  type: BusinessType.NATURIST,     icon: '🌿', color: 'bg-emerald-100 text-emerald-700' },
];

// ─── Mock data (replace with real API when backend is ready) ────────────────
const MOCK_BUSINESSES: BusinessModel[] = [
  { id: '1',  name: 'Iron Gym',          type: BusinessType.GYM,        description: 'El gimnasio más completo de La Paz con equipos modernos', city: 'La Paz',         isOpen: true,  isFeatured: true, isNew: false, rating: 4.8, reviewCount: 234, category: 'Gimnasio',   tags: ['CrossFit', 'Pesas', 'Cardio'] },
  { id: '2',  name: 'FitLife Center',    type: BusinessType.GYM,        description: 'Clases grupales, spinning y zona de funcional', city: 'Cochabamba',    isOpen: true,  isFeatured: false, isNew: true,  rating: 4.5, reviewCount: 87,  category: 'Gimnasio',   tags: ['Spinning', 'Yoga', 'Zumba'] },
  { id: '3',  name: 'Moda Bella',        type: BusinessType.STORE,      description: 'Ropa de mujer de las últimas tendencias internacionales', city: 'La Paz',         isOpen: true,  isFeatured: true, isNew: false, rating: 4.6, reviewCount: 156, category: 'Moda',       tags: ['Mujer', 'Casual', 'Formal'] },
  { id: '4',  name: 'TechBolivia VPS',   type: BusinessType.IT_SERVICES,description: 'Hosting, VPS, dominios y servicios cloud para tu negocio', city: 'La Paz',         isOpen: true,  isFeatured: true, isNew: false, rating: 4.9, reviewCount: 412, category: 'TI',         tags: ['VPS', 'Hosting', 'Cloud'] },
  { id: '5',  name: 'Pizzería El Horno', type: BusinessType.RESTAURANT, description: 'Las mejores pizzas artesanales con ingredientes frescos', city: 'Santa Cruz',    isOpen: false, isFeatured: false, isNew: false, rating: 4.3, reviewCount: 298, category: 'Comida',     tags: ['Pizza', 'Italiana', 'Delivery'] },
  { id: '6',  name: 'Farmacia Del Barrio', type: BusinessType.PHARMACY, description: 'Medicamentos, perfumería y productos de salud 24/7', city: 'Cochabamba',    isOpen: true,  isFeatured: false, isNew: false, rating: 4.4, reviewCount: 67,  category: 'Salud',      tags: ['24hrs', 'Genéricos', 'Delivery'] },
  { id: '7',  name: 'Librería Minerva',  type: BusinessType.BOOKSTORE,  description: 'Libros universitarios, útiles escolares y papelería', city: 'La Paz',         isOpen: true,  isFeatured: false, isNew: false, rating: 4.2, reviewCount: 43,  category: 'Libros',     tags: ['Universitario', 'Escolar'] },
  { id: '8',  name: 'Beauty Studio',     type: BusinessType.BEAUTY,     description: 'Salón de belleza, uñas, maquillaje profesional y spa', city: 'Santa Cruz',    isOpen: true,  isFeatured: true, isNew: true,  rating: 4.7, reviewCount: 189, category: 'Belleza',    tags: ['Uñas', 'Maquillaje', 'Spa'] },
  { id: '9',  name: 'Academia Sigma',    type: BusinessType.EDUCATION,  description: 'Cursos de programación, diseño y marketing digital', city: 'La Paz',         isOpen: true,  isFeatured: false, isNew: true,  rating: 4.6, reviewCount: 112, category: 'Educación',  tags: ['Programación', 'Diseño', 'Online'] },
  { id: '10', name: 'ServerMax',         type: BusinessType.IT_SERVICES,description: 'Desarrollo web, aplicaciones móviles y consultoría IT', city: 'Cochabamba',    isOpen: true,  isFeatured: false, isNew: false, rating: 4.8, reviewCount: 76,  category: 'TI',         tags: ['Web', 'Apps', 'Consultoría'] },
  { id: '11', name: 'Naturalia',         type: BusinessType.NATURIST,   description: 'Productos naturales, hierbas medicinales y suplementos', city: 'La Paz',        isOpen: true,  isFeatured: false, isNew: false, rating: 4.1, reviewCount: 34,  category: 'Salud',      tags: ['Orgánico', 'Hierbas', 'Suplementos'] },
  { id: '12', name: 'Ferretería Central', type: BusinessType.HARDWARE,  description: 'Todo en construcción, herramientas y materiales', city: 'Santa Cruz',    isOpen: true,  isFeatured: false, isNew: false, rating: 4.0, reviewCount: 28,  category: 'Construcción', tags: ['Herramientas', 'Materiales'] },
];

export const useMallStore = () => {
  const dispatch = useDispatch();
  const { businesses, filteredBusinesses, selectedType, selectedCity, searchQuery, loading } =
    useSelector((state: RootState) => state.mall);

  const loadBusinesses = async () => {
    dispatch(setLoading(true));
    try {
      // TODO: replace with real API call when gateway endpoint is ready
      // const { data } = await mallApi.get('/businesses/public');
      // dispatch(setBusinesses(data));
      await new Promise((r) => setTimeout(r, 400)); // simula latencia
      dispatch(setBusinesses(MOCK_BUSINESSES));
    } catch {
      dispatch(setBusinesses(MOCK_BUSINESSES));
    } finally {
      dispatch(setLoading(false));
    }
  };

  const filterByType = (type: BusinessType | null) => dispatch(setSelectedType(type));
  const filterByCity = (city: string | null) => dispatch(setSelectedCity(city));
  const search = (q: string) => dispatch(setSearchQuery(q));

  return {
    businesses,
    filteredBusinesses,
    selectedType,
    selectedCity,
    searchQuery,
    loading,
    categories: BUSINESS_CATEGORIES,
    loadBusinesses,
    filterByType,
    filterByCity,
    search,
  };
};
