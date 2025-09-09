# Contributing to CodeRover

Thank you for considering contributing to CodeRover! This document provides guidelines and information about contributing to this project.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Making Changes](#making-changes)
- [Testing](#testing)
- [Pull Request Process](#pull-request-process)
- [Style Guidelines](#style-guidelines)

## 📜 Code of Conduct

By participating in this project, you agree to abide by our Code of Conduct. Please be respectful, inclusive, and constructive in all interactions.

## 🚀 Getting Started

### Prerequisites

Before you begin, ensure you have:
- Java 21 or higher
- Maven 3.9+
- MySQL 8.0+
- Git
- A GitHub account

### Development Setup

1. **Fork the repository** on GitHub

2. **Clone your fork**:
   ```bash
   git clone https://github.com/your-username/coderover-spring-web.git
   cd coderover-spring-web
   ```

3. **Add upstream remote**:
   ```bash
   git remote add upstream https://github.com/DamianRavinduPeiris/coderover-spring-web.git
   ```

4. **Set up environment**:
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

5. **Create database**:
   ```sql
   CREATE DATABASE coderover_dev;
   ```

6. **Build the project**:
   ```bash
   export JAVA_HOME=/path/to/java21
   mvn clean compile
   ```

## 🔄 Making Changes

### Branch Strategy

1. **Create a feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Keep your branch updated**:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

### Commit Guidelines

- Use clear, descriptive commit messages
- Start with a verb in imperative mood (e.g., "Add", "Fix", "Update")
- Limit the first line to 50 characters
- Reference issues when applicable

**Good commit examples**:
```
Add GitHub OAuth2 configuration validation
Fix repository tree traversal for deep directories
Update email service to handle SMTP timeouts
```

## 🧪 Testing

### Running Tests

```bash
# Run unit tests
mvn test

# Run with coverage
mvn clean verify

# View coverage report
open target/site/jacoco/index.html
```

### Writing Tests

- Write unit tests for new functionality
- Follow the existing test patterns
- Use meaningful test names that describe what is being tested
- Mock external dependencies appropriately

**Test structure**:
```java
@ExtendWith(MockitoExtension.class)
class YourServiceTest {
    
    @Mock
    private DependencyService dependencyService;
    
    @InjectMocks
    private YourService yourService;
    
    @Test
    @DisplayName("Should return expected result when valid input provided")
    void shouldReturnExpectedResultWhenValidInputProvided() {
        // Given
        // When
        // Then
    }
}
```

## 📤 Pull Request Process

### Before Submitting

1. **Ensure tests pass**: `mvn test`
2. **Check code formatting**: Follow the project's style guidelines
3. **Update documentation**: If you've changed APIs or added features
4. **Rebase on main**: Ensure your branch is up to date

### Submitting the Pull Request

1. **Push your changes**:
   ```bash
   git push origin feature/your-feature-name
   ```

2. **Create a Pull Request** on GitHub with:
   - Clear title and description
   - Reference to any related issues
   - Screenshots if UI changes are involved
   - List of changes made

3. **Fill out the PR template** (if available)

### PR Review Process

- Maintainers will review your PR
- Address any requested changes
- Once approved, your PR will be merged

## 🎨 Style Guidelines

### Java Code Style

- Follow Java naming conventions
- Use meaningful variable and method names
- Keep methods small and focused
- Add Javadoc for public methods
- Use Lombok annotations to reduce boilerplate

**Example**:
```java
@Service
@RequiredArgsConstructor
@Log4j2
public class GitHubServiceImpl implements GitHubService {

    private final GitHubClient gitHubClient;

    /**
     * Fetches user repositories filtered by programming language.
     *
     * @param accessToken GitHub access token
     * @param language Programming language filter
     * @return List of repositories
     */
    public List<RepoDTO> fetchUserRepositories(String accessToken, String language) {
        // Implementation
    }
}
```

### Package Structure

Follow the existing package structure:
```
com.damian.coderover/
├── config/      # Configuration classes
├── controller/  # REST controllers
├── dto/         # Data transfer objects
├── entity/      # JPA entities
├── exception/   # Custom exceptions
├── feign/       # Feign clients
├── service/     # Business logic
└── util/        # Utility classes
```

## 🐛 Reporting Issues

When reporting issues, please include:

- **Description**: Clear description of the problem
- **Steps to reproduce**: Detailed steps to reproduce the issue
- **Expected behavior**: What you expected to happen
- **Actual behavior**: What actually happened
- **Environment**: Java version, OS, etc.
- **Logs**: Relevant error messages or stack traces

## ❓ Questions?

If you have questions about contributing, please:

1. Check existing issues and discussions
2. Create a new issue with the "question" label
3. Join our discussions on GitHub

## 🎉 Recognition

All contributors will be recognized in our README and release notes. Thank you for making CodeRover better!

---

**Happy contributing! 🚀**