# Online Food Backend

Backend REST de commande de repas, prêt à être consommé par un frontend React/TypeScript.

## Stack

- Java 21
- Spring Boot 3.3.5
- Spring Security 6 et JWT
- Spring Data JPA / Hibernate
- MySQL 8
- Maven Wrapper
- Swagger / OpenAPI
- JUnit 5, Mockito et MockMvc

## Prérequis

- JDK 21
- MySQL 8.x
- VS Code

Extensions VS Code recommandées : **Extension Pack for Java** et **Spring Boot Extension Pack**.

Vérifier Java sous Windows :

```powershell
java -version
javac -version
```

## Configuration

Copier les noms de variables de `.env.example`. Spring Boot ne charge pas automatiquement un fichier `.env` : définir les variables dans le terminal, dans Windows ou dans la configuration de lancement VS Code.

| Variable | Rôle | Valeur de développement par défaut |
|---|---|---|
| `DB_URL` | URL JDBC MySQL | `jdbc:mysql://localhost:3306/online_food?createDatabaseIfNotExist=true&serverTimezone=UTC` |
| `DB_USERNAME` | utilisateur MySQL | `root` |
| `DB_PASSWORD` | mot de passe MySQL | vide |
| `JWT_SECRET` | secret HMAC, au moins 32 caractères | valeur locale non destinée à la production |
| `JWT_EXPIRATION_MS` | durée de vie du JWT | `86400000` (24 h) |
| `FRONTEND_URL` | origine CORS principale | `http://localhost:5173` |

Exemple PowerShell :

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "votre_mot_de_passe"
$env:JWT_SECRET = "une-valeur-aleatoire-longue-d-au-moins-32-caracteres"
$env:FRONTEND_URL = "http://localhost:5173"
```

Ne jamais versionner `.env`, un mot de passe ou un secret réel. Pour la production, fournir impérativement un `JWT_SECRET` aléatoire robuste.

## Database

Le schéma par défaut est `online_food`. Le paramètre `createDatabaseIfNotExist=true` permet à MySQL de le créer si le compte possède ce droit. Sinon :

```sql
CREATE DATABASE online_food CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Hibernate utilise `spring.jpa.hibernate.ddl-auto=update`, adapté au développement et non destructif. Avant une mise en production, remplacer cette stratégie par des migrations versionnées (Flyway ou Liquibase) et `validate`.

## Installation et lancement

Windows :

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Linux/macOS :

```bash
./mvnw clean test
./mvnw spring-boot:run
```

L’API écoute par défaut sur `http://localhost:8080`.

## Swagger

- Interface : `http://localhost:8080/swagger-ui/index.html`
- Document OpenAPI : `http://localhost:8080/v3/api-docs`

Dans Swagger, utiliser **Authorize** avec le JWT retourné par l’authentification. Les routes Swagger restent publiques, les opérations API gardent leurs règles de sécurité.

## Auth

- `POST /auth/signup` crée uniquement un compte `ROLE_CUSTOMER`, hashé avec BCrypt, puis renvoie un JWT.
- `POST /auth/signin` authentifie email/mot de passe et renvoie un JWT.
- L’alias historique `POST /auth/signing` reste accepté pour compatibilité.

Envoyer ensuite :

```http
Authorization: Bearer <jwt>
```

La création de comptes `ROLE_RESTAURANT_OWNER` ou `ROLE_ADMIN` n’est pas exposée au signup public. Elle doit passer par un processus administré (provisionnement base/outil d’administration futur).

## Principaux endpoints

Toutes les routes `/api/**` requièrent un JWT. Les routes `/api/admin/**` exigent `ROLE_RESTAURANT_OWNER` ou `ROLE_ADMIN`, avec en plus un contrôle du propriétaire de la ressource.

### Utilisateur et restaurants

- `GET /api/users/profile`
- `GET /api/restaurant`
- `GET /api/restaurant/{id}`
- `GET /api/restaurant/search?keyword=...`
- `PUT /api/restaurant/{id}/add-favorite`
- `POST /api/admin/restaurants`
- `PUT /api/admin/restaurants/{id}`
- `DELETE /api/admin/restaurants/{id}`
- `PUT /api/admin/restaurants/{id}/status`
- `GET /api/admin/restaurants/user`

### Catégories, plats et ingrédients

- `GET /api/food/search?name=...`
- `GET /api/food/restaurant/{restaurantId}`
- `GET /api/restaurants/{restaurantId}/categories`
- `POST /api/admin/categories`
- `POST /api/admin/food`
- `PUT /api/admin/food/{id}`
- `DELETE /api/admin/food/{id}`
- `/api/admin/ingredients/**` pour catégories, ingrédients et disponibilité du stock

### Panier et commandes

- `GET /api/cart`
- `POST /api/cart/items`
- `PUT /api/cart/items`
- `DELETE /api/cart/items/{id}`
- `DELETE /api/cart`
- `POST /api/orders`
- `GET /api/orders`
- `GET /api/orders/{id}`
- `GET /api/admin/order/restaurant/{id}`
- `PUT /api/admin/orders/{orderId}/status/{orderStatus}`

Statuts de commande acceptés : `PENDING`, `OUT_FOR_DELIVERY`, `DELIVERED`, `COMPLETED`, `CANCELLED`.

## Erreurs API

Les erreurs ont un format uniforme sans stack trace :

```json
{
  "timestamp": "2026-08-28T10:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Restaurant not found with id: 10",
  "path": "/api/restaurant/10"
}
```

## Tests

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd clean package
```

Les tests utilisent H2 en mémoire en mode MySQL et couvrent le contexte JPA, signup/login, BCrypt, protection JWT, validation, panier, commande et les retours restaurant/plats. Aucun serveur MySQL n’est requis pour exécuter les tests.

## Frontend Integration

- Backend : `http://localhost:8080`
- Frontend Vite : `http://localhost:5173`
- Swagger : `http://localhost:8080/swagger-ui/index.html`
- Contrat détaillé : [`docs/API_CONTRACT.md`](docs/API_CONTRACT.md)

### Authentication

- `POST /auth/signup`
- `POST /auth/signin`
- `GET /api/users/me`

Le login renvoie `token`, `tokenType` et le profil utilisateur. Envoyer ensuite :

```http
Authorization: Bearer <token>
```

### Public API

Ces routes sont accessibles sans JWT :

- `GET /api/restaurant`
- `GET /api/restaurant/search?keyword=burger`
- `GET /api/restaurant/{id}`
- `GET /api/food/search?name=burger`
- `GET /api/food/restaurant/{restaurantId}`
- `GET /api/restaurants/{restaurantId}/categories`

### Customer API

- Profil : `GET /api/users/me` et `GET /api/users/profile`
- Favoris : `PUT /api/restaurant/{id}/add-favorite`
- Panier : `GET /api/cart`, `POST|PUT /api/cart/items`, `DELETE /api/cart/items/{id}`
- Commandes : `POST|GET /api/orders`, `GET /api/orders/{id}`

### Restaurant Owner API

- Restaurant : `/api/admin/restaurants/**`
- Catégories : `POST /api/admin/categories`
- Plats : `/api/admin/food/**`
- Ingrédients : `/api/admin/ingredients/**`
- Commandes : `/api/admin/order/restaurant/{id}` et `/api/admin/orders/{orderId}/status/{orderStatus}`

Chaque opération vérifie aussi que le restaurant appartient réellement à l’utilisateur, sauf pour `ROLE_ADMIN`.

La création utilise `POST /api/admin/food`, la modification métier `PATCH /api/admin/food/{id}` et l’ancien `PUT /api/admin/food/{id}` conserve le basculement de disponibilité.

### Admin API

`ROLE_ADMIN` peut utiliser les routes `/api/admin/**` existantes. Le signup public ne permet jamais de créer un administrateur.

### Development profile

Le profil `dev` crée uniquement si nécessaire trois comptes et un petit catalogue :

| Rôle | Email | Mot de passe |
|---|---|---|
| Customer | `customer@test.com` | `Customer123!` |
| Restaurant owner | `owner@test.com` | `Owner123!` |
| Admin | `admin@test.com` | `Admin123!` |

Ces comptes ne sont jamais créés hors du profil `dev`. Lancer sous PowerShell :

```powershell
$env:JWT_SECRET = "une-valeur-dev-longue-d-au-moins-32-caracteres"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

### Example JSON

Login :

```json
{"email":"customer@test.com","password":"Customer123!"}
```

Food :

```json
{"id":12,"name":"Classic Burger","price":15000,"restaurant":{"id":1,"name":"Tsinjo Food"},"category":{"id":2,"name":"Burgers"},"ingredients":[]}
```

Cart :

```json
{"id":1,"total":30000,"totalItems":2,"items":[{"foodId":12,"quantity":2,"unitPrice":15000,"selectedIngredients":[]}]}
```

Order :

```json
{"id":100,"status":"PENDING","totalAmount":30000,"restaurant":{"id":1,"name":"Tsinjo Food"},"items":[]}
```

Error :

```json
{"timestamp":"2026-08-28T12:00:00Z","status":400,"error":"Validation Failed","message":"Invalid request","fieldErrors":{"quantity":"must be greater than 0"},"path":"/api/cart/items"}
```
