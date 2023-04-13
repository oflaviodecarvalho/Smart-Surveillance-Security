package org.flavio.server;

import org.flavio.server.clients.RPCClients;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        setupDb();

        /**
         *
         * Start the server
         */
        SmartServer server=new SmartServer();
        server.start();
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