import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

export interface SalesCartItem {
  productId: string;
  productName: string;
  sku: string;
  unitId: string;
  unitName: string;
  quantity: number;
  unitCost: number;
  salePrice: number;
  siatActivityCode?: number;
  siatProductServiceCode?: number;
  siatMeasurementUnitId?: number;
}

export type DocumentType = 'recibo' | 'factura';

interface SalesCartState {
  items: SalesCartItem[];
  customerId: string;
  customerLabel: string;
  documentNumber: string;
  notes: string;
  open: boolean;
  documentType: DocumentType;
  buyerNit: string;
}

const initialState: SalesCartState = {
  items: [],
  customerId: '',
  customerLabel: '',
  documentNumber: '',
  notes: '',
  open: false,
  documentType: 'recibo',
  buyerNit: '',
};

export const salesCartSlice = createSlice({
  name: 'salesCart',
  initialState,
  reducers: {
    toggleCart: (state) => {
      state.open = !state.open;
    },
    openCart: (state) => {
      state.open = true;
    },
    closeCart: (state) => {
      state.open = false;
    },
    addItem: (state, action: PayloadAction<SalesCartItem>) => {
      const existing = state.items.find(
        (i) => i.productId === action.payload.productId && i.unitId === action.payload.unitId,
      );
      if (existing) {
        existing.quantity += action.payload.quantity;
      } else {
        state.items.push({ ...action.payload });
      }
    },
    removeItem: (state, action: PayloadAction<{ productId: string; unitId: string }>) => {
      state.items = state.items.filter(
        (i) => !(i.productId === action.payload.productId && i.unitId === action.payload.unitId),
      );
    },
    updateQuantity: (
      state,
      action: PayloadAction<{ productId: string; unitId: string; quantity: number }>,
    ) => {
      const item = state.items.find(
        (i) => i.productId === action.payload.productId && i.unitId === action.payload.unitId,
      );
      if (item) {
        item.quantity = Math.max(1, action.payload.quantity);
      }
    },
    incrementQty: (state, action: PayloadAction<{ productId: string; unitId: string }>) => {
      const item = state.items.find(
        (i) => i.productId === action.payload.productId && i.unitId === action.payload.unitId,
      );
      if (item) item.quantity += 1;
    },
    decrementQty: (state, action: PayloadAction<{ productId: string; unitId: string }>) => {
      const item = state.items.find(
        (i) => i.productId === action.payload.productId && i.unitId === action.payload.unitId,
      );
      if (item && item.quantity > 1) item.quantity -= 1;
    },
    setCustomer: (state, action: PayloadAction<{ id: string; label: string }>) => {
      state.customerId = action.payload.id;
      state.customerLabel = action.payload.label;
    },
    setDocumentNumber: (state, action: PayloadAction<string>) => {
      state.documentNumber = action.payload;
    },
    setNotes: (state, action: PayloadAction<string>) => {
      state.notes = action.payload;
    },
    setDocumentType: (state, action: PayloadAction<DocumentType>) => {
      state.documentType = action.payload;
    },
    setBuyerNit: (state, action: PayloadAction<string>) => {
      state.buyerNit = action.payload;
    },
    clearCart: () => initialState,
  },
});

export const {
  toggleCart,
  openCart,
  closeCart,
  addItem,
  removeItem,
  updateQuantity,
  incrementQty,
  decrementQty,
  setCustomer,
  setDocumentNumber,
  setNotes,
  setDocumentType,
  setBuyerNit,
  clearCart,
} = salesCartSlice.actions;
