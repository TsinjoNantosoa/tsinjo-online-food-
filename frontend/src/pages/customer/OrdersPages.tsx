import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Link, useParams, useSearchParams } from "react-router-dom";
import { orderApi } from "../../api";
import {
  EmptyState,
  ErrorState,
  PageLoader,
  Status,
} from "../../components/ui";
import type { OrderStatus } from "../../types/api";
import { formatDateTime, formatPrice, parseId } from "../../utils/format";
const statuses: OrderStatus[] = [
  "PENDING",
  "OUT_FOR_DELIVERY",
  "DELIVERED",
  "COMPLETED",
  "CANCELLED",
];
const kind = (s: OrderStatus) =>
  s === "CANCELLED"
    ? "danger"
    : s === "COMPLETED" || s === "DELIVERED"
      ? "success"
      : s === "PENDING"
        ? "warning"
        : "neutral";
export const statusLabel = (s: OrderStatus) =>
  s
    .toLowerCase()
    .replaceAll("_", " ")
    .replace(/^./, (x) => x.toUpperCase());
export function OrderStatusBadge({ status }: { status: OrderStatus }) {
  return <Status kind={kind(status)}>{statusLabel(status)}</Status>;
}
export function OrdersPage() {
  const [filter, setFilter] = useState<OrderStatus | "ALL">("ALL");
  const query = useQuery({ queryKey: ["orders"], queryFn: orderApi.list });
  if (query.isLoading) return <PageLoader />;
  if (query.isError)
    return (
      <main className="container page">
        <ErrorState error={query.error} />
      </main>
    );
  const orders =
    query.data?.filter((o) => filter === "ALL" || o.status === filter) ?? [];
  return (
    <main className="container page">
      <div className="page-heading">
        <p className="kicker">Your history</p>
        <h1>Orders</h1>
      </div>
      <div className="tabs">
        <button
          className={filter === "ALL" ? "active" : ""}
          onClick={() => setFilter("ALL")}
        >
          All
        </button>
        {statuses.map((s) => (
          <button
            className={filter === s ? "active" : ""}
            onClick={() => setFilter(s)}
            key={s}
          >
            {statusLabel(s)}
          </button>
        ))}
      </div>
      {orders.length ? (
        <div className="order-list">
          {orders.map((o) => (
            <Link to={`/orders/${o.id}`} className="order-card" key={o.id}>
              <div>
                <small>{formatDateTime(o.createdAt)}</small>
                <h2>{o.restaurant.name}</h2>
                <p>
                  Order #{o.id} · {o.totalItems} items
                </p>
              </div>
              <div>
                <OrderStatusBadge status={o.status} />
                <strong>{formatPrice(o.totalAmount)}</strong>
              </div>
            </Link>
          ))}
        </div>
      ) : (
        <EmptyState
          title="No orders yet"
          description="Completed and active orders will appear here."
        />
      )}
    </main>
  );
}
export function OrderDetailPage() {
  const id = parseId(useParams().id);
  const [params] = useSearchParams();
  const query = useQuery({
    queryKey: ["order", id],
    queryFn: () => orderApi.get(id),
    enabled: id > 0,
  });
  if (query.isLoading) return <PageLoader />;
  if (query.isError)
    return (
      <main className="container page">
        <ErrorState error={query.error} />
      </main>
    );
  const o = query.data!;
  const address = o.deliveryAddress;
  return (
    <main className="container page narrow">
      {params.get("placed") && (
        <div className="success-banner">
          <strong>Order placed successfully.</strong>
          <span>The restaurant has received your order.</span>
        </div>
      )}
      <div className="order-detail-head">
        <div>
          <p className="kicker">Order #{o.id}</p>
          <h1>{o.restaurant.name}</h1>
          <p>{formatDateTime(o.createdAt)}</p>
        </div>
        <OrderStatusBadge status={o.status} />
      </div>
      <section className="timeline" aria-label="Order progress">
        {statuses
          .filter((s) => s !== "CANCELLED")
          .map((s, index) => {
            const current = statuses.indexOf(o.status);
            return (
              <div
                className={
                  o.status !== "CANCELLED" && index <= current ? "done" : ""
                }
                key={s}
              >
                <i />
                <span>{statusLabel(s)}</span>
              </div>
            );
          })}
      </section>
      <div className="detail-grid">
        <section className="detail-card">
          <h2>Items</h2>
          {o.items.map((i) => (
            <div className="order-item" key={i.id}>
              <div>
                <strong>
                  {i.quantity} × {i.foodName}
                </strong>
                {i.ingredients.length > 0 && (
                  <small>{i.ingredients.join(", ")}</small>
                )}
              </div>
              <span>{formatPrice(i.totalPrice)}</span>
            </div>
          ))}
          <div className="summary-total">
            <span>Total</span>
            <strong>{formatPrice(o.totalAmount)}</strong>
          </div>
        </section>
        <aside className="detail-card">
          <h2>Delivery</h2>
          <address>
            {address.streetAddress}
            <br />
            {address.city}
            {address.state ? `, ${address.state}` : ""}
            <br />
            {address.postalCode} {address.country}
          </address>
          <h3>Customer</h3>
          <p>
            {o.customer.fullName}
            <br />
            {o.customer.email}
          </p>
        </aside>
      </div>
    </main>
  );
}
