package com.hotinno.feedmonitor.batch.feed;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

//@Component
public class FeedsMonitorBatch {

	private final Log log = LogFactory.getLog(getClass().getName());

	@Autowired
	private JobLauncher jobLauncher;

	private final Job job;

	// Every 10 seconds
	//@Scheduled(fixedDelay = 10000)
	// Every 15 minutes
	@Scheduled(cron = "0 */15 * * * ?")
	public void runMonitor() throws Throwable {
		JobParameters params = new JobParametersBuilder().addDate("date",
				new Date()).toJobParameters();

		JobExecution jobExecution = jobLauncher.run(job, params);

		BatchStatus batchStatus = jobExecution.getStatus();
		while (batchStatus.isRunning()) {
			log.info("Still running...");
			Thread.sleep(1000);
		}

		log.info(String.format("Exit status: %s", jobExecution.getExitStatus()
				.getExitCode()));
		JobInstance jobInstance = jobExecution.getJobInstance();
		log.info(String.format("job instance Id: %d", jobInstance.getId()));
	}

	public FeedsMonitorBatch(Job jobToRun) {
		this.job = jobToRun;
	}

}
