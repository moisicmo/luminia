import { ShoppingBag, Plus, Store } from 'lucide-react';
import type { MallProduct } from '@/store/mall/mallSlice';
import { useMallCart } from '@/hooks/useMallCart';
import { assetUrl } from '@/lib/assets';

interface Props {
  product: MallProduct;
}

export const MallProductCard = ({ product }: Props) => {
  const { addToCart } = useMallCart();

  const handleAdd = () => {
    addToCart({
      productId: product.id,
      productName: product.name,
      imageUrl: product.imageUrl,
      salePrice: Number(product.salePrice),
      quantity: 1,
      businessId: product.businessId,
      businessName: product.businessName,
      businessSlug: product.businessSlug,
    });
  };

  return (
    <div className="group bg-white rounded-2xl border border-gray-100 overflow-hidden hover:shadow-lg transition-all duration-300">
      {/* Image */}
      <div className="aspect-square bg-gray-50 relative overflow-hidden">
        {product.imageUrl ? (
          <img
            src={assetUrl(product.imageUrl)}
            alt={product.name}
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center">
            <ShoppingBag className="w-10 h-10 text-gray-200" />
          </div>
        )}
        {/* Add button overlay */}
        <button
          onClick={handleAdd}
          className="absolute bottom-2 right-2 w-9 h-9 rounded-full bg-violet-600 hover:bg-violet-700 text-white
                     flex items-center justify-center shadow-lg opacity-0 group-hover:opacity-100 transition-all
                     translate-y-2 group-hover:translate-y-0"
        >
          <Plus className="w-5 h-5" />
        </button>
      </div>

      {/* Info */}
      <div className="p-3">
        {/* Business badge */}
        <div className="flex items-center gap-1.5 mb-1.5">
          {product.businessLogo ? (
            <img src={product.businessLogo} alt="" className="w-4 h-4 rounded-full object-cover" />
          ) : (
            <Store className="w-3.5 h-3.5 text-gray-400" />
          )}
          <span className="text-xs text-gray-400 truncate">{product.businessName}</span>
        </div>

        <p className="text-sm font-medium text-gray-800 truncate group-hover:text-violet-600 transition-colors">
          {product.name}
        </p>

        {product.category && (
          <span className="text-xs text-gray-400">{product.category.name}</span>
        )}

        <p className="text-base font-bold text-gray-900 mt-1">
          Bs {Number(product.salePrice).toLocaleString('es-BO', { minimumFractionDigits: 2 })}
        </p>
      </div>
    </div>
  );
};
