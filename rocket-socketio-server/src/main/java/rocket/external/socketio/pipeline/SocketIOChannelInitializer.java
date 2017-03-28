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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocket.external.socketio.config.Configuration;
import rocket.external.socketio.pipeline.handler.*;
import rocket.external.socketio.scheduler.CancelableScheduler;
import rocket.external.socketio.scheduler.impl.HashedWheelScheduler;

import javax.annotation.concurrent.ThreadSafe;


/**
 * Created by hanwm on 17/3/24.
 */
@ThreadSafe
public class SocketIOChannelInitializer extends ChannelInitializer<Channel> {

    private Logger  logger = LoggerFactory.getLogger(SocketIOChannelInitializer.class);

    public static final String       RESOURCE_HANDLER       = "resourceHandler";
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

    private ResourceHandler  resourceHandler;
    private AuthorizeHandler authorizeHandler;
    private PacketHandler    packetHandler;
    private EncoderHandler   encoderHandler;
    private WrongUrlHandler  wrongUrlHandler;


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        scheduler.update(ctx);
    }

    public void start(Configuration configuration) {
        this.configuration = configuration;
        String connectPath = configuration.getContext() + "/" + protocol + "/";

        resourceHandler = new ResourceHandler(configuration.getContext());
        authorizeHandler = new AuthorizeHandler(connectPath,scheduler,configuration);


    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(HTTP_DECODER, new HttpRequestDecoder());
        pipeline.addLast(HTTP_AGGREGATOR, new HttpObjectAggregator(configuration.getMaxHttpContentLength()));
        pipeline.addLast(HTTP_ENCODER, new HttpResponseEncoder());


        pipeline.addLast(RESOURCE_HANDLER, resourceHandler);
        pipeline.addLast(AUTHORIZE_HANDLER, authorizeHandler);
        //pipeline.addLast(SOCKETIO_ENCODER, new EncoderHandler());
        pipeline.addLast(WRONG_URL_HANDLER, new WrongUrlHandler());

        //pipeline.addLast("http-codec",new HttpServerCodec());
        //pipeline.addLast("aggregator",new HttpObjectAggregator(65536));
        //pipeline.addLast("http-chunked",new ChunkedWriteHandler());
        //pipeline.addLast("handler",new WebSocketHandler());
    }


}
