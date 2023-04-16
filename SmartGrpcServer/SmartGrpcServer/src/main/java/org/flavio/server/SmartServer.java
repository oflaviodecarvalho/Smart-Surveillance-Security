package org.flavio.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServiceDescriptor;
import org.flavio.server.clients.RPCClients;
import org.flavio.server.clients.RPCClientsMain;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SmartServer {
    Server server;
 String[] args;
    public SmartServer(String[] args) {
        this.args=args;
    }

    public void start() throws IOException{
        /**
         * Create a server instance and add our services
         * I
         */
        try {
            server= ServerBuilder.forPort(8089).addService(new AccessMonitoringImpl())
                    .addService(new ListControlServiceImpl())
                    .addService(new MonitoringCenterImpl())
                    .build().start();
            Main.eventsList.add(Calendar.getInstance().getTime()+" :Server Started on port "+server.getPort());
           setupMainWindow();
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void setupMainWindow() throws InterruptedException {
        RPCClientsMain.main(args);
    }


}
