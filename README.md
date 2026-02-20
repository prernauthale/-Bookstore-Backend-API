# 📚 Bookstore Backend API

A secure and production-ready **Spring Boot REST API** for an online Bookstore system with:

* 🔐 JWT Authentication & Role-based Authorization
* 📖 Book Management (Admin Only)
* 🛒 Order Management
* 💳 Payment & Order Status Tracking
* 📄 PDF Invoice Generation
* 📑 Pagination & Search
* 📘 Swagger API Documentation

---

## 🚀 Tech Stack

* ☕ Java 17
* 🌱 Spring Boot 3
* 🔐 Spring Security + JWT
* 🗄 Spring Data JPA (Hibernate)
* 🛢 MySQL
* 📄 OpenPDF (Invoice generation)
* 📘 Swagger (Springdoc OpenAPI)
* 🛠 Lombok

---

## 📌 Features

### 👤 Authentication

* User Registration
* Login with JWT
* BCrypt Password Encryption
* Role-based Access (ADMIN / CUSTOMER)

---

### 📚 Book Management

* Add Book (Admin)
* Update Book (Admin)
* Delete Book (Admin)
* Get All Books (Pagination supported)
* Search by:

  * Title
  * Author
  * Genre

---

### 🛒 Order Management

* Place Order (Customer)
* View My Orders
* Update Order Status (Admin)
* Update Payment Status (Admin)
* Automatic Stock Reduction

---

### 📄 Invoice Generation

* Download Order Invoice as PDF
* Includes:

  * Order ID
  * Customer Email
  * Order Date
  * Payment Status
  * Order Status
  * Ordered Items
  * Total Amount

---

### 📘 Swagger Documentation

API documentation available at:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 🔐 Roles & Access

| Role     | Permissions                               |
| -------- | ----------------------------------------- |
| ADMIN    | Manage books, update order/payment status |
| CUSTOMER | Place orders, view own orders             |

---

## ⚙️ Installation & Setup

### 1️⃣ Clone Repository

```bash
git clone https://github.com/your-username/bookstore-backend.git
cd bookstore-backend
```

---

### 2️⃣ Configure Database

Update `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bookstore_db
spring.datasource.username=root
spring.datasource.password=yourpassword

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

### 3️⃣ Build Project

```bash
mvn clean install
```

---

### 4️⃣ Run Application

```bash
mvn spring-boot:run
```

Server will start at:

```
http://localhost:8080
```

---

## 🔑 API Endpoints

### 🔐 Auth

```
POST /auth/register
POST /auth/login
```

---

### 📚 Books

```
GET    /books
GET    /books/{id}
POST   /books           (ADMIN)
PUT    /books/{id}      (ADMIN)
DELETE /books/{id}      (ADMIN)
```

---

### 🛒 Orders

```
POST   /orders
GET    /orders/my
PUT    /orders/{id}/status     (ADMIN)
PUT    /orders/{id}/payment    (ADMIN)
GET    /orders/{id}/invoice
```

---

## 📊 Project Structure

```
com.bookstore
│
├── controller
├── service
├── repository
├── entity
├── dto
├── security
├── exception
└── config
```

---

## 🧪 Testing

Use:

* Swagger UI
* Postman
* curl

JWT Token must be passed in header:

```
Authorization: Bearer <your_token>
```

---

## 📈 Future Improvements

* Add Cart functionality
* Add Order history pagination
* Email invoice sending
* Payment gateway integration
* Docker deployment
* Unit & Integration tests

---

## 👩‍💻 Author

**Prerna Uthale**
Backend Developer Intern

---

## 📄 License

This project is developed for internship submission and educational purposes.

---
