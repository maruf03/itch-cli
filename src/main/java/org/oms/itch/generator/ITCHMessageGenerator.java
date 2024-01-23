package org.oms.itch.generator;

import java.nio.ByteBuffer;

public interface ITCHMessageGenerator {
    ByteBuffer nextMessage();

    ByteBuffer nextMessage(String type);
}
