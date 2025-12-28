# ADR 000: Java 25 Runtime Mandate

**Status:** ACCEPTED
**Date:** 2025-12-28
**Supersedes:** N/A
**Context:** This is a foundational decision that affects all other architectural choices.

## The Decision

The `net-bullet` project **EXCLUSIVELY targets Java 25** as its runtime platform. This is non-negotiable and enforced at build time.

## The Rationale

### 1. Virtual Threads (Project Loom)
Java 25 provides stable Virtual Threads, which are essential for high-concurrency game server workloads. We can spawn millions of virtual threads with minimal overhead, enabling:
- One thread per connection without OS thread exhaustion
- Simplified async programming (no callback hell)
- Better debuggability than reactive programming

### 2. Pattern Matching & Records
Modern Java syntax reduces boilerplate and improves code clarity:
- Record types for immutable DTOs (e.g., player position, game state)
- Pattern matching for complex message routing
- Sealed types for type-safe protocol definitions

### 3. Performance Improvements
Java 25 includes cumulative JIT optimizations, GC improvements, and JVM enhancements that directly benefit our use case:
- Faster startup time
- Lower memory footprint
- Better throughput for network I/O

### 4. Security
Newer Java versions receive security updates. Running on Java 8/11/17 exposes the project to known CVEs that won't be patched.

### 5. Future-Proofing
By adopting Java 25 now, we avoid painful migration work later. The ecosystem is moving forward, and staying on old versions creates technical debt.

## Enforcement Mechanism

### Maven Enforcer Plugin
```xml
<requireJavaVersion>
    <version>[25,)</version>
    <message>STRICT COMPLIANCE: Java 25 is mandatory. Found ${java.version}</message>
</requireJavaVersion>
```

This causes the build to **FAIL IMMEDIATELY** if Java 25 is not detected.

### CI Pipeline
GitHub Actions explicitly configures Java 25:
```yaml
- name: ☕ Set up JDK 25
  uses: actions/setup-java@v4
  with:
    java-version: '25'
    distribution: 'temurin'
```

### Developer Environment
- `.java-version` file for jenv/asdf/mise
- `.sdkmanrc` file for SDKMAN!
- Documentation in [SETUP.md](../SETUP.md)

## Migration Strategy (For Existing Codebases)

N/A - This is a greenfield project.

## Consequences

### Positive
✅ Access to cutting-edge JVM features
✅ Best performance and security
✅ Simplified concurrency model (Virtual Threads)
✅ Modern syntax reduces bugs

### Negative
❌ Requires Java 25 installed locally (setup friction)
❌ Some IDEs may have limited Java 25 support initially
❌ Hosting providers must support Java 25 (most do)

### Neutral
⚠️ No fallback to older Java versions
⚠️ Developers must upgrade their environments

## Alternatives Considered

### Alternative 1: Target Java 17 LTS
**Rejected.** While LTS is stable, it lacks Virtual Threads and pattern matching. We'd need complex async frameworks (Reactor/RxJava) to achieve similar concurrency, increasing complexity.

### Alternative 2: Multi-Release JAR (MRJAR)
**Rejected.** Adds build complexity and testing burden. We'd need to maintain two codebases (Java 11 + Java 25). Not worth it for a new project.

### Alternative 3: Use Java 21 LTS
**Rejected.** Java 21 has Virtual Threads, but Java 25 includes additional pattern matching improvements and performance optimizations. Since we're starting fresh, we might as well use the latest.

## Review Schedule

This decision will be reviewed **only if**:
1. A critical security vulnerability in Java 25 requires downgrading
2. A major hosting provider drops Java 25 support
3. The JVM community abandons Java 25 in favor of a new platform

**Current Status:** No review needed. Java 25 is stable and widely supported.

## References

- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444)
- [JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441)
- [Java 25 Release Notes](https://jdk.java.net/25/release-notes)
- [Adoptium Eclipse Temurin Downloads](https://adoptium.net/)

## Implementation Checklist

- [x] Set `maven.compiler.release=25` in pom.xml
- [x] Configure maven-enforcer-plugin with `requireJavaVersion`
- [x] Update CI pipeline to use Java 25
- [x] Create `.java-version` and `.sdkmanrc` files
- [x] Document installation in SETUP.md
- [x] Add warning in README.md
- [x] Update all ADR/spec headers with "Runtime: Java 25"
- [x] Add Copilot Instructions enforcement (Section 1)

---

**This ADR is foundational. All other ADRs assume Java 25 as the baseline.**
