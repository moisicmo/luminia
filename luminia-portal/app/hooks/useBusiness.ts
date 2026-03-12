import { useEffect, useState } from 'react';
import { luminiApi } from '@/services/luminiApi';

// Luminia's own systemId (root URL, no subdomain)
const LUMINIA_SYSTEM_ID = '00000000-0000-0000-0000-000000000002';
const LUMINIA_BUSINESS_ID = '00000000-0000-0000-0000-000000000003';

export interface BusinessContext {
  businessId: string;
  systemId: string;
  name: string;
  slug: string | null;
}

function detectSlug(): string | null {
  if (typeof window === 'undefined') return null;

  // Dev: ?subdomain=mitienda
  const params = new URLSearchParams(window.location.search);
  const qp = params.get('subdomain');
  if (qp) return qp;

  // Prod: mitienda.luminia.com
  const host = window.location.hostname;
  const parts = host.split('.');
  if (parts.length === 3 && parts[1] === 'luminia' && parts[0] !== 'www') {
    return parts[0];
  }

  return null;
}

export const useBusiness = () => {
  const slug = detectSlug();

  const [business, setBusiness] = useState<BusinessContext | null>(
    slug
      ? null
      : {
          businessId: LUMINIA_BUSINESS_ID,
          systemId: LUMINIA_SYSTEM_ID,
          name: 'Luminia',
          slug: null,
        },
  );
  const [loading, setLoading] = useState(!!slug);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!slug) return;
    setLoading(true);
    luminiApi
      .get<{ businessId: string; systemId: string | null; name: string; url: string }>(
        `/business/resolve?url=${encodeURIComponent(slug)}`,
      )
      .then(({ data }) => {
        setBusiness({
          businessId: data.businessId,
          systemId: data.systemId ?? LUMINIA_SYSTEM_ID,
          name: data.name,
          slug,
        });
      })
      .catch(() => {
        setError('No se encontró el negocio para esta URL');
      })
      .finally(() => setLoading(false));
  }, [slug]);

  return { business, loading, error };
};
