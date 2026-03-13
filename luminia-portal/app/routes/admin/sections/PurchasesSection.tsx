import { useEffect, useState } from 'react';
import { Plus, Search, Loader2, ShoppingCart, Trash2, Check, XCircle } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { luminiApi } from '@/services/luminiApi';
import { useWarehouse } from '../components/AdminLayout';

// ─── Types ──────────────────────────────────────────────────────────────────

interface InputRecord {
  id: string;
  type: string;
  documentNumber?: string;
  date: string;
  notes?: string;
  status: string;
  total: number;
  supplier?: { id: string; name: string };
  warehouse?: { id: string; name: string };
  _count?: { details: number };
  createdAt: string;
  updatedAt: string;
}

interface Product {
  id: string;
  name: string;
  sku: string;
  baseUnit?: { id: string; name: string; abbreviation: string };
  purchasePrice?: number;
}

interface Supplier {
  id: string;
  name: string;
}

interface UnitOption {
  id: string;
  name: string;
  abbreviation: string;
}

interface DetailLine {
  productId: string;
  productName: string;
  unitId: string;
  unitName: string;
  quantity: number;
  unitCost: number;
}

// ─── Constants ──────────────────────────────────────────────────────────────

const STATUS_CLS: Record<string, string> = {
  CONFIRMED: 'bg-green-100 text-green-700',
  DRAFT:     'bg-gray-100 text-gray-600',
  CANCELLED: 'bg-red-100 text-red-700',
};

const STATUS_LABEL: Record<string, string> = {
  CONFIRMED: 'CONFIRMADO',
  DRAFT:     'BORRADOR',
  CANCELLED: 'CANCELADO',
};

function formatDate(d: string) {
  try { return new Date(d).toLocaleDateString('es-BO', { dateStyle: 'medium' }); }
  catch { return d; }
}

// ─── Component ──────────────────────────────────────────────────────────────

export function PurchasesSection() {
  const { selectedWarehouse } = useWarehouse();

  const [inputs, setInputs] = useState<InputRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [dialogOpen, setDialogOpen] = useState(false);
  const [saving, setSaving] = useState(false);

  // Form state for new purchase
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [units, setUnits] = useState<UnitOption[]>([]);

  const [supplierId, setSupplierId] = useState('');
  const [documentNumber, setDocumentNumber] = useState('');
  const [purchaseDate, setPurchaseDate] = useState(() => new Date().toISOString().slice(0, 10));
  const [notes, setNotes] = useState('');
  const [lines, setLines] = useState<DetailLine[]>([]);

  // Line being added
  const [lineProductId, setLineProductId] = useState('');
  const [lineUnitId, setLineUnitId] = useState('');
  const [lineQty, setLineQty] = useState('1');
  const [lineCost, setLineCost] = useState('');

  useEffect(() => { loadInputs(); }, []);

  async function loadInputs() {
    setLoading(true);
    try {
      const { data } = await luminiApi.get('/inventory/inputs', { params: { type: 'PURCHASE' } });
      setInputs(Array.isArray(data) ? data : []);
    } catch {
      setInputs([]);
    } finally {
      setLoading(false);
    }
  }

  async function openNewPurchase() {
    setDialogOpen(true);
    setSupplierId('');
    setDocumentNumber('');
    setPurchaseDate(new Date().toISOString().slice(0, 10));
    setNotes('');
    setLines([]);

    try {
      const [supRes, prodRes, unitRes] = await Promise.all([
        luminiApi.get('/inventory/suppliers'),
        luminiApi.get('/inventory/products'),
        luminiApi.get('/inventory/units'),
      ]);
      setSuppliers(Array.isArray(supRes.data) ? supRes.data : []);
      setProducts(Array.isArray(prodRes.data) ? prodRes.data : []);
      setUnits(Array.isArray(unitRes.data) ? unitRes.data : []);
    } catch {
      // silenciar
    }
  }

  function addLine() {
    const product = products.find((p) => p.id === lineProductId);
    const unit = units.find((u) => u.id === lineUnitId);
    if (!product || !unit || !lineQty || !lineCost) return;

    setLines((prev) => [
      ...prev,
      {
        productId: product.id,
        productName: `${product.sku} - ${product.name}`,
        unitId: unit.id,
        unitName: unit.abbreviation || unit.name,
        quantity: Number(lineQty),
        unitCost: Number(lineCost),
      },
    ]);
    setLineProductId('');
    setLineUnitId('');
    setLineQty('1');
    setLineCost('');
  }

  function removeLine(idx: number) {
    setLines((prev) => prev.filter((_, i) => i !== idx));
  }

  async function savePurchase() {
    if (!selectedWarehouse) {
      alert('Selecciona un almacén en el header antes de crear una compra');
      return;
    }
    if (lines.length === 0) return;

    setSaving(true);
    try {
      await luminiApi.post('/inventory/inputs', {
        warehouseId: selectedWarehouse.id,
        supplierId: supplierId || undefined,
        type: 'PURCHASE',
        documentNumber: documentNumber || undefined,
        date: purchaseDate,
        notes: notes || undefined,
        details: lines.map((l) => ({
          productId: l.productId,
          unitId: l.unitId,
          quantity: l.quantity,
          unitCost: l.unitCost,
        })),
      });
      setDialogOpen(false);
      loadInputs();
    } catch (err: any) {
      alert(err?.response?.data?.message ?? 'Error al crear la compra');
    } finally {
      setSaving(false);
    }
  }

  async function confirmInput(id: string) {
    try {
      await luminiApi.post(`/inventory/inputs/${id}/confirm`);
      loadInputs();
    } catch (err: any) {
      alert(err?.response?.data?.message ?? 'Error al confirmar');
    }
  }

  async function cancelInput(id: string) {
    try {
      await luminiApi.post(`/inventory/inputs/${id}/cancel`);
      loadInputs();
    } catch (err: any) {
      alert(err?.response?.data?.message ?? 'Error al cancelar');
    }
  }

  function handleProductSelect(productId: string) {
    setLineProductId(productId);
    const product = products.find((p) => p.id === productId);
    if (product?.baseUnit) {
      setLineUnitId(product.baseUnit.id);
    }
    if (product?.purchasePrice) {
      setLineCost(String(Number(product.purchasePrice)));
    }
  }

  const filtered = inputs.filter((o) =>
    (o.supplier?.name ?? '').toLowerCase().includes(search.toLowerCase()) ||
    (o.documentNumber ?? '').toLowerCase().includes(search.toLowerCase()) ||
    o.id.slice(0, 8).includes(search),
  );

  const totalLines = lines.reduce((s, l) => s + l.quantity * l.unitCost, 0);

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
          <Input placeholder="Buscar compra..." value={search} onChange={(e) => setSearch(e.target.value)} className="pl-8 h-9 text-sm" />
        </div>
        <Button size="sm" className="bg-violet-600 hover:bg-violet-700 text-white" onClick={openNewPurchase}>
          <Plus className="w-4 h-4 mr-1" /> Nueva compra
        </Button>
      </div>

      {/* Purchases table */}
      <div className="bg-white rounded-xl border border-gray-100 overflow-hidden">
        <Table>
          <TableHeader>
            <TableRow className="bg-gray-50 hover:bg-gray-50">
              <TableHead className="text-xs font-semibold text-gray-500">Proveedor</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500">Nro. Comprobante</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500">Sucursal</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500">Fecha Compra</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500">Recepción</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-center">Items</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-right">Total</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-center">Estado</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500">Obs.</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-center">Acciones</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {filtered.length === 0 ? (
              <TableRow>
                <TableCell colSpan={10} className="text-center py-12 text-gray-400 text-sm">
                  <ShoppingCart className="w-8 h-8 mx-auto mb-2 text-gray-200" />
                  Sin compras registradas
                </TableCell>
              </TableRow>
            ) : filtered.map((o) => (
              <TableRow key={o.id} className="hover:bg-gray-50">
                <TableCell className="text-sm text-gray-700 font-medium">{o.supplier?.name ?? '-'}</TableCell>
                <TableCell className="text-sm text-gray-600 font-mono">{o.documentNumber ?? '-'}</TableCell>
                <TableCell className="text-sm text-gray-500">{o.warehouse?.name ?? '-'}</TableCell>
                <TableCell className="text-sm text-gray-500">{formatDate(o.date || o.createdAt)}</TableCell>
                <TableCell className="text-sm text-gray-500">
                  {o.status === 'CONFIRMED' ? formatDate(o.updatedAt) : '-'}
                </TableCell>
                <TableCell className="text-center text-sm text-gray-700">{o._count?.details ?? 0}</TableCell>
                <TableCell className="text-right font-semibold text-sm text-gray-800">Bs {Number(o.total).toLocaleString()}</TableCell>
                <TableCell className="text-center">
                  <Badge className={`${STATUS_CLS[o.status] ?? 'bg-gray-100 text-gray-600'} text-[10px]`}>
                    {STATUS_LABEL[o.status] ?? o.status}
                  </Badge>
                </TableCell>
                <TableCell className="text-xs text-gray-400 max-w-[120px] truncate" title={o.notes ?? ''}>
                  {o.notes ?? '-'}
                </TableCell>
                <TableCell className="text-center">
                  {o.status === 'DRAFT' && (
                    <div className="flex gap-1 justify-center">
                      <button onClick={() => confirmInput(o.id)} title="Confirmar" className="text-green-600 hover:text-green-800">
                        <Check className="w-4 h-4" />
                      </button>
                      <button onClick={() => cancelInput(o.id)} title="Cancelar" className="text-red-500 hover:text-red-700">
                        <XCircle className="w-4 h-4" />
                      </button>
                    </div>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>

      {/* ─── New Purchase Dialog ─────────────────────────────────────────── */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent className="sm:max-w-2xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>Nueva compra</DialogTitle>
          </DialogHeader>

          <div className="space-y-4 mt-2">
            {selectedWarehouse ? (
              <p className="text-xs text-gray-500">
                Almacén: <span className="font-medium text-gray-700">{selectedWarehouse.name}</span>
              </p>
            ) : (
              <p className="text-xs text-red-500 font-medium">
                Selecciona un almacén en el header antes de continuar
              </p>
            )}

            {/* Row 1: Supplier + Document + Date */}
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <div>
                <Label className="text-xs mb-1">Proveedor</Label>
                <select
                  value={supplierId}
                  onChange={(e) => setSupplierId(e.target.value)}
                  className="w-full h-9 text-sm border border-gray-200 rounded-md px-2 bg-white focus:outline-none focus:ring-1 focus:ring-violet-500"
                >
                  <option value="">Sin proveedor</option>
                  {suppliers.map((s) => (
                    <option key={s.id} value={s.id}>{s.name}</option>
                  ))}
                </select>
              </div>
              <div>
                <Label className="text-xs mb-1">N. Comprobante</Label>
                <Input
                  value={documentNumber}
                  onChange={(e) => setDocumentNumber(e.target.value)}
                  placeholder="FAC-001"
                  className="h-9 text-sm"
                />
              </div>
              <div>
                <Label className="text-xs mb-1">Fecha</Label>
                <Input
                  type="date"
                  value={purchaseDate}
                  onChange={(e) => setPurchaseDate(e.target.value)}
                  className="h-9 text-sm"
                />
              </div>
            </div>

            {/* Notes */}
            <div>
              <Label className="text-xs mb-1">Notas (opcional)</Label>
              <Input
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
                placeholder="Notas adicionales..."
                className="h-9 text-sm"
              />
            </div>

            {/* Add product line */}
            <div className="bg-gray-50 rounded-lg p-3 space-y-2">
              <p className="text-xs font-semibold text-gray-600">Agregar producto</p>
              <div className="grid grid-cols-2 sm:grid-cols-5 gap-2">
                <div className="col-span-2">
                  <select
                    value={lineProductId}
                    onChange={(e) => handleProductSelect(e.target.value)}
                    className="w-full h-8 text-xs border border-gray-200 rounded-md px-2 bg-white focus:outline-none focus:ring-1 focus:ring-violet-500"
                  >
                    <option value="">Producto...</option>
                    {products.map((p) => (
                      <option key={p.id} value={p.id}>{p.sku} - {p.name}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <select
                    value={lineUnitId}
                    onChange={(e) => setLineUnitId(e.target.value)}
                    className="w-full h-8 text-xs border border-gray-200 rounded-md px-2 bg-white focus:outline-none focus:ring-1 focus:ring-violet-500"
                  >
                    <option value="">Unidad...</option>
                    {units.map((u) => (
                      <option key={u.id} value={u.id}>{u.abbreviation || u.name}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <Input
                    type="number"
                    min="1"
                    value={lineQty}
                    onChange={(e) => setLineQty(e.target.value)}
                    placeholder="Cant."
                    className="h-8 text-xs"
                  />
                </div>
                <div className="flex gap-1">
                  <Input
                    type="number"
                    min="0"
                    step="0.01"
                    value={lineCost}
                    onChange={(e) => setLineCost(e.target.value)}
                    placeholder="P.Unit"
                    className="h-8 text-xs flex-1"
                  />
                  <Button
                    size="sm"
                    type="button"
                    onClick={addLine}
                    disabled={!lineProductId || !lineUnitId || !lineQty || !lineCost}
                    className="h-8 px-2 bg-violet-600 hover:bg-violet-700 text-white"
                  >
                    <Plus className="w-3 h-3" />
                  </Button>
                </div>
              </div>
            </div>

            {/* Lines table */}
            {lines.length > 0 && (
              <div className="border border-gray-100 rounded-lg overflow-hidden">
                <Table>
                  <TableHeader>
                    <TableRow className="bg-gray-50 hover:bg-gray-50">
                      <TableHead className="text-xs">Producto</TableHead>
                      <TableHead className="text-xs text-center">Unidad</TableHead>
                      <TableHead className="text-xs text-center">Cantidad</TableHead>
                      <TableHead className="text-xs text-right">P. Unit.</TableHead>
                      <TableHead className="text-xs text-right">Subtotal</TableHead>
                      <TableHead className="text-xs w-10"></TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {lines.map((l, i) => (
                      <TableRow key={i}>
                        <TableCell className="text-xs text-gray-700">{l.productName}</TableCell>
                        <TableCell className="text-xs text-center text-gray-500">{l.unitName}</TableCell>
                        <TableCell className="text-xs text-center">{l.quantity}</TableCell>
                        <TableCell className="text-xs text-right">Bs {l.unitCost.toFixed(2)}</TableCell>
                        <TableCell className="text-xs text-right font-medium">Bs {(l.quantity * l.unitCost).toFixed(2)}</TableCell>
                        <TableCell>
                          <button onClick={() => removeLine(i)} className="text-red-400 hover:text-red-600">
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
                <div className="flex justify-end px-4 py-2 bg-gray-50 border-t border-gray-100">
                  <span className="text-sm font-semibold text-gray-800">
                    Total: Bs {totalLines.toFixed(2)}
                  </span>
                </div>
              </div>
            )}

            {/* Actions */}
            <div className="flex justify-end gap-2 pt-2">
              <Button variant="outline" size="sm" onClick={() => setDialogOpen(false)}>
                Cancelar
              </Button>
              <Button
                size="sm"
                className="bg-violet-600 hover:bg-violet-700 text-white"
                disabled={saving || lines.length === 0 || !selectedWarehouse}
                onClick={savePurchase}
              >
                {saving && <Loader2 className="w-3 h-3 animate-spin mr-1" />}
                Guardar compra
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
