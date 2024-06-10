package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.v2.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.v2.entity.DcatDatasetEntity;

public class DcatDatasetMapper {
    private DcatDatasetMapper() {
    }

    public static DcatDataset fromEntity(DcatDatasetEntity entity) {
        return new DcatDataset(entity.getCollectionName(), entity.getModel());
    }
}
