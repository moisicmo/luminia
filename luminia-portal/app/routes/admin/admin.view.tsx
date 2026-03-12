import { useEffect, useState } from 'react';
import { Loader2, Lock } from 'lucide-react';
import { luminiApi } from '@/services/luminiApi';
import { AdminLayout, type AdminSection } from './components/AdminLayout';
import { DashboardSection } from './sections/DashboardSection';
import { ProductsSection } from './sections/ProductsSection';
import { CategoriesSection } from './sections/CategoriesSection';
import { InventorySection } from './sections/InventorySection';
import { PurchasesSection } from './sections/PurchasesSection';
import { SalesSection } from './sections/SalesSection';
import { BranchesSection } from './sections/BranchesSection';
import { TransfersSection } from './sections/TransfersSection';
import { KardexSection } from './sections/KardexSection';
import { TeamSection } from './sections/TeamSection';

interface Props {
  slug: string;
}

interface BusinessInfo {
  id: string;
  name: string;
}

type AuthState = 'checking' | 'authorized' | 'unauthorized' | 'not-logged';

function getTokenPayload(): { sub: string; name: string } | null {
  try {
    const token = localStorage.getItem('luminia_token');
    if (!token) return null;
    const payload = JSON.parse(atob(token.split('.')[1]));
    if (payload.exp * 1000 < Date.now()) return null;
    return payload;
  } catch {
    return null;
  }
}

function getSectionFromPath(): AdminSection {
  const path = window.location.pathname;
  if (path.includes('/admin/products'))   return 'products';
  if (path.includes('/admin/categories')) return 'categories';
  if (path.includes('/admin/inventory'))  return 'inventory';
  if (path.includes('/admin/purchases'))  return 'purchases';
  if (path.includes('/admin/sales'))      return 'sales';
  if (path.includes('/admin/branches'))   return 'branches';
  if (path.includes('/admin/transfers'))  return 'transfers';
  if (path.includes('/admin/kardex'))     return 'kardex';
  if (path.includes('/admin/team'))       return 'team';
  return 'dashboard';
}

export function AdminView({ slug }: Props) {
  const [authState, setAuthState] = useState<AuthState>('checking');
  const [business, setBusiness] = useState<BusinessInfo | null>(null);
  const [userName, setUserName] = useState('');
  const [section, setSection] = useState<AdminSection>(getSectionFromPath);

  useEffect(() => {
    const payload = getTokenPayload();
    if (!payload) { setAuthState('not-logged'); return; }

    setUserName(payload.name ?? payload.sub ?? 'Admin');

    luminiApi.get('/business/resolve?url=' + slug)
      .then(({ data }) => {
        const bizId = data.businessId;
        setBusiness({ id: bizId, name: data.name });
        localStorage.setItem('luminia_business_id', bizId);
        return luminiApi.get(`/business/${bizId}/members`);
      })
      .then(({ data }) => {
        const isMember = data.some((m: any) => m.userId === payload.sub || m.personId === payload.sub);
        setAuthState(isMember ? 'authorized' : 'unauthorized');
      })
      .catch(() => setAuthState('unauthorized'));
  }, [slug]);

  const handleNavigate = (s: AdminSection) => {
    setSection(s);
    const path = s === 'dashboard' ? '/admin' : `/admin/${s}`;
    window.history.pushState({}, '', path + window.location.search);
  };

  const handleLogout = () => {
    localStorage.removeItem('luminia_token');
    localStorage.removeItem('luminia_business_id');
    window.location.href = '/';
  };

  if (authState === 'checking') {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Loader2 className="w-8 h-8 animate-spin text-violet-500" />
      </div>
    );
  }

  if (authState === 'not-logged') {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center gap-4 text-gray-500">
        <Lock className="w-10 h-10 text-gray-300" />
        <p className="font-semibold text-gray-700">Debes iniciar sesión para acceder al panel</p>
        <a href="/" className="text-sm text-violet-600 hover:underline">Ir al Mall →</a>
      </div>
    );
  }

  if (authState === 'unauthorized') {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center gap-4 text-gray-500">
        <Lock className="w-10 h-10 text-red-300" />
        <p className="font-semibold text-gray-700">No tienes acceso al panel de este negocio</p>
        <a href="/" className="text-sm text-violet-600 hover:underline">Ir al Mall →</a>
      </div>
    );
  }

  const SECTIONS: Record<AdminSection, React.ReactNode> = {
    dashboard:  <DashboardSection />,
    products:   <ProductsSection />,
    categories: <CategoriesSection />,
    inventory:  <InventorySection />,
    purchases:  <PurchasesSection />,
    sales:      <SalesSection />,
    branches:   <BranchesSection />,
    transfers:  <TransfersSection />,
    kardex:     <KardexSection />,
    team:       <TeamSection />,
  };

  return (
    <AdminLayout
      businessName={business?.name ?? slug}
      slug={slug}
      userName={userName}
      section={section}
      onNavigate={handleNavigate}
      onLogout={handleLogout}
    >
      {SECTIONS[section]}
    </AdminLayout>
  );
}
