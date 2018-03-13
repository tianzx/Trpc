package net.tianzx.registy;

import net.tianzx.constants.Constant;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 * User: tianzx
 * Date: 3/12/18
 * Time: 1:41 PM
 */
public class ServiceRegister {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRegister.class);

    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private String registerAddress;

    public ServiceRegister(String registerAddress) {
        this.registerAddress = registerAddress;
    }

    public void registerData(String data) {
        if (data != null) {
            ZooKeeper zk = connectServer();
            if (zk != null) {
                addRootNode(zk); // Add root node if not exist
                createNode(zk, data);
            }
        }

    }

    private void createNode(ZooKeeper zk, String data) {
        byte[] bytes = data.getBytes();
        try {
            String path = zk.create(Constant.ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.err.println("create zookeeper node ({} => {})" + path + data);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addRootNode(ZooKeeper zk) {
        try {
            Stat stat = zk.exists(Constant.ZK_REGISTRY_PATH, false);
            if (stat == null) {
                zk.create(Constant.ZK_REGISTRY_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registerAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {

                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return zk;
    }

    public static void main(String[] args) {
        ServiceRegister serviceRegister = new ServiceRegister("101.200.122.27:2181");
        serviceRegister.registerData("localhost:18866");
    }
}
