package com.nostronmo.manage.instancer.logic.quartz.jobs;

import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import com.nostronmo.manage.instancer.global.ContextProvider;
import com.nostronmo.manage.instancer.logic.quartz.component.QuartzLogicComponent;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@DisallowConcurrentExecution
@NoArgsConstructor
public class DelegatingJob implements Job {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			JobDataMap dataMap = context.getMergedJobDataMap();
			String jobId = dataMap.getString("jobId");
			String executorBeanName = dataMap.getString("executorBeanName");

			log.info("Quartz Execution :: Job ID: {} , on executor: {}", jobId, executorBeanName);
			ScheduledTaskExecutor executor = ContextProvider.getBean(executorBeanName, ScheduledTaskExecutor.class);
			executor.execute(dataMap.getString("jobId"), dataMap.getString("targetService"),
					(List<String>) dataMap.get("duration"));

			QuartzLogicComponent schedulerService = ContextProvider.getBean(QuartzLogicComponent.class);
			schedulerService.updateLastRun(jobId);

		} catch (Exception e) {
			throw new JobExecutionException("Failed to find or execute the ScheduledTaskExecutor: {}", e.getCause());
		}
	}

}
