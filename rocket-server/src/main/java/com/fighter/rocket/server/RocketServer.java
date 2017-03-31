package com.fighter.rocket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocket.external.socketio.server.SocketIOServer;

import java.util.Timer;

/**
 * Created by hanwm on 17/3/29.
 */
public class RocketServer {

    private static final Logger logger = LoggerFactory.getLogger(RocketServer.class);

    private SocketIOServer socketIOServer;
    private boolean isDevelopMode = true;
    private Timer heartbeatTimer = new Timer("Timer-socketIO heartbeat",true);

    public void start(){
        socketIOServer.start();
    }

    public void stop(){
        try {
            if (null != socketIOServer){
                socketIOServer.stop();
            }
            heartbeatTimer.cancel();
        } catch (Exception e){
            logger.error("RocketServer shut down error!",e);
        }
    }

    public SocketIOServer getSocketIOServer() {
        return socketIOServer;
    }

    public void setSocketIOServer(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
    }

    public boolean isDevelopMode() {
        return isDevelopMode;
    }

    public void setIsDevelopMode(boolean isDevelopMode) {
        this.isDevelopMode = isDevelopMode;
    }
}
