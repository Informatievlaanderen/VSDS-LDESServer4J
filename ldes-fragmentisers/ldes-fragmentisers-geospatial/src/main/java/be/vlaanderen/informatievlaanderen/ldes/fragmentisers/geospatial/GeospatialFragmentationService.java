package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;

public class GeospatialFragmentationService implements FragmentationService {
	
	private final LdesConfig ldesConfig;
	private final LdesMemberRepository ldesMemberRepository;
	private final LdesFragmentRepository ldesFragmentRepository;
	private final FragmentCreator fragmentCreator;

	public GeospatialFragmentationService(LdesConfig ldesConfig, LdesMemberRepository ldesMemberRepository, LdesFragmentRepository ldesFragmentRepository, FragmentCreator fragmentCreator) {
		this.ldesConfig = ldesConfig;
		this.ldesMemberRepository = ldesMemberRepository;
		this.ldesFragmentRepository = ldesFragmentRepository;
		this.fragmentCreator = fragmentCreator;
	}

	@Override
	public void addMemberToFragment(String ldesMemberId) {
		// TODO Auto-generated method stub
		
	}
}
