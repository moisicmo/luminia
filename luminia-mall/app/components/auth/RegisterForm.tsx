import { useState, type FormEvent } from 'react';
import { useForm } from '@/hooks/useForm';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Button } from '@/components/ui/button';
import { Eye, EyeOff, Loader2, CheckCircle2 } from 'lucide-react';

const formFields = { name: '', lastName: '', email: '', password: '', confirmPassword: '' };
const formValidations = {
  name:            [(v: string) => v.trim().length >= 2, 'Ingresa tu nombre'],
  lastName:        [(v: string) => v.trim().length >= 2, 'Ingresa tu apellido'],
  email:           [(v: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v), 'Email inválido'],
  password:        [(v: string) => v.length >= 6, 'Mínimo 6 caracteres'],
  confirmPassword: [(v: string, state: any) => v === state.password, 'Las contraseñas no coinciden'],
};

interface Props {
  onSuccess: () => void;
  loading: boolean;
  error: string | null;
  onRegister: (name: string, lastName: string, email: string, password: string) => Promise<boolean>;
}

export const RegisterForm = ({ onSuccess, loading, error, onRegister }: Props) => {
  const [submitted, setSubmitted] = useState(false);
  const [done, setDone] = useState(false);
  const [showPass, setShowPass] = useState(false);

  const {
    name, lastName, email, password, confirmPassword,
    onInputChange, isFormValid,
    nameValid, lastNameValid, emailValid, passwordValid, confirmPasswordValid,
  } = useForm(formFields, formValidations);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSubmitted(true);
    if (!isFormValid) return;
    const ok = await onRegister(name, lastName, email, password);
    if (ok) {
      setDone(true);
      setTimeout(onSuccess, 2000);
    }
  };

  if (done) {
    return (
      <div className="flex flex-col items-center gap-3 py-6 text-center">
        <CheckCircle2 className="w-12 h-12 text-green-500" />
        <p className="font-semibold text-gray-800">¡Cuenta creada!</p>
        <p className="text-sm text-gray-500">Ahora puedes iniciar sesión.</p>
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-3">
      <div className="grid grid-cols-2 gap-3">
        {/* Name */}
        <div className="space-y-1.5">
          <Label htmlFor="reg-name">Nombre</Label>
          <Input id="reg-name" name="name" value={name} onChange={onInputChange} placeholder="Juan"
            className={submitted && nameValid ? 'border-red-400' : ''} />
          {submitted && nameValid && <p className="text-xs text-red-500">{nameValid}</p>}
        </div>

        {/* Last name */}
        <div className="space-y-1.5">
          <Label htmlFor="reg-last">Apellido</Label>
          <Input id="reg-last" name="lastName" value={lastName} onChange={onInputChange} placeholder="Pérez"
            className={submitted && lastNameValid ? 'border-red-400' : ''} />
          {submitted && lastNameValid && <p className="text-xs text-red-500">{lastNameValid}</p>}
        </div>
      </div>

      {/* Email */}
      <div className="space-y-1.5">
        <Label htmlFor="reg-email">Correo electrónico</Label>
        <Input id="reg-email" name="email" type="email" value={email} onChange={onInputChange}
          placeholder="tu@correo.com" className={submitted && emailValid ? 'border-red-400' : ''}
          autoComplete="email" />
        {submitted && emailValid && <p className="text-xs text-red-500">{emailValid}</p>}
      </div>

      {/* Password */}
      <div className="space-y-1.5">
        <Label htmlFor="reg-pass">Contraseña</Label>
        <div className="relative">
          <Input id="reg-pass" name="password" type={showPass ? 'text' : 'password'}
            value={password} onChange={onInputChange} placeholder="••••••••"
            className={submitted && passwordValid ? 'border-red-400 pr-10' : 'pr-10'}
            autoComplete="new-password" />
          <button type="button" onClick={() => setShowPass((p) => !p)}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600">
            {showPass ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
          </button>
        </div>
        {submitted && passwordValid && <p className="text-xs text-red-500">{passwordValid}</p>}
      </div>

      {/* Confirm password */}
      <div className="space-y-1.5">
        <Label htmlFor="reg-confirm">Confirmar contraseña</Label>
        <Input id="reg-confirm" name="confirmPassword" type="password"
          value={confirmPassword} onChange={onInputChange} placeholder="••••••••"
          className={submitted && confirmPasswordValid ? 'border-red-400' : ''}
          autoComplete="new-password" />
        {submitted && confirmPasswordValid && <p className="text-xs text-red-500">{confirmPasswordValid}</p>}
      </div>

      {/* API error */}
      {error && (
        <p className="text-xs text-red-500 bg-red-50 border border-red-100 rounded-lg px-3 py-2">{error}</p>
      )}

      <Button type="submit" disabled={loading}
        className="w-full bg-violet-600 hover:bg-violet-700 text-white rounded-xl h-10">
        {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : 'Crear cuenta'}
      </Button>
    </form>
  );
};
