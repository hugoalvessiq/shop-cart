# 🛍️ SuperMall

SuperMall is a complete e-commerce application developed as a full-stack learning project with JWT authentication, product management, shopping cart, and order system.

## 📦 Technologies Used

### Backend (Spring Boot)
- Java 21
- Spring Boot 3
- Spring Security + JWT (Access + Refresh Token)
- JPA + Hibernate
- PostgreSQL
- Maven

### Frontend (React)
- React 18
- Vite
- Axios
- Tailwind CSS
- React Router Dom

## 🔐 Key Features

### Authentication
- User registration and login
- "Keep me logged in" login
- JWT token generation
- Automatic Refresh Token
- Protected routes (admin and user)

### Regular User
- Product viewing
- Adding and removing products from the cart
- Order checkout
- Order history
- Profile update
- Account deletion (with warning if items are in the cart)

### Administrator
- Complete product CRUD
- User management
- Role-restricted access

## 🗃️ Structure
```bash
supermall/
├── backend/ → Spring Boot Application
│ └── src/
│ └── main/
│ └── java/com/supermall/...
├── frontend/ → React Application (Vite)
│ └── src/
│ ├── api/
│ ├── components/
│ ├── pages/
│ ├── App.jsx
│ └── main.jsx
```

- ## ⚙️ Requirements
- Java 21
- Node.js 18+
- PostgreSQL
- Maven
- Docker (optional for database)
- .env.local with:

## 🪪 Standard User Created
```env
DB_NAME=shop_cart
DB_USER=postgres
DB_PASSWORD=

JWT_SECRET=<your_base64_key>
JWT_EXPIRATION=86400000
```

## 🚀 How to Run
### Backend
```bash
cd backend
./create_db.sh # creates the database (optional)
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

#### Access: [localhost](http://localhost:5173)

## 📚 Learnings

During the project, the following was worked on:
- Structuring a REST API with Java + Spring Boot

- Secure JWT handling and authentication with Refresh Token

- Clear separation of responsibilities in the backend

- Best practices in the frontend with React + Tailwind

- Integration and consistency between client and server

#### Developed with AI assistance by [Hugo](https://github.com/hugoalvessiq)