package com.yl.distribute.scheduler.common.enums;

public enum JobStrategy {
	
	MEMORY,
    TASK,
    RANDOM;
    
    public static JobStrategy getJobStrategy(String jobStrategy) {
        for(JobStrategy js : JobStrategy.values()) {
            if(js.toString().equalsIgnoreCase(jobStrategy)) {
                return js;
            }
        }
        return JobStrategy.MEMORY;
    }
}
