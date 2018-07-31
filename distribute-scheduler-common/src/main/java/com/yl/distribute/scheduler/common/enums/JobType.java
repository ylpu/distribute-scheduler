package com.yl.distribute.scheduler.common.enums;

public enum JobType { 
    
    COMMAND,
    JAR;
    
    public static JobType getTaskType(String taskType) {
        for(JobType tt : JobType.values()) {
            if(tt.toString().equalsIgnoreCase(taskType)) {
                return tt;
            }
        }
        throw new RuntimeException("can not find task type " + taskType);
    }
}
