package org.flavio.server;

import io.grpc.stub.StreamObserver;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Locale;

public class MonitoringCenterImpl extends MonitoringCenterGrpc.MonitoringCenterImplBase {

    /**
     *Retrieve and relay any available alert messages
     * Alert messages are logged when an invasion event is received
     * @param request
     * @param responseObserver
     */
    @Override
    public void alertMessages(AlertMessagesRequest request, StreamObserver<AlertMessagesResponse> responseObserver) {
        try {
            Connection connection=Main.getConnection();
            String sql="SELECT * FROM Alerts";
            PreparedStatement statement=connection.prepareStatement(sql);
            ResultSet resultSet=statement.executeQuery();

            var formatter=new SimpleDateFormat("dd/MM/yyyy@HH:mm", Locale.getDefault());
            AlertMessagesResponse.Builder builder=AlertMessagesResponse.newBuilder();
            while (resultSet.next()){
                var time=resultSet.getDate(1);
                var message=resultSet.getString(2);
                builder.addMessages(formatter.format(time)+" =>"+message);

            }
            responseObserver.onNext(builder.build());
        }catch (SQLException exception){
            exception.printStackTrace();
            responseObserver.onError(exception);
        }
    }

    /**
    Invasion detected.Delete user and log event
     The control client will then send the request to close the door
     */
    @Override
    public void deleteUser(InvasionRequest request, StreamObserver<InvasionResponse> responseObserver) {
        try {
            Connection connection=Main.getConnection();
            String sql="DELETE FROM Users WHERE userName=?";
            PreparedStatement statement=connection.prepareStatement(sql);
            statement.setString(1,request.getUserName());
            statement.execute();
            sql="INSERT INTO Alerts(dateOfIncident,message)Values(?,?)";
            statement=connection.prepareStatement(sql);
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(2,"Intrusion detected. Intruder user name: "+request.getUserName());
            statement.execute();
            statement.closeOnCompletion();
            connection.close();

        }catch (SQLException e){
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }

    /**
    Whether to close doors
     */

    @Override
    public StreamObserver<ControlDoorRequest> controlDoorsRemotely(StreamObserver<ControlDoorResponse> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(ControlDoorRequest value) {
responseObserver.onNext(ControlDoorResponse.newBuilder()
                .setIsOpen(value.getClose())
        .setSuccess(true)
        .build());
            }

            @Override
            public void onError(Throwable t) {
responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {

            }
        };
    }
}
