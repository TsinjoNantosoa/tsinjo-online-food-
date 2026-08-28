import type { AddressRequest } from "../types/api";
export const buildFoodCustomizationRequest = (
  foodId: number,
  quantity: number,
  ingredientIds: number[],
) => ({ foodId, quantity, ingredientIds: [...new Set(ingredientIds)] });
export const buildCartQuantityRequest = (
  cartItemId: number,
  quantity: number,
) => ({ cartItemId, quantity });
export const buildCheckoutRequest = (
  restaurantId: number,
  deliveryAddress: AddressRequest,
) => ({ restaurantId, deliveryAddress });
