package rocket.external.socketio.pipeline.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by hanebert on 17/3/25.
 */
@Sharable
public class WrongUrlHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(WrongUrlHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest req = (FullHttpRequest) msg;
            Channel channel = ctx.channel();
            QueryStringDecoder queryDecoder = new QueryStringDecoder(req.uri());

            HttpResponse res = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
            channel.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
            req.release();
            logger.warn("Wrong request! url: {}, ip: {}", queryDecoder.path(), channel.remoteAddress());
        }
    }
}
