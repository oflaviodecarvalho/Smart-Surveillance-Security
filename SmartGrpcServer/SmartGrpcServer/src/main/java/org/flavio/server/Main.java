package org.flavio.server;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.flavio.server.clients.RPCClients;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class Main {
    public static ObservableList<String> eventsList= FXCollections.observableArrayList();
    public static ObservableList<String> userList=FXCollections.observableArrayList();

    public static ObservableList<String> listControlMessages=FXCollections.observableArrayList();
    public static ObservableList<String> alertMessages=FXCollections.observableArrayList();
    public static ObservableList<String> accessMessages=FXCollections.observableArrayList();

    public static Map<String ,LatLng> rolesAndBounds=new LinkedHashMap<>();
    public static Map<String,LatLng> usersAndLocations=new LinkedHashMap<>();


    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        rolesAndBounds.put("Admin", LatLng.newBuilder()
                        .setLatitude(34.0)
                        .setLongitude(0.345)
                .build());
        rolesAndBounds.put("Staff", LatLng.newBuilder()
                .setLatitude(24.0)
                .setLongitude(0.123)
                .build());
        rolesAndBounds.put("Security", LatLng.newBuilder()
                .setLatitude(12.0)
                .setLongitude(0.234)
                .build());
        setupDb();

        /**
         *
         * Start the server
         */
        SmartServer server=new SmartServer(args);
        server.start();
        eventsList.add(Calendar.getInstance().getTime()+" :Server Started on port "+server.server.getPort());
        server.server.awaitTermination();

        /**
         * Client configuration. We will use the UI to make the process simpler
         */

    }

    /**
     * Creates necessay tables such as Users and alerts
     * @throws SQLException
     */
    private static void setupDb() throws SQLException {
        Connection connection=getConnection();
        connection.setAutoCommit(true);
        String sql="CREATE TABLE IF NOT EXISTS Users(userName TEXT NOT NULL,password TEXT NOT NULL,roles TEXT NOT NULL)";
        PreparedStatement statement= connection.prepareStatement(sql);
        statement.execute();
        sql="CREATE TABLE IF NOT EXISTS Alerts(dateOfIncident TIMESTAMP NOT NULL,message TEXT NOT NULL)";
        statement= connection.prepareStatement(sql);
        statement.execute();
        statement.closeOnCompletion();
        connection.close();

    }

    /**
     * Creates SQLITE JDBC Connection from file.
     * File based dbs are faster which is consistent with RPC
     * @return:Connection
     */

    public static Connection getConnection(){
        try {
            return DriverManager.getConnection("jdbc:sqlite:flavio.db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}