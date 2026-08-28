# Online Food API Contract

Base URL: `http://localhost:8080`

Protected endpoints require `Authorization: Bearer <token>`. Prices are integer amounts in the application currency. Dates are ISO-8601 strings.

## Authentication

### POST /auth/signup

- Auth: public
- Request: `{"fullName":"Jane Doe","email":"jane@example.com","password":"Password123!"}`
- Response `201`: `{"token":"...","tokenType":"Bearer","user":{"id":1,"fullName":"Jane Doe","email":"jane@example.com","role":"ROLE_CUSTOMER","addresses":[]}}`
- Status: `201`, `400`, `409`

### POST /auth/signin

- Auth: public
- Request: `{"email":"jane@example.com","password":"Password123!"}`
- Response `200`: same authentication shape as signup
- Status: `200`, `400`, `401`

### GET /api/users/me

- Auth: JWT; any authenticated role
- Response: `UserResponse` without password or token
- Status: `200`, `401`

## Public catalogue

### GET /api/restaurant

- Auth: public
- Response: array of `RestaurantResponse`
- Status: `200`

### GET /api/restaurant/{id}

- Auth: public
- Response: `RestaurantResponse`
- Status: `200`, `404`

### GET /api/restaurant/search?keyword={text}

- Auth: public
- Response: array of `RestaurantResponse`
- Status: `200`

### GET /api/food/restaurant/{restaurantId}

- Auth: public
- Optional query: `vegetarian`, `nonVegetarian`, `seasonal`, `food_category`
- Response: array of `FoodResponse`
- Status: `200`

### GET /api/food/search?name={text}

- Auth: public
- Response: array of `FoodResponse`
- Status: `200`

### GET /api/restaurants/{restaurantId}/categories

- Auth: public
- Response: `[{"id":2,"name":"Burgers"}]`
- Status: `200`, `404`

## Cart

### POST /api/cart/items

- Auth: JWT; customer
- Request: `{"foodId":12,"quantity":2,"ingredientIds":[5,7]}`
- Response: `CartItemResponse`
- Status: `201`, `400`, `401`, `404`

Items merge only when `foodId` and the normalized set of `ingredientIds` are equal.

### PUT /api/cart/items

- Auth: JWT; cart owner
- Request: `{"cartItemId":15,"quantity":3}`
- Response: `CartItemResponse`
- Status: `200`, `400`, `401`, `403`, `404`

### GET /api/cart

- Auth: JWT; cart owner
- Response: `CartResponse` with total, totalItems and display-ready items
- Status: `200`, `401`, `404`

### DELETE /api/cart/items/{id}

- Auth: JWT; cart owner
- Response: empty
- Status: `204`, `401`, `403`, `404`

## Orders

### POST /api/orders

- Auth: JWT; customer
- Request:

```json
{
  "restaurantId": 1,
  "deliveryAddress": {
    "streetAddress": "1 Main Street",
    "city": "Antananarivo",
    "state": "Analamanga",
    "postalCode": "101",
    "country": "Madagascar"
  }
}
```

- Response: `OrderResponse`
- Status: `201`, `400`, `401`, `404`

### GET /api/orders and GET /api/orders/{id}

- Auth: JWT; order owner
- Response: array of `OrderResponse`, or one `OrderResponse`
- Status: `200`, `401`, `403`, `404`

## Restaurant owner operations

### POST /api/admin/food

- Auth: `ROLE_RESTAURANT_OWNER` or `ROLE_ADMIN`; restaurant ownership checked
- Request:

```json
{
  "name": "Classic Burger",
  "description": "Beef burger with cheese",
  "price": 15000,
  "restaurantId": 1,
  "categoryId": 2,
  "ingredientIds": [5, 7],
  "images": [],
  "vegetarian": false,
  "seasonal": false
}
```

- Response: `FoodResponse`
- Status: `201`, `400`, `401`, `403`, `404`

Other protected owner routes:

- `POST|PUT|DELETE /api/admin/restaurants/**`
- `POST /api/admin/categories`
- `PATCH /api/admin/food/{id}` pour modifier les détails
- `PUT /api/admin/food/{id}` pour basculer la disponibilité
- `DELETE /api/admin/food/{id}`
- `/api/admin/ingredients/**`
- `GET /api/admin/order/restaurant/{id}`
- `PUT /api/admin/orders/{orderId}/status/{orderStatus}`

## Error contract

Standard error:

```json
{"timestamp":"2026-08-28T12:00:00Z","status":404,"error":"Not Found","message":"Food not found with id: 12","fieldErrors":null,"path":"/api/food/12"}
```

Validation error includes a `fieldErrors` object keyed by request field. Stack traces are never returned.
