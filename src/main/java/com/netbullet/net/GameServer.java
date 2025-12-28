package com.netbullet.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The core TCP Game Server. Manages the Netty lifecycle and binds to the
 * network port.
 */
public final class GameServer implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(GameServer.class);
    private final BootstrapFactory bootstrapFactory;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    /**
     * Constructs a new GameServer.
     *
     * @param bootstrapFactory
     *            factory for creating transport objects
     */
    public GameServer(BootstrapFactory bootstrapFactory) {
        this.bootstrapFactory = bootstrapFactory;
    }

    /**
     * Starts the server on the specified port.
     * <p>
     * The returned future completes successfully once the underlying Netty
     * server channel has been bound. If binding fails for any reason
     * (for example, the port is already in use or the process lacks
     * sufficient privileges), the future is completed exceptionally with the
     * cause reported by Netty and the server remains stopped.
     * <p>
     * This method is not idempotent and is intended to be called at most once
     * per {@code GameServer} instance. Concurrent or repeated invocations are
     * not supported and may result in additional bind attempts or resource
     * leaks; callers should create a new {@code GameServer} instance if a
     * restarted server is required.
     *
     * @param port
     *            the TCP port to bind to
     * @return a future that completes when the server is bound, or completes
     *         exceptionally if the bind operation fails
     */
    public CompletableFuture<Void> start(int port) {
        this.bossGroup = bootstrapFactory.createEventLoopGroup(1, "boss");
        this.workerGroup = bootstrapFactory.createEventLoopGroup(Runtime.getRuntime().availableProcessors(), "worker");

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(bootstrapFactory.getServerSocketChannelClass())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        // Pipeline is empty for Phase 1
                    }
                });

        CompletableFuture<Void> startupFuture = new CompletableFuture<>();
        try {
            b.bind(port).addListener((ChannelFuture future) -> {
                if (future.isSuccess()) {
                    this.serverChannel = future.channel();
                    InetSocketAddress addr = (InetSocketAddress) serverChannel.localAddress();
                    LOG.info("GameServer started on port: {}", addr.getPort());
                    startupFuture.complete(null);
                } else {
                    LOG.error("Failed to bind port {}", port, future.cause());
                    shutdownEventLoopGroupsOnStartFailure();
                    startupFuture.completeExceptionally(future.cause());
                }
            });
        } catch (RuntimeException e) {
            LOG.error("Exception while binding to port {}", port, e);
            shutdownEventLoopGroupsOnStartFailure();
            startupFuture.completeExceptionally(e);
        }

        return startupFuture;
    }

    /**
     * Shuts down event loop groups when server startup fails.
     * This is only used on start() failure paths to avoid leaking Netty threads.
     */
    private void shutdownEventLoopGroupsOnStartFailure() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * Returns the actual TCP port the server is currently listening on.
     *
     * @return the TCP port number if the server is running and bound, or {@code -1}
     *         if the server has not been started yet or has already been stopped
     */
    public int getPort() {
        if (serverChannel != null && serverChannel.isOpen()) {
            return ((InetSocketAddress) serverChannel.localAddress()).getPort();
        }
        return -1;
    }

    /**
     * Stops the server and releases all resources.
     *
     * @return a future that completes when shutdown is finished
     */
    public CompletableFuture<Void> stop() {
        LOG.info("Stopping GameServer...");
        CompletableFuture<Void> shutdownFuture = new CompletableFuture<>();

        if (bossGroup != null && workerGroup != null) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully().addListener(future -> {
                LOG.info("GameServer stopped.");
                shutdownFuture.complete(null);
            });
        } else {
            shutdownFuture.complete(null);
        }
        return shutdownFuture;
    }

    @Override
    public void close() {
        stop().join();
    }
}
