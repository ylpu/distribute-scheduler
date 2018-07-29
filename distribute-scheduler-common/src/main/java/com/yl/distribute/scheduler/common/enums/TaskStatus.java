package com.yl.distribute.scheduler.common.enums;

import java.io.Serializable;

public enum TaskStatus implements Serializable{
	
   INITIAL("initial"),
    
   FAILED("failed"),
   
   SUCCESS("success"),
   
   RUNNING("running");
   
   private String status;
    
   private TaskStatus(String status) {
       this.status = status;
   }

   public String getStatus() {
       return status;
   }  
}
