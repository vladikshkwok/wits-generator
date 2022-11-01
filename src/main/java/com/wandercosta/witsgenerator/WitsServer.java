package com.wandercosta.witsgenerator;

import com.wandercosta.witsgenerator.connection.TcpServer;
import com.wandercosta.witsgenerator.generator.WitsGenerator;

import java.io.IOException;
import java.util.Objects;

/**
 * This class is responsible for starting the TCP Server thread and writing to its clients from time
 * to time, according to the frequency configured.
 *
 * @author Wander Costa (www.wandercosta.com)
 */
public class WitsServer {

    private static final String NULL_TCPSERVER = "TcpServer must be provided.";
    private static final String NULL_WITSGENERATOR = "WitsGenerator must be provided.";
    private static final String WRONG_PORT = "Port must be between 1 and 65535, inclusive.";
    private static final String WRONG_FREQUENCY
            = "Frequency must be provided and be greater than 0.";
    private static final String WRONG_RECORDS = "Records must be between 1 and 99, inclusive.";
    private static final String WRONG_ITEMS = "Items must be between 1 and 99, inclusive.";

    private final TcpServer server;
    private final WitsGenerator generator;
    private final int records;
    private final int items;
    private final int frequency;
    
    private transient boolean keepRunning;

    public WitsServer(TcpServer server, WitsGenerator generator, int port, int frequency,
            int records, int items) {
        validate(server, generator, port, frequency, records, items);
        this.server = server;
        this.records = records;
        this.items = items;
        this.frequency = frequency;
        this.generator = generator;
    }

    @SuppressWarnings("SleepWhileInLoop")
    public void start()  {
        keepRunning = true;
        server.start();
        System.out.println("WitsServer started!");
        long spentTime;
        while (keepRunning) {
            spentTime = System.currentTimeMillis();

            try {
                server.writeln(generator.generate(records, items));
            } catch (IOException e) {
                server.stopServer();
                break;
            }

            spentTime = -spentTime + System.currentTimeMillis();
            try {
                Thread.sleep(frequency - spentTime);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }

    public boolean serverIsAlive() {
        return server.isAlive();
    }

    public void stop() {
        if (keepRunning) {
            keepRunning = false;
            server.stopServer();
            System.out.println("WitsServer stopped!");
        }
    }

    private void validate(TcpServer server, WitsGenerator generator, int port, int frequency,
            int records, int items) {
        Objects.requireNonNull(server, NULL_TCPSERVER);
        Objects.requireNonNull(generator, NULL_WITSGENERATOR);
        if (port <= 0) {
            throw new IllegalArgumentException(WRONG_PORT);
        }
        if (frequency <= 0) {
            throw new IllegalArgumentException(WRONG_FREQUENCY);
        }
        if (records <= 0 || records >= 100) {
            throw new IllegalArgumentException(WRONG_RECORDS);
        }
        if (items <= 0 || items >= 100) {
            throw new IllegalArgumentException(WRONG_ITEMS);
        }
    }

}
