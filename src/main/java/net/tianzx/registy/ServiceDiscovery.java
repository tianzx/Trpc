package net.tianzx.registy;

import net.tianzx.client.ConnectManage;
import net.tianzx.constants.Constant;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 * User: tianzx
 * Date: 3/12/18
 * Time: 3:33 PM
 */
public class ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

    private volatile List<String> dataList = new ArrayList<>();

    private String registerAddress;

    private ZooKeeper zk;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public ServiceDiscovery(String registerAddress) {
        this.registerAddress = registerAddress;
        zk = connectServer(registerAddress);
        if (zk != null) {
            watchNode(zk);
        }
    }

    private void watchNode(ZooKeeper zk) {
        try {
            List<String> nodeList = zk.getChildren(Constant.ZK_REGISTRY_PATH, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.NodeChildrenChanged) {
                        watchNode(zk);
                    }
                }
            });
            List<String> dataList = new ArrayList<>();
            for (String node : nodeList) {
                byte[] bytes = zk.getData(Constant.ZK_REGISTRY_PATH + "/" + node, false, null);
                System.err.println(new String(bytes));
                dataList.add(new String(bytes));
            }
//            System.err.println(da);
            logger.debug("node data: {}", dataList);
            this.dataList = dataList;
            logger.debug("Service discovery triggered updating connected server node.");
            updateConnectedServer();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ZooKeeper connectServer(String registerAddress) {
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

    private void updateConnectedServer(){
        ConnectManage.getInstance().updateConnectedServer(this.dataList);
    }

    public void stop(){
        if(zk!=null){
            try {
                zk.close();
            } catch (InterruptedException e) {
                logger.error("", e);
            }
        }
    }
    public static void main(String[] args) {
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery("101.200.122.27:2181");
//        serviceDiscovery
        for(;;){

        }
    }
}
