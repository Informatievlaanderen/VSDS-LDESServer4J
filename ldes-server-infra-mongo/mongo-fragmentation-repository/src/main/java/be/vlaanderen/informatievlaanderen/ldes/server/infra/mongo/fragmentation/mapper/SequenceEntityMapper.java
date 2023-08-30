package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentSequence;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.SequenceEntity;
import org.springframework.stereotype.Component;

@Component
public class SequenceEntityMapper {

	public FragmentSequence toFragmentSequence(SequenceEntity entity) {
		final ViewName viewName = ViewName.fromString(entity.getViewName());
		return new FragmentSequence(viewName, entity.getLastProcessedSequence());
	}

	public SequenceEntity toEntity(FragmentSequence fragmentSequence) {
		String viewName = fragmentSequence.viewName().asString();
		return new SequenceEntity(viewName, fragmentSequence.sequenceNr());
	}

}
