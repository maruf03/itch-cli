package org.oms.itch.server;

import com.paritytrading.nassau.MessageListener;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;

@Slf4j
public class ServerMessageListener implements MessageListener {
    @Override
    public void message(ByteBuffer byteBuffer) throws IOException {
        log.info("Message Received from Client");
        log.info("Message : {}", byteBuffer);
    }
}
