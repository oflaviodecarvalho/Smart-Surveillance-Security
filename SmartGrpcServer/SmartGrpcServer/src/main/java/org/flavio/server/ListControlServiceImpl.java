package org.flavio.server;

import io.grpc.stub.StreamObserver;
import org.flavio.server.clients.RPCClientsMain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

public class ListControlServiceImpl extends ListControlGrpc.ListControlImplBase {

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<ConfirmationCreateUserResponse> responseObserver) {

        Main.eventsList.add(Calendar.getInstance().getTime()+": Create User Requested");
        /**
         * Creates a new user with the provided parameters
         */
        try {
          Connection connection=Main.getConnection();
          String sql="INSERT INTO Users(userName,password,roles)Values(?,?,?)";
          String roles=request.getRolesList().get(0);
          PreparedStatement statement=connection.prepareStatement(sql);
          statement.setString(1,request.getUserName());
          statement.setString(2,request.getPassword());
          statement.setString(3,roles);
          statement.execute();
          statement.closeOnCompletion();

          connection.close();
          responseObserver.onNext(ConfirmationCreateUserResponse.newBuilder()
                  .setMessage("User Created")
                  .setSuccess(true)
                  .build());
          responseObserver.onCompleted();
      }catch (SQLException e){
          e.printStackTrace();
      }

    }

    @Override
    public void modifyAccessLevel(ModifyUserAccessLevelRequest request, StreamObserver<ConfirmationModifyAccessLevelResponse> responseObserver) {
         Main.eventsList.add(Calendar.getInstance().getTime()+":Modify Access Level Requested");

        /**
         * Replaces the existing access level for this user with the new access levels
         */
        try {
            Connection connection=Main.getConnection();
            String sql="UPDATE Users SET roles=? WHERE userName=?";
            PreparedStatement statement=connection.prepareStatement(sql);
            statement.setString(1, request.getNewRolesList().get(0));
            statement.setString(2,request.getUserName());
            statement.execute();
            statement.closeOnCompletion();
            connection.close();
            responseObserver.onNext(ConfirmationModifyAccessLevelResponse.newBuilder()
                    .setMessage("Access Level Changed")
                            .setSuccess(true)
                    .build());
            responseObserver.onCompleted();

        }catch (SQLException e){
            responseObserver.onError(e);
            e.printStackTrace();
        }
    }


    @Override
    public void loadUserList(LoadUsersRequest request, StreamObserver<UserListResponse> responseObserver) {
        RPCClientsMain.executor.execute(() -> Main.eventsList.add(Calendar.getInstance().getTime()+":Load Users Requested"));

        try {
            Connection connection=Main.getConnection();
            var statement=connection.prepareStatement("SELECT * FROM Users");
            var resultset=statement.executeQuery();
            UserListResponse.Builder builder=UserListResponse.newBuilder();
            while (resultset.next()){
                var name=resultset.getString(1);
                var roles=resultset.getString(3);
                builder.addUsers(name+"=>"+roles);

            }
            statement.closeOnCompletion();
            connection.close();
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        }catch (SQLException e){
            responseObserver.onError(e);
            e.printStackTrace();
        }

        }
}
