# Implementation Tasks: Network Kernel

## Phase 1: Foundation (Current)
- [x] **Setup:** Add `netty-all` dependency to `pom.xml`.
- [x] **Logic:** Implement `BootstrapFactory` (OS detection).
- [x] **Logic:** Implement `GameServer` (Start/Stop lifecycle).
- [x] **Test:** Implement `GameServerIT` (Connectivity proof).
- [ ] **Verification:** Pass `mvn clean verify` locally.

## Phase 2: Binary Protocol & ECS Bridge (Active)
### 2.1 Protocol Definitions
- [ ] **Design:** Map opcodes to client intent operations (e.g., `0x01` Move, `0x02` Attack).
- [ ] **Logic:** Implement Java 25 `Record` classes for each intent payload.

### 2.2 Netty Pipeline
- [ ] **TDD:** Write `FrameDecoderTest` verifying fragmented packet reassembly.
- [ ] **Logic:** Implement `FrameDecoder` (LengthFieldBasedFrameDecoder).
- [ ] **TDD:** Write `PacketDecoderTest` verifying byte-to-record translation.
- [ ] **Logic:** Implement `PacketDecoder`.

### 2.3 The Bridge
- [ ] **Logic:** Implement `IngestionHandler` to push records into a `ConcurrentLinkedQueue`.
- [ ] **Integration:** Update `GameServer`'s `ChannelInitializer` to chain `FrameDecoder -> PacketDecoder -> IngestionHandler`.

### 2.4 The Game Loop
- [ ] **Logic:** Implement `GameLoop` `Runnable` with a fixed-timestep calculation.
- [ ] **Integration:** Wire the `ConcurrentLinkedQueue` reference between `GameServer` (producer) and `GameLoop` (consumer) without using a DI framework.