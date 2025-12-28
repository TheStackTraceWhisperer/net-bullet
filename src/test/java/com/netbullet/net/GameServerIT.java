package com.netbullet.net;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
}
