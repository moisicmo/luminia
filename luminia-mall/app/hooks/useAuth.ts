import { useDispatch, useSelector } from 'react-redux';
import { useState } from 'react';
import { luminiApi } from '@/services/luminiApi';
import { setAuthenticated, setNotAuthenticated, setChecking, type MallUser } from '@/store';
import type { RootState } from '@/store';

export const useAuth = () => {
  const dispatch = useDispatch();
  const { status, user, token } = useSelector((s: RootState) => s.mallAuth);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const login = async (email: string, password: string): Promise<boolean> => {
    setLoading(true);
    setError(null);
    try {
      const { data } = await luminiApi.post('/auth/login', { email, password });
      const mallUser: MallUser = {
        id: data.sub ?? data.personId ?? data.id,
        name: data.name ?? '',
        lastName: data.lastName ?? '',
        email: data.email ?? email,
      };
      localStorage.setItem('luminia_token', data.token);
      dispatch(setAuthenticated({ user: mallUser, token: data.token }));
      return true;
    } catch (err: any) {
      setError(err?.response?.data?.message ?? 'Credenciales incorrectas');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const register = async (
    name: string,
    lastName: string,
    email: string,
    password: string,
  ): Promise<boolean> => {
    setLoading(true);
    setError(null);
    try {
      await luminiApi.post('/auth/register', { name, lastName, email, password });
      return true;
    } catch (err: any) {
      setError(err?.response?.data?.message ?? 'No se pudo crear la cuenta');
      return false;
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem('luminia_token');
    localStorage.removeItem('luminia_business_id');
    dispatch(setNotAuthenticated());
  };

  const checkAuth = () => {
    const storedToken = localStorage.getItem('luminia_token');
    if (!storedToken) {
      dispatch(setNotAuthenticated());
      return;
    }
    try {
      const payload = JSON.parse(atob(storedToken.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
      if (payload.exp * 1000 < Date.now()) {
        logout();
        return;
      }
      dispatch(setAuthenticated({
        user: { id: payload.sub, name: payload.name ?? '', lastName: payload.lastName ?? '', email: payload.email ?? '' },
        token: storedToken,
      }));
    } catch {
      logout();
    }
  };

  return { status, user, token, loading, error, login, register, logout, checkAuth, clearError: () => setError(null) };
};
