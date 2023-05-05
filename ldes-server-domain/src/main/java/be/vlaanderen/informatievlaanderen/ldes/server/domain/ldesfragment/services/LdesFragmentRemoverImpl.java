package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.springframework.stereotype.Service;

@Service
public class LdesFragmentRemoverImpl implements LdesFragmentRemover {
	private final LdesFragmentRepository ldesFragmentRepository;

	public LdesFragmentRemoverImpl(LdesFragmentRepository ldesFragmentRepository) {
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	@Override
	public void removeLdesFragmentsOfView(ViewName viewName) {
		ldesFragmentRepository.removeLdesFragmentsOfView(viewName.asString());
	}
}
