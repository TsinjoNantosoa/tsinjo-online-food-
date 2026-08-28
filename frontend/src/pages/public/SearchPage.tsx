import { useEffect, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { useNavigate, useSearchParams } from "react-router-dom";
import { catalogueApi } from "../../api";
import { FoodCard, RestaurantCard } from "../../components/cards";
import { EmptyState, ErrorState } from "../../components/ui";
import { SearchSvg } from "../../components/svg/Icons";
export function SearchPage() {
  const [params, setParams] = useSearchParams();
  const [input, setInput] = useState(params.get("q") || "");
  const [term, setTerm] = useState(input);
  const nav = useNavigate();
  useEffect(() => {
    const t = setTimeout(() => {
      setTerm(input.trim());
      setParams(input.trim() ? { q: input.trim() } : {}, { replace: true });
    }, 300);
    return () => clearTimeout(t);
  }, [input, setParams]);
  const restaurants = useQuery({
    queryKey: ["search", "restaurants", term],
    queryFn: () => catalogueApi.searchRestaurants(term),
    enabled: term.length > 0,
  });
  const foods = useQuery({
    queryKey: ["search", "foods", term],
    queryFn: () => catalogueApi.searchFood(term),
    enabled: term.length > 0,
  });
  return (
    <main className="container page">
      <div className="page-heading">
        <p className="kicker">Search everything</p>
        <h1>What sounds good?</h1>
      </div>
      <label className="search-box">
        <SearchSvg />
        <span className="sr-only">Search</span>
        <input
          autoFocus
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="Restaurant or dish"
        />
      </label>
      {!term ? (
        <EmptyState
          title="Start with a craving"
          description="Search dishes and restaurants from the live catalogue."
        />
      ) : (
        <>
          {restaurants.isError || foods.isError ? (
            <ErrorState error={restaurants.error || foods.error} />
          ) : null}
          <section className="section compact">
            <h2>Restaurants</h2>
            {restaurants.data?.length ? (
              <div className="card-grid">
                {restaurants.data.map((r) => (
                  <RestaurantCard key={r.id} restaurant={r} />
                ))}
              </div>
            ) : (
              !restaurants.isLoading && (
                <p className="muted">No restaurants match.</p>
              )
            )}
          </section>
          <section className="section compact">
            <h2>Dishes</h2>
            {foods.data?.length ? (
              <div className="food-grid">
                {foods.data.map((f) => (
                  <FoodCard
                    key={f.id}
                    food={f}
                    onClick={() => nav(`/restaurants/${f.restaurant.id}`)}
                  />
                ))}
              </div>
            ) : (
              !foods.isLoading && <p className="muted">No dishes match.</p>
            )}
          </section>
        </>
      )}
    </main>
  );
}
