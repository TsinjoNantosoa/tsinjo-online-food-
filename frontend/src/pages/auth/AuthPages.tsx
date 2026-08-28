import { useState, type ReactNode } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { authApi } from "../../api";
import { ApiError } from "../../api/client";
import { Button, Input } from "../../components/ui";
import { useAuth } from "../../features/auth/AuthProvider";
import type { AuthResponse } from "../../types/api";
const loginSchema = z.object({
  email: z.email("Enter a valid email"),
  password: z.string().min(1, "Password is required"),
});
const signupSchema = z.object({
  fullName: z.string().min(2, "Enter your full name").max(120),
  email: z.email("Enter a valid email"),
  password: z.string().min(8, "Use at least 8 characters").max(72),
});
type Login = z.infer<typeof loginSchema>;
type Signup = z.infer<typeof signupSchema>;
function DevLoginHints() {
  if (!import.meta.env.DEV) return null;
  return (
    <aside className="dev-hints">
      <strong>Development accounts</strong>
      <p>Customer: customer@test.com</p>
      <p>Owner: owner@test.com</p>
      <p>Admin: admin@test.com</p>
      <small>Available only with the Spring dev profile.</small>
    </aside>
  );
}
function useCompleteAuth() {
  const { authenticate } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  return (r: AuthResponse) => {
    authenticate(r);
    const fallback =
      r.user.role === "ROLE_RESTAURANT_OWNER"
        ? "/owner"
        : r.user.role === "ROLE_ADMIN"
          ? "/admin"
          : "/";
    const from = (location.state as { from?: string } | null)?.from;
    navigate(from && r.user.role === "ROLE_CUSTOMER" ? from : fallback, {
      replace: true,
    });
  };
}
export function LoginPage() {
  const complete = useCompleteAuth();
  const [server, setServer] = useState("");
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<Login>({ resolver: zodResolver(loginSchema) });
  const submit = handleSubmit(async (values) => {
    setServer("");
    try {
      complete(await authApi.signin(values));
    } catch (e) {
      setServer(e instanceof ApiError ? e.message : "Sign in failed");
    }
  });
  return (
    <AuthShell title="Welcome back" intro="Your next great meal is waiting.">
      <form onSubmit={submit}>
        <Input
          label="Email"
          type="email"
          autoComplete="email"
          error={errors.email?.message}
          {...register("email")}
        />
        <Input
          label="Password"
          type="password"
          autoComplete="current-password"
          error={errors.password?.message}
          {...register("password")}
        />
        {server && <p className="form-alert">{server}</p>}
        <Button type="submit" size="lg" loading={isSubmitting}>
          Sign in
        </Button>
      </form>
      <p>
        New here? <Link to="/signup">Create an account</Link>
      </p>
      <DevLoginHints />
    </AuthShell>
  );
}
export function SignupPage() {
  const complete = useCompleteAuth();
  const [server, setServer] = useState("");
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors, isSubmitting },
  } = useForm<Signup>({ resolver: zodResolver(signupSchema) });
  const submit = handleSubmit(async (values) => {
    setServer("");
    try {
      complete(await authApi.signup(values));
    } catch (e) {
      if (e instanceof ApiError) {
        Object.entries(e.fieldErrors).forEach(([k, v]) =>
          setError(k as keyof Signup, { message: v }),
        );
        setServer(e.message);
      } else setServer("Account creation failed");
    }
  });
  return (
    <AuthShell
      title="Create your account"
      intro="Discover nearby kitchens in a few moments."
    >
      <form onSubmit={submit}>
        <Input
          label="Full name"
          autoComplete="name"
          error={errors.fullName?.message}
          {...register("fullName")}
        />
        <Input
          label="Email"
          type="email"
          autoComplete="email"
          error={errors.email?.message}
          {...register("email")}
        />
        <Input
          label="Password"
          type="password"
          autoComplete="new-password"
          error={errors.password?.message}
          {...register("password")}
        />
        <small>Use 8–72 characters.</small>
        {server && <p className="form-alert">{server}</p>}
        <Button type="submit" size="lg" loading={isSubmitting}>
          Create account
        </Button>
      </form>
      <p>
        Already registered? <Link to="/login">Sign in</Link>
      </p>
    </AuthShell>
  );
}
function AuthShell({
  title,
  intro,
  children,
}: {
  title: string;
  intro: string;
  children: ReactNode;
}) {
  return (
    <main className="auth-page">
      <section className="auth-panel">
        <Link to="/" className="brand">
          <span className="brand-mark">T</span>Tsinjo Food
        </Link>
        <div>
          <p className="kicker">Good to have you</p>
          <h1>{title}</h1>
          <p>{intro}</p>
        </div>
        {children}
      </section>
      <aside className="auth-art">
        <blockquote>
          “A warmer way to order from the places that make our city taste like
          home.”
        </blockquote>
      </aside>
    </main>
  );
}
