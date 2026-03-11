import { Search } from 'lucide-react';
import type { BusinessCategory } from '@/models';

interface Props {
  categories: BusinessCategory[];
  onSelectType: (type: any) => void;
}

export const HeroSearch = ({ categories, onSelectType }: Props) => {
  return (
    <section className="relative overflow-hidden bg-gradient-to-br from-violet-600 via-indigo-600 to-purple-700 py-16 px-4">
      {/* Decorative blobs */}
      <div className="absolute top-0 left-0 w-72 h-72 bg-white/5 rounded-full -translate-x-1/2 -translate-y-1/2" />
      <div className="absolute bottom-0 right-0 w-96 h-96 bg-white/5 rounded-full translate-x-1/3 translate-y-1/3" />

      <div className="relative max-w-3xl mx-auto text-center">
        <p className="text-violet-200 text-sm font-medium mb-2 tracking-wide uppercase">
          La plataforma de negocios de Bolivia
        </p>
        <h1 className="text-3xl sm:text-5xl font-bold text-white mb-4 leading-tight">
          Descubre miles de negocios<br className="hidden sm:block" /> en un solo lugar
        </h1>
        <p className="text-violet-200 mb-8 text-base sm:text-lg">
          Desde gimnasios hasta servicios cloud — todo lo que necesitas está aquí
        </p>

        {/* Search box */}
        <div className="relative max-w-xl mx-auto">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <input
            type="text"
            placeholder="¿Qué estás buscando?"
            className="w-full pl-12 pr-6 py-4 rounded-2xl text-gray-800 bg-white shadow-xl
                       focus:outline-none focus:ring-4 focus:ring-white/30 text-base placeholder-gray-400"
            readOnly
          />
        </div>

        {/* Quick category pills */}
        <div className="flex flex-wrap justify-center gap-2 mt-6">
          {categories.slice(0, 6).map((cat) => (
            <button
              key={cat.id}
              onClick={() => onSelectType(cat.type)}
              className="flex items-center gap-1.5 px-4 py-1.5 rounded-full bg-white/15 hover:bg-white/25
                         text-white text-sm font-medium transition-all backdrop-blur-sm border border-white/20"
            >
              <span>{cat.icon}</span>
              <span>{cat.label}</span>
            </button>
          ))}
        </div>
      </div>
    </section>
  );
};
