// Esta ruta existe solo para que React Router no lance 404 en /admin/*.
// El renderizado real lo maneja root.tsx → App → AdminView.
export default function AdminRoute() {
  return null;
}
