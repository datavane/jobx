package com.jobxhub.common.graph;
/**
 * @Package org.opencron.common.graph
 * @Title: KahnTopo
 * @author hitechr
 * @date 2018/4/11 13:36
 * @version V1.0
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @Descriptions: 拓扑排序
 */
public class KahnTopo<T> {
    private List<Node<T>> result; // 用来存储结果集
    private boolean cycle=false;
    private Queue<Node<T>> setOfZeroIndegree; // 用来存储入度为0的顶点
    private Graph graph;

    /**
     * 构造函数，初始化
     * @param di
     */
    public KahnTopo(Graph di) {
        this.graph = di;
        this.result = new ArrayList<>();
        this.setOfZeroIndegree = new LinkedList<>();
        // 对入度为0的集合进行初始化
        for(Node iterator : this.graph.vertexSet){
            if(iterator.getPathIn() == 0){
                this.setOfZeroIndegree.add(iterator);
            }
        }
        process();
    }

    /**
     * 拓扑排序处理过程
     */
    private void process() {
        while (!setOfZeroIndegree.isEmpty()) {
            Node v = setOfZeroIndegree.poll();

            // 将当前顶点添加到结果集中
            result.add(v);

            if(this.graph.edgeNode.keySet().isEmpty()){
                if( setOfZeroIndegree.isEmpty()){
                    return;
                }else{
                    continue;
                }
            }

            if(this.graph.edgeNode.get(v)==null){//尾结点
                continue;
            }

            try {
                // 遍历由v引出的所有边
                for (Node w : this.graph.edgeNode.get(v) ) {
                    // 将该边从图中移除，通过减少边的数量来表示
                    w.setPathIn(w.getPathIn()-1);
                    if (0 == w.getPathIn()){ // 如果入度为0，那么加入入度为0的集合
                        setOfZeroIndegree.add(w);
                    }
                }
            } catch (Exception e) {
                System.out.println(v+" 出错了" + this.graph.edgeNode.get(v));
            }
            this.graph.vertexSet.remove(v);
            this.graph.edgeNode.remove(v);
        }

        // 如果此时图中还存在边，那么说明图中含有环路
        if (!this.graph.vertexSet.isEmpty()) {
            cycle=true;
        }
    }

    /**
     * 是否有环路
     * @return
     */
    public boolean hasCycle(){
        return cycle;
    }

    /**
     * 结果集，如果有环路则抛出异常
     * @return
     */
    public Iterable<Node<T>> getResult() {
        if(cycle){
            throw new IllegalArgumentException("Has Cycle !");
        }
        return result;
    }

    public static void main(String[] args) {

            Node n25 = new Node(25);
            Node n26 = new Node(26);
            Node n24 = new Node(24);
            Node n29 = new Node(29);
            Node n41 = new Node(41);
            Node n44 = new Node(44);
            Node n45 = new Node(45);
            Node n46 = new Node(46);

            Graph graph = new Graph();
           graph.addNode(n25,n24)
                   .addNode(n26,n24)
                   .addNode(n24,n29)
                   .addNode(n24,n41)
                   .addNode(n41,n44)
                   .addNode(n24,n46)
                   .addNode(n29,n45);


           /* KahnTopo topo = new KahnTopo(graph);
            topo.process();
            for(Node temp : topo.getResult()){
                System.out.print(temp.getVal().toString() + "（"+temp.getLevel()+"）-->");
            }*/
        }

}
