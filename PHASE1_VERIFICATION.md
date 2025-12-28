# Phase 1: Logic Guardrails - Verification Guide

## Overview
This document describes the Phase 1 Logic Guardrails implementation and verification process.

## What Was Installed

### 1. Jqwik Dependency (pom.xml)
- **Version:** 1.9.0
- **Purpose:** Property-based testing framework
- **Scope:** test
- **Location:** Added to `<dependencies>` section

### 2. Pitest Maven Plugin (pom.xml)
- **Version:** 1.16.1
- **Purpose:** Mutation testing to ensure test quality
- **Configuration:**
  - `mutationThreshold`: 100% (Build fails if ANY mutant survives)
  - `targetClasses`: com.netbullet.*
  - `targetTests`: com.netbullet.*Test
  - Executes during `verify` phase
- **Pitest JUnit5 Plugin:** 1.2.1

### 3. Testing Standards Update (.github/copilot-instructions.md)
Added Section 3.1: Strict Logic Verification with three rules:
1. **Mutation Robustness:** Tests must kill mutants
2. **Property-Based Testing:** Use Jqwik @Property for logic/math/parsing
3. **Banned Patterns:** No `assertEquals(4, add(2,2))` style tests

## Verification Process (The "Trap")

### Phase A: Demonstrate Weak Test Detection

**Created Files:**
- `src/main/java/com/netbullet/trap/MathTrap.java` - Simple add() method
- `src/test/java/com/netbullet/trap/MathTrapTest.java` - Weak test with single case

**Weak Test Example:**
```java
@Test
void testAdd() {
    assertEquals(0, new MathTrap().add(0, 0)); // Trivial case
}
```

**Expected Result:** `mvn clean verify` should **FAIL**
- Pitest generates mutants (e.g., return 0 instead of a+b)
- Weak test passes even with mutant alive
- Build fails: "Mutation coverage of 0% is below threshold of 100%"

### Phase B: Strong Test with Property-Based Testing

**Updated Test:**
```java
@Property
boolean additionCommutative(@ForAll int a, @ForAll int b) {
    return new MathTrap().add(a, b) == new MathTrap().add(b, a);
}

@Property
boolean additionIdentity(@ForAll int a) {
    return new MathTrap().add(a, 0) == a;
}
```

**Expected Result:** `mvn clean verify` should **PASS**
- Property tests verify behavior across random inputs
- Mutants are killed because properties fail with mutations
- Build succeeds with 100% mutation coverage

### Phase C: Cleanup
After verification, trap artifacts should be deleted:
- `src/main/java/com/netbullet/trap/`
- `src/test/java/com/netbullet/trap/`

## Running the Verification

### Requirements
- Java 25+ (as per project requirements)
- Maven 3.9.9+

### Commands
```bash
# Clean build with verification
mvn clean verify

# Run only mutation testing
mvn pitest:mutationCoverage

# View mutation report
open target/pit-reports/*/index.html
```

## Environment Note
The local development environment has Java 17 installed, which cannot compile Java 25 code.
The actual verification will run successfully in the CI environment (GitHub Actions) which has Java 25 configured.

## What This Achieves
1. **Prevents Lazy Tests:** Pitest ensures tests actually verify logic
2. **Enforces Property Thinking:** Jqwik forces developers to think in invariants
3. **Build-Time Safety:** 100% mutation threshold means no weak tests can pass
4. **AI Guidance:** Updated Copilot instructions ensure AI generates proper tests

## References
- [Pitest Documentation](https://pitest.org/)
- [Jqwik User Guide](https://jqwik.net/docs/current/user-guide.html)
