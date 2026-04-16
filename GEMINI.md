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

---

# Karpathy-Inspired Coding Guidelines

**Tradeoff:** These guidelines bias toward caution over speed. For trivial tasks, use judgment.

## 1. Think Before Coding

**Don't assume. Don't hide confusion. Surface tradeoffs.**

Before implementing:
- State your assumptions explicitly. If uncertain, ask.
- If multiple interpretations exist, present them - don't pick silently.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear, stop. Name what's confusing. Ask.

## 2. Simplicity First

**Minimum code that solves the problem. Nothing speculative.**

- No features beyond what was asked.
- No abstractions for single-use code.
- No "flexibility" or "configurability" that wasn't requested.
- No error handling for impossible scenarios.
- If you write 200 lines and it could be 50, rewrite it.

Ask yourself: "Would a senior engineer say this is overcomplicated?" If yes, simplify.

## 3. Surgical Changes

**Touch only what you must. Clean up only your own mess.**

When editing existing code:
- Don't "improve" adjacent code, comments, or formatting.
- Don't refactor things that aren't broken.
- Match existing style, even if you'd do it differently.
- If you notice unrelated dead code, mention it - don't delete it.

When your changes create orphans:
- Remove imports/variables/functions that YOUR changes made unused.
- Don't remove pre-existing dead code unless asked.

The test: Every changed line should trace directly to the user's request.

## 4. Goal-Driven Execution

**Define success criteria. Loop until verified.**

Transform tasks into verifiable goals:
- "Add validation" → "Write tests for invalid inputs, then make them pass"
- "Fix the bug" → "Write a test that reproduces it, then make it pass"
- "Refactor X" → "Ensure tests pass before and after"

For multi-step tasks, state a brief plan:
```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

Strong success criteria let you loop independently. Weak criteria ("make it work") require constant clarification.
