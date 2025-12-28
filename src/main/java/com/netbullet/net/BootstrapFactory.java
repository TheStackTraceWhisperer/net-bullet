package com.netbullet.net;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Factory to create Netty Transport resources based on the operating system.
 * Selects Epoll on Linux and NIO on other platforms.
 */
public class BootstrapFactory {

    /**
     * Creates an EventLoopGroup optimized for the current OS.
     *
     * @param threads
     *            number of threads in the group
     * @param namePrefix
     *            prefix for thread names
     * @return the platform-specific EventLoopGroup
     */
    public EventLoopGroup createEventLoopGroup(int threads, String namePrefix) {
        ThreadFactory factory = new NamedThreadFactory(namePrefix);
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup(threads, factory);
        }
        return new NioEventLoopGroup(threads, factory);
    }

    /**
     * Returns the ServerSocketChannel class optimized for the current OS.
     *
     * @return the platform-specific ServerSocketChannel class
     */
    public Class<? extends ServerSocketChannel> getServerSocketChannelClass() {
        if (Epoll.isAvailable()) {
            return EpollServerSocketChannel.class;
        }
        return NioServerSocketChannel.class;
    }

    /**
     * Simple thread factory to name Netty threads for easier debugging.
     */
    private static class NamedThreadFactory implements ThreadFactory {
        private final String prefix;
        private final AtomicInteger counter = new AtomicInteger(0);

        NamedThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, prefix + "-" + counter.incrementAndGet());
        }
    }
}
