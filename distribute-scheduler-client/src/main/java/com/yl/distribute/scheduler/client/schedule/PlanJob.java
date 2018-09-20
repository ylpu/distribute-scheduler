package com.yl.distribute.scheduler.client.schedule;

import java.sql.SQLException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.yl.distribute.scheduler.common.bean.JobConf;
import com.yl.distribute.scheduler.common.bean.WorkFlow;

/**
 * job plan是一个定时任务
 *
 */
public class PlanJob implements Job{
    
    private static Log LOG = LogFactory.getLog(PlanJob.class);
    
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        
        WorkFlow plan = (WorkFlow) jobExecutionContext.getJobDetail().getJobDataMap().get("data");
        JobParser jobParser = null;
        try {
            jobParser = new JobParser(plan.getJobPlan().getJobPlanFile().getAsciiStream());
        } catch (SQLException e) {
            LOG.error(e);
        }
        JobConf jobConf = jobParser.getRootJob(jobParser.readStream());
        new JobDriver(jobConf).start();
    }
}