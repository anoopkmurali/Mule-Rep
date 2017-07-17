package com.concretepage.springbatch;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;




public class JobMain {
	
		public static void main(String[] args) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {

			SpringApplication app = new SpringApplication(BatchConfiguration.class);
		    app.setWebEnvironment(false);
		    ConfigurableApplicationContext ctx = app.run(args);

		    JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);
		    Job job = ctx.getBean("createMarkSheet", Job.class);
		    JobParameters jobParameters = new JobParametersBuilder().toJobParameters();

		    JobExecution jobExecution = jobLauncher.run(job, jobParameters);
		    BatchStatus batchStatus = jobExecution.getStatus();
		    System.out.println("Batch Status :"+batchStatus);
		    
		}



}
