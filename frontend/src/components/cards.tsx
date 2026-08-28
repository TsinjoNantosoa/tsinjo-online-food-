import { Link } from "react-router-dom";
import type { FoodResponse, RestaurantResponse } from "../types/api";
import { formatPrice } from "../utils/format";
import { SafeImage, Status } from "./ui";

export function RestaurantCard({
  restaurant,
}: {
  restaurant: RestaurantResponse;
}) {
  return (
    <Link className="restaurant-card" to={`/restaurants/${restaurant.id}`}>
      <SafeImage src={restaurant.images[0]} alt={restaurant.name} />
      <div className="card-body">
        <div className="card-row">
          <h3>{restaurant.name}</h3>
          <Status kind={restaurant.open ? "success" : "neutral"}>
            {restaurant.open ? "Open" : "Closed"}
          </Status>
        </div>
        <p className="eyebrow">{restaurant.cuisineType}</p>
        <p className="clamp">{restaurant.description}</p>
        <small>
          {restaurant.address?.city} · {restaurant.openingHours}
        </small>
      </div>
    </Link>
  );
}
export function FoodCard({
  food,
  onClick,
}: {
  food: FoodResponse;
  onClick?: () => void;
}) {
  const select = () => {
    sessionStorage.setItem("tsinjo_restaurant_id", String(food.restaurant.id));
    onClick?.();
  };
  return (
    <button className="food-card" onClick={select} disabled={!food.available}>
      <SafeImage src={food.images[0]} alt={food.name} />
      <div className="card-body">
        <div className="card-row">
          <h3>{food.name}</h3>
          <strong>{formatPrice(food.price)}</strong>
        </div>
        <p className="clamp">{food.description}</p>
        <div className="food-meta">
          <span>{food.category?.name}</span>
          {food.vegetarian && <span>Vegetarian</span>}
          {food.seasonal && <span>Seasonal</span>}
          {!food.available && <span>Unavailable</span>}
        </div>
      </div>
    </button>
  );
}
