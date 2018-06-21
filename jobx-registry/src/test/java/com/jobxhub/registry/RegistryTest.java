package com.jobxhub.registry;

import com.jobxhub.registry.zookeeper.zkclient.ZkclientZookeeperClient;
import org.junit.Test;
import com.jobxhub.registry.zookeeper.ChildListener;
import com.jobxhub.registry.zookeeper.ZookeeperClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import com.jobxhub.common.util.collection.HashMap;

public class RegistryTest {

    private ZookeeperClient zookeeperClient;

    String url = "zookeeper://127.0.0.1:2181";

 //   @Before
    public void init() {
        zookeeperClient = new ZkclientZookeeperClient(URL.valueOf(url));
    }

    @Test
    public void create() throws IOException {
        zookeeperClient.create("/jobx/agent/6",true);
        System.in.read();
    }

    @Test
    public void delete() throws IOException {
        zookeeperClient.delete("/jobx/agent/2");
        System.in.read();
    }

    @Test
    public void lister() throws IOException {


        zookeeperClient.addChildListener("/jobx/agent",new ChildListener(){
            @Override
            public void childChanged(String path, List<String> children) {
                System.out.println("add:----->"+path);
                for (String child:children) {
                    System.out.println(child);
                }
            }
        });

        zookeeperClient.removeChildListener("/jobx/agent",new ChildListener(){
            @Override
            public void childChanged(String path, List<String> children) {
                System.out.println("remove:----->"+path);
                for (String child:children) {
                    System.out.println(child);
                }
            }
        });


        System.in.read();
    }

    @Test
    public void get(){
        Map<String,String> map = new HashMap<String, String>();
        map.putAll(null);
    }

    @Test
    public void backup(){
        URL url = URL.valueOf("zookeeper://127.0.0.1:2181");
        System.out.println(url.getBackupAddress());
    }

}
