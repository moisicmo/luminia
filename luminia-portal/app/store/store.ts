import { configureStore } from '@reduxjs/toolkit';
import { authSlice, cartSlice, mallSlice, mallAuthSlice } from '.';

export const store = configureStore({
  reducer: {
    auth: authSlice.reducer,
    carts: cartSlice.reducer,
    mall: mallSlice.reducer,
    mallAuth: mallAuthSlice.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({ serializableCheck: false }),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
