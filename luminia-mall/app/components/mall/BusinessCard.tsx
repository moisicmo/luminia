import { MapPin, Star } from 'lucide-react';
import type { BusinessModel } from '@/models';
import { cn } from '@/lib/utils';

// Gradient palettes by type for the cover placeholder
const COVER_GRADIENTS: Record<string, string> = {
  GYM:         'from-orange-400 to-red-500',
  STORE:       'from-pink-400 to-rose-500',
  RESTAURANT:  'from-amber-400 to-orange-500',
  IT_SERVICES: 'from-blue-400 to-indigo-600',
  PHARMACY:    'from-green-400 to-emerald-500',
  BOOKSTORE:   'from-yellow-400 to-amber-500',
  HARDWARE:    'from-slate-400 to-gray-600',
  BEAUTY:      'from-purple-400 to-pink-500',
  EDUCATION:   'from-indigo-400 to-violet-600',
  NATURIST:    'from-emerald-400 to-teal-500',
  OTHER:       'from-gray-300 to-gray-500',
};

const TYPE_ICONS: Record<string, string> = {
  GYM: '🏋️', STORE: '👗', RESTAURANT: '🍕', IT_SERVICES: '💻',
  PHARMACY: '💊', BOOKSTORE: '📚', HARDWARE: '🔧', BEAUTY: '💅',
  EDUCATION: '🎓', NATURIST: '🌿', OTHER: '🏪',
};

interface Props {
  business: BusinessModel;
}

export const BusinessCard = ({ business }: Props) => {
  const gradient = COVER_GRADIENTS[business.type] ?? COVER_GRADIENTS.OTHER;
  const icon = TYPE_ICONS[business.type] ?? '🏪';

  return (
    <article className="bg-white rounded-2xl border border-gray-100 shadow-sm hover:shadow-md transition-all overflow-hidden group cursor-pointer">
      {/* Cover */}
      <div className={cn('relative h-32 bg-gradient-to-br', gradient)}>
        {/* Badges */}
        <div className="absolute top-2 left-2 flex gap-1.5">
          {business.isFeatured && (
            <span className="px-2 py-0.5 rounded-full bg-white/90 text-[10px] font-semibold text-amber-600">
              ⭐ Destacado
            </span>
          )}
          {business.isNew && (
            <span className="px-2 py-0.5 rounded-full bg-violet-600 text-[10px] font-semibold text-white">
              Nuevo
            </span>
          )}
        </div>

        {/* Status dot */}
        <div className="absolute top-2 right-2 flex items-center gap-1 px-2 py-0.5 rounded-full bg-white/90">
          <span className={cn('w-1.5 h-1.5 rounded-full', business.isOpen ? 'bg-green-500' : 'bg-gray-400')} />
          <span className="text-[10px] font-medium text-gray-600">
            {business.isOpen ? 'Abierto' : 'Cerrado'}
          </span>
        </div>

        {/* Business icon */}
        <div className="absolute -bottom-5 left-4 w-10 h-10 rounded-xl bg-white shadow-md flex items-center justify-center text-xl border border-gray-100">
          {icon}
        </div>
      </div>

      {/* Content */}
      <div className="pt-7 px-4 pb-4">
        <h3 className="font-semibold text-gray-900 text-sm leading-tight group-hover:text-violet-700 transition-colors">
          {business.name}
        </h3>
        <p className="text-[11px] text-violet-600 font-medium mt-0.5">{business.category}</p>
        <p className="text-xs text-gray-500 mt-1.5 line-clamp-2 leading-relaxed">
          {business.description}
        </p>

        {/* Footer */}
        <div className="flex items-center justify-between mt-3 pt-3 border-t border-gray-50">
          {/* City */}
          <div className="flex items-center gap-1 text-gray-400">
            <MapPin className="w-3 h-3" />
            <span className="text-[11px]">{business.city}</span>
          </div>

          {/* Rating */}
          {business.rating && (
            <div className="flex items-center gap-1">
              <Star className="w-3 h-3 text-amber-400 fill-amber-400" />
              <span className="text-[11px] font-medium text-gray-700">{business.rating}</span>
              <span className="text-[11px] text-gray-400">({business.reviewCount})</span>
            </div>
          )}
        </div>

        {/* Tags */}
        {business.tags && business.tags.length > 0 && (
          <div className="flex flex-wrap gap-1 mt-2">
            {business.tags.slice(0, 3).map((tag) => (
              <span key={tag} className="px-2 py-0.5 rounded-full bg-gray-50 text-[10px] text-gray-500 border border-gray-100">
                {tag}
              </span>
            ))}
          </div>
        )}
      </div>
    </article>
  );
};
