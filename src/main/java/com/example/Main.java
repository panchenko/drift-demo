package com.example;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HostAndPort;
import io.airlift.drift.client.DriftClientFactory;
import io.airlift.drift.client.ExceptionClassifier;
import io.airlift.drift.client.address.AddressSelector;
import io.airlift.drift.client.address.SimpleAddressSelector;
import io.airlift.drift.codec.ThriftCodecManager;
import io.airlift.drift.server.DriftServer;
import io.airlift.drift.server.DriftService;
import io.airlift.drift.server.stats.NullMethodInvocationStatsFactory;
import io.airlift.drift.transport.netty.client.DriftNettyClientConfig;
import io.airlift.drift.transport.netty.server.DriftNettyServerConfig;
import io.airlift.drift.transport.netty.server.DriftNettyServerTransport;
import io.airlift.drift.transport.netty.server.DriftNettyServerTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.airlift.drift.transport.netty.client.DriftNettyMethodInvokerFactory.createStaticDriftNettyMethodInvokerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        DriftServer server = new DriftServer(
                new DriftNettyServerTransportFactory(new DriftNettyServerConfig()),
                new ThriftCodecManager(),
                new NullMethodInvocationStatsFactory(),
                ImmutableSet.of(new DriftService(new Impl1()), new DriftService(new Impl2())),
                ImmutableSet.of()
        );
        server.start();
        final int port = ((DriftNettyServerTransport) server.getServerTransport()).getPort();
        LOGGER.info("Running server on port {}", port);

        AddressSelector<?> addressSelector = new SimpleAddressSelector(ImmutableList.of(HostAndPort.fromParts("localhost", port)));
        DriftClientFactory clientFactory = new DriftClientFactory(
                new ThriftCodecManager(),
                createStaticDriftNettyMethodInvokerFactory(new DriftNettyClientConfig()),
                addressSelector,
                ExceptionClassifier.NORMAL_RESULT);

        Service1 service1 = clientFactory.createDriftClient(Service1.class).get();
        Service2 service2 = clientFactory.createDriftClient(Service2.class).get();

        LOGGER.info("result from service1: {}", service1.method(10));
        LOGGER.info("result from service2: {}", service2.method(20));

        server.shutdown();
    }
}
