package com.justfors.network;

import com.justfors.client.Client;
import com.justfors.client.NetConnectionClient;
import com.justfors.protocol.TransferData;
import com.justfors.server.NetConnectionServer;
import com.justfors.server.Server;
import com.justfors.stream.InputStream;
import com.justfors.stream.OutputStream;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.time.Instant;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Connection extends Thread implements NetConnectionClient, NetConnectionServer {

    private static Map<String, com.justfors.common.Connection> connections = new ConcurrentHashMap<>();

    private String userId;


    public Connection(String userId) {
        this.userId = userId;
    }

    @Override
    public void run() {
        String message = null;
        Scanner sc = new Scanner(System.in);
        while (true) {
            message = sc.nextLine();
            sendAll(prepareData(message));
        }
    }

    public void clientConnectionExecute(InputStream inputStream, OutputStream outputStream, Socket socket) throws IOException {
        acquaintance(outputStream);
        while (true) {
            try {
                String userMessage = inputStream.readLine();
                if (userMessage != null && !userMessage.equals("")) {
                    TransferData data = TransferData.reciveTransferData(userMessage);
                    receiveMessage(data, socket);
                }
            } catch (SocketException e) {
                break;
            }
        }
    }

    public void serverConnectionExecute(InputStream inputStream, OutputStream outputStream, Socket socket) throws IOException {
        acquaintance(outputStream);
        System.out.println("New connection : " + socket);
        while (true) {
            try {
                String userMessage = inputStream.readLine();
                if (userMessage != null && !userMessage.equals("")) {
                    TransferData data = TransferData.reciveTransferData(userMessage);
                    receiveMessage(data, socket);
                }
            } catch (SocketException e) {
                System.out.println("Connection close..." + socket);
                break;
            }
        }
    }

    private static void sendAll(String message) {
        for (Client.ClientConnection connection : Client.connections) {
            connection.getOut().send(message);
        }
        for (Server.ServerConnection connection : Server.connections) {
            connection.getOut().send(message);
        }
    }


    private static void sendTo(String userId, String message) {
        connections.get(userId).getOut().send(message);
    }

    private void receiveMessage(TransferData data, Socket socket) {
        if (data.getToken().equals(Token.NEW_CONNECTION)) {
            com.justfors.common.Connection conn = null;
            for (Server.ServerConnection connection : Server.connections) {
                if (connection.getSocket().equals(socket)){
                    conn = connection;
                }
            }
            for (Client.ClientConnection connection : Client.connections) {
                if (connection.getSocket().equals(socket)){
                    conn = connection;
                }
            }
            connections.put(data.getUser() + ":" + socket.getInetAddress().getHostAddress(), conn);
        } else if (data.getToken().equals(Token.CONNECTION_LIST)) {
            //TODO: create new connections
        } else {
            System.out.println(data.getData());
        }
    }

    private void acquaintance(OutputStream out) {
        TransferData data = new TransferData();
        data.setUser(this.userId);
        data.setCreateDate(Instant.now());
        data.setToken(Token.NEW_CONNECTION);
        out.send(data.build());
    }

    private String prepareData(String msg){
        TransferData data = new TransferData();
        data.setUser(this.userId);
        data.setCreateDate(Instant.now());
        data.setData(msg);
        data.setToken("");
        return data.build();
    }
}
