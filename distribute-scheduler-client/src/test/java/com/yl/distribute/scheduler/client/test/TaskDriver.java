package com.yl.distribute.scheduler.client.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

class Node{
    
    private String value;
    private String status;
    private List<Node> childs;
    private List<Node> parents;
    
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public List<Node> getChilds() {
        return childs;
    }
    public void setChilds(List<Node> childs) {
        this.childs = childs;
    }
    public List<Node> getParents() {
        return parents;
    }
    public void setParents(List<Node> parents) {
        this.parents = parents;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }    
    
    
}

public class TaskDriver {
    
    private static  Stack<Node> stack = new Stack<Node>();
    
    /**
     * 解析任务并交给TaskClient去提交
     * @param rootTask
     * @throws Exception
     */    
    public static void parseTask(List<Node> nodes) {
        if(nodes == null ) {
            return;
        } else{
            for(Node n : nodes) {
                parseTask(n.getChilds());
                if(!parentsFinished(n)){
                    stack.push(n);
                }
                else{
                    System.out.println(n.getValue());   
                }                            
                
            }   
        }
    }
    
    
    private static boolean parentsFinished(Node node){
        if(node.getParents() == null){
            return true;
        }
        for(Node n : node.getParents()){
            if(!n.getStatus().equalsIgnoreCase("s")){
                return false;
            }
        }
        return true;
    }
    
    public static void main(String[] args) throws InterruptedException{
        List<Node> childs = new ArrayList<Node>();
        
        Node node = new Node();
        node.setValue("a");
        node.setStatus("r");
        
        Node node1 = new Node();
        node1.setValue("b");
        node1.setStatus("i");
        
        Node node2 = new Node();
        node2.setValue("c");
        node2.setStatus("i");
        
        Node node3 = new Node();
        node3.setValue("d");
        node3.setStatus("i");
        
        childs.add(node1);
        childs.add(node2);
        node.setChilds(childs);
        node.setParents(null);
        
        node1.setParents(Arrays.asList(node));
        node1.setChilds(Arrays.asList(node3));       
        
        node2.setParents(Arrays.asList(node));
        node2.setChilds(Arrays.asList(node3));
        
        node3.setParents(Arrays.asList(node1,node2));
        node2.setChilds(null);
        
        parseTask(node.getChilds());        
        
        new Thread(new Runnable(){

            @Override
            public void run() {
                
                while(true){
                    if(!stack.empty()){
                        Node node = stack.pop();
                        if (parentsFinished(node)){
                            System.out.println(node.getValue());
                        }else{
                            stack.push(node);
                        }
                    }
    
                }
                
            }
            
        }).start();
        
        Thread.sleep(10000);
        node.setStatus("s");
        Thread.sleep(10000);
        node1.setStatus("s");
        node2.setStatus("s");        
    }
}
