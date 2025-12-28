# Implementation Tasks: Network Kernel

## Phase 1: Foundation (Current)
- [x] **Setup:** Add `netty-all` dependency to `pom.xml`.
- [x] **Logic:** Implement `BootstrapFactory` (OS detection).
- [x] **Logic:** Implement `GameServer` (Start/Stop lifecycle).
- [x] **Test:** Implement `GameServerIT` (Connectivity proof).
- [ ] **Verification:** Pass `mvn clean verify` locally.

## Phase 2: Protocol (Next)
- [ ] **Design:** Define binary packet format.
- [ ] **Logic:** Implement Packet Decoder/Encoder.
- [ ] **Logic:** Implement Session Manager.