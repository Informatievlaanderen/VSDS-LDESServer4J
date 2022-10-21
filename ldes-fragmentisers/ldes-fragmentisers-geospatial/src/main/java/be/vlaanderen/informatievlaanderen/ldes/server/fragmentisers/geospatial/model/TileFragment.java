package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;

public record TileFragment(LdesFragment ldesFragment, boolean created) {}
