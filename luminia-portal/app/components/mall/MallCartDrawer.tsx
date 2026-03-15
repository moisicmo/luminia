import { Minus, Plus, ShoppingCart, Trash2, X } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Sheet, SheetContent, SheetHeader, SheetTitle } from '@/components/ui/sheet';
import { useMallCart } from '@/hooks/useMallCart';
import { assetUrl } from '@/lib/assets';

export const MallCartDrawer = () => {
  const { items, open, itemCount, total, hide, increment, decrement, removeFromCart, clear } =
    useMallCart();

  return (
    <Sheet open={open} onOpenChange={(v) => !v && hide()}>
      <SheetContent side="right" className="w-full sm:max-w-md flex flex-col">
        <SheetHeader className="border-b pb-4">
          <SheetTitle className="flex items-center gap-2">
            <ShoppingCart className="w-5 h-5" />
            Carrito ({itemCount})
          </SheetTitle>
        </SheetHeader>

        {items.length === 0 ? (
          <div className="flex-1 flex flex-col items-center justify-center text-gray-400 gap-3">
            <ShoppingCart className="w-12 h-12 text-gray-200" />
            <p className="text-sm">Tu carrito está vacío</p>
          </div>
        ) : (
          <>
            <div className="flex-1 overflow-y-auto py-4 space-y-3">
              {items.map((item) => (
                <div key={item.productId} className="flex gap-3 p-3 rounded-xl bg-gray-50">
                  {/* Image */}
                  <div className="w-16 h-16 rounded-lg bg-white border border-gray-100 overflow-hidden shrink-0 flex items-center justify-center">
                    {item.imageUrl ? (
                      <img src={assetUrl(item.imageUrl)} alt={item.productName} className="w-full h-full object-cover" />
                    ) : (
                      <ShoppingCart className="w-6 h-6 text-gray-200" />
                    )}
                  </div>

                  {/* Info */}
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-gray-800 truncate">{item.productName}</p>
                    <p className="text-xs text-gray-400 truncate">{item.businessName}</p>
                    <p className="text-sm font-bold text-gray-900 mt-1">
                      Bs {(item.salePrice * item.quantity).toLocaleString('es-BO', { minimumFractionDigits: 2 })}
                    </p>
                  </div>

                  {/* Qty controls */}
                  <div className="flex flex-col items-center gap-1 shrink-0">
                    <button
                      onClick={() => removeFromCart(item.productId)}
                      className="text-gray-300 hover:text-red-500 transition-colors"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                    <div className="flex items-center gap-1.5 bg-white rounded-lg border border-gray-200 px-1">
                      <button onClick={() => decrement(item.productId)} className="p-0.5 text-gray-500 hover:text-gray-800">
                        <Minus className="w-3.5 h-3.5" />
                      </button>
                      <span className="text-sm font-medium w-6 text-center">{item.quantity}</span>
                      <button onClick={() => increment(item.productId)} className="p-0.5 text-gray-500 hover:text-gray-800">
                        <Plus className="w-3.5 h-3.5" />
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {/* Footer */}
            <div className="border-t pt-4 space-y-3">
              <div className="flex justify-between items-center">
                <span className="text-sm text-gray-500">Total</span>
                <span className="text-xl font-bold text-gray-900">
                  Bs {total.toLocaleString('es-BO', { minimumFractionDigits: 2 })}
                </span>
              </div>
              <Button className="w-full bg-violet-600 hover:bg-violet-700 text-white rounded-xl h-12">
                Proceder al pago
              </Button>
              <button onClick={clear} className="w-full text-sm text-gray-400 hover:text-gray-600 transition-colors">
                Vaciar carrito
              </button>
            </div>
          </>
        )}
      </SheetContent>
    </Sheet>
  );
};
