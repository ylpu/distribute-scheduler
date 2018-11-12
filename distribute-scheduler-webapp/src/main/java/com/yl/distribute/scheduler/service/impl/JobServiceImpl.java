package com.yl.distribute.scheduler.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yl.distribute.scheduler.common.bean.JobRequest;
import com.yl.distribute.scheduler.common.dao.BaseDao;
import com.yl.distribute.scheduler.common.enums.JobStrategy;
import com.yl.distribute.scheduler.common.service.impl.BaseServiceImpl;
import com.yl.distribute.scheduler.dao.SchedulerJobMapper;
import com.yl.distribute.scheduler.entity.SchedulerJob;
import com.yl.distribute.scheduler.service.JobService;

@Service
@Transactional
public class JobServiceImpl extends BaseServiceImpl<SchedulerJob,String> implements JobService {

    @Autowired
    private SchedulerJobMapper schedulerJobMapper;

    @Override
    protected BaseDao<SchedulerJob, String> getDao() {
        return schedulerJobMapper;
    }

	@Override
	public void addJob(JobRequest job) {		
		SchedulerJob schedulerJob = new SchedulerJob();
		if(job != null) {
			BeanUtils.copyProperties(job, schedulerJob);
			schedulerJob.setJobStrategy(job.getJobStrategy().toString());
			insertSelective(schedulerJob);
		}		
	}

	@Override
	public JobRequest getJobById(String id) {
		JobRequest jobRequest = new JobRequest();
		SchedulerJob schedulerJob = findOneById(id);
		if(schedulerJob != null) {
			BeanUtils.copyProperties(schedulerJob, jobRequest);
			jobRequest.setJobStrategy(JobStrategy.getJobStrategy(schedulerJob.getJobStrategy()));	
		}
		return jobRequest;
	}
}
