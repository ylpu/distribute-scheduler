package com.yl.distribute.scheduler.common.enums;

import java.io.Serializable;

public enum TaskStatus implements Serializable{
	
   SUBMIT,
   
   START,
    
   FAILED,
   
   SUCCESS,
   
   RUNNING;
   
   public static TaskStatus getTaskStatus(String taskStatus) {
       for(TaskStatus ts : TaskStatus.values()) {
           if(ts.toString().equalsIgnoreCase(taskStatus)) {
               return ts;
           }
       }
       throw new RuntimeException("does not support task type " + taskStatus);
   }
 
}
