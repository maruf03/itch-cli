package org.oms.itch.client;

import com.paritytrading.nassau.soupbintcp.SoupBinTCP;
import com.paritytrading.nassau.soupbintcp.SoupBinTCPClient;
import com.paritytrading.nassau.soupbintcp.SoupBinTCPClientStatusListener;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ClientStatusListener implements SoupBinTCPClientStatusListener {
    @Override
    public void heartbeatTimeout(SoupBinTCPClient soupBinTCPClient) throws IOException {
        throw new IOException("Heartbeat timeout");
    }

    @Override
    public void loginAccepted(SoupBinTCPClient soupBinTCPClient, SoupBinTCP.LoginAccepted loginAccepted)
            throws IOException {
        log.info("Login accepted");
        log.info("Session Id: {}", loginAccepted.getSession());
        log.info("Payload with seq no : {}", loginAccepted.getSequenceNumber());
    }

    @Override
    public void loginRejected(SoupBinTCPClient soupBinTCPClient, SoupBinTCP.LoginRejected loginRejected)
            throws IOException {
        log.info("Login Rejected");
        log.info("Rejected Reason Code: {}", (char) loginRejected.getRejectReasonCode());
        throw new IOException("Login rejected");
    }

    @Override
    public void endOfSession(SoupBinTCPClient soupBinTCPClient) throws IOException {
        log.info("End of session");
        throw new IOException("End of session");
    }
}
