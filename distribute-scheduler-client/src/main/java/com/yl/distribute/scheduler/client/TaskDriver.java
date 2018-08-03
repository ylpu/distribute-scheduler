package com.yl.distribute.scheduler.client;

import java.util.List;
import java.util.concurrent.TimeUnit;
import com.yl.distribute.scheduler.client.callback.ResponseQueue;
import com.yl.distribute.scheduler.common.bean.TaskResponse;


class Node{
    
    private String value;
    private List<Node> childs;
    
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
}

public class TaskDriver {
    /**
     * 解析任务并交给TaskClient去提交
     * @param rootTask
     * @throws Exception
     */
    public void runTask(String rootTask) throws Exception {
        
        TaskResponse task = null;
        while((task = ResponseQueue.getTaskQueue().poll(30,TimeUnit.SECONDS)) != null) {
            System.out.println(task.getId());
        }
    }
    
    public static void printNode(List<Node> nodes) {
        if(nodes == null ) {
            return;
        } else{
            for(Node n : nodes) {
                System.out.println(n.getValue());
                printNode(n.getChilds());
                
            }   
        }
    }
}
