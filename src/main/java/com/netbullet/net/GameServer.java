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
 * The core TCP Game Server.
 * Manages the Netty lifecycle and binds to the network port.
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
     * @param bootstrapFactory factory for creating transport objects
     */
    public GameServer(BootstrapFactory bootstrapFactory) {
        this.bootstrapFactory = bootstrapFactory;
    }

    /**
     * Starts the server on the specified port.
     *
     * @param port the TCP port to bind to
     * @return a future that completes when the server is bound
     */
    public CompletableFuture<Void> start(int port) {
        CompletableFuture<Void> startupFuture = new CompletableFuture<>();
        
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

        b.bind(port).addListener((ChannelFuture future) -> {
            if (future.isSuccess()) {
                this.serverChannel = future.channel();
                InetSocketAddress addr = (InetSocketAddress) serverChannel.localAddress();
                LOG.info("GameServer started on port: {}", addr.getPort());
                startupFuture.complete(null);
            } else {
                LOG.error("Failed to bind port {}", port, future.cause());
                startupFuture.completeExceptionally(future.cause());
            }
        });

        return startupFuture;
    }

    /**
     * Returns the actual port the server is listening on.
     *
     * @return port number, or -1 if not running
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
