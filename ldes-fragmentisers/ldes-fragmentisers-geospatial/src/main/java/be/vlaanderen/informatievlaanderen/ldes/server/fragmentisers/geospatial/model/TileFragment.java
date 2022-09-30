package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;

public class TileFragment {
	private final LdesFragment ldesFragment;
	private final boolean created;

	public TileFragment(LdesFragment ldesFragment, boolean created) {
		this.ldesFragment = ldesFragment;
		this.created = created;
	}

	public LdesFragment getLdesFragment() {
		return ldesFragment;
	}

	public boolean isCreated() {
		return created;
	}
}
