package org.oms.itch.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SocketChannel;

public class Client implements Closeable {
    final SocketChannel clientChannel;

    private Client(SocketChannel clientChannel) {
        this.clientChannel = clientChannel;
    }

    public static Client connect(SocketAddress address) throws IOException {
        SocketChannel channel = SocketChannel.open();

        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        channel.connect(address);
        channel.configureBlocking(false);

        return new Client(channel);
    }

    public SocketChannel getClientChannel() {
        return clientChannel;
    }

    @Override
    public void close() throws IOException {
        clientChannel.close();
    }
}
