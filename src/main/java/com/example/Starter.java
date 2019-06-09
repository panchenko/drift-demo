package com.example;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HostAndPort;
import io.airlift.drift.client.DriftClientFactory;
import io.airlift.drift.client.ExceptionClassifier;
import io.airlift.drift.client.address.SimpleAddressSelector;
import io.airlift.drift.codec.ThriftCodecManager;
import io.airlift.drift.server.DriftServer;
import io.airlift.drift.server.DriftService;
import io.airlift.drift.server.stats.NullMethodInvocationStatsFactory;
import io.airlift.drift.transport.netty.client.DriftNettyMethodInvokerFactory;
import io.airlift.drift.transport.netty.server.DriftNettyServerConfig;
import io.airlift.drift.transport.netty.server.DriftNettyServerTransport;
import io.airlift.drift.transport.netty.server.DriftNettyServerTransportFactory;

public class Starter {
    public final DriftServer server = new DriftServer(
            new DriftNettyServerTransportFactory(new DriftNettyServerConfig()),
            new ThriftCodecManager(),
            new NullMethodInvocationStatsFactory(),
            ImmutableSet.of(new DriftService(new Impl1()), new DriftService(new Impl2())),
            ImmutableSet.of());

    public int getPort() {
        return ((DriftNettyServerTransport) server.getServerTransport()).getPort();
    }

    public static DriftClientFactory createClientFactory(int port, DriftNettyMethodInvokerFactory<?> invokerFactory) {
        return new DriftClientFactory(
                new ThriftCodecManager(),
                invokerFactory,
                new SimpleAddressSelector(ImmutableList.of(HostAndPort.fromParts("localhost", port)),true),
                ExceptionClassifier.NORMAL_RESULT);
    }
}
