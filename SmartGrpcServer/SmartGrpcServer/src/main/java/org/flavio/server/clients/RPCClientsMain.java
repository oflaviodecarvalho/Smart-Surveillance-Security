package org.flavio.server.clients;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.naming.ldap.StartTlsResponse;
import javax.swing.*;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RPCClientsMain extends Application {
    public static RPCClients rpcClients;
    public static Executor executor;
    public static void main(String[] args) throws InterruptedException {
        rpcClients=new RPCClients();
        rpcClients.initClients();
      launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        executor= Executors.newSingleThreadExecutor(Executors.defaultThreadFactory());
        Parent root= FXMLLoader.load(getClass().getClassLoader().getResource("start.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
