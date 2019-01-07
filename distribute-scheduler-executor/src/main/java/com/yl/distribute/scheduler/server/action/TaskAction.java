package com.yl.distribute.scheduler.server.action;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.yl.distribute.scheduler.common.constants.GlobalConstants;
import com.yl.distribute.scheduler.common.enums.OSInfo;
import com.yl.distribute.scheduler.common.utils.IOUtils;
import com.yl.distribute.scheduler.common.utils.TaskProcessUtils;

@Path("task")
public class TaskAction {

    @POST
    @Path("kill/{taskId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void killTask(@PathParam("taskId") String taskId) throws Exception {
    	
    	String processFile = "";
    	OSInfo osinfo = OSInfo.getOsInfo();
    	if(osinfo == OSInfo.Windows) {
    		processFile = GlobalConstants.WIN_PID_DIR + taskId + ".pid";  		
    	}else if(osinfo == OSInfo.Linux) {
    		processFile = GlobalConstants.LINUX_PID_DIR + taskId + ".pid";   		
    	} 
    	String pid = IOUtils.readFile(processFile);
		TaskProcessUtils.killProcess(osinfo,pid);
    }
}
