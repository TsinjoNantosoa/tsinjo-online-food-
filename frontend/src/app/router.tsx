import type { ReactNode } from "react";
import { createBrowserRouter } from "react-router-dom";
import { AppLayout } from "../components/layout/AppLayout";
import { ProtectedRoute, RoleRoute } from "../features/auth/RouteGuards";
import { HomePage } from "../pages/public/HomePage";
import { RestaurantsPage } from "../pages/public/RestaurantsPage";
import { RestaurantPage } from "../pages/public/RestaurantPage";
import { SearchPage } from "../pages/public/SearchPage";
import { LoginPage, SignupPage } from "../pages/auth/AuthPages";
import { CartPage } from "../pages/customer/CartPage";
import { CheckoutPage } from "../pages/customer/CheckoutPage";
import { OrderDetailPage, OrdersPage } from "../pages/customer/OrdersPages";
import { ProfilePage } from "../pages/customer/ProfilePage";
import { AdminPage, OwnerPage } from "../pages/management/ManagementPages";
import { ForbiddenPage, NotFoundPage } from "../pages/system/SystemPages";
const customer = (element: ReactNode) => (
  <RoleRoute roles={["ROLE_CUSTOMER"]}>{element}</RoleRoute>
);
const owner = (element: ReactNode) => (
  <RoleRoute roles={["ROLE_RESTAURANT_OWNER"]}>{element}</RoleRoute>
);
const admin = (element: ReactNode) => (
  <RoleRoute roles={["ROLE_ADMIN"]}>{element}</RoleRoute>
);
export const router = createBrowserRouter([
  {
    element: <AppLayout />,
    children: [
      { path: "/", element: <HomePage /> },
      { path: "/restaurants", element: <RestaurantsPage /> },
      { path: "/restaurants/:id", element: <RestaurantPage /> },
      { path: "/search", element: <SearchPage /> },
      { path: "/cart", element: customer(<CartPage />) },
      { path: "/checkout", element: customer(<CheckoutPage />) },
      { path: "/orders", element: customer(<OrdersPage />) },
      { path: "/orders/:id", element: customer(<OrderDetailPage />) },
      {
        path: "/profile",
        element: (
          <ProtectedRoute>
            <ProfilePage />
          </ProtectedRoute>
        ),
      },
      { path: "/forbidden", element: <ForbiddenPage /> },
      { path: "*", element: <NotFoundPage /> },
    ],
  },
  { path: "/login", element: <LoginPage /> },
  { path: "/signup", element: <SignupPage /> },
  { path: "/owner/*", element: owner(<OwnerPage />) },
  { path: "/admin/*", element: admin(<AdminPage />) },
]);
