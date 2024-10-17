package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions;

import org.springframework.batch.core.JobExecutionException;

public class FragmentationJobException extends RuntimeException {
	public FragmentationJobException(JobExecutionException e) {
		super(e);
	}
}
