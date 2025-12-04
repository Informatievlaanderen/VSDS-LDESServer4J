package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
public class BatchConfiguration {
	public static final String ASYNC_JOB_LAUNCHER = "asyncJobLauncher";

	@Bean(name = ASYNC_JOB_LAUNCHER)
	public JobLauncher simpleJobLauncher(JobRepository jobRepository) throws Exception {
		TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor("fragmentation_batch"));
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}
}
