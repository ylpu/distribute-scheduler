package com.yl.distribute.scheduler.common.enums;

import java.io.Serializable;

public enum JobStatus implements Serializable{
    
   FAILED("failed"),
   
   SUCCESS("success"),
   
   RUNNING("running");
   
   private String status;
    
   private JobStatus(String status) {
       this.status = status;
   }

   public String getStatus() {
       return status;
   }  
}
