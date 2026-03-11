import { type BusinessCategory, type BusinessType } from '@/models';
import { cn } from '@/lib/utils';

interface Props {
  categories: BusinessCategory[];
  selected: BusinessType | null;
  onSelect: (type: BusinessType | null) => void;
}

export const BusinessTypeGrid = ({ categories, selected, onSelect }: Props) => {
  return (
    <section className="max-w-7xl mx-auto px-4 py-8">
      <h2 className="text-lg font-semibold text-gray-800 mb-4">Explorar por categoría</h2>

      <div className="grid grid-cols-5 sm:grid-cols-10 gap-3">
        {/* All button */}
        <button
          onClick={() => onSelect(null)}
          className={cn(
            'flex flex-col items-center gap-1.5 p-3 rounded-xl border transition-all',
            selected === null
              ? 'border-violet-400 bg-violet-50 shadow-sm'
              : 'border-gray-100 bg-white hover:border-violet-200 hover:bg-violet-50/50',
          )}
        >
          <span className="text-2xl">🏪</span>
          <span className="text-[11px] font-medium text-gray-600 text-center leading-tight">Todos</span>
        </button>

        {categories.map((cat) => (
          <button
            key={cat.id}
            onClick={() => onSelect(selected === cat.type ? null : cat.type)}
            className={cn(
              'flex flex-col items-center gap-1.5 p-3 rounded-xl border transition-all',
              selected === cat.type
                ? 'border-violet-400 bg-violet-50 shadow-sm'
                : 'border-gray-100 bg-white hover:border-violet-200 hover:bg-violet-50/50',
            )}
          >
            <span className="text-2xl">{cat.icon}</span>
            <span className="text-[11px] font-medium text-gray-600 text-center leading-tight">{cat.label}</span>
          </button>
        ))}
      </div>
    </section>
  );
};
