package org.oms.itch.server;

import com.paritytrading.nassau.MessageListener;
import com.paritytrading.nassau.soupbintcp.SoupBinTCPServer;
import com.paritytrading.nassau.soupbintcp.SoupBinTCPServerStatusListener;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Server implements Closeable {
    private final ServerSocketChannel serverChannel;

    private Server(ServerSocketChannel serverChannel) {
        this.serverChannel = serverChannel;
    }

    public static Server open(SocketAddress address) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(address);

        return new Server(serverChannel);
    }

    public SoupBinTCPServer accept(MessageListener messageListener, SoupBinTCPServerStatusListener statusListener)
            throws IOException {
        SocketChannel channel = serverChannel.accept();
        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        channel.configureBlocking(true);
        return new SoupBinTCPServer(channel, messageListener, statusListener);
    }

    @Override
    public void close() throws IOException {
        serverChannel.close();
    }
}
