package org.oms.itch.client;

import com.paritytrading.nassau.MessageListener;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;

@Slf4j
public class ClientMessageListener implements MessageListener {
    @Override
    public void message(ByteBuffer byteBuffer) throws IOException {
        log.info("Receiving data...");
        byte[] data = new byte[byteBuffer.remaining()];
        byteBuffer.get(data);
        log.info("Received data: {}", new String(data));
    }
}
