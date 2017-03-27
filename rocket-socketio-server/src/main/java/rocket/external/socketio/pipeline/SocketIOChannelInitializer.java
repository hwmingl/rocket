package rocket.external.socketio.pipeline;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocket.external.socketio.config.Configuration;
import rocket.external.socketio.pipeline.handler.AuthorizeHandler;
import rocket.external.socketio.pipeline.handler.WrongUrlHandler;
import rocket.external.socketio.scheduler.CancelableScheduler;
import rocket.external.socketio.scheduler.impl.HashedWheelScheduler;


/**
 * Created by hanwm on 17/3/24.
 */
public class SocketIOChannelInitializer extends ChannelInitializer<Channel> {

    private Logger  logger = LoggerFactory.getLogger(SocketIOChannelInitializer.class);


    public static final String       SOCKETIO_ENCODER       = "socketioEncoder";
    public static final String       AUTHORIZE_HANDLER      = "authorizeHandler";
    public static final String       PACKET_HANDLER         = "packetHandler";
    public static final String       HTTP_ENCODER           = "httpEncoder";
    public static final String       HTTP_AGGREGATOR        = "aggregator";
    public static final String       HTTP_DECODER           = "httpDecoder";
    public static final String       WRONG_URL_HANDLER      = "wrongUrlBlocker";



    private final int protocol = 1;
    private Configuration configuration;
    private CancelableScheduler scheduler = new HashedWheelScheduler();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        scheduler.update(ctx);
    }

    public void start(Configuration configuration) {
        this.configuration = configuration;

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
