import { useEffect, useState } from 'react';
import type { Route } from './+types/home';
import { useMallStore } from '@/hooks/useMallStore';
import {
  MallTopbar,
  HeroSearch,
  BusinessTypeGrid,
  CityBar,
  BusinessGrid,
} from '@/components/mall';
import { AuthModal } from '@/components/auth';

export function meta({}: Route.MetaArgs) {
  return [
    { title: 'Luminia Mall — Descubre negocios en Bolivia' },
    { name: 'description', content: 'El marketplace de negocios de Bolivia. Encuentra gimnasios, tiendas, restaurantes, servicios de TI y más.' },
  ];
}

export default function Home() {
  const {
    filteredBusinesses,
    selectedType,
    selectedCity,
    searchQuery,
    loading,
    categories,
    loadBusinesses,
    filterByType,
    filterByCity,
    search,
  } = useMallStore();

  const [authOpen, setAuthOpen] = useState(false);
  const [authTab, setAuthTab] = useState<'login' | 'register'>('login');

  useEffect(() => {
    loadBusinesses();
  }, []);

  const openAuth = (tab: 'login' | 'register' = 'login') => {
    setAuthTab(tab);
    setAuthOpen(true);
  };

  const sectionTitle = selectedType
    ? (categories.find((c) => c.type === selectedType)?.label ?? 'Negocios')
    : selectedCity
    ? `Negocios en ${selectedCity}`
    : searchQuery
    ? `Resultados para "${searchQuery}"`
    : 'Todos los negocios';

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Sticky topbar with inline search */}
      <MallTopbar searchQuery={searchQuery} onSearch={search} onAuthOpen={openAuth} />

      {/* Hero with big search + quick category pills */}
      <HeroSearch categories={categories} onSelectType={filterByType} />

      {/* City filter bar */}
      <CityBar selected={selectedCity} onSelect={filterByCity} />

      <main>
        {/* Business type icon grid */}
        <BusinessTypeGrid
          categories={categories}
          selected={selectedType}
          onSelect={filterByType}
        />

        {/* Divider */}
        <div className="max-w-7xl mx-auto px-4 mb-6">
          <div className="h-px bg-gray-100" />
        </div>

        {/* Business results */}
        <BusinessGrid
          businesses={filteredBusinesses}
          loading={loading}
          title={sectionTitle}
        />
      </main>

      <AuthModal open={authOpen} onOpenChange={setAuthOpen} defaultTab={authTab} />
    </div>
  );
}
