# 🛒 E-Commerce Platform (Backend API)

A **production-style backend API** for an e-commerce platform, built as a **learning project**. The system enables users to browse products, manage shopping carts, place orders, and securely process payments. While fully functional, the project primarily demonstrates **backend development, security best practices, and DevOps processes** including automated testing, containerization, and CI/CD deployment.

---

## Features

### Customer Features
- Secure registration and login with JWT authentication  
- Product browsing, search, and category filtering  
- Shopping cart management (add, update, remove items)  
- Stripe checkout for secure payment processing  
- Address management for multiple shipping addresses  

### Admin Features
- Product management (create, update, delete)  
- Inventory control  
- Category organization  
- Order oversight and monitoring  

### Technical Highlights
- JWT Authentication with access and refresh tokens  
- Role-based access control (User/Admin)  
- Stripe Webhook integration for payment confirmations  
- Database migrations using Flyway  
- Interactive API documentation via Swagger UI  
- Docker Compose for local development  
- MapStruct for clean DTO mapping  
- Global exception handling for consistent error responses  

---

## Tech Stack

**Backend:**  
- Spring Boot 3.5.7  
- Java 25  
- Spring Security + JWT  
- Spring Data JPA (Hibernate)  
- Flyway for database migrations  
- SpringDoc OpenAPI (Swagger UI)  

**Database:**  
- PostgreSQL  

**DevOps & Tools:**  
- Docker & Docker Compose  
- Spring Dotenv for environment management  
- Maven build tool  

**Key Dependencies:**  
- MapStruct – DTO mapping  
- Lombok – Boilerplate reduction  
- Stripe Java SDK – Payment processing  
- JJWT – JWT token generation  
- Bean Validation – Input validation  

---

## Architecture

- **Layered design:** Controller → Service → Repository  
- **Stateless authentication** using JWT  
- **Separation of concerns** between business logic and persistence  
- **Production-ready features:** Payment webhooks, role-based access control, and API documentation  

---

## Installation & Development

1. Clone the repository:  
```bash
git clone https://github.com/your-username/ecommerce-backend.git
cd ecommerce-backend
