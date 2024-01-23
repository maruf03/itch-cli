package org.oms.itch.cli;

import com.paritytrading.nassau.soupbintcp.SoupBinTCPServer;
import org.oms.itch.generator.ITCHGeneratorConverter;
import org.oms.itch.generator.ITCHMessageGenerator;
import org.oms.itch.server.Server;
import org.oms.itch.server.ServerMessageListener;
import org.oms.itch.server.ServerStatusListener;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

@Command(name = "server", version = "ITCH Server 1.0", mixinStandardHelpOptions = true, description = "Run Server for ITCH Protocol")
public class RunServer implements Runnable {
    @Option(names = { "-p", "--port" }, description = "Port to listen on", required = true)
    private int port;

    @Option(names = { "-n", "--noOfPackets" }, description = "Number of packets to send", defaultValue = "100")
    private int noOfPackets;

    @Option(names = { "-l",
            "--latency" }, description = "Latency in milliseconds (0 for maximum throughput)", defaultValue = "100")
    private long latency;

    @Option(names = { "-j", "--threads" }, description = "Number of threads in the thread pool", defaultValue = "10")
    private int numberOfThreads;

    @Option(names = { "-g",
            "--generator" }, description = "ITCH Message generator [totalview, news, index]", required = true, converter = ITCHGeneratorConverter.class)
    ITCHMessageGenerator generator;

    @Override
    public void run() {
        System.out.println("Running Server on port " + port);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        try (final Server server = Server.open(new InetSocketAddress(port))) {
            try {
                while (true) {
                    SoupBinTCPServer session = server.accept(new ServerMessageListener(), new ServerStatusListener());
                    executorService.submit(() -> {
                        try {
                            while (session.receive() <= 0)
                                ;
                            for (int i = 0; i < noOfPackets; i++) {
                                try {
                                    session.send(generator.nextMessage());
                                    Thread.sleep(latency);
                                } catch (IOException | InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            session.endSession();
                            session.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
