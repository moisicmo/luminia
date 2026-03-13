import { reactRouter } from "@react-router/dev/vite";
import tailwindcss from "@tailwindcss/vite";
import { defineConfig, loadEnv } from "vite";
import tsconfigPaths from "vite-tsconfig-paths";

// Cargar las variables de entorno desde .env
const env = loadEnv('', process.cwd());

// Obtener el valor de la variable de entorno para el host y el puerto
const { VITE_HOST, VITE_PORT } = env;

export default defineConfig({
  base: '/', // importante para que .well-known esté en raíz
  plugins: [tailwindcss(), reactRouter(), tsconfigPaths()],
    server: {
    host: VITE_HOST || 'localhost',
    port: Number(VITE_PORT) || 3000,
    allowedHosts: ['.lvh.me', '.luminia.local', '.luminia.com'],
  },
});
