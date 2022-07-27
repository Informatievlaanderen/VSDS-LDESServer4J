package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentview.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.LdesFragmentConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentview.entities.LdesFragmentView;
import org.springframework.stereotype.Component;

import static org.apache.jena.riot.RDFFormat.JSONLD11;
import static org.apache.jena.riot.RDFFormat.NQUADS;

@Component
public class LdesFragmentViewConverter {

    private final LdesFragmentConverter ldesFragmentConverter;

    public LdesFragmentViewConverter(LdesFragmentConverter ldesFragmentConverter) {
        this.ldesFragmentConverter = ldesFragmentConverter;
    }


    public LdesFragmentView convertToLdesFragmentView(LdesFragment ldesFragment){
        String s = RdfModelConverter.toString(ldesFragmentConverter.toModel(ldesFragment), NQUADS);
        return new LdesFragmentView(ldesFragment.getFragmentId(),s , true);
    }
}
