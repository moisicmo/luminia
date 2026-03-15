import { useEffect, useState } from 'react';
import {
  Plus, Pencil, Trash2, MapPin, Loader2, Building2, Monitor,
  ChevronDown, ChevronRight, Printer,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Badge } from '@/components/ui/badge';
import { luminiApi } from '@/services/luminiApi';

// ─── Types ──────────────────────────────────────────────────────────────────

type PaperSize = 'LETTER' | 'ROLL_80' | 'ROLL_58';

const PAPER_LABELS: Record<PaperSize, string> = {
  LETTER: 'Carta',
  ROLL_80: 'Rollo 80mm',
  ROLL_58: 'Rollo 58mm',
};

interface PointOfSale {
  id: string;
  name: string;
  paperSize: PaperSize;
  active: boolean;
}

interface Branch {
  id: string;
  name: string;
  region: string;
  address?: string;
  municipality?: string;
  phone?: string;
  pointsOfSale: PointOfSale[];
}

const EMPTY_BRANCH = { name: '', region: '', address: '', municipality: '', phone: '' };

export function BranchesSection() {
  const [branches, setBranches] = useState<Branch[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  // branch dialog
  const [branchOpen, setBranchOpen] = useState(false);
  const [branchForm, setBranchForm] = useState(EMPTY_BRANCH);
  const [editingBranchId, setEditingBranchId] = useState<string | null>(null);
  const [error, setError] = useState('');

  // pos dialog
  const [posOpen, setPosOpen] = useState(false);
  const [posName, setPosName] = useState('');
  const [posPaperSize, setPosPaperSize] = useState<PaperSize>('LETTER');
  const [posBranchId, setPosBranchId] = useState('');
  const [editingPosId, setEditingPosId] = useState<string | null>(null);

  // expanded branches
  const [expanded, setExpanded] = useState<Set<string>>(new Set());

  const businessId = localStorage.getItem('luminia_business_id') ?? '';

  useEffect(() => { load(); }, []);

  async function load() {
    setLoading(true);
    try {
      const { data } = await luminiApi.get(`/business/${businessId}/branches`);
      const list: Branch[] = Array.isArray(data) ? data : [];
      setBranches(list);
      if (expanded.size === 0) {
        setExpanded(new Set(list.map((b) => b.id)));
      }
    } catch {
      setBranches([]);
    } finally {
      setLoading(false);
    }
  }

  function toggleExpand(id: string) {
    setExpanded((prev) => {
      const next = new Set(prev);
      next.has(id) ? next.delete(id) : next.add(id);
      return next;
    });
  }

  // ─── Branch CRUD ──────────────────────────────────────────────────────────

  function openCreateBranch() {
    setBranchForm({ ...EMPTY_BRANCH });
    setEditingBranchId(null);
    setError('');
    setBranchOpen(true);
  }

  function openEditBranch(b: Branch) {
    setBranchForm({
      name: b.name,
      region: b.region,
      address: b.address ?? '',
      municipality: b.municipality ?? '',
      phone: b.phone ?? '',
    });
    setEditingBranchId(b.id);
    setError('');
    setBranchOpen(true);
  }

  async function handleSaveBranch() {
    if (!branchForm.name.trim()) { setError('El nombre es requerido'); return; }
    if (!branchForm.region.trim()) { setError('La región/ciudad es requerida'); return; }
    setSaving(true);
    setError('');
    try {
      if (editingBranchId) {
        await luminiApi.patch(`/business/${businessId}/branches/${editingBranchId}`, branchForm);
      } else {
        await luminiApi.post(`/business/${businessId}/branches`, branchForm);
      }
      setBranchOpen(false);
      await load();
    } catch (e: any) {
      setError(e.response?.data?.message || 'Error al guardar');
    } finally {
      setSaving(false);
    }
  }

  async function handleDeleteBranch(id: string) {
    if (!confirm('¿Desactivar esta sucursal?')) return;
    try {
      await luminiApi.delete(`/business/${businessId}/branches/${id}`);
      await load();
    } catch (e: any) {
      alert(e.response?.data?.message || 'Error al eliminar');
    }
  }

  // ─── POS CRUD ─────────────────────────────────────────────────────────────

  function openCreatePos(branchId: string) {
    setPosName('');
    setPosPaperSize('LETTER');
    setPosBranchId(branchId);
    setEditingPosId(null);
    setPosOpen(true);
  }

  function openEditPos(branchId: string, pos: PointOfSale) {
    setPosName(pos.name);
    setPosPaperSize(pos.paperSize ?? 'LETTER');
    setPosBranchId(branchId);
    setEditingPosId(pos.id);
    setPosOpen(true);
  }

  async function handleSavePos() {
    if (!posName.trim()) return;
    setSaving(true);
    try {
      const posDto = { name: posName, paperSize: posPaperSize };
      if (editingPosId) {
        await luminiApi.patch(`/business/${businessId}/branches/${posBranchId}/pos/${editingPosId}`, posDto);
      } else {
        await luminiApi.post(`/business/${businessId}/branches/${posBranchId}/pos`, posDto);
      }
      setPosOpen(false);
      await load();
    } catch (e: any) {
      alert(e.response?.data?.message || 'Error al guardar');
    } finally {
      setSaving(false);
    }
  }

  async function handleDeletePos(branchId: string, posId: string) {
    if (!confirm('¿Desactivar este punto de venta?')) return;
    try {
      await luminiApi.delete(`/business/${businessId}/branches/${branchId}/pos/${posId}`);
      await load();
    } catch (e: any) {
      alert(e.response?.data?.message || 'Error al eliminar');
    }
  }

  // ─── Render ───────────────────────────────────────────────────────────────

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <Loader2 className="w-6 h-6 animate-spin text-violet-500" />
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex justify-end">
        <Button onClick={openCreateBranch} size="sm" className="bg-violet-600 hover:bg-violet-700 text-white">
          <Plus className="w-4 h-4 mr-1" /> Nueva sucursal
        </Button>
      </div>

      {branches.length === 0 ? (
        <div className="flex flex-col items-center py-16 text-gray-400">
          <Building2 className="w-10 h-10 mb-3 text-gray-200" />
          <p className="text-sm font-medium">No hay sucursales</p>
          <p className="text-xs mt-1">Crea una sucursal para gestionar tus puntos de venta</p>
        </div>
      ) : (
        <div className="grid gap-3">
          {branches.map((branch) => {
            const isExpanded = expanded.has(branch.id);
            return (
              <div key={branch.id} className="bg-white border border-gray-100 rounded-xl overflow-hidden">
                {/* Branch header */}
                <div className="flex items-start gap-4 p-5">
                  <button
                    onClick={() => toggleExpand(branch.id)}
                    className="mt-1 p-1 text-gray-400 hover:text-violet-600 rounded transition"
                  >
                    {isExpanded
                      ? <ChevronDown className="w-4 h-4" />
                      : <ChevronRight className="w-4 h-4" />
                    }
                  </button>
                  <div className="w-10 h-10 rounded-xl bg-violet-50 flex items-center justify-center shrink-0">
                    <Building2 className="w-5 h-5 text-violet-600" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 flex-wrap">
                      <h3 className="font-semibold text-gray-900">{branch.name}</h3>
                      <Badge className="bg-gray-100 text-gray-500 text-[10px]">
                        {branch.pointsOfSale.length} {branch.pointsOfSale.length === 1 ? 'caja' : 'cajas'}
                      </Badge>
                    </div>
                    <div className="flex flex-wrap items-center gap-3 mt-1 text-sm text-gray-500">
                      {branch.region && (
                        <span className="flex items-center gap-1">
                          <MapPin className="w-3.5 h-3.5 text-gray-300" /> {branch.region}
                        </span>
                      )}
                      {branch.address && <span>{branch.address}</span>}
                      {branch.phone && <span>{branch.phone}</span>}
                    </div>
                  </div>
                  <div className="flex gap-1 shrink-0">
                    <button onClick={() => openEditBranch(branch)} className="p-2 text-gray-400 hover:text-violet-600 rounded-lg hover:bg-violet-50 transition-all">
                      <Pencil className="w-4 h-4" />
                    </button>
                    <button onClick={() => handleDeleteBranch(branch.id)} className="p-2 text-gray-400 hover:text-red-500 rounded-lg hover:bg-red-50 transition-all">
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>

                {/* Points of Sale */}
                {isExpanded && (
                  <div className="border-t border-gray-100 bg-gray-50/50 px-5 py-3">
                    <div className="flex items-center justify-between mb-2">
                      <span className="text-xs font-medium text-gray-500 uppercase tracking-wider">Puntos de venta</span>
                      <button
                        onClick={() => openCreatePos(branch.id)}
                        className="text-xs text-violet-600 hover:text-violet-700 font-medium flex items-center gap-1"
                      >
                        <Plus className="w-3 h-3" /> Agregar
                      </button>
                    </div>
                    {branch.pointsOfSale.length === 0 ? (
                      <p className="text-xs text-gray-400 py-2">Sin puntos de venta</p>
                    ) : (
                      <div className="space-y-1.5">
                        {branch.pointsOfSale.map((pos) => (
                          <div key={pos.id} className="flex items-center gap-3 bg-white rounded-lg px-3 py-2 border border-gray-100">
                            <Monitor className="w-4 h-4 text-gray-400" />
                            <span className="text-sm text-gray-700 flex-1">{pos.name}</span>
                            <span className="flex items-center gap-1 text-[10px] text-gray-400">
                              <Printer className="w-3 h-3" /> {PAPER_LABELS[pos.paperSize] ?? 'Carta'}
                            </span>
                            <button onClick={() => openEditPos(branch.id, pos)} className="p-1 text-gray-400 hover:text-violet-600 rounded transition">
                              <Pencil className="w-3.5 h-3.5" />
                            </button>
                            <button onClick={() => handleDeletePos(branch.id, pos.id)} className="p-1 text-gray-400 hover:text-red-500 rounded transition">
                              <Trash2 className="w-3.5 h-3.5" />
                            </button>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}

      {/* ─── Branch Dialog ──────────────────────────────────────────────── */}
      <Dialog open={branchOpen} onOpenChange={setBranchOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>{editingBranchId ? 'Editar sucursal' : 'Nueva sucursal'}</DialogTitle>
          </DialogHeader>
          <div className="space-y-4 pt-2">
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1.5">
                <Label>Nombre *</Label>
                <Input value={branchForm.name} onChange={(e) => setBranchForm((p) => ({ ...p, name: e.target.value }))} placeholder="Sucursal Centro" />
              </div>
              <div className="space-y-1.5">
                <Label>Ciudad / Región *</Label>
                <Input value={branchForm.region} onChange={(e) => setBranchForm((p) => ({ ...p, region: e.target.value }))} placeholder="Santa Cruz" />
              </div>
            </div>
            <div className="space-y-1.5">
              <Label>Dirección</Label>
              <Input value={branchForm.address} onChange={(e) => setBranchForm((p) => ({ ...p, address: e.target.value }))} placeholder="Av. Principal #123" />
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1.5">
                <Label>Municipio</Label>
                <Input value={branchForm.municipality} onChange={(e) => setBranchForm((p) => ({ ...p, municipality: e.target.value }))} placeholder="Municipio" />
              </div>
              <div className="space-y-1.5">
                <Label>Teléfono</Label>
                <Input value={branchForm.phone} onChange={(e) => setBranchForm((p) => ({ ...p, phone: e.target.value }))} placeholder="3-XXXXXXX" />
              </div>
            </div>
            {error && <p className="text-xs text-red-500">{error}</p>}
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setBranchOpen(false)} disabled={saving}>Cancelar</Button>
            <Button className="bg-violet-600 hover:bg-violet-700 text-white" onClick={handleSaveBranch} disabled={saving}>
              {saving ? <Loader2 className="w-4 h-4 animate-spin" /> : editingBranchId ? 'Guardar' : 'Crear'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* ─── POS Dialog ─────────────────────────────────────────────────── */}
      <Dialog open={posOpen} onOpenChange={setPosOpen}>
        <DialogContent className="max-w-sm">
          <DialogHeader>
            <DialogTitle>{editingPosId ? 'Editar punto de venta' : 'Nuevo punto de venta'}</DialogTitle>
          </DialogHeader>
          <div className="space-y-4 pt-2">
            <div className="space-y-1.5">
              <Label>Nombre *</Label>
              <Input value={posName} onChange={(e) => setPosName(e.target.value)} placeholder="Caja 2" />
            </div>
            <div className="space-y-1.5">
              <Label>Tamaño de papel para impresión</Label>
              <select
                value={posPaperSize}
                onChange={(e) => setPosPaperSize(e.target.value as PaperSize)}
                className="w-full rounded-md border border-gray-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-violet-500"
              >
                <option value="LETTER">Carta (215.9 × 279.4 mm)</option>
                <option value="ROLL_80">Rollo POS 80mm</option>
                <option value="ROLL_58">Rollo POS 58mm</option>
              </select>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setPosOpen(false)} disabled={saving}>Cancelar</Button>
            <Button className="bg-violet-600 hover:bg-violet-700 text-white" onClick={handleSavePos} disabled={saving || !posName.trim()}>
              {saving ? <Loader2 className="w-4 h-4 animate-spin" /> : editingPosId ? 'Guardar' : 'Crear'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
