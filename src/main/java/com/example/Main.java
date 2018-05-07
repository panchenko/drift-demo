package com.example;

import io.airlift.drift.client.DriftClientFactory;
import io.airlift.drift.transport.netty.client.DriftNettyClientConfig;
import io.airlift.drift.transport.netty.client.DriftNettyMethodInvokerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.airlift.drift.transport.netty.client.DriftNettyMethodInvokerFactory.createStaticDriftNettyMethodInvokerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        final Starter starter = new Starter();
        starter.server.start();
        final int port = starter.getPort();
        LOGGER.info("Running server on port {}", port);

        try (DriftNettyMethodInvokerFactory<?> invokerFactory = createStaticDriftNettyMethodInvokerFactory(new DriftNettyClientConfig())) {
            DriftClientFactory clientFactory = Starter.createClientFactory(port, invokerFactory);
            Service1 service1 = clientFactory.createDriftClient(Service1.class).get();
            Service2 service2 = clientFactory.createDriftClient(Service2.class).get();

            LOGGER.info("result from service1: {}", service1.method(10));
            LOGGER.info("result from service2: {}", service2.method(20));
        }
        starter.server.shutdown();
    }
}
