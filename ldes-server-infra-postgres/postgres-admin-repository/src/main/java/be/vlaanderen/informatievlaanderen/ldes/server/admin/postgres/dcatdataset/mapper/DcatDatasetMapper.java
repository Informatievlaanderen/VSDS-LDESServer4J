package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.entity.DcatDatasetEntity;

public class DcatDatasetMapper {
    private DcatDatasetMapper() {
    }

    public static DcatDataset fromEntity(DcatDatasetEntity entity) {
        return new DcatDataset(entity.getCollectionName(), entity.getModel());
    }
}
