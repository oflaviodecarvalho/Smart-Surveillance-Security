package org.flavio.server.clients;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.flavio.server.*;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class RPCClients {



    /**
     * There are three stubs available
     * Blocking Stub ----Prevents all other calls while in use
     * Future Stub ---- Executed asynchronously
     * Normal Stub ----Synchronized calls
     *
     *
     * Here we just declare them
     */
    AccessMonitoringGrpc.AccessMonitoringBlockingStub accessMonitoringStub;
    ListControlGrpc.ListControlBlockingStub listControlStub;
    MonitoringCenterGrpc.MonitoringCenterBlockingStub monitoringCenterStub;

    /***
     * Connect to the server and then initialize the stubs
     */
   public void initClients() {
       ManagedChannel managedChannel= ManagedChannelBuilder.forAddress("localhost",8089)
               .usePlaintext()
               .build();
       try {
           accessMonitoringStub=AccessMonitoringGrpc.newBlockingStub(managedChannel);
           listControlStub=ListControlGrpc.newBlockingStub(managedChannel);
           monitoringCenterStub=MonitoringCenterGrpc.newBlockingStub(managedChannel);
       }catch (Exception e){
           e.printStackTrace();
           //managedChannel.awaitTermination(5,TimeUnit.SECONDS);
       }


    }


    /**
     * Making RPC calls. and listening for response
     */
    public  void createUser(String userName,String password){
        try {
            CreateUserRequest request= CreateUserRequest.newBuilder()
                    .setPassword(password)
                    .setUserName(userName)
                    .addRoles("User")
                    .addRoles("Admin")
                    .addRoles("Super Admin")
                    .build();
            System.out.println(userName+" "+password);

            ConfirmationCreateUserResponse response=listControlStub.createUser(request);
            System.out.println(response.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
