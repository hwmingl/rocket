package rocket.external.socketio.scheduler;

import io.netty.channel.ChannelHandlerContext;
import rocket.external.socketio.scheduler.key.SchedulerKey;

import java.util.concurrent.TimeUnit;

/**
 * Created by hanwm on 17/3/24.
 */
public interface CancelableScheduler {

    void update(ChannelHandlerContext ctx);

    void cancel(SchedulerKey key);

    void schedule(Runnable runnable, long delay, TimeUnit unit);

    void schedule(SchedulerKey key, Runnable runnable, long delay, TimeUnit unit);

    void shutdown();

}
