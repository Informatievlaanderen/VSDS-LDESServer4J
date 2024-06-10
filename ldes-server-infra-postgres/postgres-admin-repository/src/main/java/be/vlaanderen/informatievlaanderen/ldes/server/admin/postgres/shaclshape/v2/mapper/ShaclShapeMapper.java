package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.v2.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.v2.entity.ShaclShapeEntity;

public class ShaclShapeMapper {
    private ShaclShapeMapper() {}

    public static ShaclShape fromEntity(ShaclShapeEntity entity) {
        return new ShaclShape(entity.getCollectionName(), entity.getModel());
    }
}
