package rocket.external.socketio.pipeline.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by hanwm on 17/3/27.
 */
@Sharable
public class ResourceHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(ResourceHandler.class);

    private final String context;

    public ResourceHandler(String context) {
        this.context = context;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest){
            FullHttpRequest httpRequest = (FullHttpRequest) msg;
            QueryStringDecoder queryDecoder = new QueryStringDecoder(httpRequest.uri());
            String path = queryDecoder.path();
            if (path.equals("/ok.jsp")){
                HttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
                HttpHeaders.setHeader(response, CONTENT_TYPE, "text/html; charset=UTF-8");
                ByteBuf content = Unpooled.copiedBuffer("OK", CharsetUtil.UTF_8);

                ctx.channel().write(response);
                // Close the connection as soon as the message is sent.
                ctx.channel().writeAndFlush(content).addListener(ChannelFutureListener.CLOSE);
                httpRequest.release();
                return;
            }
        }
        ctx.fireChannelRead(msg);
    }
}
