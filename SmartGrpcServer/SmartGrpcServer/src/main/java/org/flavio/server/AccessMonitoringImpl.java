package org.flavio.server;

import io.grpc.stub.StreamObserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class AccessMonitoringImpl extends AccessMonitoringGrpc.AccessMonitoringImplBase {

    /**
     * Just a flag for whether the client should ring alarm
     * @param request
     * @param responseObserver
     */
    @Override
    public void ringAudibleAlarm(RingAlarmRequest request, StreamObserver<RingAlarmResponse> responseObserver) {

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
        SetPerimeterLevelResponse response= SetPerimeterLevelResponse.newBuilder()
                .setMessage("Success")
                .addAllPerimeter(request.getPerimeterList())
                .setSuccess(true)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<CheckUserAccessPermissionRequest> checkUsersAccessPermission(StreamObserver<CheckUserAccessPermissionResponse> responseObserver) {


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
                 responseObserver.onNext(CheckUserAccessPermissionResponse.newBuilder()
                         .addAllPermissions(Arrays.stream(roles.split(",")).collect(Collectors.toList())).build());
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
                /**
                 * An error occurred but the client still needs to know
                 * Then we call onComplete to prevent further streaming
                 */
                responseObserver.onError(t);
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                /**
                 * Nothing to do here
                 */
            }
        };
    }
}