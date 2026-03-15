const BACKEND_URL = import.meta.env.VITE_HOST_BACKEND ?? 'http://localhost:3000';

/**
 * Resolve a file URL (e.g. `/api/files/assets/xxx.png`) to an absolute URL
 * pointing to the backend. Handles both relative and absolute URLs.
 */
export function assetUrl(url: string | undefined | null): string {
  if (!url) return '';
  if (url.startsWith('blob:') || url.startsWith('data:') || url.startsWith('http')) return url;
  return `${BACKEND_URL}${url}`;
}
