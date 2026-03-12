import { useEffect, useState } from 'react';
import { Plus, Search, Loader2, ShoppingCart } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { luminiApi } from '@/services/luminiApi';

interface Purchase {
  id: string;
  supplierName: string;
  itemCount: number;
  total: number;
  currency: string;
  status: string;
  date: string;
}

const STATUS_CLS: Record<string, string> = {
  CONFIRMED: 'bg-green-100 text-green-700',
  COMPLETED: 'bg-green-100 text-green-700',
  DRAFT:     'bg-gray-100 text-gray-600',
  PENDING:   'bg-amber-100 text-amber-700',
  CANCELLED: 'bg-red-100 text-red-700',
};

const STATUS_LABEL: Record<string, string> = {
  CONFIRMED: 'RECIBIDO',
  COMPLETED: 'RECIBIDO',
  DRAFT:     'BORRADOR',
  PENDING:   'PENDIENTE',
  CANCELLED: 'CANCELADO',
};

function formatDate(d: string) {
  try { return new Date(d).toLocaleDateString('es-BO', { dateStyle: 'medium' }); }
  catch { return d; }
}

export function PurchasesSection() {
  const [purchases, setPurchases] = useState<Purchase[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  useEffect(() => { load(); }, []);

  async function load() {
    setLoading(true);
    try {
      const { data } = await luminiApi.get('/orders', { params: { type: 'PURCHASE' } });
      const orders = Array.isArray(data) ? data : [];
      setPurchases(orders.map((o: any) => ({
        id: o.id?.slice(0, 8) ?? '',
        supplierName: o.customerName || o.supplierName || 'Proveedor',
        itemCount: o.items?.length ?? 0,
        total: Number(o.totalAmount || 0),
        currency: o.currency || 'BOB',
        status: o.status || 'DRAFT',
        date: o.createdAt || '',
      })));
    } catch {
      setPurchases([]);
    } finally {
      setLoading(false);
    }
  }

  const filtered = purchases.filter((o) =>
    o.supplierName.toLowerCase().includes(search.toLowerCase()) || o.id.includes(search),
  );

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <Loader2 className="w-6 h-6 animate-spin text-violet-500" />
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex gap-3 items-center">
        <div className="relative flex-1 max-w-xs">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-gray-400" />
          <Input placeholder="Buscar orden..." value={search} onChange={(e) => setSearch(e.target.value)} className="pl-8 h-9 text-sm" />
        </div>
        <Button size="sm" className="bg-violet-600 hover:bg-violet-700 text-white">
          <Plus className="w-4 h-4 mr-1" /> Nueva compra
        </Button>
      </div>

      <div className="bg-white rounded-xl border border-gray-100 overflow-hidden">
        <Table>
          <TableHeader>
            <TableRow className="bg-gray-50 hover:bg-gray-50">
              <TableHead className="text-xs font-semibold text-gray-500">Orden</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500">Proveedor</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500">Fecha</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-center">Items</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-right">Total</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-center">Estado</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {filtered.length === 0 ? (
              <TableRow>
                <TableCell colSpan={6} className="text-center py-12 text-gray-400 text-sm">
                  <ShoppingCart className="w-8 h-8 mx-auto mb-2 text-gray-200" />
                  Sin órdenes de compra
                </TableCell>
              </TableRow>
            ) : filtered.map((o) => (
              <TableRow key={o.id} className="hover:bg-gray-50 cursor-pointer">
                <TableCell className="font-mono text-sm font-semibold text-violet-600">{o.id}</TableCell>
                <TableCell className="text-sm text-gray-700">{o.supplierName}</TableCell>
                <TableCell className="text-sm text-gray-500">{formatDate(o.date)}</TableCell>
                <TableCell className="text-center text-sm text-gray-700">{o.itemCount}</TableCell>
                <TableCell className="text-right font-semibold text-sm text-gray-800">Bs {o.total.toLocaleString()}</TableCell>
                <TableCell className="text-center">
                  <Badge className={`${STATUS_CLS[o.status] ?? 'bg-gray-100 text-gray-600'} text-[10px]`}>
                    {STATUS_LABEL[o.status] ?? o.status}
                  </Badge>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    </div>
  );
}
