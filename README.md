# CodeRover Spring Backend

## 1. Overview
CodeRover is an AI-assisted defect detection platform that integrates with GitHub to analyse source code and surface actionable findings to developers.  

The **Spring Boot backend** is the application server responsible for:
- Authentication (GitHub OAuth 2.0)
- API orchestration
- Repository and file browsing via the GitHub REST API
- Dispatching analysis requests to the Python-based ML service (FastAPI)

⚠️ Minimal user profile data is persisted for session continuity. **No source code is stored at rest.**

---

## 2. Key Features
- GitHub OAuth 2.0 login and secure session management  
- Repository listing, branch discovery, tree browsing, and file content retrieval (via GitHub API)  
- Analysis orchestration against the ML microservice (GPT-based and Salesforce CodeT5 pipelines)  
- Consistent JSON response envelope across endpoints  
- Production-ready patterns: Service Layer, DI/IoC, Feign clients, DTOs, Repository (Spring Data JPA), Builder, Exception translation, Mapper/Assembler  
- CI-ready with unit/integration tests and JaCoCo coverage reporting  
- Optional CI quality gate via GitHub Actions to fail builds when high-severity findings are detected  

---

## 3. Architecture (High Level)
```

Frontend (React SPA) → Spring Boot Backend →
(a) GitHub OAuth + REST API for repos/files
(b) FastAPI ML microservice for predictions

```
The persistence layer stores only the **User entity**.

---

## 4. Technology Stack
- Java 21 (or LTS used by your environment)  
- Spring Boot, Spring Security (OAuth2), Spring Data JPA  
- OpenFeign (typed HTTP clients)  
- Maven or Gradle (choose per project setup)  
- PostgreSQL / MySQL / H2 (pick your configured provider)  
- JUnit 5, Mockito, JaCoCo  

---

## 5. Prerequisites
- JDK 21 (or configured project JDK)  
- Git and GitHub account with OAuth App registered  
- Database instance (or H2 for local development)  
- Network access to GitHub API and ML service  
- Environment variables/properties configured (see below)  

---

## 6. Configuration
Application configuration is externalised.

**OAuth**
```

spring.security.oauth2.client.registration.github.client-id
spring.security.oauth2.client.registration.github.client-secret
spring.security.oauth2.client.registration.github.scope
spring.security.oauth2.client.provider.github.authorization-uri
spring.security.oauth2.client.provider.github.token-uri
spring.security.oauth2.client.provider.github.user-info-uri

```

**ML service**
```

review\.client.base-url
review\.client.timeout-ms
review\.client.prompt
codet5.client.base-url

```

**App settings**
```

user.default-profile-picture-url
cors.allowed-origins

```

**Database**
```

spring.datasource.url
spring.datasource.username
spring.datasource.password
spring.jpa.hibernate.ddl-auto

```

**JWT (if used for /api/v1/github/user)**
```

security.jwt.secret
security.jwt.cookie-name=access\_token
security.jwt.expiry

````

---

## 7. Running Locally
1. Configure OAuth App in GitHub  
   - Homepage URL: `http(s)://localhost:<port>`  
   - Authorization callback URL: `http(s)://localhost:<port>/login/oauth2/code/github`  

2. Set environment variables or `application.properties` / `application.yml`  

3. Start database (if not using H2)  

4. Build and run  
   ```bash
   # Maven
   mvn clean package
   java -jar target/coderover-backend.jar

   # Gradle
   ./gradlew build
   java -jar build/libs/coderover-backend.jar
````

5. Access application

   * Backend: [http://localhost:8080](http://localhost:8080)
   * Frontend should point to backend base URL

---

## 8. API Overview (Selected)

**Response envelope:**

```json
{ "message": "string", "data": "any", "statusCode": number }
```

**GitHub endpoints** (`/api/v1/github`)

* `GET /user/repos` → lists authenticated user repositories (filters Java repos)
* `GET /repos/{owner}/{repo}/tree?branch=main` → recursive Git tree
* `GET /repos/{owner}/{repo}/blob?sha=<sha>` → fetch file blob
* `GET /repos/{owner}/{repo}` → lists all branches
* `GET /user` → reads user info (JWT cookie required)

**Review endpoints** (`/api/v1/review`)

* `POST /api/v1/review` → request code review via OpenAI-compatible client
* `POST /api/v1/review/codeT5/v1` → request review via CodeT5

---

## 9. Testing and Quality

* Unit tests with JUnit 5 and Mockito
* Integration tests with Feign stubs or WireMock/MockWebServer
* JaCoCo coverage reports
* CI thresholds and gating recommended

---

## 10. CI/CD (GitHub Actions)

* Checkout, set up JDK
* Build & test backend, publish JaCoCo
* (Optional) Call ML service `/predict` on changed files; fail on high-severity findings

---

## 11. Security and Privacy

* OAuth tokens never exposed to browser
* Minimal user data persisted, **no repository source stored**
* CORS restricted to trusted origins
* Secure cookies & HTTPS in production

---

## 12. Deployment

* Containerise with Docker (JRE base)
* Configure env vars and secrets in Kubernetes/ECS/etc.
* Place behind reverse proxy or API gateway with TLS termination

---

## 13. Troubleshooting

* OAuth callback mismatch → check GitHub OAuth App settings
* `401 /api/v1/github/user` → check JWT cookie and secret
* `5xx GitHub` → confirm token validity, rate limits
* `5xx ML service` → verify base URL, timeouts, model readiness

---

## 14. Contributing

* Follow code style and package structure
* Add unit tests with changes
* Update API docs and config notes when modified

---



```
```
