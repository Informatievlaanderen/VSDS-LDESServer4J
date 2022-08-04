package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import org.apache.jena.rdf.model.Model;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragment;

public interface LdesFragmentConverter {

    Model toModel(final LdesFragment ldesFragment);
}
