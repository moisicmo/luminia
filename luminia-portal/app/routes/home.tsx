import { useEffect, useState, useRef, useCallback } from 'react';
import type { Route } from './+types/home';
import { useMallStore } from '@/hooks/useMallStore';
import {
  MallTopbar,
  HeroSearch,
  BusinessTypeGrid,
  MallProductGrid,
  MallCartDrawer,
} from '@/components/mall';
import { AuthModal } from '@/components/auth';
import { BusinessType } from '@/models';

export function meta({}: Route.MetaArgs) {
  return [
    { title: 'Luminia Mall — El marketplace de Bolivia' },
    { name: 'description', content: 'Encuentra los mejores productos de todas las tiendas de Bolivia. Ropa, electrónica, alimentos y más.' },
  ];
}

export default function Home() {
  const {
    selectedType,
    searchQuery,
    products,
    productsLoading,
    categories,
    loadProducts,
    filterByType,
    search,
  } = useMallStore();

  const [authOpen, setAuthOpen] = useState(false);
  const [authTab, setAuthTab] = useState<'login' | 'register'>('login');
  const debounceRef = useRef<ReturnType<typeof setTimeout>>(undefined);

  // Load products on mount
  useEffect(() => {
    loadProducts({ take: 40 });
  }, []);

  // Debounced search
  const handleSearch = useCallback((q: string) => {
    search(q);
    clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      loadProducts({ search: q || undefined, businessType: selectedType || undefined, take: 40 });
    }, 300);
  }, [selectedType]);

  // Filter by business type
  const handleFilterType = useCallback((type: BusinessType | null) => {
    const newType = type === selectedType ? null : type;
    filterByType(newType);
    loadProducts({ search: searchQuery || undefined, businessType: newType || undefined, take: 40 });
  }, [selectedType, searchQuery]);

  const openAuth = (tab: 'login' | 'register' = 'login') => {
    setAuthTab(tab);
    setAuthOpen(true);
  };

  const sectionTitle = selectedType
    ? (categories.find((c) => c.type === selectedType)?.label ?? 'Productos')
    : searchQuery
    ? `Resultados para "${searchQuery}"`
    : 'Todos los productos';

  return (
    <div className="min-h-screen bg-gray-50">
      <MallTopbar searchQuery={searchQuery} onSearch={handleSearch} onAuthOpen={openAuth} />

      <HeroSearch
        categories={categories}
        onSelectType={handleFilterType}
        searchQuery={searchQuery}
        onSearch={handleSearch}
      />

      <main>
        <BusinessTypeGrid
          categories={categories}
          selected={selectedType}
          onSelect={handleFilterType}
        />

        {/* Divider */}
        <div className="max-w-7xl mx-auto px-4 mb-6">
          <div className="h-px bg-gray-100" />
        </div>

        <MallProductGrid
          products={products}
          loading={productsLoading}
          title={sectionTitle}
        />
      </main>

      <MallCartDrawer />
      <AuthModal open={authOpen} onOpenChange={setAuthOpen} defaultTab={authTab} />
    </div>
  );
}
