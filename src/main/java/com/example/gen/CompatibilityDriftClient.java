package com.example.gen;

import com.example.Starter;
import io.airlift.drift.client.DriftClientFactory;
import io.airlift.drift.transport.netty.client.DriftNettyClientConfig;
import io.airlift.drift.transport.netty.client.DriftNettyMethodInvokerFactory;
import org.apache.thrift.TException;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TServerSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.airlift.drift.transport.netty.client.DriftNettyMethodInvokerFactory.createStaticDriftNettyMethodInvokerFactory;

public class CompatibilityDriftClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompatibilityDriftClient.class);

    public static void main(String[] args) throws TException {
        Service1.Processor<Service1.Iface> processor = new Service1.Processor<>(new Service1.Iface() {
            @Override
            public int method1(int arg0) {
                LOGGER.info("Called method1({})", arg0);
                return arg0 + 1;
            }
        });
        TServerSocket transport = new TServerSocket(0);
        TThreadPoolServer server = new TThreadPoolServer(new TThreadPoolServer.Args(transport)
                .transportFactory(new TFramedTransport.Factory())
                .processor(processor));
        new Thread(server::serve).start();
        final int port = transport.getServerSocket().getLocalPort();
        LOGGER.info("Running server on port {}", port);

        try (DriftNettyMethodInvokerFactory<?> invokerFactory = createStaticDriftNettyMethodInvokerFactory(new DriftNettyClientConfig())) {
            DriftClientFactory clientFactory = Starter.createClientFactory(port, invokerFactory);
            com.example.Service1 service1 = clientFactory.createDriftClient(com.example.Service1.class).get();

            LOGGER.info("result from service1: {}", service1.method(10));
        }
        server.stop();
    }
}
