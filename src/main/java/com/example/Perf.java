package com.example;

import io.airlift.drift.client.DriftClientFactory;
import io.airlift.drift.transport.netty.client.DriftNettyClientConfig;
import io.airlift.drift.transport.netty.client.DriftNettyMethodInvokerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.airlift.drift.transport.netty.client.DriftNettyMethodInvokerFactory.createStaticDriftNettyMethodInvokerFactory;
import static java.lang.Runtime.getRuntime;

public class Perf {
    private static final Logger LOGGER = LoggerFactory.getLogger(Perf.class);

    public static void main(String[] args) throws InterruptedException {
        final Starter starter = new Starter();
        starter.server.start();
        final int port = starter.getPort();
        LOGGER.info("Running server on port {}", port);

        DriftNettyClientConfig clientConfig = new DriftNettyClientConfig();
        try (DriftNettyMethodInvokerFactory<?> invokerFactory = createStaticDriftNettyMethodInvokerFactory(clientConfig)) {
            DriftClientFactory clientFactory = Starter.createClientFactory(port, invokerFactory);
            Service1 service1 = clientFactory.createDriftClient(Service1.class).get();
            // warm up
            EXECUTOR_SERVICE.invokeAll(Collections.<Callable<Integer>>nCopies(1_000, () -> service1.method(1)));

            // test
            List<Callable<Integer>> callables = new ArrayList<>();
            for (int i = 0; i < 10_000; ++i) {
                final int value = i;
                callables.add(() -> service1.method(value));
            }
            final long start = System.currentTimeMillis();
            EXECUTOR_SERVICE.invokeAll(callables);
            System.out.println(System.currentTimeMillis() - start);
        }

        starter.server.shutdown();
        EXECUTOR_SERVICE.shutdown();
    }

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(getRuntime().availableProcessors());
}
