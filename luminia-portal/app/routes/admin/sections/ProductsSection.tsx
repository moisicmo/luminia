import { useEffect, useState } from 'react';
import { Plus, Search, Pencil, Trash2, Package, Loader2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from '@/components/ui/table';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { luminiApi } from '@/services/luminiApi';

interface Product {
  id: string;
  name: string;
  description?: string;
  categoryId?: string;
  categoryName?: string;
  price: number;
  cost?: number;
  stock?: number;
  active: boolean;
}

interface Category {
  id: string;
  name: string;
}

const EMPTY_FORM = { name: '', description: '', categoryId: '', price: 0, cost: 0, active: true };

export function ProductsSection() {
  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [search, setSearch] = useState('');
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(EMPTY_FORM);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [error, setError] = useState('');

  useEffect(() => { load(); }, []);

  async function load() {
    setLoading(true);
    try {
      const [prodRes, catRes] = await Promise.all([
        luminiApi.get('/inventory/products'),
        luminiApi.get('/inventory/categories'),
      ]);
      const prods = Array.isArray(prodRes.data) ? prodRes.data : [];
      const cats = Array.isArray(catRes.data) ? catRes.data : [];
      setCategories(cats);
      setProducts(prods.map((p: any) => ({
        id: p.id,
        name: p.name,
        description: p.description,
        categoryId: p.categoryId,
        categoryName: cats.find((c: any) => c.id === p.categoryId)?.name ?? '',
        price: Number(p.price ?? 0),
        cost: Number(p.cost ?? 0),
        stock: p.stock ?? p.currentStock ?? 0,
        active: p.active !== false,
      })));
    } catch {
      setProducts([]);
    } finally {
      setLoading(false);
    }
  }

  const filtered = products.filter((p) =>
    p.name.toLowerCase().includes(search.toLowerCase()) ||
    p.categoryName?.toLowerCase().includes(search.toLowerCase()),
  );

  function openCreate() {
    setForm({ ...EMPTY_FORM });
    setEditingId(null);
    setError('');
    setOpen(true);
  }

  function openEdit(p: Product) {
    setForm({
      name: p.name,
      description: p.description || '',
      categoryId: p.categoryId || '',
      price: p.price,
      cost: p.cost || 0,
      active: p.active,
    });
    setEditingId(p.id);
    setError('');
    setOpen(true);
  }

  async function handleSave() {
    if (!form.name.trim()) { setError('El nombre es requerido'); return; }
    setSaving(true);
    setError('');
    try {
      const payload: any = {
        name: form.name,
        description: form.description || undefined,
        price: form.price,
        cost: form.cost,
        active: form.active,
      };
      if (form.categoryId) payload.categoryId = form.categoryId;

      if (editingId) {
        await luminiApi.patch(`/inventory/products/${editingId}`, payload);
      } else {
        await luminiApi.post('/inventory/products', payload);
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
    if (!confirm('¿Eliminar este producto?')) return;
    try {
      await luminiApi.delete(`/inventory/products/${id}`);
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
      <div className="flex gap-3 items-center">
        <div className="relative flex-1 max-w-xs">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-gray-400" />
          <Input
            placeholder="Buscar producto..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-8 h-9 text-sm"
          />
        </div>
        <Button onClick={openCreate} size="sm" className="bg-violet-600 hover:bg-violet-700 text-white">
          <Plus className="w-4 h-4 mr-1" /> Nuevo
        </Button>
      </div>

      <div className="bg-white rounded-xl border border-gray-100 overflow-hidden">
        <Table>
          <TableHeader>
            <TableRow className="bg-gray-50 hover:bg-gray-50">
              <TableHead className="text-xs font-semibold text-gray-500">Producto</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500">Categoría</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-right">Precio</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-center">Stock</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-center">Estado</TableHead>
              <TableHead className="w-16" />
            </TableRow>
          </TableHeader>
          <TableBody>
            {filtered.length === 0 ? (
              <TableRow>
                <TableCell colSpan={6} className="text-center py-12 text-gray-400 text-sm">
                  <Package className="w-8 h-8 mx-auto mb-2 text-gray-200" />
                  No hay productos
                </TableCell>
              </TableRow>
            ) : filtered.map((p) => (
              <TableRow key={p.id} className="hover:bg-gray-50">
                <TableCell>
                  <p className="font-medium text-sm text-gray-800">{p.name}</p>
                  {p.description && <p className="text-xs text-gray-400 truncate max-w-50">{p.description}</p>}
                </TableCell>
                <TableCell className="text-sm text-gray-500">{p.categoryName || '—'}</TableCell>
                <TableCell className="text-sm text-right font-semibold text-gray-800">Bs {p.price.toLocaleString()}</TableCell>
                <TableCell className="text-center">
                  <span className={`text-xs font-bold ${p.stock === 0 ? 'text-red-500' : (p.stock ?? 0) <= 5 ? 'text-amber-500' : 'text-gray-700'}`}>
                    {p.stock ?? '—'}
                  </span>
                </TableCell>
                <TableCell className="text-center">
                  <Badge className={p.active ? 'bg-green-100 text-green-700 hover:bg-green-100' : 'bg-gray-100 text-gray-500 hover:bg-gray-100'}>
                    {p.active ? 'Activo' : 'Inactivo'}
                  </Badge>
                </TableCell>
                <TableCell>
                  <div className="flex items-center gap-1 justify-end">
                    <button onClick={() => openEdit(p)} className="p-1.5 text-gray-400 hover:text-violet-600 rounded-lg hover:bg-violet-50 transition-all">
                      <Pencil className="w-3.5 h-3.5" />
                    </button>
                    <button onClick={() => handleDelete(p.id)} className="p-1.5 text-gray-400 hover:text-red-500 rounded-lg hover:bg-red-50 transition-all">
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>

      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>{editingId ? 'Editar producto' : 'Nuevo producto'}</DialogTitle>
          </DialogHeader>
          <div className="space-y-4 pt-2">
            <div className="space-y-1.5">
              <Label>Nombre</Label>
              <Input value={form.name} onChange={(e) => setForm((p) => ({ ...p, name: e.target.value }))} placeholder="Nombre del producto" />
            </div>
            <div className="space-y-1.5">
              <Label>Descripción</Label>
              <Input value={form.description} onChange={(e) => setForm((p) => ({ ...p, description: e.target.value }))} placeholder="Descripción breve" />
            </div>
            <div className="space-y-1.5">
              <Label>Categoría</Label>
              <Select value={form.categoryId || '_none'} onValueChange={(v) => setForm((p) => ({ ...p, categoryId: v === '_none' ? '' : v }))}>
                <SelectTrigger><SelectValue placeholder="Seleccionar..." /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="_none">Sin categoría</SelectItem>
                  {categories.map((c) => (
                    <SelectItem key={c.id} value={c.id}>{c.name}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1.5">
                <Label>Precio (Bs)</Label>
                <Input type="number" min={0} step="0.01" value={form.price} onChange={(e) => setForm((p) => ({ ...p, price: +e.target.value }))} />
              </div>
              <div className="space-y-1.5">
                <Label>Costo (Bs)</Label>
                <Input type="number" min={0} step="0.01" value={form.cost} onChange={(e) => setForm((p) => ({ ...p, cost: +e.target.value }))} />
              </div>
            </div>
            <div className="space-y-1.5">
              <Label>Estado</Label>
              <Select value={form.active ? 'activo' : 'inactivo'} onValueChange={(v) => setForm((p) => ({ ...p, active: v === 'activo' }))}>
                <SelectTrigger><SelectValue /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="activo">Activo</SelectItem>
                  <SelectItem value="inactivo">Inactivo</SelectItem>
                </SelectContent>
              </Select>
            </div>
            {error && <p className="text-xs text-red-500">{error}</p>}
            <div className="flex gap-2 pt-2">
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
