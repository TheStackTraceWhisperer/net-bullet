# ADR 001: Pure Java Dependency Injection

**Status:** ACCEPTED
**Date:** 2025-12-28
**Runtime:** Java 25
**Context:** The `net-bullet` project targets "Industrial Grade" performance, sub-millisecond startup times, and zero "magic" behavior.

## The Decision
We will use **Manual Constructor Injection** for all component wiring. We explicitly **REJECT** the inclusion of Dependency Injection frameworks (Spring Boot, Micronaut, Quarkus, Guice, Dagger).

## The Reasoning (The "Why")

1.  **Startup Latency:** Frameworks involve classpath scanning, configuration parsing, and proxy generation. This costs 500ms+ on startup. Our target is <50ms.
2.  **Runtime Overhead:** Many frameworks use Reflection or dynamic proxies, which pollute stack traces and can interfere with "Hot Loop" optimization (JIT inlining).
3.  **Debuggability:** "Magic" wiring hides the initialization order. Manual `new GameServer(new BootstrapFactory())` makes the dependency graph explicit and compiler-checked.
4.  **Artifact Size:** A framework adds megabytes of transitive dependencies. `net-bullet` is a micro-kernel; every kilobyte matters.

## The Trade-Offs (The Cost)

* **Boilerplate:** We must write `new X(new Y())` chains manually.
* **Verbosity:** `Main.java` will grow as the system complexity grows.
* **Feature Loss:** We lose "free" features like `@ConfigurationProperties`, Metrics autoconfiguration, and Declarative HTTP clients.

## The Litmus Test (When to Revisit)

We will **ONLY** consider adopting a Compile-Time DI framework (like Dagger or Micronaut) if:

1.  **Complexity:** The `Main.java` wiring block exceeds **200 lines** of pure object instantiation.
2.  **Graph Depth:** The dependency graph exceeds **5 layers** of depth, making manual wiring prone to human error.
3.  **Performance Proof:** The framework can demonstrate **Zero Reflection** usage and <10ms impact on startup time via native image or AOT compilation.

**Until then, any PR introducing a DI framework must be rejected.**
