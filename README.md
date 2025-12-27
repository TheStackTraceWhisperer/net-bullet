# net-bullet

A high-quality Java 25 project with industrial-grade quality control and testing infrastructure.

## Prerequisites

- Java 25 or higher
- Maven 3.8.0 or higher

## Quality Control Infrastructure

This project enforces extremely high standards for code quality, security, and maintainability through a comprehensive suite of plugins and tools.

### Code Quality Tools

#### 1. **SpotBugs** - Static Bug Detection
- Detects potential bugs at the bytecode level
- Includes FindSecBugs for security vulnerability detection
- Configuration: Maximum effort, Low threshold
- Fails build on any detected issues

```bash
mvn spotbugs:check
```

#### 2. **Checkstyle** - Code Style Enforcement
- Enforces consistent coding standards
- Configuration: `checkstyle.xml`
- Validates naming conventions, formatting, and structure
- Fails on warnings

```bash
mvn checkstyle:check
```

#### 3. **PMD** - Code Quality Analysis
- Detects code smells, potential bugs, and anti-patterns
- Includes CPD (Copy-Paste Detector)
- Configuration: `pmd-ruleset.xml`
- Enforces best practices across multiple categories

```bash
mvn pmd:check
mvn pmd:cpd-check
```

#### 4. **JaCoCo** - Code Coverage
- Minimum line coverage: 80%
- Minimum branch coverage: 70%
- Generates detailed coverage reports
- Fails build if thresholds not met

```bash
mvn jacoco:report
```

### Security Tools

#### 5. **OWASP Dependency-Check**
- Scans dependencies for known security vulnerabilities
- Fails build on CVSS score ≥ 7
- Generates HTML and JSON reports

```bash
mvn dependency-check:check
```

#### 6. **Maven Enforcer**
- Enforces build environment requirements
- Requires Java 25+, Maven 3.8.0+
- Ensures dependency convergence
- Requires explicit plugin versions

### Testing Infrastructure

#### 7. **JUnit 5** - Unit Testing
- Modern testing framework with assertions
- Supports parameterized tests and test lifecycle
- Convention: `*Test.java` files

```bash
mvn test
```

#### 8. **Testcontainers** - Integration Testing
- Containerized testing for databases and services
- Preferred over mocking for integration tests
- Convention: `*IT.java` files

```bash
mvn verify
```

#### 9. **AssertJ** - Fluent Assertions
- Provides readable and maintainable test assertions
- Enhances test clarity

### Code Formatting

#### 10. **Formatter Maven Plugin**
- Enforces consistent code formatting
- Configuration: `formatter.xml`
- Based on Eclipse formatter

```bash
mvn formatter:format    # Auto-format code
mvn formatter:validate  # Check formatting
```

#### 11. **License Maven Plugin**
- Ensures license headers on all source files
- Configuration: `LICENSE_HEADER.txt`
- Enforces Apache 2.0 license

```bash
mvn license:format  # Add headers
mvn license:check   # Verify headers
```

### Documentation

#### 12. **Maven Javadoc Plugin**
- Generates API documentation
- Fails on missing or invalid Javadoc
- Includes preview features support

```bash
mvn javadoc:javadoc
```

### Dependency Management

#### 13. **Versions Maven Plugin**
- Check for dependency updates
- Manage versions across the project

```bash
mvn versions:display-dependency-updates
mvn versions:display-plugin-updates
```

## Build Commands

### Standard Build
```bash
mvn clean verify
```

### Full Quality Check (without OWASP, as it's slow)
```bash
mvn clean verify -DskipOWASP=true
```

### Quick Build (skip quality checks for development)
```bash
mvn clean install -DskipTests -Dcheckstyle.skip -Dpmd.skip -Dspotbugs.skip
```

### Run Only Tests
```bash
mvn test                    # Unit tests only
mvn integration-test        # Integration tests only
mvn verify                  # Both unit and integration tests
```

### Generate All Reports
```bash
mvn clean verify site
```

## Compiler Configuration

The project uses strict compiler settings to catch issues early:

- **Java 25 with preview features enabled**
- **All lint warnings enabled** (`-Xlint:all`)
- **Warnings treated as errors** (`-Werror`)
- **Deprecation warnings shown**

## Project Structure

```
net-bullet/
├── src/
│   ├── main/java/          # Production code
│   └── test/java/          # Test code
│       ├── *Test.java      # Unit tests (Surefire)
│       └── *IT.java        # Integration tests (Failsafe)
├── checkstyle.xml          # Checkstyle configuration
├── pmd-ruleset.xml         # PMD rules
├── formatter.xml           # Code formatter config
├── LICENSE_HEADER.txt      # License header template
└── pom.xml                 # Maven configuration
```

## Quality Thresholds

| Metric | Threshold | Tool |
|--------|-----------|------|
| Line Coverage | ≥ 80% | JaCoCo |
| Branch Coverage | ≥ 70% | JaCoCo |
| Security CVSS | < 7 | OWASP |
| Code Style | 0 violations | Checkstyle |
| Code Quality | 0 violations | PMD |
| Bug Detection | 0 issues | SpotBugs |

## Continuous Integration

The project includes GitHub Actions CI that:
- Builds on Java 25
- Runs all quality checks
- Executes all tests
- Publishes test results
- Uploads reports as artifacts

## Development Guidelines

1. **Write tests first** - Integration tests preferred over unit tests
2. **Run quality checks locally** before pushing
3. **Keep coverage high** - Aim for >80% line coverage
4. **Follow the style** - Use `mvn formatter:format` to auto-format
5. **Update dependencies carefully** - Check for vulnerabilities
6. **Document public APIs** - Javadoc required for public methods
7. **Handle exceptions properly** - No silent failures allowed
8. **Use modern Java 25 features** - Virtual threads, records, pattern matching

## Troubleshooting

### Build fails on quality checks
Run individual checks to identify issues:
```bash
mvn checkstyle:check    # Code style issues
mvn pmd:check          # Code quality issues
mvn spotbugs:check     # Potential bugs
```

### Dependency vulnerabilities
Check and update dependencies:
```bash
mvn versions:display-dependency-updates
mvn dependency:tree
```

### Coverage below threshold
Generate coverage report to identify gaps:
```bash
mvn clean test jacoco:report
# Open target/site/jacoco/index.html
```

## License

Apache License 2.0 - See LICENSE_HEADER.txt for details.
