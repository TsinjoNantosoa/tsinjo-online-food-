import {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react";
import { authApi } from "../../api";
import { session } from "../../api/client";
import type { AuthResponse, UserResponse, UserRole } from "../../types/api";
type AuthContextValue = {
  user: UserResponse | null;
  loading: boolean;
  authenticate: (response: AuthResponse) => void;
  logout: () => void;
  hasRole: (roles: UserRole[]) => boolean;
};
const AuthContext = createContext<AuthContextValue | null>(null);
export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserResponse | null>(null);
  const [loading, setLoading] = useState(Boolean(session.getToken()));
  useEffect(() => {
    let active = true;
    const unauthorized = () => {
      setUser(null);
      setLoading(false);
    };
    window.addEventListener("auth:unauthorized", unauthorized);
    if (session.getToken())
      authApi
        .me()
        .then((u) => {
          if (active) setUser(u);
        })
        .catch((e) => {
          if ((e as { status?: number }).status !== 401 && active)
            setUser(null);
        })
        .finally(() => {
          if (active) setLoading(false);
        });
    return () => {
      active = false;
      window.removeEventListener("auth:unauthorized", unauthorized);
    };
  }, []);
  const authenticate = (r: AuthResponse) => {
    session.setToken(r.token || r.jwt || "");
    setUser(r.user);
  };
  const logout = () => {
    session.clear();
    setUser(null);
  };
  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        authenticate,
        logout,
        hasRole: (r) => Boolean(user && r.includes(user.role)),
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}
export function useAuth() {
  const value = useContext(AuthContext);
  if (!value) throw new Error("useAuth must be used inside AuthProvider");
  return value;
}
