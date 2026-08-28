import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { catalogueApi } from "../../api";
import { RestaurantCard } from "../../components/cards";
import { EmptyState, ErrorState, PageLoader } from "../../components/ui";
import { SearchSvg } from "../../components/svg/Icons";
export function RestaurantsPage() {
  const [term, setTerm] = useState("");
  const query = useQuery({
    queryKey: ["restaurants", term],
    queryFn: () =>
      term.trim()
        ? catalogueApi.searchRestaurants(term.trim())
        : catalogueApi.restaurants(),
  });
  return (
    <main className="container page">
      <div className="page-heading">
        <p className="kicker">The local directory</p>
        <h1>Find your table, delivered.</h1>
        <p>Browse kitchens serving Antananarivo and beyond.</p>
      </div>
      <label className="search-box">
        <SearchSvg />
        <span className="sr-only">Search restaurants</span>
        <input
          value={term}
          onChange={(e) => setTerm(e.target.value)}
          placeholder="Search by restaurant or cuisine"
        />
      </label>
      {query.isLoading ? (
        <PageLoader />
      ) : query.isError ? (
        <ErrorState error={query.error} retry={() => query.refetch()} />
      ) : query.data?.length ? (
        <>
          <p className="result-count">
            {query.data.length} restaurant{query.data.length === 1 ? "" : "s"}
          </p>
          <div className="card-grid">
            {query.data.map((r) => (
              <RestaurantCard key={r.id} restaurant={r} />
            ))}
          </div>
        </>
      ) : (
        <EmptyState
          title="No restaurants found"
          description="Try another name or browse the full directory."
        />
      )}
    </main>
  );
}
