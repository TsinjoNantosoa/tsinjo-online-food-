import { useAuth } from "../../features/auth/AuthProvider";
import { Button, EmptyState } from "../../components/ui";
export function ProfilePage() {
  const { user, logout } = useAuth();
  if (!user) return null;
  return (
    <main className="container page narrow">
      <div className="page-heading">
        <p className="kicker">Your account</p>
        <h1>Profile</h1>
      </div>
      <section className="profile-card">
        <div className="avatar">{user.fullName.slice(0, 1).toUpperCase()}</div>
        <div>
          <h2>{user.fullName}</h2>
          <p>{user.email}</p>
          <span className="role-label">
            {user.role.replace("ROLE_", "").replace("_", " ")}
          </span>
        </div>
      </section>
      <section className="section compact">
        <h2>Saved addresses</h2>
        {user.addresses.length ? (
          user.addresses.map((a) => (
            <address className="address-card" key={a.id}>
              {a.streetAddress}
              <br />
              {a.city}, {a.country}
            </address>
          ))
        ) : (
          <EmptyState
            title="No saved addresses"
            description="Addresses appear here when provided by your account."
          />
        )}
      </section>
      <Button variant="secondary" onClick={logout}>
        Sign out
      </Button>
    </main>
  );
}
