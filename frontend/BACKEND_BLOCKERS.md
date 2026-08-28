# Backend blockers

## Cart restaurant identity

`POST /api/orders` requires `restaurantId`, but `GET /api/cart` and `CartItemResponse` expose neither a restaurant ID nor a restaurant summary. The frontend preserves the selected restaurant ID in `sessionStorage` when a dish is opened. This supports the normal menu-to-checkout flow, but a cart restored in a new browser session cannot safely infer its restaurant. Checkout therefore blocks and asks the user to return to the restaurant instead of guessing.

Recommended backend improvement: add `restaurant: { id, name }` to `CartResponse`.

## Favorite state

`PUT /api/restaurant/{id}/add-favorite` toggles a favorite, but no response/profile endpoint exposes a reliable favorite list after refresh. No favorite toggle is shown because its current state cannot be represented truthfully.
