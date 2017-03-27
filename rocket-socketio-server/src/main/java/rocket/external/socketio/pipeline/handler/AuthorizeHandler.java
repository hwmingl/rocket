package rocket.external.socketio.pipeline.handler;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by hanebert on 17/3/25.
 */
@Sharable
public class AuthorizeHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(AuthorizeHandler.class);
    public static final String               HTTP_DATE_FORMAT       = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String               HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int                  HTTP_CACHE_SECONDS     = 60;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof FullHttpRequest){
            FullHttpRequest request = (FullHttpRequest) msg;
            Channel channel = ctx.channel();
            QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri());
            if (queryDecoder.path().equals("auth")){
                // create ok response
                HttpResponse res = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
                channel.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
                return;
            }
        }

        ctx.fireChannelRead(msg);

    }
}
