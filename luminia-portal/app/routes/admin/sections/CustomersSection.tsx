import { useEffect, useState } from 'react';
import { Plus, Pencil, Trash2, UserCheck, Loader2, Mail, Phone } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { luminiApi } from '@/services/luminiApi';

interface Customer {
  id: string;
  name: string;
  lastName?: string;
  taxId?: string;
  phone?: string;
  email?: string;
  address?: string;
}

const EMPTY = { name: '', lastName: '', taxId: '', phone: '', email: '', address: '' };

export function CustomersSection() {
  const businessId = localStorage.getItem('luminia_business_id') ?? '';
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(EMPTY);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');

  useEffect(() => { load(); }, []);

  async function load() {
    setLoading(true);
    try {
      const { data } = await luminiApi.get(`/business/${businessId}/customers`);
      setCustomers(Array.isArray(data) ? data : []);
    } catch {
      setCustomers([]);
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

  function openEdit(c: Customer) {
    setForm({
      name: c.name,
      lastName: c.lastName ?? '',
      taxId: c.taxId ?? '',
      phone: c.phone ?? '',
      email: c.email ?? '',
      address: c.address ?? '',
    });
    setEditingId(c.id);
    setError('');
    setOpen(true);
  }

  async function handleSave() {
    if (!form.name.trim()) { setError('El nombre es requerido'); return; }
    setSaving(true);
    setError('');
    try {
      const payload: any = { name: form.name };
      if (form.lastName) payload.lastName = form.lastName;
      if (form.taxId) payload.taxId = form.taxId;
      if (form.phone) payload.phone = form.phone;
      if (form.email) payload.email = form.email;
      if (form.address) payload.address = form.address;

      if (editingId) {
        await luminiApi.patch(`/business/${businessId}/customers/${editingId}`, payload);
      } else {
        await luminiApi.post(`/business/${businessId}/customers`, payload);
      }
      setOpen(false);
      await load();
    } catch (e: any) {
      setError(e.response?.data?.message || 'Error al guardar');
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(id: string) {
    if (!confirm('¿Eliminar este cliente?')) return;
    try {
      await luminiApi.delete(`/business/${businessId}/customers/${id}`);
      await load();
    } catch (e: any) {
      alert(e.response?.data?.message || 'Error al eliminar');
    }
  }

  const filtered = customers.filter((c) => {
    const fullName = `${c.name} ${c.lastName ?? ''}`.toLowerCase();
    return fullName.includes(search.toLowerCase()) || (c.phone ?? '').includes(search) || (c.email ?? '').toLowerCase().includes(search.toLowerCase());
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
      <div className="flex gap-3 items-center">
        <div className="relative flex-1 max-w-xs">
          <Input placeholder="Buscar cliente..." value={search} onChange={(e) => setSearch(e.target.value)} className="h-9 text-sm" />
        </div>
        <Button onClick={openCreate} size="sm" className="bg-violet-600 hover:bg-violet-700 text-white">
          <Plus className="w-4 h-4 mr-1" /> Nuevo cliente
        </Button>
      </div>

      {filtered.length === 0 ? (
        <div className="flex flex-col items-center py-16 text-gray-400">
          <UserCheck className="w-10 h-10 mb-3 text-gray-200" />
          <p className="text-sm font-medium">{customers.length === 0 ? 'No hay clientes' : 'Sin resultados'}</p>
          <p className="text-xs mt-1">Crea un cliente para registrar tus ventas</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          {filtered.map((c) => (
            <div key={c.id} className="bg-white border border-gray-100 rounded-xl p-4 flex items-start gap-3">
              <div className="w-9 h-9 rounded-lg bg-violet-50 flex items-center justify-center shrink-0">
                <UserCheck className="w-4 h-4 text-violet-600" />
              </div>
              <div className="flex-1 min-w-0">
                <p className="font-semibold text-sm text-gray-800">{c.name} {c.lastName ?? ''}</p>
                {c.taxId && <p className="text-[10px] text-gray-400">NIT/CI: {c.taxId}</p>}
                <div className="flex gap-3 mt-1">
                  {c.phone && (
                    <span className="flex items-center gap-1 text-[10px] text-gray-400">
                      <Phone className="w-3 h-3" /> {c.phone}
                    </span>
                  )}
                  {c.email && (
                    <span className="flex items-center gap-1 text-[10px] text-gray-400">
                      <Mail className="w-3 h-3" /> {c.email}
                    </span>
                  )}
                </div>
              </div>
              <div className="flex gap-1 shrink-0">
                <button onClick={() => openEdit(c)} className="p-1.5 text-gray-400 hover:text-violet-600 rounded-lg hover:bg-violet-50 transition-all">
                  <Pencil className="w-3.5 h-3.5" />
                </button>
                <button onClick={() => handleDelete(c.id)} className="p-1.5 text-gray-400 hover:text-red-500 rounded-lg hover:bg-red-50 transition-all">
                  <Trash2 className="w-3.5 h-3.5" />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent className="max-w-sm">
          <DialogHeader><DialogTitle>{editingId ? 'Editar cliente' : 'Nuevo cliente'}</DialogTitle></DialogHeader>
          <div className="space-y-3 pt-2">
            <div className="grid grid-cols-2 gap-2">
              <div className="space-y-1.5">
                <Label>Nombre *</Label>
                <Input value={form.name} onChange={(e) => setForm((p) => ({ ...p, name: e.target.value }))} placeholder="Juan" />
              </div>
              <div className="space-y-1.5">
                <Label>Apellido</Label>
                <Input value={form.lastName} onChange={(e) => setForm((p) => ({ ...p, lastName: e.target.value }))} placeholder="Pérez" />
              </div>
            </div>
            <div className="space-y-1.5">
              <Label>NIT / CI</Label>
              <Input value={form.taxId} onChange={(e) => setForm((p) => ({ ...p, taxId: e.target.value }))} placeholder="1234567890" />
            </div>
            <div className="grid grid-cols-2 gap-2">
              <div className="space-y-1.5">
                <Label>Teléfono</Label>
                <Input value={form.phone} onChange={(e) => setForm((p) => ({ ...p, phone: e.target.value }))} placeholder="+591 7XXXXXXX" />
              </div>
              <div className="space-y-1.5">
                <Label>Email</Label>
                <Input value={form.email} onChange={(e) => setForm((p) => ({ ...p, email: e.target.value }))} placeholder="correo@ejemplo.com" />
              </div>
            </div>
            <div className="space-y-1.5">
              <Label>Dirección</Label>
              <Input value={form.address} onChange={(e) => setForm((p) => ({ ...p, address: e.target.value }))} placeholder="Av. Principal #123" />
            </div>
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
