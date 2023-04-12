package org.flavio.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServiceDescriptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SmartServer {
    Server server;
    public void start() throws IOException{
        /**
         * Create a server instance and add our services
         * I
         */
        server= ServerBuilder.forPort(8089).addService(new AccessMonitoringImpl())
                .addService(new ListControlServiceImpl())
                .addService(new MonitoringCenterImpl())
                .build();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Shutting down gRPC server");
            try {
                server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }));
    }


}
