import { useDispatch } from 'react-redux';
import { Minus, Plus, Trash2, ShoppingCart, Loader2, Receipt, FileText } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Sheet, SheetContent, SheetHeader, SheetTitle, SheetFooter } from '@/components/ui/sheet';
import { useAppSelector } from '@/hooks/useStore';
import type { RootState } from '@/store';
import {
  closeCart,
  removeItem,
  incrementQty,
  decrementQty,
  setCustomer,
  setDocumentNumber,
  setNotes,
  setDocumentType,
  setBuyerNit,
  clearCart,
  type SalesCartItem,
  type DocumentType,
} from '@/store/salesCart/salesCartSlice';

interface CustomerOption {
  id: string;
  name: string;
  lastName?: string;
}

interface Props {
  customers: CustomerOption[];
  saving: boolean;
  onCheckout: () => void;
}

export function SalesCartDrawer({ customers, saving, onCheckout }: Props) {
  const dispatch = useDispatch();
  const { items, open, customerId, documentNumber, notes, documentType, buyerNit } = useAppSelector(
    (state: RootState) => state.salesCart,
  );

  const total = items.reduce((s, i) => s + i.quantity * i.salePrice, 0);
  const itemCount = items.reduce((s, i) => s + i.quantity, 0);

  return (
    <Sheet open={open} onOpenChange={(v) => { if (!v) dispatch(closeCart()); }}>
      <SheetContent side="right" className="p-0 w-full sm:max-w-md flex flex-col">
        <SheetHeader className="border-b border-gray-100 px-4 py-3">
          <SheetTitle className="flex items-center gap-2 text-sm">
            <ShoppingCart className="w-4 h-4 text-violet-600" />
            Carrito de venta
            {itemCount > 0 && (
              <span className="ml-auto bg-violet-100 text-violet-700 text-xs font-bold px-2 py-0.5 rounded-full">
                {itemCount}
              </span>
            )}
          </SheetTitle>
        </SheetHeader>

        {items.length === 0 ? (
          <div className="flex-1 flex flex-col items-center justify-center text-gray-400">
            <ShoppingCart className="w-12 h-12 mb-3 text-gray-200" />
            <p className="text-sm">El carrito esta vacio</p>
            <p className="text-xs mt-1">Agrega productos desde la lista</p>
          </div>
        ) : (
          <>
            {/* Cart items */}
            <ScrollArea className="flex-1 px-4 py-3">
              <div className="space-y-3">
                {items.map((item) => (
                  <CartItemRow key={`${item.productId}-${item.unitId}`} item={item} />
                ))}
              </div>
            </ScrollArea>

            {/* Cart form */}
            <div className="border-t border-gray-100 px-4 py-3 space-y-3 bg-gray-50">
              {/* Document type toggle */}
              <div>
                <Label className="text-xs mb-1.5">Tipo de documento</Label>
                <div className="flex bg-gray-100 rounded-lg p-0.5">
                  <button
                    onClick={() => dispatch(setDocumentType('recibo'))}
                    className={`flex-1 flex items-center justify-center gap-1.5 px-3 py-1.5 text-xs font-medium rounded-md transition ${
                      documentType === 'recibo'
                        ? 'bg-white text-violet-700 shadow-sm'
                        : 'text-gray-500 hover:text-gray-700'
                    }`}
                  >
                    <Receipt className="w-3.5 h-3.5" />
                    Recibo
                  </button>
                  <button
                    onClick={() => dispatch(setDocumentType('factura'))}
                    className={`flex-1 flex items-center justify-center gap-1.5 px-3 py-1.5 text-xs font-medium rounded-md transition ${
                      documentType === 'factura'
                        ? 'bg-white text-emerald-700 shadow-sm'
                        : 'text-gray-500 hover:text-gray-700'
                    }`}
                  >
                    <FileText className="w-3.5 h-3.5" />
                    Factura
                  </button>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <Label className="text-xs mb-1">Cliente</Label>
                  <select
                    value={customerId}
                    onChange={(e) => {
                      const c = customers.find((c) => c.id === e.target.value);
                      dispatch(setCustomer({
                        id: e.target.value,
                        label: c ? `${c.name} ${c.lastName ?? ''}`.trim() : '',
                      }));
                    }}
                    className="w-full h-8 text-xs border border-gray-200 rounded-md px-2 bg-white focus:outline-none focus:ring-1 focus:ring-violet-500"
                  >
                    <option value="">Consumidor final</option>
                    {customers.map((c) => (
                      <option key={c.id} value={c.id}>
                        {c.name} {c.lastName ?? ''}
                      </option>
                    ))}
                  </select>
                </div>
                <div>
                  <Label className="text-xs mb-1">N. Comprobante</Label>
                  <Input
                    value={documentNumber}
                    onChange={(e) => dispatch(setDocumentNumber(e.target.value))}
                    placeholder="NV-001"
                    className="h-8 text-xs"
                  />
                </div>
              </div>

              {/* NIT field for factura */}
              {documentType === 'factura' && (
                <div>
                  <Label className="text-xs mb-1">NIT del comprador</Label>
                  <Input
                    value={buyerNit}
                    onChange={(e) => dispatch(setBuyerNit(e.target.value))}
                    placeholder="Ej: 1234567013"
                    className="h-8 text-xs"
                  />
                </div>
              )}

              <div>
                <Label className="text-xs mb-1">Notas</Label>
                <Input
                  value={notes}
                  onChange={(e) => dispatch(setNotes(e.target.value))}
                  placeholder="Notas opcionales..."
                  className="h-8 text-xs"
                />
              </div>
            </div>
          </>
        )}

        {/* Footer */}
        <SheetFooter className="border-t border-gray-100 px-4 py-3">
          <div className="w-full space-y-3">
            <div className="flex items-center justify-between">
              <span className="text-sm font-medium text-gray-600">Total</span>
              <span className="text-lg font-bold text-gray-900">Bs {total.toFixed(2)}</span>
            </div>
            <div className="flex gap-2">
              {items.length > 0 && (
                <Button
                  variant="outline"
                  size="sm"
                  className="text-red-500 hover:text-red-700 hover:bg-red-50"
                  onClick={() => dispatch(clearCart())}
                >
                  <Trash2 className="w-3.5 h-3.5 mr-1" />
                  Vaciar
                </Button>
              )}
              <Button
                size="sm"
                className={`flex-1 text-white ${
                  documentType === 'factura'
                    ? 'bg-emerald-600 hover:bg-emerald-700'
                    : 'bg-violet-600 hover:bg-violet-700'
                }`}
                disabled={items.length === 0 || saving || (documentType === 'factura' && !buyerNit)}
                onClick={onCheckout}
              >
                {saving && <Loader2 className="w-3 h-3 animate-spin mr-1" />}
                {documentType === 'factura' ? 'Emitir factura' : 'Registrar venta'}
              </Button>
            </div>
          </div>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}

function CartItemRow({ item }: { item: SalesCartItem }) {
  const dispatch = useDispatch();
  const key = { productId: item.productId, unitId: item.unitId };

  return (
    <div className="flex items-start gap-3 bg-white rounded-lg border border-gray-100 p-3">
      <div className="flex-1 min-w-0">
        <p className="text-xs font-medium text-gray-800 truncate">{item.productName}</p>
        <p className="text-[10px] text-gray-400 mt-0.5">
          {item.sku} · {item.unitName} · Bs {item.salePrice.toFixed(2)} c/u
        </p>
        <p className="text-xs font-semibold text-violet-700 mt-1">
          Bs {(item.quantity * item.salePrice).toFixed(2)}
        </p>
      </div>
      <div className="flex items-center gap-1 shrink-0">
        <button
          onClick={() => dispatch(decrementQty(key))}
          className="w-6 h-6 rounded-md bg-gray-100 hover:bg-gray-200 flex items-center justify-center text-gray-600 transition"
        >
          <Minus className="w-3 h-3" />
        </button>
        <span className="w-8 text-center text-xs font-semibold text-gray-800">
          {item.quantity}
        </span>
        <button
          onClick={() => dispatch(incrementQty(key))}
          className="w-6 h-6 rounded-md bg-violet-100 hover:bg-violet-200 flex items-center justify-center text-violet-700 transition"
        >
          <Plus className="w-3 h-3" />
        </button>
        <button
          onClick={() => dispatch(removeItem(key))}
          className="w-6 h-6 rounded-md hover:bg-red-50 flex items-center justify-center text-gray-300 hover:text-red-500 transition ml-1"
        >
          <Trash2 className="w-3 h-3" />
        </button>
      </div>
    </div>
  );
}
