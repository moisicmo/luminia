import { Loader2 } from 'lucide-react';
import type { BusinessModel } from '@/models';
import { BusinessCard } from './BusinessCard';

interface Props {
  businesses: BusinessModel[];
  loading: boolean;
  title?: string;
}

export const BusinessGrid = ({ businesses, loading, title }: Props) => {
  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center py-24 text-gray-400">
        <Loader2 className="w-8 h-8 animate-spin mb-3" />
        <p className="text-sm">Cargando negocios...</p>
      </div>
    );
  }

  if (businesses.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center py-24 text-gray-400">
        <span className="text-5xl mb-4">🔍</span>
        <p className="text-base font-medium text-gray-600">No encontramos resultados</p>
        <p className="text-sm mt-1">Intenta con otra categoría o ciudad</p>
      </div>
    );
  }

  return (
    <section className="max-w-7xl mx-auto px-4 pb-12">
      {title && (
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-semibold text-gray-800">{title}</h2>
          <span className="text-sm text-gray-400">{businesses.length} negocios</span>
        </div>
      )}
      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
        {businesses.map((b) => (
          <BusinessCard key={b.id} business={b} />
        ))}
      </div>
    </section>
  );
};
