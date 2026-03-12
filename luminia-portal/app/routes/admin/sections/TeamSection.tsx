import { useEffect, useState, useCallback } from 'react';
import { Users, ShieldCheck, Plus, Trash2, Loader2, ChevronDown, ChevronUp, UserMinus } from 'lucide-react';
import { luminiApi } from '@/services/luminiApi';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { cn } from '@/lib/utils';

// ─── Types ────────────────────────────────────────────────────────────────────

interface Member {
  id: string;
  userId: string;
  role: 'OWNER' | 'MEMBER';
  roleId?: string | null;
  businessRole?: { id: string; name: string } | null;
  active: boolean;
}

interface BusinessRole {
  id: string;
  name: string;
  description?: string;
  isSystem: boolean;
  permissions: string[];
  memberCount: number;
}

type Tab = 'members' | 'roles';

// ─── Permission matrix ────────────────────────────────────────────────────────

const PERMISSION_GROUPS: Record<string, string[]> = {
  'Productos':   ['productos:ver', 'productos:crear', 'productos:editar', 'productos:eliminar'],
  'Categorías':  ['categorias:ver', 'categorias:crear', 'categorias:editar', 'categorias:eliminar'],
  'Inventario':  ['inventario:ver', 'inventario:ajustar'],
  'Compras':     ['compras:ver', 'compras:crear', 'compras:aprobar', 'compras:cancelar'],
  'Ventas':      ['ventas:ver', 'ventas:crear', 'ventas:cancelar'],
  'Sucursales':  ['sucursales:ver', 'sucursales:gestionar'],
  'Traspasos':   ['traspasos:ver', 'traspasos:crear'],
  'Kardex':      ['kardex:ver'],
  'Reportes':    ['reportes:ver'],
  'Miembros':    ['miembros:ver', 'miembros:invitar', 'miembros:gestionar'],
  'Roles':       ['roles:ver', 'roles:crear', 'roles:gestionar'],
};

function permLabel(p: string) {
  const action = p.split(':')[1];
  const map: Record<string, string> = { ver: 'Ver', crear: 'Crear', editar: 'Editar', eliminar: 'Eliminar', ajustar: 'Ajustar', aprobar: 'Aprobar', cancelar: 'Cancelar', gestionar: 'Gestionar', invitar: 'Invitar' };
  return map[action] ?? action;
}

// ─── Assign Role Dialog ───────────────────────────────────────────────────────

function AssignRoleDialog({ member, roles, businessId, onClose, onDone }: {
  member: Member;
  roles: BusinessRole[];
  businessId: string;
  onClose: () => void;
  onDone: () => void;
}) {
  const [selectedRoleId, setSelectedRoleId] = useState(member.roleId ?? '');
  const [saving, setSaving] = useState(false);

  const save = async () => {
    if (!selectedRoleId) return;
    setSaving(true);
    try {
      await luminiApi.patch(`/business/${businessId}/members/${member.id}/role`, { roleId: selectedRoleId });
      onDone();
    } finally {
      setSaving(false);
    }
  };

  return (
    <DialogContent className="max-w-sm">
      <DialogHeader>
        <DialogTitle>Asignar rol</DialogTitle>
      </DialogHeader>
      <div className="space-y-2 mt-2">
        {roles.map((r) => (
          <button
            key={r.id}
            onClick={() => setSelectedRoleId(r.id)}
            className={cn(
              'w-full text-left px-3 py-2.5 rounded-lg border text-sm transition-all',
              selectedRoleId === r.id
                ? 'border-violet-400 bg-violet-50 text-violet-700'
                : 'border-gray-200 hover:border-gray-300',
            )}
          >
            <p className="font-medium">{r.name}</p>
            <p className="text-xs text-gray-400">{r.permissions.length} permisos</p>
          </button>
        ))}
      </div>
      <div className="flex gap-2 justify-end mt-4">
        <Button variant="outline" size="sm" onClick={onClose}>Cancelar</Button>
        <Button size="sm" disabled={!selectedRoleId || saving} onClick={save}>
          {saving && <Loader2 className="w-3.5 h-3.5 mr-1.5 animate-spin" />}
          Asignar
        </Button>
      </div>
    </DialogContent>
  );
}

// ─── Create/Edit Role Dialog ──────────────────────────────────────────────────

function RoleDialog({ businessId, role, onClose, onDone }: {
  businessId: string;
  role?: BusinessRole;
  onClose: () => void;
  onDone: () => void;
}) {
  const [name, setName] = useState(role?.name ?? '');
  const [description, setDescription] = useState(role?.description ?? '');
  const [permissions, setPermissions] = useState<string[]>(role?.permissions ?? []);
  const [saving, setSaving] = useState(false);

  const toggle = (p: string) =>
    setPermissions((prev) => prev.includes(p) ? prev.filter((x) => x !== p) : [...prev, p]);

  const toggleGroup = (perms: string[]) => {
    const allOn = perms.every((p) => permissions.includes(p));
    if (allOn) setPermissions((prev) => prev.filter((p) => !perms.includes(p)));
    else setPermissions((prev) => [...new Set([...prev, ...perms])]);
  };

  const save = async () => {
    if (!name.trim()) return;
    setSaving(true);
    try {
      if (role) {
        await luminiApi.patch(`/business/${businessId}/roles/${role.id}`, { name, description, permissions });
      } else {
        await luminiApi.post(`/business/${businessId}/roles`, { name, description, permissions });
      }
      onDone();
    } finally {
      setSaving(false);
    }
  };

  return (
    <DialogContent className="max-w-lg max-h-[85vh] overflow-y-auto">
      <DialogHeader>
        <DialogTitle>{role ? 'Editar rol' : 'Nuevo rol'}</DialogTitle>
      </DialogHeader>
      <div className="space-y-4 mt-2">
        <div className="space-y-1.5">
          <Label>Nombre</Label>
          <Input value={name} onChange={(e) => setName(e.target.value)} placeholder="Ej. Supervisor de ventas" />
        </div>
        <div className="space-y-1.5">
          <Label>Descripción (opcional)</Label>
          <Input value={description} onChange={(e) => setDescription(e.target.value)} placeholder="Breve descripción del rol" />
        </div>

        <div className="space-y-1.5">
          <Label>Permisos</Label>
          <div className="border border-gray-200 rounded-lg divide-y divide-gray-100 overflow-hidden">
            {Object.entries(PERMISSION_GROUPS).map(([group, perms]) => {
              const allOn = perms.every((p) => permissions.includes(p));
              const someOn = perms.some((p) => permissions.includes(p));
              return (
                <div key={group} className="px-3 py-2.5">
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-xs font-semibold text-gray-600">{group}</span>
                    <button
                      onClick={() => toggleGroup(perms)}
                      className={cn('text-[10px] px-2 py-0.5 rounded-full font-medium transition-colors',
                        allOn ? 'bg-violet-100 text-violet-700' : someOn ? 'bg-amber-50 text-amber-600' : 'bg-gray-100 text-gray-500'
                      )}
                    >
                      {allOn ? 'Todos' : someOn ? 'Parcial' : 'Ninguno'}
                    </button>
                  </div>
                  <div className="flex flex-wrap gap-1.5">
                    {perms.map((p) => (
                      <button
                        key={p}
                        onClick={() => toggle(p)}
                        className={cn(
                          'text-[11px] px-2 py-1 rounded-md border transition-all',
                          permissions.includes(p)
                            ? 'bg-violet-600 border-violet-600 text-white'
                            : 'border-gray-200 text-gray-500 hover:border-gray-300',
                        )}
                      >
                        {permLabel(p)}
                      </button>
                    ))}
                  </div>
                </div>
              );
            })}
          </div>
          <p className="text-xs text-gray-400">{permissions.length} permisos seleccionados</p>
        </div>
      </div>
      <div className="flex gap-2 justify-end mt-4">
        <Button variant="outline" size="sm" onClick={onClose}>Cancelar</Button>
        <Button size="sm" disabled={!name.trim() || saving} onClick={save}>
          {saving && <Loader2 className="w-3.5 h-3.5 mr-1.5 animate-spin" />}
          {role ? 'Guardar cambios' : 'Crear rol'}
        </Button>
      </div>
    </DialogContent>
  );
}

// ─── Main ─────────────────────────────────────────────────────────────────────

export function TeamSection() {
  const businessId = localStorage.getItem('luminia_business_id') ?? '';
  const [tab, setTab] = useState<Tab>('members');

  const [members, setMembers] = useState<Member[]>([]);
  const [roles, setRoles] = useState<BusinessRole[]>([]);
  const [loading, setLoading] = useState(true);

  const [assignTarget, setAssignTarget] = useState<Member | null>(null);
  const [roleDialog, setRoleDialog] = useState<{ open: boolean; role?: BusinessRole }>({ open: false });
  const [expandedRole, setExpandedRole] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const [membersRes, rolesRes] = await Promise.all([
        luminiApi.get(`/business/${businessId}/members`),
        luminiApi.get(`/business/${businessId}/roles`),
      ]);
      setMembers(membersRes.data.members ?? membersRes.data ?? []);
      setRoles(rolesRes.data);
    } finally {
      setLoading(false);
    }
  }, [businessId]);

  useEffect(() => { load(); }, [load]);

  const removeMember = async (memberId: string) => {
    if (!confirm('¿Remover este miembro?')) return;
    await luminiApi.delete(`/business/${businessId}/members/${memberId}`);
    load();
  };

  const removeRole = async (roleId: string) => {
    if (!confirm('¿Eliminar este rol?')) return;
    await luminiApi.delete(`/business/${businessId}/roles/${roleId}`);
    load();
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-48">
        <Loader2 className="w-6 h-6 animate-spin text-violet-500" />
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {/* Tabs */}
      <div className="flex gap-1 bg-gray-100 p-1 rounded-xl w-fit">
        {([['members', Users, 'Miembros'], ['roles', ShieldCheck, 'Roles']] as const).map(([id, Icon, label]) => (
          <button
            key={id}
            onClick={() => setTab(id)}
            className={cn(
              'flex items-center gap-1.5 px-4 py-1.5 rounded-lg text-sm font-medium transition-all',
              tab === id ? 'bg-white text-violet-700 shadow-sm' : 'text-gray-500 hover:text-gray-700',
            )}
          >
            <Icon className="w-3.5 h-3.5" />
            {label}
          </button>
        ))}
      </div>

      {/* Members tab */}
      {tab === 'members' && (
        <div className="bg-white rounded-2xl shadow-sm">
          <div className="flex items-center justify-between px-5 py-4 border-b border-gray-100">
            <h2 className="font-semibold text-gray-800">Miembros del equipo</h2>
          </div>
          <div className="divide-y divide-gray-50">
            {members.length === 0 && (
              <p className="text-center text-sm text-gray-400 py-10">No hay miembros aún</p>
            )}
            {members.map((m) => (
              <div key={m.id} className="flex items-center gap-3 px-5 py-3.5">
                <div className="w-9 h-9 rounded-full bg-gradient-to-br from-violet-400 to-indigo-500 flex items-center justify-center text-white text-xs font-bold shrink-0">
                  {m.userId.slice(0, 2).toUpperCase()}
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-800 truncate">{m.userId}</p>
                  <p className="text-xs text-gray-400">
                    {m.role === 'OWNER' ? 'Dueño' : (m.businessRole?.name ?? 'Sin rol asignado')}
                  </p>
                </div>
                {m.role === 'OWNER' ? (
                  <Badge variant="outline" className="text-violet-600 border-violet-200 bg-violet-50">Dueño</Badge>
                ) : (
                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => setAssignTarget(m)}
                      className="text-xs text-gray-400 hover:text-violet-600 transition-colors px-2 py-1 rounded-md hover:bg-violet-50"
                    >
                      {m.businessRole?.name ?? 'Asignar rol'}
                    </button>
                    <button onClick={() => removeMember(m.id)} className="text-gray-300 hover:text-red-500 transition-colors">
                      <UserMinus className="w-4 h-4" />
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Roles tab */}
      {tab === 'roles' && (
        <div className="bg-white rounded-2xl shadow-sm">
          <div className="flex items-center justify-between px-5 py-4 border-b border-gray-100">
            <h2 className="font-semibold text-gray-800">Roles y permisos</h2>
            <Button size="sm" onClick={() => setRoleDialog({ open: true })}>
              <Plus className="w-3.5 h-3.5 mr-1.5" />
              Nuevo rol
            </Button>
          </div>
          <div className="divide-y divide-gray-50">
            {roles.map((r) => (
              <div key={r.id}>
                <div
                  className="flex items-center gap-3 px-5 py-3.5 cursor-pointer hover:bg-gray-50 transition-colors"
                  onClick={() => setExpandedRole(expandedRole === r.id ? null : r.id)}
                >
                  <div className="w-8 h-8 rounded-lg bg-violet-50 flex items-center justify-center shrink-0">
                    <ShieldCheck className="w-4 h-4 text-violet-500" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2">
                      <p className="text-sm font-medium text-gray-800">{r.name}</p>
                      {r.isSystem && <Badge variant="outline" className="text-[10px] py-0">Sistema</Badge>}
                    </div>
                    <p className="text-xs text-gray-400">{r.permissions.length} permisos · {r.memberCount} miembro{r.memberCount !== 1 ? 's' : ''}</p>
                  </div>
                  <div className="flex items-center gap-1">
                    {!r.isSystem && (
                      <>
                        <button
                          onClick={(e) => { e.stopPropagation(); setRoleDialog({ open: true, role: r }); }}
                          className="text-xs text-gray-400 hover:text-violet-600 px-2 py-1 rounded hover:bg-violet-50 transition-colors"
                        >
                          Editar
                        </button>
                        <button
                          onClick={(e) => { e.stopPropagation(); removeRole(r.id); }}
                          className="text-gray-300 hover:text-red-500 transition-colors p-1"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                        </button>
                      </>
                    )}
                    {expandedRole === r.id
                      ? <ChevronUp className="w-4 h-4 text-gray-300 ml-1" />
                      : <ChevronDown className="w-4 h-4 text-gray-300 ml-1" />
                    }
                  </div>
                </div>

                {expandedRole === r.id && (
                  <div className="px-5 pb-4 bg-gray-50 border-t border-gray-100">
                    <div className="pt-3 space-y-2">
                      {Object.entries(PERMISSION_GROUPS).map(([group, perms]) => {
                        const active = perms.filter((p) => r.permissions.includes(p));
                        if (active.length === 0) return null;
                        return (
                          <div key={group} className="flex items-start gap-3">
                            <span className="text-[11px] font-semibold text-gray-500 w-24 shrink-0 pt-0.5">{group}</span>
                            <div className="flex flex-wrap gap-1">
                              {active.map((p) => (
                                <span key={p} className="text-[11px] bg-violet-100 text-violet-700 px-2 py-0.5 rounded-full">
                                  {permLabel(p)}
                                </span>
                              ))}
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Assign role dialog */}
      <Dialog open={!!assignTarget} onOpenChange={() => setAssignTarget(null)}>
        {assignTarget && (
          <AssignRoleDialog
            member={assignTarget}
            roles={roles}
            businessId={businessId}
            onClose={() => setAssignTarget(null)}
            onDone={() => { setAssignTarget(null); load(); }}
          />
        )}
      </Dialog>

      {/* Create/edit role dialog */}
      <Dialog open={roleDialog.open} onOpenChange={() => setRoleDialog({ open: false })}>
        {roleDialog.open && (
          <RoleDialog
            businessId={businessId}
            role={roleDialog.role}
            onClose={() => setRoleDialog({ open: false })}
            onDone={() => { setRoleDialog({ open: false }); load(); }}
          />
        )}
      </Dialog>
    </div>
  );
}
