package rocket.external.socketio.scheduler.impl;

import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import rocket.external.socketio.scheduler.CancelableScheduler;
import rocket.external.socketio.scheduler.key.SchedulerKey;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by hanwm on 17/3/24.
 */
public class HashedWheelScheduler implements CancelableScheduler {

    private final Map<SchedulerKey, Timeout> scheduledFutures = Maps.newConcurrentMap();
    private final HashedWheelTimer executorService  = new HashedWheelTimer();
    private volatile ChannelHandlerContext   ctx;

    @Override
    public void update(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void cancel(SchedulerKey key) {
        Timeout timeout = scheduledFutures.remove(key);
        if (timeout != null) {
            timeout.cancel();
        }
    }

    @Override
    public void schedule(final Runnable runnable, long delay, TimeUnit unit) {
        executorService.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                runnable.run();
            }
        }, delay, unit);
    }

    @Override
    public void schedule(final SchedulerKey key, final Runnable runnable, long delay, TimeUnit unit) {
        Timeout timeout = executorService.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                ctx.executor().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            runnable.run();
                        } finally {
                            scheduledFutures.remove(key);
                        }
                    }
                });
            }
        }, delay, unit);
        scheduledFutures.put(key, timeout);
    }

    @Override
    public void shutdown() {
        executorService.stop();
    }

}
