package com.wandercosta.witsgenerator;

import com.wandercosta.witsgenerator.connection.TcpServer;
import com.wandercosta.witsgenerator.generator.WitsGenerator;
import com.wandercosta.witsgenerator.generator.WitsLineGenerator;
import java.io.IOException;
import java.util.Arrays;
import javax.net.ServerSocketFactory;

/**
 * The main class to run the application.
 *
 * @author Wander Costa (www.wandercosta.com)
 */
public class Main {

    public static void main(String... args) throws IOException {
        boolean error = false;

        if (args == null || args.length != 4) {
            error = true;
            System.err.println("Wrong input: 4 parameters are expected.");
        }

        if (!error) {
            try {
                int port = Integer.parseInt(args[0]);
                int freq = Integer.parseInt(args[1]);
                int records = Integer.parseInt(args[2]);
                int items = Integer.parseInt(args[3]);

                WitsGenerator gen = new WitsGenerator(new WitsLineGenerator());
                ServerSocketFactory serverSocketFactory = ServerSocketFactory.getDefault();
                TcpServer tcpServer = new TcpServer(serverSocketFactory, port);
                WitsServer witsServer = new WitsServer(tcpServer, gen, port, freq, records, items);

                startShutdownHook(witsServer);

                witsServer.start();
            } catch (NumberFormatException ex) {
                error = true;
                System.err.println("Wrong input: " + ex.getMessage());
            } catch (IOException ex) {
                error = true;
                System.err.println("Error in the stream: " + ex.getMessage());
            }
        }

        if (error || Arrays.asList(args).contains("--help")) {
            System.out.println(help());
            System.exit(1);
        }
    }

    public static String help() {
        return "Usage: java -jar WitsGenerator.jar [port] [frequency] [records] [items]\n"
                + "Creates a socket server and transmit Wits randomic data from time\n"
                + "to time. Only allows ONE single client connected.\n"
                + "\n"
                + "Mandatory arguments\n"
                + "  port\t\tThe port to run the socket Server\n"
                + "  frequency\tThe frequency, in seconds, of the data generation\n"
                + "  records\tThe amount of records to be transmitted\n"
                + "  items\t\tThe amount of items to be transmitted in each reacord";
    }

    private static void startShutdownHook(WitsServer witsServer) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown(witsServer)));
    }

    private static void shutdown(WitsServer witsServer) {
        System.out.println("\nShutting down...");
        witsServer.stop();
    }

}
