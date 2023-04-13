package org.flavio.server.clients;

import java.util.Scanner;

public class RPCClientsMain {
    public static void main(String[] args) throws InterruptedException {
        RPCClients clients=new RPCClients();
        clients.initClients();
        Scanner scanner=new Scanner(System.in);
        while (scanner.hasNextLine()){
           String data=scanner.nextLine();
           data=data.trim();
           clients.createUser(data.split("\\s")[0],data.split("\\s")[1]);
           //System.out.println(data);
        }
    }
}
