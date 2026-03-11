import { useEffect, useState } from 'react';
import { ShoppingBag, MapPin, Star, Phone, Globe, ArrowLeft, Loader2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { mallApi } from '@/services/mallApi';

interface StorefrontBusiness {
  id: string;
  name: string;
  slug: string;
  description: string;
  city: string;
  type: string;
  logoUrl?: string;
  coverUrl?: string;
  rating?: number;
  reviewCount?: number;
  phone?: string;
  website?: string;
}

interface Props {
  slug: string;
}

export function StorefrontView({ slug }: Props) {
  const [business, setBusiness] = useState<StorefrontBusiness | null>(null);
  const [loading, setLoading] = useState(true);
  const [notFound, setNotFound] = useState(false);

  useEffect(() => {
    setLoading(true);
    mallApi.get(`/business/slug/${slug}`)
      .then(({ data }) => setBusiness(data))
      .catch(() => setNotFound(true))
      .finally(() => setLoading(false));
  }, [slug]);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Loader2 className="w-8 h-8 animate-spin text-violet-500" />
      </div>
    );
  }

  if (notFound || !business) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center gap-4 text-gray-500 p-4">
        <ShoppingBag className="w-12 h-12 text-gray-300" />
        <h1 className="text-2xl font-bold text-gray-800">Negocio no encontrado</h1>
        <p className="text-sm text-center">
          No encontramos ningún negocio con el subdominio <strong>{slug}</strong>
        </p>
        <Button variant="outline" onClick={() => window.location.href = '/'}>
          <ArrowLeft className="w-4 h-4 mr-2" />
          Ir al Mall
        </Button>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Cover */}
      <div className="h-48 sm:h-64 bg-gradient-to-br from-violet-600 via-indigo-600 to-purple-700 relative">
        {business.coverUrl && (
          <img
            src={business.coverUrl}
            alt={business.name}
            className="absolute inset-0 w-full h-full object-cover"
          />
        )}
        <div className="absolute inset-0 bg-black/30" />

        {/* Back to mall */}
        <a
          href="/"
          className="absolute top-4 left-4 flex items-center gap-1.5 text-white/90 text-sm
                     bg-black/20 hover:bg-black/30 backdrop-blur-sm rounded-full px-3 py-1.5 transition-all"
        >
          <ArrowLeft className="w-4 h-4" />
          Luminia Mall
        </a>
      </div>

      {/* Business header */}
      <div className="max-w-4xl mx-auto px-4">
        <div className="bg-white rounded-2xl shadow-sm -mt-10 relative z-10 p-6 mb-6">
          <div className="flex gap-4 items-start">
            {/* Logo / avatar */}
            <div className="w-16 h-16 sm:w-20 sm:h-20 rounded-2xl bg-gradient-to-br from-violet-500 to-indigo-600
                            flex items-center justify-center shrink-0 shadow-md">
              {business.logoUrl ? (
                <img src={business.logoUrl} alt={business.name} className="w-full h-full object-cover rounded-2xl" />
              ) : (
                <ShoppingBag className="w-8 h-8 text-white" />
              )}
            </div>

            <div className="flex-1 min-w-0">
              <h1 className="text-xl sm:text-2xl font-bold text-gray-900 truncate">{business.name}</h1>
              <p className="text-gray-500 text-sm mt-1 line-clamp-2">{business.description}</p>

              <div className="flex flex-wrap items-center gap-3 mt-3 text-sm text-gray-500">
                {business.city && (
                  <span className="flex items-center gap-1">
                    <MapPin className="w-3.5 h-3.5" />
                    {business.city}
                  </span>
                )}
                {business.rating && (
                  <span className="flex items-center gap-1 text-amber-500">
                    <Star className="w-3.5 h-3.5 fill-current" />
                    <span className="font-medium">{business.rating.toFixed(1)}</span>
                    {business.reviewCount && (
                      <span className="text-gray-400">({business.reviewCount})</span>
                    )}
                  </span>
                )}
                {business.phone && (
                  <span className="flex items-center gap-1">
                    <Phone className="w-3.5 h-3.5" />
                    {business.phone}
                  </span>
                )}
                {business.website && (
                  <a
                    href={business.website}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="flex items-center gap-1 text-violet-600 hover:underline"
                  >
                    <Globe className="w-3.5 h-3.5" />
                    Sitio web
                  </a>
                )}
              </div>
            </div>
          </div>
        </div>

        {/* Products / catalog placeholder */}
        <div className="bg-white rounded-2xl shadow-sm p-6 mb-6">
          <h2 className="text-lg font-semibold text-gray-800 mb-4">Catálogo</h2>
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
            {Array.from({ length: 8 }).map((_, i) => (
              <div key={i} className="animate-pulse">
                <div className="aspect-square bg-gray-100 rounded-xl mb-2" />
                <div className="h-3 bg-gray-100 rounded w-3/4 mb-1" />
                <div className="h-3 bg-gray-100 rounded w-1/2" />
              </div>
            ))}
          </div>
          <p className="text-center text-sm text-gray-400 mt-6">
            Pronto podrás ver los productos de este negocio
          </p>
        </div>
      </div>
    </div>
  );
}
