import { useState } from 'react';
import { Plus, ArrowRight, ArrowLeftRight } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Badge } from '@/components/ui/badge';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';

// TODO: Conectar a endpoint de traspasos cuando esté disponible en el gateway
// Por ahora la UI está lista para recibir datos reales

const STATUS_CLS: Record<string, string> = {
  COMPLETADO: 'bg-green-100 text-green-700',
  PENDIENTE:  'bg-amber-100 text-amber-700',
  CANCELADO:  'bg-red-100 text-red-700',
};

const EMPTY = { from: '', to: '', product: '', qty: 1 };

export function TransfersSection() {
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState(EMPTY);

  return (
    <div className="space-y-4">
      <div className="flex justify-end">
        <Button onClick={() => setOpen(true)} size="sm" className="bg-violet-600 hover:bg-violet-700 text-white">
          <Plus className="w-4 h-4 mr-1" /> Nuevo traspaso
        </Button>
      </div>

      <div className="bg-white rounded-xl border border-gray-100 overflow-hidden">
        <Table>
          <TableHeader>
            <TableRow className="bg-gray-50 hover:bg-gray-50">
              <TableHead className="text-xs font-semibold text-gray-500">ID</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500">Origen → Destino</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500">Producto</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-center">Cant.</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500">Fecha</TableHead>
              <TableHead className="text-xs font-semibold text-gray-500 text-center">Estado</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow>
              <TableCell colSpan={6} className="text-center py-12 text-gray-400 text-sm">
                <ArrowLeftRight className="w-8 h-8 mx-auto mb-2 text-gray-200" />
                Sin traspasos registrados
                <p className="text-xs mt-1 text-gray-300">Los traspasos entre sucursales aparecerán aquí</p>
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </div>

      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent className="max-w-sm">
          <DialogHeader><DialogTitle>Nuevo traspaso</DialogTitle></DialogHeader>
          <div className="space-y-4 pt-2">
            <div className="space-y-1.5">
              <Label>Sucursal origen</Label>
              <Select value={form.from} onValueChange={(v) => setForm((p) => ({ ...p, from: v }))}>
                <SelectTrigger><SelectValue placeholder="Selecciona..." /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="principal">Principal</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-1.5">
              <Label>Sucursal destino</Label>
              <Select value={form.to} onValueChange={(v) => setForm((p) => ({ ...p, to: v }))}>
                <SelectTrigger><SelectValue placeholder="Selecciona..." /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="principal">Principal</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-1.5">
              <Label>Producto</Label>
              <Input value={form.product} onChange={(e) => setForm((p) => ({ ...p, product: e.target.value }))} placeholder="Nombre del producto" />
            </div>
            <div className="space-y-1.5">
              <Label>Cantidad</Label>
              <Input type="number" min={1} value={form.qty} onChange={(e) => setForm((p) => ({ ...p, qty: +e.target.value }))} />
            </div>
            <div className="flex gap-2 pt-1">
              <Button variant="outline" className="flex-1" onClick={() => setOpen(false)}>Cancelar</Button>
              <Button className="flex-1 bg-violet-600 hover:bg-violet-700 text-white" onClick={() => { setOpen(false); setForm(EMPTY); }}>
                Confirmar
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
