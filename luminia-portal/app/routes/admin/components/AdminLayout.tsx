import { useState } from 'react';
import {
  LayoutDashboard, Package, Tag, Boxes, ShoppingCart, TrendingUp,
  Building2, ArrowLeftRight, ClipboardList, Users, Menu, X, LogOut,
  ChevronRight, Store,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Sheet, SheetContent, SheetTrigger } from '@/components/ui/sheet';
import { ScrollArea } from '@/components/ui/scroll-area';
import { cn } from '@/lib/utils';

export type AdminSection =
  | 'dashboard' | 'products' | 'categories' | 'inventory'
  | 'purchases' | 'sales' | 'branches' | 'transfers' | 'kardex' | 'team';

interface NavItem {
  id: AdminSection;
  label: string;
  icon: React.ElementType;
  group?: string;
}

const NAV_ITEMS: NavItem[] = [
  { id: 'dashboard',  label: 'Dashboard',   icon: LayoutDashboard },
  { id: 'products',   label: 'Productos',   icon: Package,        group: 'Catálogo' },
  { id: 'categories', label: 'Categorías',  icon: Tag,            group: 'Catálogo' },
  { id: 'inventory',  label: 'Inventario',  icon: Boxes,          group: 'Operaciones' },
  { id: 'purchases',  label: 'Compras',     icon: ShoppingCart,   group: 'Operaciones' },
  { id: 'sales',      label: 'Ventas',      icon: TrendingUp,     group: 'Operaciones' },
  { id: 'branches',   label: 'Sucursales',  icon: Building2,      group: 'Gestión' },
  { id: 'transfers',  label: 'Traspasos',   icon: ArrowLeftRight, group: 'Gestión' },
  { id: 'kardex',     label: 'Kardex',      icon: ClipboardList,  group: 'Gestión' },
  { id: 'team',       label: 'Equipo',      icon: Users,          group: 'Gestión' },
];

interface Props {
  businessName: string;
  slug: string;
  userName: string;
  section: AdminSection;
  onNavigate: (s: AdminSection) => void;
  onLogout: () => void;
  children: React.ReactNode;
}

function SidebarContent({ businessName, slug, userName, section, onNavigate, onLogout, onClose }: Omit<Props, 'children'> & { onClose?: () => void }) {
  let currentGroup = '';

  return (
    <div className="flex flex-col h-full">
      {/* Logo */}
      <div className="px-4 py-4 border-b border-gray-100">
        <div className="flex items-center gap-2.5">
          <div className="w-8 h-8 rounded-lg bg-violet-600 flex items-center justify-center shrink-0">
            <Store className="w-4 h-4 text-white" />
          </div>
          <div className="min-w-0">
            <p className="font-bold text-gray-900 text-sm truncate">{businessName}</p>
            <p className="text-[10px] text-gray-400 truncate">{slug}.luminia.com</p>
          </div>
        </div>
      </div>

      {/* Nav */}
      <ScrollArea className="flex-1 px-3 py-3">
        <nav className="space-y-0.5">
          {NAV_ITEMS.map((item) => {
            const showGroup = item.group && item.group !== currentGroup;
            if (showGroup) currentGroup = item.group!;
            const Icon = item.icon;
            const active = section === item.id;
            return (
              <div key={item.id}>
                {showGroup && (
                  <p className="text-[10px] font-semibold text-gray-400 uppercase tracking-wider px-2 pt-4 pb-1">
                    {item.group}
                  </p>
                )}
                <button
                  onClick={() => { onNavigate(item.id); onClose?.(); }}
                  className={cn(
                    'w-full flex items-center gap-2.5 px-2.5 py-2 rounded-lg text-sm font-medium transition-all',
                    active
                      ? 'bg-violet-50 text-violet-700'
                      : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900',
                  )}
                >
                  <Icon className={cn('w-4 h-4 shrink-0', active ? 'text-violet-600' : 'text-gray-400')} />
                  <span className="flex-1 text-left">{item.label}</span>
                  {active && <ChevronRight className="w-3 h-3 text-violet-400" />}
                </button>
              </div>
            );
          })}
        </nav>
      </ScrollArea>

      {/* User footer */}
      <div className="px-3 py-3 border-t border-gray-100 space-y-1">
        <a
          href={`/?subdomain=${slug}`}
          className="flex items-center gap-2 px-2.5 py-2 rounded-lg text-sm text-gray-500 hover:bg-gray-50 transition-all"
        >
          <Store className="w-4 h-4 text-gray-400" />
          Ver tienda
        </a>
        <div className="flex items-center gap-2.5 px-2.5 py-2">
          <Avatar className="w-7 h-7">
            <AvatarFallback className="text-[10px] bg-violet-100 text-violet-700">
              {userName.slice(0, 2).toUpperCase()}
            </AvatarFallback>
          </Avatar>
          <span className="flex-1 text-xs font-medium text-gray-700 truncate">{userName}</span>
          <button onClick={onLogout} className="text-gray-400 hover:text-red-500 transition-colors">
            <LogOut className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>
    </div>
  );
}

export function AdminLayout({ businessName, slug, userName, section, onNavigate, onLogout, children }: Props) {
  const [mobileOpen, setMobileOpen] = useState(false);
  const sectionLabel = NAV_ITEMS.find((i) => i.id === section)?.label ?? 'Dashboard';

  return (
    <div className="flex h-screen bg-gray-50 overflow-hidden">
      {/* Desktop sidebar */}
      <aside className="hidden lg:flex flex-col w-56 bg-white border-r border-gray-100 shrink-0">
        <SidebarContent
          businessName={businessName} slug={slug} userName={userName}
          section={section} onNavigate={onNavigate} onLogout={onLogout}
        />
      </aside>

      {/* Mobile sidebar */}
      <Sheet open={mobileOpen} onOpenChange={setMobileOpen}>
        <SheetContent side="left" className="p-0 w-56">
          <SidebarContent
            businessName={businessName} slug={slug} userName={userName}
            section={section} onNavigate={onNavigate} onLogout={onLogout}
            onClose={() => setMobileOpen(false)}
          />
        </SheetContent>
      </Sheet>

      {/* Main content */}
      <div className="flex flex-col flex-1 min-w-0 overflow-hidden">
        {/* Topbar */}
        <header className="bg-white border-b border-gray-100 px-4 h-14 flex items-center gap-3 shrink-0">
          <button
            onClick={() => setMobileOpen(true)}
            className="lg:hidden text-gray-400 hover:text-gray-600"
          >
            <Menu className="w-5 h-5" />
          </button>
          <h1 className="font-semibold text-gray-800 text-sm">{sectionLabel}</h1>
        </header>

        {/* Page content */}
        <main className="flex-1 overflow-y-auto p-4 lg:p-6">
          {children}
        </main>
      </div>
    </div>
  );
}
