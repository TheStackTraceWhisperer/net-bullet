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

## 4. Phase 2: Binary Protocol & ECS Bridge
- **REQ-010:** The system SHALL handle TCP fragmentation by utilizing a length-prefixed framing protocol (e.g., 2-byte length header) before any payload decoding occurs.
- **REQ-011:** The system SHALL decode binary payloads strictly into immutable Java 25 `Record` instances (e.g., `PlayerMoveIntent`) to represent client actions.
- **REQ-012:** The system MUST NOT execute any game logic, physics calculations, or ECS processing on the Netty `EventLoopGroup` threads.
- **REQ-013:** The system SHALL enqueue decoded intent records into a thread-safe, non-blocking Multi-Producer Single-Consumer (MPSC) queue.
- **REQ-014:** The system SHALL implement a dedicated Game Loop thread that drains the intent queue at a fixed timestep (e.g., 60 TPS) and dispatches the intents to the ECS layer.
- **REQ-015:** If the intent queue reaches its maximum capacity (slow consumer/DDoS attempt), the Netty ingestion handler MUST drop the packet and log a rate-limiting warning rather than throwing an `OutOfMemoryError`.