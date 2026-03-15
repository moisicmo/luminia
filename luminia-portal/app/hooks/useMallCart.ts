import { useDispatch, useSelector } from 'react-redux';
import type { RootState } from '@/store';
import {
  addItem,
  removeItem,
  incrementQty,
  decrementQty,
  openCart,
  closeCart,
  clearCart,
  type MallCartItem,
} from '@/store/mallCart/mallCartSlice';

export const useMallCart = () => {
  const dispatch = useDispatch();
  const { items, open } = useSelector((s: RootState) => s.mallCart);

  const itemCount = items.reduce((sum, i) => sum + i.quantity, 0);
  const total = items.reduce((sum, i) => sum + i.salePrice * i.quantity, 0);

  return {
    items,
    open,
    itemCount,
    total,
    addToCart: (item: MallCartItem) => dispatch(addItem(item)),
    removeFromCart: (productId: string) => dispatch(removeItem(productId)),
    increment: (productId: string) => dispatch(incrementQty(productId)),
    decrement: (productId: string) => dispatch(decrementQty(productId)),
    toggle: () => dispatch(open ? closeCart() : openCart()),
    show: () => dispatch(openCart()),
    hide: () => dispatch(closeCart()),
    clear: () => dispatch(clearCart()),
  };
};
