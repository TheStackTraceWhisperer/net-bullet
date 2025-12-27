# Quality Metrics and Control

This document provides detailed information about the quality control infrastructure implemented in the Net Bullet project.

## Overview

The project enforces extremely high standards for code quality, security, and maintainability through a comprehensive suite of plugins and tools. This setup is designed to maintain industrial-grade stability and zero-defect correctness as mandated by the GitHub Copilot instructions.

## Implemented Quality Gates

### 1. Static Code Analysis

#### SpotBugs (v4.8.3.1)
- **Purpose**: Bytecode-level bug detection
- **Configuration**: Maximum effort, Low threshold
- **Security**: Includes FindSecBugs plugin for security vulnerability detection
- **Status**: Active (warnings mode, set to fail on errors after cleanup)
- **Reports**: `target/spotbugsXml.xml`

#### Checkstyle (v10.14.0)
- **Purpose**: Code style and convention enforcement
- **Configuration**: `checkstyle.xml` with strict rules
- **Standards Enforced**:
  - Naming conventions (constants, variables, methods, classes)
  - Import management (no star imports, no unused imports)
  - Whitespace and formatting consistency
  - Javadoc completeness for public APIs
  - Block structure (braces required)
  - Maximum line length: 120 characters
  - Maximum method length: 150 lines
  - Maximum parameters: 7
- **Status**: Active (warnings mode, set to fail on errors after cleanup)
- **Reports**: `target/checkstyle-result.xml`

#### PMD (v6.55.0)
- **Purpose**: Code quality and anti-pattern detection
- **Configuration**: `pmd-ruleset.xml` with comprehensive rules
- **Rule Categories**:
  - Best Practices
  - Code Style
  - Design
  - Documentation
  - Error Prone patterns
  - Multithreading issues
  - Performance problems
  - Security vulnerabilities
- **CPD**: Copy-Paste Detector (minimum 100 tokens)
- **Status**: Active (warnings mode, set to fail on errors after cleanup)
- **Reports**: `target/pmd.xml`, `target/cpd.xml`

### 2. Code Coverage

#### JaCoCo (v0.8.11)
- **Purpose**: Test coverage measurement
- **Thresholds**:
  - Line Coverage: ≥ 80%
  - Branch Coverage: ≥ 70%
- **Configuration**: Fails build if thresholds not met
- **Reports**: `target/site/jacoco/index.html`

### 3. Security

#### OWASP Dependency-Check (v9.0.9)
- **Purpose**: Identifies known vulnerabilities in dependencies
- **Configuration**: Fails build on CVSS ≥ 7
- **Status**: Skipped by default (run manually due to execution time)
- **Enable**: `mvn verify -Ddependency-check.skip=false`
- **Reports**: `dependency-check-report.html`, `dependency-check-report.json`

#### FindSecBugs (v1.13.0)
- **Purpose**: Security-focused bug detection
- **Integration**: Included as SpotBugs plugin
- **Detects**: SQL injection, XSS, insecure crypto, etc.

### 4. Build Enforcement

#### Maven Enforcer Plugin (v3.4.1)
- **Requirements**:
  - Maven: ≥ 3.8.0
  - Java: ≥ 17 (targeting Java 25 for production)
  - All plugins must have explicit versions
  - No dependency version conflicts
  - Dependency convergence required
- **Status**: Active (fails build on violations)

### 5. Code Formatting

#### Formatter Maven Plugin (v2.23.0)
- **Purpose**: Consistent code formatting
- **Configuration**: `formatter.xml` (Eclipse formatter style)
- **Standards**:
  - 4-space indentation
  - 120 character line limit
  - End-of-line: LF
  - Brace position: end of line
- **Status**: Skipped initially (enable after formatting existing code)
- **Enable**: Remove `<skip>true</skip>` from pom.xml

#### License Maven Plugin (v4.3)
- **Purpose**: License header management
- **Configuration**: `LICENSE_HEADER.txt` (Apache 2.0)
- **Applies to**: All `.java` files in src/main and src/test
- **Status**: Skipped initially (enable after adding headers)
- **Enable**: Remove `<skip>true</skip>` from pom.xml

### 6. Documentation

#### Maven Javadoc Plugin (v3.6.3)
- **Purpose**: API documentation generation
- **Configuration**: Fails on errors or warnings
- **Level**: All access modifiers (public, protected, private)
- **Status**: Active (generates javadoc JAR)

### 7. Testing

#### JUnit 5 (v5.10.2)
- **Purpose**: Unit testing framework
- **Convention**: `*Test.java` files
- **Plugin**: Maven Surefire (v3.2.5)

#### Testcontainers (v1.19.7)
- **Purpose**: Integration testing with containerized dependencies
- **Convention**: `*IT.java` files
- **Plugin**: Maven Failsafe (v3.2.5)
- **Preferred over**: Mocking frameworks for database/cache testing

#### AssertJ (v3.25.3)
- **Purpose**: Fluent assertions
- **Benefits**: Improved test readability and maintainability

## Gradual Adoption Strategy

The quality control infrastructure is configured for gradual adoption to avoid overwhelming existing codebases:

### Phase 1: Monitoring (Current)
- All plugins active but in **warning mode**
- Violations reported but don't fail builds
- Teams can review and address issues incrementally

### Phase 2: Enforcement (Future)
Once issues are addressed, enable strict enforcement by changing these flags in `pom.xml`:

```xml
<!-- Checkstyle -->
<failsOnError>true</failsOnError>

<!-- PMD -->
<failOnViolation>true</failOnViolation>

<!-- SpotBugs -->
<failOnError>true</failOnError>

<!-- Compiler -->
<arg>-Werror</arg> <!-- Treat warnings as errors -->

<!-- Formatter -->
<skip>false</skip>

<!-- License -->
<skip>false</skip>
```

## Build Commands

### Standard Build (with quality checks)
```bash
mvn clean verify
```

### Quick Build (skip quality for development)
```bash
mvn clean install -DskipTests -Dcheckstyle.skip -Dpmd.skip -Dspotbugs.skip
```

### Security Scan
```bash
mvn verify -Ddependency-check.skip=false
```

### Individual Plugin Execution
```bash
mvn checkstyle:check      # Code style
mvn pmd:check             # Code quality
mvn pmd:cpd-check         # Duplicate code
mvn spotbugs:check        # Bug detection
mvn jacoco:report         # Coverage report
mvn javadoc:javadoc       # API documentation
```

### Dependency Management
```bash
mvn versions:display-dependency-updates  # Check for updates
mvn versions:display-plugin-updates      # Check plugin updates
mvn dependency:tree                      # View dependency tree
```

### Code Formatting
```bash
mvn formatter:format      # Auto-format code
mvn formatter:validate    # Check formatting
mvn license:format        # Add license headers
mvn license:check         # Verify license headers
```

## Quality Metrics Dashboard

### Current Status (Example)
| Metric | Target | Current | Status |
|--------|--------|---------|--------|
| Line Coverage | ≥ 80% | TBD | 🟡 Monitoring |
| Branch Coverage | ≥ 70% | TBD | 🟡 Monitoring |
| Checkstyle Violations | 0 | TBD | 🟡 Monitoring |
| PMD Violations | 0 | TBD | 🟡 Monitoring |
| SpotBugs Issues | 0 | TBD | 🟡 Monitoring |
| Security Vulnerabilities | 0 | TBD | 🟡 Monitoring |
| Code Duplication | Minimal | TBD | 🟡 Monitoring |

### Legend
- 🟢 **Passing**: Meets all thresholds
- 🟡 **Monitoring**: Tracking but not enforcing
- 🔴 **Failing**: Below threshold, needs attention

## CI/CD Integration

The GitHub Actions CI workflow automatically:
1. Builds the project with Java 25 (when available, currently Java 17)
2. Runs all unit tests (Surefire)
3. Runs all integration tests (Failsafe)
4. Executes quality checks (Checkstyle, PMD, SpotBugs)
5. Generates coverage reports (JaCoCo)
6. Publishes test results
7. Uploads artifacts (test reports, quality reports)

## Plugin Versions

| Plugin | Version | Purpose |
|--------|---------|---------|
| maven-compiler-plugin | 3.13.0 | Java compilation |
| maven-surefire-plugin | 3.2.5 | Unit tests |
| maven-failsafe-plugin | 3.2.5 | Integration tests |
| maven-enforcer-plugin | 3.4.1 | Build rules |
| spotbugs-maven-plugin | 4.8.3.1 | Bug detection |
| maven-checkstyle-plugin | 3.3.1 | Code style |
| maven-pmd-plugin | 3.21.2 | Code quality |
| jacoco-maven-plugin | 0.8.11 | Code coverage |
| dependency-check-maven | 9.0.9 | Security scanning |
| versions-maven-plugin | 2.16.2 | Dependency management |
| formatter-maven-plugin | 2.23.0 | Code formatting |
| license-maven-plugin | 4.3 | License headers |
| maven-javadoc-plugin | 3.6.3 | Documentation |
| maven-source-plugin | 3.3.0 | Source JAR |
| maven-jar-plugin | 3.3.0 | Artifact creation |

## Dependency Versions

| Dependency | Version | Purpose |
|------------|---------|---------|
| junit-jupiter | 5.10.2 | Testing framework |
| testcontainers | 1.19.7 | Integration testing |
| assertj-core | 3.25.3 | Fluent assertions |
| spotbugs-annotations | 4.8.3 | Bug prevention |

## Configuration Files

| File | Purpose |
|------|---------|
| `checkstyle.xml` | Checkstyle rules and configuration |
| `pmd-ruleset.xml` | PMD rules and exclusions |
| `formatter.xml` | Code formatting standards |
| `LICENSE_HEADER.txt` | License header template |
| `.gitignore` | Excludes quality reports from version control |

## Best Practices

1. **Run quality checks locally** before pushing code
2. **Address violations incrementally** - don't accumulate technical debt
3. **Review reports regularly** - use `target/site/` for HTML reports
4. **Keep dependencies updated** - check for security vulnerabilities
5. **Maintain high coverage** - especially for critical paths
6. **Write integration tests** - preferred over unit tests per project guidelines
7. **Document public APIs** - required Javadoc for public methods
8. **Follow formatter standards** - use `mvn formatter:format`

## Troubleshooting

### Build Fails on Enforcer
**Issue**: Missing plugin versions or dependency conflicts
**Solution**: Add explicit versions in `<pluginManagement>` or resolve conflicts

### Checkstyle Violations
**Issue**: Code style inconsistencies
**Solution**: Review `checkstyle.xml` and fix issues or request rule adjustments

### PMD Violations
**Issue**: Code quality issues detected
**Solution**: Refactor code or add justified exclusions to `pmd-ruleset.xml`

### SpotBugs Issues
**Issue**: Potential bugs detected
**Solution**: Fix the bug or add `@SuppressFBWarnings` with justification

### Coverage Below Threshold
**Issue**: Insufficient test coverage
**Solution**: Add tests for uncovered code paths

### OWASP Vulnerabilities
**Issue**: Security vulnerabilities in dependencies
**Solution**: Update dependencies or accept risk with documented justification

## Future Enhancements

1. **Mutation Testing**: Add PITest for test quality verification
2. **Architecture Rules**: Add ArchUnit for architectural constraints
3. **Performance Testing**: Add JMH for performance regression detection
4. **Container Security**: Add Trivy for container vulnerability scanning
5. **SonarQube Integration**: For centralized quality management
6. **Custom Rules**: Project-specific Checkstyle/PMD rules

## References

- [Checkstyle Documentation](https://checkstyle.org/)
- [PMD Documentation](https://pmd.github.io/)
- [SpotBugs Documentation](https://spotbugs.github.io/)
- [JaCoCo Documentation](https://www.jacoco.org/)
- [OWASP Dependency-Check](https://owasp.org/www-project-dependency-check/)
- [Testcontainers Documentation](https://www.testcontainers.org/)

---

*Last Updated: 2025-12-27*
*Maintained by: Net Bullet Team*
