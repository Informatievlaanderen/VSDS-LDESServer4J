package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial;

import java.util.Optional;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.repository.LdesMemberRepository;

public class GeospatialFragmentCreator implements FragmentCreator {

	private LdesConfig ldesConfig;
	private GeospatialConfig geospatialConfig;
	private final LdesMemberRepository ldesMemberRepository;
	private final LdesFragmentRepository ldesFragmentRepository;

	public GeospatialFragmentCreator(final LdesConfig ldesConfig, final GeospatialConfig geospatialConfig, final LdesMemberRepository ldesMemberRepository, final LdesFragmentRepository ldesFragmentRepository) {
		this.ldesConfig = ldesConfig;
		this.geospatialConfig = geospatialConfig;
		this.ldesMemberRepository = ldesMemberRepository;
		this.ldesFragmentRepository = ldesFragmentRepository;
	}

	/*
	 * DATA
	 * -> MEMBERS
	 * 		-> CONTROLLER
	 * 			-> FRAGMENTATION SERVICE addMember
	 * 
	 * 
	 */

	@Override
	public LdesFragment createNewFragment(Optional<LdesFragment> optionalExistingLdesFragment, LdesMember firstMember) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean needsToCreateNewFragment(LdesFragment fragment) {
		// TODO Auto-generated method stub
		return false;
	}
}
