package org.oms.itch.cli;

import com.paritytrading.nassau.soupbintcp.SoupBinTCP;
import com.paritytrading.nassau.soupbintcp.SoupBinTCPClient;
import org.oms.itch.client.Client;
import org.oms.itch.client.ClientMessageListener;
import org.oms.itch.client.ClientStatusListener;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.net.InetSocketAddress;

@Command(name = "client", version = "ITCH Client 1.0", mixinStandardHelpOptions = true, description = "Run Client for ITCH Protocol")
public class RunClient implements Runnable {
    @Option(names = { "-h", "--host" }, description = "Host to connect to", defaultValue = "localhost")
    private String host;

    @Option(names = { "-p", "--port" }, description = "Port to connect to", required = true)
    private int port;

    @Option(names = { "--username" }, description = "Username", required = true)
    private String username;

    @Option(names = { "--password" }, description = "Password", required = true)
    private String password;

    @Option(names = { "--sessionId" }, description = "Session ID", defaultValue = "")
    private String sessionId;

    @Option(names = { "--requestSequenceNo" }, description = "Request Sequence Number", defaultValue = "1")
    private long sequenceNumber;

    @Override
    public void run() {
        try (final Client client = Client.connect(new InetSocketAddress(host, port))) {

            SoupBinTCPClient session = new SoupBinTCPClient(client.getClientChannel(), new ClientMessageListener(),
                    new ClientStatusListener());

            SoupBinTCP.LoginRequest loginRequest = new SoupBinTCP.LoginRequest();

            loginRequest.setUsername(username);
            loginRequest.setPassword(password);
            loginRequest.setRequestedSession(sessionId);
            loginRequest.setRequestedSequenceNumber(sequenceNumber);

            session.login(loginRequest);
            while (session.receive() >= 0) {
                session.keepAlive();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
