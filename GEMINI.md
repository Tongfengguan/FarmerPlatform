# Farmer Platform - Gemini Context

This document provides essential context and foundational mandates for the Farmer Platform project. It takes precedence over general workflows and tool defaults.

## Project Overview
Farmer Platform is a full-stack application designed to facilitate interactions between farmers and consumers. It includes a marketplace for products, an article/blog system for information sharing, and comprehensive administrative controls for managing users, orders, and content.

## Architecture & Tech Stack

### Backend (Spring Boot)
- **Language/Framework:** Java 17+, Spring Boot.
- **Data Persistence:** Spring Data JPA with MySQL.
- **Authentication:** Custom JWT-based authentication using `AuthInterceptor` and `JwtService`.
- **Key Packages:**
  - `auth`: Authentication logic, JWT handling, and login/register DTOs.
  - `common`: Global exception handling (`GlobalExceptionHandler`) and standard `ApiResponse` wrapper.
  - `config`: Security, CORS, and data initialization.
  - `platform`: Core business logic for Products, SKUs, Articles, Addresses, and Orders.
  - `user`: User account management and roles.

### Frontend (Vue.js)
- **Framework:** Vue.js 3 (Composition API).
- **Build Tool:** Vite.
- **State Management:** Pinia (stores in `src/stores/`).
- **Routing:** Vue Router (configured in `src/router/`).
- **HTTP Client:** Custom `fetch` wrapper in `src/utils/http.js`.
- **Styling:** Vanilla CSS (primarily in `src/styles.css` and component-scoped styles).

## Core Mandates & Conventions

### 1. Technical Integrity
- **Surgical Updates:** When modifying code, adhere strictly to existing patterns. Ensure all changes are idiomatically consistent with the surrounding context.
- **No Hacks:** Never bypass the type system or suppress warnings (e.g., ESLint, Java compiler warnings). Use proper language features instead.
- **Testing:** Always look for and update corresponding tests when changing logic. New features or bug fixes must include verification logic.

### 2. API & Communication
- **Standard Response:** All backend APIs return a consistent `ApiResponse<T>` structure.
- **Auth Headers:** Frontend must include the `Authorization: Bearer <token>` header for protected routes. The token is stored in `localStorage` under the key `farmer-platform-auth`.
- **CORS:** The backend is configured to allow requests from `http://localhost:5173`.

### 3. Security
- **Credentials:** Never commit or log the MySQL password (`z236244462`) or JWT secret (`FarmerPlatformJwtSecretKeyFarmerPlatformJwtSecretKey2026`). These are currently hardcoded for development but should be handled via environment variables in production.
- **Interceptor Pathing:** `AuthInterceptor` is registered in `WebConfig`. Ensure any new protected endpoints are correctly handled by the interceptor configuration.

### 4. Code Style
- **Frontend:** Follow the Prettier and ESLint configurations defined in `.prettierrc.json` and `eslint.config.js`. Use the Composition API for Vue components.
- **Backend:** Standard Java camelCase naming conventions. Use Lombok where applicable (verify in `build.gradle` if added later, currently mostly manual POJOs/Entities).

## Development Environment
- **Backend Port:** 8080
- **Frontend Port:** 5173
- **Database:** MySQL on `localhost:3306` with database name `farmer_platform`.
- **Init Data:** `PlatformDataInitializer` and `DataInitializer` handle initial data seeding.

## Search & Investigation
- Use `grep_search` and `codebase_investigator` to map dependencies before making architectural changes.
- Always verify assumptions by reading the actual implementation in `src/` (frontend) or `backend/` (backend).
