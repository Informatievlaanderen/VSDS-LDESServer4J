package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import org.apache.jena.rdf.model.Model;

public interface LdesFragmentConverter {

    Model toModel(final LdesFragment ldesFragment);
}
