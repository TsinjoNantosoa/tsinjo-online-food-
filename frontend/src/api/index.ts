import { api } from "./client";
import type * as T from "../types/api";
const qs = (values: Record<string, string | boolean | undefined>) => {
  const p = new URLSearchParams();
  Object.entries(values).forEach(([k, v]) => {
    if (v !== undefined && v !== "" && v !== false) p.set(k, String(v));
  });
  const s = p.toString();
  return s ? `?${s}` : "";
};
export const authApi = {
  signin: (body: { email: string; password: string }) =>
    api<T.AuthResponse>("/auth/signin", { method: "POST", body, auth: false }),
  signup: (body: { fullName: string; email: string; password: string }) =>
    api<T.AuthResponse>("/auth/signup", { method: "POST", body, auth: false }),
  me: () => api<T.UserResponse>("/api/users/me"),
};
export const catalogueApi = {
  restaurants: () =>
    api<T.RestaurantResponse[]>("/api/restaurant", { auth: false }),
  restaurant: (id: number) =>
    api<T.RestaurantResponse>(`/api/restaurant/${id}`, { auth: false }),
  searchRestaurants: (keyword: string) =>
    api<T.RestaurantResponse[]>(`/api/restaurant/search${qs({ keyword })}`, {
      auth: false,
    }),
  foods: (
    id: number,
    filters: Record<string, string | boolean | undefined> = {},
  ) =>
    api<T.FoodResponse[]>(`/api/food/restaurant/${id}${qs(filters)}`, {
      auth: false,
    }),
  searchFood: (name: string) =>
    api<T.FoodResponse[]>(`/api/food/search${qs({ name })}`, { auth: false }),
  categories: (id: number) =>
    api<T.CategorySummary[]>(`/api/restaurants/${id}/categories`, {
      auth: false,
    }),
};
export const cartApi = {
  get: () => api<T.CartResponse>("/api/cart"),
  add: (body: { foodId: number; quantity: number; ingredientIds: number[] }) =>
    api<T.CartItemResponse>("/api/cart/items", { method: "POST", body }),
  update: (body: { cartItemId: number; quantity: number }) =>
    api<T.CartItemResponse>("/api/cart/items", { method: "PUT", body }),
  remove: (id: number) =>
    api<void>(`/api/cart/items/${id}`, { method: "DELETE" }),
  clear: () => api<T.CartResponse>("/api/cart", { method: "DELETE" }),
};
export const orderApi = {
  create: (body: { restaurantId: number; deliveryAddress: T.AddressRequest }) =>
    api<T.OrderResponse>("/api/orders", { method: "POST", body }),
  list: () => api<T.OrderResponse[]>("/api/orders"),
  get: (id: number) => api<T.OrderResponse>(`/api/orders/${id}`),
};
export const adminApi = {
  ownerRestaurant: () =>
    api<T.RestaurantResponse>("/api/admin/restaurants/user"),
  createRestaurant: (body: T.CreateRestaurantRequest) =>
    api<T.RestaurantResponse>("/api/admin/restaurants", {
      method: "POST",
      body,
    }),
  updateRestaurant: (id: number, body: T.CreateRestaurantRequest) =>
    api<T.RestaurantResponse>(`/api/admin/restaurants/${id}`, {
      method: "PUT",
      body,
    }),
  toggleRestaurant: (id: number) =>
    api<T.RestaurantResponse>(`/api/admin/restaurants/${id}/status`, {
      method: "PUT",
    }),
  deleteRestaurant: (id: number) =>
    api<void>(`/api/admin/restaurants/${id}`, { method: "DELETE" }),
  createFood: (body: T.CreateFoodRequest) =>
    api<T.FoodResponse>("/api/admin/food", { method: "POST", body }),
  updateFood: (id: number, body: T.CreateFoodRequest) =>
    api<T.FoodResponse>(`/api/admin/food/${id}`, { method: "PATCH", body }),
  toggleFood: (id: number) =>
    api<T.FoodResponse>(`/api/admin/food/${id}`, { method: "PUT" }),
  deleteFood: (id: number) =>
    api<void>(`/api/admin/food/${id}`, { method: "DELETE" }),
  createCategory: (body: { name: string; restaurantId: number }) =>
    api<T.CategorySummary>("/api/admin/categories", { method: "POST", body }),
  ingredients: (id: number) =>
    api<T.IngredientItemResponse[]>(`/api/admin/ingredients/restaurant/${id}`),
  ingredientCategories: (id: number) =>
    api<T.IngredientCategoryResponse[]>(
      `/api/admin/ingredients/restaurant/${id}/category`,
    ),
  createIngredientCategory: (body: { name: string; restaurantId: number }) =>
    api<T.IngredientCategoryResponse>("/api/admin/ingredients/category", {
      method: "POST",
      body,
    }),
  createIngredient: (body: {
    name: string;
    categoryId: number;
    restaurantId: number;
  }) =>
    api<T.IngredientItemResponse>("/api/admin/ingredients", {
      method: "POST",
      body,
    }),
  toggleStock: (id: number) =>
    api<T.IngredientItemResponse>(`/api/admin/ingredients/${id}/stoke`, {
      method: "PUT",
    }),
  orders: (id: number, status?: T.OrderStatus) =>
    api<T.OrderResponse[]>(
      `/api/admin/order/restaurant/${id}${qs({ order_status: status })}`,
    ),
  updateOrderStatus: (id: number, status: T.OrderStatus) =>
    api<T.OrderResponse>(`/api/admin/orders/${id}/status/${status}`, {
      method: "PUT",
    }),
};
