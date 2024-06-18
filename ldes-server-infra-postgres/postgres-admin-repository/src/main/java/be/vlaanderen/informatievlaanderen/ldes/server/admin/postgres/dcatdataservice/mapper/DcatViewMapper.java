package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.entity.DcatDataServiceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;

public class DcatViewMapper {
    private DcatViewMapper() {
    }

    public static DcatView fromEntity(DcatDataServiceEntity entity) {
        return DcatView.from(
                ViewName.fromString(entity.getViewName()),
                entity.getModel()
        );
    }
}
