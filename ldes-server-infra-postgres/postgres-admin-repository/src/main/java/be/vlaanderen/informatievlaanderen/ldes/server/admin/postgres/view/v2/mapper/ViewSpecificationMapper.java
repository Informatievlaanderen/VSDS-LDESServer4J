package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.v2.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.PostgresAdminConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity.FragmentationConfigEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.v2.entity.ViewEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;

import java.util.List;

public class ViewSpecificationMapper {
    private ViewSpecificationMapper() {
    }

    public static ViewEntity toEntity(ViewSpecification viewSpecification) {
        final List<String> retentionStrings = viewSpecification.getRetentionConfigs().stream()
                .map(retentionModel -> RDFWriter.source(retentionModel).lang(PostgresAdminConstants.SERIALISATION_LANG).asString())
                .toList();
        return new ViewEntity(
                viewSpecification.getName().getViewName(),
                viewSpecification.getFragmentations().stream().map(FragmentationConfigEntity::toEntity).toList(),
                retentionStrings,
                viewSpecification.getPageSize()
        );
    }

    public static ViewSpecification fromEntity(ViewEntity viewEntity) {
        final List<Model> retentionPolicies = viewEntity.getRetentionPolicies().stream()
                .map(retentionString -> RDFParser.fromString(retentionString).lang(PostgresAdminConstants.SERIALISATION_LANG).toModel())
                .toList();
        return new ViewSpecification(
                ViewName.fromString(viewEntity.getComposedViewName()),
                retentionPolicies,
                viewEntity.getFragmentations().stream().map(FragmentationConfigEntity::fromEntity).toList(),
                viewEntity.getPageSize()
        );
    }
}
