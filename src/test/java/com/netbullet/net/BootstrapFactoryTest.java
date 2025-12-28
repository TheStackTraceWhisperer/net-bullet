package com.netbullet.net;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for BootstrapFactory transport selection logic.
 */
class BootstrapFactoryTest {

    @Test
    void testCreateEventLoopGroupReturnsNonNull() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();
        EventLoopGroup group = factory.createEventLoopGroup(2, "test");

        assertThat(group).isNotNull();
        assertThat(group.isShutdown()).isFalse();

        // Cleanup
        group.shutdownGracefully().sync();
    }

    @Test
    void testGetServerSocketChannelClassReturnsNonNull() {
        BootstrapFactory factory = new BootstrapFactory();
        Class<? extends ServerSocketChannel> channelClass = factory.getServerSocketChannelClass();

        assertThat(channelClass).isNotNull();
    }

    @Test
    void testMultipleEventLoopGroupsHaveDifferentNames() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();

        EventLoopGroup group1 = factory.createEventLoopGroup(1, "worker");
        EventLoopGroup group2 = factory.createEventLoopGroup(1, "boss");

        assertThat(group1).isNotNull();
        assertThat(group2).isNotNull();
        assertThat(group1).isNotSameAs(group2);

        // Cleanup
        group1.shutdownGracefully().sync();
        group2.shutdownGracefully().sync();
    }

    @Test
    void testThreadNamingIncrementsCorrectly() throws Exception {
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
        group.shutdownGracefully().sync();
    }

    @Test
    void testCreateEventLoopGroupRejectsZeroThreads() {
        BootstrapFactory factory = new BootstrapFactory();

        assertThatThrownBy(() -> {
            factory.createEventLoopGroup(0, "test");
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("threads must be positive");
    }

    @Test
    void testCreateEventLoopGroupRejectsNegativeThreads() {
        BootstrapFactory factory = new BootstrapFactory();

        assertThatThrownBy(() -> {
            factory.createEventLoopGroup(-1, "test");
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("threads must be positive");
    }

    @Test
    void testCreateEventLoopGroupRejectsNullNamePrefix() {
        BootstrapFactory factory = new BootstrapFactory();

        assertThatThrownBy(() -> {
            factory.createEventLoopGroup(1, null);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("namePrefix cannot be null");
    }

    @Test
    void testCreateEventLoopGroupAcceptsMinimumValidThreads() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();
        EventLoopGroup group = factory.createEventLoopGroup(1, "test");

        assertThat(group).isNotNull();
        assertThat(group.isShutdown()).isFalse();

        // Cleanup
        group.shutdownGracefully().sync();
    }

    @Test
    void testCreateEventLoopGroupAcceptsLargeThreadCount() throws Exception {
        BootstrapFactory factory = new BootstrapFactory();
        EventLoopGroup group = factory.createEventLoopGroup(100, "test");

        assertThat(group).isNotNull();
        assertThat(group.isShutdown()).isFalse();

        // Cleanup
        group.shutdownGracefully().sync();
    }

    @Test
    void testServerSocketChannelClassIsValidType() {
        BootstrapFactory factory = new BootstrapFactory();
        Class<? extends ServerSocketChannel> channelClass = factory.getServerSocketChannelClass();

        assertThat(channelClass).isNotNull();
        assertThat(ServerSocketChannel.class).isAssignableFrom(channelClass);
    }
}
