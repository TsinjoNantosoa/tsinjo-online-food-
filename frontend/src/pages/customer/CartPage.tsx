import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import { cartApi } from "../../api";
import {
  Button,
  EmptyState,
  ErrorState,
  PageLoader,
  SafeImage,
} from "../../components/ui";
import { formatPrice } from "../../utils/format";
export const buildCartQuantityRequest = (
  cartItemId: number,
  quantity: number,
) => ({ cartItemId, quantity });
export function CartPage() {
  const qc = useQueryClient();
  const cart = useQuery({ queryKey: ["cart"], queryFn: cartApi.get });
  const refresh = () => qc.invalidateQueries({ queryKey: ["cart"] });
  const update = useMutation({
    mutationFn: cartApi.update,
    onSuccess: refresh,
  });
  const remove = useMutation({
    mutationFn: cartApi.remove,
    onSuccess: refresh,
  });
  const clear = useMutation({ mutationFn: cartApi.clear, onSuccess: refresh });
  if (cart.isLoading) return <PageLoader />;
  if (cart.isError)
    return (
      <main className="container page">
        <ErrorState error={cart.error} />
      </main>
    );
  if (!cart.data?.items.length)
    return (
      <main className="container page">
        <EmptyState
          title="Your cart is empty"
          description="A good meal starts with a little browsing."
          action={
            <Link className="button primary md" to="/restaurants">
              Explore restaurants
            </Link>
          }
        />
      </main>
    );
  return (
    <main className="container page">
      <div className="page-heading">
        <p className="kicker">Your selection</p>
        <h1>Cart</h1>
      </div>
      <div className="cart-layout">
        <section className="cart-items">
          {cart.data.items.map((item) => (
            <article className="cart-item" key={item.id}>
              <SafeImage src={item.foodImage} alt={item.foodName} />
              <div>
                <h2>{item.foodName}</h2>
                {item.selectedIngredients.length > 0 && (
                  <p>
                    {item.selectedIngredients.map((i) => i.name).join(", ")}
                  </p>
                )}
                <small>{formatPrice(item.unitPrice)} each</small>
                <div className="quantity">
                  <button
                    aria-label="Decrease"
                    disabled={update.isPending}
                    onClick={() =>
                      item.quantity === 1
                        ? remove.mutate(item.id)
                        : update.mutate(
                            buildCartQuantityRequest(
                              item.id,
                              item.quantity - 1,
                            ),
                          )
                    }
                  >
                    −
                  </button>
                  <span>{item.quantity}</span>
                  <button
                    aria-label="Increase"
                    disabled={update.isPending}
                    onClick={() =>
                      update.mutate(
                        buildCartQuantityRequest(item.id, item.quantity + 1),
                      )
                    }
                  >
                    +
                  </button>
                </div>
                <button
                  className="text-danger"
                  onClick={() => remove.mutate(item.id)}
                >
                  Remove
                </button>
              </div>
              <strong>{formatPrice(item.totalPrice)}</strong>
            </article>
          ))}
        </section>
        <aside className="summary-card">
          <h2>Order summary</h2>
          <div>
            <span>Items</span>
            <span>{cart.data.totalItems}</span>
          </div>
          <div className="summary-total">
            <span>Total</span>
            <strong>{formatPrice(cart.data.total)}</strong>
          </div>
          <Link className="button primary lg" to="/checkout">
            Proceed to checkout
          </Link>
          <Button
            variant="ghost"
            loading={clear.isPending}
            onClick={() => clear.mutate()}
          >
            Clear cart
          </Button>
        </aside>
      </div>
    </main>
  );
}
