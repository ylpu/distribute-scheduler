package com.yl.distribute.scheduler.common.enums;

 public enum  OSInfo {	 

    Linux("linux"),    

    Windows("windows");
	 
    private String description;
    
    private static String OS = System.getProperty("os.name").toLowerCase(); 

    private OSInfo(String desc){
        this.description = desc;
    }
    
    public String getDescription() {
		return description;
	}

	public static OSInfo getOsInfo() {
    	for(OSInfo osinfo : OSInfo.values()) {
    		if(OS.indexOf(osinfo.getDescription()) >= 0) {
    			return osinfo;
    		}
    	}
    	return OSInfo.Linux;
    }
}