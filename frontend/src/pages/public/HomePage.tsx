import { useQuery } from "@tanstack/react-query";
import { Link, useNavigate } from "react-router-dom";
import { catalogueApi } from "../../api";
import { FoodCard, RestaurantCard } from "../../components/cards";
import { ArrowSvg, SearchSvg } from "../../components/svg/Icons";
import { ErrorState } from "../../components/ui";
export function HomePage() {
  const nav = useNavigate();
  const restaurants = useQuery({
    queryKey: ["restaurants"],
    queryFn: catalogueApi.restaurants,
  });
  const first = restaurants.data?.[0];
  const foods = useQuery({
    queryKey: ["foods", first?.id, "home"],
    queryFn: () => catalogueApi.foods(first!.id),
    enabled: Boolean(first),
  });
  return (
    <main>
      <section className="hero container">
        <div className="hero-copy">
          <p className="kicker">Local kitchens. Remarkable meals.</p>
          <h1>Food worth slowing down for. Delivery that doesn’t.</h1>
          <p>
            Discover independent restaurants and customize every plate, from the
            first ingredient to the final bite.
          </p>
          <div className="hero-actions">
            <Link className="button primary lg" to="/restaurants">
              Explore restaurants <ArrowSvg />
            </Link>
            <Link className="button secondary lg" to="/search">
              <SearchSvg /> Search food
            </Link>
          </div>
        </div>
        <div className="hero-art" aria-hidden="true">
          <div className="plate">
            <span />
            <i />
            <b />
          </div>
          <p>
            Made nearby
            <br />
            <strong>Delivered warmly</strong>
          </p>
        </div>
      </section>
      <section className="container section">
        <div className="section-heading">
          <div>
            <p className="kicker">Around you</p>
            <h2>Restaurants worth knowing</h2>
          </div>
          <Link to="/restaurants">
            View all <ArrowSvg />
          </Link>
        </div>
        {restaurants.isError ? (
          <ErrorState error={restaurants.error} />
        ) : (
          <div className="card-grid">
            {restaurants.data?.slice(0, 3).map((r) => (
              <RestaurantCard key={r.id} restaurant={r} />
            ))}
          </div>
        )}
      </section>
      <section className="how">
        <div className="container section">
          <p className="kicker">Simple by design</p>
          <h2>From craving to table</h2>
          <div className="steps">
            {[
              ["01", "Choose", "Explore real menus from local restaurants."],
              ["02", "Customize", "Pick ingredients and make the meal yours."],
              ["03", "Enjoy", "Place your order and follow its status."],
            ].map((x) => (
              <article key={x[0]}>
                <b>{x[0]}</b>
                <h3>{x[1]}</h3>
                <p>{x[2]}</p>
              </article>
            ))}
          </div>
        </div>
      </section>
      {foods.data?.length ? (
        <section className="container section">
          <div className="section-heading">
            <div>
              <p className="kicker">From the menu</p>
              <h2>Featured dishes</h2>
            </div>
          </div>
          <div className="food-grid">
            {foods.data.slice(0, 4).map((f) => (
              <FoodCard
                key={f.id}
                food={f}
                onClick={() => nav(`/restaurants/${f.restaurant.id}`)}
              />
            ))}
          </div>
        </section>
      ) : null}
      <section className="container final-cta">
        <div>
          <p className="kicker">Dinner is closer than it feels</p>
          <h2>Meet your next favorite kitchen.</h2>
        </div>
        <Link className="button primary lg" to="/restaurants">
          Start exploring <ArrowSvg />
        </Link>
      </section>
    </main>
  );
}
