import { useState } from "react";
import { Link, NavLink, Outlet } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { cartApi } from "../../api";
import { useAuth } from "../../features/auth/AuthProvider";
import { CartSvg, CloseSvg, MenuSvg } from "../svg/Icons";
export function Brand() {
  return (
    <Link className="brand" to="/">
      <span className="brand-mark">T</span>
      <span>Tsinjo Food</span>
    </Link>
  );
}
export function AppLayout() {
  const [open, setOpen] = useState(false);
  const { user } = useAuth();
  const cart = useQuery({
    queryKey: ["cart"],
    queryFn: cartApi.get,
    enabled: user?.role === "ROLE_CUSTOMER",
  });
  const link = (to: string, label: string) => (
    <NavLink onClick={() => setOpen(false)} to={to}>
      {label}
    </NavLink>
  );
  return (
    <>
      <header className="site-header">
        <div className="header-inner">
          <Brand />
          <nav className={open ? "nav-open" : ""} aria-label="Main navigation">
            {link("/restaurants", "Restaurants")}
            {link("/search", "Search")}
            {user?.role === "ROLE_CUSTOMER" && (
              <>
                {link("/orders", "Orders")}
                <NavLink
                  onClick={() => setOpen(false)}
                  to="/cart"
                  className="cart-link"
                >
                  <CartSvg /> Cart <b>{cart.data?.totalItems ?? 0}</b>
                </NavLink>
              </>
            )}
            {user?.role === "ROLE_RESTAURANT_OWNER" &&
              link("/owner", "Store dashboard")}
            {user?.role === "ROLE_ADMIN" && link("/admin", "Admin")}
            {user && link("/profile", "Profile")}
            {!user && (
              <>
                {link("/login", "Sign in")}
                <NavLink to="/signup" className="nav-cta">
                  Create account
                </NavLink>
              </>
            )}
          </nav>
          <button
            className="menu-toggle"
            onClick={() => setOpen(!open)}
            aria-label={open ? "Close menu" : "Open menu"}
            aria-expanded={open}
          >
            {open ? <CloseSvg /> : <MenuSvg />}
          </button>
        </div>
      </header>
      <Outlet />
      <footer>
        <div className="container footer-grid">
          <Brand />
          <p>Good food, thoughtfully delivered across Madagascar.</p>
          <nav>
            <Link to="/restaurants">Restaurants</Link>
            <Link to="/search">Search</Link>
          </nav>
        </div>
        <div className="container footer-bottom">
          © {new Date().getFullYear()} Tsinjo Food
        </div>
      </footer>
    </>
  );
}
