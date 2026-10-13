package com.nostronmo.manage.instancer.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.boot.quartz.autoconfigure.QuartzProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Lazy(false)
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class QuartzConfiguration implements SchedulingConfigurer {

	private final DataSource dataSource;
	private final PlatformTransactionManager transactionManager;
	
	@Bean
	SchedulerFactoryBean schedulerFactoryBean(QuartzProperties quartzProperties) {
	    SchedulerFactoryBean factory = new SchedulerFactoryBean();

	    factory.setDataSource(dataSource);
	    factory.setTransactionManager(transactionManager);
	    factory.setOverwriteExistingJobs(true);
	    factory.setAutoStartup(true);
	    factory.setWaitForJobsToCompleteOnShutdown(true);

	    Properties properties = new Properties();
	    properties.putAll(quartzProperties.getProperties());
	    factory.setQuartzProperties(properties);

	    return factory;
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(4);
		scheduler.setThreadNamePrefix("ai-scheduler-");
		scheduler.setWaitForTasksToCompleteOnShutdown(true);
		scheduler.setAwaitTerminationSeconds(30);

		scheduler.setErrorHandler(throwable -> log.error("Unhandled exception caught in scheduled task", throwable));

		scheduler.initialize();
		taskRegistrar.setTaskScheduler(scheduler);
	}

}
