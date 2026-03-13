import { useEffect, useState } from 'react';
import { Plus, Pencil, Trash2, MapPin, Loader2, Warehouse } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Badge } from '@/components/ui/badge';
import { luminiApi } from '@/services/luminiApi';
import { useWarehouse } from '../components/AdminLayout';

interface WarehouseRecord {
  id: string;
  name: string;
  description?: string;
  address?: string;
  isDefault: boolean;
}

const EMPTY = { name: '', description: '', address: '', isDefault: false };

export function BranchesSection() {
  const { refreshWarehouses } = useWarehouse();
  const [warehouses, setWarehouses] = useState<WarehouseRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(EMPTY);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [error, setError] = useState('');

  useEffect(() => { load(); }, []);

  async function load() {
    setLoading(true);
    try {
      const { data } = await luminiApi.get('/inventory/warehouses');
      setWarehouses(Array.isArray(data) ? data : []);
    } catch {
      setWarehouses([]);
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

  function openEdit(w: WarehouseRecord) {
    setForm({ name: w.name, description: w.description ?? '', address: w.address ?? '', isDefault: w.isDefault });
    setEditingId(w.id);
    setError('');
    setOpen(true);
  }

  async function handleSave() {
    if (!form.name.trim()) { setError('El nombre es requerido'); return; }
    setSaving(true);
    setError('');
    try {
      const payload: any = { name: form.name };
      if (form.description) payload.description = form.description;
      if (form.address) payload.address = form.address;
      payload.isDefault = form.isDefault;

      if (editingId) {
        await luminiApi.patch(`/inventory/warehouses/${editingId}`, payload);
      } else {
        await luminiApi.post('/inventory/warehouses', payload);
      }
      setOpen(false);
      await load();
      refreshWarehouses();
    } catch (e: any) {
      setError(e.response?.data?.message || 'Error al guardar');
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(id: string) {
    if (!confirm('¿Eliminar esta sucursal/almacén?')) return;
    try {
      await luminiApi.delete(`/inventory/warehouses/${id}`);
      await load();
      refreshWarehouses();
    } catch (e: any) {
      alert(e.response?.data?.message || 'Error al eliminar');
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
      <div className="flex justify-end">
        <Button onClick={openCreate} size="sm" className="bg-violet-600 hover:bg-violet-700 text-white">
          <Plus className="w-4 h-4 mr-1" /> Nueva sucursal
        </Button>
      </div>

      {warehouses.length === 0 ? (
        <div className="flex flex-col items-center py-16 text-gray-400">
          <Warehouse className="w-10 h-10 mb-3 text-gray-200" />
          <p className="text-sm font-medium">No hay sucursales</p>
          <p className="text-xs mt-1">Crea una sucursal o almacén para gestionar tu inventario</p>
        </div>
      ) : (
        <div className="grid gap-3">
          {warehouses.map((w) => (
            <div key={w.id} className="bg-white border border-gray-100 rounded-xl p-5 flex items-start gap-4">
              <div className="w-10 h-10 rounded-xl bg-violet-50 flex items-center justify-center shrink-0">
                <MapPin className="w-5 h-5 text-violet-600" />
              </div>
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 flex-wrap">
                  <h3 className="font-semibold text-gray-900">{w.name}</h3>
                  {w.isDefault && (
                    <Badge className="bg-violet-100 text-violet-700 hover:bg-violet-100 text-[10px]">Principal</Badge>
                  )}
                </div>
                {w.description && <p className="text-xs text-gray-400 mt-0.5">{w.description}</p>}
                {w.address && (
                  <span className="flex items-center gap-1 mt-1 text-sm text-gray-500">
                    <MapPin className="w-3.5 h-3.5 text-gray-300" /> {w.address}
                  </span>
                )}
              </div>
              <div className="flex gap-1 shrink-0">
                <button onClick={() => openEdit(w)} className="p-2 text-gray-400 hover:text-violet-600 rounded-lg hover:bg-violet-50 transition-all">
                  <Pencil className="w-4 h-4" />
                </button>
                <button onClick={() => handleDelete(w.id)} className="p-2 text-gray-400 hover:text-red-500 rounded-lg hover:bg-red-50 transition-all">
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader><DialogTitle>{editingId ? 'Editar sucursal' : 'Nueva sucursal'}</DialogTitle></DialogHeader>
          <div className="space-y-4 pt-2">
            <div className="space-y-1.5">
              <Label>Nombre *</Label>
              <Input value={form.name} onChange={(e) => setForm((p) => ({ ...p, name: e.target.value }))} placeholder="Ej: Sucursal Centro" />
            </div>
            <div className="space-y-1.5">
              <Label>Descripción</Label>
              <Input value={form.description} onChange={(e) => setForm((p) => ({ ...p, description: e.target.value }))} placeholder="Almacén principal del negocio" />
            </div>
            <div className="space-y-1.5">
              <Label>Dirección</Label>
              <Input value={form.address} onChange={(e) => setForm((p) => ({ ...p, address: e.target.value }))} placeholder="Av. Principal #123" />
            </div>
            <label className="flex items-center gap-2 cursor-pointer">
              <input
                type="checkbox"
                checked={form.isDefault}
                onChange={(e) => setForm((p) => ({ ...p, isDefault: e.target.checked }))}
                className="rounded border-gray-300 text-violet-600 focus:ring-violet-500"
              />
              <span className="text-sm text-gray-700">Sucursal principal</span>
            </label>
            {error && <p className="text-xs text-red-500">{error}</p>}
            <div className="flex gap-2 pt-1">
              <Button variant="outline" className="flex-1" onClick={() => setOpen(false)} disabled={saving}>Cancelar</Button>
              <Button className="flex-1 bg-violet-600 hover:bg-violet-700 text-white" onClick={handleSave} disabled={saving}>
                {saving ? <Loader2 className="w-4 h-4 animate-spin" /> : editingId ? 'Guardar' : 'Crear'}
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
