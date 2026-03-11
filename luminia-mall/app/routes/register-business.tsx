import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router';
import type { Route } from './+types/register-business';
import { useForm } from '@/hooks/useForm';
import { useAuth } from '@/hooks/useAuth';
import { luminiApi } from '@/services/luminiApi';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';
import {
  Building2, ChevronLeft, CheckCircle2, Loader2,
  Store, Dumbbell, Utensils, Monitor, Pill, BookOpen, Wrench, Sparkles, GraduationCap, Leaf,
} from 'lucide-react';
import { cn } from '@/lib/utils';
import { BusinessType } from '@/models';

export function meta({}: Route.MetaArgs) {
  return [{ title: 'Registra tu negocio — Luminia Mall' }];
}

// ─── Business type options ─────────────────────────────────────────────────────
const TYPES = [
  { type: BusinessType.STORE,       label: 'Tienda',       icon: Store,        color: 'text-pink-500' },
  { type: BusinessType.GYM,         label: 'Gimnasio',     icon: Dumbbell,     color: 'text-orange-500' },
  { type: BusinessType.RESTAURANT,  label: 'Restaurante',  icon: Utensils,     color: 'text-red-500' },
  { type: BusinessType.IT_SERVICES, label: 'Servicios TI', icon: Monitor,      color: 'text-blue-500' },
  { type: BusinessType.PHARMACY,    label: 'Farmacia',     icon: Pill,         color: 'text-green-500' },
  { type: BusinessType.BOOKSTORE,   label: 'Librería',     icon: BookOpen,     color: 'text-yellow-600' },
  { type: BusinessType.HARDWARE,    label: 'Ferretería',   icon: Wrench,       color: 'text-gray-600' },
  { type: BusinessType.BEAUTY,      label: 'Belleza',      icon: Sparkles,     color: 'text-purple-500' },
  { type: BusinessType.EDUCATION,   label: 'Educación',    icon: GraduationCap, color: 'text-indigo-500' },
  { type: BusinessType.NATURIST,    label: 'Naturista',    icon: Leaf,         color: 'text-emerald-500' },
];

const CITIES = ['La Paz', 'Cochabamba', 'Santa Cruz', 'Oruro', 'Sucre', 'Tarija'];

const formFields = { name: '', slug: '', description: '', city: '', type: '' };
const formValidations = {
  name:        [(v: string) => v.trim().length >= 2, 'Ingresa el nombre del negocio'],
  slug:        [(v: string) => /^[a-z0-9-]{3,30}$/.test(v), 'Solo letras minúsculas, números y guiones (3-30 chars)'],
  description: [(v: string) => v.trim().length >= 10, 'Descripción muy corta (mínimo 10 caracteres)'],
  city:        [(v: string) => v.length > 0, 'Selecciona tu ciudad'],
  type:        [(v: string) => v.length > 0, 'Selecciona el tipo de negocio'],
};

interface CreatedBusiness {
  id: string;
  name: string;
  slug: string;
}

export default function RegisterBusiness() {
  const navigate = useNavigate();
  const { status, user } = useAuth();
  const [submitted, setSubmitted] = useState(false);
  const [loading, setLoading] = useState(false);
  const [apiError, setApiError] = useState<string | null>(null);
  const [created, setCreated] = useState<CreatedBusiness | null>(null);

  const {
    name, slug, description, city, type,
    onInputChange, onValueChange, isFormValid,
    nameValid, slugValid, descriptionValid, cityValid, typeValid,
  } = useForm(formFields, formValidations);

  // Auto-generate slug from name
  const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    onInputChange(e);
    const generated = e.target.value
      .toLowerCase()
      .normalize('NFD').replace(/[\u0300-\u036f]/g, '')
      .replace(/[^a-z0-9\s-]/g, '')
      .replace(/\s+/g, '-')
      .slice(0, 30);
    onValueChange('slug', generated);
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSubmitted(true);
    if (!isFormValid) return;
    setLoading(true);
    setApiError(null);
    try {
      const { data } = await luminiApi.post('/business', {
        name: name.trim(),
        slug,
        description: description.trim(),
        city,
        type,
      });
      localStorage.setItem('luminia_business_id', data.id);
      setCreated({ id: data.id, name: data.name, slug: data.slug ?? slug });
    } catch (err: any) {
      setApiError(err?.response?.data?.message ?? 'No se pudo crear el negocio');
    } finally {
      setLoading(false);
    }
  };

  // ─── Redirect to login if not authenticated ────────────────────────────────
  if (status === 'not-authenticated') {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center gap-4 text-gray-500">
        <Building2 className="w-10 h-10" />
        <p className="font-medium">Debes iniciar sesión para registrar un negocio</p>
        <Button onClick={() => navigate('/')} variant="outline">
          Ir al Mall
        </Button>
      </div>
    );
  }

  // ─── Success screen ────────────────────────────────────────────────────────
  if (created) {
    const domain = `${created.slug}.luminia.com`;
    return (
      <div className="min-h-screen bg-gradient-to-br from-violet-50 to-indigo-50 flex items-center justify-center p-4">
        <div className="bg-white rounded-3xl shadow-lg p-8 max-w-md w-full text-center space-y-5">
          <div className="w-16 h-16 rounded-2xl bg-green-100 flex items-center justify-center mx-auto">
            <CheckCircle2 className="w-9 h-9 text-green-500" />
          </div>
          <div>
            <h2 className="text-2xl font-bold text-gray-900">¡Tu negocio está listo!</h2>
            <p className="text-gray-500 mt-1 text-sm">
              Hemos creado tu espacio en Luminia Mall
            </p>
          </div>

          <div className="bg-violet-50 border border-violet-100 rounded-2xl p-4 space-y-1">
            <p className="text-xs text-violet-500 font-medium uppercase tracking-wide">Tu tienda pública</p>
            <p className="text-lg font-bold text-violet-700 break-all">
              🔗 {domain}
            </p>
          </div>

          <div className="bg-gray-50 rounded-2xl p-4 space-y-1">
            <p className="text-xs text-gray-400 font-medium uppercase tracking-wide">Tu panel de administración</p>
            <p className="text-base font-semibold text-gray-700 break-all">
              🔐 {domain}/admin
            </p>
          </div>

          <Separator />

          <div className="flex gap-3">
            <Button
              variant="outline"
              className="flex-1 rounded-xl"
              onClick={() => navigate('/')}
            >
              Ir al Mall
            </Button>
            <Button
              className="flex-1 bg-violet-600 hover:bg-violet-700 text-white rounded-xl"
              onClick={() => window.location.href = `/?subdomain=${created.slug}`}
            >
              Ver mi tienda
            </Button>
          </div>
        </div>
      </div>
    );
  }

  // ─── Registration form ─────────────────────────────────────────────────────
  return (
    <div className="min-h-screen bg-gray-50">
      {/* Topbar */}
      <header className="sticky top-0 z-50 bg-white border-b border-gray-100 shadow-sm">
        <div className="max-w-2xl mx-auto px-4 h-14 flex items-center gap-3">
          <button onClick={() => navigate('/')} className="text-gray-400 hover:text-gray-600 transition-colors">
            <ChevronLeft className="w-5 h-5" />
          </button>
          <div className="flex items-center gap-2">
            <Building2 className="w-4 h-4 text-violet-600" />
            <span className="font-semibold text-gray-800 text-sm">Registrar negocio</span>
          </div>
          {user && (
            <span className="ml-auto text-xs text-gray-400">
              Hola, {user.name}
            </span>
          )}
        </div>
      </header>

      <main className="max-w-2xl mx-auto px-4 py-8">
        <div className="mb-6">
          <h1 className="text-2xl font-bold text-gray-900">Cuéntanos sobre tu negocio</h1>
          <p className="text-gray-500 text-sm mt-1">
            En minutos tendrás tu espacio en Luminia Mall con tu propio subdominio
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">

          {/* Business type selector */}
          <div className="space-y-2">
            <Label>Tipo de negocio</Label>
            <div className="grid grid-cols-5 gap-2">
              {TYPES.map(({ type: t, label, icon: Icon, color }) => (
                <button
                  key={t}
                  type="button"
                  onClick={() => onValueChange('type', t)}
                  className={cn(
                    'flex flex-col items-center gap-1.5 p-3 rounded-xl border-2 transition-all text-center',
                    type === t
                      ? 'border-violet-500 bg-violet-50'
                      : 'border-gray-100 bg-white hover:border-violet-200',
                  )}
                >
                  <Icon className={cn('w-5 h-5', color)} />
                  <span className="text-[10px] font-medium text-gray-600 leading-tight">{label}</span>
                </button>
              ))}
            </div>
            {submitted && typeValid && <p className="text-xs text-red-500">{typeValid}</p>}
          </div>

          {/* Name */}
          <div className="space-y-1.5">
            <Label htmlFor="biz-name">Nombre de tu negocio</Label>
            <Input
              id="biz-name"
              name="name"
              value={name}
              onChange={handleNameChange}
              placeholder="Ej: Iron Gym, Moda Bella, TechBolivia..."
              className={submitted && nameValid ? 'border-red-400' : ''}
            />
            {submitted && nameValid && <p className="text-xs text-red-500">{nameValid}</p>}
          </div>

          {/* Slug / subdomain */}
          <div className="space-y-1.5">
            <Label htmlFor="biz-slug">Tu subdominio</Label>
            <div className="flex items-center gap-0 rounded-xl border border-gray-200 overflow-hidden focus-within:ring-2 focus-within:ring-violet-400 focus-within:border-transparent">
              <span className="bg-gray-50 text-gray-400 text-sm px-3 py-2 border-r border-gray-200 whitespace-nowrap shrink-0">
                luminia.com/
              </span>
              <input
                id="biz-slug"
                name="slug"
                value={slug}
                onChange={onInputChange}
                placeholder="minegocio"
                className="flex-1 px-3 py-2 text-sm bg-white outline-none text-gray-800 placeholder-gray-400"
              />
            </div>
            {slug && !slugValid && (
              <p className="text-xs text-violet-500">
                Tu tienda: <strong>{slug}.luminia.com</strong>
              </p>
            )}
            {submitted && slugValid && <p className="text-xs text-red-500">{slugValid}</p>}
          </div>

          {/* Description */}
          <div className="space-y-1.5">
            <Label htmlFor="biz-desc">Descripción breve</Label>
            <textarea
              id="biz-desc"
              name="description"
              value={description}
              onChange={onInputChange}
              placeholder="¿Qué ofrece tu negocio? Cuéntalo en pocas palabras..."
              rows={3}
              className={cn(
                'w-full rounded-xl border border-gray-200 px-3 py-2 text-sm resize-none',
                'focus:outline-none focus:ring-2 focus:ring-violet-400 focus:border-transparent',
                submitted && descriptionValid ? 'border-red-400' : '',
              )}
            />
            {submitted && descriptionValid && <p className="text-xs text-red-500">{descriptionValid}</p>}
          </div>

          {/* City */}
          <div className="space-y-1.5">
            <Label>Ciudad</Label>
            <div className="flex flex-wrap gap-2">
              {CITIES.map((c) => (
                <button
                  key={c}
                  type="button"
                  onClick={() => onValueChange('city', c)}
                  className={cn(
                    'px-4 py-1.5 rounded-full border text-sm font-medium transition-all',
                    city === c
                      ? 'border-violet-500 bg-violet-50 text-violet-700'
                      : 'border-gray-200 text-gray-500 hover:border-violet-200',
                  )}
                >
                  {c}
                </button>
              ))}
            </div>
            {submitted && cityValid && <p className="text-xs text-red-500">{cityValid}</p>}
          </div>

          {apiError && (
            <p className="text-sm text-red-500 bg-red-50 border border-red-100 rounded-xl px-4 py-3">
              {apiError}
            </p>
          )}

          <Button
            type="submit"
            disabled={loading}
            className="w-full h-12 bg-violet-600 hover:bg-violet-700 text-white rounded-xl text-base font-semibold"
          >
            {loading
              ? <><Loader2 className="w-4 h-4 animate-spin mr-2" />Creando tu negocio...</>
              : '🚀 Crear mi negocio'}
          </Button>
        </form>
      </main>
    </div>
  );
}
