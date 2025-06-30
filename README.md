# ⏳ Pomodoro Timer Web App

A simple RESTful Pomodoro timer built with **Spring Boot**. It helps users manage focus sessions using the Pomodoro technique. The app includes endpoints to start timers, manage tasks, and persist data using an embedded **H2 database**.

---

## 🚀 Features

- Start Pomodoro sessions with custom durations.
- Manage tasks (CRUD operations).
- Auto-persist session/task data using **H2 in-memory database**.
- RESTful API endpoints.
- Ready for extension with a frontend (React, Vue, etc.).

---

## 🛠️ Tech Stack

- Java 17
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- H2 Database
- Maven

---

## ⚙️ Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- VS Code or IntelliJ (recommended)

### Run the Project

1. Clone the repo:
   ```bash
   git clone https://github.com/your-username/pomodoro.git
   cd pomodoro

2. Build and run:
   ```bash
   mvn clean install
   mvn spring-boot:run

3. Access the app:
   ```arduino
   http://localhost:8080/
