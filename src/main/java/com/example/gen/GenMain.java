package com.example.gen;

import com.example.gen.Service1.Iface;
import com.example.gen.Service1.Processor;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenMain.class);

    public static void main(String[] args) throws TException {
        Processor<Iface> processor = new Processor<>(new Iface() {
            @Override
            public int method1(int arg0) {
                LOGGER.info("Called method1({})", arg0);
                return arg0 + 1;
            }
        });
        try (TServerSocket transport = new TServerSocket(0)) {
            TThreadPoolServer server = new TThreadPoolServer(new TThreadPoolServer.Args(transport)
                    .transportFactory(new TFramedTransport.Factory())
                    .processor(processor));
            new Thread(server::serve, "Thrift Server").start();

            final int port = transport.getServerSocket().getLocalPort();
            LOGGER.info("Running server on port {}", port);

            try (TSocket clientTransport = new TSocket("localhost", port)) {
                clientTransport.open();
                final Iface client = new Service1.Client.Factory().getClient(new TBinaryProtocol(
                        new TFramedTransport(clientTransport)));

                LOGGER.info("result from service1: {}", client.method1(10));
            }

            server.stop();
        }
        LOGGER.info("Finished");
    }
}
