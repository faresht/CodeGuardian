# 🛡️ CodeGuardian - AI-Powered Secure Code Analysis

**CodeGuardian** is a privacy-first, secure code analysis platform that combines traditional static analysis tools with local Large Language Models (LLMs) to detect, explain, and fix security vulnerabilities in your code. 

Built with **Spring Boot**, **Ollama**, and **Spring AI**, it ensures your code never leaves your infrastructure—making it perfect for enterprise environments where data privacy is paramount.

---

## 🚀 Features

*   **🔒 Privacy-First**: Uses local LLMs (via Ollama) so your sensitive source code never leaves your server.
*   **🧠 AI-Powered Insights**: Explains vulnerabilities in plain English and generates fix suggestions using the `qwen2.5-coder` model.
*   **🔍 Hybrid Analysis**: Combines AI analysis with industry-standard static analysis tools:
    *   **Java**: SpotBugs + AI
    *   **Python**: Bandit (Planned) + AI
    *   **JavaScript**: ESLint (Planned) + AI
*   **🛡️ Secure API**: Protected by JWT (JSON Web Tokens) authentication.
*   **📊 Comprehensive Reports**: Generates detailed JSON reports with severity levels, categories (OWASP Top 10), and actionable fixes.
*   **☁️ Dockerized**: Fully containerized setup for easy deployment.

---

## 🛠️ Tech Stack

*   **Backend**: Java 21, Spring Boot 3.2
*   **AI Integration**: Spring AI, Ollama
*   **LLM Model**: `qwen2.5-coder:1.5b` (Lightweight & Efficient)
*   **Database**: PostgreSQL
*   **Security**: Spring Security, JWT
*   **Tools**: SpotBugs, Maven, Docker & Docker Compose

---

## 📋 Prerequisites

*   [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running.
*   (Optional) Java 21 if running locally without Docker.

---

## ⚡ Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/faresht/CodeGuardian.git
cd codeguardian
```

### 2. Start the Application
Use Docker Compose to bring up the backend, database, and Ollama services.

```bash
docker-compose up --build
```
*Wait for the services to initialize. The first run may take a few minutes to pull the AI model.*

### 3. Access Swagger UI
Once running, open your browser to explore and test the APIs:
👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

---

## 📖 Usage Guide

### Step 1: Register User
Create a new account to get access.
*   **Endpoint**: `POST /api/auth/register`
*   **Body**:
    ```json
    {
      "username": "admin",
      "password": "securepassword",
      "role": "ROLE_USER"
    }
    ```

### Step 2: Login & Get Token
Login to receive your JWT Bearer token.
*   **Endpoint**: `POST /api/auth/authenticate`
*   **Body**:
    ```json
    {
      "username": "admin",
      "password": "securepassword"
    }
    ```
*   **Response**: Copy the `token` string.

### Step 3: Scan Code
Upload a source code file for analysis.
*   **Endpoint**: `POST /api/scan`
*   **Header**: `Authorization: Bearer <YOUR_TOKEN>`
*   **Body (form-data)**:
    *   `file`: (Upload a file, e.g., from `examples/VulnerableCode.java`)

### Step 4: View Report
You will receive a JSON response containing detected vulnerabilities, their severity, and AI-generated fixes.

---

## 📂 Project Structure

```
codeguardian/
├── src/                  # Source code
├── docker/               # Docker configurations
├── examples/             # Vulnerable code samples for testing
├── docker-compose.yml    # Container orchestration
└── pom.xml               # Maven dependencies
```

---

## 🐛 Troubleshooting

**Scans return 0 vulnerabilities?**
1.  Ensure the Ollama container is running and the model is loaded.
2.  If the model is missing, pull it manually:
    ```bash
    docker exec secure-code-platform-ollama-1 ollama pull qwen2.5-coder:1.5b
    ```
3.  Check the backend logs for `Raw AI Response`:
    ```bash
    docker logs secure-code-platform-backend-1
    ```

---

## 🤝 Contributing
Contributions are welcome! Please open an issue or submit a pull request.


