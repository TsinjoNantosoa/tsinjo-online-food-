import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { catalogueApi, cartApi } from "../../api";
import { FoodCard } from "../../components/cards";
import {
  Button,
  EmptyState,
  ErrorState,
  Modal,
  PageLoader,
  SafeImage,
  Status,
} from "../../components/ui";
import type { FoodResponse } from "../../types/api";
import { formatPrice, parseId } from "../../utils/format";
import { useAuth } from "../../features/auth/AuthProvider";
export function RestaurantPage() {
  const id = parseId(useParams().id);
  const [filters, setFilters] = useState({
    vegetarian: false,
    nonVegetarian: false,
    seasonal: false,
    food_category: "",
  });
  const [selected, setSelected] = useState<FoodResponse | null>(null);
  const [ingredients, setIngredients] = useState<number[]>([]);
  const [quantity, setQuantity] = useState(1);
  const restaurant = useQuery({
    queryKey: ["restaurant", id],
    queryFn: () => catalogueApi.restaurant(id),
    enabled: id > 0,
  });
  const categories = useQuery({
    queryKey: ["categories", id],
    queryFn: () => catalogueApi.categories(id),
    enabled: id > 0,
  });
  const foods = useQuery({
    queryKey: ["foods", id, filters],
    queryFn: () => catalogueApi.foods(id, filters),
    enabled: id > 0,
  });
  const { user } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const qc = useQueryClient();
  const add = useMutation({
    mutationFn: cartApi.add,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["cart"] });
      setSelected(null);
    },
  });
  const openFood = (food: FoodResponse) => {
    setSelected(food);
    setIngredients([]);
    setQuantity(1);
  };
  const addToCart = () => {
    if (!selected) return;
    if (user?.role !== "ROLE_CUSTOMER") {
      navigate("/login", { state: { from: location.pathname } });
      return;
    }
    add.mutate({ foodId: selected.id, quantity, ingredientIds: ingredients });
  };
  if (restaurant.isLoading) return <PageLoader />;
  if (restaurant.isError)
    return (
      <main className="container page">
        <ErrorState error={restaurant.error} />
      </main>
    );
  const r = restaurant.data!;
  return (
    <main>
      <section className="restaurant-hero">
        <SafeImage src={r.images[0]} alt={r.name} />
        <div className="container restaurant-hero-content">
          <p className="kicker">{r.cuisineType}</p>
          <h1>{r.name}</h1>
          <p>{r.description}</p>
          <div>
            <Status kind={r.open ? "success" : "neutral"}>
              {r.open ? "Open now" : "Currently closed"}
            </Status>
            <span>
              {r.address.city} · {r.openingHours}
            </span>
          </div>
        </div>
      </section>
      <section className="container menu-layout">
        <aside className="filters">
          <h2>Menu</h2>
          <button
            className={
              !filters.vegetarian &&
              !filters.nonVegetarian &&
              !filters.seasonal &&
              !filters.food_category
                ? "active"
                : ""
            }
            onClick={() =>
              setFilters({
                vegetarian: false,
                nonVegetarian: false,
                seasonal: false,
                food_category: "",
              })
            }
          >
            All dishes
          </button>
          <button
            className={filters.vegetarian ? "active" : ""}
            onClick={() =>
              setFilters({
                ...filters,
                vegetarian: !filters.vegetarian,
                nonVegetarian: false,
              })
            }
          >
            Vegetarian
          </button>
          <button
            className={filters.nonVegetarian ? "active" : ""}
            onClick={() =>
              setFilters({
                ...filters,
                nonVegetarian: !filters.nonVegetarian,
                vegetarian: false,
              })
            }
          >
            Non-vegetarian
          </button>
          <button
            className={filters.seasonal ? "active" : ""}
            onClick={() =>
              setFilters({ ...filters, seasonal: !filters.seasonal })
            }
          >
            Seasonal
          </button>
          {categories.data?.map((c) => (
            <button
              className={filters.food_category === c.name ? "active" : ""}
              onClick={() =>
                setFilters({
                  ...filters,
                  food_category: filters.food_category === c.name ? "" : c.name,
                })
              }
              key={c.id}
            >
              {c.name}
            </button>
          ))}
        </aside>
        <div>
          {foods.isError ? (
            <ErrorState error={foods.error} />
          ) : foods.data?.length ? (
            <div className="food-grid">
              {foods.data.map((f) => (
                <FoodCard food={f} key={f.id} onClick={() => openFood(f)} />
              ))}
            </div>
          ) : (
            !foods.isLoading && (
              <EmptyState
                title="No food found"
                description="No menu items match these filters."
              />
            )
          )}
        </div>
      </section>
      <Modal
        open={Boolean(selected)}
        onClose={() => setSelected(null)}
        title={selected?.name || "Dish"}
      >
        {selected && (
          <div className="food-customizer">
            <SafeImage src={selected.images[0]} alt={selected.name} />
            <p>{selected.description}</p>
            <strong>{formatPrice(selected.price)}</strong>
            {selected.ingredients.length > 0 && (
              <fieldset>
                <legend>Choose ingredients</legend>
                {selected.ingredients.map((i) => (
                  <label className={!i.inStock ? "disabled" : ""} key={i.id}>
                    <input
                      type="checkbox"
                      disabled={!i.inStock}
                      checked={ingredients.includes(i.id)}
                      onChange={() =>
                        setIngredients((x) =>
                          x.includes(i.id)
                            ? x.filter((v) => v !== i.id)
                            : [...x, i.id],
                        )
                      }
                    />
                    <span>{i.name}</span>
                    <small>{i.inStock ? i.categoryName : "Out of stock"}</small>
                  </label>
                ))}
              </fieldset>
            )}
            <div className="quantity">
              <button
                onClick={() => setQuantity((q) => Math.max(1, q - 1))}
                aria-label="Decrease quantity"
              >
                −
              </button>
              <span>{quantity}</span>
              <button
                onClick={() => setQuantity((q) => q + 1)}
                aria-label="Increase quantity"
              >
                +
              </button>
            </div>
            {add.error && <p className="field-error">{add.error.message}</p>}
            <Button
              onClick={addToCart}
              loading={add.isPending}
              disabled={!r.open}
            >
              Add to cart · {formatPrice(selected.price * quantity)}
            </Button>
          </div>
        )}
      </Modal>
    </main>
  );
}
