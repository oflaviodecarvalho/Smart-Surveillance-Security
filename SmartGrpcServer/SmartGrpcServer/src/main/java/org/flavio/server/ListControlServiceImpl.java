package org.flavio.server;

import io.grpc.stub.StreamObserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ListControlServiceImpl extends ListControlGrpc.ListControlImplBase {
    @Override
    public void createUser(CreateUserRequest request, StreamObserver<ConfirmationCreateUserResponse> responseObserver) {
        System.out.println("Add User Reuested"+request.getUserName());
        /**
         * Creates a new user with the provided parameters
         */
        try {
          Connection connection=Main.getConnection();
          String sql="INSERT INTO Users(userName,password,roles)Values(?,?,?)";
          String roles=String.join(",",request.getRolesList());
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

        /**
         * Replaces the existing access level for this user with the new access levels
         */
        try {
            Connection connection=Main.getConnection();
            String sql="UPDATE Users SET roles=? WHERE userName=?";
            PreparedStatement statement=connection.prepareStatement(sql);
            statement.setString(1,String.join(",", request.getNewRolesList()));
            statement.setString(2,request.getUserName());
            statement.execute();
            statement.closeOnCompletion();
            connection.close();
            responseObserver.onNext(ConfirmationModifyAccessLevelResponse.newBuilder()
                    .setMessage("Access Level Changed")
                            .setSuccess(true)
                    .build());

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
