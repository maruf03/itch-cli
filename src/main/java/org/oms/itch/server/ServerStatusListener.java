package org.oms.itch.server;

import com.paritytrading.nassau.soupbintcp.SoupBinTCP;
import com.paritytrading.nassau.soupbintcp.SoupBinTCPServer;
import com.paritytrading.nassau.soupbintcp.SoupBinTCPServerStatusListener;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class ServerStatusListener implements SoupBinTCPServerStatusListener {
    @Override
    public void heartbeatTimeout(SoupBinTCPServer soupBinTCPServer) throws IOException {
        throw new IOException("Heartbeat timeout");
    }

    @Override
    public void loginRequest(SoupBinTCPServer soupBinTCPServer, SoupBinTCP.LoginRequest loginRequest)
            throws IOException {
        log.info("Login Requested");
        log.info("Payload with session : {}", loginRequest.getRequestedSession());
        log.info("Payload with seq no : {}", loginRequest.getRequestedSequenceNumber());
        log.info("Payload with username : {}", loginRequest.getUsername());

        SoupBinTCP.LoginAccepted loginAccepted = new SoupBinTCP.LoginAccepted();

        loginAccepted.setSession(loginRequest.getRequestedSession().trim().isEmpty()
                ? UUID.randomUUID().toString().replace("-", "").substring(0, 10)
                : loginRequest.getRequestedSession());
        loginAccepted.setSequenceNumber(loginRequest.getRequestedSequenceNumber());

        soupBinTCPServer.accept(loginAccepted);
    }

    @Override
    public void logoutRequest(SoupBinTCPServer soupBinTCPServer) throws IOException {
        log.info("Logout Requested");
        soupBinTCPServer.close();
    }
}
