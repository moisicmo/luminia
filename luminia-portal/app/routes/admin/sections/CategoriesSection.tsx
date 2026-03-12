import { useEffect, useState } from 'react';
import { Plus, Pencil, Trash2, Tag, Loader2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Badge } from '@/components/ui/badge';
import { luminiApi } from '@/services/luminiApi';

interface Category {
  id: string;
  name: string;
  description: string;
  _count?: { products: number };
}

const EMPTY = { name: '', description: '' };

export function CategoriesSection() {
  const [cats, setCats] = useState<Category[]>([]);
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
      const { data } = await luminiApi.get('/inventory/categories');
      setCats(Array.isArray(data) ? data : []);
    } catch {
      setCats([]);
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

  function openEdit(c: Category) {
    setForm({ name: c.name, description: c.description || '' });
    setEditingId(c.id);
    setError('');
    setOpen(true);
  }

  async function handleSave() {
    if (!form.name.trim()) { setError('El nombre es requerido'); return; }
    setSaving(true);
    setError('');
    try {
      if (editingId) {
        await luminiApi.patch(`/inventory/categories/${editingId}`, form);
      } else {
        await luminiApi.post('/inventory/categories', form);
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
    if (!confirm('¿Eliminar esta categoría?')) return;
    try {
      await luminiApi.delete(`/inventory/categories/${id}`);
      await load();
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
          <Plus className="w-4 h-4 mr-1" /> Nueva categoría
        </Button>
      </div>

      {cats.length === 0 ? (
        <div className="flex flex-col items-center py-16 text-gray-400">
          <Tag className="w-10 h-10 mb-3 text-gray-200" />
          <p className="text-sm font-medium">No hay categorías</p>
          <p className="text-xs mt-1">Crea una categoría para organizar tus productos</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          {cats.map((c) => (
            <div key={c.id} className="bg-white border border-gray-100 rounded-xl p-4 flex items-start gap-3">
              <div className="w-9 h-9 rounded-lg bg-violet-50 flex items-center justify-center shrink-0">
                <Tag className="w-4 h-4 text-violet-600" />
              </div>
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2">
                  <p className="font-semibold text-sm text-gray-800">{c.name}</p>
                  {c._count?.products != null && (
                    <Badge variant="secondary" className="text-[10px]">{c._count.products} productos</Badge>
                  )}
                </div>
                {c.description && <p className="text-xs text-gray-400 mt-0.5 truncate">{c.description}</p>}
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
          <DialogHeader><DialogTitle>{editingId ? 'Editar categoría' : 'Nueva categoría'}</DialogTitle></DialogHeader>
          <div className="space-y-4 pt-2">
            <div className="space-y-1.5">
              <Label>Nombre</Label>
              <Input value={form.name} onChange={(e) => setForm((p) => ({ ...p, name: e.target.value }))} placeholder="Ej: Ropa, Calzado..." />
            </div>
            <div className="space-y-1.5">
              <Label>Descripción</Label>
              <Input value={form.description} onChange={(e) => setForm((p) => ({ ...p, description: e.target.value }))} placeholder="Descripción breve" />
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
