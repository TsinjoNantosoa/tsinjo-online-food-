import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Link, useLocation } from "react-router-dom";
import { adminApi, catalogueApi } from "../../api";
import {
  Button,
  EmptyState,
  ErrorState,
  Input,
  Modal,
  PageLoader,
  SafeImage,
  Status,
  Textarea,
} from "../../components/ui";
import type {
  CreateFoodRequest,
  CreateRestaurantRequest,
  RestaurantResponse,
} from "../../types/api";
import { formatPrice } from "../../utils/format";
import {
  CategoryManager,
  IngredientManager,
  RestaurantOrders,
} from "./OperationsSections";
const emptyRestaurant: CreateRestaurantRequest = {
  name: "",
  description: "",
  cuisineType: "",
  address: {
    streetAddress: "",
    city: "",
    state: "",
    postalCode: "",
    country: "",
  },
  contactInformation: { email: "", mobile: "", twitter: "", instagram: "" },
  openingHours: "",
  images: [],
};
function splitUrls(value: string) {
  return value
    .split(/[,\n]/)
    .map((v) => v.trim())
    .filter(Boolean);
}
export function OwnerPage() {
  const restaurant = useQuery({
    queryKey: ["ownerRestaurant"],
    queryFn: adminApi.ownerRestaurant,
    retry: false,
  });
  if (restaurant.isLoading)
    return (
      <ManagementShell owner>
        <PageLoader />
      </ManagementShell>
    );
  if (
    restaurant.isError &&
    (restaurant.error as { status?: number }).status === 404
  )
    return (
      <ManagementShell owner>
        <RestaurantEditor />
      </ManagementShell>
    );
  if (restaurant.isError)
    return (
      <ManagementShell owner>
        <ErrorState error={restaurant.error} />
      </ManagementShell>
    );
  return (
    <ManagementShell owner>
      <Workspace restaurant={restaurant.data!} canEditRestaurant />
    </ManagementShell>
  );
}
export function AdminPage() {
  const restaurants = useQuery({
    queryKey: ["restaurants"],
    queryFn: catalogueApi.restaurants,
  });
  const [selected, setSelected] = useState<number>(0);
  const restaurant = restaurants.data?.find(
    (r) => r.id === (selected || restaurants.data?.[0]?.id),
  );
  if (restaurants.isLoading)
    return (
      <ManagementShell>
        <PageLoader />
      </ManagementShell>
    );
  return (
    <ManagementShell>
      <div className="admin-select">
        <div>
          <p className="kicker">Supported administration</p>
          <h1>Restaurant operations</h1>
          <p>
            Select a public restaurant to manage only the resources exposed by
            the backend.
          </p>
        </div>
        <label>
          <span>Restaurant</span>
          <select
            value={restaurant?.id ?? ""}
            onChange={(e) => setSelected(Number(e.target.value))}
          >
            {restaurants.data?.map((r) => (
              <option key={r.id} value={r.id}>
                {r.name}
              </option>
            ))}
          </select>
        </label>
      </div>
      {restaurant ? (
        <Workspace restaurant={restaurant} canEditRestaurant />
      ) : (
        <EmptyState
          title="No restaurants"
          description="No public restaurants are currently available."
        />
      )}
    </ManagementShell>
  );
}
function ManagementShell({
  owner = false,
  children,
}: {
  owner?: boolean;
  children: React.ReactNode;
}) {
  const location = useLocation();
  const base = owner ? "/owner" : "/admin";
  return (
    <main className="management">
      <aside className="management-nav">
        <Link className="brand inverse" to="/">
          <span className="brand-mark">T</span>Tsinjo
        </Link>
        <nav>
          {[
            "Overview",
            "Restaurant",
            "Menu",
            "Categories",
            "Ingredients",
            "Orders",
          ].map((x) => (
            <a
              className={
                location.hash === `#${x.toLowerCase()}` ? "active" : ""
              }
              href={`${base}#${x.toLowerCase()}`}
              key={x}
            >
              {x}
            </a>
          ))}
        </nav>
        <Link to="/">View catalogue</Link>
      </aside>
      <div className="management-main">{children}</div>
    </main>
  );
}
function Workspace({
  restaurant,
  canEditRestaurant,
}: {
  restaurant: RestaurantResponse;
  canEditRestaurant: boolean;
}) {
  const location = useLocation();
  const pathSection = location.pathname.split("/").filter(Boolean).at(-1);
  const supportedSections = [
    "restaurant",
    "menu",
    "categories",
    "ingredients",
    "orders",
  ];
  const section =
    location.hash.slice(1) ||
    (pathSection && supportedSections.includes(pathSection)
      ? pathSection
      : "overview");
  const foods = useQuery({
    queryKey: ["foods", restaurant.id, "management"],
    queryFn: () => catalogueApi.foods(restaurant.id),
  });
  const orders = useQuery({
    queryKey: ["restaurantOrders", restaurant.id],
    queryFn: () => adminApi.orders(restaurant.id),
  });
  const revenue =
    orders.data
      ?.filter((o) => o.status !== "CANCELLED")
      .reduce((sum, o) => sum + o.totalAmount, 0) ?? 0;
  if (section === "restaurant")
    return <RestaurantEditor restaurant={restaurant} />;
  if (section === "menu") return <FoodManager restaurant={restaurant} />;
  if (section === "categories")
    return <CategoryManager restaurant={restaurant} />;
  if (section === "ingredients")
    return <IngredientManager restaurant={restaurant} />;
  if (section === "orders") return <RestaurantOrders restaurant={restaurant} />;
  return (
    <>
      <div className="dashboard-head">
        <div>
          <p className="kicker">Operational overview</p>
          <h1>{restaurant.name}</h1>
          <p>
            {restaurant.cuisineType} · {restaurant.address.city}
          </p>
        </div>
        <Status kind={restaurant.open ? "success" : "neutral"}>
          {restaurant.open ? "Open" : "Closed"}
        </Status>
      </div>
      <div className="kpi-grid">
        <article>
          <span>Menu items</span>
          <strong>{foods.data?.length ?? "—"}</strong>
        </article>
        <article>
          <span>Orders</span>
          <strong>{orders.data?.length ?? "—"}</strong>
        </article>
        <article>
          <span>Recorded revenue</span>
          <strong>{orders.data ? formatPrice(revenue) : "—"}</strong>
        </article>
        <article>
          <span>Status</span>
          <strong>{restaurant.open ? "Open" : "Closed"}</strong>
        </article>
      </div>
      <div className="detail-card">
        <h2>Today’s control room</h2>
        <p>
          Use the navigation to manage the restaurant, menu, ingredients and
          real orders.
        </p>
        {canEditRestaurant && (
          <Link className="button secondary md" to="#restaurant">
            Edit restaurant
          </Link>
        )}
      </div>
    </>
  );
}
function RestaurantEditor({ restaurant }: { restaurant?: RestaurantResponse }) {
  const qc = useQueryClient();
  const [form, setForm] = useState<CreateRestaurantRequest>(
    restaurant
      ? {
          name: restaurant.name,
          description: restaurant.description,
          cuisineType: restaurant.cuisineType,
          address: {
            streetAddress: restaurant.address.streetAddress,
            city: restaurant.address.city,
            state: restaurant.address.state,
            postalCode: restaurant.address.postalCode,
            country: restaurant.address.country,
          },
          contactInformation:
            restaurant.contactInformation ?? emptyRestaurant.contactInformation,
          openingHours: restaurant.openingHours,
          images: restaurant.images,
        }
      : emptyRestaurant,
  );
  const save = useMutation({
    mutationFn: () =>
      restaurant
        ? adminApi.updateRestaurant(restaurant.id, form)
        : adminApi.createRestaurant(form),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["ownerRestaurant"] });
      qc.invalidateQueries({ queryKey: ["restaurants"] });
    },
  });
  const toggle = useMutation({
    mutationFn: () => adminApi.toggleRestaurant(restaurant!.id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["ownerRestaurant"] }),
  });
  const field = (key: keyof CreateRestaurantRequest, value: string) =>
    setForm((f) => ({ ...f, [key]: value }));
  return (
    <section>
      <div className="dashboard-head">
        <div>
          <p className="kicker">Restaurant</p>
          <h1>
            {restaurant ? "Restaurant details" : "Create your restaurant"}
          </h1>
        </div>
        {restaurant && (
          <Button
            variant="secondary"
            onClick={() => toggle.mutate()}
            loading={toggle.isPending}
          >
            {restaurant.open ? "Close restaurant" : "Open restaurant"}
          </Button>
        )}
      </div>
      <form
        className="form-card management-form"
        onSubmit={(e) => {
          e.preventDefault();
          save.mutate();
        }}
      >
        <Input
          label="Name"
          value={form.name}
          onChange={(e) => field("name", e.target.value)}
          required
        />
        <Input
          label="Cuisine type"
          value={form.cuisineType}
          onChange={(e) => field("cuisineType", e.target.value)}
          required
        />
        <Textarea
          label="Description"
          value={form.description}
          onChange={(e) => field("description", e.target.value)}
          required
        />
        <Input
          label="Opening hours"
          value={form.openingHours}
          onChange={(e) => field("openingHours", e.target.value)}
          required
        />
        <Textarea
          label="Image URLs (comma or line separated)"
          value={form.images.join("\n")}
          onChange={(e) =>
            setForm((f) => ({ ...f, images: splitUrls(e.target.value) }))
          }
        />
        <h2>Address</h2>
        {(
          ["streetAddress", "city", "state", "postalCode", "country"] as const
        ).map((k) => (
          <Input
            key={k}
            label={k.replace(/([A-Z])/g, " $1")}
            value={form.address[k] ?? ""}
            required={k === "streetAddress" || k === "city" || k === "country"}
            onChange={(e) =>
              setForm((f) => ({
                ...f,
                address: { ...f.address, [k]: e.target.value },
              }))
            }
          />
        ))}
        <h2>Contact</h2>
        {(["email", "mobile", "twitter", "instagram"] as const).map((k) => (
          <Input
            key={k}
            label={k}
            value={form.contactInformation[k] ?? ""}
            onChange={(e) =>
              setForm((f) => ({
                ...f,
                contactInformation: {
                  ...f.contactInformation,
                  [k]: e.target.value,
                },
              }))
            }
          />
        ))}
        {save.error && <p className="form-alert">{save.error.message}</p>}
        <Button type="submit" loading={save.isPending}>
          {restaurant ? "Save changes" : "Create restaurant"}
        </Button>
      </form>
    </section>
  );
}
function FoodManager({ restaurant }: { restaurant: RestaurantResponse }) {
  const qc = useQueryClient();
  const foods = useQuery({
    queryKey: ["foods", restaurant.id, "management"],
    queryFn: () => catalogueApi.foods(restaurant.id),
  });
  const categories = useQuery({
    queryKey: ["categories", restaurant.id],
    queryFn: () => catalogueApi.categories(restaurant.id),
  });
  const ingredients = useQuery({
    queryKey: ["ingredients", restaurant.id],
    queryFn: () => adminApi.ingredients(restaurant.id),
  });
  const [editing, setEditing] = useState<number | null>(null);
  const [open, setOpen] = useState(false);
  const [confirmDelete, setConfirmDelete] = useState<number | null>(null);
  const invalidate = () =>
    qc.invalidateQueries({ queryKey: ["foods", restaurant.id] });
  const toggle = useMutation({
    mutationFn: adminApi.toggleFood,
    onSuccess: invalidate,
  });
  const remove = useMutation({
    mutationFn: adminApi.deleteFood,
    onSuccess: () => {
      setConfirmDelete(null);
      invalidate();
    },
  });
  const current = foods.data?.find((f) => f.id === editing);
  return (
    <section>
      <div className="dashboard-head">
        <div>
          <p className="kicker">Menu</p>
          <h1>Food management</h1>
        </div>
        <Button
          onClick={() => {
            setEditing(null);
            setOpen(true);
          }}
        >
          Create food
        </Button>
      </div>
      {foods.isError ? (
        <ErrorState error={foods.error} />
      ) : foods.data?.length ? (
        <div className="management-list">
          {foods.data.map((f) => (
            <article key={f.id}>
              <SafeImage src={f.images[0]} alt={f.name} />
              <div>
                <h2>{f.name}</h2>
                <p>
                  {f.category.name} · {formatPrice(f.price)}
                </p>
              </div>
              <Status kind={f.available ? "success" : "neutral"}>
                {f.available ? "Available" : "Unavailable"}
              </Status>
              <div className="row-actions">
                <Button
                  size="sm"
                  variant="ghost"
                  onClick={() => {
                    setEditing(f.id);
                    setOpen(true);
                  }}
                >
                  Edit
                </Button>
                <Button
                  size="sm"
                  variant="secondary"
                  onClick={() => toggle.mutate(f.id)}
                >
                  Toggle availability
                </Button>
                <Button
                  size="sm"
                  variant="danger"
                  onClick={() => setConfirmDelete(f.id)}
                >
                  Delete
                </Button>
              </div>
            </article>
          ))}
        </div>
      ) : (
        <EmptyState
          title="No food found"
          description="Create the first menu item for this restaurant."
        />
      )}
      <Modal
        title={current ? "Edit food" : "Create food"}
        open={open}
        onClose={() => setOpen(false)}
      >
        <FoodForm
          restaurant={restaurant}
          current={current}
          categories={categories.data ?? []}
          ingredients={ingredients.data ?? []}
          onDone={() => {
            setOpen(false);
            invalidate();
          }}
        />
      </Modal>
      <Modal
        title="Delete this dish?"
        open={confirmDelete !== null}
        onClose={() => setConfirmDelete(null)}
      >
        <p>This permanently removes the dish from the restaurant menu.</p>
        {remove.error && <p className="form-alert">{remove.error.message}</p>}
        <div className="row-actions">
          <Button variant="ghost" onClick={() => setConfirmDelete(null)}>
            Keep dish
          </Button>
          <Button
            variant="danger"
            loading={remove.isPending}
            onClick={() =>
              confirmDelete !== null && remove.mutate(confirmDelete)
            }
          >
            Delete permanently
          </Button>
        </div>
      </Modal>
    </section>
  );
}
function FoodForm({
  restaurant,
  current,
  categories,
  ingredients,
  onDone,
}: {
  restaurant: RestaurantResponse;
  current?: import("../../types/api").FoodResponse;
  categories: import("../../types/api").CategorySummary[];
  ingredients: import("../../types/api").IngredientItemResponse[];
  onDone: () => void;
}) {
  const [form, setForm] = useState<CreateFoodRequest>({
    name: current?.name ?? "",
    description: current?.description ?? "",
    price: current?.price ?? 0,
    restaurantId: restaurant.id,
    categoryId: current?.category.id ?? categories[0]?.id ?? 0,
    ingredientIds: current?.ingredients.map((i) => i.id) ?? [],
    images: current?.images ?? [],
    vegetarian: current?.vegetarian ?? false,
    seasonal: current?.seasonal ?? false,
  });
  const save = useMutation({
    mutationFn: () =>
      current
        ? adminApi.updateFood(current.id, form)
        : adminApi.createFood(form),
    onSuccess: onDone,
  });
  return (
    <form
      className="modal-form"
      onSubmit={(e) => {
        e.preventDefault();
        save.mutate();
      }}
    >
      <Input
        label="Name"
        value={form.name}
        onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
        required
      />
      <Textarea
        label="Description"
        value={form.description}
        onChange={(e) =>
          setForm((f) => ({ ...f, description: e.target.value }))
        }
      />
      <Input
        label="Price (Ar)"
        type="number"
        min="1"
        value={form.price || ""}
        onChange={(e) =>
          setForm((f) => ({ ...f, price: Number(e.target.value) }))
        }
        required
      />
      <label className="field">
        <span>Category</span>
        <select
          value={form.categoryId}
          onChange={(e) =>
            setForm((f) => ({ ...f, categoryId: Number(e.target.value) }))
          }
        >
          {categories.map((c) => (
            <option key={c.id} value={c.id}>
              {c.name}
            </option>
          ))}
        </select>
      </label>
      <fieldset>
        <legend>Ingredients</legend>
        {ingredients.map((i) => (
          <label className="check-row" key={i.id}>
            <input
              type="checkbox"
              checked={form.ingredientIds.includes(i.id)}
              onChange={() =>
                setForm((f) => ({
                  ...f,
                  ingredientIds: f.ingredientIds.includes(i.id)
                    ? f.ingredientIds.filter((x) => x !== i.id)
                    : [...f.ingredientIds, i.id],
                }))
              }
            />
            {i.name}
          </label>
        ))}
      </fieldset>
      <Textarea
        label="Image URLs"
        value={form.images.join("\n")}
        onChange={(e) =>
          setForm((f) => ({ ...f, images: splitUrls(e.target.value) }))
        }
      />
      <label className="check-row">
        <input
          type="checkbox"
          checked={form.vegetarian}
          onChange={(e) =>
            setForm((f) => ({ ...f, vegetarian: e.target.checked }))
          }
        />
        Vegetarian
      </label>
      <label className="check-row">
        <input
          type="checkbox"
          checked={form.seasonal}
          onChange={(e) =>
            setForm((f) => ({ ...f, seasonal: e.target.checked }))
          }
        />
        Seasonal
      </label>
      {save.error && <p className="form-alert">{save.error.message}</p>}
      <Button type="submit" loading={save.isPending}>
        Save food
      </Button>
    </form>
  );
}
