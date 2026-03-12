/**
 * Todos los permisos disponibles en el sistema, agrupados por módulo.
 * Formato: "modulo:accion"
 */
export const PERMISSION_GROUPS = {
  productos:  ['ver', 'crear', 'editar', 'eliminar'],
  categorias: ['ver', 'crear', 'editar', 'eliminar'],
  inventario: ['ver', 'ajustar'],
  compras:    ['ver', 'crear', 'aprobar', 'cancelar'],
  ventas:     ['ver', 'crear', 'cancelar'],
  sucursales: ['ver', 'gestionar'],
  traspasos:  ['ver', 'crear'],
  kardex:     ['ver'],
  reportes:   ['ver'],
  miembros:   ['ver', 'invitar', 'gestionar'],
  roles:      ['ver', 'crear', 'gestionar'],
} as const;

export type PermissionModule = keyof typeof PERMISSION_GROUPS;

export const ALL_PERMISSIONS: string[] = Object.entries(PERMISSION_GROUPS).flatMap(
  ([mod, actions]) => actions.map((a) => `${mod}:${a}`),
);

// Default roles created automatically when a business is registered
export const DEFAULT_ROLES = [
  {
    name: 'Administrador',
    description: 'Acceso completo al panel excepto cambiar al dueño',
    isSystem: true,
    permissions: ALL_PERMISSIONS,
  },
  {
    name: 'Cajero',
    description: 'Puede crear ventas y ver inventario',
    isSystem: true,
    permissions: [
      'ventas:ver', 'ventas:crear',
      'productos:ver', 'categorias:ver',
      'inventario:ver',
    ],
  },
  {
    name: 'Vendedor',
    description: 'Puede registrar ventas y consultar productos',
    isSystem: true,
    permissions: ['ventas:ver', 'ventas:crear', 'productos:ver', 'categorias:ver'],
  },
];
