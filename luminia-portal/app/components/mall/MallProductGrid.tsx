import { Loader2, PackageSearch } from 'lucide-react';
import type { MallProduct } from '@/store/mall/mallSlice';
import { MallProductCard } from './MallProductCard';

interface Props {
  products: MallProduct[];
  loading: boolean;
  title: string;
}

export const MallProductGrid = ({ products, loading, title }: Props) => {
  return (
    <section className="max-w-7xl mx-auto px-4 pb-12">
      <h2 className="text-lg font-semibold text-gray-800 mb-4">{title}</h2>

      {loading ? (
        <div className="flex items-center justify-center py-20">
          <Loader2 className="w-8 h-8 animate-spin text-violet-500" />
        </div>
      ) : products.length === 0 ? (
        <div className="text-center py-20">
          <PackageSearch className="w-12 h-12 text-gray-200 mx-auto mb-3" />
          <p className="text-gray-400 text-sm">No se encontraron productos</p>
        </div>
      ) : (
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
          {products.map((product) => (
            <MallProductCard key={product.id} product={product} />
          ))}
        </div>
      )}
    </section>
  );
};
