import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Link, useNavigate } from "react-router-dom";
import { cartApi, orderApi } from "../../api";
import { Button, EmptyState, Input, PageLoader } from "../../components/ui";
import { formatPrice } from "../../utils/format";
import type { AddressRequest } from "../../types/api";
const schema = z.object({
  streetAddress: z.string().min(3),
  city: z.string().min(2),
  state: z.string().max(120).nullable(),
  postalCode: z.string().max(30).nullable(),
  country: z.string().min(2),
});
export const buildOrderRequest = (
  restaurantId: number,
  deliveryAddress: AddressRequest,
) => ({ restaurantId, deliveryAddress });
export function CheckoutPage() {
  const cart = useQuery({ queryKey: ["cart"], queryFn: cartApi.get });
  const nav = useNavigate();
  const qc = useQueryClient();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<AddressRequest>({
    resolver: zodResolver(schema),
    defaultValues: {
      streetAddress: "",
      city: "",
      state: "",
      postalCode: "",
      country: "",
    },
  });
  const order = useMutation({
    mutationFn: orderApi.create,
    onSuccess: (r) => {
      qc.invalidateQueries({ queryKey: ["cart"] });
      qc.invalidateQueries({ queryKey: ["orders"] });
      nav(`/orders/${r.id}?placed=1`);
    },
  });
  if (cart.isLoading) return <PageLoader />;
  if (!cart.data?.items.length)
    return (
      <main className="container page">
        <EmptyState
          title="Nothing to check out"
          description="Your cart is empty."
        />
      </main>
    );
  const restaurantId = Number(sessionStorage.getItem("tsinjo_restaurant_id"));
  const submit = handleSubmit(
    (address) =>
      restaurantId > 0 &&
      order.mutate(buildOrderRequest(restaurantId, address)),
  );
  return (
    <main className="container page">
      <div className="page-heading">
        <p className="kicker">One last detail</p>
        <h1>Delivery address</h1>
      </div>
      {!restaurantId && (
        <p className="form-alert">
          The backend cart does not expose its restaurant. Return to the
          restaurant menu before checkout so the restaurant can be verified.{" "}
          <Link to="/restaurants">Browse restaurants</Link>
        </p>
      )}
      <form className="checkout-layout" onSubmit={submit}>
        <section className="form-card">
          <Input
            label="Street address"
            error={errors.streetAddress?.message}
            {...register("streetAddress")}
          />
          <div className="form-grid">
            <Input
              label="City"
              error={errors.city?.message}
              {...register("city")}
            />
            <Input
              label="State / region"
              error={errors.state?.message}
              {...register("state")}
            />
            <Input
              label="Postal code"
              error={errors.postalCode?.message}
              {...register("postalCode")}
            />
            <Input
              label="Country"
              error={errors.country?.message}
              {...register("country")}
            />
          </div>
        </section>
        <aside className="summary-card">
          <h2>Order summary</h2>
          {cart.data.items.map((i) => (
            <div key={i.id}>
              <span>
                {i.quantity} × {i.foodName}
              </span>
              <span>{formatPrice(i.totalPrice)}</span>
            </div>
          ))}
          <div className="summary-total">
            <span>Total</span>
            <strong>{formatPrice(cart.data.total)}</strong>
          </div>
          {order.error && <p className="form-alert">{order.error.message}</p>}
          <Button
            type="submit"
            size="lg"
            loading={order.isPending}
            disabled={!restaurantId}
          >
            Place order
          </Button>
        </aside>
      </form>
    </main>
  );
}
