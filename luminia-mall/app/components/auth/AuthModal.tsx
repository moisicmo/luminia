import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Separator } from '@/components/ui/separator';
import { ShoppingBag } from 'lucide-react';
import { LoginForm } from './LoginForm';
import { RegisterForm } from './RegisterForm';
import { useAuth } from '@/hooks/useAuth';

export type AuthTab = 'login' | 'register';

interface Props {
  open: boolean;
  defaultTab?: AuthTab;
  onOpenChange: (open: boolean) => void;
}

export const AuthModal = ({ open, defaultTab = 'login', onOpenChange }: Props) => {
  const { loading, error, login, register, clearError } = useAuth();
  const onClose = () => onOpenChange(false);

  const handleTabChange = () => clearError();

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md p-0 overflow-hidden gap-0">
        {/* Header */}
        <div className="bg-gradient-to-br from-violet-600 to-indigo-600 px-6 py-5">
          <div className="flex items-center gap-2 mb-1">
            <div className="w-7 h-7 rounded-lg bg-white/20 flex items-center justify-center">
              <ShoppingBag className="w-4 h-4 text-white" />
            </div>
            <span className="font-bold text-white text-base">Luminia Mall</span>
          </div>
          <DialogHeader>
            <DialogTitle className="text-white/90 text-sm font-normal">
              Accede o crea tu cuenta para registrar tu negocio
            </DialogTitle>
          </DialogHeader>
        </div>

        <div className="px-6 py-5">
          <Tabs defaultValue={defaultTab} onValueChange={handleTabChange}>
            <TabsList className="w-full mb-5">
              <TabsTrigger value="login" className="flex-1">Iniciar sesión</TabsTrigger>
              <TabsTrigger value="register" className="flex-1">Crear cuenta</TabsTrigger>
            </TabsList>

            <TabsContent value="login">
              <LoginForm
                loading={loading}
                error={error}
                onLogin={login}
                onSuccess={onClose}
              />
              <div className="mt-4 flex items-center gap-2">
                <Separator className="flex-1" />
                <span className="text-xs text-gray-400">o</span>
                <Separator className="flex-1" />
              </div>
              <p className="text-center text-xs text-gray-500 mt-3">
                ¿No tienes cuenta?{' '}
                <button
                  type="button"
                  onClick={() => {
                    const trigger = document.querySelector<HTMLButtonElement>('[data-slot="tabs-trigger"][value="register"]');
                    trigger?.click();
                  }}
                  className="text-violet-600 font-medium hover:underline"
                >
                  Regístrate gratis
                </button>
              </p>
            </TabsContent>

            <TabsContent value="register">
              <RegisterForm
                loading={loading}
                error={error}
                onRegister={register}
                onSuccess={() => {
                  const trigger = document.querySelector<HTMLButtonElement>('[data-slot="tabs-trigger"][value="login"]');
                  trigger?.click();
                }}
              />
            </TabsContent>
          </Tabs>
        </div>
      </DialogContent>
    </Dialog>
  );
};
