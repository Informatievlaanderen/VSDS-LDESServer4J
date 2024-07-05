package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamClosedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewSupplier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MembersBucketisedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.NewViewBucketisedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.ViewNeedsRebucketisationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MembersIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch.BucketProcessor;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.FRAGMENTATION_CRON;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch.BucketJobDefinitions.*;

@Service
@EnableScheduling
public class FragmentationService {
	public static final String LDES_SERVER_CREATE_FRAGMENTS_COUNT = "ldes_server_create_fragments_count";
	private final JobLauncher jobLauncher;
	private final JobExplorer jobExplorer;
	private final ApplicationEventPublisher eventPublisher;
	private final AtomicBoolean shouldTriggerBucketisation = new AtomicBoolean(false);
	private final FragmentRepository fragmentRepository;
	private final Job bucketisationJob;
	private final Job rebucketiseJob;

	public FragmentationService(JobLauncher jobLauncher, JobExplorer jobExplorer, JobRepository jobRepository,
	                            PlatformTransactionManager transactionManager,
	                            @Qualifier("newMemberReader") ItemReader<FragmentationMember> newMemberReader,
	                            @Qualifier("refragmentEventStream") ItemReader<FragmentationMember> rebucketiseMemberReader,
	                            BucketProcessor processor,
	                            ItemWriter<List<BucketisedMember>> bucketWriter) {
		this.jobLauncher = jobLauncher;
		this.jobExplorer = jobExplorer;
		this.eventPublisher = eventPublisher;
		this.fragmentRepository = fragmentRepository;
		this.bucketisationJob = bucketiseJob(jobRepository, transactionManager, newMemberReader, processor, bucketWriter);
		this.rebucketiseJob = rebucketiseJob(jobRepository, transactionManager, rebucketiseMemberReader, processor, bucketWriter);
	}

	@EventListener(MembersIngestedEvent.class)
	public void executeFragmentation() {
		shouldTriggerBucketisation.set(true);
	}

	@EventListener
	public void handleViewInitializationEvent(ViewNeedsRebucketisationEvent event) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
		launchJob(rebucketiseJob, new JobParametersBuilder()
				.addString("viewName", event.viewName().asString())
				.addLocalDateTime("triggered", LocalDateTime.now())
				.toJobParameters());
	}

	@Scheduled(cron = FRAGMENTATION_CRON)
	public void scheduledJobLauncher() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
		if (shouldTriggerBucketisation.get() && !isJobRunning(BUCKETISATION_JOB) && !isJobRunning(REBUCKETISATION_JOB)) {
			shouldTriggerBucketisation.set(false);
			launchJob(bucketisationJob, new JobParameters());
		}
	}

	@EventListener
	public void markFragmentsImmutableInCollection(EventStreamClosedEvent event) {
		fragmentRepository.markFragmentsImmutableInCollection(event.collectionName());
	}

	@EventListener({ViewAddedEvent.class, ViewInitializationEvent.class})
	@Order
	public void handleViewAddedEvent(ViewSupplier event) {
		eventPublisher.publishEvent(new ViewNeedsRebucketisationEvent(event.viewSpecification().getName()));
	}

	private void launchJob(Job job, JobParameters jobParameters) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
		JobExecution jobExecution = jobLauncher.run(job, jobParameters);

		if (jobExecution.getStepExecutions().stream().toList().getFirst().getWriteCount() != 0) {
			if (job.getName().equals(BUCKETISATION_JOB)) {
				eventPublisher.publishEvent(new MembersBucketisedEvent());
			} else if (job.getName().equals(REBUCKETISATION_JOB)) {
				eventPublisher.publishEvent(new NewViewBucketisedEvent(jobParameters.getString("viewName")));
			}
		}
	}

	private boolean isJobRunning(String jobName) {
		return !jobExplorer.findRunningJobExecutions(jobName).isEmpty();
	}

}
