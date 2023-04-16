package org.flavio.server.clients;

import io.grpc.*;
import io.grpc.stub.StreamObserver;
import javafx.application.Application;
import org.flavio.server.*;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

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
    AccessMonitoringGrpc.AccessMonitoringStub accessMonitoringStub;
    ListControlGrpc.ListControlStub listControlStub;
    MonitoringCenterGrpc.MonitoringCenterStub monitoringCenterStub;
    ManagedChannel managedChannel;

    /***
     * Connect to the server and then initialize the stubs
     */
   public void initClients() {
       /**
        * We will use this channel for the clients
        */
       managedChannel= ManagedChannelBuilder.forAddress("localhost",8089)
               .usePlaintext()
               .enableRetry()
               .build();

       /**
        * Listen for client state change
        */
       managedChannel.notifyWhenStateChanged(ConnectivityState.SHUTDOWN, () -> Main.eventsList.add(Calendar.getInstance().getTime()+" Clients state changed "+managedChannel.getState(false)));
       managedChannel.notifyWhenStateChanged(ConnectivityState.IDLE, () -> Main.eventsList.add(Calendar.getInstance().getTime() + " Clients state changed " + managedChannel.getState(false)));
       managedChannel.notifyWhenStateChanged(ConnectivityState.CONNECTING, () -> Main.eventsList.add(Calendar.getInstance().getTime()+" Clients state changed "+managedChannel.getState(false)));
       managedChannel.notifyWhenStateChanged(ConnectivityState.TRANSIENT_FAILURE, () -> Main.eventsList.add(Calendar.getInstance().getTime()+" Clients state changed "+managedChannel.getState(false)));
       managedChannel.notifyWhenStateChanged(ConnectivityState.READY, () -> Main.eventsList.add(Calendar.getInstance().getTime()+" Clients state changed "+managedChannel.getState(false)));

       try {
           /**
           These are the stubs we are using. We can initialize the others if needed
            */
           accessMonitoringStub=AccessMonitoringGrpc.newStub(managedChannel);
           listControlStub=ListControlGrpc.newStub(managedChannel);
           monitoringCenterStub=MonitoringCenterGrpc.newStub(managedChannel);
       }catch (Exception e){
           e.printStackTrace();
           //managedChannel.awaitTermination(5,TimeUnit.SECONDS);
       }


    }


    /**
     * Making RPC calls. and listening for response
     */
    public  void createUser(String userName, String password, List<String> rolesList){
        System.out.println(userName+" "+password+" "+rolesList);
        try {
            CreateUserRequest request= CreateUserRequest.newBuilder()
                    .setPassword(password)
                    .setUserName(userName)
                    .addAllRoles(rolesList)
                    .build();
            listControlStub.createUser(request, new StreamObserver<>() {
                @Override
                public void onNext(ConfirmationCreateUserResponse value) {
                    Main.listControlMessages.add(value.toString());
                    Main.eventsList.add(Calendar.getInstance().getTime()+":"+value.getMessage());
                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onCompleted() {
                    Main.eventsList.add(Calendar.getInstance().getTime()+": Add user complete");

                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void loadUsers() {
        ListControlGrpc.ListControlStub stub=ListControlGrpc.newStub(managedChannel);
        stub.loadUserList(LoadUsersRequest.newBuilder().build(), new StreamObserver<>() {
            @Override
            public void onNext(UserListResponse value) {
                Main.userList.clear();
                Main.userList.addAll(value.getUsersList());
            }

            @Override
            public void onError(Throwable t) {
                JOptionPane.showMessageDialog(null,t.getMessage());
            }

            @Override
            public void onCompleted() {
        Main.listControlMessages.add(Calendar.getInstance().getTime()+": Userlist rpc completed");
            }
        });
    }

    public void modifyAccessLevel(String userName,List<String> rolesList) {
       listControlStub.modifyAccessLevel(ModifyUserAccessLevelRequest.newBuilder()
               .setUserName(userName)
               .addAllNewRoles(rolesList)
               .build(), new StreamObserver<>() {
           @Override
           public void onNext(ConfirmationModifyAccessLevelResponse value) {
               Main.listControlMessages.add(Calendar.getInstance().getTime()+":"+value.getMessage());

           }

           @Override
           public void onError(Throwable t) {
               Main.listControlMessages.add(Calendar.getInstance().getTime()+":"+t.getMessage());

           }

           @Override
           public void onCompleted() {
               Main.listControlMessages.add(Calendar.getInstance().getTime()+": Modify access level completed");

           }
       });


    }

    public void deleteUser(String userName){
       monitoringCenterStub.deleteUser(InvasionRequest.newBuilder()
               .setUserName(userName)
               .setMessage("User tresspassing")
               .build(), new StreamObserver<>() {
           @Override
           public void onNext(InvasionResponse value) {
               Main.alertMessages.add(value.toString());
           }

           @Override
           public void onError(Throwable t) {
               Main.alertMessages.add(t.getMessage());
           }

           @Override
           public void onCompleted() {
               Main.alertMessages.add(Calendar.getInstance().getTime()+": User Deletion complete");

           }
       });

    }

    public void loadAlerts(){
        Main.alertMessages.clear();
        monitoringCenterStub.alertMessages(AlertMessagesRequest.newBuilder().build(),
                new StreamObserver<>() {
                    @Override
                    public void onNext(AlertMessagesResponse value) {
Main.alertMessages.addAll(value.getMessagesList());
                    }

                    @Override
                    public void onError(Throwable t) {
Main.alertMessages.add(t.getMessage());
                    }

                    @Override
                    public void onCompleted() {

                    }
                });
    }

    public void ringBell() {
        accessMonitoringStub.ringAudibleAlarm(RingAlarmRequest.getDefaultInstance(), new StreamObserver<>() {
            @Override
            public void onNext(RingAlarmResponse value) {

            }

            @Override
            public void onError(Throwable t) {
   JOptionPane.showMessageDialog(null,t.getMessage());
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    public void setPerimeter(String userName, LatLng.Builder setLatitude) {
        accessMonitoringStub.setPerimeterLevel(SetPerimeterLevelRequest.newBuilder()
                .addPerimeter(setLatitude.build())
                .setUserName(userName)
                .build(), new StreamObserver<>() {
            @Override
            public void onNext(SetPerimeterLevelResponse value) {
   Main.accessMessages.add("Set perimeter "+value.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                JOptionPane.showMessageDialog(null,t.getMessage(),null,JOptionPane.ERROR_MESSAGE);

            }

            @Override
            public void onCompleted() {

            }
        });
    }

    public void checkUserPermission(String userName) {
       StreamObserver<CheckUserAccessPermissionRequest> request=accessMonitoringStub.checkUsersAccessPermission(new StreamObserver<>() {
            @Override
            public void onNext(CheckUserAccessPermissionResponse value) {
Main.accessMessages.add(value.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                JOptionPane.showMessageDialog(null,t.getMessage());

            }

            @Override
            public void onCompleted() {
                Main.accessMessages.add("Checked permission");
            }
        });
    request.onNext(CheckUserAccessPermissionRequest.newBuilder()
            .setUserName(userName)
            .build());
    request.onCompleted();
    }
}
