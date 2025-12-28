package com.netbullet.net;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.Socket;
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
}
