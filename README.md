# Global IP Intelligence Platform

A full-stack web application for IP intelligence monitoring and analysis, featuring role-based access control, real-time analytics, and comprehensive user management.

## 📋 Overview

The Global IP Intelligence Platform is a comprehensive solution for monitoring, analyzing, and managing IP-related intelligence data. It provides different interfaces for Admins, Analysts, and Users, each with role-specific features and capabilities.

## 🎯 Project Milestones

### ✅ Milestone 1: Week 1 & 2 — Authentication & Setup (COMPLETED)

**Objectives:**
- Define roles: User, Analyst, Admin
- Initialize Spring Boot backend and React frontend
- Implement JWT and OAuth2 authentication
- Create registration/login UI and profile management

**Completed Tasks:**

#### Backend Implementation
- ✅ Spring Boot 3.5.5 project setup with Maven
- ✅ PostgreSQL database configuration and integration
- ✅ User entity with role-based access (USER, ANALYST, ADMIN)
- ✅ JWT token generation and validation service
- ✅ OAuth2 Google Sign-In integration
- ✅ Authentication REST endpoints (`/api/auth/register`, `/api/auth/login`)
- ✅ Spring Security configuration with role-based authorization
- ✅ Password encryption using BCrypt
- ✅ Custom UserDetailsService implementation
- ✅ CORS configuration for frontend integration

#### Frontend Implementation
- ✅ React 19.2.1 application setup with React Router 7.10.1
- ✅ Tailwind CSS 3.4.18 integration for responsive design
- ✅ Dark mode support with ThemeContext
- ✅ Login page with email/password and Google OAuth2 button
- ✅ Registration page with role selection dropdown
- ✅ OAuth2RedirectHandler component for Google auth callback
- ✅ ProtectedRoute component for route guarding
- ✅ JWT token management (localStorage)
- ✅ Axios interceptor for automatic authentication headers
- ✅ Toast notification system for user feedback
- ✅ DarkModeToggle component

#### UI Components Created
- ✅ LandingPage with feature showcase
- ✅ UserDashboard with personalized interface
- ✅ AnalystDashboard with analytics tools preview
- ✅ AdminDashboard with user management interface
- ✅ Profile management module
- ✅ Responsive navigation with role-based routing

**Outcome:**
- ✅ **Auth endpoints**: Fully functional registration and login APIs
- ✅ **Role-based login**: JWT authentication with USER, ANALYST, ADMIN roles
- ✅ **Profile module**: Role-specific dashboards with protected routes
- ✅ Secure backend with Spring Security and BCrypt password hashing
- ✅ Modern, responsive UI with dark mode support
- ✅ OAuth2 Google Sign-In working seamlessly

## ✨ Features

- **Authentication & Security**: JWT-based authentication with OAuth2 Google Sign-In
- **Role-Based Access Control**: Admin, Analyst, and User roles with specific permissions
- **Real-time Analytics**: Interactive dashboards with data visualization
- **IP Intelligence**: Monitor and analyze IP-related data
- **Responsive Design**: Works seamlessly on all devices

## 🛠️ Tech Stack

**Frontend:**
- React 19.2.1
- React Router 7.10.1
- Tailwind CSS 3.4.18
- Recharts 3.5.1
- Axios 1.13.2

**Backend:**
- Spring Boot 3.5.5
- Spring Security
- Spring Data JPA
- PostgreSQL Database
- JWT Authentication
- Java 21

## 📋 Prerequisites

- **Java JDK 21** or higher
- **Maven 3.6+**
- **PostgreSQL 14+**
- **npm** or **yarn**
- **Git**

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/springboardmentor1311/Global-IP-Intelligence-Platform-Group-A.git
cd Global-IP-Intelligence-Platform-Group-A
```

### 2. Setup Database

```sql
CREATE DATABASE ip_intelligence;
```

### 3. Start Backend

```bash
cd GlobalIpBackend
# Configure application.properties with your database credentials
./mvnw spring-boot:run
```

Backend will run on `http://localhost:8080`

### 4. Start Frontend

```bash
cd GlobalIpFrontend
npm install
npm start
```

Frontend will run on `http://localhost:3000`

## 📁 Project Structure

```
Global-IP-Intelligence-Platform/
├── README.md                    # Main project documentation
├── GlobalIpBackend/             # Spring Boot backend
│   ├── README.md               # Backend-specific documentation
│   ├── src/                    # Java source files
│   ├── pom.xml                 # Maven configuration
│   └── ...
└── GlobalIpFrontend/           # React frontend
    ├── README.md               # Frontend-specific documentation
    ├── src/                    # React source files
    ├── package.json            # npm dependencies
    └── ...
```

## 📚 Documentation

For detailed setup and configuration instructions, please refer to:
- [Backend Documentation](./GlobalIpBackend/README.md)
- [Frontend Documentation](./GlobalIpFrontend/README.md)

## 🔑 Key Features by Role

### Admin Dashboard
- User management (create, update, delete)
- System configuration
- Analytics overview

### Analyst Dashboard
- IP analysis tools
- Threat intelligence reports
- Data visualization

### User Dashboard
- Personal IP monitoring
- Report generation
- Profile management

## 🌐 API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/auth/register` | POST | User registration |
| `/api/auth/login` | POST | User login |
| `/api/profile` | GET | Get user profile |
| `/api/admin/*` | * | Admin endpoints |
| `/api/analyst/*` | * | Analyst endpoints |
| `/api/user/*` | * | User endpoints |

## 🧪 Testing

**Backend:**
```bash
cd GlobalIpBackend
./mvnw test
```

**Frontend:**
```bash
cd GlobalIpFrontend
npm test
```

## 📦 Building for Production

**Backend:**
```bash
cd GlobalIpBackend
./mvnw clean package
```

**Frontend:**
```bash
cd GlobalIpFrontend
npm run build
```

## 🚀 Deployment

### Backend Deployment
The backend generates a JAR file that can be deployed to any Java-compatible server:
```bash
java -jar target/ip-backend-0.0.1-SNAPSHOT.jar
```

### Frontend Deployment
The frontend build can be deployed to:
- Vercel
- Netlify
- AWS S3 + CloudFront
- Azure Static Web Apps

## 🔒 Security

- JWT-based authentication
- Role-based authorization
- Secure password hashing
- OAuth2 integration
- CORS configuration

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 🐛 Troubleshooting

**Backend not starting?**
- Check PostgreSQL is running
- Verify database credentials in `application.properties`
- Ensure port 8080 is available

**Frontend not connecting to backend?**
- Verify backend is running on port 8080
- Check proxy configuration in `package.json`
- Clear browser cache

## 📄 License

This project is developed as part of the Springboard mentorship program.

## 👥 Team

Global IP Intelligence Platform - Group A

## 📞 Support

For issues and questions, please create an issue in the GitHub repository.

---

**Version:** 1.0.0  
**Last Updated:** December 2025
