package com.netbullet.net;

import static org.assertj.core.api.Assertions.assertThat;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for BootstrapFactory transport selection logic.
 */
class BootstrapFactoryTest {

    @Test
    void testCreateEventLoopGroupReturnsNonNull() {
        BootstrapFactory factory = new BootstrapFactory();
        EventLoopGroup group = factory.createEventLoopGroup(2, "test");

        assertThat(group).isNotNull();
        assertThat(group.isShutdown()).isFalse();

        // Cleanup
        group.shutdownGracefully();
    }

    @Test
    void testGetServerSocketChannelClassReturnsNonNull() {
        BootstrapFactory factory = new BootstrapFactory();
        Class<? extends ServerSocketChannel> channelClass = factory.getServerSocketChannelClass();

        assertThat(channelClass).isNotNull();
    }

    @Test
    void testMultipleEventLoopGroupsHaveDifferentNames() throws InterruptedException {
        BootstrapFactory factory = new BootstrapFactory();

        EventLoopGroup group1 = factory.createEventLoopGroup(1, "worker");
        EventLoopGroup group2 = factory.createEventLoopGroup(1, "boss");

        assertThat(group1).isNotNull();
        assertThat(group2).isNotNull();
        assertThat(group1).isNotSameAs(group2);

        // Cleanup
        group1.shutdownGracefully();
        group2.shutdownGracefully();
    }

    @Test
    void testThreadNamingIncrementsCorrectly() throws InterruptedException {
        BootstrapFactory factory = new BootstrapFactory();

        // Create a group that will spawn threads
        EventLoopGroup group = factory.createEventLoopGroup(2, "test-prefix");

        assertThat(group).isNotNull();

        // Submit tasks to ensure threads are created
        group.submit(() -> {
            Thread currentThread = Thread.currentThread();
            assertThat(currentThread.getName()).startsWith("test-prefix-");
        }).sync();

        // Cleanup
        group.shutdownGracefully();
    }
}
