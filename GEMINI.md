# Farmer Platform - Gemini Context

This document provides essential context and foundational mandates for the Farmer Platform project. It takes precedence over general workflows and tool defaults.

## Project Overview
Farmer Platform is a full-stack application designed to facilitate interactions between farmers and consumers. It includes a marketplace for products, an article/blog system for information sharing, and comprehensive administrative controls for managing users, orders, and content.

## Architecture & Tech Stack

### Backend (Spring Boot)
- **Language/Framework:** Java 21, Spring Boot 4.
- **Data Persistence:** Spring Data JPA with MySQL.
- **Authentication:** Custom JWT-based authentication using `AuthInterceptor` and `JwtService`. **Fixed**: Interceptor now correctly handles OPTIONS requests and allows anonymous access to login/register/public-products.
- **Key Packages:**
  - `auth`: Authentication logic, JWT handling, and login/register DTOs.
  - `common`: Standard `ApiResponse<T>` and `PagedResponse<T>` wrappers.
  - `platform`: Core business logic for Products, SKUs, Articles, Addresses, and Orders. Supports **Pagination** at the database level.
  - `user`: User account management with optimized SQL aggregation for summaries.

### Frontend (Vue.js)
- **Framework:** Vue.js 3 (Composition API).
- **UI Library:** Element Plus (Auto-import and Dark Mode).
- **State Management:** Pinia.
- **Business Logic**: **Strictly real-data driven**. Mock data (`mockData.js`) has been completely removed. Business categories and statuses are centralized in `src/utils/constants.js`.

### AI Agent Service (DeepSeek)
- **Framework:** LangChain (Node.js/TypeScript).
- **Architecture**: **Safe Summary Mode**. 
  - **Step 1**: Identify tool call and fetch raw data from backend.
  - **Step 2**: Discard complex protocol history and initiate a clean summary context to prevent `<｜DSML｜>` tag leakage.
- **Capabilities**: Concise business analysis (max 200 words), direct concluding points, and tool calling via backend API.

## Core Mandates & Conventions

### 1. Technical Integrity
- **No Mock Data**: Do not introduce mock data files. All views must fetch data from the Spring Boot API.
- **Surgical Updates**: Adhere strictly to existing patterns.
- **Pagination First**: Always use `Pageable` for list-based API endpoints.
- **Safe AI Response**: Maintain the multi-stage regex filtering in both `index.ts` (backend) and `AiAssistantView.vue` (frontend) to ensure pure text output.

### 2. UI/UX Standards
- **Element Plus**: Prefer Element Plus components.
- **Stability**: Ensure visual stability for animations.
- **Concise AI**: AI reports should be brief (3-4 bullet points) and action-oriented.

### 3. Security
- **Auth Integrity**: Ensure `AuthInterceptor` logic correctly separates public paths from secured paths.
- **Credentials**: MySQL password (`z236244462`) and JWT secret are for development only.

## Development Environment
- **Backend (API)**: `8080` (Base URL: `http://localhost:8080/api`)
- **Frontend (Vue)**: `5173`
- **AI Agent (Node)**: `3000` (provides `/api/analyze` for streaming analysis)
- **Database**: MySQL on `localhost:3306` (`farmer_platform`)
