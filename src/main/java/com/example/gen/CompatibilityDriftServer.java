package com.example.gen;

import com.example.Starter;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompatibilityDriftServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompatibilityDriftServer.class);

    public static void main(String[] args) throws TException {
        final Starter starter = new Starter();
        starter.server.start();
        final int port = starter.getPort();
        LOGGER.info("Running server on port {}", port);

        TSocket clientTransport = new TSocket("localhost", port);
        clientTransport.open();
        final Service1.Iface service1 = new Service1.Client.Factory().getClient(new TBinaryProtocol(
                new TFramedTransport(clientTransport)));

        LOGGER.info("result from service1: {}", service1.method1(10));
        clientTransport.close();

        starter.server.shutdown();
    }
}
