package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RetentionModelSerializer {
    private final RdfModelConverter rdfModelConverter;

    public RetentionModelSerializer(RdfModelConverter rdfModelConverter) {
        this.rdfModelConverter = rdfModelConverter;
    }

    public List<String> serialize(List<Model> models) {
        return models
                .stream()
                .map(retentionModel -> RdfModelConverter.toString(retentionModel, Lang.NQUADS))
                .toList();
    }

    public List<Model> deSerialize(List<String> retentionPolicies) {
        return retentionPolicies
                .stream()
                .map(retentionModel -> rdfModelConverter.fromString(retentionModel, Lang.NQUADS))
                .toList();

    }
}
