package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentSequence;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PaginationSequenceEntity;
import org.springframework.stereotype.Component;

@Component
public class PaginationSequenceEntityMapper {

	public FragmentSequence toFragmentSequence(PaginationSequenceEntity entity) {
		final ViewName viewName = ViewName.fromString(entity.getViewName());
		return new FragmentSequence(viewName, entity.getLastProcessedSequence());
	}

	public PaginationSequenceEntity toEntity(FragmentSequence fragmentSequence) {
		String viewName = fragmentSequence.viewName().asString();
		return new PaginationSequenceEntity(viewName, fragmentSequence.sequenceNr());
	}

}
