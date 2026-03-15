import { useEffect, useState } from 'react';
import {
  Loader2, Plus, RefreshCw, Building2, Monitor, CheckCircle2,
  AlertCircle, Settings, Link2, Shield, FileText, XCircle,
  Eye, Upload, Wifi, WifiOff,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter,
} from '@/components/ui/dialog';
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from '@/components/ui/table';
import { luminiApi } from '@/services/luminiApi';

// ─── Types ────────────────────────────────────────────────────────────────

interface SiatConfig {
  id: string;
  environment: 'TEST' | 'PRODUCTION';
  nit: string;
  socialReason: string;
  mainActivityCode: number;
  isOnline: boolean;
  active: boolean;
}

interface SignatureInfo {
  id: string;
  subject: string;
  startDate: string;
  endDate: string;
  active: boolean;
  expiringSoon: boolean;
  createdAt: string;
}

interface BranchOffice {
  id: string;
  code: number;
  name: string;
  address?: string;
  city?: string;
  phone?: string;
  active: boolean;
  branchId?: string;
  branchOfficeSiatId: number;
}

interface PointSale {
  id: string;
  code: number;
  name: string;
  description?: string;
  type: string;
  branchOfficeId: string;
  branchOffice?: BranchOffice;
  pointOfSaleId?: string;
  pointSaleSiatId: number;
  active: boolean;
  cuisCode?: string;
  cuisExpiration?: string;
  cufdCode?: string;
  cufdExpiration?: string;
}

interface BusinessBranch {
  id: string;
  name: string;
  region: string;
  pointsOfSale: { id: string; name: string }[];
}

interface Invoice {
  id: string;
  invoiceNumber: number;
  cuf: string;
  broadcastDate: string;
  status: string;
  issuerNit: string;
  issuerName: string;
  buyerNit: string;
  buyerName: string;
  totalAmount: number;
  receptionCode?: string;
  createdAt: string;
}

type Tab = 'config' | 'branches' | 'pos' | 'sync' | 'invoices';

const STATUS_LABELS: Record<string, { label: string; color: string }> = {
  PENDING: { label: 'Pendiente', color: 'bg-yellow-100 text-yellow-700' },
  ACCEPTED: { label: 'Aceptada', color: 'bg-green-100 text-green-700' },
  REJECTED: { label: 'Rechazada', color: 'bg-red-100 text-red-600' },
  CANCELLED: { label: 'Anulada', color: 'bg-gray-100 text-gray-500' },
};

// ─── Component ──────────────────────────────────────────────────────────

export function SiatSection() {
  const [tab, setTab] = useState<Tab>('config');

  // Config
  const [config, setConfig] = useState<SiatConfig | null>(null);
  const [configLoading, setConfigLoading] = useState(true);
  const [configForm, setConfigForm] = useState({ environment: 'TEST', nit: '', socialReason: '', mainActivityCode: '' });
  const [configSaving, setConfigSaving] = useState(false);

  // Signature
  const [signature, setSignature] = useState<SignatureInfo | null>(null);
  const [sigDialog, setSigDialog] = useState(false);
  const [sigForm, setSigForm] = useState({ certificate: '', privateKey: '' });
  const [sigSaving, setSigSaving] = useState(false);

  // SIAT Branches & POS
  const [siatBranches, setSiatBranches] = useState<BranchOffice[]>([]);
  const [siatPointSales, setSiatPointSales] = useState<PointSale[]>([]);
  const [loadingBranches, setLoadingBranches] = useState(true);
  const [loadingPos, setLoadingPos] = useState(true);

  // Business branches (for linking)
  const [bizBranches, setBizBranches] = useState<BusinessBranch[]>([]);

  // Dialogs
  const [branchDialog, setBranchDialog] = useState(false);
  const [posDialog, setPosDialog] = useState(false);
  const [saving, setSaving] = useState(false);

  // branch form
  const [branchForm, setBranchForm] = useState({ code: '', name: '', address: '', city: '', phone: '', branchId: '' });
  // pos form
  const [posForm, setPosForm] = useState({ code: '', name: '', description: '', type: 'PHYSICAL', branchOfficeId: '', pointOfSaleId: '' });

  // sync
  const [syncing, setSyncing] = useState<Record<string, boolean>>({});
  const [syncResults, setSyncResults] = useState<Record<string, { ok: boolean; message: string }>>({});

  // invoices
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [invoiceTotal, setInvoiceTotal] = useState(0);
  const [invoiceLoading, setInvoiceLoading] = useState(true);

  // cancel dialog
  const [cancelDialog, setCancelDialog] = useState(false);
  const [cancelInvoiceId, setCancelInvoiceId] = useState('');
  const [cancelReason, setCancelReason] = useState('');
  const [cancelling, setCancelling] = useState(false);

  const businessId = localStorage.getItem('luminia_business_id') ?? '';

  useEffect(() => {
    loadConfig();
    loadSignature();
    loadSiatBranches();
    loadSiatPointSales();
    loadBizBranches();
    loadInvoices();
  }, []);

  // ─── Loaders ──────────────────────────────────────────────────────

  async function loadConfig() {
    setConfigLoading(true);
    try {
      const { data } = await luminiApi.get('/billing/config');
      setConfig(data ?? null);
      if (data) {
        setConfigForm({
          environment: data.environment,
          nit: data.nit,
          socialReason: data.socialReason,
          mainActivityCode: String(data.mainActivityCode),
        });
      }
    } catch { setConfig(null); }
    finally { setConfigLoading(false); }
  }

  async function loadSignature() {
    try {
      const { data } = await luminiApi.get('/billing/signatures/active');
      setSignature(data ?? null);
    } catch { setSignature(null); }
  }

  async function loadSiatBranches() {
    setLoadingBranches(true);
    try {
      const { data } = await luminiApi.get('/billing/branch-offices');
      setSiatBranches(Array.isArray(data) ? data : []);
    } catch { setSiatBranches([]); }
    finally { setLoadingBranches(false); }
  }

  async function loadSiatPointSales() {
    setLoadingPos(true);
    try {
      const { data } = await luminiApi.get('/billing/point-sales');
      setSiatPointSales(Array.isArray(data) ? data : []);
    } catch { setSiatPointSales([]); }
    finally { setLoadingPos(false); }
  }

  async function loadBizBranches() {
    try {
      const { data } = await luminiApi.get(`/business/${businessId}/branches`);
      setBizBranches(Array.isArray(data) ? data : []);
    } catch { setBizBranches([]); }
  }

  async function loadInvoices() {
    setInvoiceLoading(true);
    try {
      const { data } = await luminiApi.get('/billing/invoices');
      setInvoices(data?.items ?? []);
      setInvoiceTotal(data?.total ?? 0);
    } catch { setInvoices([]); }
    finally { setInvoiceLoading(false); }
  }

  // ─── Config Actions ───────────────────────────────────────────────

  async function handleSaveConfig() {
    setConfigSaving(true);
    try {
      await luminiApi.put('/billing/config', {
        environment: configForm.environment,
        nit: configForm.nit,
        socialReason: configForm.socialReason,
        mainActivityCode: Number(configForm.mainActivityCode),
      });
      await loadConfig();
    } catch (err: any) {
      alert(err?.response?.data?.message ?? 'Error al guardar configuración');
    } finally { setConfigSaving(false); }
  }

  async function handleToggleOnline() {
    if (!config) return;
    try {
      await luminiApi.put('/billing/config', {
        environment: config.environment,
        nit: config.nit,
        socialReason: config.socialReason,
        mainActivityCode: config.mainActivityCode,
        isOnline: !config.isOnline,
      });
      await loadConfig();
    } catch (err: any) {
      alert(err?.response?.data?.message ?? 'Error');
    }
  }

  // ─── Signature Actions ────────────────────────────────────────────

  async function handleUploadSignature() {
    setSigSaving(true);
    try {
      await luminiApi.post('/billing/signatures', {
        certificate: sigForm.certificate,
        privateKey: sigForm.privateKey,
      });
      setSigDialog(false);
      setSigForm({ certificate: '', privateKey: '' });
      await loadSignature();
    } catch (err: any) {
      alert(err?.response?.data?.message ?? 'Error al subir certificado');
    } finally { setSigSaving(false); }
  }

  // ─── Branch CRUD ──────────────────────────────────────────────────

  function openBranchDialog() {
    setBranchForm({ code: '', name: '', address: '', city: '', phone: '', branchId: '' });
    setBranchDialog(true);
  }

  async function handleCreateBranch() {
    setSaving(true);
    try {
      await luminiApi.post('/billing/branch-offices', {
        branchOfficeSiatId: Number(branchForm.code),
        name: branchForm.name,
        description: branchForm.name,
        address: branchForm.address || undefined,
        city: branchForm.city || undefined,
        phone: branchForm.phone || undefined,
        branchId: branchForm.branchId || undefined,
      });
      setBranchDialog(false);
      loadSiatBranches();
    } catch (err: any) {
      alert(err?.response?.data?.message ?? 'Error al registrar sucursal SIAT');
    } finally { setSaving(false); }
  }

  // ─── POS CRUD ─────────────────────────────────────────────────────

  function openPosDialog() {
    setPosForm({
      code: '', name: '', description: '', type: 'PHYSICAL',
      branchOfficeId: siatBranches[0]?.id ?? '', pointOfSaleId: '',
    });
    setPosDialog(true);
  }

  async function handleCreatePos() {
    setSaving(true);
    try {
      await luminiApi.post('/billing/point-sales', {
        pointSaleSiatId: Number(posForm.code),
        name: posForm.name,
        description: posForm.description || posForm.name,
        type: posForm.type,
        branchOfficeId: posForm.branchOfficeId,
        pointOfSaleId: posForm.pointOfSaleId || undefined,
      });
      setPosDialog(false);
      loadSiatPointSales();
    } catch (err: any) {
      alert(err?.response?.data?.message ?? 'Error al registrar punto de venta SIAT');
    } finally { setSaving(false); }
  }

  // ─── Sync ─────────────────────────────────────────────────────────

  async function handleSync(action: 'codes' | 'all-codes' | 'parameters', pointSaleId?: string) {
    const key = action + (pointSaleId ?? '');
    setSyncing((p) => ({ ...p, [key]: true }));
    setSyncResults((p) => ({ ...p, [key]: undefined as any }));
    try {
      const body: any = {};
      if (action === 'codes' && pointSaleId) body.pointSaleId = pointSaleId;
      await luminiApi.post(`/billing/sync/${action}`, body);
      setSyncResults((p) => ({ ...p, [key]: { ok: true, message: 'Sincronizado correctamente' } }));
      if (action === 'codes' || action === 'all-codes') loadSiatPointSales();
    } catch (err: any) {
      setSyncResults((p) => ({ ...p, [key]: { ok: false, message: err?.response?.data?.message ?? 'Error de sincronización' } }));
    } finally {
      setSyncing((p) => ({ ...p, [key]: false }));
    }
  }

  // ─── Invoice Actions ──────────────────────────────────────────────

  function openCancelDialog(invoiceId: string) {
    setCancelInvoiceId(invoiceId);
    setCancelReason('');
    setCancelDialog(true);
  }

  async function handleCancelInvoice() {
    setCancelling(true);
    try {
      await luminiApi.post(`/billing/invoices/${cancelInvoiceId}/cancel`, {
        cancellationReasonId: Number(cancelReason) || 1,
      });
      setCancelDialog(false);
      await loadInvoices();
    } catch (err: any) {
      alert(err?.response?.data?.message ?? 'Error al anular factura');
    } finally { setCancelling(false); }
  }

  // ─── Helpers ──────────────────────────────────────────────────────

  function linkedBranchName(branchId?: string) {
    if (!branchId) return null;
    return bizBranches.find((b) => b.id === branchId)?.name ?? null;
  }

  const allBizPos = bizBranches.flatMap((b) => b.pointsOfSale.map((p) => ({ ...p, branchName: b.name })));

  function linkedPosName(pointOfSaleId?: string) {
    if (!pointOfSaleId) return null;
    const p = allBizPos.find((pp) => pp.id === pointOfSaleId);
    return p ? `${p.branchName} — ${p.name}` : null;
  }

  // ─── Render ───────────────────────────────────────────────────────

  const tabs: { id: Tab; label: string }[] = [
    { id: 'config', label: 'Configuración' },
    { id: 'branches', label: 'Sucursales SIAT' },
    { id: 'pos', label: 'Puntos de venta' },
    { id: 'sync', label: 'Sincronización' },
    { id: 'invoices', label: 'Facturas' },
  ];

  return (
    <div className="space-y-4">
      {/* Tabs */}
      <div className="flex items-center gap-3 flex-wrap">
        <div className="flex bg-gray-100 rounded-lg p-0.5">
          {tabs.map((t) => (
            <button
              key={t.id}
              onClick={() => setTab(t.id)}
              className={`px-3 py-1.5 text-xs font-medium rounded-md transition ${
                tab === t.id ? 'bg-white text-violet-700 shadow-sm' : 'text-gray-500 hover:text-gray-700'
              }`}
            >
              {t.label}
            </button>
          ))}
        </div>

        <div className="ml-auto flex gap-2">
          {tab === 'branches' && (
            <Button size="sm" className="bg-violet-600 hover:bg-violet-700 text-white" onClick={openBranchDialog}>
              <Plus className="w-3.5 h-3.5 mr-1" /> Registrar sucursal
            </Button>
          )}
          {tab === 'pos' && (
            <Button size="sm" className="bg-violet-600 hover:bg-violet-700 text-white" onClick={openPosDialog}>
              <Plus className="w-3.5 h-3.5 mr-1" /> Registrar punto de venta
            </Button>
          )}
        </div>
      </div>

      {/* ─── CONFIG TAB ────────────────────────────────────────────────── */}
      {tab === 'config' && (
        configLoading ? (
          <div className="flex items-center justify-center py-20"><Loader2 className="w-6 h-6 animate-spin text-violet-500" /></div>
        ) : (
          <div className="grid gap-4 lg:grid-cols-2">
            {/* SIAT Config Card */}
            <Card className="border-gray-100">
              <CardHeader className="pb-2">
                <CardTitle className="text-sm font-medium flex items-center gap-2">
                  <Settings className="w-4 h-4 text-violet-600" />
                  Credenciales SIAT
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <Label className="text-xs">Ambiente</Label>
                    <select
                      value={configForm.environment}
                      onChange={(e) => setConfigForm((p) => ({ ...p, environment: e.target.value }))}
                      className="w-full h-8 text-xs border border-gray-200 rounded-md px-2 bg-white focus:outline-none focus:ring-1 focus:ring-violet-500"
                    >
                      <option value="TEST">Pruebas (Piloto)</option>
                      <option value="PRODUCTION">Producción</option>
                    </select>
                  </div>
                  <div>
                    <Label className="text-xs">NIT *</Label>
                    <Input
                      value={configForm.nit}
                      onChange={(e) => setConfigForm((p) => ({ ...p, nit: e.target.value }))}
                      placeholder="1234567890"
                      className="h-8 text-xs"
                    />
                  </div>
                </div>
                <div>
                  <Label className="text-xs">Razón Social *</Label>
                  <Input
                    value={configForm.socialReason}
                    onChange={(e) => setConfigForm((p) => ({ ...p, socialReason: e.target.value }))}
                    placeholder="EMPRESA S.R.L."
                    className="h-8 text-xs"
                  />
                </div>
                <div>
                  <Label className="text-xs">Código Actividad Principal *</Label>
                  <Input
                    type="number"
                    value={configForm.mainActivityCode}
                    onChange={(e) => setConfigForm((p) => ({ ...p, mainActivityCode: e.target.value }))}
                    placeholder="474100"
                    className="h-8 text-xs"
                  />
                </div>
                <Button
                  size="sm"
                  className="w-full bg-violet-600 hover:bg-violet-700 text-white"
                  disabled={!configForm.nit || !configForm.socialReason || !configForm.mainActivityCode || configSaving}
                  onClick={handleSaveConfig}
                >
                  {configSaving && <Loader2 className="w-3 h-3 animate-spin mr-1" />}
                  {config ? 'Actualizar configuración' : 'Guardar configuración'}
                </Button>

                {config && (
                  <div className="pt-2 border-t border-gray-100">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        {config.isOnline
                          ? <Wifi className="w-4 h-4 text-green-600" />
                          : <WifiOff className="w-4 h-4 text-orange-500" />
                        }
                        <span className="text-xs text-gray-600">
                          Modo: {config.isOnline ? 'En línea' : 'Contingencia (offline)'}
                        </span>
                      </div>
                      <Button size="sm" variant="outline" className="text-xs h-7" onClick={handleToggleOnline}>
                        Cambiar a {config.isOnline ? 'offline' : 'online'}
                      </Button>
                    </div>
                  </div>
                )}
              </CardContent>
            </Card>

            {/* Signature Card */}
            <Card className="border-gray-100">
              <CardHeader className="pb-2">
                <CardTitle className="text-sm font-medium flex items-center gap-2">
                  <Shield className="w-4 h-4 text-violet-600" />
                  Certificado Digital
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                {signature ? (
                  <>
                    <div className="bg-gray-50 rounded-lg p-3 space-y-1.5">
                      <div className="flex items-center justify-between">
                        <span className="text-xs font-medium text-gray-700">{signature.subject || 'Certificado'}</span>
                        {signature.expiringSoon ? (
                          <Badge className="bg-orange-100 text-orange-700 text-[10px]">Expira pronto</Badge>
                        ) : (
                          <Badge className="bg-green-100 text-green-700 text-[10px]">Vigente</Badge>
                        )}
                      </div>
                      <p className="text-[11px] text-gray-400">
                        Válido: {new Date(signature.startDate).toLocaleDateString('es-BO')} — {new Date(signature.endDate).toLocaleDateString('es-BO')}
                      </p>
                    </div>
                    <Button size="sm" variant="outline" className="w-full text-xs" onClick={() => setSigDialog(true)}>
                      <Upload className="w-3 h-3 mr-1" /> Reemplazar certificado
                    </Button>
                  </>
                ) : (
                  <>
                    <div className="text-center py-6 text-gray-400">
                      <Shield className="w-8 h-8 mx-auto mb-2 text-gray-200" />
                      <p className="text-xs">Sin certificado digital</p>
                      <p className="text-[10px] mt-1">Sube tu certificado PEM para firmar facturas</p>
                    </div>
                    <Button size="sm" className="w-full bg-violet-600 hover:bg-violet-700 text-white text-xs" onClick={() => setSigDialog(true)}>
                      <Upload className="w-3 h-3 mr-1" /> Subir certificado
                    </Button>
                  </>
                )}
              </CardContent>
            </Card>
          </div>
        )
      )}

      {/* ─── BRANCHES TAB ──────────────────────────────────────────────── */}
      {tab === 'branches' && (
        loadingBranches ? (
          <div className="flex items-center justify-center py-20"><Loader2 className="w-6 h-6 animate-spin text-violet-500" /></div>
        ) : siatBranches.length === 0 ? (
          <div className="text-center py-16 text-gray-400 text-sm">
            <Building2 className="w-8 h-8 mx-auto mb-2 text-gray-200" />
            No hay sucursales SIAT registradas
          </div>
        ) : (
          <div className="bg-white rounded-xl border border-gray-100 overflow-hidden">
            <Table>
              <TableHeader>
                <TableRow className="bg-gray-50 hover:bg-gray-50">
                  <TableHead className="text-xs font-semibold text-gray-500">Código SIN</TableHead>
                  <TableHead className="text-xs font-semibold text-gray-500">Nombre</TableHead>
                  <TableHead className="text-xs font-semibold text-gray-500">Sucursal vinculada</TableHead>
                  <TableHead className="text-xs font-semibold text-gray-500">Ciudad</TableHead>
                  <TableHead className="text-xs font-semibold text-gray-500 text-center">Estado</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {siatBranches.map((b) => {
                  const linked = linkedBranchName(b.branchId);
                  return (
                    <TableRow key={b.id}>
                      <TableCell className="text-xs font-mono text-gray-600">{b.branchOfficeSiatId}</TableCell>
                      <TableCell className="text-xs font-medium text-gray-800">{b.name}</TableCell>
                      <TableCell>
                        {linked ? (
                          <span className="text-xs text-violet-600 flex items-center gap-1">
                            <Link2 className="w-3 h-3" /> {linked}
                          </span>
                        ) : (
                          <span className="text-xs text-gray-300">Sin vincular</span>
                        )}
                      </TableCell>
                      <TableCell className="text-xs text-gray-500">{b.city ?? '-'}</TableCell>
                      <TableCell className="text-center">
                        <Badge className={b.active ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'}>
                          {b.active ? 'Activa' : 'Inactiva'}
                        </Badge>
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          </div>
        )
      )}

      {/* ─── POS TAB ───────────────────────────────────────────────────── */}
      {tab === 'pos' && (
        loadingPos ? (
          <div className="flex items-center justify-center py-20"><Loader2 className="w-6 h-6 animate-spin text-violet-500" /></div>
        ) : siatPointSales.length === 0 ? (
          <div className="text-center py-16 text-gray-400 text-sm">
            <Monitor className="w-8 h-8 mx-auto mb-2 text-gray-200" />
            No hay puntos de venta SIAT registrados
          </div>
        ) : (
          <div className="bg-white rounded-xl border border-gray-100 overflow-hidden">
            <Table>
              <TableHeader>
                <TableRow className="bg-gray-50 hover:bg-gray-50">
                  <TableHead className="text-xs font-semibold text-gray-500">Código SIN</TableHead>
                  <TableHead className="text-xs font-semibold text-gray-500">Nombre</TableHead>
                  <TableHead className="text-xs font-semibold text-gray-500">Tipo</TableHead>
                  <TableHead className="text-xs font-semibold text-gray-500">Sucursal SIAT</TableHead>
                  <TableHead className="text-xs font-semibold text-gray-500">POS vinculado</TableHead>
                  <TableHead className="text-xs font-semibold text-gray-500">CUIS</TableHead>
                  <TableHead className="text-xs font-semibold text-gray-500">CUFD</TableHead>
                  <TableHead className="text-xs font-semibold text-gray-500 text-center">Estado</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {siatPointSales.map((ps) => {
                  const linked = linkedPosName(ps.pointOfSaleId);
                  return (
                    <TableRow key={ps.id}>
                      <TableCell className="text-xs font-mono text-gray-600">{ps.pointSaleSiatId}</TableCell>
                      <TableCell className="text-xs font-medium text-gray-800">{ps.name}</TableCell>
                      <TableCell className="text-xs text-gray-500">{ps.type}</TableCell>
                      <TableCell className="text-xs text-gray-500">{ps.branchOffice?.name ?? '-'}</TableCell>
                      <TableCell>
                        {linked ? (
                          <span className="text-xs text-violet-600 flex items-center gap-1">
                            <Link2 className="w-3 h-3" /> {linked}
                          </span>
                        ) : (
                          <span className="text-xs text-gray-300">Sin vincular</span>
                        )}
                      </TableCell>
                      <TableCell>
                        {ps.cuisCode ? (
                          <div>
                            <span className="text-xs text-green-600 font-mono">{ps.cuisCode.slice(0, 12)}...</span>
                            {ps.cuisExpiration && (
                              <p className="text-[10px] text-gray-400">Exp: {new Date(ps.cuisExpiration).toLocaleDateString('es-BO')}</p>
                            )}
                          </div>
                        ) : (
                          <span className="text-xs text-gray-300">Sin CUIS</span>
                        )}
                      </TableCell>
                      <TableCell>
                        {ps.cufdCode ? (
                          <div>
                            <span className="text-xs text-green-600 font-mono">{ps.cufdCode.slice(0, 12)}...</span>
                            {ps.cufdExpiration && (
                              <p className="text-[10px] text-gray-400">Exp: {new Date(ps.cufdExpiration).toLocaleDateString('es-BO')}</p>
                            )}
                          </div>
                        ) : (
                          <span className="text-xs text-gray-300">Sin CUFD</span>
                        )}
                      </TableCell>
                      <TableCell className="text-center">
                        <Badge className={ps.active ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'}>
                          {ps.active ? 'Activo' : 'Inactivo'}
                        </Badge>
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          </div>
        )
      )}

      {/* ─── SYNC TAB ──────────────────────────────────────────────────── */}
      {tab === 'sync' && (
        !config ? (
          <div className="bg-yellow-50 border border-yellow-200 rounded-lg px-4 py-6 text-center">
            <Settings className="w-8 h-8 mx-auto mb-2 text-yellow-400" />
            <p className="text-sm font-medium text-yellow-700">Configura tus credenciales SIAT primero</p>
            <p className="text-xs text-yellow-600 mt-1">Ve a la pestaña "Configuración" para ingresar tu NIT y datos del SIAT</p>
            <Button size="sm" variant="outline" className="mt-3" onClick={() => setTab('config')}>
              Ir a Configuración
            </Button>
          </div>
        ) : (
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            <SyncCard
              title="Sincronizar CUIS/CUFD"
              description="Solicita códigos CUIS y CUFD para todos los puntos de venta"
              icon={<RefreshCw className="w-5 h-5 text-violet-600" />}
              loading={syncing['all-codes'] ?? false}
              result={syncResults['all-codes']}
              onSync={() => handleSync('all-codes')}
            />
            <SyncCard
              title="Sincronizar parámetros"
              description="Actividades económicas, tipos de documento, unidades de medida, etc."
              icon={<Settings className="w-5 h-5 text-violet-600" />}
              loading={syncing['parameters'] ?? false}
              result={syncResults['parameters']}
              onSync={() => handleSync('parameters')}
            />
            {siatPointSales.map((ps) => {
              const key = 'codes' + ps.id;
              return (
                <SyncCard
                  key={ps.id}
                  title={`CUIS/CUFD - ${ps.name}`}
                  description={`Sincronizar códigos para "${ps.name}" (${ps.branchOffice?.name ?? 'Sin sucursal'})`}
                  icon={<Monitor className="w-5 h-5 text-blue-600" />}
                  loading={syncing[key] ?? false}
                  result={syncResults[key]}
                  onSync={() => handleSync('codes', ps.id)}
                />
              );
            })}
          </div>
        )
      )}

      {/* ─── INVOICES TAB ──────────────────────────────────────────────── */}
      {tab === 'invoices' && (
        invoiceLoading ? (
          <div className="flex items-center justify-center py-20"><Loader2 className="w-6 h-6 animate-spin text-violet-500" /></div>
        ) : invoices.length === 0 ? (
          <div className="text-center py-16 text-gray-400 text-sm">
            <FileText className="w-8 h-8 mx-auto mb-2 text-gray-200" />
            No hay facturas emitidas
          </div>
        ) : (
          <div className="space-y-3">
            <p className="text-xs text-gray-400">{invoiceTotal} factura{invoiceTotal !== 1 ? 's' : ''}</p>
            <div className="bg-white rounded-xl border border-gray-100 overflow-hidden">
              <Table>
                <TableHeader>
                  <TableRow className="bg-gray-50 hover:bg-gray-50">
                    <TableHead className="text-xs font-semibold text-gray-500">#</TableHead>
                    <TableHead className="text-xs font-semibold text-gray-500">Fecha</TableHead>
                    <TableHead className="text-xs font-semibold text-gray-500">Comprador</TableHead>
                    <TableHead className="text-xs font-semibold text-gray-500">NIT</TableHead>
                    <TableHead className="text-xs font-semibold text-gray-500 text-right">Total</TableHead>
                    <TableHead className="text-xs font-semibold text-gray-500 text-center">Estado</TableHead>
                    <TableHead className="text-xs font-semibold text-gray-500 text-center">Acciones</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {invoices.map((inv) => {
                    const st = STATUS_LABELS[inv.status] ?? { label: inv.status, color: 'bg-gray-100 text-gray-500' };
                    return (
                      <TableRow key={inv.id}>
                        <TableCell className="text-xs font-mono text-gray-600">{inv.invoiceNumber}</TableCell>
                        <TableCell className="text-xs text-gray-500">
                          {new Date(inv.broadcastDate).toLocaleDateString('es-BO')}
                        </TableCell>
                        <TableCell className="text-xs font-medium text-gray-800">{inv.buyerName}</TableCell>
                        <TableCell className="text-xs text-gray-500">{inv.buyerNit}</TableCell>
                        <TableCell className="text-xs text-right font-medium text-gray-800">
                          Bs {Number(inv.totalAmount).toFixed(2)}
                        </TableCell>
                        <TableCell className="text-center">
                          <Badge className={st.color}>{st.label}</Badge>
                        </TableCell>
                        <TableCell className="text-center">
                          <div className="flex items-center justify-center gap-1">
                            {(inv.status === 'ACCEPTED' || inv.status === 'PENDING') && (
                              <button
                                onClick={() => openCancelDialog(inv.id)}
                                className="p-1 text-gray-400 hover:text-red-500 rounded transition"
                                title="Anular"
                              >
                                <XCircle className="w-3.5 h-3.5" />
                              </button>
                            )}
                          </div>
                        </TableCell>
                      </TableRow>
                    );
                  })}
                </TableBody>
              </Table>
            </div>
          </div>
        )
      )}

      {/* ─── Branch Dialog ────────────────────────────────────────────── */}
      <Dialog open={branchDialog} onOpenChange={setBranchDialog}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle className="text-sm font-semibold flex items-center gap-2">
              <Building2 className="w-4 h-4 text-violet-600" />
              Registrar sucursal en SIAT
            </DialogTitle>
          </DialogHeader>
          <div className="space-y-3">
            <div className="space-y-1.5">
              <Label className="text-xs">Vincular a sucursal existente</Label>
              <select
                value={branchForm.branchId}
                onChange={(e) => {
                  const bid = e.target.value;
                  setBranchForm((p) => ({ ...p, branchId: bid }));
                  if (bid) {
                    const b = bizBranches.find((bb) => bb.id === bid);
                    if (b) setBranchForm((p) => ({ ...p, name: p.name || b.name }));
                  }
                }}
                className="w-full h-8 text-xs border border-gray-200 rounded-md px-2 bg-white focus:outline-none focus:ring-1 focus:ring-violet-500"
              >
                <option value="">Sin vincular</option>
                {bizBranches.map((b) => (
                  <option key={b.id} value={b.id}>{b.name} ({b.region})</option>
                ))}
              </select>
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <Label className="text-xs mb-1">Código SIN *</Label>
                <Input type="number" value={branchForm.code} onChange={(e) => setBranchForm((p) => ({ ...p, code: e.target.value }))} placeholder="0" className="h-8 text-xs" />
              </div>
              <div>
                <Label className="text-xs mb-1">Nombre *</Label>
                <Input value={branchForm.name} onChange={(e) => setBranchForm((p) => ({ ...p, name: e.target.value }))} placeholder="Sucursal principal" className="h-8 text-xs" />
              </div>
            </div>
            <div>
              <Label className="text-xs mb-1">Dirección</Label>
              <Input value={branchForm.address} onChange={(e) => setBranchForm((p) => ({ ...p, address: e.target.value }))} placeholder="Av. Ejemplo 123" className="h-8 text-xs" />
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <Label className="text-xs mb-1">Ciudad</Label>
                <Input value={branchForm.city} onChange={(e) => setBranchForm((p) => ({ ...p, city: e.target.value }))} placeholder="Santa Cruz" className="h-8 text-xs" />
              </div>
              <div>
                <Label className="text-xs mb-1">Teléfono</Label>
                <Input value={branchForm.phone} onChange={(e) => setBranchForm((p) => ({ ...p, phone: e.target.value }))} placeholder="3-XXXXXXX" className="h-8 text-xs" />
              </div>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" size="sm" onClick={() => setBranchDialog(false)}>Cancelar</Button>
            <Button size="sm" className="bg-violet-600 hover:bg-violet-700 text-white" disabled={!branchForm.code || !branchForm.name || saving} onClick={handleCreateBranch}>
              {saving && <Loader2 className="w-3 h-3 animate-spin mr-1" />}
              Registrar
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* ─── POS Dialog ───────────────────────────────────────────────── */}
      <Dialog open={posDialog} onOpenChange={setPosDialog}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle className="text-sm font-semibold flex items-center gap-2">
              <Monitor className="w-4 h-4 text-violet-600" />
              Registrar punto de venta en SIAT
            </DialogTitle>
          </DialogHeader>
          <div className="space-y-3">
            <div className="space-y-1.5">
              <Label className="text-xs">Vincular a punto de venta existente</Label>
              <select
                value={posForm.pointOfSaleId}
                onChange={(e) => {
                  const pid = e.target.value;
                  setPosForm((p) => ({ ...p, pointOfSaleId: pid }));
                  if (pid) {
                    const pos = allBizPos.find((pp) => pp.id === pid);
                    if (pos) setPosForm((p) => ({ ...p, name: p.name || pos.name }));
                  }
                }}
                className="w-full h-8 text-xs border border-gray-200 rounded-md px-2 bg-white focus:outline-none focus:ring-1 focus:ring-violet-500"
              >
                <option value="">Sin vincular</option>
                {allBizPos.map((p) => (
                  <option key={p.id} value={p.id}>{p.branchName} — {p.name}</option>
                ))}
              </select>
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <Label className="text-xs mb-1">Código SIN *</Label>
                <Input type="number" value={posForm.code} onChange={(e) => setPosForm((p) => ({ ...p, code: e.target.value }))} placeholder="0" className="h-8 text-xs" />
              </div>
              <div>
                <Label className="text-xs mb-1">Nombre *</Label>
                <Input value={posForm.name} onChange={(e) => setPosForm((p) => ({ ...p, name: e.target.value }))} placeholder="Caja 1" className="h-8 text-xs" />
              </div>
            </div>
            <div>
              <Label className="text-xs mb-1">Descripción</Label>
              <Input value={posForm.description} onChange={(e) => setPosForm((p) => ({ ...p, description: e.target.value }))} placeholder="Caja principal" className="h-8 text-xs" />
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <Label className="text-xs mb-1">Tipo</Label>
                <select value={posForm.type} onChange={(e) => setPosForm((p) => ({ ...p, type: e.target.value }))} className="w-full h-8 text-xs border border-gray-200 rounded-md px-2 bg-white focus:outline-none focus:ring-1 focus:ring-violet-500">
                  <option value="PHYSICAL">Físico</option>
                  <option value="VIRTUAL">Virtual</option>
                </select>
              </div>
              <div>
                <Label className="text-xs mb-1">Sucursal SIAT *</Label>
                <select value={posForm.branchOfficeId} onChange={(e) => setPosForm((p) => ({ ...p, branchOfficeId: e.target.value }))} className="w-full h-8 text-xs border border-gray-200 rounded-md px-2 bg-white focus:outline-none focus:ring-1 focus:ring-violet-500">
                  <option value="">Seleccionar...</option>
                  {siatBranches.map((b) => (
                    <option key={b.id} value={b.id}>{b.name} (Cod. {b.branchOfficeSiatId})</option>
                  ))}
                </select>
              </div>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" size="sm" onClick={() => setPosDialog(false)}>Cancelar</Button>
            <Button size="sm" className="bg-violet-600 hover:bg-violet-700 text-white" disabled={!posForm.code || !posForm.name || !posForm.branchOfficeId || saving} onClick={handleCreatePos}>
              {saving && <Loader2 className="w-3 h-3 animate-spin mr-1" />}
              Registrar
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* ─── Signature Upload Dialog ──────────────────────────────────── */}
      <Dialog open={sigDialog} onOpenChange={setSigDialog}>
        <DialogContent className="sm:max-w-lg">
          <DialogHeader>
            <DialogTitle className="text-sm font-semibold flex items-center gap-2">
              <Shield className="w-4 h-4 text-violet-600" />
              Subir certificado digital
            </DialogTitle>
          </DialogHeader>
          <div className="space-y-3">
            <div>
              <Label className="text-xs">Certificado (PEM) *</Label>
              <textarea
                value={sigForm.certificate}
                onChange={(e) => setSigForm((p) => ({ ...p, certificate: e.target.value }))}
                placeholder="-----BEGIN CERTIFICATE-----&#10;...&#10;-----END CERTIFICATE-----"
                rows={5}
                className="w-full text-xs border border-gray-200 rounded-md px-3 py-2 font-mono bg-white focus:outline-none focus:ring-1 focus:ring-violet-500 resize-none"
              />
            </div>
            <div>
              <Label className="text-xs">Llave Privada (PEM) *</Label>
              <textarea
                value={sigForm.privateKey}
                onChange={(e) => setSigForm((p) => ({ ...p, privateKey: e.target.value }))}
                placeholder="-----BEGIN PRIVATE KEY-----&#10;...&#10;-----END PRIVATE KEY-----"
                rows={5}
                className="w-full text-xs border border-gray-200 rounded-md px-3 py-2 font-mono bg-white focus:outline-none focus:ring-1 focus:ring-violet-500 resize-none"
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" size="sm" onClick={() => setSigDialog(false)}>Cancelar</Button>
            <Button size="sm" className="bg-violet-600 hover:bg-violet-700 text-white" disabled={!sigForm.certificate || !sigForm.privateKey || sigSaving} onClick={handleUploadSignature}>
              {sigSaving && <Loader2 className="w-3 h-3 animate-spin mr-1" />}
              Subir
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* ─── Cancel Invoice Dialog ────────────────────────────────────── */}
      <Dialog open={cancelDialog} onOpenChange={setCancelDialog}>
        <DialogContent className="sm:max-w-sm">
          <DialogHeader>
            <DialogTitle className="text-sm font-semibold">Anular factura</DialogTitle>
          </DialogHeader>
          <div className="space-y-3">
            <div>
              <Label className="text-xs">Código motivo de anulación *</Label>
              <Input
                type="number"
                value={cancelReason}
                onChange={(e) => setCancelReason(e.target.value)}
                placeholder="1"
                className="h-8 text-xs"
              />
              <p className="text-[10px] text-gray-400 mt-1">1 = Error de datos, 2 = Nota de crédito/débito, etc.</p>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" size="sm" onClick={() => setCancelDialog(false)}>Cancelar</Button>
            <Button size="sm" className="bg-red-600 hover:bg-red-700 text-white" disabled={!cancelReason || cancelling} onClick={handleCancelInvoice}>
              {cancelling && <Loader2 className="w-3 h-3 animate-spin mr-1" />}
              Anular
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}

// ─── Sync Card ────────────────────────────────────────────────────────────

function SyncCard({
  title, description, icon, loading, result, onSync,
}: {
  title: string;
  description: string;
  icon: React.ReactNode;
  loading: boolean;
  result?: { ok: boolean; message: string };
  onSync: () => void;
}) {
  return (
    <Card className="border-gray-100">
      <CardHeader className="pb-2">
        <CardTitle className="text-sm font-medium flex items-center gap-2">
          {icon}
          {title}
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-3">
        <p className="text-xs text-gray-400">{description}</p>
        {result && (
          <div className={`flex items-center gap-1.5 text-xs ${result.ok ? 'text-green-600' : 'text-red-500'}`}>
            {result.ok ? <CheckCircle2 className="w-3.5 h-3.5" /> : <AlertCircle className="w-3.5 h-3.5" />}
            {result.message}
          </div>
        )}
        <Button size="sm" variant="outline" className="w-full" disabled={loading} onClick={onSync}>
          {loading ? <Loader2 className="w-3.5 h-3.5 animate-spin mr-1" /> : <RefreshCw className="w-3.5 h-3.5 mr-1" />}
          Sincronizar
        </Button>
      </CardContent>
    </Card>
  );
}
