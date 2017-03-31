package com.fighter.rocket.client;

import com.fighter.rocket.authorize.RocketAuthorize;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by hanwm on 17/3/29.
 */
public class WebSocketClient {

    private static final String TERMINAL_ID = UUID.randomUUID().toString();
    private static final String USER_ID = "user1001";
    private static final String APP_ID = "appTest";
    private static final String APP_KEY = "loveYouLove";
    private static final String HOST = "127.0.0.1";

    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final Bootstrap bootstrap = new Bootstrap();

    public void connect(String host,int port) {

    }

    private String getWebSocketAddr() throws UnsupportedEncodingException {
        String queryStr = getQueryStr();

        String authResult = getAuthorizeResult(queryStr);
        System.out.println("authResult:"+authResult);
        String[] args = authResult.split(":");
        StringBuffer stringBuffer = new StringBuffer("ws://"+ HOST +":6060/socket.io/1/websocket/");
        stringBuffer.append(args[0]).append(",").append(args[6].replaceAll("\\n",""));
        stringBuffer.append(URLDecoder.decode(queryStr,"UTF-8"));
        stringBuffer.append("&terminalId=").append(TERMINAL_ID);
        System.out.println(stringBuffer.toString());
        return stringBuffer.toString();
    }

    private String getAuthorizeResult(String queryStr){
        String serverHost = "http://" + HOST + ":6060/socket.io/1/";
        String query = queryStr.substring(7);
        Map<String,String> queryMap = new HashMap<String, String>();
        try {
            queryMap.put("param", URLDecoder.decode(query,"UTF-8"));
            queryMap.put("t", String.valueOf(System.currentTimeMillis()));
            queryMap.put("terminalId", TERMINAL_ID);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = null;
        HttpProxy httpProxy = new HttpProxy(serverHost);
        try {
            result = httpProxy.doGet(queryMap, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getQueryStr(){
        String queryStr = RocketAuthorize.getQueryString(APP_ID, APP_KEY, USER_ID, 24l * 60 * 60 * 1000 * 10000);
        return queryStr;
    }

    public static void main(String[] args) {
        String queryStr = new WebSocketClient().getQueryStr();
        System.out.println(queryStr);
        try {
            System.out.println(URLDecoder.decode(queryStr,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
