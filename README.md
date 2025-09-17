# CodeRover - AI Code Review Companion

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen)
![Maven](https://img.shields.io/badge/Maven-3.9+-blue)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1)

## Overview

CodeRover is a sophisticated Spring Boot web application that provides AI-powered code review services with seamless GitHub integration. The application enables developers to authenticate via GitHub OAuth2, browse their repositories, and leverage multiple AI models (GPT-5 and CodeT5) for intelligent code analysis and review.

## Key Features

### Authentication & Security
- **GitHub OAuth2 Integration**: Secure login using GitHub credentials
- **JWT Token Management**: Session management with JSON Web Tokens
- **Spring Security**: Comprehensive security configuration with role-based access

### GitHub Integration
- **Repository Management**: Fetch and browse user repositories (Java-focused filtering)
- **File System Navigation**: Explore repository trees and file structures
- **Branch Management**: Access and switch between different repository branches
- **File Content Retrieval**: Read and analyze individual file contents via GitHub API

### AI-Powered Code Review
- **Multiple AI Providers**: 
  - GPT-5 integration via custom Review Client
  - CodeT5 model integration for specialized code analysis
- **Intelligent Analysis**: Automated code review with detailed feedback
- **Build Status Review**: Specialized prompts for build and deployment analysis
- **Customizable Prompts**: Configurable review prompts for different scenarios

### Communication
- **Email Reports**: Send formatted code review reports via SMTP
- **Real-time Notifications**: Instant feedback on code analysis results

### Data Management
- **MySQL Integration**: Persistent data storage with Spring Data JPA
- **User Management**: Store and manage user profiles and preferences
- **Repository Tracking**: Track user interactions and review history

## Technology Stack

### Backend Framework
- **Java 21**: Latest LTS version for optimal performance
- **Spring Boot 3.5.0**: Modern Spring framework with latest features
- **Spring Security**: OAuth2 and JWT authentication
- **Spring Data JPA**: Database abstraction and ORM
- **Spring Web**: RESTful web services

### External Integrations
- **OpenFeign**: Declarative HTTP client for external APIs
- **GitHub API**: Complete GitHub integration
- **OAuth2 Client**: GitHub OAuth2 authentication flow

### Database & Persistence
- **MySQL 8**: Primary database for data persistence
- **Hibernate**: JPA implementation for ORM
- **Connection Pooling**: Optimized database connections

### Development & Testing
- **Maven**: Dependency management and build automation
- **JUnit 5**: Modern testing framework
- **Mockito**: Mocking framework for unit tests
- **AssertJ**: Fluent assertion library
- **Jacoco**: Code coverage analysis

### Utilities & Libraries
- **Lombok**: Reduce boilerplate code
- **Log4j2**: Advanced logging framework
- **JJWT**: JSON Web Token library
- **Jakarta Mail**: Email functionality

## Prerequisites

- **Java 21** or higher
- **Maven 3.9+**
- **MySQL 8.0+**
- **Docker** (optional, for containerized deployment)

## Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/DamianRavinduPeiris/coderover-spring-web.git
cd coderover-spring-web
```

### 2. Database Setup
Create a MySQL database for the application:
```sql
CREATE DATABASE coderover;
```

### 3. Environment Configuration
Create a `.env` file or set the following environment variables:

```bash
# GitHub OAuth2
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret

# Database Configuration
DB_URL=jdbc:mysql://localhost:3306/coderover
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# JWT Configuration
JWT_SECRET=your_jwt_secret_key

# Frontend Configuration
FRONTEND_REDIRECT_URI=http://localhost:3000

# Email Configuration
MAIL_HOST=smtp.gmail.com
APP_USERNAME=your_email@gmail.com
APP_PASSWORD=your_app_password
MAIL_FROM=your_email@gmail.com
MAIL_DEBUG=false

# AI Review Service Configuration
REVIEW_CLIENT_BASE_URL=your_review_api_url
REVIEW_CLIENT_TOKEN=your_review_api_token
REVIEW_CLIENT_PROMPT=your_custom_review_prompt
REVIEW_CLIENT_BUILD_PROMPT=your_build_review_prompt

# CodeT5 Configuration
CODE_T5_V1_BASE_URL=your_codet5_api_url

# User Configuration
USER_DEFAULT_PROFILE_PICTURE_URL=https://github.com/identicons/default.png
```

### 4. Build and Run
```bash
# Using Maven
mvn clean install
mvn spring-boot:run

# Using Java directly
java -jar target/coderover-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## Docker Deployment

### Build Docker Image
```bash
docker build -t coderover-spring .
```

### Run with Docker
```bash
docker run -p 8080:8080 \
  -e GITHUB_CLIENT_ID=your_client_id \
  -e GITHUB_CLIENT_SECRET=your_client_secret \
  -e DB_URL=your_db_url \
  -e DB_USERNAME=your_db_user \
  -e DB_PASSWORD=your_db_password \
  -e JWT_SECRET=your_jwt_secret \
  coderover-spring
```

## API Documentation

### Authentication Endpoints
- `GET /oauth2/authorization/github` - Initiate GitHub OAuth2 flow
- `POST /login/oauth2/code/github` - GitHub OAuth2 callback

### GitHub Integration
- `GET /api/v1/github/user/repos` - Fetch user repositories
- `GET /api/v1/github/repos/{owner}/{repo}/tree` - Get repository file tree
- `GET /api/v1/github/repos/{owner}/{repo}/blob` - Get file content
- `GET /api/v1/github/repos/{owner}/{repo}` - Get repository branches
- `GET /api/v1/github/user` - Get user information

### Code Review Services
- `POST /api/v1/review` - Request standard code review
- `POST /api/v1/review/codeT5/v1` - Request CodeT5 model review
- `POST /api/v1/review/status` - Request build status review

### Email Services
- `POST /email/send-report` - Send code review report via email

### Health Check
- `GET /health` - Application health status
- `GET /actuator/health` - Detailed health information

## Testing

### Run Unit Tests
```bash
mvn test
```

### Run with Coverage
```bash
mvn clean verify
```

Coverage reports will be generated in `target/site/jacoco/index.html`

### Test Configuration
The project includes comprehensive test exclusions in `pom.xml` to focus on service layer testing while excluding configuration and DTO classes.

## CI/CD Pipeline

The project includes GitHub Actions workflow for automated review and build:

### Workflow Features
- **Automated Code Review**: AI-powered review of changed Java files on pull requests
- **Build Validation**: Maven build execution with Java 21
- **Quality Gates**: Build blocking based on review feedback
- **Caching**: Maven dependencies caching for faster builds

### Workflow Triggers
- Pull requests to `master` branch
- Automatic review of all changed Java files
- Build execution only if review passes

## Project Structure

```
src/
├── main/
│   ├── java/com/damian/coderover/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # JPA entities
│   │   ├── exception/       # Custom exceptions
│   │   ├── feign/           # Feign clients for external APIs
│   │   ├── filter/          # Security filters
│   │   ├── handler/         # OAuth2 handlers
│   │   ├── repository/      # Data repositories
│   │   ├── response/        # Response models
│   │   ├── service/         # Business logic services
│   │   └── util/            # Utility classes
│   └── resources/
│       └── application.yml  # Application configuration
└── tests/
    └── java/               # Test classes
```

## Configuration Details

### GitHub OAuth2 Setup
1. Go to GitHub → Settings → Developer settings → OAuth Apps
2. Create a new OAuth App with:
   - Application name: CodeRover
   - Homepage URL: `http://localhost:8080`
   - Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`
3. Copy the Client ID and Client Secret to your environment variables

### AI Review Service Setup
Configure your preferred AI review service endpoints and authentication tokens in the environment variables.

### Environment Files
- Copy `.env.example` to `.env` and configure your settings
- Never commit `.env` files to version control
- Use different environment files for different deployment environments

## Additional Resources

- **[CONTRIBUTING.md](CONTRIBUTING.md)**: Detailed contribution guidelines
- **[.env.example](.env.example)**: Environment variables template

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow Java coding conventions
- Write unit tests for new features
- Update documentation as needed
- Ensure all tests pass before submitting PR

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

**Damian Ravindu Peiris**
- GitHub: [@DamianRavinduPeiris](https://github.com/DamianRavinduPeiris)
- Email: [Contact via GitHub](https://github.com/DamianRavinduPeiris)

## Acknowledgments

- Spring Boot team for the excellent framework
- GitHub for their comprehensive API
- OpenAI and Hugging Face for AI model access
- All contributors and users of this project

---

**Ready to revolutionize your code review process? Get started with CodeRover today!**
