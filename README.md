# Farmer Platform: Wisdom of the Soil

A modern, full-stack "Smart Agriculture" platform designed to bridge the gap between farmers and consumers. This project provides a seamless ecosystem for policy information, agricultural marketplace, and business management.

## 🌿 The "Harvest" Design System

The platform has been completely reimagined with a **Natural & Organic** design philosophy. Moving away from traditional rigid layouts, we've implemented a **Human-Centric** visual language:

-   **Biophilic Palette:** A refreshing theme centered around **Emerald Greens** (Growth), **Amber Golds** (Harvest), and **Morning Dew Slates**.
-   **Floating Island Layout:** Navigation and content containers use "floating island" and "bento grid" patterns, creating a spacious, airy, and modern feel.
-   **Organic Curves:** Large rounded corners and layered natural shadows mimic the soft forms found in nature, reducing visual fatigue.
-   **Modern Interaction:** Smooth transitions, glassmorphism effects, and a responsive "Floating Sidebar" for administrators.

## 🌟 Key Features

-   **Intelligent AI Agent:** Powered by **DeepSeek-V3** via LangChain. The agent can "perceive" real-time business data to provide natural language insights and automated reporting.
-   **High-Performance Backend:** Built on **Spring Boot 3+**, featuring database-level pagination, optimized SQL aggregations, and robust DTO-based API design.
-   **Reactive Frontend:** A highly responsive **Vue 3 (Composition API)** interface with **Pinia** for state management and **Element Plus** for a polished UI components.
-   **Data-Driven:** Strictly real-data driven architecture with zero mock dependencies, ensuring a production-ready foundation.

## 🛠 Tech Stack

### Frontend (Vue Ecosystem)
- `Vue 3 (Composition API)` + `Vite`
- `Pinia` (Incrementally updated state management)
- `Element Plus` (Customized with the "Harvest" design system)
- `Axios` & `Native Fetch`

### Backend (Java Ecosystem)
- `Java 21` + `Spring Boot 3`
- `Spring Data JPA` + `MySQL`
- `Lombok` + `JJWT` (Secure Authentication)

### AI Service (Agent)
- `Node.js` + `TypeScript`
- `LangChain` + `DeepSeek API`
- `Server-Sent Events (SSE)` for real-time streaming analysis

## 🚀 Quick Start

### 1. Backend API (Port 8080)
```bash
cd backend/farmer_platform
.\gradlew.bat bootRun
```

### 2. AI Agent Service (Port 3000)
Enter the `ai-agent` directory and configure `DEEPSEEK_API_KEY` in `.env`:
```bash
cd ai-agent
npm install
npm start
```

### 3. Frontend Interface (Port 5173)
```bash
npm install
npm run dev
```

## 📂 Project Structure

```text
farmer-platform/
  ├── ai-agent/                 # Node.js AI Service (DeepSeek Agent)
  ├── backend/                  # Spring Boot Backend Source
  ├── src/                      # Vue Frontend Source
  │   ├── views/admin/          # Admin Dashboard (Modern Floating Layout)
  │   ├── views/user/           # User Interface (Island Navigation)
  │   └── stores/               # Pinia State Stores
  └── GEMINI.md                 # Project Context & Engineering Standards
```

## 🔐 Default Credentials

-   **Administrator:** `tfgkk` / `123456`
-   **Regular User:** `张大农` / `123456`

---
*Connecting the Soil to the Soul through Smart Technology.*
