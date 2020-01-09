package com.justfors;

import com.dosse.upnp.UPnP;
import com.justfors.client.Client;
import com.justfors.network.Connection;
import com.justfors.server.Server;
import com.justfors.windows.ChatWindow;

public class Main {

    public static void main(String[] args) {

        while (ChatWindow.nickname == null) {}
        Connection connection = new Connection(ChatWindow.nickname);
        if(UPnP.openPortTCP(7777)) {
            new Server(7777, connection).start();
        }
        String host = ChatWindow.ip;
        Integer port = ChatWindow.port;
        new Client(host, port, connection).start();
    }
}
