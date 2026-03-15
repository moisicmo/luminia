import { useDispatch, useSelector } from 'react-redux';
import type { RootState } from '@/store';
import {
  setBusinesses,
  setLoading,
  setSelectedType,
  setSelectedCity,
  setSearchQuery,
  setProducts,
  setProductsTotal,
  setProductsLoading,
} from '@/store';
import { mallApi } from '@/services/mallApi';
import { BusinessType, type BusinessCategory, type BusinessModel } from '@/models';

// ─── Static categories (business types) ────────────────────────────────────────
export const BUSINESS_CATEGORIES: BusinessCategory[] = [
  { id: '1',  label: 'Tiendas',      type: BusinessType.TIENDA,          icon: '👗', color: 'bg-pink-100 text-pink-700' },
  { id: '2',  label: 'Restaurantes', type: BusinessType.RESTAURANT,      icon: '🍕', color: 'bg-red-100 text-red-700' },
  { id: '3',  label: 'Salud',        type: BusinessType.SALUD,           icon: '💊', color: 'bg-green-100 text-green-700' },
  { id: '4',  label: 'Servicios',    type: BusinessType.SERVICIO,        icon: '💻', color: 'bg-blue-100 text-blue-700' },
  { id: '5',  label: 'E-commerce',   type: BusinessType.ECOMMERCE,       icon: '🛒', color: 'bg-violet-100 text-violet-700' },
  { id: '6',  label: 'Capacitación', type: BusinessType.CAPACITACION,    icon: '📚', color: 'bg-yellow-100 text-yellow-700' },
  { id: '7',  label: 'Consultoría',  type: BusinessType.CONSULTORIA,     icon: '🔧', color: 'bg-gray-100 text-gray-700' },
  { id: '8',  label: 'Entretenim.',  type: BusinessType.ENTRETENIMIENTO, icon: '🎭', color: 'bg-purple-100 text-purple-700' },
  { id: '9',  label: 'Instituto',    type: BusinessType.INSTITUTO,       icon: '🎓', color: 'bg-indigo-100 text-indigo-700' },
  { id: '10', label: 'Hotel',        type: BusinessType.HOTEL,           icon: '🏨', color: 'bg-emerald-100 text-emerald-700' },
];

export const useMallStore = () => {
  const dispatch = useDispatch();
  const { businesses, filteredBusinesses, selectedType, selectedCity, searchQuery, loading, products, productsTotal, productsLoading } =
    useSelector((state: RootState) => state.mall);

  const loadBusinesses = async () => {
    dispatch(setLoading(true));
    try {
      const { data } = await mallApi.get<Array<{
        id: string;
        name: string;
        businessType: string;
        url: string | null;
        logo: string | null;
        city: string | null;
        address: string | null;
        phone: string | null;
      }>>('/business/public');

      const businesses: BusinessModel[] = data.map((b) => ({
        id: b.id,
        name: b.name,
        type: b.businessType as BusinessType,
        city: b.city ?? undefined,
        address: b.address ?? undefined,
        phone: b.phone ?? undefined,
        logoUrl: b.logo ?? undefined,
        website: b.url ? `${b.url}.luminia.com` : undefined,
      }));

      dispatch(setBusinesses(businesses));
    } catch {
      dispatch(setBusinesses([]));
    } finally {
      dispatch(setLoading(false));
    }
  };

  const loadProducts = async (params?: { search?: string; businessType?: string; take?: number; skip?: number }) => {
    dispatch(setProductsLoading(true));
    try {
      const { data } = await mallApi.get<{ items: any[]; total: number }>('/mall/products', { params });
      dispatch(setProducts(data.items));
      dispatch(setProductsTotal(data.total));
    } catch {
      dispatch(setProducts([]));
      dispatch(setProductsTotal(0));
    } finally {
      dispatch(setProductsLoading(false));
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
    products,
    productsTotal,
    productsLoading,
    categories: BUSINESS_CATEGORIES,
    loadBusinesses,
    loadProducts,
    filterByType,
    filterByCity,
    search,
  };
};
