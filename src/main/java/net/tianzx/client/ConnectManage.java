package net.tianzx.client;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: tianzx
 * Date: 3/13/18
 * Time: 10:11 AM
 */
public class ConnectManage {
    private static final Logger logger = LoggerFactory.getLogger(ConnectManage.class);

    private volatile static ConnectManage connectManage;

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            16, 16, 600L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(65536));

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
    private CopyOnWriteArrayList<RpcClientHandler> connectedHandlers = new CopyOnWriteArrayList<>();

    private ReentrantLock lock = new ReentrantLock();
    private Condition connected = lock.newCondition();
    private long connectTimeoutMillis = 6000;
    private AtomicInteger roundRobin = new AtomicInteger(0);
    private volatile boolean isRunning = true;

    private ConnectManage() {
    }

    public static ConnectManage getInstance() {
        if (connectManage == null) {
            synchronized (ConnectManage.class) {
                if (connectManage == null) {
                    connectManage = new ConnectManage();
                }
            }
        }
        return connectManage;
    }

    public void updateConnectedServer(List<String> allServerAddress) {
        if (allServerAddress != null) {

        } else {
            logger.error("No available server node. All server nodes are down !!!");

        }
    }

    public RpcClientHandler chooseHandler() {
        CopyOnWriteArrayList<RpcClientHandler> handlers = (CopyOnWriteArrayList<RpcClientHandler>) this.connectedHandlers.clone();
        int size = handlers.size();
        while (isRunning && size <= 0) {
            try {
                boolean available = waitingForHandler();
                if(available){
                    handlers = (CopyOnWriteArrayList<RpcClientHandler>) this.connectedHandlers.clone();
                    size = handlers.size();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.error("Waiting for available node is interrupted! ", e);
                throw new RuntimeException("Can't connect any servers!", e);
            }
            int index = (roundRobin.getAndAdd(1) + size) % size;
            return handlers.get(index);
        }
        return null;
    }

    private boolean waitingForHandler() throws InterruptedException {
        lock.lock();
        try {
            return connected.await(this.connectTimeoutMillis, TimeUnit.MILLISECONDS);
        } finally {
            lock.unlock();
        }
    }

    public void stop() {
        isRunning = false;
        for (int i = 0; i < connectedHandlers.size(); ++i) {
            RpcClientHandler connectedServerHandler = connectedHandlers.get(i);
        }
    }
}
