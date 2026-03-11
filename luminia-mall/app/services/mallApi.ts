import axios from 'axios';

const mallApi = axios.create({
  baseURL: `${import.meta.env.VITE_HOST_BACKEND ?? 'http://localhost:3000'}/api`,
});

// No auth interceptor — public-facing API
export { mallApi };
