import { useState, type FormEvent } from 'react';
import { useForm } from '@/hooks/useForm';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Button } from '@/components/ui/button';
import { Eye, EyeOff, Loader2 } from 'lucide-react';

const formFields = { email: '', password: '' };
const formValidations = {
  email: [(v: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v), 'Ingresa un email válido'],
  password: [(v: string) => v.length >= 6, 'Mínimo 6 caracteres'],
};

interface Props {
  onSuccess: () => void;
  loading: boolean;
  error: string | null;
  onLogin: (email: string, password: string) => Promise<boolean>;
}

export const LoginForm = ({ onSuccess, loading, error, onLogin }: Props) => {
  const [submitted, setSubmitted] = useState(false);
  const [showPass, setShowPass] = useState(false);

  const { email, password, onInputChange, isFormValid, emailValid, passwordValid } =
    useForm(formFields, formValidations);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSubmitted(true);
    if (!isFormValid) return;
    const ok = await onLogin(email, password);
    if (ok) onSuccess();
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {/* Email */}
      <div className="space-y-1.5">
        <Label htmlFor="login-email">Correo electrónico</Label>
        <Input
          id="login-email"
          name="email"
          type="email"
          value={email}
          onChange={onInputChange}
          placeholder="tu@correo.com"
          className={submitted && emailValid ? 'border-red-400 focus-visible:ring-red-300' : ''}
          autoComplete="email"
        />
        {submitted && emailValid && <p className="text-xs text-red-500">{emailValid}</p>}
      </div>

      {/* Password */}
      <div className="space-y-1.5">
        <Label htmlFor="login-password">Contraseña</Label>
        <div className="relative">
          <Input
            id="login-password"
            name="password"
            type={showPass ? 'text' : 'password'}
            value={password}
            onChange={onInputChange}
            placeholder="••••••••"
            className={submitted && passwordValid ? 'border-red-400 pr-10' : 'pr-10'}
            autoComplete="current-password"
          />
          <button
            type="button"
            onClick={() => setShowPass((p) => !p)}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
          >
            {showPass ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
          </button>
        </div>
        {submitted && passwordValid && <p className="text-xs text-red-500">{passwordValid}</p>}
      </div>

      {/* API error */}
      {error && (
        <p className="text-xs text-red-500 bg-red-50 border border-red-100 rounded-lg px-3 py-2">
          {error}
        </p>
      )}

      <Button
        type="submit"
        disabled={loading}
        className="w-full bg-violet-600 hover:bg-violet-700 text-white rounded-xl h-10"
      >
        {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : 'Iniciar sesión'}
      </Button>
    </form>
  );
};
