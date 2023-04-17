package org.flavio.server;

import io.grpc.stub.StreamObserver;
import org.flavio.server.clients.RPCClientsMain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.stream.Collectors;

public class AccessMonitoringImpl extends AccessMonitoringGrpc.AccessMonitoringImplBase {

    /**
     * Just a flag for whether the client should ring alarm
     * @param request
     * @param responseObserver
     */
    @Override
    public void ringAudibleAlarm(RingAlarmRequest request, StreamObserver<RingAlarmResponse> responseObserver) {
        Main.eventsList.add(Calendar.getInstance().getTime()+": Ring bell requested");

        doRingAlarm();
        responseObserver.onNext(RingAlarmResponse.newBuilder()
                .setRing(true).build());
        responseObserver.onCompleted();
    }

    /**
     * The perimeter is viewed as a list of Latitude,Longitude bounds
     * @param request
     * @param responseObserver
     */
    @Override
    public void setPerimeterLevel(SetPerimeterLevelRequest request, StreamObserver<SetPerimeterLevelResponse> responseObserver) {
        Main.usersAndLocations.put(request.getUserName(),request.getPerimeterList().get(0));
        Main.eventsList.add(Calendar.getInstance().getTime()+": Set Perimeter level requested");

        SetPerimeterLevelResponse response= SetPerimeterLevelResponse.newBuilder()
                .setMessage("Success")
                .addAllPerimeter(request.getPerimeterList())
                .setSuccess(true)
                .build();
        RPCClientsMain.rpcClients.checkUserPermission(request.getUserName());
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public StreamObserver<CheckUserAccessPermissionRequest> checkUsersAccessPermission(StreamObserver<CheckUserAccessPermissionResponse> responseObserver) {
        Main.eventsList.add(Calendar.getInstance().getTime()+": User permission checking");


        /**
         * As requests keep on coming, we get the user roles and return them to client
         */
        return new StreamObserver<>() {
            @Override
            public void onNext(CheckUserAccessPermissionRequest value) {
             try {
                 Connection connection=Main.getConnection();
                 String sql="SELECT roles FROM Users WHERE userName=?";
                 PreparedStatement statement=connection.prepareStatement(sql);
                 statement.setString(1,value.getUserName());
                 ResultSet resultSet=statement.executeQuery();
                 var roles=resultSet.getString(1);
                 statement.closeOnCompletion();
                 connection.close();
                 var userLocation=Main.usersAndLocations.get(value.getUserName());
                 var allowedLocation=Main.rolesAndBounds.get(roles);
                 var distance=getDistance(allowedLocation,userLocation);
                 System.out.println("User is "+distance+" from bounds");
                 responseObserver.onNext(CheckUserAccessPermissionResponse.newBuilder()
                         .addPermissions(roles).build());
                 responseObserver.onCompleted();
             }catch (SQLException exception){
                 exception.printStackTrace();
                 /**
                  * Error handling Calls should always invoke OnError if failed
                  */
                 onError(exception);
             }
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                /**
                 * An error occurred but the client still needs to know
                 * Then we call onComplete to prevent further streaming
                 */
                responseObserver.onError(t);

            }

            @Override
            public void onCompleted() {
                /**
                 * Nothing to do here
                 */
            }
        };
    }
    private double getDistance(LatLng from, LatLng to){
        final int R=6371;
        double latDistance=Math.toRadians(to.getLatitude()- from.getLatitude());
        double lonDistance=Math.toRadians(to.getLongitude()- from.getLongitude());
        double a=(Math.sin(latDistance/2)*Math.sin(latDistance/2))+Math.cos(Math.toRadians(from.getLatitude()))
                * Math.cos(Math.toRadians(to.getLatitude()))
                *(Math.sin(lonDistance/2)*Math.sin(lonDistance/2));
        double c=2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
        return R*c;
    }

    private void doRingAlarm(){

            java.awt.Toolkit.getDefaultToolkit().beep();

    }
}