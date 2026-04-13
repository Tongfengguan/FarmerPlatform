# Farmer Platform - Gemini Context

This document provides essential context and foundational mandates for the Farmer Platform project. It takes precedence over general workflows and tool defaults.

## Project Overview
Farmer Platform is a full-stack application designed to facilitate interactions between farmers and consumers. It includes a marketplace for products, an article/blog system for information sharing, and comprehensive administrative controls for managing users, orders, and content.

## Architecture & Tech Stack

### Backend (Spring Boot)
- **Language/Framework:** Java 21, Spring Boot 4.
- **Data Persistence:** Spring Data JPA with MySQL.
- **Authentication:** Custom JWT-based authentication using `AuthInterceptor` and `JwtService`.
- **Engineering:** Lombok integrated for reduced boilerplate.
- **Key Packages:**
  - `auth`: Authentication logic, JWT handling, and login/register DTOs.
  - `common`: Standard `ApiResponse<T>` and `PagedResponse<T>` wrappers.
  - `platform`: Core business logic for Products, SKUs, Articles, Addresses, and Orders. Supports **Pagination** at the database level.
  - `user`: User account management with optimized SQL aggregation for summaries.

### Frontend (Vue.js)
- **Framework:** Vue.js 3 (Composition API).
- **UI Component Library:** **Element Plus** (fully integrated with Auto-import and Dark Mode).
- **State Management:** Pinia (surgical updates for optimized performance).
- **Routing:** Vue Router.
- **HTTP Client:** Custom `fetch` wrapper and `axios` for AI services.
- **Visuals:** Modern aesthetic with refined transitions, hover effects, and stabilized carousels.

### AI Agent Service (DeepSeek)
- **Framework:** LangChain (Node.js/TypeScript).
- **Model:** DeepSeek-V3 / DeepSeek-Chat.
- **Capabilities:** Real-time business data analysis, tool calling (connects to Farmer Platform backend), and streaming responses (SSE).

## Core Mandates & Conventions

### 1. Technical Integrity
- **Surgical Updates:** When modifying code, adhere strictly to existing patterns.
- **Pagination First:** Always use `Pageable` for list-based API endpoints.
- **DTO Mutations:** Refactor mutation endpoints to return the single updated entity rather than full lists.

### 2. UI/UX Standards
- **Element Plus:** Prefer Element Plus components over manual CSS/HTML where applicable.
- **Stability:** Ensure visual stability (e.g., `backface-visibility`, `transform: translateZ(0)`) for animations.
- **Dark Theme:** Maintain consistency with the established dark green/grey theme.

### 3. Security
- **Credentials:** MySQL password (`z236244462`) and JWT secret (`FarmerPlatformJwtSecretKeyFarmerPlatformJwtSecretKey2026`) are hardcoded for development only.

## Development Environment
- **Backend (API):** `8080`
- **Frontend (Vue):** `5173`
- **AI Agent (Node):** `3000` (provides `/api/analyze` for streaming analysis)
- **Database:** MySQL on `localhost:3306` (`farmer_platform`)
