import {
  isRouteErrorResponse,
  Links,
  Meta,
  Outlet,
  Scripts,
  ScrollRestoration,
} from 'react-router';
import { useEffect, useState } from 'react';
import { Provider } from 'react-redux';
import { store } from './store';
import type { Route } from './+types/root';
import { StorefrontView } from './routes/storefront/storefront.view';
import { AdminView } from './routes/admin/admin.view';
import { useAuth } from './hooks/useAuth';
import './app.css';

export const links: Route.LinksFunction = () => [
  { rel: 'preconnect', href: 'https://fonts.googleapis.com' },
  { rel: 'preconnect', href: 'https://fonts.gstatic.com', crossOrigin: 'anonymous' },
  {
    rel: 'stylesheet',
    href: 'https://fonts.googleapis.com/css2?family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&display=swap',
  },
];

/**
 * Detect business slug from subdomain or query param.
 *
 * Supports:
 * 1. ?subdomain=patito                → dev query param
 * 2. patito.lvh.me:4300               → dev via lvh.me (resolves to 127.0.0.1)
 * 3. patito.luminia.local:4300         → dev via /etc/hosts
 * 4. patito.luminia.com               → production
 */
function detectSubdomain(): string | null {
  if (typeof window === 'undefined') return null;

  // 1. Query param (always works, any environment)
  const params = new URLSearchParams(window.location.search);
  const qp = params.get('subdomain');
  if (qp) return qp;

  const host = window.location.hostname;
  const parts = host.split('.');

  // 2. Dev: patito.lvh.me (2 parts after split = subdomain + lvh + me)
  if (parts.length === 3 && parts[1] === 'lvh' && parts[2] === 'me') {
    return parts[0];
  }

  // 3. Dev: patito.luminia.local
  if (parts.length === 3 && parts[2] === 'local' && parts[0] !== 'www') {
    return parts[0];
  }

  // 4. Prod: patito.luminia.com (3 parts, not www)
  if (parts.length === 3 && parts[1] === 'luminia' && parts[0] !== 'www') {
    return parts[0];
  }

  return null;
}

export function Layout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="es">
      <head>
        <meta charSet="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <Meta />
        <Links />
      </head>
      <body>
        {children}
        <ScrollRestoration />
        <Scripts />
      </body>
    </html>
  );
}

function isAdminPath(): boolean {
  if (typeof window === 'undefined') return false;
  return window.location.pathname.startsWith('/admin');
}

function AppContent() {
  const [clientReady, setClientReady] = useState(false);
  const { checkAuth } = useAuth();

  useEffect(() => {
    // Capture token passed via URL (cross-subdomain transfer)
    if (typeof window !== 'undefined') {
      const params = new URLSearchParams(window.location.search);
      const urlToken = params.get('_token');
      if (urlToken) {
        localStorage.setItem('luminia_token', urlToken);
        // Clean the URL to remove the token param
        params.delete('_token');
        const clean = params.toString();
        const newUrl = window.location.pathname + (clean ? `?${clean}` : '') + window.location.hash;
        window.history.replaceState({}, '', newUrl);
      }
    }
    checkAuth();
    setClientReady(true);
  }, []);

  // During SSR and initial hydration, render the router outlet
  // Once mounted on client, detect subdomain and render accordingly
  if (!clientReady) return <Outlet />;

  const subdomain = detectSubdomain();
  const admin = isAdminPath();

  if (subdomain && admin) return <AdminView slug={subdomain} />;
  if (subdomain) return <StorefrontView slug={subdomain} />;
  return <Outlet />;
}

export default function App() {
  return (
    <Provider store={store}>
      <AppContent />
    </Provider>
  );
}

export function ErrorBoundary({ error }: Route.ErrorBoundaryProps) {
  let message = 'Oops!';
  let details = 'Ocurrió un error inesperado.';
  if (isRouteErrorResponse(error)) {
    message = error.status === 404 ? '404' : 'Error';
    details = error.status === 404 ? 'Página no encontrada.' : error.statusText || details;
  } else if (import.meta.env.DEV && error instanceof Error) {
    details = error.message;
  }
  return (
    <main className="flex flex-col items-center justify-center min-h-screen text-gray-600">
      <h1 className="text-4xl font-bold mb-2">{message}</h1>
      <p>{details}</p>
    </main>
  );
}
