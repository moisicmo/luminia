import { useEffect, useState } from 'react';
import { Plus, Search, Loader2, ShoppingCart, Trash2, Check, XCircle, FileText, ChevronDown } from 'lucide-react';
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
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion';
import { luminiApi } from '@/services/luminiApi';
import { useWarehouse } from '../components/AdminLayout';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

// ─── Types ──────────────────────────────────────────────────────────────────

interface InputDetail {
  id: string;
  quantity: number;
  unitCost: number;
  totalCost: number;
  product: { id: string; name: string; sku: string };
  unit: { id: string; name: string; abbreviation: string };
}

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
  details?: InputDetail[];
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

// ─── PDF Generator ──────────────────────────────────────────────────────────

function generatePurchasePdf(input: InputRecord) {
  const doc = new jsPDF();

  // Header
  doc.setFontSize(18);
  doc.setFont('helvetica', 'bold');
  doc.text('ORDEN DE COMPRA', 105, 20, { align: 'center' });

  doc.setFontSize(10);
  doc.setFont('helvetica', 'normal');
  doc.setTextColor(100);
  doc.text(`N° ${input.documentNumber || input.id.slice(0, 8).toUpperCase()}`, 105, 28, { align: 'center' });

  // Info section
  doc.setTextColor(0);
  doc.setFontSize(10);
  const infoY = 40;
  doc.setFont('helvetica', 'bold');
  doc.text('Proveedor:', 14, infoY);
  doc.setFont('helvetica', 'normal');
  doc.text(input.supplier?.name ?? 'Sin proveedor', 50, infoY);

  doc.setFont('helvetica', 'bold');
  doc.text('Almacén:', 14, infoY + 7);
  doc.setFont('helvetica', 'normal');
  doc.text(input.warehouse?.name ?? '-', 50, infoY + 7);

  doc.setFont('helvetica', 'bold');
  doc.text('Fecha:', 120, infoY);
  doc.setFont('helvetica', 'normal');
  doc.text(formatDate(input.date || input.createdAt), 145, infoY);

  doc.setFont('helvetica', 'bold');
  doc.text('Estado:', 120, infoY + 7);
  doc.setFont('helvetica', 'normal');
  doc.text(STATUS_LABEL[input.status] ?? input.status, 145, infoY + 7);

  if (input.notes) {
    doc.setFont('helvetica', 'bold');
    doc.text('Notas:', 14, infoY + 14);
    doc.setFont('helvetica', 'normal');
    doc.text(input.notes, 50, infoY + 14);
  }

  // Details table
  const details = input.details ?? [];
  autoTable(doc, {
    startY: infoY + (input.notes ? 22 : 16),
    head: [['#', 'SKU', 'Producto', 'Unidad', 'Cantidad', 'P. Unit.', 'Subtotal']],
    body: details.map((d, i) => [
      i + 1,
      d.product.sku,
      d.product.name,
      d.unit.abbreviation || d.unit.name,
      d.quantity,
      `Bs ${Number(d.unitCost).toFixed(2)}`,
      `Bs ${Number(d.totalCost).toFixed(2)}`,
    ]),
    styles: { fontSize: 9 },
    headStyles: { fillColor: [109, 40, 217] },
    columnStyles: {
      0: { cellWidth: 10 },
      4: { halign: 'center' },
      5: { halign: 'right' },
      6: { halign: 'right' },
    },
  });

  // Total
  const finalY = (doc as any).lastAutoTable.finalY + 8;
  doc.setFont('helvetica', 'bold');
  doc.setFontSize(12);
  doc.text(`Total: Bs ${Number(input.total).toFixed(2)}`, 196, finalY, { align: 'right' });

  // Footer
  doc.setFontSize(8);
  doc.setFont('helvetica', 'normal');
  doc.setTextColor(150);
  doc.text(`Generado el ${new Date().toLocaleDateString('es-BO')} - Luminia`, 105, 285, { align: 'center' });

  doc.save(`compra-${input.documentNumber || input.id.slice(0, 8)}.pdf`);
}

// ─── Component ──────────────────────────────────────────────────────────────

export function PurchasesSection() {
  const { selectedWarehouse } = useWarehouse();

  const [inputs, setInputs] = useState<InputRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [dialogOpen, setDialogOpen] = useState(false);
  const [saving, setSaving] = useState(false);
  const [loadingDetails, setLoadingDetails] = useState<Record<string, boolean>>({});

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

  async function loadInputDetails(id: string) {
    const existing = inputs.find((i) => i.id === id);
    if (existing?.details) return;

    setLoadingDetails((prev) => ({ ...prev, [id]: true }));
    try {
      const { data } = await luminiApi.get(`/inventory/inputs/${id}`);
      setInputs((prev) => prev.map((i) => (i.id === id ? { ...i, details: data.details } : i)));
    } catch {
      // silenciar
    } finally {
      setLoadingDetails((prev) => ({ ...prev, [id]: false }));
    }
  }

  async function handleDownloadPdf(id: string) {
    let input = inputs.find((i) => i.id === id);
    if (!input?.details) {
      setLoadingDetails((prev) => ({ ...prev, [id]: true }));
      try {
        const { data } = await luminiApi.get(`/inventory/inputs/${id}`);
        input = { ...input!, details: data.details };
        setInputs((prev) => prev.map((i) => (i.id === id ? input! : i)));
      } catch {
        alert('Error al cargar detalles para el PDF');
        return;
      } finally {
        setLoadingDetails((prev) => ({ ...prev, [id]: false }));
      }
    }
    generatePurchasePdf(input!);
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

      {/* Purchases list with accordion */}
      <div className="bg-white rounded-xl border border-gray-100 overflow-hidden">
        {filtered.length === 0 ? (
          <div className="text-center py-12 text-gray-400 text-sm">
            <ShoppingCart className="w-8 h-8 mx-auto mb-2 text-gray-200" />
            Sin compras registradas
          </div>
        ) : (
          <Accordion type="single" collapsible className="w-full">
            {filtered.map((o) => (
              <AccordionItem key={o.id} value={o.id} className="border-b border-gray-100">
                <AccordionTrigger
                  className="px-4 py-3 hover:no-underline hover:bg-gray-50 gap-3"
                  onClick={() => loadInputDetails(o.id)}
                >
                  <div className="flex items-center gap-3 flex-1 min-w-0 text-left">
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2 flex-wrap">
                        <span className="text-sm font-medium text-gray-700">{o.supplier?.name ?? 'Sin proveedor'}</span>
                        {o.documentNumber && (
                          <span className="text-xs text-gray-400 font-mono">{o.documentNumber}</span>
                        )}
                        <Badge className={`${STATUS_CLS[o.status] ?? 'bg-gray-100 text-gray-600'} text-[10px]`}>
                          {STATUS_LABEL[o.status] ?? o.status}
                        </Badge>
                      </div>
                      <div className="flex items-center gap-3 mt-1 text-xs text-gray-400">
                        <span>{o.warehouse?.name ?? '-'}</span>
                        <span>{formatDate(o.date || o.createdAt)}</span>
                        <span>{o._count?.details ?? 0} items</span>
                        {o.notes && <span className="truncate max-w-[150px]" title={o.notes}>{o.notes}</span>}
                      </div>
                    </div>
                    <div className="flex items-center gap-2 shrink-0">
                      <span className="font-semibold text-sm text-gray-800">Bs {Number(o.total).toLocaleString()}</span>
                      <button
                        onClick={(e) => { e.stopPropagation(); handleDownloadPdf(o.id); }}
                        title="Descargar PDF"
                        className="text-violet-500 hover:text-violet-700 p-1"
                      >
                        <FileText className="w-4 h-4" />
                      </button>
                      {o.status === 'DRAFT' && (
                        <>
                          <button onClick={(e) => { e.stopPropagation(); confirmInput(o.id); }} title="Confirmar" className="text-green-600 hover:text-green-800 p-1">
                            <Check className="w-4 h-4" />
                          </button>
                          <button onClick={(e) => { e.stopPropagation(); cancelInput(o.id); }} title="Cancelar" className="text-red-500 hover:text-red-700 p-1">
                            <XCircle className="w-4 h-4" />
                          </button>
                        </>
                      )}
                    </div>
                  </div>
                </AccordionTrigger>
                <AccordionContent className="px-4 pb-4">
                  {loadingDetails[o.id] ? (
                    <div className="flex items-center justify-center py-4">
                      <Loader2 className="w-4 h-4 animate-spin text-violet-400" />
                      <span className="ml-2 text-xs text-gray-400">Cargando detalles...</span>
                    </div>
                  ) : o.details && o.details.length > 0 ? (
                    <div className="border border-gray-100 rounded-lg overflow-hidden">
                      <Table>
                        <TableHeader>
                          <TableRow className="bg-gray-50 hover:bg-gray-50">
                            <TableHead className="text-xs font-semibold text-gray-500">SKU</TableHead>
                            <TableHead className="text-xs font-semibold text-gray-500">Producto</TableHead>
                            <TableHead className="text-xs font-semibold text-gray-500 text-center">Unidad</TableHead>
                            <TableHead className="text-xs font-semibold text-gray-500 text-center">Cantidad</TableHead>
                            <TableHead className="text-xs font-semibold text-gray-500 text-right">P. Unit.</TableHead>
                            <TableHead className="text-xs font-semibold text-gray-500 text-right">Subtotal</TableHead>
                          </TableRow>
                        </TableHeader>
                        <TableBody>
                          {o.details.map((d) => (
                            <TableRow key={d.id}>
                              <TableCell className="text-xs text-gray-500 font-mono">{d.product.sku}</TableCell>
                              <TableCell className="text-xs text-gray-700">{d.product.name}</TableCell>
                              <TableCell className="text-xs text-center text-gray-500">{d.unit.abbreviation || d.unit.name}</TableCell>
                              <TableCell className="text-xs text-center">{d.quantity}</TableCell>
                              <TableCell className="text-xs text-right">Bs {Number(d.unitCost).toFixed(2)}</TableCell>
                              <TableCell className="text-xs text-right font-medium">Bs {Number(d.totalCost).toFixed(2)}</TableCell>
                            </TableRow>
                          ))}
                        </TableBody>
                      </Table>
                      <div className="flex justify-end px-4 py-2 bg-gray-50 border-t border-gray-100">
                        <span className="text-sm font-semibold text-gray-800">Total: Bs {Number(o.total).toFixed(2)}</span>
                      </div>
                    </div>
                  ) : (
                    <p className="text-xs text-gray-400 py-2">Sin detalles disponibles</p>
                  )}
                </AccordionContent>
              </AccordionItem>
            ))}
          </Accordion>
        )}
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
