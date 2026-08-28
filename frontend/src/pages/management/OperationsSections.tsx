import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { adminApi, catalogueApi } from "../../api";
import {
  Button,
  EmptyState,
  ErrorState,
  Input,
  Status,
} from "../../components/ui";
import type {
  OrderResponse,
  OrderStatus,
  RestaurantResponse,
} from "../../types/api";
import { formatDateTime, formatPrice } from "../../utils/format";
import { OrderStatusBadge, statusLabel } from "../customer/OrdersPages";
const statuses: OrderStatus[] = [
  "PENDING",
  "OUT_FOR_DELIVERY",
  "DELIVERED",
  "COMPLETED",
  "CANCELLED",
];
export function CategoryManager({
  restaurant,
}: {
  restaurant: RestaurantResponse;
}) {
  const qc = useQueryClient();
  const [name, setName] = useState("");
  const categories = useQuery({
    queryKey: ["categories", restaurant.id],
    queryFn: () => catalogueApi.categories(restaurant.id),
  });
  const foods = useQuery({
    queryKey: ["foods", restaurant.id, "category-count"],
    queryFn: () => catalogueApi.foods(restaurant.id),
  });
  const add = useMutation({
    mutationFn: adminApi.createCategory,
    onSuccess: () => {
      setName("");
      qc.invalidateQueries({ queryKey: ["categories", restaurant.id] });
    },
  });
  return (
    <section>
      <div className="dashboard-head">
        <div>
          <p className="kicker">Taxonomy</p>
          <h1>Menu categories</h1>
        </div>
      </div>
      <form
        className="inline-form"
        onSubmit={(e) => {
          e.preventDefault();
          if (name.trim())
            add.mutate({ name: name.trim(), restaurantId: restaurant.id });
        }}
      >
        <Input
          label="New category"
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
        />
        <Button type="submit" loading={add.isPending}>
          Add category
        </Button>
      </form>
      {add.error && <p className="form-alert">{add.error.message}</p>}
      {categories.data?.length ? (
        <div className="management-list simple">
          {categories.data.map((c) => (
            <article key={c.id}>
              <div>
                <h2>{c.name}</h2>
                <p>
                  {foods.data?.filter((f) => f.category.id === c.id).length ??
                    "—"}{" "}
                  menu items
                </p>
              </div>
            </article>
          ))}
        </div>
      ) : (
        !categories.isLoading && (
          <EmptyState
            title="No categories"
            description="Create a category before adding food."
          />
        )
      )}
    </section>
  );
}
export function IngredientManager({
  restaurant,
}: {
  restaurant: RestaurantResponse;
}) {
  const qc = useQueryClient();
  const ingredients = useQuery({
    queryKey: ["ingredients", restaurant.id],
    queryFn: () => adminApi.ingredients(restaurant.id),
  });
  const categories = useQuery({
    queryKey: ["ingredientCategories", restaurant.id],
    queryFn: () => adminApi.ingredientCategories(restaurant.id),
  });
  const [categoryName, setCategoryName] = useState("");
  const [ingredientName, setIngredientName] = useState("");
  const [categoryId, setCategoryId] = useState(0);
  const refresh = () => {
    qc.invalidateQueries({ queryKey: ["ingredients", restaurant.id] });
    qc.invalidateQueries({ queryKey: ["ingredientCategories", restaurant.id] });
  };
  const addCategory = useMutation({
    mutationFn: adminApi.createIngredientCategory,
    onSuccess: () => {
      setCategoryName("");
      refresh();
    },
  });
  const addIngredient = useMutation({
    mutationFn: adminApi.createIngredient,
    onSuccess: () => {
      setIngredientName("");
      refresh();
    },
  });
  const stock = useMutation({
    mutationFn: adminApi.toggleStock,
    onSuccess: refresh,
  });
  return (
    <section>
      <div className="dashboard-head">
        <div>
          <p className="kicker">Customization</p>
          <h1>Ingredients</h1>
        </div>
      </div>
      <div className="two-forms">
        <form
          className="form-card"
          onSubmit={(e) => {
            e.preventDefault();
            addCategory.mutate({
              name: categoryName,
              restaurantId: restaurant.id,
            });
          }}
        >
          <h2>Ingredient category</h2>
          <Input
            label="Name"
            value={categoryName}
            onChange={(e) => setCategoryName(e.target.value)}
            required
          />
          <Button loading={addCategory.isPending}>Create category</Button>
        </form>
        <form
          className="form-card"
          onSubmit={(e) => {
            e.preventDefault();
            addIngredient.mutate({
              name: ingredientName,
              categoryId: categoryId || categories.data?.[0]?.id || 0,
              restaurantId: restaurant.id,
            });
          }}
        >
          <h2>New ingredient</h2>
          <Input
            label="Name"
            value={ingredientName}
            onChange={(e) => setIngredientName(e.target.value)}
            required
          />
          <label className="field">
            <span>Category</span>
            <select
              value={categoryId || categories.data?.[0]?.id || 0}
              onChange={(e) => setCategoryId(Number(e.target.value))}
            >
              {categories.data?.map((c) => (
                <option value={c.id} key={c.id}>
                  {c.name}
                </option>
              ))}
            </select>
          </label>
          <Button
            disabled={!categories.data?.length}
            loading={addIngredient.isPending}
          >
            Create ingredient
          </Button>
        </form>
      </div>
      {ingredients.isError ? (
        <ErrorState error={ingredients.error} />
      ) : ingredients.data?.length ? (
        <div className="management-list simple">
          {ingredients.data.map((i) => (
            <article key={i.id}>
              <div>
                <h2>{i.name}</h2>
                <p>{i.categoryName}</p>
              </div>
              <Status kind={i.inStock ? "success" : "neutral"}>
                {i.inStock ? "In stock" : "Out of stock"}
              </Status>
              <Button
                size="sm"
                variant="secondary"
                onClick={() => stock.mutate(i.id)}
              >
                Toggle stock
              </Button>
            </article>
          ))}
        </div>
      ) : (
        !ingredients.isLoading && (
          <EmptyState
            title="No ingredients"
            description="Create ingredient categories and their options."
          />
        )
      )}
    </section>
  );
}
export function RestaurantOrders({
  restaurant,
}: {
  restaurant: RestaurantResponse;
}) {
  const qc = useQueryClient();
  const [filter, setFilter] = useState<OrderStatus | undefined>();
  const [selected, setSelected] = useState<OrderResponse | null>(null);
  const orders = useQuery({
    queryKey: ["restaurantOrders", restaurant.id, filter],
    queryFn: () => adminApi.orders(restaurant.id, filter),
  });
  const update = useMutation({
    mutationFn: ({ id, status }: { id: number; status: OrderStatus }) =>
      adminApi.updateOrderStatus(id, status),
    onSuccess: (r) => {
      setSelected(r);
      qc.invalidateQueries({ queryKey: ["restaurantOrders", restaurant.id] });
    },
  });
  return (
    <section>
      <div className="dashboard-head">
        <div>
          <p className="kicker">Fulfilment</p>
          <h1>Restaurant orders</h1>
        </div>
      </div>
      <div className="tabs">
        <button
          className={!filter ? "active" : ""}
          onClick={() => setFilter(undefined)}
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
      {orders.isError ? (
        <ErrorState error={orders.error} />
      ) : orders.data?.length ? (
        <div className="orders-workspace">
          <div className="management-list simple">
            {orders.data.map((o) => (
              <button
                className="admin-order"
                onClick={() => setSelected(o)}
                key={o.id}
              >
                <div>
                  <strong>Order #{o.id}</strong>
                  <small>
                    {formatDateTime(o.createdAt)} · {o.customer.fullName}
                  </small>
                </div>
                <OrderStatusBadge status={o.status} />
                <strong>{formatPrice(o.totalAmount)}</strong>
              </button>
            ))}
          </div>
          {selected && (
            <aside className="order-panel">
              <h2>Order #{selected.id}</h2>
              <p>
                {selected.customer.fullName}
                <br />
                {selected.deliveryAddress.streetAddress},{" "}
                {selected.deliveryAddress.city}
              </p>
              {selected.items.map((i) => (
                <div key={i.id}>
                  {i.quantity} × {i.foodName}
                  <span>{formatPrice(i.totalPrice)}</span>
                </div>
              ))}
              <label className="field">
                <span>Update status</span>
                <select
                  value={selected.status}
                  onChange={(e) =>
                    update.mutate({
                      id: selected.id,
                      status: e.target.value as OrderStatus,
                    })
                  }
                >
                  {statuses.map((s) => (
                    <option value={s} key={s}>
                      {statusLabel(s)}
                    </option>
                  ))}
                </select>
              </label>
            </aside>
          )}
        </div>
      ) : (
        !orders.isLoading && (
          <EmptyState
            title="No orders"
            description="No restaurant orders match this status."
          />
        )
      )}
    </section>
  );
}
