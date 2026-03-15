import { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import {
  Search, Loader2, TrendingUp, Check, XCircle, FileText,
  ShoppingCart, DollarSign, Lock, Clock, AlertTriangle, Printer,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { luminiApi } from '@/services/luminiApi';
import { useWarehouse } from '../components/AdminLayout';
import { SalesCartDrawer } from '../components/SalesCartDrawer';
import { useAppSelector } from '@/hooks/useStore';
import type { RootState } from '@/store';
import {
  addItem,
  openCart,
  clearCart,
} from '@/store/salesCart/salesCartSlice';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

// ─── Types ──────────────────────────────────────────────────────────────────

interface OutputDetail {
  id: string;
  quantity: number;
  unitCost: number;
  salePrice: number;
  totalCost: number;
  product: { id: string; name: string; sku: string };
  unit: { id: string; name: string; abbreviation: string };
}

interface OutputRecord {
  id: string;
  type: string;
  documentNumber?: string;
  date: string;
  notes?: string;
  status: string;
  total: number;
  customer?: { id: string; name: string; lastName?: string };
  warehouse?: { id: string; name: string };
  _count?: { details: number };
  details?: OutputDetail[];
  createdAt: string;
  updatedAt: string;
}

interface Product {
  id: string;
  name: string;
  sku: string;
  baseUnit?: { id: string; name: string; abbreviation: string };
  salePrice?: number;
  purchasePrice?: number;
  siatActivityCode?: number;
  siatProductServiceCode?: number;
  siatMeasurementUnitId?: number;
}

interface CustomerOption { id: string; name: string; lastName?: string }

type PaperSize = 'LETTER' | 'ROLL_80' | 'ROLL_58';

interface CashSession {
  id: string;
  status: 'OPEN' | 'CLOSED';
  openingAmount: number;
  closingAmount?: number;
  expectedAmount?: number;
  difference?: number;
  openedAt: string;
  closedAt?: string;
  notes?: string;
  cashierId: string;
  pointOfSale: {
    id: string;
    name: string;
    paperSize?: PaperSize;
    branch: { id: string; name: string };
  };
}

interface BranchWithPos {
  id: string;
  name: string;
  pointsOfSale: { id: string; name: string }[];
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

function formatDateTime(d: string) {
  try { return new Date(d).toLocaleString('es-BO', { dateStyle: 'medium', timeStyle: 'short' }); }
  catch { return d; }
}

function customerName(c?: { name: string; lastName?: string }) {
  if (!c) return 'Consumidor final';
  return `${c.name} ${c.lastName ?? ''}`.trim();
}

// ─── PDF ────────────────────────────────────────────────────────────────────

const PAPER_CONFIGS: Record<PaperSize, { width: number; height: number; unit: 'mm'; orientation: 'portrait' }> = {
  LETTER:  { width: 215.9, height: 279.4, unit: 'mm', orientation: 'portrait' },
  ROLL_80: { width: 80, height: 297, unit: 'mm', orientation: 'portrait' },
  ROLL_58: { width: 58, height: 297, unit: 'mm', orientation: 'portrait' },
};

const PAPER_LABELS: Record<PaperSize, string> = {
  LETTER: 'Carta',
  ROLL_80: 'Rollo 80mm',
  ROLL_58: 'Rollo 58mm',
};

function generateSalePdf(output: OutputRecord, paperSize: PaperSize = 'LETTER'): string {
  const cfg = PAPER_CONFIGS[paperSize];
  const isRoll = paperSize !== 'LETTER';
  const pageW = cfg.width;
  const margin = isRoll ? 3 : 14;
  const contentW = pageW - margin * 2;
  const centerX = pageW / 2;

  const doc = new jsPDF({ orientation: 'portrait', unit: 'mm', format: [cfg.width, cfg.height] });

  if (isRoll) {
    // ─── POS Roll layout — first pass to measure height ────────────
    const fs = paperSize === 'ROLL_58' ? 6.5 : 7;
    const details = output.details ?? [];

    // Estimate height: header(18) + meta(10) + items + total(13) + footer(6)
    let estY = 18 + 10 + (output.warehouse?.name ? 3.5 : 0);
    details.forEach((d) => {
      const lineCount = Math.ceil((d.product.name.length * (fs * 0.35)) / contentW);
      estY += lineCount * 3 + 4;
    });
    estY += 17; // separators + total + footer

    return generateSalePdf_roll(output, paperSize, estY + 4);
  } else {
    // ─── Letter layout (original) ──────────────────────────────────
    doc.setFontSize(18);
    doc.setFont('helvetica', 'bold');
    doc.text('NOTA DE VENTA', centerX, 20, { align: 'center' });

    doc.setFontSize(10);
    doc.setFont('helvetica', 'normal');
    doc.setTextColor(100);
    doc.text(`N° ${output.documentNumber || output.id.slice(0, 8).toUpperCase()}`, centerX, 28, { align: 'center' });

    doc.setTextColor(0);
    const y = 40;
    doc.setFont('helvetica', 'bold');
    doc.text('Cliente:', 14, y);
    doc.setFont('helvetica', 'normal');
    doc.text(customerName(output.customer), 50, y);

    doc.setFont('helvetica', 'bold');
    doc.text('Almacen:', 14, y + 7);
    doc.setFont('helvetica', 'normal');
    doc.text(output.warehouse?.name ?? '-', 50, y + 7);

    doc.setFont('helvetica', 'bold');
    doc.text('Fecha:', 120, y);
    doc.setFont('helvetica', 'normal');
    doc.text(formatDate(output.date || output.createdAt), 145, y);

    doc.setFont('helvetica', 'bold');
    doc.text('Estado:', 120, y + 7);
    doc.setFont('helvetica', 'normal');
    doc.text(STATUS_LABEL[output.status] ?? output.status, 145, y + 7);

    if (output.notes) {
      doc.setFont('helvetica', 'bold');
      doc.text('Notas:', 14, y + 14);
      doc.setFont('helvetica', 'normal');
      doc.text(output.notes, 50, y + 14);
    }

    const details = output.details ?? [];
    autoTable(doc, {
      startY: y + (output.notes ? 22 : 16),
      head: [['#', 'SKU', 'Producto', 'Unidad', 'Cant.', 'Precio', 'Subtotal']],
      body: details.map((d, i) => [
        i + 1,
        d.product.sku,
        d.product.name,
        d.unit.abbreviation || d.unit.name,
        d.quantity,
        `Bs ${Number(d.salePrice).toFixed(2)}`,
        `Bs ${(Number(d.quantity) * Number(d.salePrice)).toFixed(2)}`,
      ]),
      styles: { fontSize: 9 },
      headStyles: { fillColor: [109, 40, 217] },
      columnStyles: { 0: { cellWidth: 10 }, 4: { halign: 'center' }, 5: { halign: 'right' }, 6: { halign: 'right' } },
    });

    const finalY = (doc as any).lastAutoTable.finalY + 8;
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(12);
    doc.text(`Total: Bs ${Number(output.total).toFixed(2)}`, 196, finalY, { align: 'right' });

    doc.setFontSize(8);
    doc.setFont('helvetica', 'normal');
    doc.setTextColor(150);
    doc.text(`Generado el ${new Date().toLocaleDateString('es-BO')} - Luminia`, centerX, 275, { align: 'center' });

    return String(doc.output('bloburl'));
  }
}

/** Roll-specific generator that trims page height to content */
function generateSalePdf_roll(output: OutputRecord, paperSize: PaperSize, totalHeight: number): string {
  const cfg = PAPER_CONFIGS[paperSize];
  const pageW = cfg.width;
  const margin = 3;
  const contentW = pageW - margin * 2;
  const centerX = pageW / 2;
  const fs = paperSize === 'ROLL_58' ? 6.5 : 7;

  const doc = new jsPDF({ orientation: 'portrait', unit: 'mm', format: [pageW, totalHeight] });

  let y = 6;

  doc.setFontSize(paperSize === 'ROLL_58' ? 10 : 12);
  doc.setFont('helvetica', 'bold');
  doc.text('NOTA DE VENTA', centerX, y, { align: 'center' });
  y += 5;

  doc.setFontSize(8);
  doc.setFont('helvetica', 'normal');
  doc.text(`N° ${output.documentNumber || output.id.slice(0, 8).toUpperCase()}`, centerX, y, { align: 'center' });
  y += 4;

  doc.setLineDashPattern([1, 1], 0);
  doc.line(margin, y, pageW - margin, y);
  y += 3;

  doc.setFontSize(fs);
  doc.text(`Cliente: ${customerName(output.customer)}`, margin, y);
  y += 3.5;
  doc.text(`Fecha: ${formatDate(output.date || output.createdAt)}`, margin, y);
  y += 3.5;
  if (output.warehouse?.name) {
    doc.text(`Almacén: ${output.warehouse.name}`, margin, y);
    y += 3.5;
  }

  doc.setLineDashPattern([1, 1], 0);
  doc.line(margin, y, pageW - margin, y);
  y += 3;

  const details = output.details ?? [];
  details.forEach((d) => {
    doc.setFont('helvetica', 'normal');
    const qty = Number(d.quantity);
    const price = Number(d.salePrice);
    const sub = qty * price;
    const nameLines = doc.splitTextToSize(d.product.name, contentW - 4);
    nameLines.forEach((line: string) => {
      doc.text(line, margin, y);
      y += 3;
    });
    doc.text(`  ${qty} x Bs ${price.toFixed(2)}`, margin, y);
    doc.text(`Bs ${sub.toFixed(2)}`, pageW - margin, y, { align: 'right' });
    y += 4;
  });

  doc.setLineDashPattern([1, 1], 0);
  doc.line(margin, y, pageW - margin, y);
  y += 4;

  doc.setFont('helvetica', 'bold');
  doc.setFontSize(fs + 2);
  doc.text('TOTAL:', margin, y);
  doc.text(`Bs ${Number(output.total).toFixed(2)}`, pageW - margin, y, { align: 'right' });
  y += 5;

  doc.setFontSize(6);
  doc.setFont('helvetica', 'normal');
  doc.setTextColor(120);
  doc.text(`${new Date().toLocaleDateString('es-BO')} - Luminia`, centerX, y, { align: 'center' });

  return String(doc.output('bloburl'));
}

// ─── Component ──────────────────────────────────────────────────────────────

type Tab = 'products' | 'history' | 'caja';

export function SalesSection() {
  const { selectedWarehouse } = useWarehouse();
  const dispatch = useDispatch();
  const cartItems = useAppSelector((state: RootState) => state.salesCart.items);
  const cartState = useAppSelector((state: RootState) => state.salesCart);

  const businessId = localStorage.getItem('luminia_business_id') ?? '';

  // ─── Sales history ────────────────────────────────────────────────
  const [outputs, setOutputs] = useState<OutputRecord[]>([]);
  const [loadingOutputs, setLoadingOutputs] = useState(true);
  const [loadingDetails, setLoadingDetails] = useState<Record<string, boolean>>({});

  // ─── Products ─────────────────────────────────────────────────────
  const [products, setProducts] = useState<Product[]>([]);
  const [customers, setCustomers] = useState<CustomerOption[]>([]);
  const [loadingProducts, setLoadingProducts] = useState(true);
  const [productSearch, setProductSearch] = useState('');
  const [historySearch, setHistorySearch] = useState('');

  const [tab, setTab] = useState<Tab>('products');
  const [saving, setSaving] = useState(false);

  // ─── Cash register ───────────────────────────────────────────────
  const [activeSession, setActiveSession] = useState<CashSession | null>(null);
  const [loadingSession, setLoadingSession] = useState(true);
  const [branches, setBranches] = useState<BranchWithPos[]>([]);
  const [cashSessions, setCashSessions] = useState<CashSession[]>([]);
  const [loadingSessions, setLoadingSessions] = useState(false);

  // open cash dialog
  const [openCashDialog, setOpenCashDialog] = useState(false);
  const [cashPosId, setCashPosId] = useState('');
  const [cashOpeningAmount, setCashOpeningAmount] = useState('');
  const [cashNotes, setCashNotes] = useState('');
  const [openingCash, setOpeningCash] = useState(false);

  // close cash dialog
  const [closeCashDialog, setCloseCashDialog] = useState(false);
  const [cashClosingAmount, setCashClosingAmount] = useState('');
  const [closeNotes, setCloseNotes] = useState('');
  const [closingCash, setClosingCash] = useState(false);

  // PDF preview dialog
  const [pdfPreviewOpen, setPdfPreviewOpen] = useState(false);
  const [pdfPreviewUrl, setPdfPreviewUrl] = useState('');
  const [pdfPaperSize, setPdfPaperSize] = useState<PaperSize>('LETTER');
  const [pdfOutput, setPdfOutput] = useState<OutputRecord | null>(null);

  useEffect(() => {
    loadOutputs();
    loadCatalog();
    loadActiveSession();
  }, []);

  // ─── Loaders ──────────────────────────────────────────────────────

  async function loadActiveSession() {
    setLoadingSession(true);
    try {
      const { data } = await luminiApi.get(`/business/${businessId}/cash-register/active`);
      setActiveSession(data ?? null);
    } catch {
      setActiveSession(null);
    } finally {
      setLoadingSession(false);
    }
  }

  async function loadBranches() {
    try {
      const { data } = await luminiApi.get(`/business/${businessId}/branches`);
      setBranches(Array.isArray(data) ? data : []);
      // auto-select first POS if available
      const allPos = (data as BranchWithPos[]).flatMap((b) => b.pointsOfSale);
      if (allPos.length > 0 && !cashPosId) setCashPosId(allPos[0].id);
    } catch {
      setBranches([]);
    }
  }

  async function loadCashSessions() {
    setLoadingSessions(true);
    try {
      const { data } = await luminiApi.get(`/business/${businessId}/cash-register`, { params: { take: '20' } });
      setCashSessions(data?.items ?? []);
    } catch {
      setCashSessions([]);
    } finally {
      setLoadingSessions(false);
    }
  }

  async function loadOutputs() {
    setLoadingOutputs(true);
    try {
      const { data } = await luminiApi.get('/inventory/outputs', { params: { type: 'SALE' } });
      setOutputs(Array.isArray(data) ? data : []);
    } catch {
      setOutputs([]);
    } finally {
      setLoadingOutputs(false);
    }
  }

  async function loadCatalog() {
    setLoadingProducts(true);
    try {
      const [prodRes, custRes] = await Promise.all([
        luminiApi.get('/inventory/products'),
        luminiApi.get(`/business/${businessId}/customers`),
      ]);
      setProducts(Array.isArray(prodRes.data) ? prodRes.data : []);
      setCustomers(Array.isArray(custRes.data) ? custRes.data : []);
    } catch { /* silenciar */ }
    finally { setLoadingProducts(false); }
  }

  async function loadOutputDetails(id: string) {
    const existing = outputs.find((o) => o.id === id);
    if (existing?.details) return;
    setLoadingDetails((prev) => ({ ...prev, [id]: true }));
    try {
      const { data } = await luminiApi.get(`/inventory/outputs/${id}`);
      setOutputs((prev) => prev.map((o) => (o.id === id ? { ...o, details: data.details } : o)));
    } catch { /* silenciar */ }
    finally { setLoadingDetails((prev) => ({ ...prev, [id]: false })); }
  }

  // ─── Cash register actions ────────────────────────────────────────

  function handleOpenCashDialog() {
    loadBranches();
    setCashOpeningAmount('');
    setCashNotes('');
    setOpenCashDialog(true);
  }

  async function handleOpenCash() {
    if (!cashPosId || !cashOpeningAmount) return;
    setOpeningCash(true);
    try {
      const { data } = await luminiApi.post(`/business/${businessId}/cash-register/open`, {
        pointOfSaleId: cashPosId,
        openingAmount: Number(cashOpeningAmount),
        notes: cashNotes || undefined,
      });
      setActiveSession(data);
      setOpenCashDialog(false);
      loadCashSessions();
    } catch (err: any) {
      alert(err?.response?.data?.message ?? 'Error al abrir caja');
    } finally {
      setOpeningCash(false);
    }
  }

  function handleCloseCashDialog() {
    setCashClosingAmount('');
    setCloseNotes('');
    setCloseCashDialog(true);
  }

  async function handleCloseCash() {
    if (!activeSession || !cashClosingAmount) return;
    setClosingCash(true);
    try {
      await luminiApi.post(`/business/${businessId}/cash-register/close`, {
        sessionId: activeSession.id,
        closingAmount: Number(cashClosingAmount),
        notes: closeNotes || undefined,
      });
      setActiveSession(null);
      setCloseCashDialog(false);
      loadCashSessions();
    } catch (err: any) {
      alert(err?.response?.data?.message ?? 'Error al cerrar caja');
    } finally {
      setClosingCash(false);
    }
  }

  // ─── Sales actions ────────────────────────────────────────────────

  async function handleDownloadPdf(id: string) {
    let output = outputs.find((o) => o.id === id);
    if (!output?.details) {
      setLoadingDetails((prev) => ({ ...prev, [id]: true }));
      try {
        const { data } = await luminiApi.get(`/inventory/outputs/${id}`);
        output = { ...output!, details: data.details };
        setOutputs((prev) => prev.map((o) => (o.id === id ? output! : o)));
      } catch {
        alert('Error al cargar detalles para el PDF');
        return;
      } finally {
        setLoadingDetails((prev) => ({ ...prev, [id]: false }));
      }
    }
    // Default paper size from active session POS config, fallback to LETTER
    const defaultSize: PaperSize = activeSession?.pointOfSale?.paperSize ?? 'LETTER';
    setPdfOutput(output!);
    setPdfPaperSize(defaultSize);
    setPdfPreviewUrl(generateSalePdf(output!, defaultSize));
    setPdfPreviewOpen(true);
  }

  function handlePaperSizeChange(size: PaperSize) {
    if (!pdfOutput) return;
    setPdfPaperSize(size);
    // Revoke previous blob URL
    if (pdfPreviewUrl) URL.revokeObjectURL(pdfPreviewUrl);
    setPdfPreviewUrl(generateSalePdf(pdfOutput, size));
  }

  function handlePrintPdf() {
    if (!pdfPreviewUrl) return;
    const printWindow = window.open(pdfPreviewUrl, '_blank');
    if (printWindow) {
      printWindow.addEventListener('load', () => {
        printWindow.print();
      });
    }
  }

  function handleAddToCart(product: Product) {
    if (!product.baseUnit) return;
    dispatch(addItem({
      productId: product.id,
      productName: product.name,
      sku: product.sku,
      unitId: product.baseUnit.id,
      unitName: product.baseUnit.abbreviation || product.baseUnit.name,
      quantity: 1,
      unitCost: Number(product.purchasePrice ?? 0),
      salePrice: Number(product.salePrice ?? 0),
      siatActivityCode: product.siatActivityCode,
      siatProductServiceCode: product.siatProductServiceCode,
      siatMeasurementUnitId: product.siatMeasurementUnitId,
    }));
  }

  async function handleCheckout() {
    if (!selectedWarehouse) {
      alert('Selecciona un almacen en el header');
      return;
    }
    if (cartItems.length === 0) return;
    if (!activeSession) {
      alert('Debes abrir caja antes de vender');
      setTab('caja');
      return;
    }

    setSaving(true);
    try {
      // 1. Create the sale output
      const { data: output } = await luminiApi.post('/inventory/outputs', {
        warehouseId: selectedWarehouse.id,
        customerId: cartState.customerId || undefined,
        type: 'SALE',
        documentNumber: cartState.documentNumber || undefined,
        date: new Date().toISOString().slice(0, 10),
        notes: cartState.notes || undefined,
        details: cartItems.map((i) => ({
          productId: i.productId,
          unitId: i.unitId,
          quantity: i.quantity,
          unitCost: i.unitCost,
          salePrice: i.salePrice,
        })),
      });

      // 2. Add sale amount to cash register session
      const saleTotal = cartItems.reduce((s, i) => s + i.quantity * i.salePrice, 0);
      try {
        await luminiApi.post(`/business/${businessId}/cash-register/add-sale`, {
          sessionId: activeSession.id,
          amount: saleTotal,
        });
        // Update local session expected amount
        setActiveSession((prev) => prev ? {
          ...prev,
          expectedAmount: Number(prev.expectedAmount ?? prev.openingAmount) + saleTotal,
        } : null);
      } catch {
        // Non-critical — sale is already recorded
      }

      // 3. If factura, issue SIAT invoice
      if (cartState.documentType === 'factura') {
        try {
          await luminiApi.post('/billing/invoices/simple', {
            outputId: output.id,
            buyerNit: cartState.buyerNit,
            buyerName: cartState.customerLabel || 'SIN NOMBRE',
            details: cartItems.map((i) => ({
              productId: i.productId,
              productName: i.productName,
              quantity: i.quantity,
              unitPrice: i.salePrice,
              subtotal: i.quantity * i.salePrice,
              siatActivityCode: i.siatActivityCode,
              siatProductServiceCode: i.siatProductServiceCode,
              siatMeasurementUnitId: i.siatMeasurementUnitId,
            })),
            total: saleTotal,
          });
        } catch (err: any) {
          alert('Venta registrada pero error al emitir factura SIAT: ' + (err?.response?.data?.message ?? 'Error desconocido'));
        }
      }

      dispatch(clearCart());
      loadOutputs();
    } catch (err: any) {
      alert(err?.response?.data?.message ?? 'Error al crear la venta');
    } finally {
      setSaving(false);
    }
  }

  async function confirmOutput(id: string) {
    try {
      await luminiApi.post(`/inventory/outputs/${id}/confirm`);
      loadOutputs();
    } catch (err: any) {
      alert(err?.response?.data?.message ?? 'Error al confirmar');
    }
  }

  async function cancelOutput(id: string) {
    try {
      await luminiApi.post(`/inventory/outputs/${id}/cancel`);
      loadOutputs();
    } catch (err: any) {
      alert(err?.response?.data?.message ?? 'Error al cancelar');
    }
  }

  // ─── Filters ──────────────────────────────────────────────────────
  const filteredProducts = products.filter((p) =>
    p.name.toLowerCase().includes(productSearch.toLowerCase()) ||
    p.sku.toLowerCase().includes(productSearch.toLowerCase()),
  );

  const filteredOutputs = outputs.filter((o) =>
    (o.customer ? `${o.customer.name} ${o.customer.lastName ?? ''}` : '').toLowerCase().includes(historySearch.toLowerCase()) ||
    (o.documentNumber ?? '').toLowerCase().includes(historySearch.toLowerCase()) ||
    o.id.slice(0, 8).includes(historySearch),
  );

  const totalConfirmed = outputs.filter((o) => o.status === 'CONFIRMED').reduce((s, o) => s + Number(o.total), 0);
  const cartCount = cartItems.reduce((s, i) => s + i.quantity, 0);

  const allPos = branches.flatMap((b) => b.pointsOfSale.map((p) => ({ ...p, branchName: b.name })));

  // Load cash sessions when switching to caja tab
  useEffect(() => {
    if (tab === 'caja') {
      loadCashSessions();
      if (branches.length === 0) loadBranches();
    }
  }, [tab]);

  // ─── Render ───────────────────────────────────────────────────────

  return (
    <div className="space-y-4">
      {/* Top bar: tabs + cart button */}
      <div className="flex gap-3 items-center flex-wrap">
        <div className="flex bg-gray-100 rounded-lg p-0.5">
          <button
            onClick={() => setTab('products')}
            className={`px-3 py-1.5 text-xs font-medium rounded-md transition ${
              tab === 'products' ? 'bg-white text-violet-700 shadow-sm' : 'text-gray-500 hover:text-gray-700'
            }`}
          >
            Productos
          </button>
          <button
            onClick={() => setTab('history')}
            className={`px-3 py-1.5 text-xs font-medium rounded-md transition ${
              tab === 'history' ? 'bg-white text-violet-700 shadow-sm' : 'text-gray-500 hover:text-gray-700'
            }`}
          >
            Historial
          </button>
          <button
            onClick={() => setTab('caja')}
            className={`px-3 py-1.5 text-xs font-medium rounded-md transition flex items-center gap-1 ${
              tab === 'caja' ? 'bg-white text-violet-700 shadow-sm' : 'text-gray-500 hover:text-gray-700'
            }`}
          >
            <DollarSign className="w-3 h-3" />
            Caja
            {activeSession && (
              <span className="w-1.5 h-1.5 rounded-full bg-green-500" />
            )}
          </button>
        </div>

        {(tab === 'products' || tab === 'history') && (
          <div className="relative flex-1 max-w-xs">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-gray-400" />
            {tab === 'products' ? (
              <Input
                placeholder="Buscar producto..."
                value={productSearch}
                onChange={(e) => setProductSearch(e.target.value)}
                className="pl-8 h-9 text-sm"
              />
            ) : (
              <Input
                placeholder="Buscar venta..."
                value={historySearch}
                onChange={(e) => setHistorySearch(e.target.value)}
                className="pl-8 h-9 text-sm"
              />
            )}
          </div>
        )}

        <div className="ml-auto flex items-center gap-2">
          {tab === 'history' && (
            <div className="bg-violet-50 border border-violet-100 rounded-lg px-4 py-1.5">
              <span className="text-xs text-violet-500 font-medium">Cobrado:</span>
              <span className="text-sm font-bold text-violet-700 ml-1">Bs {totalConfirmed.toLocaleString()}</span>
            </div>
          )}
          {tab !== 'caja' && (
            <Button
              size="sm"
              className="bg-violet-600 hover:bg-violet-700 text-white relative"
              onClick={() => dispatch(openCart())}
            >
              <ShoppingCart className="w-4 h-4 mr-1" />
              Carrito
              {cartCount > 0 && (
                <span className="absolute -top-1.5 -right-1.5 bg-red-500 text-white text-[10px] font-bold w-5 h-5 rounded-full flex items-center justify-center">
                  {cartCount}
                </span>
              )}
            </Button>
          )}
        </div>
      </div>

      {/* ─── Products Tab ─────────────────────────────────────────────── */}
      {tab === 'products' && (
        <>
          {/* Gate: must have open cash register */}
          {!loadingSession && !activeSession && (
            <div className="bg-amber-50 border border-amber-200 rounded-xl p-4 flex items-center gap-3">
              <Lock className="w-5 h-5 text-amber-500 shrink-0" />
              <div className="flex-1">
                <p className="text-sm font-medium text-amber-800">Debes abrir caja antes de vender</p>
                <p className="text-xs text-amber-600 mt-0.5">Ve a la pestaña "Caja" para iniciar tu turno</p>
              </div>
              <Button size="sm" variant="outline" className="border-amber-300 text-amber-700 hover:bg-amber-100" onClick={() => setTab('caja')}>
                Abrir caja
              </Button>
            </div>
          )}

          {loadingProducts ? (
            <div className="flex items-center justify-center py-20">
              <Loader2 className="w-6 h-6 animate-spin text-violet-500" />
            </div>
          ) : filteredProducts.length === 0 ? (
            <div className="text-center py-16 text-gray-400 text-sm">
              Sin productos encontrados
            </div>
          ) : (
            <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-3">
              {filteredProducts.map((p) => {
                const inCart = cartItems.find((i) => i.productId === p.id);
                return (
                  <button
                    key={p.id}
                    onClick={() => handleAddToCart(p)}
                    disabled={!p.baseUnit}
                    className={`relative bg-white rounded-xl border p-3 text-left transition hover:shadow-md hover:border-violet-200 active:scale-[0.98] disabled:opacity-40 disabled:cursor-not-allowed ${
                      inCart ? 'border-violet-300 ring-1 ring-violet-200' : 'border-gray-100'
                    }`}
                  >
                    {inCart && (
                      <span className="absolute top-2 right-2 bg-violet-600 text-white text-[10px] font-bold w-5 h-5 rounded-full flex items-center justify-center">
                        {inCart.quantity}
                      </span>
                    )}
                    <p className="text-xs text-gray-400 font-mono">{p.sku}</p>
                    <p className="text-sm font-medium text-gray-800 mt-1 truncate">{p.name}</p>
                    <div className="flex items-baseline gap-1 mt-2">
                      <span className="text-sm font-bold text-violet-700">
                        Bs {Number(p.salePrice ?? 0).toFixed(2)}
                      </span>
                      {p.baseUnit && (
                        <span className="text-[10px] text-gray-400">/{p.baseUnit.abbreviation}</span>
                      )}
                    </div>
                    {!p.baseUnit && (
                      <p className="text-[10px] text-red-400 mt-1">Sin unidad base</p>
                    )}
                  </button>
                );
              })}
            </div>
          )}
        </>
      )}

      {/* ─── History Tab ──────────────────────────────────────────────── */}
      {tab === 'history' && (
        loadingOutputs ? (
          <div className="flex items-center justify-center py-20">
            <Loader2 className="w-6 h-6 animate-spin text-violet-500" />
          </div>
        ) : (
          <div className="bg-white rounded-xl border border-gray-100 overflow-hidden">
            {filteredOutputs.length === 0 ? (
              <div className="text-center py-12 text-gray-400 text-sm">
                <TrendingUp className="w-8 h-8 mx-auto mb-2 text-gray-200" />
                Sin ventas registradas
              </div>
            ) : (
              <Accordion type="single" collapsible className="w-full">
                {filteredOutputs.map((o) => (
                  <AccordionItem key={o.id} value={o.id} className="border-b border-gray-100">
                    <AccordionTrigger
                      className="px-4 py-3 hover:no-underline hover:bg-gray-50 gap-3"
                      onClick={() => loadOutputDetails(o.id)}
                    >
                      <div className="flex items-center gap-3 flex-1 min-w-0 text-left">
                        <div className="flex-1 min-w-0">
                          <div className="flex items-center gap-2 flex-wrap">
                            <span className="text-sm font-medium text-gray-700">{customerName(o.customer)}</span>
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
                          </div>
                        </div>
                        <div className="flex items-center gap-2 shrink-0">
                          <span className="font-semibold text-sm text-gray-800">Bs {Number(o.total).toLocaleString()}</span>
                          <button
                            onClick={(e) => { e.stopPropagation(); handleDownloadPdf(o.id); }}
                            title="Descargar nota de venta"
                            className="text-violet-500 hover:text-violet-700 p-1"
                          >
                            <FileText className="w-4 h-4" />
                          </button>
                          {o.status === 'DRAFT' && (
                            <>
                              <button onClick={(e) => { e.stopPropagation(); confirmOutput(o.id); }} title="Confirmar" className="text-green-600 hover:text-green-800 p-1">
                                <Check className="w-4 h-4" />
                              </button>
                              <button onClick={(e) => { e.stopPropagation(); cancelOutput(o.id); }} title="Cancelar" className="text-red-500 hover:text-red-700 p-1">
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
                                <TableHead className="text-xs font-semibold text-gray-500 text-right">Precio</TableHead>
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
                                  <TableCell className="text-xs text-right">Bs {Number(d.salePrice).toFixed(2)}</TableCell>
                                  <TableCell className="text-xs text-right font-medium">Bs {(Number(d.quantity) * Number(d.salePrice)).toFixed(2)}</TableCell>
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
        )
      )}

      {/* ─── Cash Register Tab ────────────────────────────────────────── */}
      {tab === 'caja' && (
        <div className="space-y-4">
          {/* Active session card */}
          {loadingSession ? (
            <div className="flex items-center justify-center py-12">
              <Loader2 className="w-6 h-6 animate-spin text-violet-500" />
            </div>
          ) : activeSession ? (
            <div className="bg-white border border-green-200 rounded-xl p-5">
              <div className="flex items-center gap-3 mb-4">
                <div className="w-10 h-10 rounded-xl bg-green-50 flex items-center justify-center">
                  <DollarSign className="w-5 h-5 text-green-600" />
                </div>
                <div className="flex-1">
                  <div className="flex items-center gap-2">
                    <h3 className="font-semibold text-gray-900">Caja abierta</h3>
                    <Badge className="bg-green-100 text-green-700 text-[10px]">ACTIVA</Badge>
                  </div>
                  <p className="text-xs text-gray-500 mt-0.5">
                    {activeSession.pointOfSale.branch.name} — {activeSession.pointOfSale.name}
                  </p>
                </div>
                <Button size="sm" variant="outline" className="border-red-200 text-red-600 hover:bg-red-50" onClick={handleCloseCashDialog}>
                  <Lock className="w-3.5 h-3.5 mr-1" /> Cerrar caja
                </Button>
              </div>

              <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
                <div className="bg-gray-50 rounded-lg p-3">
                  <p className="text-[10px] text-gray-400 uppercase tracking-wider">Apertura</p>
                  <p className="text-sm font-bold text-gray-800 mt-1">Bs {Number(activeSession.openingAmount).toFixed(2)}</p>
                </div>
                <div className="bg-violet-50 rounded-lg p-3">
                  <p className="text-[10px] text-violet-400 uppercase tracking-wider">Esperado</p>
                  <p className="text-sm font-bold text-violet-700 mt-1">Bs {Number(activeSession.expectedAmount ?? activeSession.openingAmount).toFixed(2)}</p>
                </div>
                <div className="bg-gray-50 rounded-lg p-3">
                  <p className="text-[10px] text-gray-400 uppercase tracking-wider">Abierta desde</p>
                  <p className="text-sm font-medium text-gray-700 mt-1">{formatDateTime(activeSession.openedAt)}</p>
                </div>
              </div>
            </div>
          ) : (
            <div className="bg-white border border-gray-100 rounded-xl p-6 text-center">
              <DollarSign className="w-10 h-10 text-gray-200 mx-auto mb-3" />
              <h3 className="text-sm font-semibold text-gray-700">No hay caja abierta</h3>
              <p className="text-xs text-gray-400 mt-1 mb-4">Abre una caja para comenzar a registrar ventas</p>
              <Button size="sm" className="bg-violet-600 hover:bg-violet-700 text-white" onClick={handleOpenCashDialog}>
                <DollarSign className="w-4 h-4 mr-1" /> Abrir caja
              </Button>
            </div>
          )}

          {/* Sessions history */}
          <div>
            <h3 className="text-sm font-semibold text-gray-700 mb-3">Historial de cajas</h3>
            {loadingSessions ? (
              <div className="flex items-center justify-center py-8">
                <Loader2 className="w-5 h-5 animate-spin text-violet-500" />
              </div>
            ) : cashSessions.length === 0 ? (
              <div className="text-center py-8 text-gray-400 text-xs">
                <Clock className="w-6 h-6 mx-auto mb-2 text-gray-200" />
                Sin registros de caja
              </div>
            ) : (
              <div className="bg-white rounded-xl border border-gray-100 overflow-hidden">
                <Table>
                  <TableHeader>
                    <TableRow className="bg-gray-50 hover:bg-gray-50">
                      <TableHead className="text-xs font-semibold text-gray-500">Punto de venta</TableHead>
                      <TableHead className="text-xs font-semibold text-gray-500">Apertura</TableHead>
                      <TableHead className="text-xs font-semibold text-gray-500 text-right">Monto apertura</TableHead>
                      <TableHead className="text-xs font-semibold text-gray-500 text-right">Esperado</TableHead>
                      <TableHead className="text-xs font-semibold text-gray-500 text-right">Cierre</TableHead>
                      <TableHead className="text-xs font-semibold text-gray-500 text-right">Diferencia</TableHead>
                      <TableHead className="text-xs font-semibold text-gray-500 text-center">Estado</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {cashSessions.map((s) => {
                      const diff = s.difference != null ? Number(s.difference) : null;
                      return (
                        <TableRow key={s.id}>
                          <TableCell className="text-xs text-gray-700">
                            {s.pointOfSale?.branch?.name} — {s.pointOfSale?.name}
                          </TableCell>
                          <TableCell className="text-xs text-gray-500">{formatDateTime(s.openedAt)}</TableCell>
                          <TableCell className="text-xs text-right">Bs {Number(s.openingAmount).toFixed(2)}</TableCell>
                          <TableCell className="text-xs text-right">{s.expectedAmount != null ? `Bs ${Number(s.expectedAmount).toFixed(2)}` : '-'}</TableCell>
                          <TableCell className="text-xs text-right">{s.closingAmount != null ? `Bs ${Number(s.closingAmount).toFixed(2)}` : '-'}</TableCell>
                          <TableCell className="text-xs text-right">
                            {diff != null ? (
                              <span className={diff >= 0 ? 'text-green-600' : 'text-red-600'}>
                                {diff >= 0 ? '+' : ''}{diff.toFixed(2)}
                              </span>
                            ) : '-'}
                          </TableCell>
                          <TableCell className="text-center">
                            <Badge className={s.status === 'OPEN' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'}>
                              {s.status === 'OPEN' ? 'Abierta' : 'Cerrada'}
                            </Badge>
                          </TableCell>
                        </TableRow>
                      );
                    })}
                  </TableBody>
                </Table>
              </div>
            )}
          </div>
        </div>
      )}

      {/* ─── Open Cash Dialog ─────────────────────────────────────────── */}
      <Dialog open={openCashDialog} onOpenChange={setOpenCashDialog}>
        <DialogContent className="max-w-sm">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <DollarSign className="w-4 h-4 text-violet-600" />
              Abrir caja
            </DialogTitle>
          </DialogHeader>
          <div className="space-y-4 pt-2">
            <div className="space-y-1.5">
              <Label className="text-xs">Punto de venta</Label>
              <select
                value={cashPosId}
                onChange={(e) => setCashPosId(e.target.value)}
                className="w-full h-9 text-sm border border-gray-200 rounded-md px-3 bg-white focus:outline-none focus:ring-1 focus:ring-violet-500"
              >
                <option value="">Seleccionar...</option>
                {allPos.map((p) => (
                  <option key={p.id} value={p.id}>{p.branchName} — {p.name}</option>
                ))}
              </select>
            </div>
            <div className="space-y-1.5">
              <Label className="text-xs">Monto de apertura (Bs)</Label>
              <Input
                type="number"
                value={cashOpeningAmount}
                onChange={(e) => setCashOpeningAmount(e.target.value)}
                placeholder="0.00"
                min="0"
                step="0.01"
              />
            </div>
            <div className="space-y-1.5">
              <Label className="text-xs">Notas (opcional)</Label>
              <Input
                value={cashNotes}
                onChange={(e) => setCashNotes(e.target.value)}
                placeholder="Turno mañana, etc."
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setOpenCashDialog(false)}>Cancelar</Button>
            <Button
              className="bg-violet-600 hover:bg-violet-700 text-white"
              onClick={handleOpenCash}
              disabled={!cashPosId || !cashOpeningAmount || openingCash}
            >
              {openingCash ? <Loader2 className="w-4 h-4 animate-spin" /> : 'Abrir caja'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* ─── Close Cash Dialog ────────────────────────────────────────── */}
      <Dialog open={closeCashDialog} onOpenChange={setCloseCashDialog}>
        <DialogContent className="max-w-sm">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <Lock className="w-4 h-4 text-red-500" />
              Cerrar caja
            </DialogTitle>
          </DialogHeader>
          <div className="space-y-4 pt-2">
            {activeSession && (
              <div className="bg-gray-50 rounded-lg p-3 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-500">Monto esperado:</span>
                  <span className="font-bold text-violet-700">Bs {Number(activeSession.expectedAmount ?? activeSession.openingAmount).toFixed(2)}</span>
                </div>
              </div>
            )}
            <div className="space-y-1.5">
              <Label className="text-xs">Monto de cierre (Bs) — lo que contaste en caja</Label>
              <Input
                type="number"
                value={cashClosingAmount}
                onChange={(e) => setCashClosingAmount(e.target.value)}
                placeholder="0.00"
                min="0"
                step="0.01"
              />
            </div>
            {cashClosingAmount && activeSession && (
              <div className="text-sm">
                {(() => {
                  const diff = Number(cashClosingAmount) - Number(activeSession.expectedAmount ?? activeSession.openingAmount);
                  if (diff === 0) return <p className="text-green-600 flex items-center gap-1"><Check className="w-3.5 h-3.5" /> Cuadra perfecto</p>;
                  if (diff > 0) return <p className="text-blue-600 flex items-center gap-1">Sobrante: +Bs {diff.toFixed(2)}</p>;
                  return <p className="text-red-600 flex items-center gap-1"><AlertTriangle className="w-3.5 h-3.5" /> Faltante: Bs {diff.toFixed(2)}</p>;
                })()}
              </div>
            )}
            <div className="space-y-1.5">
              <Label className="text-xs">Notas (opcional)</Label>
              <Input
                value={closeNotes}
                onChange={(e) => setCloseNotes(e.target.value)}
                placeholder="Observaciones del cierre"
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setCloseCashDialog(false)}>Cancelar</Button>
            <Button
              className="bg-red-600 hover:bg-red-700 text-white"
              onClick={handleCloseCash}
              disabled={!cashClosingAmount || closingCash}
            >
              {closingCash ? <Loader2 className="w-4 h-4 animate-spin" /> : 'Cerrar caja'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* ─── PDF Preview Dialog ──────────────────────────────────────── */}
      <Dialog open={pdfPreviewOpen} onOpenChange={(open) => {
        if (!open && pdfPreviewUrl) URL.revokeObjectURL(pdfPreviewUrl);
        setPdfPreviewOpen(open);
      }}>
        <DialogContent className="max-w-3xl h-[85vh] flex flex-col">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <Printer className="w-4 h-4 text-violet-600" />
              Vista previa — Nota de venta
            </DialogTitle>
          </DialogHeader>

          <div className="flex items-center gap-3 pb-2">
            <Label className="text-xs text-gray-500 whitespace-nowrap">Tamaño de papel:</Label>
            <select
              value={pdfPaperSize}
              onChange={(e) => handlePaperSizeChange(e.target.value as PaperSize)}
              className="rounded-md border border-gray-200 bg-white px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-violet-500"
            >
              <option value="LETTER">Carta (215.9 × 279.4 mm)</option>
              <option value="ROLL_80">Rollo POS 80mm</option>
              <option value="ROLL_58">Rollo POS 58mm</option>
            </select>
          </div>

          <div className="flex-1 min-h-0 bg-gray-100 rounded-lg overflow-hidden">
            {pdfPreviewUrl && (
              <iframe
                src={pdfPreviewUrl}
                className="w-full h-full border-0"
                title="Vista previa PDF"
              />
            )}
          </div>

          <DialogFooter className="pt-2">
            <Button variant="outline" onClick={() => setPdfPreviewOpen(false)}>Cerrar</Button>
            <Button
              className="bg-violet-600 hover:bg-violet-700 text-white"
              onClick={handlePrintPdf}
            >
              <Printer className="w-4 h-4 mr-1" /> Imprimir
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* ─── Cart Drawer ──────────────────────────────────────────────── */}
      <SalesCartDrawer
        customers={customers}
        saving={saving}
        onCheckout={handleCheckout}
      />
    </div>
  );
}
