import { useEffect, useState } from 'react';
import { Search, ArrowUpCircle, ArrowDownCircle, Loader2, ClipboardList } from 'lucide-react';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Badge } from '@/components/ui/badge';
import { luminiApi } from '@/services/luminiApi';
import { useWarehouse } from '../components/AdminLayout';

interface Product {
  id: string;
  name: string;
}

interface KardexRow {
  id: string;
  movementDate: string;
  movementType: 'IN' | 'OUT';
  documentType: string;
  documentNumber?: string;
  quantity: number;
  unitCost: number;
  totalCost: number;
  balanceQty: number;
  balanceCost: number;
  product?: { name: string; sku: string };
  warehouse?: { name: string };
  unit?: { abbreviation: string };
  notes?: string;
}

const DOC_LABEL: Record<string, string> = {
  INPUT: 'Entrada',
  OUTPUT: 'Salida',
  TRANSFER: 'Traspaso',
  ADJUSTMENT: 'Ajuste',
};

function formatDate(d: string) {
  try { return new Date(d).toLocaleDateString('es-BO', { dateStyle: 'medium' }); }
  catch { return d; }
}

export function KardexSection() {
  const { selectedWarehouse } = useWarehouse();
  const [products, setProducts] = useState<Product[]>([]);
  const [selectedProduct, setSelectedProduct] = useState('');
  const [rows, setRows] = useState<KardexRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  useEffect(() => { loadProducts(); }, []);

  useEffect(() => {
    if (selectedProduct) loadKardex();
  }, [selectedProduct, selectedWarehouse?.id]);

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

  async function loadKardex() {
    try {
      const params: any = { productId: selectedProduct };
      if (selectedWarehouse) params.warehouseId = selectedWarehouse.id;
      const { data } = await luminiApi.get('/inventory/kardex', { params });
      setRows(Array.isArray(data) ? data : []);
    } catch {
      setRows([]);
    }
  }

  const totalIn = rows.filter((r) => r.movementType === 'IN').reduce((s, r) => s + Number(r.quantity), 0);
  const totalOut = rows.filter((r) => r.movementType === 'OUT').reduce((s, r) => s + Number(r.quantity), 0);
  const balance = rows.length > 0 ? Number(rows[0].balanceQty) : 0;

  const filtered = rows.filter((r) =>
    (r.documentNumber ?? '').toLowerCase().includes(search.toLowerCase()) ||
    (r.notes ?? '').toLowerCase().includes(search.toLowerCase()),
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
          { label: 'Entradas', value: totalIn, cls: 'text-green-600', bg: 'bg-green-50' },
          { label: 'Salidas', value: totalOut, cls: 'text-red-500', bg: 'bg-red-50' },
          { label: 'Saldo act.', value: balance, cls: 'text-violet-600', bg: 'bg-violet-50' },
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
              <TableHead className="text-xs font-semibold text-gray-500">Documento</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-center">Cantidad</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-right">Costo Unit.</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-center">Saldo</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {filtered.length === 0 ? (
              <TableRow>
                <TableCell colSpan={6} className="text-center py-12 text-gray-400 text-sm">
                  <ClipboardList className="w-8 h-8 mx-auto mb-2 text-gray-200" />
                  Sin movimientos registrados
                  <p className="text-xs mt-1 text-gray-300">Los movimientos de kardex aparecerán aquí</p>
                </TableCell>
              </TableRow>
            ) : filtered.map((r) => {
              const isIn = r.movementType === 'IN';
              return (
                <TableRow key={r.id} className="hover:bg-gray-50">
                  <TableCell className="text-sm text-gray-500">{formatDate(r.movementDate)}</TableCell>
                  <TableCell>
                    <div className="flex items-center gap-1.5">
                      {isIn
                        ? <ArrowUpCircle className="w-3.5 h-3.5 text-green-500" />
                        : <ArrowDownCircle className="w-3.5 h-3.5 text-red-500" />}
                      <Badge className={`text-[10px] ${isIn ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                        {DOC_LABEL[r.documentType] ?? r.documentType}
                      </Badge>
                    </div>
                  </TableCell>
                  <TableCell className="text-sm text-gray-500">{r.documentNumber ?? '-'}</TableCell>
                  <TableCell className="text-center">
                    <span className={`font-semibold text-sm ${isIn ? 'text-green-600' : 'text-red-600'}`}>
                      {isIn ? '+' : '-'}{Number(r.quantity)}
                    </span>
                  </TableCell>
                  <TableCell className="text-right text-sm text-gray-600">Bs {Number(r.unitCost).toFixed(2)}</TableCell>
                  <TableCell className="text-center font-bold text-sm text-gray-800">{Number(r.balanceQty)}</TableCell>
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </div>
    </div>
  );
}
