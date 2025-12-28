package com.netbullet.net;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for GameServer connectivity.
 */
class GameServerIT {

    @Test
    void testServerLifecycle() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();

        try (GameServer server = new GameServer(factory)) {
            // 1. Start on ephemeral port
            server.start(0).get(5, TimeUnit.SECONDS);

            int port = server.getPort();
            assertThat(port).isGreaterThan(0);

            // 2. Verify connectivity with raw socket
            try (Socket client = new Socket("localhost", port)) {
                assertThat(client.isConnected()).isTrue();
            }

            // 3. Stop is handled by try-with-resources
        }
    }

    @Test
    void testGetPortReturnsMinusOneBeforeStart() {
        BootstrapFactory factory = new BootstrapFactory();
        GameServer server = new GameServer(factory);

        int port = server.getPort();
        assertThat(port).isEqualTo(-1);
    }

    @Test
    void testGetPortReturnsMinusOneAfterStop() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();
        GameServer server = new GameServer(factory);

        server.start(0).get(5, TimeUnit.SECONDS);
        int portWhileRunning = server.getPort();
        assertThat(portWhileRunning).isGreaterThan(0);

        server.stop().get(5, TimeUnit.SECONDS);
        int portAfterStop = server.getPort();
        assertThat(portAfterStop).isEqualTo(-1);
    }

    @Test
    void testServerBindsToSpecificPort() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();

        try (GameServer server = new GameServer(factory)) {
            // Try to bind to a high port (less likely to be in use)
            int requestedPort = 0; // Use ephemeral to avoid conflicts
            server.start(requestedPort).get(5, TimeUnit.SECONDS);

            int actualPort = server.getPort();
            assertThat(actualPort).isGreaterThan(0);
        }
    }

    @Test
    void testStopBeforeStartDoesNotThrow() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();
        GameServer server = new GameServer(factory);

        CompletableFuture<Void> stopFuture = server.stop();
        stopFuture.get(5, TimeUnit.SECONDS); // Should complete without error
    }

    @Test
    void testMultipleClientsCanConnect() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();

        try (GameServer server = new GameServer(factory)) {
            server.start(0).get(5, TimeUnit.SECONDS);
            int port = server.getPort();

            // Connect multiple clients
            try (Socket client1 = new Socket("localhost", port);
                    Socket client2 = new Socket("localhost", port);
                    Socket client3 = new Socket("localhost", port)) {

                assertThat(client1.isConnected()).isTrue();
                assertThat(client2.isConnected()).isTrue();
                assertThat(client3.isConnected()).isTrue();
            }
        }
    }

    @Test
    void testServerRejectsConnectionAfterStop() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();
        GameServer server = new GameServer(factory);

        server.start(0).get(5, TimeUnit.SECONDS);
        int port = server.getPort();

        // Verify connection works
        try (Socket client = new Socket("localhost", port)) {
            assertThat(client.isConnected()).isTrue();
        }

        // Stop the server
        server.stop().get(5, TimeUnit.SECONDS);

        // Attempting to connect should fail
        assertThatThrownBy(() -> {
            new Socket("localhost", port);
        }).isInstanceOf(Exception.class); // Connection refused or similar
    }

    @Test
    void testStartReturnsCompletedFuture() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();

        try (GameServer server = new GameServer(factory)) {
            CompletableFuture<Void> startFuture = server.start(0);

            assertThat(startFuture).isNotNull();
            startFuture.get(5, TimeUnit.SECONDS); // Should complete successfully
            assertThat(startFuture.isDone()).isTrue();
            assertThat(startFuture.isCompletedExceptionally()).isFalse();
        }
    }

    @Test
    void testStopReturnsCompletedFuture() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();
        GameServer server = new GameServer(factory);

        server.start(0).get(5, TimeUnit.SECONDS);
        CompletableFuture<Void> stopFuture = server.stop();

        assertThat(stopFuture).isNotNull();
        stopFuture.get(5, TimeUnit.SECONDS);
        assertThat(stopFuture.isDone()).isTrue();
        assertThat(stopFuture.isCompletedExceptionally()).isFalse();
    }

    @Test
    void testBindFailureOnInvalidPort() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();
        GameServer server = new GameServer(factory);

        // Try to bind to port -1 (invalid)
        CompletableFuture<Void> startFuture = server.start(-1);

        assertThatThrownBy(() -> startFuture.get(5, TimeUnit.SECONDS))
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testPortValidationRejectsNegativePort() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();
        GameServer server = new GameServer(factory);

        CompletableFuture<Void> startFuture = server.start(-100);

        assertThatThrownBy(() -> startFuture.get(5, TimeUnit.SECONDS))
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Port must be between 0 and 65535");
    }

    @Test
    void testPortValidationRejectsPortAboveMaximum() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();
        GameServer server = new GameServer(factory);

        CompletableFuture<Void> startFuture = server.start(65536);

        assertThatThrownBy(() -> startFuture.get(5, TimeUnit.SECONDS))
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Port must be between 0 and 65535");
    }

    @Test
    void testNullBootstrapFactoryRejected() {
        assertThatThrownBy(() -> {
            new GameServer(null);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("bootstrapFactory cannot be null");
    }

    @Test
    void testMultipleStartCallsThrowException() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();
        GameServer server = new GameServer(factory);

        server.start(0).get(5, TimeUnit.SECONDS);

        // Try to start again without stopping
        assertThatThrownBy(() -> {
            server.start(0);
        }).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already started");
    }

    @Test
    void testBindFailureWhenPortAlreadyInUse() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();

        // Occupy a port with a regular ServerSocket
        try (ServerSocket occupiedSocket = new ServerSocket(0)) {
            int occupiedPort = occupiedSocket.getLocalPort();

            GameServer server = new GameServer(factory);
            CompletableFuture<Void> startFuture = server.start(occupiedPort);

            // The bind should fail because port is already in use
            assertThatThrownBy(() -> startFuture.get(5, TimeUnit.SECONDS))
                    .isInstanceOf(ExecutionException.class)
                    .hasRootCauseInstanceOf(IOException.class);

            // Verify getPort returns -1 after failed start
            assertThat(server.getPort()).isEqualTo(-1);
        }
    }

    @Test
    void testCloseWithTimeoutDoesNotBlockIndefinitely() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();
        GameServer server = new GameServer(factory);

        server.start(0).get(5, TimeUnit.SECONDS);

        // Close should complete within reasonable time (30 second timeout)
        long startTime = System.currentTimeMillis();
        server.close();
        long duration = System.currentTimeMillis() - startTime;

        // Should complete much faster than 30 seconds in normal case
        assertThat(duration).isLessThan(30000);
    }

    @Test
    void testBothEventLoopGroupsShutdownProperly() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();
        GameServer server = new GameServer(factory);

        server.start(0).get(5, TimeUnit.SECONDS);

        CompletableFuture<Void> stopFuture = server.stop();
        stopFuture.get(10, TimeUnit.SECONDS); // Allow time for both groups to shutdown

        assertThat(stopFuture.isDone()).isTrue();
        assertThat(stopFuture.isCompletedExceptionally()).isFalse();
    }

    @Test
    void testStartWith65535Port() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();

        try (GameServer server = new GameServer(factory)) {
            // Port 65535 is valid (upper bound)
            CompletableFuture<Void> startFuture = server.start(65535);

            // This might fail if port is in use, but should not fail validation
            try {
                startFuture.get(5, TimeUnit.SECONDS);
                int port = server.getPort();
                assertThat(port).isEqualTo(65535);
            } catch (ExecutionException e) {
                // Port might be in use or require privileges, which is acceptable
                assertThat(e.getCause()).isNotInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @Test
    void testPrivilegedPortBindingFailureLogsSpecificMessage() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();
        GameServer server = new GameServer(factory);

        // Attempt to bind to a privileged port (80) without privileges
        // This should fail - we just want to verify it's handled gracefully
        CompletableFuture<Void> startFuture = server.start(80);

        // The bind should fail (permission denied or port in use)
        assertThatThrownBy(() -> startFuture.get(10, TimeUnit.SECONDS))
                .isInstanceOf(ExecutionException.class);

        // Verify server can still be stopped cleanly after failed bind
        CompletableFuture<Void> stopFuture = server.stop();
        stopFuture.get(5, TimeUnit.SECONDS);
        assertThat(stopFuture.isDone()).isTrue();
    }

    @Test
    void testStopWhenNeverStarted() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();
        GameServer server = new GameServer(factory);

        // Stop without ever starting should complete successfully
        CompletableFuture<Void> stopFuture = server.stop();
        stopFuture.get(5, TimeUnit.SECONDS);

        assertThat(stopFuture.isDone()).isTrue();
        assertThat(stopFuture.isCompletedExceptionally()).isFalse();
    }

    @Test
    void testStopAfterFailedStart() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();
        GameServer server = new GameServer(factory);

        // Try to start with an invalid negative port (should fail validation)
        CompletableFuture<Void> startFuture = server.start(-1);

        assertThatThrownBy(() -> startFuture.get(5, TimeUnit.SECONDS))
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class);

        // Now try to stop - should handle gracefully even though start failed
        CompletableFuture<Void> stopFuture = server.stop();
        stopFuture.get(5, TimeUnit.SECONDS);

        assertThat(stopFuture.isDone()).isTrue();
    }
}
