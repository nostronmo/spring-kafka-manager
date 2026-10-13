package com.nostronmo.manage.instancer.logic.quartz.jobs;

import java.util.List;

public interface ScheduledTaskExecutor {

	void execute(String jobId, String targetService, List<String> duration);

	void stop(String jobId, String targetService);
	
	String getTopic();

	String getBeanName();

}
