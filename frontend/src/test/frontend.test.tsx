import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { ApiError, parseApiError, session } from "../api/client";
import { AuthProvider } from "../features/auth/AuthProvider";
import { ProtectedRoute, RoleRoute } from "../features/auth/RouteGuards";
import { formatPrice } from "../utils/format";
import {
  buildCartQuantityRequest,
  buildCheckoutRequest,
  buildFoodCustomizationRequest,
} from "../utils/requests";
import { OrderStatusBadge, statusLabel } from "../pages/customer/OrdersPages";
import { SafeImage } from "../components/ui";
describe("frontend contracts", () => {
  beforeEach(() => vi.restoreAllMocks());
  it("stores and clears the bearer session", () => {
    session.setToken("jwt-value");
    expect(session.getToken()).toBe("jwt-value");
    session.clear();
    expect(session.getToken()).toBeNull();
  });
  it("redirects protected routes without authentication", async () => {
    render(
      <MemoryRouter initialEntries={["/private"]}>
        <AuthProvider>
          <Routes>
            <Route path="/login" element={<p>Login screen</p>} />
            <Route
              path="/private"
              element={
                <ProtectedRoute>
                  <p>Private</p>
                </ProtectedRoute>
              }
            />
          </Routes>
        </AuthProvider>
      </MemoryRouter>,
    );
    expect(await screen.findByText("Login screen")).toBeInTheDocument();
  });
  it("blocks a role that does not match", async () => {
    session.setToken("token");
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue(
        new Response(
          JSON.stringify({
            id: 1,
            fullName: "Customer",
            email: "c@test.com",
            role: "ROLE_CUSTOMER",
            addresses: [],
          }),
          { status: 200 },
        ),
      ),
    );
    render(
      <MemoryRouter initialEntries={["/admin"]}>
        <AuthProvider>
          <Routes>
            <Route path="/forbidden" element={<p>Forbidden screen</p>} />
            <Route
              path="/admin"
              element={
                <RoleRoute roles={["ROLE_ADMIN"]}>
                  <p>Admin</p>
                </RoleRoute>
              }
            />
          </Routes>
        </AuthProvider>
      </MemoryRouter>,
    );
    expect(await screen.findByText("Forbidden screen")).toBeInTheDocument();
  });
  it("formats Ariary prices centrally", () =>
    expect(formatPrice(15000)).toMatch(/15[\s\u202f]000 Ar/));
  it("parses backend field errors", () => {
    const error = parseApiError(
      { message: "Invalid request", fieldErrors: { email: "already used" } },
      400,
    );
    expect(error).toBeInstanceOf(ApiError);
    expect(error.fieldErrors.email).toBe("already used");
  });
  it("builds ingredient ids without duplicates", () =>
    expect(buildFoodCustomizationRequest(12, 2, [7, 5, 7])).toEqual({
      foodId: 12,
      quantity: 2,
      ingredientIds: [7, 5],
    }));
  it("builds the exact cart quantity body", () =>
    expect(buildCartQuantityRequest(15, 3)).toEqual({
      cartItemId: 15,
      quantity: 3,
    }));
  it("builds the exact checkout address body", () => {
    const address = {
      streetAddress: "1 Main",
      city: "Tana",
      state: "Analamanga",
      postalCode: "101",
      country: "Madagascar",
    };
    expect(buildCheckoutRequest(1, address)).toEqual({
      restaurantId: 1,
      deliveryAddress: address,
    });
  });
  it("renders known order status text", () => {
    render(<OrderStatusBadge status="OUT_FOR_DELIVERY" />);
    expect(screen.getByText("Out for delivery")).toBeInTheDocument();
    expect(statusLabel("CANCELLED")).toBe("Cancelled");
  });
  it("falls back when an image fails", async () => {
    render(<SafeImage src="bad.jpg" alt="Meal" />);
    fireEvent.error(screen.getByAltText("Meal"));
    await waitFor(() =>
      expect(
        screen.getByRole("img", { name: "Meal image unavailable" }),
      ).toBeInTheDocument(),
    );
  });
});
