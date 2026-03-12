import { useEffect, useState } from 'react';
import { Search, AlertTriangle, Loader2, Boxes } from 'lucide-react';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { luminiApi } from '@/services/luminiApi';

interface InventoryRow {
  id: string;
  product: string;
  category: string;
  stock: number;
  minStock: number;
  cost: number;
  active: boolean;
}

function stockStatus(stock: number, min: number) {
  if (stock === 0) return { label: 'Sin stock', cls: 'bg-red-100 text-red-700' };
  if (stock <= min) return { label: 'Stock bajo', cls: 'bg-amber-100 text-amber-700' };
  return { label: 'OK', cls: 'bg-green-100 text-green-700' };
}

export function InventorySection() {
  const [rows, setRows] = useState<InventoryRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [stockFilter, setStockFilter] = useState('all');

  useEffect(() => { load(); }, []);

  async function load() {
    setLoading(true);
    try {
      const [prodRes, catRes] = await Promise.all([
        luminiApi.get('/inventory/products'),
        luminiApi.get('/inventory/categories'),
      ]);
      const products = Array.isArray(prodRes.data) ? prodRes.data : [];
      const cats = Array.isArray(catRes.data) ? catRes.data : [];
      const catMap = Object.fromEntries(cats.map((c: any) => [c.id, c.name]));

      setRows(products.map((p: any) => ({
        id: p.id,
        product: p.name,
        category: catMap[p.categoryId] ?? '',
        stock: Number(p.stock ?? p.currentStock ?? 0),
        minStock: Number(p.minStock ?? 5),
        cost: Number(p.cost ?? 0),
        active: p.active !== false,
      })));
    } catch {
      setRows([]);
    } finally {
      setLoading(false);
    }
  }

  const filtered = rows.filter((i) => {
    const matchSearch = i.product.toLowerCase().includes(search.toLowerCase());
    if (stockFilter === 'low') return matchSearch && i.stock > 0 && i.stock <= i.minStock;
    if (stockFilter === 'out') return matchSearch && i.stock === 0;
    if (stockFilter === 'ok') return matchSearch && i.stock > i.minStock;
    return matchSearch;
  });

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
          <Input placeholder="Buscar producto..." value={search} onChange={(e) => setSearch(e.target.value)} className="pl-8 h-9 text-sm" />
        </div>
        <Select value={stockFilter} onValueChange={setStockFilter}>
          <SelectTrigger className="w-40 h-9 text-sm"><SelectValue placeholder="Estado stock" /></SelectTrigger>
          <SelectContent>
            <SelectItem value="all">Todos</SelectItem>
            <SelectItem value="ok">OK</SelectItem>
            <SelectItem value="low">Stock bajo</SelectItem>
            <SelectItem value="out">Sin stock</SelectItem>
          </SelectContent>
        </Select>
        <div className="ml-auto flex gap-3">
          <div className="bg-green-50 border border-green-100 rounded-lg px-3 py-1.5">
            <span className="text-xs text-green-600 font-medium">{rows.filter(r => r.stock > r.minStock).length} OK</span>
          </div>
          <div className="bg-amber-50 border border-amber-100 rounded-lg px-3 py-1.5">
            <span className="text-xs text-amber-600 font-medium">{rows.filter(r => r.stock > 0 && r.stock <= r.minStock).length} Bajo</span>
          </div>
          <div className="bg-red-50 border border-red-100 rounded-lg px-3 py-1.5">
            <span className="text-xs text-red-600 font-medium">{rows.filter(r => r.stock === 0).length} Agotado</span>
          </div>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-gray-100 overflow-hidden">
        <Table>
          <TableHeader>
            <TableRow className="bg-gray-50 hover:bg-gray-50">
              <TableHead className="text-xs font-semibold text-gray-500">Producto</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500">Categoría</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-center">Stock</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-center">Mínimo</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-right">Costo</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-center">Estado</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {filtered.length === 0 ? (
              <TableRow>
                <TableCell colSpan={6} className="text-center py-12 text-gray-400 text-sm">
                  <Boxes className="w-8 h-8 mx-auto mb-2 text-gray-200" />
                  Sin productos en inventario
                </TableCell>
              </TableRow>
            ) : filtered.map((i) => {
              const s = stockStatus(i.stock, i.minStock);
              return (
                <TableRow key={i.id} className="hover:bg-gray-50">
                  <TableCell>
                    <p className="text-sm font-medium text-gray-800">{i.product}</p>
                  </TableCell>
                  <TableCell className="text-sm text-gray-500">{i.category || '—'}</TableCell>
                  <TableCell className="text-center">
                    <div className="flex items-center justify-center gap-1">
                      {i.stock > 0 && i.stock <= i.minStock && <AlertTriangle className="w-3 h-3 text-amber-400" />}
                      <span className={`font-bold text-sm ${i.stock === 0 ? 'text-red-600' : i.stock <= i.minStock ? 'text-amber-600' : 'text-gray-800'}`}>
                        {i.stock}
                      </span>
                    </div>
                  </TableCell>
                  <TableCell className="text-center text-sm text-gray-500">{i.minStock}</TableCell>
                  <TableCell className="text-right text-sm font-medium text-gray-700">Bs {i.cost.toLocaleString()}</TableCell>
                  <TableCell className="text-center">
                    <Badge className={`${s.cls} text-[10px]`}>{s.label}</Badge>
                  </TableCell>
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </div>
    </div>
  );
}
