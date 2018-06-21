package com.jobxhub.common.graph;
/**
 * @Package org.opencron.common.graph
 * @Title: Graph
 * @author hitechr
 * @date 2018/4/11 13:03
 * @version V1.0
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Descriptions:
 */
public class Graph {

    /**
     * 图中节点的集合
     */
    public Set<Node> vertexSet= new HashSet<>();

    /**
     * 相邻的节点，纪录边
     */
    public Map<Node, Set<Node>> edgeNode = new HashMap<>();

    /**
     * 将节点添加到图中
     * @param source
     * @param target
     * @return
     */
    public Graph addNode(Node source,Node target){
        if(source==null
                || target==null){
            return this;
        }

        if(target.getLevel()<=source.getLevel()){
            target.setLevel(source.getLevel()+1);
        }

        if(!vertexSet.contains(source)){
            vertexSet.add(source);
        }

        if(!vertexSet.contains(target)){
            vertexSet.add(target);
        }
        if(edgeNode.containsKey(source)
                && edgeNode.get(source).contains(target)){
            return this;
        }
        if(edgeNode.containsKey(source)){
            edgeNode.get(source).add(target);
        }else {
            Set<Node> targetSet = new HashSet<>();
            targetSet.add(target);
            edgeNode.put(source,targetSet);
        }
        target.pathIn();
        return this;
    }


    public Set<Node> getVertexSet() {
        return vertexSet;
    }

    public Map<Node, Set<Node>> getEdgeNode() {
        return edgeNode;
    }
}
