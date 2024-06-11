package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity.FragmentationConfigEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity.ViewEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;

public class ViewSpecificationMapper {
    private ViewSpecificationMapper() {
    }

    public static ViewEntity toEntity(ViewSpecification viewSpecification) {
        return new ViewEntity(
                viewSpecification.getName().getViewName(),
                viewSpecification.getFragmentations().stream().map(FragmentationConfigEntity::toEntity).toList(),
                viewSpecification.getRetentionConfigs(),
                viewSpecification.getPageSize()
        );
    }

    public static ViewSpecification fromEntity(ViewEntity viewEntity) {
        return new ViewSpecification(
                ViewName.fromString(viewEntity.getComposedViewName()),
                viewEntity.getRetentionPolicies(),
                viewEntity.getFragmentations().stream().map(FragmentationConfigEntity::fromEntity).toList(),
                viewEntity.getPageSize()
        );
    }
}
