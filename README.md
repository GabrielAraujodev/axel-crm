# Axel CRM

Multi-tenant CRM system built with Angular 18 + Spring Boot 3 + PostgreSQL.

## Tech Stack

| Layer    | Technology                                      |
| -------- | ----------------------------------------------- |
| Frontend | Angular 18, Angular Material, Chart.js          |
| Backend  | Spring Boot 4.0, Spring Security, Spring Data JPA |
| Database | PostgreSQL, Flyway migrations                   |
| Auth     | JWT (Auth0 java-jwt)                            |
| Docs     | SpringDoc OpenAPI (Swagger UI)                  |
| Deploy   | Render (free tier via Blueprint)                |

## Structure

```
axel-crm/
├── backend/              # Spring Boot REST API
│   ├── src/main/
│   │   ├── java/com/axelcrm/
│   │   └── resources/
│   │       ├── application.yml          # default config
│   │       ├── application-render.yml   # Render profile
│   │       └── db/migration/            # Flyway migrations
│   ├── Dockerfile
│   └── pom.xml
├── frontend/             # Angular SPA
│   ├── src/
│   │   ├── app/
│   │   └── environments/
│   └── package.json
├── render.yaml           # Render Blueprint (infra as code)
├── .gitignore
└── README.md
```

## Local Development

### Prerequisites

- Java 21
- Node.js 20+
- PostgreSQL 16+

### Backend

```bash
cd backend

# create database
createdb axelcrm

# run (uses application.yml defaults)
./mvnw spring-boot:run
```

API will be available at `http://localhost:8080`.

### Frontend

```bash
cd frontend
npm install
ng serve
```

App will be available at `http://localhost:4200`.

Set the API URL in `src/environments/environment.ts`:

```ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1'
};
```

### API Documentation (Swagger)

When running locally:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Deploy to Render

[![Deploy to Render](https://render.com/images/deploy-to-render-button.svg)](https://dashboard.render.com/blueprints?repo=https://github.com/GabrielAraujodev/axel-crm)

Click the button above or:

1. Push this repo to GitHub
2. Go to [Render Blueprints](https://dashboard.render.com/blueprints)
3. Connect your repository
4. Render reads `render.yaml` and creates:
   - **PostgreSQL** database (free)
   - **Web Service** `axel-crm-api` (Docker, free)
   - **Static Site** `axel-crm-app` (free)
5. Set `JWT_SECRET` in the Web Service environment variables

### Expected URLs

| Service        | URL                                             |
| -------------- | ----------------------------------------------- |
| API            | `https://axel-crm-api.onrender.com`             |
| Swagger UI     | `https://axel-crm-api.onrender.com/swagger-ui.html` |
| Frontend       | `https://axel-crm-app.onrender.com`             |

> The frontend build command replaces `__API_URL__` with the actual API URL at build time (`sed` substitution in `render.yaml`).
> The `JWT_SECRET` must be set manually in the Render dashboard (`sync: false`).

## Environment Variables

### Backend (`application-render.yml`)

| Variable       | Source         | Description          |
| -------------- | -------------- | -------------------- |
| `DB_HOST`      | Render DB      | PostgreSQL host      |
| `DB_PORT`      | Render DB      | PostgreSQL port      |
| `DB_NAME`      | Render DB      | Database name        |
| `DB_USER`      | Render DB      | Database user        |
| `DB_PASSWORD`  | Render DB      | Database password    |
| `JWT_SECRET`   | manual         | JWT signing secret   |
| `JWT_EXPIRATION` | default     | Token expiry (ms)    |

### Frontend

| Variable  | Value                                   | Description      |
| --------- | --------------------------------------- | ---------------- |
| `API_URL` | `https://axel-crm-api.onrender.com/api/v1` | Backend API URL |

## Features

- [x] JWT authentication & authorization
- [x] Multi-tenant data isolation
- [x] Customer & lead management
- [x] Sales pipeline (Kanban)
- [x] PDF report generation
- [x] Swagger/OpenAPI documentation
- [x] Flyway database migrations
- [x] One-click Render deployment
- [ ] Email integration
- [ ] Calendar & tasks
- [ ] File attachments
