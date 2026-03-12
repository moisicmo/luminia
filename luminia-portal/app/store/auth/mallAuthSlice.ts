import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

export interface MallUser {
  id: string;
  name: string;
  lastName: string;
  email: string;
}

interface MallAuthState {
  status: 'checking' | 'authenticated' | 'not-authenticated';
  user: MallUser | null;
  token: string | null;
}

const initialState: MallAuthState = {
  status: 'checking',
  user: null,
  token: null,
};

export const mallAuthSlice = createSlice({
  name: 'mallAuth',
  initialState,
  reducers: {
    setAuthenticated: (state, { payload }: PayloadAction<{ user: MallUser; token: string }>) => {
      state.status = 'authenticated';
      state.user = payload.user;
      state.token = payload.token;
    },
    setNotAuthenticated: (state) => {
      state.status = 'not-authenticated';
      state.user = null;
      state.token = null;
    },
    setChecking: (state) => {
      state.status = 'checking';
    },
  },
});

export const { setAuthenticated, setNotAuthenticated, setChecking } = mallAuthSlice.actions;
