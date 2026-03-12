import { useEffect, useState } from 'react';
import { Plus, Pencil, MapPin, Phone, Users, Loader2, Building2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Badge } from '@/components/ui/badge';
import { luminiApi } from '@/services/luminiApi';

interface Branch {
  id: string;
  name: string;
  region: string;
  address: string;
  phone: string;
  municipality?: string;
  active: boolean;
}

const EMPTY = { name: '', region: '', address: '', phone: '', municipality: '' };

export function BranchesSection() {
  const [branches, setBranches] = useState<Branch[]>([]);
  const [loading, setLoading] = useState(true);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(EMPTY);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [error, setError] = useState('');

  useEffect(() => { load(); }, []);

  async function load() {
    setLoading(true);
    try {
      const businessId = localStorage.getItem('luminia_business_id');
      if (!businessId) { setBranches([]); return; }
      const { data } = await luminiApi.get(`/business/${businessId}`);
      const biz = data;
      // branches pueden venir del business o como relación
      const branchList = biz.branches ?? (biz.branch ? [biz.branch] : []);
      setBranches(branchList.map((b: any) => ({
        id: b.id,
        name: b.name,
        region: b.region || '',
        address: b.address || '',
        phone: b.phone || '',
        municipality: b.municipality || '',
        active: b.active !== false,
      })));
    } catch {
      setBranches([]);
    } finally {
      setLoading(false);
    }
  }

  function openCreate() {
    setForm({ ...EMPTY });
    setEditingId(null);
    setError('');
    setOpen(true);
  }

  function openEdit(b: Branch) {
    setForm({ name: b.name, region: b.region, address: b.address, phone: b.phone, municipality: b.municipality || '' });
    setEditingId(b.id);
    setError('');
    setOpen(true);
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
      <div className="flex justify-end">
        <Button onClick={openCreate} size="sm" className="bg-violet-600 hover:bg-violet-700 text-white">
          <Plus className="w-4 h-4 mr-1" /> Nueva sucursal
        </Button>
      </div>

      {branches.length === 0 ? (
        <div className="flex flex-col items-center py-16 text-gray-400">
          <Building2 className="w-10 h-10 mb-3 text-gray-200" />
          <p className="text-sm font-medium">No hay sucursales</p>
        </div>
      ) : (
        <div className="grid gap-3">
          {branches.map((b) => (
            <div key={b.id} className="bg-white border border-gray-100 rounded-xl p-5 flex items-start gap-4">
              <div className="w-10 h-10 rounded-xl bg-violet-50 flex items-center justify-center shrink-0">
                <MapPin className="w-5 h-5 text-violet-600" />
              </div>
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 flex-wrap">
                  <h3 className="font-semibold text-gray-900">{b.name}</h3>
                  <Badge className={b.active ? 'bg-green-100 text-green-700 hover:bg-green-100' : 'bg-gray-100 text-gray-500 hover:bg-gray-100'}>
                    {b.active ? 'Activa' : 'Inactiva'}
                  </Badge>
                </div>
                <div className="mt-2 flex flex-wrap gap-4 text-sm text-gray-500">
                  {(b.address || b.region) && (
                    <span className="flex items-center gap-1">
                      <MapPin className="w-3.5 h-3.5 text-gray-300" />
                      {[b.address, b.municipality, b.region].filter(Boolean).join(', ')}
                    </span>
                  )}
                  {b.phone && (
                    <span className="flex items-center gap-1">
                      <Phone className="w-3.5 h-3.5 text-gray-300" />
                      {b.phone}
                    </span>
                  )}
                </div>
              </div>
              <button onClick={() => openEdit(b)} className="p-2 text-gray-400 hover:text-violet-600 rounded-lg hover:bg-violet-50 transition-all shrink-0">
                <Pencil className="w-4 h-4" />
              </button>
            </div>
          ))}
        </div>
      )}

      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader><DialogTitle>{editingId ? 'Editar sucursal' : 'Nueva sucursal'}</DialogTitle></DialogHeader>
          <div className="space-y-4 pt-2">
            <div className="space-y-1.5">
              <Label>Nombre</Label>
              <Input value={form.name} onChange={(e) => setForm((p) => ({ ...p, name: e.target.value }))} placeholder="Ej: Sucursal Centro" />
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1.5">
                <Label>Ciudad/Región</Label>
                <Input value={form.region} onChange={(e) => setForm((p) => ({ ...p, region: e.target.value }))} placeholder="La Paz" />
              </div>
              <div className="space-y-1.5">
                <Label>Teléfono</Label>
                <Input value={form.phone} onChange={(e) => setForm((p) => ({ ...p, phone: e.target.value }))} placeholder="+591..." />
              </div>
            </div>
            <div className="space-y-1.5">
              <Label>Dirección</Label>
              <Input value={form.address} onChange={(e) => setForm((p) => ({ ...p, address: e.target.value }))} placeholder="Av. ..." />
            </div>
            <div className="space-y-1.5">
              <Label>Municipio</Label>
              <Input value={form.municipality} onChange={(e) => setForm((p) => ({ ...p, municipality: e.target.value }))} placeholder="Municipio" />
            </div>
            {error && <p className="text-xs text-red-500">{error}</p>}
            <div className="flex gap-2 pt-1">
              <Button variant="outline" className="flex-1" onClick={() => setOpen(false)}>Cancelar</Button>
              <Button className="flex-1 bg-violet-600 hover:bg-violet-700 text-white" onClick={() => { setOpen(false); alert('Endpoint de sucursales pendiente de implementar en el gateway'); }}>
                {editingId ? 'Guardar' : 'Crear'}
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
