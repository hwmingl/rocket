package com.fighter.rocket.spring;

import com.fighter.rocket.server.RocketServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by hanwm on 17/3/29.
 */
public class SpringCtx {

    private static RocketServer rocketServer;
    public static ApplicationContext ctx;

    static {
        String otherApushSpringXml = System.getProperty("otherSpringXml");
        if (null != otherApushSpringXml) {
            ctx = new ClassPathXmlApplicationContext(otherApushSpringXml, "rocket/socketio/rocket-spring.xml");
        } else {
            ctx = new ClassPathXmlApplicationContext("rocket/socketio/rocket-spring.xml");
        }
    }

    public static RocketServer getRocketServer(){
        if (null != rocketServer){
            return rocketServer;
        }
        synchronized (SpringCtx.class){
            if (rocketServer == null){
                rocketServer = ctx.getBean(RocketServer.class);
            }
            return rocketServer;
        }
    }

}
