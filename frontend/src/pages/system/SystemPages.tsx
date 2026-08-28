import { Link } from "react-router-dom";
export function NotFoundPage() {
  return (
    <main className="container system-page">
      <p className="kicker">404</p>
      <h1>This page is off the menu.</h1>
      <p>The page may have moved or never existed.</p>
      <Link className="button primary md" to="/">
        Back home
      </Link>
    </main>
  );
}
export function ForbiddenPage() {
  return (
    <main className="container system-page">
      <p className="kicker">403</p>
      <h1>This area isn’t available to your account.</h1>
      <p>Return to the catalogue or use an account with the required role.</p>
      <Link className="button primary md" to="/">
        Back home
      </Link>
    </main>
  );
}
