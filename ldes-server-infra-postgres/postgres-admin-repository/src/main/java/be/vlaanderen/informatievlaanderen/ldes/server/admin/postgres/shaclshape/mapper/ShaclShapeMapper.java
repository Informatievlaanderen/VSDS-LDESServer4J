package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.entity.ShaclShapeEntity;

public class ShaclShapeMapper {
    private ShaclShapeMapper() {}

    public static ShaclShape fromEntity(ShaclShapeEntity entity) {
        return new ShaclShape(entity.getCollectionName(), entity.getModel());
    }
}
