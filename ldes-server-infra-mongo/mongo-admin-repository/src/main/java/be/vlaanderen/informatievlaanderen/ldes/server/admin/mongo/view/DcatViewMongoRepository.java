package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.DataServiceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.repository.DataServiceEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.service.DcatServiceEntityConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

public class DcatViewMongoRepository implements DcatViewRepository {

    private final DataServiceEntityRepository dataServiceEntityRepository;
    private final DcatServiceEntityConverter dcatServiceEntityConverter;
    private final MongoTemplate mongoTemplate;


    public DcatViewMongoRepository(DataServiceEntityRepository dataServiceEntityRepository,
                                   DcatServiceEntityConverter dcatServiceEntityConverter, MongoTemplate mongoTemplate) {
        this.dataServiceEntityRepository = dataServiceEntityRepository;
        this.dcatServiceEntityConverter = dcatServiceEntityConverter;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(DcatView dcatView) {
        DataServiceEntity dataServiceEntity = dcatServiceEntityConverter.fromDcatView(dcatView);
        dataServiceEntityRepository.save(dataServiceEntity);
    }

    @Override
    public Optional<DcatView> findByViewName(ViewName viewName) {
        return dataServiceEntityRepository.findById(viewName.asString())
                .map(dcatServiceEntityConverter::toDcatView);
    }

    @Override
    public void delete(ViewName viewName) {
        dataServiceEntityRepository.deleteById(viewName.asString());
    }

    @Override
    public void deleteByCollectionName(String collectionName) {
        final Query query = new Query();
        query.addCriteria(Criteria.where("_id").regex("^" + collectionName + "/"));

        mongoTemplate.remove(query, DataServiceEntity.class);
    }

    @Override
    public List<DcatView> findAll() {
        return dataServiceEntityRepository.findAll().stream().map(dcatServiceEntityConverter::toDcatView).toList();
    }

}
