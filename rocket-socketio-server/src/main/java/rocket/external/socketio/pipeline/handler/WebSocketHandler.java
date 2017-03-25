package rocket.external.socketio.pipeline.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


/**
 * Created by hanwm on 16/8/18.
 */
public class WebSocketHandler extends ChannelInboundHandlerAdapter{

    private Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    private WebSocketServerHandshaker webSocketServerHandshaker;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //HTTP接入
        if (msg instanceof FullHttpRequest){
            handleHttpRequest(ctx , (FullHttpRequest) msg);
        }
        //WebSocket接入
        else if (msg instanceof WebSocketFrame){
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }


    private void handleHttpRequest(ChannelHandlerContext ctx,FullHttpRequest request){
        //如果HTTP解码失败，返回HTTP异常
        if (!request.decoderResult().isSuccess() || (!"websocket".equals(request.headers().get("Upgrade").toString().toLowerCase()))){
            sendHttpResponse(ctx,request,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        //构造握手响应返回，本机测试
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(request),
                null,false);
        webSocketServerHandshaker = wsFactory.newHandshaker(request);
        if (webSocketServerHandshaker == null){
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        }else {
            webSocketServerHandshaker.handshake(ctx.channel(), request);

            //QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
            //Map<String,List<String>> params = queryStringDecoder.parameters();
            //String uid = params.get("uid").get(0);
            logger.info("web socket handshake successfully.");
        }
    }


    private String getWebSocketLocation(HttpRequest req) {
        String protocol = "ws://";
        return protocol + req.headers().get(HttpHeaders.Names.HOST) + req.getUri();
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx,FullHttpRequest request,FullHttpResponse response){
        //返回应答给客户端
        if (response.status().code() != 200){
            ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(response, response.content().readableBytes());
        }
        //如果是非Keep-Alive，关闭连接
        ChannelFuture channelFuture = ctx.channel().writeAndFlush(response);
        if (HttpUtil.isKeepAlive(request)){
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }


    private void handleWebSocketFrame(ChannelHandlerContext ctx,WebSocketFrame frame){
        //判断是否是关闭链路的指令
        if (frame instanceof CloseWebSocketFrame){
            webSocketServerHandshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }

        //判断是否是Ping消息
        if (frame instanceof PingWebSocketFrame){
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
        }

        //暂时只支持文本消息，不支持二进制
        if (!(frame instanceof TextWebSocketFrame)){
            throw new UnsupportedOperationException(String.format("%s frame types not supported",frame.getClass().getName()));
        }

        String requestTxt = ((TextWebSocketFrame)frame).text();
        if (requestTxt != null && requestTxt.contains(",")){
            /*String [] arr = requestTxt.split(",");
            String uid = arr[0];
            String txt = arr[1];
            Channel channel = channel2Client.get(uid);
            channel.writeAndFlush(new TextWebSocketFrame("To:"+ uid + ","+txt));*/
        }else {
            logger.info(ctx.channel()+" received "+requestTxt);
            ctx.channel().write(new TextWebSocketFrame(requestTxt + ",欢迎使用Netty WebSocket服务，现在时间：" + new Date().toString()));
        }

    }

}
