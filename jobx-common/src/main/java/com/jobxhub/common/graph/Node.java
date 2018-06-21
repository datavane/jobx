package com.jobxhub.common.graph;
/**
 * @Package org.opencron.common.graph
 * @Title: Node
 * @author hitechr
 * @date 2018/4/11 13:06
 * @version V1.0
 */

/**
 * @Descriptions: 顶点数据元素
 */
public class Node<T> {
    private T val;
    private int pathIn = 0; // 入链路数量
    private int level;//节点的层次
    public Node(T val) {
        this.val = val;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPathIn() {
        return pathIn;
    }

    public void setPathIn(int pathIn) {
        this.pathIn = pathIn;
    }

    public void pathIn(){
        this.setPathIn(this.getPathIn()+1);
    }
    public void levelIn(){
        this.setLevel(this.getLevel()+1);
    }


    public T getVal() {
        return val;
    }

    public void setVal(T val) {
        this.val = val;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node<?> node = (Node<?>) o;

        return val.equals(node.val);
    }

    @Override
    public int hashCode() {
        return val.hashCode();
    }

    @Override
    public String toString() {
        return "Node{" +
                "val=" + val +
                ", pathIn=" + pathIn +
                ", level=" + level +
                '}';
    }
}
