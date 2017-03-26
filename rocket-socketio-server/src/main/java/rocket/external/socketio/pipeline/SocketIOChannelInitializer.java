package rocket.external.socketio.pipeline;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import rocket.external.socketio.config.Configuration;
import rocket.external.socketio.pipeline.handler.AuthorizeHandler;
import rocket.external.socketio.pipeline.handler.EncoderHandler;
import rocket.external.socketio.pipeline.handler.WebSocketHandler;
import rocket.external.socketio.pipeline.handler.WrongUrlHandler;

/**
 * Created by hanwm on 17/3/24.
 */
public class SocketIOChannelInitializer extends ChannelInitializer<Channel> {

    public static final String       SOCKETIO_ENCODER       = "socketioEncoder";
    public static final String       AUTHORIZE_HANDLER      = "authorizeHandler";
    public static final String       PACKET_HANDLER         = "packetHandler";
    public static final String       HTTP_ENCODER           = "httpEncoder";
    public static final String       HTTP_AGGREGATOR        = "aggregator";
    public static final String       HTTP_DECODER           = "httpDecoder";
    public static final String       WRONG_URL_HANDLER      = "wrongUrlBlocker";


    private Configuration configuration;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(HTTP_DECODER, new HttpRequestDecoder());
        pipeline.addLast(HTTP_AGGREGATOR, new HttpObjectAggregator(65536));
        pipeline.addLast(HTTP_ENCODER, new HttpResponseEncoder());

        pipeline.addLast(AUTHORIZE_HANDLER, new AuthorizeHandler());
        //pipeline.addLast(SOCKETIO_ENCODER, new EncoderHandler());
        pipeline.addLast(WRONG_URL_HANDLER, new WrongUrlHandler());

        //pipeline.addLast("http-codec",new HttpServerCodec());
        //pipeline.addLast("aggregator",new HttpObjectAggregator(65536));
        //pipeline.addLast("http-chunked",new ChunkedWriteHandler());
        //pipeline.addLast("handler",new WebSocketHandler());
    }


}
