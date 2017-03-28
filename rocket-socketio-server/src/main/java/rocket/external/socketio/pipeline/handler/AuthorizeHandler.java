package rocket.external.socketio.pipeline.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocket.external.socketio.config.Configuration;
import rocket.external.socketio.scheduler.CancelableScheduler;

import java.util.concurrent.atomic.AtomicInteger;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


/**
 * Created by hanebert on 17/3/25.
 */
@Sharable
public class AuthorizeHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(AuthorizeHandler.class);

    private final String connectPath;
    private final CancelableScheduler cancelableScheduler;
    private final Configuration configuration;

    private static final AtomicInteger newCount     = new AtomicInteger(0);
    private static final AtomicInteger newFailCount = new AtomicInteger(0);

    public static final String               HTTP_DATE_FORMAT       = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String               HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int                  HTTP_CACHE_SECONDS     = 60;

    public AuthorizeHandler(String connectPath,CancelableScheduler cancelableScheduler,Configuration configuration){
        this.connectPath = connectPath;
        this.cancelableScheduler = cancelableScheduler;
        this.configuration = configuration;
    }

    public static AtomicInteger getNewCount() {
        return newCount;
    }

    public static AtomicInteger getNewFailCount() {
        return newFailCount;
    }

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
