# Requirements: Network Kernel (Phase 1)

## 1. Transport Layer
- **REQ-001:** The system SHALL automatically select `EpollEventLoopGroup` when running on Linux (x86_64/aarch64).
- **REQ-002:** The system SHALL fall back to `NioEventLoopGroup` on non-Linux operating systems (Windows/Mac).
- **REQ-003:** The selection logic MUST be contained within a dedicated `BootstrapFactory` to isolate platform-specific code.

## 2. Server Lifecycle
- **REQ-004:** The `GameServer` SHALL support asynchronous startup via `CompletableFuture<Void>`.
- **REQ-005:** The `GameServer` SHALL support asynchronous shutdown via `CompletableFuture<Void>`.
- **REQ-006:** The `GameServer` MUST implement `AutoCloseable` to ensure strict resource cleanup in `try-with-resources` blocks.
- **REQ-007:** The server MUST bind to a configurable TCP port.

## 3. Reliability
- **REQ-008:** The build MUST pass strict static analysis (SpotBugs, PMD, Checkstyle) with zero waivers.
- **REQ-009:** Integration tests MUST verify actual TCP connectivity (binding and connecting).