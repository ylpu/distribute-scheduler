package com.yl.distribute.scheduler.service.impl;

import javax.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.common.dao.BaseDao;
import com.yl.distribute.scheduler.common.enums.TaskStatus;
import com.yl.distribute.scheduler.common.service.impl.BaseServiceImpl;
import com.yl.distribute.scheduler.dao.SchedulerTaskMapper;
import com.yl.distribute.scheduler.entity.SchedulerTask;
import com.yl.distribute.scheduler.service.JobService;
import com.yl.distribute.scheduler.service.TaskService;

@Service
@Transactional
public class TaskServiceImpl extends BaseServiceImpl<SchedulerTask,String> implements TaskService {

    @Autowired
    private SchedulerTaskMapper schedulerTaskMapper;
    
    @Autowired
    private JobService jobService;

    @Override
    protected BaseDao<SchedulerTask, String> getDao() {
        return schedulerTaskMapper;
    }

	@Override
	public String getErrorLog(String id) {
		return null;
	}

	@Override
	public String getOutputLog(String id) {
		return null;
	}

	@Override
	public void addTask(TaskRequest task) {
		SchedulerTask schedulerTask = new SchedulerTask();
		if(task != null) {
			BeanUtils.copyProperties(task, schedulerTask);
			schedulerTask.setJobId(task.getJob().getJobId());
			schedulerTask.setTaskStatus(task.getTaskStatus().toString());
			insertSelective(schedulerTask);
		}
	}

	@Override
	public void updateTask(TaskRequest task) {
		SchedulerTask schedulerTask = new SchedulerTask();
		if(task != null) {
			BeanUtils.copyProperties(task, schedulerTask);
			schedulerTask.setJobId(task.getJob().getJobId());
			schedulerTask.setTaskStatus(task.getTaskStatus().toString());
			updateByPrimaryKeySelective(schedulerTask);
		}
	}

	@Override
	public TaskRequest getTaskById(String id) {
		TaskRequest task = new TaskRequest();
		SchedulerTask schedulerTask = findOneById(id);
		if(schedulerTask != null) {
		   BeanUtils.copyProperties(schedulerTask, task);
		   task.setTaskStatus(TaskStatus.getTaskStatus(schedulerTask.getTaskStatus()));
		   task.setJob(jobService.getJobById(schedulerTask.getJobId()));
		}
		return task;
	}
}
