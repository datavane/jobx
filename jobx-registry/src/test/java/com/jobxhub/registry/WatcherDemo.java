package com.jobxhub.registry;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class WatcherDemo implements Watcher {

    public void process(WatchedEvent event) {
        System.out.println("Enter the process method,the event is :"+event);
        Event.EventType type = event.getType();
        switch (type) {
            case NodeDeleted:
                System.out.println("新建节点:" + event.getPath());
        }
    }

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        String connectionString = "127.0.0.1:2181";
        ZooKeeper zooKeeper = new ZooKeeper(connectionString, 15 * 1000, new WatcherDemo(), false);
        zooKeeper.create("/myEphmeralPath1", "random".getBytes(), OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        TimeUnit.SECONDS.sleep(60);
        zooKeeper.close();
    }
}
