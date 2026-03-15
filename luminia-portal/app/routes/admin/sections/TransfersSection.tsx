import { useEffect, useState } from 'react';
import {
  Plus, Search, Loader2, ArrowLeftRight, ArrowRight, Trash2,
  Check, XCircle, FileText,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
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

interface TransferDetail {
  id: string;
  quantity: number;
  unitCost: number;
  product: { id: string; name: string; sku: string };
  unit: { id: string; name: string; abbreviation: string };
}

interface TransferRecord {
  id: string;
  date: string;
  notes?: string;
  status: string;
  fromWarehouse: { id: string; name: string };
  toWarehouse: { id: string; name: string };
  _count?: { details: number };
  details?: TransferDetail[];
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
  PENDING:    'bg-amber-100 text-amber-700',
  IN_TRANSIT: 'bg-blue-100 text-blue-700',
  COMPLETED:  'bg-green-100 text-green-700',
  CANCELLED:  'bg-red-100 text-red-700',
};

const STATUS_LABEL: Record<string, string> = {
  PENDING:    'PENDIENTE',
  IN_TRANSIT: 'EN TRANSITO',
  COMPLETED:  'COMPLETADO',
  CANCELLED:  'CANCELADO',
};

function formatDate(d: string) {
  try { return new Date(d).toLocaleDateString('es-BO', { dateStyle: 'medium' }); }
  catch { return d; }
}

// ─── PDF ────────────────────────────────────────────────────────────────────

function generateTransferPdf(t: TransferRecord) {
  const doc = new jsPDF();

  doc.setFontSize(18);
  doc.setFont('helvetica', 'bold');
  doc.text('TRASPASO DE MERCADERIA', 105, 20, { align: 'center' });

  doc.setFontSize(10);
  doc.setFont('helvetica', 'normal');
  doc.setTextColor(100);
  doc.text(`ID: ${t.id.slice(0, 8).toUpperCase()}`, 105, 28, { align: 'center' });

  doc.setTextColor(0);
  const y = 40;
  doc.setFont('helvetica', 'bold');
  doc.text('Origen:', 14, y);
  doc.setFont('helvetica', 'normal');
  doc.text(t.fromWarehouse.name, 50, y);

  doc.setFont('helvetica', 'bold');
  doc.text('Destino:', 14, y + 7);
  doc.setFont('helvetica', 'normal');
  doc.text(t.toWarehouse.name, 50, y + 7);

  doc.setFont('helvetica', 'bold');
  doc.text('Fecha:', 120, y);
  doc.setFont('helvetica', 'normal');
  doc.text(formatDate(t.date || t.createdAt), 145, y);

  doc.setFont('helvetica', 'bold');
  doc.text('Estado:', 120, y + 7);
  doc.setFont('helvetica', 'normal');
  doc.text(STATUS_LABEL[t.status] ?? t.status, 145, y + 7);

  if (t.notes) {
    doc.setFont('helvetica', 'bold');
    doc.text('Notas:', 14, y + 14);
    doc.setFont('helvetica', 'normal');
    doc.text(t.notes, 50, y + 14);
  }

  const details = t.details ?? [];
  autoTable(doc, {
    startY: y + (t.notes ? 22 : 16),
    head: [['#', 'SKU', 'Producto', 'Unidad', 'Cantidad', 'Costo Unit.']],
    body: details.map((d, i) => [
      i + 1,
      d.product.sku,
      d.product.name,
      d.unit.abbreviation || d.unit.name,
      d.quantity,
      `Bs ${Number(d.unitCost).toFixed(2)}`,
    ]),
    styles: { fontSize: 9 },
    headStyles: { fillColor: [109, 40, 217] },
    columnStyles: { 0: { cellWidth: 10 }, 4: { halign: 'center' }, 5: { halign: 'right' } },
  });

  doc.setFontSize(8);
  doc.setFont('helvetica', 'normal');
  doc.setTextColor(150);
  doc.text(`Generado el ${new Date().toLocaleDateString('es-BO')} - Luminia`, 105, 285, { align: 'center' });

  doc.save(`traspaso-${t.id.slice(0, 8)}.pdf`);
}

// ─── Component ──────────────────────────────────────────────────────────────

export function TransfersSection() {
  const { warehouses, selectedWarehouse } = useWarehouse();

  const [transfers, setTransfers] = useState<TransferRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [dialogOpen, setDialogOpen] = useState(false);
  const [saving, setSaving] = useState(false);
  const [loadingDetails, setLoadingDetails] = useState<Record<string, boolean>>({});

  // Form
  const [products, setProducts] = useState<Product[]>([]);
  const [units, setUnits] = useState<UnitOption[]>([]);
  const [fromWarehouseId, setFromWarehouseId] = useState('');
  const [toWarehouseId, setToWarehouseId] = useState('');
  const [transferDate, setTransferDate] = useState(() => new Date().toISOString().slice(0, 10));
  const [notes, setNotes] = useState('');
  const [lines, setLines] = useState<DetailLine[]>([]);

  // Line form
  const [lineProductId, setLineProductId] = useState('');
  const [lineUnitId, setLineUnitId] = useState('');
  const [lineQty, setLineQty] = useState('1');
  const [lineCost, setLineCost] = useState('');

  useEffect(() => { loadTransfers(); }, []);

  async function loadTransfers() {
    setLoading(true);
    try {
      const { data } = await luminiApi.get('/inventory/transfers');
      setTransfers(Array.isArray(data) ? data : []);
    } catch {
      setTransfers([]);
    } finally {
      setLoading(false);
    }
  }

  async function loadTransferDetails(id: string) {
    const existing = transfers.find((t) => t.id === id);
    if (existing?.details) return;
    setLoadingDetails((prev) => ({ ...prev, [id]: true }));
    try {
      const { data } = await luminiApi.get(`/inventory/transfers/${id}`);
      setTransfers((prev) => prev.map((t) => (t.id === id ? { ...t, details: data.details } : t)));
    } catch { /* silenciar */ }
    finally { setLoadingDetails((prev) => ({ ...prev, [id]: false })); }
  }

  async function handleDownloadPdf(id: string) {
    let transfer = transfers.find((t) => t.id === id);
    if (!transfer?.details) {
      setLoadingDetails((prev) => ({ ...prev, [id]: true }));
      try {
        const { data } = await luminiApi.get(`/inventory/transfers/${id}`);
        transfer = { ...transfer!, details: data.details };
        setTransfers((prev) => prev.map((t) => (t.id === id ? transfer! : t)));
      } catch {
        alert('Error al cargar detalles para el PDF');
        return;
      } finally {
        setLoadingDetails((prev) => ({ ...prev, [id]: false }));
      }
    }
    generateTransferPdf(transfer!);
  }

  async function openNewTransfer() {
    setDialogOpen(true);
    setFromWarehouseId(selectedWarehouse?.id ?? '');
    setToWarehouseId('');
    setTransferDate(new Date().toISOString().slice(0, 10));
    setNotes('');
    setLines([]);

    try {
      const [prodRes, unitRes] = await Promise.all([
        luminiApi.get('/inventory/products'),
        luminiApi.get('/inventory/units'),
      ]);
      setProducts(Array.isArray(prodRes.data) ? prodRes.data : []);
      setUnits(Array.isArray(unitRes.data) ? unitRes.data : []);
    } catch { /* silenciar */ }
  }

  function handleProductSelect(productId: string) {
    setLineProductId(productId);
    const product = products.find((p) => p.id === productId);
    if (product?.baseUnit) setLineUnitId(product.baseUnit.id);
    if (product?.purchasePrice) setLineCost(String(Number(product.purchasePrice)));
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

  async function saveTransfer() {
    if (!fromWarehouseId || !toWarehouseId) {
      alert('Selecciona almacen origen y destino');
      return;
    }
    if (fromWarehouseId === toWarehouseId) {
      alert('El almacen origen y destino no pueden ser el mismo');
      return;
    }
    if (lines.length === 0) return;

    setSaving(true);
    try {
      await luminiApi.post('/inventory/transfers', {
        fromWarehouseId,
        toWarehouseId,
        date: transferDate,
        notes: notes || undefined,
        details: lines.map((l) => ({
          productId: l.productId,
          unitId: l.unitId,
          quantity: l.quantity,
          unitCost: l.unitCost,
        })),
      });
      setDialogOpen(false);
      loadTransfers();
    } catch (err: any) {
      alert(err?.response?.data?.message ?? 'Error al crear el traspaso');
    } finally {
      setSaving(false);
    }
  }

  async function confirmTransfer(id: string) {
    try {
      await luminiApi.post(`/inventory/transfers/${id}/confirm`);
      loadTransfers();
    } catch (err: any) {
      alert(err?.response?.data?.message ?? 'Error al confirmar');
    }
  }

  async function cancelTransfer(id: string) {
    try {
      await luminiApi.post(`/inventory/transfers/${id}/cancel`);
      loadTransfers();
    } catch (err: any) {
      alert(err?.response?.data?.message ?? 'Error al cancelar');
    }
  }

  const filtered = transfers.filter((t) =>
    t.fromWarehouse.name.toLowerCase().includes(search.toLowerCase()) ||
    t.toWarehouse.name.toLowerCase().includes(search.toLowerCase()) ||
    t.id.slice(0, 8).includes(search),
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
          <Input placeholder="Buscar traspaso..." value={search} onChange={(e) => setSearch(e.target.value)} className="pl-8 h-9 text-sm" />
        </div>
        <Button size="sm" className="bg-violet-600 hover:bg-violet-700 text-white" onClick={openNewTransfer}>
          <Plus className="w-4 h-4 mr-1" /> Nuevo traspaso
        </Button>
      </div>

      {/* Transfers list with accordion */}
      <div className="bg-white rounded-xl border border-gray-100 overflow-hidden">
        {filtered.length === 0 ? (
          <div className="text-center py-12 text-gray-400 text-sm">
            <ArrowLeftRight className="w-8 h-8 mx-auto mb-2 text-gray-200" />
            Sin traspasos registrados
          </div>
        ) : (
          <Accordion type="single" collapsible className="w-full">
            {filtered.map((t) => (
              <AccordionItem key={t.id} value={t.id} className="border-b border-gray-100">
                <AccordionTrigger
                  className="px-4 py-3 hover:no-underline hover:bg-gray-50 gap-3"
                  onClick={() => loadTransferDetails(t.id)}
                >
                  <div className="flex items-center gap-3 flex-1 min-w-0 text-left">
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2 flex-wrap">
                        <span className="text-sm font-medium text-gray-700">{t.fromWarehouse.name}</span>
                        <ArrowRight className="w-3.5 h-3.5 text-violet-400 shrink-0" />
                        <span className="text-sm font-medium text-gray-700">{t.toWarehouse.name}</span>
                        <Badge className={`${STATUS_CLS[t.status] ?? 'bg-gray-100 text-gray-600'} text-[10px]`}>
                          {STATUS_LABEL[t.status] ?? t.status}
                        </Badge>
                      </div>
                      <div className="flex items-center gap-3 mt-1 text-xs text-gray-400">
                        <span>{formatDate(t.date || t.createdAt)}</span>
                        <span>{t._count?.details ?? 0} items</span>
                        {t.notes && <span className="truncate max-w-40" title={t.notes}>{t.notes}</span>}
                      </div>
                    </div>
                    <div className="flex items-center gap-2 shrink-0">
                      <button
                        onClick={(e) => { e.stopPropagation(); handleDownloadPdf(t.id); }}
                        title="Descargar PDF"
                        className="text-violet-500 hover:text-violet-700 p-1"
                      >
                        <FileText className="w-4 h-4" />
                      </button>
                      {(t.status === 'PENDING' || t.status === 'IN_TRANSIT') && (
                        <>
                          <button onClick={(e) => { e.stopPropagation(); confirmTransfer(t.id); }} title="Confirmar" className="text-green-600 hover:text-green-800 p-1">
                            <Check className="w-4 h-4" />
                          </button>
                          <button onClick={(e) => { e.stopPropagation(); cancelTransfer(t.id); }} title="Cancelar" className="text-red-500 hover:text-red-700 p-1">
                            <XCircle className="w-4 h-4" />
                          </button>
                        </>
                      )}
                    </div>
                  </div>
                </AccordionTrigger>
                <AccordionContent className="px-4 pb-4">
                  {loadingDetails[t.id] ? (
                    <div className="flex items-center justify-center py-4">
                      <Loader2 className="w-4 h-4 animate-spin text-violet-400" />
                      <span className="ml-2 text-xs text-gray-400">Cargando detalles...</span>
                    </div>
                  ) : t.details && t.details.length > 0 ? (
                    <div className="border border-gray-100 rounded-lg overflow-hidden">
                      <Table>
                        <TableHeader>
                          <TableRow className="bg-gray-50 hover:bg-gray-50">
                            <TableHead className="text-xs font-semibold text-gray-500">SKU</TableHead>
                            <TableHead className="text-xs font-semibold text-gray-500">Producto</TableHead>
                            <TableHead className="text-xs font-semibold text-gray-500 text-center">Unidad</TableHead>
                            <TableHead className="text-xs font-semibold text-gray-500 text-center">Cantidad</TableHead>
                            <TableHead className="text-xs font-semibold text-gray-500 text-right">Costo Unit.</TableHead>
                          </TableRow>
                        </TableHeader>
                        <TableBody>
                          {t.details.map((d) => (
                            <TableRow key={d.id}>
                              <TableCell className="text-xs text-gray-500 font-mono">{d.product.sku}</TableCell>
                              <TableCell className="text-xs text-gray-700">{d.product.name}</TableCell>
                              <TableCell className="text-xs text-center text-gray-500">{d.unit.abbreviation || d.unit.name}</TableCell>
                              <TableCell className="text-xs text-center">{d.quantity}</TableCell>
                              <TableCell className="text-xs text-right">Bs {Number(d.unitCost).toFixed(2)}</TableCell>
                            </TableRow>
                          ))}
                        </TableBody>
                      </Table>
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

      {/* ─── New Transfer Dialog ──────────────────────────────────────── */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent className="sm:max-w-2xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>Nuevo traspaso</DialogTitle>
          </DialogHeader>

          <div className="space-y-4 mt-2">
            {/* Warehouses + Date */}
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <div>
                <Label className="text-xs mb-1">Almacen origen</Label>
                <select
                  value={fromWarehouseId}
                  onChange={(e) => setFromWarehouseId(e.target.value)}
                  className="w-full h-9 text-sm border border-gray-200 rounded-md px-2 bg-white focus:outline-none focus:ring-1 focus:ring-violet-500"
                >
                  <option value="">Seleccionar...</option>
                  {warehouses.map((w) => (
                    <option key={w.id} value={w.id} disabled={w.id === toWarehouseId}>{w.name}</option>
                  ))}
                </select>
              </div>
              <div>
                <Label className="text-xs mb-1">Almacen destino</Label>
                <select
                  value={toWarehouseId}
                  onChange={(e) => setToWarehouseId(e.target.value)}
                  className="w-full h-9 text-sm border border-gray-200 rounded-md px-2 bg-white focus:outline-none focus:ring-1 focus:ring-violet-500"
                >
                  <option value="">Seleccionar...</option>
                  {warehouses.map((w) => (
                    <option key={w.id} value={w.id} disabled={w.id === fromWarehouseId}>{w.name}</option>
                  ))}
                </select>
              </div>
              <div>
                <Label className="text-xs mb-1">Fecha</Label>
                <Input
                  type="date"
                  value={transferDate}
                  onChange={(e) => setTransferDate(e.target.value)}
                  className="h-9 text-sm"
                />
              </div>
            </div>

            {/* Arrow indicator */}
            {fromWarehouseId && toWarehouseId && fromWarehouseId !== toWarehouseId && (
              <div className="flex items-center gap-2 bg-violet-50 rounded-lg px-3 py-2">
                <span className="text-xs font-medium text-violet-700">
                  {warehouses.find((w) => w.id === fromWarehouseId)?.name}
                </span>
                <ArrowRight className="w-4 h-4 text-violet-400" />
                <span className="text-xs font-medium text-violet-700">
                  {warehouses.find((w) => w.id === toWarehouseId)?.name}
                </span>
              </div>
            )}

            {fromWarehouseId === toWarehouseId && fromWarehouseId && (
              <p className="text-xs text-red-500 font-medium">Origen y destino no pueden ser el mismo</p>
            )}

            {/* Notes */}
            <div>
              <Label className="text-xs mb-1">Notas (opcional)</Label>
              <Input
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
                placeholder="Motivo del traspaso..."
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
                    type="number" min="1"
                    value={lineQty}
                    onChange={(e) => setLineQty(e.target.value)}
                    placeholder="Cant."
                    className="h-8 text-xs"
                  />
                </div>
                <div className="flex gap-1">
                  <Input
                    type="number" min="0" step="0.01"
                    value={lineCost}
                    onChange={(e) => setLineCost(e.target.value)}
                    placeholder="Costo"
                    className="h-8 text-xs flex-1"
                  />
                  <Button
                    size="sm" type="button" onClick={addLine}
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
                      <TableHead className="text-xs text-right">Costo Unit.</TableHead>
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
                        <TableCell>
                          <button onClick={() => removeLine(i)} className="text-red-400 hover:text-red-600">
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
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
                disabled={saving || lines.length === 0 || !fromWarehouseId || !toWarehouseId || fromWarehouseId === toWarehouseId}
                onClick={saveTransfer}
              >
                {saving && <Loader2 className="w-3 h-3 animate-spin mr-1" />}
                Crear traspaso
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
