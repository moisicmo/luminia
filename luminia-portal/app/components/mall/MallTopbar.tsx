import { Search, ShoppingBag, User, LogOut, ShoppingCart } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { useAuth } from '@/hooks/useAuth';
import { useMallCart } from '@/hooks/useMallCart';
import { useNavigate } from 'react-router';

interface Props {
  searchQuery: string;
  onSearch: (q: string) => void;
  onAuthOpen: (tab?: 'login' | 'register') => void;
}

export const MallTopbar = ({ searchQuery, onSearch, onAuthOpen }: Props) => {
  const { status, user, logout } = useAuth();
  const { itemCount, show } = useMallCart();
  const navigate = useNavigate();

  const handleRegisterBusiness = () => {
    if (status === 'authenticated') {
      navigate('/register-business');
    } else {
      onAuthOpen('login');
    }
  };

  return (
    <header className="sticky top-0 z-50 bg-white border-b border-gray-100 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 h-16 flex items-center gap-4">
        {/* Logo */}
        <a href="/" className="flex items-center gap-2 shrink-0">
          <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-violet-600 to-indigo-600 flex items-center justify-center">
            <ShoppingBag className="w-4 h-4 text-white" />
          </div>
          <span className="font-bold text-gray-900 text-lg hidden sm:block">
            Luminia <span className="text-violet-600">Mall</span>
          </span>
        </a>

        {/* Search bar */}
        <div className="flex-1 relative max-w-2xl">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => onSearch(e.target.value)}
            placeholder="Buscar productos en todo el mall..."
            className="w-full pl-10 pr-4 py-2 rounded-full border border-gray-200 bg-gray-50 text-sm
                       focus:outline-none focus:ring-2 focus:ring-violet-400 focus:border-transparent
                       placeholder-gray-400 transition-all"
          />
        </div>

        {/* Actions */}
        <div className="flex items-center gap-2 shrink-0">
          {/* Cart button */}
          <button
            onClick={show}
            className="relative p-2 text-gray-600 hover:text-violet-600 transition-colors"
          >
            <ShoppingCart className="w-5 h-5" />
            {itemCount > 0 && (
              <span className="absolute -top-0.5 -right-0.5 w-5 h-5 rounded-full bg-violet-600 text-white text-xs font-bold flex items-center justify-center">
                {itemCount > 99 ? '99+' : itemCount}
              </span>
            )}
          </button>

          {status === 'authenticated' && user ? (
            <>
              <span className="text-sm text-gray-600 hidden sm:block">
                Hola, <strong>{user.name}</strong>
              </span>
              <Button variant="ghost" size="sm" onClick={logout} className="text-gray-500">
                <LogOut className="w-4 h-4" />
              </Button>
            </>
          ) : (
            <Button
              variant="ghost"
              size="sm"
              onClick={() => onAuthOpen('login')}
              className="hidden sm:flex gap-1.5 text-gray-600"
            >
              <User className="w-4 h-4" />
              <span>Ingresar</span>
            </Button>
          )}
          <Button
            size="sm"
            onClick={handleRegisterBusiness}
            className="bg-violet-600 hover:bg-violet-700 text-white rounded-full px-4"
          >
            Registrar negocio
          </Button>
        </div>
      </div>
    </header>
  );
};
