package com.yl.distribute.scheduler.common.enums;

public enum TaskStrategy {
	
	MEMORY,
    TASK,
    RANDOM;
    
    public static TaskStrategy getTaskStrategy(String taskStrategy) {
        for(TaskStrategy ts : TaskStrategy.values()) {
            if(ts.toString().equalsIgnoreCase(taskStrategy)) {
                return ts;
            }
        }
        return TaskStrategy.MEMORY;
    }
}
