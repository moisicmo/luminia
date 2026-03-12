import { useEffect, useState } from 'react';
import { TrendingUp, Package, Building2, ShoppingCart, Loader2 } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { luminiApi } from '@/services/luminiApi';

interface Stats {
  salesToday: number;
  salesCount: number;
  productCount: number;
  branchCount: number;
  pendingOrders: number;
}

interface RecentOrder {
  id: string;
  customerName: string;
  itemCount: number;
  totalAmount: number;
  status: string;
  createdAt: string;
}

interface LowStockProduct {
  name: string;
  stock: number;
  minStock: number;
}

const STATUS_COLORS: Record<string, string> = {
  CONFIRMED: 'bg-green-100 text-green-700',
  COMPLETED: 'bg-green-100 text-green-700',
  DRAFT:     'bg-amber-100 text-amber-700',
  PENDING:   'bg-amber-100 text-amber-700',
  CANCELLED: 'bg-red-100 text-red-700',
};

const STATUS_LABELS: Record<string, string> = {
  CONFIRMED: 'PAGADO',
  COMPLETED: 'COMPLETADO',
  DRAFT:     'BORRADOR',
  PENDING:   'PENDIENTE',
  CANCELLED: 'CANCELADO',
};

function timeAgo(dateStr: string) {
  const diff = Date.now() - new Date(dateStr).getTime();
  const mins = Math.floor(diff / 60000);
  if (mins < 1) return 'ahora';
  if (mins < 60) return `hace ${mins} min`;
  const hrs = Math.floor(mins / 60);
  if (hrs < 24) return `hace ${hrs}h`;
  return `hace ${Math.floor(hrs / 24)}d`;
}

export function DashboardSection() {
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState<Stats>({ salesToday: 0, salesCount: 0, productCount: 0, branchCount: 0, pendingOrders: 0 });
  const [recentOrders, setRecentOrders] = useState<RecentOrder[]>([]);
  const [lowStock, setLowStock] = useState<LowStockProduct[]>([]);

  useEffect(() => {
    loadDashboard();
  }, []);

  async function loadDashboard() {
    setLoading(true);
    try {
      const [productsRes, ordersRes] = await Promise.allSettled([
        luminiApi.get('/inventory/products'),
        luminiApi.get('/orders'),
      ]);

      let products: any[] = [];
      if (productsRes.status === 'fulfilled') products = productsRes.value.data ?? [];

      let orders: any[] = [];
      if (ordersRes.status === 'fulfilled') orders = ordersRes.value.data ?? [];

      const today = new Date().toISOString().slice(0, 10);
      const todayOrders = orders.filter((o: any) => o.createdAt?.slice(0, 10) === today);
      const salesToday = todayOrders
        .filter((o: any) => o.status === 'CONFIRMED' || o.status === 'COMPLETED')
        .reduce((sum: number, o: any) => sum + Number(o.totalAmount || 0), 0);

      setStats({
        salesToday,
        salesCount: todayOrders.length,
        productCount: products.length,
        branchCount: 1,
        pendingOrders: orders.filter((o: any) => o.status === 'DRAFT' || o.status === 'PENDING').length,
      });

      const sorted = [...orders].sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
      setRecentOrders(sorted.slice(0, 5).map((o: any) => ({
        id: o.id?.slice(0, 8) ?? '',
        customerName: o.customerName || 'Cliente',
        itemCount: o.items?.length ?? o.itemCount ?? 0,
        totalAmount: Number(o.totalAmount || 0),
        status: o.status || 'DRAFT',
        createdAt: o.createdAt || new Date().toISOString(),
      })));

      setLowStock(
        products
          .filter((p: any) => {
            const stock = Number(p.stock ?? p.currentStock ?? 0);
            const min = Number(p.minStock ?? 5);
            return stock <= min && p.active !== false;
          })
          .slice(0, 5)
          .map((p: any) => ({
            name: p.name,
            stock: Number(p.stock ?? p.currentStock ?? 0),
            minStock: Number(p.minStock ?? 5),
          })),
      );
    } catch {
      // Stats will show 0 on error
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <Loader2 className="w-6 h-6 animate-spin text-violet-500" />
      </div>
    );
  }

  const STAT_CARDS = [
    { label: 'Ventas hoy',    value: `Bs ${stats.salesToday.toLocaleString()}`, sub: `${stats.salesCount} ventas`, icon: TrendingUp,  color: 'text-green-600',  bg: 'bg-green-50' },
    { label: 'Productos',     value: String(stats.productCount),                sub: 'registrados',               icon: Package,     color: 'text-blue-600',   bg: 'bg-blue-50' },
    { label: 'Sucursales',    value: String(stats.branchCount),                 sub: 'activas',                   icon: Building2,   color: 'text-violet-600', bg: 'bg-violet-50' },
    { label: 'Pedidos pend.', value: String(stats.pendingOrders),               sub: 'por confirmar',             icon: ShoppingCart, color: 'text-amber-600',  bg: 'bg-amber-50' },
  ];

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
        {STAT_CARDS.map((s) => {
          const Icon = s.icon;
          return (
            <Card key={s.label} className="border-gray-100">
              <CardContent className="p-4">
                <div className="flex items-start justify-between">
                  <div>
                    <p className="text-xs text-gray-500 font-medium">{s.label}</p>
                    <p className="text-2xl font-bold text-gray-900 mt-1">{s.value}</p>
                    <p className="text-xs text-gray-400 mt-0.5">{s.sub}</p>
                  </div>
                  <div className={`w-9 h-9 rounded-xl ${s.bg} flex items-center justify-center shrink-0`}>
                    <Icon className={`w-4 h-4 ${s.color}`} />
                  </div>
                </div>
              </CardContent>
            </Card>
          );
        })}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <Card className="lg:col-span-2 border-gray-100">
          <CardHeader className="pb-3">
            <CardTitle className="text-sm font-semibold text-gray-700">Ventas recientes</CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            {recentOrders.length === 0 ? (
              <div className="flex flex-col items-center py-8 text-gray-400">
                <ShoppingCart className="w-8 h-8 mb-2 text-gray-200" />
                <p className="text-sm">Sin ventas registradas</p>
              </div>
            ) : (
              <div className="divide-y divide-gray-50">
                {recentOrders.map((order) => (
                  <div key={order.id} className="flex items-center gap-3 px-5 py-3">
                    <div className="flex-1 min-w-0">
                      <p className="text-xs font-semibold text-gray-800">{order.customerName}</p>
                      <p className="text-xs text-gray-400 truncate">{order.itemCount} ítem(s)</p>
                    </div>
                    <div className="text-right shrink-0">
                      <p className="text-xs font-bold text-gray-900">Bs {order.totalAmount.toLocaleString()}</p>
                      <p className="text-[10px] text-gray-400">{timeAgo(order.createdAt)}</p>
                    </div>
                    <span className={`text-[10px] font-semibold px-2 py-0.5 rounded-full shrink-0 ${STATUS_COLORS[order.status] ?? 'bg-gray-100 text-gray-600'}`}>
                      {STATUS_LABELS[order.status] ?? order.status}
                    </span>
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>

        <Card className="border-gray-100">
          <CardHeader className="pb-3">
            <CardTitle className="text-sm font-semibold text-gray-700 flex items-center gap-2">
              Stock bajo
              {lowStock.length > 0 && (
                <Badge variant="destructive" className="text-[10px] px-1.5 py-0">{lowStock.length}</Badge>
              )}
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            {lowStock.length === 0 ? (
              <p className="text-sm text-gray-400 text-center py-4">Todo en orden</p>
            ) : (
              lowStock.map((item) => (
                <div key={item.name}>
                  <div className="flex justify-between items-center mb-1">
                    <p className="text-xs font-medium text-gray-700 truncate">{item.name}</p>
                    <span className="text-xs font-bold text-red-600 ml-2 shrink-0">{item.stock} uds</span>
                  </div>
                  <div className="h-1.5 bg-gray-100 rounded-full overflow-hidden">
                    <div
                      className="h-full bg-red-400 rounded-full"
                      style={{ width: `${Math.min(100, item.minStock > 0 ? (item.stock / item.minStock) * 100 : 0)}%` }}
                    />
                  </div>
                  <p className="text-[10px] text-gray-400 mt-0.5">Mín: {item.minStock}</p>
                </div>
              ))
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
