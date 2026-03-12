import { useEffect, useState } from 'react';
import { Search, Loader2, ShoppingCart } from 'lucide-react';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { luminiApi } from '@/services/luminiApi';

interface Sale {
  id: string;
  customerName: string;
  itemCount: number;
  total: number;
  currency: string;
  paymentMethod: string;
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
  CONFIRMED: 'PAGADO',
  COMPLETED: 'COMPLETADO',
  DRAFT:     'BORRADOR',
  PENDING:   'PENDIENTE',
  CANCELLED: 'CANCELADO',
};

function formatDate(d: string) {
  try { return new Date(d).toLocaleString('es-BO', { dateStyle: 'short', timeStyle: 'short' }); }
  catch { return d; }
}

export function SalesSection() {
  const [sales, setSales] = useState<Sale[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');

  useEffect(() => { load(); }, []);

  async function load() {
    setLoading(true);
    try {
      const { data } = await luminiApi.get('/orders', { params: { type: 'SALE' } });
      const orders = Array.isArray(data) ? data : [];
      setSales(orders.map((o: any) => ({
        id: o.id?.slice(0, 8) ?? '',
        customerName: o.customerName || 'Cliente',
        itemCount: o.items?.length ?? 0,
        total: Number(o.totalAmount || 0),
        currency: o.currency || 'BOB',
        paymentMethod: o.paymentMethod || '—',
        status: o.status || 'DRAFT',
        date: o.createdAt || '',
      })));
    } catch {
      setSales([]);
    } finally {
      setLoading(false);
    }
  }

  const filtered = sales.filter((s) => {
    const matchSearch = s.customerName.toLowerCase().includes(search.toLowerCase()) || s.id.includes(search);
    const matchStatus = statusFilter === 'all' || s.status === statusFilter;
    return matchSearch && matchStatus;
  });

  const total = filtered.filter((s) => s.status === 'CONFIRMED' || s.status === 'COMPLETED').reduce((sum, s) => sum + s.total, 0);

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <Loader2 className="w-6 h-6 animate-spin text-violet-500" />
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex gap-3 items-center flex-wrap">
        <div className="relative flex-1 max-w-xs">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-gray-400" />
          <Input placeholder="Buscar venta o cliente..." value={search} onChange={(e) => setSearch(e.target.value)} className="pl-8 h-9 text-sm" />
        </div>
        <Select value={statusFilter} onValueChange={setStatusFilter}>
          <SelectTrigger className="w-36 h-9 text-sm"><SelectValue /></SelectTrigger>
          <SelectContent>
            <SelectItem value="all">Todos</SelectItem>
            <SelectItem value="CONFIRMED">Pagado</SelectItem>
            <SelectItem value="PENDING">Pendiente</SelectItem>
            <SelectItem value="CANCELLED">Cancelado</SelectItem>
          </SelectContent>
        </Select>
        <div className="ml-auto bg-violet-50 border border-violet-100 rounded-lg px-4 py-1.5">
          <span className="text-xs text-violet-500 font-medium">Total cobrado:</span>
          <span className="text-sm font-bold text-violet-700 ml-1">Bs {total.toLocaleString()}</span>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-gray-100 overflow-hidden">
        <Table>
          <TableHeader>
            <TableRow className="bg-gray-50 hover:bg-gray-50">
              <TableHead className="text-xs font-semibold text-gray-500">#</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500">Cliente</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-center">Items</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-right">Total</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500">Fecha</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-center">Estado</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {filtered.length === 0 ? (
              <TableRow>
                <TableCell colSpan={6} className="text-center py-12 text-gray-400 text-sm">
                  <ShoppingCart className="w-8 h-8 mx-auto mb-2 text-gray-200" />
                  Sin ventas registradas
                </TableCell>
              </TableRow>
            ) : filtered.map((s) => (
              <TableRow key={s.id} className="hover:bg-gray-50">
                <TableCell className="font-mono text-sm font-semibold text-violet-600">{s.id}</TableCell>
                <TableCell className="text-sm font-medium text-gray-800">{s.customerName}</TableCell>
                <TableCell className="text-center text-sm text-gray-600">{s.itemCount}</TableCell>
                <TableCell className="text-right font-bold text-sm text-gray-900">Bs {s.total.toLocaleString()}</TableCell>
                <TableCell className="text-xs text-gray-400">{formatDate(s.date)}</TableCell>
                <TableCell className="text-center">
                  <Badge className={`${STATUS_CLS[s.status] ?? 'bg-gray-100 text-gray-600'} text-[10px]`}>
                    {STATUS_LABEL[s.status] ?? s.status}
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
