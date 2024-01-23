package org.oms.itch;

import org.apache.log4j.BasicConfigurator;
import org.oms.itch.cli.Application;
import org.oms.itch.cli.RunClient;
import org.oms.itch.cli.RunServer;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        BasicConfigurator.configure();
        CommandLine commandLine = new CommandLine(new Application());
        commandLine.addSubcommand(new RunServer());
        commandLine.addSubcommand(new RunClient());
        int exitCode = commandLine.execute(args);
        System.exit(exitCode);
    }
}
