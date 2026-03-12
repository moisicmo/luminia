import { useEffect, useState } from 'react';
import { Search, ArrowUpCircle, ArrowDownCircle, ArrowLeftRight, Loader2, ClipboardList } from 'lucide-react';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Badge } from '@/components/ui/badge';
import { luminiApi } from '@/services/luminiApi';

// TODO: Conectar a endpoint de kardex/movimientos cuando esté disponible en el gateway
// Por ahora carga la lista de productos para el selector

interface Product {
  id: string;
  name: string;
}

const TYPE_CONFIG: Record<string, { icon: any; cls: string }> = {
  VENTA:    { icon: ArrowDownCircle, cls: 'text-red-500' },
  COMPRA:   { icon: ArrowUpCircle,   cls: 'text-green-500' },
  TRASPASO: { icon: ArrowLeftRight,  cls: 'text-blue-500' },
  AJUSTE:   { icon: ArrowUpCircle,   cls: 'text-amber-500' },
};

const TYPE_BADGE: Record<string, string> = {
  VENTA:    'bg-red-100 text-red-700',
  COMPRA:   'bg-green-100 text-green-700',
  TRASPASO: 'bg-blue-100 text-blue-700',
  AJUSTE:   'bg-amber-100 text-amber-700',
};

export function KardexSection() {
  const [products, setProducts] = useState<Product[]>([]);
  const [selectedProduct, setSelectedProduct] = useState('');
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  useEffect(() => { loadProducts(); }, []);

  async function loadProducts() {
    setLoading(true);
    try {
      const { data } = await luminiApi.get('/inventory/products');
      const prods = Array.isArray(data) ? data : [];
      setProducts(prods.map((p: any) => ({ id: p.id, name: p.name })));
      if (prods.length > 0) setSelectedProduct(prods[0].id);
    } catch {
      setProducts([]);
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

  return (
    <div className="space-y-4">
      <div className="flex gap-3 items-center flex-wrap">
        <Select value={selectedProduct} onValueChange={setSelectedProduct}>
          <SelectTrigger className="w-64 h-9 text-sm"><SelectValue placeholder="Seleccionar producto" /></SelectTrigger>
          <SelectContent>
            {products.map((p) => <SelectItem key={p.id} value={p.id}>{p.name}</SelectItem>)}
          </SelectContent>
        </Select>
        <div className="relative flex-1 max-w-xs">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-gray-400" />
          <Input placeholder="Filtrar movimientos..." value={search} onChange={(e) => setSearch(e.target.value)} className="pl-8 h-9 text-sm" />
        </div>
      </div>

      {/* Summary cards */}
      <div className="grid grid-cols-3 gap-3">
        {[
          { label: 'Entradas',   value: 0, cls: 'text-green-600', bg: 'bg-green-50' },
          { label: 'Salidas',    value: 0, cls: 'text-red-500',   bg: 'bg-red-50' },
          { label: 'Saldo act.', value: 0, cls: 'text-violet-600', bg: 'bg-violet-50' },
        ].map((s) => (
          <div key={s.label} className={`${s.bg} rounded-xl p-4 text-center`}>
            <p className="text-xs text-gray-500 font-medium">{s.label}</p>
            <p className={`text-2xl font-bold mt-1 ${s.cls}`}>{s.value}</p>
          </div>
        ))}
      </div>

      <div className="bg-white rounded-xl border border-gray-100 overflow-hidden">
        <Table>
          <TableHeader>
            <TableRow className="bg-gray-50 hover:bg-gray-50">
              <TableHead className="text-xs font-semibold text-gray-500">Fecha</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500">Tipo</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500">Detalle</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-center">Cantidad</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-center">Saldo</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500">Usuario</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow>
              <TableCell colSpan={6} className="text-center py-12 text-gray-400 text-sm">
                <ClipboardList className="w-8 h-8 mx-auto mb-2 text-gray-200" />
                Sin movimientos registrados
                <p className="text-xs mt-1 text-gray-300">Los movimientos de kardex aparecerán aquí</p>
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </div>
    </div>
  );
}
