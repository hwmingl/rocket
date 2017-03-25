package com.fighter.rocket.server;

import rocket.external.socketio.server.SocketIOServer;

/**
 * Created by hanebert on 17/3/23.
 */
public class ServerLauncher {


    public static void main(String[] args){
        Configuration configuration = new Configuration();
        configuration.setHostname("127.0.0.1");
        configuration.setPort(9090);
        SocketIOServer socketIOServer = new SocketIOServer(configuration);
        socketIOServer.start();
    }

}
