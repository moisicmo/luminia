import { useEffect, useState, type FormEvent } from 'react';
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
  Check, Zap, Crown,
} from 'lucide-react';
import { cn } from '@/lib/utils';
import { BusinessType } from '@/models';

export function meta({}: Route.MetaArgs) {
  return [{ title: 'Registra tu negocio — Luminia Mall' }];
}

const TYPES = [
  { type: BusinessType.TIENDA,          label: 'Tienda',       icon: Store,         color: 'text-pink-500' },
  { type: BusinessType.SALUD,           label: 'Salud',        icon: Dumbbell,      color: 'text-orange-500' },
  { type: BusinessType.RESTAURANT,      label: 'Restaurante',  icon: Utensils,      color: 'text-red-500' },
  { type: BusinessType.SERVICIO,        label: 'Servicios',    icon: Monitor,       color: 'text-blue-500' },
  { type: BusinessType.ECOMMERCE,       label: 'E-commerce',   icon: Pill,          color: 'text-green-500' },
  { type: BusinessType.CAPACITACION,    label: 'Capacitación', icon: BookOpen,      color: 'text-yellow-600' },
  { type: BusinessType.CONSULTORIA,     label: 'Consultoría',  icon: Wrench,        color: 'text-gray-600' },
  { type: BusinessType.ENTRETENIMIENTO, label: 'Entretenim.',  icon: Sparkles,      color: 'text-purple-500' },
  { type: BusinessType.INSTITUTO,       label: 'Instituto',    icon: GraduationCap, color: 'text-indigo-500' },
  { type: BusinessType.HOTEL,           label: 'Hotel',        icon: Leaf,          color: 'text-emerald-500' },
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

interface Plan {
  id: string;
  name: string;
  price: number;
  discountPrice: number;
  billingCycle: string;
  seatLimit: number;
  trialDays: number;
  features: string[];
}

interface CreatedBusiness {
  id: string;
  name: string;
  slug: string;
}

const PLAN_ICONS: Record<string, any> = {
  Starter: Zap,
  Emprendedor: Store,
  Negocio: Crown,
  Enterprise: Sparkles,
};

const PLAN_COLORS: Record<string, string> = {
  Starter: 'border-gray-200 hover:border-gray-300',
  Emprendedor: 'border-blue-200 hover:border-blue-400',
  Negocio: 'border-violet-200 hover:border-violet-400',
  Enterprise: 'border-amber-200 hover:border-amber-400',
};

const PLAN_SELECTED_COLORS: Record<string, string> = {
  Starter: 'border-gray-500 bg-gray-50',
  Emprendedor: 'border-blue-500 bg-blue-50',
  Negocio: 'border-violet-500 bg-violet-50',
  Enterprise: 'border-amber-500 bg-amber-50',
};

const PLAN_BADGE_COLORS: Record<string, string> = {
  Starter: 'bg-gray-100 text-gray-600',
  Emprendedor: 'bg-blue-100 text-blue-700',
  Negocio: 'bg-violet-100 text-violet-700',
  Enterprise: 'bg-amber-100 text-amber-700',
};

export default function RegisterBusiness() {
  const navigate = useNavigate();
  const { status, user } = useAuth();
  const [step, setStep] = useState<1 | 2>(1);
  const [submitted, setSubmitted] = useState(false);
  const [loading, setLoading] = useState(false);
  const [apiError, setApiError] = useState<string | null>(null);
  const [created, setCreated] = useState<CreatedBusiness | null>(null);

  const [plans, setPlans] = useState<Plan[]>([]);
  const [plansLoading, setPlansLoading] = useState(false);
  const [selectedPlanId, setSelectedPlanId] = useState<string | null>(null);
  const [subscribing, setSubscribing] = useState(false);

  const {
    name, slug, description, city, type,
    onInputChange, onValueChange, isFormValid,
    nameValid, slugValid, descriptionValid, cityValid, typeValid,
  } = useForm(formFields, formValidations);

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

  useEffect(() => {
    if (step !== 2) return;
    setPlansLoading(true);
    luminiApi.get('/subscriptions/platform-plans')
      .then(({ data }) => {
        setPlans(data);
        const starter = data.find((p: Plan) => p.price === 0);
        if (starter) setSelectedPlanId(starter.id);
      })
      .catch(() => setApiError('No se pudieron cargar los planes'))
      .finally(() => setPlansLoading(false));
  }, [step]);

  const handleStep1Submit = (e: FormEvent) => {
    e.preventDefault();
    setSubmitted(true);
    if (!isFormValid) return;
    setStep(2);
    setSubmitted(false);
    setApiError(null);
  };

  const handleFinalSubmit = async () => {
    if (!selectedPlanId) return;
    setLoading(true);
    setApiError(null);
    try {
      const { data } = await luminiApi.post('/business', {
        name: name.trim(),
        businessType: type,
        url: slug,
        branch: { name: 'Sucursal Principal', region: city },
      });

      const businessId: string = data.businessId ?? data.id;
      localStorage.setItem('luminia_business_id', businessId);

      setSubscribing(true);
      await luminiApi.post(
        '/subscriptions/platform',
        { planId: selectedPlanId },
        { headers: { 'X-Business-Id': businessId } },
      );

      setCreated({ id: businessId, name: data.name, slug });
    } catch (err: any) {
      const msg = err?.response?.data?.message;
      setApiError(Array.isArray(msg) ? msg.join(', ') : (msg ?? 'No se pudo completar el registro'));
    } finally {
      setLoading(false);
      setSubscribing(false);
    }
  };

  if (status === 'not-authenticated') {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center gap-4 text-gray-500">
        <Building2 className="w-10 h-10" />
        <p className="font-medium">Debes iniciar sesión para registrar un negocio</p>
        <Button onClick={() => navigate('/')} variant="outline">Ir al Mall</Button>
      </div>
    );
  }

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
            <p className="text-gray-500 mt-1 text-sm">Hemos creado tu espacio en Luminia Mall</p>
          </div>
          <div className="bg-violet-50 border border-violet-100 rounded-2xl p-4 space-y-1">
            <p className="text-xs text-violet-500 font-medium uppercase tracking-wide">Tu tienda pública</p>
            <p className="text-lg font-bold text-violet-700 break-all">🔗 {domain}</p>
          </div>
          <div className="bg-gray-50 rounded-2xl p-4 space-y-1">
            <p className="text-xs text-gray-400 font-medium uppercase tracking-wide">Tu panel de administración</p>
            <p className="text-base font-semibold text-gray-700 break-all">🔐 {domain}/admin</p>
          </div>
          <Separator />
          <div className="flex gap-3">
            <Button variant="outline" className="flex-1 rounded-xl" onClick={() => navigate('/')}>Ir al Mall</Button>
            <Button
              className="flex-1 bg-violet-600 hover:bg-violet-700 text-white rounded-xl"
              onClick={() => {
                const { protocol, port } = window.location;
                const host = window.location.hostname;
                const parts = host.split('.');
                // lvh.me → test.lvh.me:port
                // luminia.local → test.luminia.local:port
                // luminia.com → test.luminia.com
                let base: string;
                if (parts.length >= 2 && parts[parts.length - 2] === 'lvh') {
                  base = `${created.slug}.lvh.me`;
                } else if (parts.length >= 2 && parts[parts.length - 1] === 'local') {
                  base = `${created.slug}.${parts.slice(-2).join('.')}`;
                } else if (parts.includes('luminia')) {
                  base = `${created.slug}.luminia.com`;
                } else {
                  base = `${created.slug}.${host}`;
                }
                const portSuffix = port && port !== '80' && port !== '443' ? `:${port}` : '';
                // Pass token to the new subdomain so it can store it in its own localStorage
                const token = localStorage.getItem('luminia_token');
                const tokenParam = token ? `?_token=${encodeURIComponent(token)}` : '';
                window.location.href = `${protocol}//${base}${portSuffix}/${tokenParam}`;
              }}
            >
              Ver mi tienda
            </Button>
          </div>
        </div>
      </div>
    );
  }

  const Topbar = (
    <header className="sticky top-0 z-50 bg-white border-b border-gray-100 shadow-sm">
      <div className="max-w-2xl mx-auto px-4 h-14 flex items-center gap-3">
        <button
          onClick={() => step === 2 ? setStep(1) : navigate('/')}
          className="text-gray-400 hover:text-gray-600 transition-colors"
        >
          <ChevronLeft className="w-5 h-5" />
        </button>
        <div className="flex items-center gap-2">
          <Building2 className="w-4 h-4 text-violet-600" />
          <span className="font-semibold text-gray-800 text-sm">Registrar negocio</span>
        </div>
        <div className="ml-auto flex items-center gap-2">
          <div className={cn('w-6 h-6 rounded-full text-xs font-bold flex items-center justify-center',
            step === 1 ? 'bg-violet-600 text-white' : 'bg-green-100 text-green-600')}>
            {step > 1 ? <Check className="w-3 h-3" /> : '1'}
          </div>
          <div className="w-6 h-px bg-gray-200" />
          <div className={cn('w-6 h-6 rounded-full text-xs font-bold flex items-center justify-center',
            step === 2 ? 'bg-violet-600 text-white' : 'bg-gray-100 text-gray-400')}>
            2
          </div>
        </div>
        {user && <span className="ml-3 text-xs text-gray-400">{user.name}</span>}
      </div>
    </header>
  );

  if (step === 1) {
    return (
      <div className="min-h-screen bg-gray-50">
        {Topbar}
        <main className="max-w-2xl mx-auto px-4 py-8">
          <div className="mb-6">
            <h1 className="text-2xl font-bold text-gray-900">Cuéntanos sobre tu negocio</h1>
            <p className="text-gray-500 text-sm mt-1">En minutos tendrás tu espacio en Luminia Mall con tu propio subdominio</p>
          </div>
          <form onSubmit={handleStep1Submit} className="space-y-6">
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
                      type === t ? 'border-violet-500 bg-violet-50' : 'border-gray-100 bg-white hover:border-violet-200',
                    )}
                  >
                    <Icon className={cn('w-5 h-5', color)} />
                    <span className="text-[10px] font-medium text-gray-600 leading-tight">{label}</span>
                  </button>
                ))}
              </div>
              {submitted && typeValid && <p className="text-xs text-red-500">{typeValid}</p>}
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="biz-name">Nombre de tu negocio</Label>
              <Input
                id="biz-name" name="name" value={name} onChange={handleNameChange}
                placeholder="Ej: Iron Gym, Moda Bella, TechBolivia..."
                className={submitted && nameValid ? 'border-red-400' : ''}
              />
              {submitted && nameValid && <p className="text-xs text-red-500">{nameValid}</p>}
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="biz-slug">Tu subdominio</Label>
              <div className="flex items-center rounded-xl border border-gray-200 overflow-hidden focus-within:ring-2 focus-within:ring-violet-400 focus-within:border-transparent">
                <span className="bg-gray-50 text-gray-400 text-sm px-3 py-2 border-r border-gray-200 whitespace-nowrap shrink-0">luminia.com/</span>
                <input
                  id="biz-slug" name="slug" value={slug} onChange={onInputChange}
                  placeholder="minegocio"
                  className="flex-1 px-3 py-2 text-sm bg-white outline-none text-gray-800 placeholder-gray-400"
                />
              </div>
              {slug && !slugValid && <p className="text-xs text-violet-500">Tu tienda: <strong>{slug}.luminia.com</strong></p>}
              {submitted && slugValid && <p className="text-xs text-red-500">{slugValid}</p>}
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="biz-desc">Descripción breve</Label>
              <textarea
                id="biz-desc" name="description" value={description} onChange={onInputChange}
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

            <div className="space-y-1.5">
              <Label>Ciudad</Label>
              <div className="flex flex-wrap gap-2">
                {CITIES.map((c) => (
                  <button
                    key={c} type="button" onClick={() => onValueChange('city', c)}
                    className={cn(
                      'px-4 py-1.5 rounded-full border text-sm font-medium transition-all',
                      city === c ? 'border-violet-500 bg-violet-50 text-violet-700' : 'border-gray-200 text-gray-500 hover:border-violet-200',
                    )}
                  >
                    {c}
                  </button>
                ))}
              </div>
              {submitted && cityValid && <p className="text-xs text-red-500">{cityValid}</p>}
            </div>

            <Button type="submit" className="w-full h-12 bg-violet-600 hover:bg-violet-700 text-white rounded-xl text-base font-semibold">
              Continuar → Elegir plan
            </Button>
          </form>
        </main>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {Topbar}
      <main className="max-w-2xl mx-auto px-4 py-8">
        <div className="mb-6">
          <h1 className="text-2xl font-bold text-gray-900">Elige tu plan</h1>
          <p className="text-gray-500 text-sm mt-1">Todos los planes incluyen período de prueba. Puedes cambiar de plan cuando quieras.</p>
        </div>

        {plansLoading ? (
          <div className="flex justify-center py-16"><Loader2 className="w-8 h-8 animate-spin text-violet-400" /></div>
        ) : (
          <div className="space-y-3">
            {plans.map((plan) => {
              const Icon = PLAN_ICONS[plan.name] ?? Zap;
              const isSelected = selectedPlanId === plan.id;
              return (
                <button
                  key={plan.id}
                  type="button"
                  onClick={() => setSelectedPlanId(plan.id)}
                  className={cn(
                    'w-full text-left rounded-2xl border-2 p-4 transition-all bg-white',
                    isSelected
                      ? (PLAN_SELECTED_COLORS[plan.name] ?? 'border-violet-500 bg-violet-50')
                      : (PLAN_COLORS[plan.name] ?? 'border-gray-200 hover:border-gray-300'),
                  )}
                >
                  <div className="flex items-start gap-3">
                    <div className={cn('w-10 h-10 rounded-xl flex items-center justify-center shrink-0', PLAN_BADGE_COLORS[plan.name] ?? 'bg-gray-100 text-gray-600')}>
                      <Icon className="w-5 h-5" />
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2 flex-wrap">
                        <span className="font-bold text-gray-900">{plan.name}</span>
                        {plan.trialDays > 0 && (
                          <span className="text-[10px] font-semibold bg-green-100 text-green-700 px-2 py-0.5 rounded-full">
                            {plan.trialDays} días gratis
                          </span>
                        )}
                      </div>
                      <div className="flex items-baseline gap-1 mt-0.5">
                        {plan.price === 0 ? (
                          <span className="text-xl font-bold text-gray-900">Gratis</span>
                        ) : (
                          <>
                            <span className="text-xl font-bold text-gray-900">Bs {plan.price}</span>
                            <span className="text-xs text-gray-400">/{plan.billingCycle.toLowerCase()}</span>
                          </>
                        )}
                      </div>
                      <ul className="mt-2 space-y-1">
                        {plan.features.map((f, i) => (
                          <li key={i} className="flex items-center gap-1.5 text-xs text-gray-600">
                            <Check className="w-3 h-3 text-green-500 shrink-0" />
                            {f}
                          </li>
                        ))}
                      </ul>
                    </div>
                    <div className={cn(
                      'w-5 h-5 rounded-full border-2 shrink-0 mt-1 flex items-center justify-center transition-all',
                      isSelected ? 'border-violet-600 bg-violet-600' : 'border-gray-300',
                    )}>
                      {isSelected && <Check className="w-3 h-3 text-white" />}
                    </div>
                  </div>
                </button>
              );
            })}
          </div>
        )}

        {apiError && (
          <p className="mt-4 text-sm text-red-500 bg-red-50 border border-red-100 rounded-xl px-4 py-3">{apiError}</p>
        )}

        <div className="mt-6">
          <Button
            onClick={handleFinalSubmit}
            disabled={!selectedPlanId || loading || plansLoading}
            className="w-full h-12 bg-violet-600 hover:bg-violet-700 text-white rounded-xl text-base font-semibold"
          >
            {loading
              ? <><Loader2 className="w-4 h-4 animate-spin mr-2" />{subscribing ? 'Activando plan...' : 'Creando tu negocio...'}</>
              : '🚀 Crear mi negocio'}
          </Button>
          <p className="text-center text-xs text-gray-400 mt-3">
            Al continuar aceptas nuestros términos de servicio. Puedes cancelar en cualquier momento.
          </p>
        </div>
      </main>
    </div>
  );
}
