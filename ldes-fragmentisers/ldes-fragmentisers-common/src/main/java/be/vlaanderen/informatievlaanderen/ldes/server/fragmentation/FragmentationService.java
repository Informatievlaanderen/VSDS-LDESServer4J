package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamClosedEvent;
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
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@EnableScheduling
public class FragmentationService {
	public static final int POLLING_RATE = 1500;
	public static final String LDES_SERVER_CREATE_FRAGMENTS_COUNT = "ldes_server_create_fragments_count";
	private final String BUCKETISATION_JOB = "bucketisation";
	private final String REBUCKETISATION_JOB = "rebucketisation";
	private final JobLauncher jobLauncher;
	private final JobExplorer jobExplorer;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final ItemReader<IngestedMember> newMemberReader;
	private final ItemReader<IngestedMember> rebucketiseMemberReader;
	private final BucketProcessor processor;
	private final ItemWriter<List<BucketisedMember>> bucketWriter;
	private final ApplicationEventPublisher eventPublisher;
	private AtomicBoolean shouldTriggerBucketisation = new AtomicBoolean(false);

	private final FragmentRepository fragmentRepository;

	public FragmentationService(JobLauncher jobLauncher, JobExplorer jobExplorer, JobRepository jobRepository,
	                            PlatformTransactionManager transactionManager,
	                            @Qualifier("newMemberReader") ItemReader<IngestedMember> newMemberReader,
	                            @Qualifier("refragmentEventStream") ItemReader<IngestedMember> rebucketiseMemberReader,
	                            BucketProcessor processor,
	                            ItemWriter<List<BucketisedMember>> bucketWriter, ApplicationEventPublisher eventPublisher,
	                            FragmentRepository fragmentRepository) {
		this.jobLauncher = jobLauncher;
		this.jobExplorer = jobExplorer;
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.newMemberReader = newMemberReader;
		this.rebucketiseMemberReader = rebucketiseMemberReader;
		this.processor = processor;
		this.bucketWriter = bucketWriter;
		this.eventPublisher = eventPublisher;
		this.fragmentRepository = fragmentRepository;
	}

	@EventListener
	public void executeFragmentation(MembersIngestedEvent event) {
		shouldTriggerBucketisation.set(true);
	}

	@EventListener
	public void handleViewInitializationEvent(ViewNeedsRebucketisationEvent event) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
		launchJob(rebucketiseJob(), new JobParametersBuilder()
				.addString("viewName", event.viewName().asString())
				.addLocalDateTime("triggered", LocalDateTime.now())
				.toJobParameters());
	}

	@Scheduled(fixedRate = POLLING_RATE)
	public void scheduledJobLauncher() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
		if (shouldTriggerBucketisation.get() && !isJobRunning(BUCKETISATION_JOB) && !isJobRunning(REBUCKETISATION_JOB)) {
			shouldTriggerBucketisation.set(false);
			launchJob(bucketiseJob(), new JobParameters());
		}
	}

	@EventListener
	public void markFragmentsImmutableInCollection(EventStreamClosedEvent event) {
		fragmentRepository.markFragmentsImmutableInCollection(event.collectionName());
	}

	private void launchJob(Job job, JobParameters jobParameters) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
		jobLauncher.run(job, jobParameters);
		if (job.getName().equals(BUCKETISATION_JOB)) {
			eventPublisher.publishEvent(new MembersBucketisedEvent());
		} else if (job.getName().equals(REBUCKETISATION_JOB)) {
			eventPublisher.publishEvent(new NewViewBucketisedEvent(jobParameters.getString("viewName")));
		}

	}

	private boolean isJobRunning(String jobName) {
		return !jobExplorer.findRunningJobExecutions(jobName).isEmpty();
	}

	private Job bucketiseJob() {
		return new JobBuilder(BUCKETISATION_JOB, jobRepository)
				.start(bucketiseMembers())
				.build();
	}

	private Job rebucketiseJob() {
		return new JobBuilder(REBUCKETISATION_JOB, jobRepository)
				.start(rebucketiseMembers())
				.build();
	}

	private Step bucketiseMembers() {
		return new StepBuilder("bucketiseMembers", jobRepository)
				.<IngestedMember, List<BucketisedMember>>chunk(150, transactionManager)
				.reader(newMemberReader)
				.processor(processor)
				.writer(bucketWriter)
				.allowStartIfComplete(true)
				.build();
	}

	private Step rebucketiseMembers() {
		return new StepBuilder("rebucketiseMembers", jobRepository)
				.<IngestedMember, List<BucketisedMember>>chunk(150, transactionManager)
				.reader(rebucketiseMemberReader)
				.processor(processor)
				.writer(bucketWriter)
				.allowStartIfComplete(true)
				.build();
	}

}
