package org.flavio.server;

import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class SmartGrpcServerTest {


    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private AccessMonitoringImpl accessMonitoring;

    @BeforeEach
    public void setUp(){
        accessMonitoring=new AccessMonitoringImpl();
    }

    @Test
    public void ringAudibleBellTest() throws Exception {
        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(accessMonitoring).build().start());

        AccessMonitoringGrpc.AccessMonitoringBlockingStub newBlockingStub = AccessMonitoringGrpc.newBlockingStub(
                // Create a client channel and register for automatic graceful shutdown.
                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));


        RingAlarmResponse reply =
                newBlockingStub.ringAudibleAlarm(RingAlarmRequest.newBuilder()
                        .build());

        assert reply.getRing();

    }

}
