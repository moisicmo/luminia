import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

export interface MallCartItem {
  productId: string;
  productName: string;
  imageUrl?: string;
  salePrice: number;
  quantity: number;
  businessId: string;
  businessName: string;
  businessSlug?: string;
}

interface MallCartState {
  items: MallCartItem[];
  open: boolean;
}

const initialState: MallCartState = {
  items: [],
  open: false,
};

export const mallCartSlice = createSlice({
  name: 'mallCart',
  initialState,
  reducers: {
    addItem: (state, { payload }: PayloadAction<MallCartItem>) => {
      const existing = state.items.find((i) => i.productId === payload.productId);
      if (existing) {
        existing.quantity += payload.quantity;
      } else {
        state.items.push(payload);
      }
    },
    removeItem: (state, { payload }: PayloadAction<string>) => {
      state.items = state.items.filter((i) => i.productId !== payload);
    },
    incrementQty: (state, { payload }: PayloadAction<string>) => {
      const item = state.items.find((i) => i.productId === payload);
      if (item) item.quantity += 1;
    },
    decrementQty: (state, { payload }: PayloadAction<string>) => {
      const item = state.items.find((i) => i.productId === payload);
      if (item) {
        item.quantity -= 1;
        if (item.quantity <= 0) {
          state.items = state.items.filter((i) => i.productId !== payload);
        }
      }
    },
    openCart: (state) => { state.open = true; },
    closeCart: (state) => { state.open = false; },
    clearCart: (state) => { state.items = []; },
  },
});

export const { addItem, removeItem, incrementQty, decrementQty, openCart, closeCart, clearCart } =
  mallCartSlice.actions;
