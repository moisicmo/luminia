import { MapPin } from 'lucide-react';
import { cn } from '@/lib/utils';

const CITIES = ['La Paz', 'Cochabamba', 'Santa Cruz', 'Oruro', 'Sucre', 'Tarija', 'Trinidad', 'Cobija'];

interface Props {
  selected: string | null;
  onSelect: (city: string | null) => void;
}

export const CityBar = ({ selected, onSelect }: Props) => {
  return (
    <div className="border-b border-gray-100 bg-white sticky top-16 z-40">
      <div className="max-w-7xl mx-auto px-4">
        <div className="flex items-center gap-1 overflow-x-auto py-2 scrollbar-none">
          <MapPin className="w-3.5 h-3.5 text-gray-400 shrink-0 mr-1" />
          <button
            onClick={() => onSelect(null)}
            className={cn(
              'shrink-0 px-4 py-1.5 rounded-full text-sm font-medium transition-all whitespace-nowrap',
              selected === null
                ? 'bg-violet-600 text-white'
                : 'text-gray-500 hover:text-gray-800 hover:bg-gray-100',
            )}
          >
            Todo Bolivia
          </button>
          {CITIES.map((city) => (
            <button
              key={city}
              onClick={() => onSelect(selected === city ? null : city)}
              className={cn(
                'shrink-0 px-4 py-1.5 rounded-full text-sm font-medium transition-all whitespace-nowrap',
                selected === city
                  ? 'bg-violet-600 text-white'
                  : 'text-gray-500 hover:text-gray-800 hover:bg-gray-100',
              )}
            >
              {city}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
};
