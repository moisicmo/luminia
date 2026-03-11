import {
  isRouteErrorResponse,
  Links,
  Meta,
  Outlet,
  Scripts,
  ScrollRestoration,
} from 'react-router';
import { Provider } from 'react-redux';
import { store } from './store';
import type { Route } from './+types/root';
import { StorefrontView } from './routes/storefront/storefront.view';
import './app.css';

export const links: Route.LinksFunction = () => [
  { rel: 'preconnect', href: 'https://fonts.googleapis.com' },
  { rel: 'preconnect', href: 'https://fonts.gstatic.com', crossOrigin: 'anonymous' },
  {
    rel: 'stylesheet',
    href: 'https://fonts.googleapis.com/css2?family=Inter:ital,opsz,wght@0,14..32,100..900;1,14..32,100..900&display=swap',
  },
];

/** Detect business slug from subdomain or ?subdomain= query param (for dev). */
function detectSubdomain(): string | null {
  if (typeof window === 'undefined') return null;

  // Dev: ?subdomain=mitienda
  const params = new URLSearchParams(window.location.search);
  const qp = params.get('subdomain');
  if (qp) return qp;

  // Prod: mitienda.luminia.com
  const host = window.location.hostname; // e.g. "mitienda.luminia.com"
  const parts = host.split('.');
  // Only treat as subdomain if it's x.luminia.com (3 parts) and not www
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

export default function App() {
  const subdomain = detectSubdomain();

  return (
    <Provider store={store}>
      {subdomain ? <StorefrontView slug={subdomain} /> : <Outlet />}
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
