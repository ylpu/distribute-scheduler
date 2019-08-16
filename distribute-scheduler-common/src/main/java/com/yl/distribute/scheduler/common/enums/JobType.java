package com.yl.distribute.scheduler.common.enums;

public enum JobType { 
    
    COMMAND,
    JAVA,
	HIVE,
	SPARK;

	public static JobType getTaskType(String taskType) {
        for(JobType tt : JobType.values()) {
            if(tt.toString().equalsIgnoreCase(taskType)) {
                return tt;
            }
        }
        throw new RuntimeException("does not support task type " + taskType);
    }
}
