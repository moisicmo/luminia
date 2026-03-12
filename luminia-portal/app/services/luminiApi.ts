import axios from 'axios';

const BASE_URL = import.meta.env.VITE_HOST_BACKEND ?? 'http://localhost:3000';

export const luminiApi = axios.create({ baseURL: `${BASE_URL}/api` });

luminiApi.interceptors.request.use((req) => {
  const token = localStorage.getItem('luminia_token');
  if (token) req.headers.set('Authorization', `Bearer ${token}`);
  const businessId = localStorage.getItem('luminia_business_id');
  if (businessId) req.headers.set('X-Business-Id', businessId);
  return req;
});
