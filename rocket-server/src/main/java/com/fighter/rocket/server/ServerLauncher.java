package com.fighter.rocket.server;

import com.fighter.rocket.spring.SpringCtx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocket.external.socketio.config.Configuration;
import rocket.external.socketio.server.SocketIOServer;

/**
 * Created by hanebert on 17/3/23.
 */
public class ServerLauncher {

    private static final Logger logger = LoggerFactory.getLogger(ServerLauncher.class);

    private static void start(){

        final RocketServer rocketServer = SpringCtx.getRocketServer();
        rocketServer.start();

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                logger.warn("ShutdownHook begin");
                rocketServer.stop();
                logger.warn("ShutdownHook end");
            }
        });
    }

    public static void stop(){
        final RocketServer rocketServer = SpringCtx.getRocketServer();
        rocketServer.stop();
    }


    public static void main(String[] args){
        start();
    }

}
