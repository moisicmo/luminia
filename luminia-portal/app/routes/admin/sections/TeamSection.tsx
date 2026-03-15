import { useEffect, useState, useCallback } from 'react';
import {
  Users, ShieldCheck, Plus, Trash2, Loader2, ChevronDown, ChevronUp,
  UserMinus, Mail, Phone, Send, X, MapPin, Building2,
} from 'lucide-react';
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
  branchIds?: string[];
  pointOfSaleId?: string | null;
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

interface Branch {
  id: string;
  name: string;
}

interface PointOfSale {
  id: string;
  name: string;
}

interface Invitation {
  id: string;
  email?: string;
  phone?: string;
  status: 'PENDING' | 'ACCEPTED' | 'EXPIRED' | 'CANCELLED';
  businessRole?: { id: string; name: string } | null;
  branchIds: string[];
  expiresAt: string;
  createdAt: string;
}

type Tab = 'members' | 'roles' | 'invitations';

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

// ─── Assign Role & Branches Dialog ───────────────────────────────────────────

function AssignmentDialog({ member, roles, branches, businessId, onClose, onDone }: {
  member: Member;
  roles: BusinessRole[];
  branches: Branch[];
  businessId: string;
  onClose: () => void;
  onDone: () => void;
}) {
  const [selectedRoleId, setSelectedRoleId] = useState(member.roleId ?? '');
  const [selectedBranches, setSelectedBranches] = useState<string[]>(member.branchIds ?? []);
  const [selectedPOS, setSelectedPOS] = useState(member.pointOfSaleId ?? '');
  const [posList, setPosList] = useState<PointOfSale[]>([]);
  const [saving, setSaving] = useState(false);

  // Load POS for selected branches (if only one branch selected)
  useEffect(() => {
    if (selectedBranches.length === 1) {
      luminiApi.get(`/business/${businessId}/branches/${selectedBranches[0]}/pos`)
        .then(({ data }) => setPosList(Array.isArray(data) ? data : []))
        .catch(() => setPosList([]));
    } else {
      setPosList([]);
      setSelectedPOS('');
    }
  }, [selectedBranches, businessId]);

  const toggleBranch = (id: string) => {
    setSelectedBranches((prev) =>
      prev.includes(id) ? prev.filter((b) => b !== id) : [...prev, id]
    );
  };

  const save = async () => {
    setSaving(true);
    try {
      await luminiApi.patch(`/business/${businessId}/members/${member.id}/assignment`, {
        roleId: selectedRoleId || undefined,
        branchIds: selectedBranches,
        pointOfSaleId: selectedPOS || null,
      });
      onDone();
    } finally {
      setSaving(false);
    }
  };

  return (
    <DialogContent className="max-w-md max-h-[85vh] overflow-y-auto">
      <DialogHeader>
        <DialogTitle>Configurar miembro</DialogTitle>
      </DialogHeader>
      <div className="space-y-4 mt-2">
        {/* Role selection */}
        <div className="space-y-1.5">
          <Label>Rol</Label>
          <div className="space-y-1.5">
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
        </div>

        {/* Branch selection */}
        {branches.length > 0 && (
          <div className="space-y-1.5">
            <Label>Sucursales asignadas</Label>
            <p className="text-xs text-gray-400">Sin seleccionar = acceso a todas</p>
            <div className="space-y-1">
              {branches.map((b) => (
                <label
                  key={b.id}
                  className={cn(
                    'flex items-center gap-2.5 px-3 py-2 rounded-lg border cursor-pointer transition-all text-sm',
                    selectedBranches.includes(b.id)
                      ? 'border-violet-400 bg-violet-50'
                      : 'border-gray-200 hover:border-gray-300',
                  )}
                >
                  <input
                    type="checkbox"
                    checked={selectedBranches.includes(b.id)}
                    onChange={() => toggleBranch(b.id)}
                    className="accent-violet-600"
                  />
                  <Building2 className="w-3.5 h-3.5 text-gray-400" />
                  {b.name}
                </label>
              ))}
            </div>
          </div>
        )}

        {/* POS selection (only if one branch) */}
        {posList.length > 0 && (
          <div className="space-y-1.5">
            <Label>Punto de venta</Label>
            <select
              value={selectedPOS}
              onChange={(e) => setSelectedPOS(e.target.value)}
              className="w-full text-sm bg-white border border-gray-200 rounded-lg px-3 py-2 focus:outline-none focus:ring-1 focus:ring-violet-500"
            >
              <option value="">Sin asignar</option>
              {posList.map((pos) => (
                <option key={pos.id} value={pos.id}>{pos.name}</option>
              ))}
            </select>
          </div>
        )}
      </div>
      <div className="flex gap-2 justify-end mt-4">
        <Button variant="outline" size="sm" onClick={onClose}>Cancelar</Button>
        <Button size="sm" disabled={!selectedRoleId || saving} onClick={save}>
          {saving && <Loader2 className="w-3.5 h-3.5 mr-1.5 animate-spin" />}
          Guardar
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

// ─── Invite Dialog ───────────────────────────────────────────────────────────

function InviteDialog({ businessId, roles, branches, onClose, onDone }: {
  businessId: string;
  roles: BusinessRole[];
  branches: Branch[];
  onClose: () => void;
  onDone: () => void;
}) {
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [roleId, setRoleId] = useState('');
  const [selectedBranches, setSelectedBranches] = useState<string[]>([]);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const toggleBranch = (id: string) => {
    setSelectedBranches((prev) =>
      prev.includes(id) ? prev.filter((b) => b !== id) : [...prev, id]
    );
  };

  const save = async () => {
    if (!email && !phone) { setError('Indica email o teléfono'); return; }
    if (!roleId) { setError('Selecciona un rol'); return; }
    setSaving(true);
    setError('');
    try {
      await luminiApi.post(`/business/${businessId}/invitations`, {
        email: email || undefined,
        phone: phone || undefined,
        roleId,
        branchIds: selectedBranches,
      });
      onDone();
    } catch (e: any) {
      setError(e.response?.data?.message || 'Error al enviar invitación');
    } finally {
      setSaving(false);
    }
  };

  return (
    <DialogContent className="max-w-md max-h-[85vh] overflow-y-auto">
      <DialogHeader>
        <DialogTitle>Invitar miembro</DialogTitle>
      </DialogHeader>
      <div className="space-y-4 mt-2">
        <div className="space-y-1.5">
          <Label>Email</Label>
          <Input value={email} onChange={(e) => setEmail(e.target.value)} placeholder="correo@ejemplo.com" />
        </div>
        <div className="space-y-1.5">
          <Label>Teléfono (alternativo)</Label>
          <Input value={phone} onChange={(e) => setPhone(e.target.value)} placeholder="+591 7XXXXXXX" />
        </div>

        <div className="space-y-1.5">
          <Label>Rol *</Label>
          <div className="space-y-1.5">
            {roles.map((r) => (
              <button
                key={r.id}
                onClick={() => setRoleId(r.id)}
                className={cn(
                  'w-full text-left px-3 py-2 rounded-lg border text-sm transition-all',
                  roleId === r.id
                    ? 'border-violet-400 bg-violet-50 text-violet-700'
                    : 'border-gray-200 hover:border-gray-300',
                )}
              >
                <p className="font-medium">{r.name}</p>
                <p className="text-xs text-gray-400">{r.permissions.length} permisos</p>
              </button>
            ))}
          </div>
        </div>

        {branches.length > 0 && (
          <div className="space-y-1.5">
            <Label>Sucursales</Label>
            <p className="text-xs text-gray-400">Sin seleccionar = acceso a todas</p>
            <div className="space-y-1">
              {branches.map((b) => (
                <label
                  key={b.id}
                  className={cn(
                    'flex items-center gap-2.5 px-3 py-2 rounded-lg border cursor-pointer transition-all text-sm',
                    selectedBranches.includes(b.id)
                      ? 'border-violet-400 bg-violet-50'
                      : 'border-gray-200 hover:border-gray-300',
                  )}
                >
                  <input
                    type="checkbox"
                    checked={selectedBranches.includes(b.id)}
                    onChange={() => toggleBranch(b.id)}
                    className="accent-violet-600"
                  />
                  <Building2 className="w-3.5 h-3.5 text-gray-400" />
                  {b.name}
                </label>
              ))}
            </div>
          </div>
        )}

        {error && <p className="text-xs text-red-500">{error}</p>}
      </div>
      <div className="flex gap-2 justify-end mt-4">
        <Button variant="outline" size="sm" onClick={onClose}>Cancelar</Button>
        <Button size="sm" disabled={saving} onClick={save}>
          {saving ? <Loader2 className="w-3.5 h-3.5 mr-1.5 animate-spin" /> : <Send className="w-3.5 h-3.5 mr-1.5" />}
          Enviar invitación
        </Button>
      </div>
    </DialogContent>
  );
}

// ─── Status badge ────────────────────────────────────────────────────────────

const STATUS_COLORS: Record<string, string> = {
  PENDING: 'bg-amber-50 text-amber-600 border-amber-200',
  ACCEPTED: 'bg-green-50 text-green-600 border-green-200',
  EXPIRED: 'bg-gray-50 text-gray-400 border-gray-200',
  CANCELLED: 'bg-red-50 text-red-400 border-red-200',
};
const STATUS_LABELS: Record<string, string> = {
  PENDING: 'Pendiente', ACCEPTED: 'Aceptada', EXPIRED: 'Expirada', CANCELLED: 'Cancelada',
};

// ─── Main ─────────────────────────────────────────────────────────────────────

export function TeamSection() {
  const businessId = localStorage.getItem('luminia_business_id') ?? '';
  const [tab, setTab] = useState<Tab>('members');

  const [members, setMembers] = useState<Member[]>([]);
  const [roles, setRoles] = useState<BusinessRole[]>([]);
  const [branches, setBranches] = useState<Branch[]>([]);
  const [invitations, setInvitations] = useState<Invitation[]>([]);
  const [loading, setLoading] = useState(true);

  const [assignTarget, setAssignTarget] = useState<Member | null>(null);
  const [roleDialog, setRoleDialog] = useState<{ open: boolean; role?: BusinessRole }>({ open: false });
  const [inviteOpen, setInviteOpen] = useState(false);
  const [expandedRole, setExpandedRole] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const [membersRes, rolesRes, branchesRes, invRes] = await Promise.all([
        luminiApi.get(`/business/${businessId}/members`),
        luminiApi.get(`/business/${businessId}/roles`),
        luminiApi.get(`/business/${businessId}/branches`),
        luminiApi.get(`/business/${businessId}/invitations`),
      ]);
      setMembers(membersRes.data.members ?? membersRes.data ?? []);
      setRoles(rolesRes.data);
      setBranches(Array.isArray(branchesRes.data) ? branchesRes.data : []);
      setInvitations(Array.isArray(invRes.data) ? invRes.data : []);
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

  const cancelInvitation = async (invId: string) => {
    if (!confirm('¿Cancelar esta invitación?')) return;
    await luminiApi.delete(`/business/${businessId}/invitations/${invId}`);
    load();
  };

  const branchMap = Object.fromEntries(branches.map((b) => [b.id, b.name]));

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
        {([['members', Users, 'Miembros'], ['roles', ShieldCheck, 'Roles'], ['invitations', Send, 'Invitaciones']] as const).map(([id, Icon, label]) => (
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
            {id === 'invitations' && invitations.filter((i) => i.status === 'PENDING').length > 0 && (
              <span className="w-4 h-4 rounded-full bg-amber-400 text-white text-[9px] flex items-center justify-center font-bold">
                {invitations.filter((i) => i.status === 'PENDING').length}
              </span>
            )}
          </button>
        ))}
      </div>

      {/* Members tab */}
      {tab === 'members' && (
        <div className="bg-white rounded-2xl shadow-sm">
          <div className="flex items-center justify-between px-5 py-4 border-b border-gray-100">
            <h2 className="font-semibold text-gray-800">Miembros del equipo</h2>
            <Button size="sm" onClick={() => setInviteOpen(true)}>
              <Plus className="w-3.5 h-3.5 mr-1.5" />
              Invitar
            </Button>
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
                  {m.role !== 'OWNER' && m.branchIds && m.branchIds.length > 0 && (
                    <div className="flex flex-wrap gap-1 mt-1">
                      {m.branchIds.map((bId) => (
                        <span key={bId} className="text-[10px] bg-blue-50 text-blue-600 px-1.5 py-0.5 rounded-md flex items-center gap-0.5">
                          <MapPin className="w-2.5 h-2.5" />
                          {branchMap[bId] ?? bId.slice(0, 8)}
                        </span>
                      ))}
                    </div>
                  )}
                </div>
                {m.role === 'OWNER' ? (
                  <Badge variant="outline" className="text-violet-600 border-violet-200 bg-violet-50">Dueño</Badge>
                ) : (
                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => setAssignTarget(m)}
                      className="text-xs text-gray-400 hover:text-violet-600 transition-colors px-2 py-1 rounded-md hover:bg-violet-50"
                    >
                      Configurar
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

      {/* Invitations tab */}
      {tab === 'invitations' && (
        <div className="bg-white rounded-2xl shadow-sm">
          <div className="flex items-center justify-between px-5 py-4 border-b border-gray-100">
            <h2 className="font-semibold text-gray-800">Invitaciones</h2>
            <Button size="sm" onClick={() => setInviteOpen(true)}>
              <Plus className="w-3.5 h-3.5 mr-1.5" />
              Nueva invitación
            </Button>
          </div>
          <div className="divide-y divide-gray-50">
            {invitations.length === 0 && (
              <div className="flex flex-col items-center py-12 text-gray-400">
                <Send className="w-8 h-8 mb-2 text-gray-200" />
                <p className="text-sm">No hay invitaciones</p>
                <p className="text-xs mt-1">Invita miembros a tu equipo</p>
              </div>
            )}
            {invitations.map((inv) => (
              <div key={inv.id} className="flex items-center gap-3 px-5 py-3.5">
                <div className="w-9 h-9 rounded-full bg-gray-100 flex items-center justify-center shrink-0">
                  {inv.email ? <Mail className="w-4 h-4 text-gray-400" /> : <Phone className="w-4 h-4 text-gray-400" />}
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-800 truncate">
                    {inv.email || inv.phone}
                  </p>
                  <div className="flex items-center gap-2 mt-0.5">
                    {inv.businessRole && (
                      <span className="text-[10px] text-violet-600 bg-violet-50 px-1.5 py-0.5 rounded">
                        {inv.businessRole.name}
                      </span>
                    )}
                    {inv.branchIds.length > 0 && (
                      <span className="text-[10px] text-blue-500">
                        {inv.branchIds.length} sucursal{inv.branchIds.length > 1 ? 'es' : ''}
                      </span>
                    )}
                    <span className="text-[10px] text-gray-400">
                      {new Date(inv.createdAt).toLocaleDateString()}
                    </span>
                  </div>
                </div>
                <Badge variant="outline" className={cn('text-[10px]', STATUS_COLORS[inv.status])}>
                  {STATUS_LABELS[inv.status]}
                </Badge>
                {inv.status === 'PENDING' && (
                  <button
                    onClick={() => cancelInvitation(inv.id)}
                    className="text-gray-300 hover:text-red-500 transition-colors p-1"
                  >
                    <X className="w-4 h-4" />
                  </button>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Assignment dialog (role + branches + POS) */}
      <Dialog open={!!assignTarget} onOpenChange={() => setAssignTarget(null)}>
        {assignTarget && (
          <AssignmentDialog
            member={assignTarget}
            roles={roles}
            branches={branches}
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

      {/* Invite dialog */}
      <Dialog open={inviteOpen} onOpenChange={setInviteOpen}>
        {inviteOpen && (
          <InviteDialog
            businessId={businessId}
            roles={roles}
            branches={branches}
            onClose={() => setInviteOpen(false)}
            onDone={() => { setInviteOpen(false); load(); }}
          />
        )}
      </Dialog>
    </div>
  );
}
