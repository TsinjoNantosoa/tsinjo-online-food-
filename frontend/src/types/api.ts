export type UserRole = "ROLE_CUSTOMER" | "ROLE_RESTAURANT_OWNER" | "ROLE_ADMIN";
export interface AddressResponse {
  id: number;
  streetAddress: string;
  city: string;
  state: string | null;
  postalCode: string | null;
  country: string;
}
export type AddressRequest = Omit<AddressResponse, "id">;
export interface ContactInformation {
  email: string | null;
  mobile: string | null;
  twitter: string | null;
  instagram: string | null;
}
export interface UserResponse {
  id: number;
  fullName: string;
  email: string;
  role: UserRole;
  addresses: AddressResponse[];
}
export interface AuthResponse {
  token: string;
  tokenType: string;
  user: UserResponse;
  message?: string;
  jwt?: string;
  role?: UserRole;
}
export interface RestaurantResponse {
  id: number;
  name: string;
  description: string;
  cuisineType: string;
  address: AddressResponse;
  contactInformation: ContactInformation | null;
  openingHours: string;
  images: string[];
  registrationDate: string;
  open: boolean;
  ownerId: number | null;
}
export interface RestaurantSummary {
  id: number;
  name: string;
}
export interface CategorySummary {
  id: number;
  name: string;
}
export interface IngredientItemResponse {
  id: number;
  name: string;
  inStock: boolean;
  categoryId: number;
  categoryName: string;
}
export interface IngredientCategoryResponse {
  id: number;
  name: string;
  ingredients: IngredientItemResponse[];
}
export interface FoodResponse {
  id: number;
  name: string;
  description: string;
  price: number;
  images: string[];
  available: boolean;
  vegetarian: boolean;
  seasonal: boolean;
  restaurant: RestaurantSummary;
  category: CategorySummary;
  ingredients: IngredientItemResponse[];
  creationDate: string;
}
export interface CartItemResponse {
  id: number;
  foodId: number;
  foodName: string;
  foodImage: string | null;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  selectedIngredients: IngredientItemResponse[];
}
export interface CartResponse {
  id: number;
  total: number;
  totalItems: number;
  items: CartItemResponse[];
}
export type OrderStatus =
  "PENDING" | "OUT_FOR_DELIVERY" | "DELIVERED" | "COMPLETED" | "CANCELLED";
export interface OrderItemResponse {
  id: number;
  foodId: number;
  foodName: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  ingredients: string[];
}
export interface OrderResponse {
  id: number;
  status: OrderStatus;
  createdAt: string;
  totalAmount: number;
  totalItems: number;
  customer: { id: number; fullName: string; email: string };
  restaurant: RestaurantSummary;
  deliveryAddress: AddressResponse;
  items: OrderItemResponse[];
}
export interface CreateRestaurantRequest {
  name: string;
  description: string;
  cuisineType: string;
  address: AddressRequest;
  contactInformation: ContactInformation;
  openingHours: string;
  images: string[];
}
export interface CreateFoodRequest {
  name: string;
  description: string;
  price: number;
  restaurantId: number;
  categoryId: number;
  ingredientIds: number[];
  images: string[];
  vegetarian: boolean;
  seasonal: boolean;
}
export interface ApiErrorBody {
  timestamp?: string;
  status: number;
  error?: string;
  message: string;
  fieldErrors?: Record<string, string> | null;
  path?: string;
}
