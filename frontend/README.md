# Tsinjo Food Frontend

A responsive React client for the existing Spring Boot food-ordering API.

## Stack

React 19, TypeScript strict, Vite, React Router, TanStack Query, React Hook Form, Zod, Vitest and Testing Library. The visual system is custom CSS with local SVG components; there is no icon library or large UI framework.

## Requirements

- Node.js 22 LTS or newer
- The backend on `http://localhost:8080`
- Java 21 and the backend's configured MySQL database for live flows

## Installation

```powershell
cd frontend
npm install
Copy-Item .env.example .env
npm run dev
```

`VITE_API_BASE_URL=http://localhost:8080` is the default and can be changed in `.env`.

## Quality commands

```powershell
npm run lint
npm run test
npm run build
```

## Backend

The Java 21 / Spring Boot backend lives in `../backend-food`, serves on port 8080, and exposes Swagger at `http://localhost:8080/swagger-ui/index.html`. Runtime content always comes from this API; the frontend contains no mock catalogue.

## Development accounts

With the Spring `dev` profile only:

- Customer: `customer@test.com` / `Customer123!`
- Restaurant owner: `owner@test.com` / `Owner123!`
- Admin: `admin@test.com` / `Admin123!`

Hints are rendered only when `import.meta.env.DEV` is true and credentials are never prefilled.

## Main routes

- Public: `/`, `/restaurants`, `/restaurants/:id`, `/search`, `/login`, `/signup`
- Customer: `/cart`, `/checkout`, `/orders`, `/orders/:id`, `/profile`
- Owner: `/owner` with hash-based operational sections
- Admin: `/admin` with a public restaurant selector and supported operations

## Architecture and authentication

`src/api` owns all HTTP contracts and bearer handling. `AuthProvider` restores `/api/users/me`, route guards enforce authentication and roles, and 401 responses clear only invalid sessions. TanStack Query owns server state and targeted invalidation. Feature pages, reusable UI, layout, local SVG, formatting and exact backend DTO types are separated under `src/`.

## API integration

The client uses the documented public catalogue, cart, order, owner and admin endpoints. The intentionally misspelled stock endpoint `/api/admin/ingredients/{id}/stoke` is used unchanged. Known contract gaps are documented in [BACKEND_BLOCKERS.md](BACKEND_BLOCKERS.md).

## No icon libraries

All pictograms are tiny local React SVGs using `currentColor`. No icon package is installed.
