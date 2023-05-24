package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.exception.MissingViewDcatException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.apache.jena.rdf.model.Model;
import org.springframework.stereotype.Service;

// TODO: 24/05/2023 test
@Service
public class DcatViewServiceImpl implements DcatViewService {

    private final DcatViewRepository dcatViewRepository;

    public DcatViewServiceImpl(DcatViewRepository dcatViewRepository) {
        this.dcatViewRepository = dcatViewRepository;
    }

    @Override
    public void create(ViewName viewName, Model dcat) {
        dcatViewRepository.create(DcatView.from(viewName, dcat));
    }

    @Override
    public void update(ViewName viewName, Model dcat) {
        if (dcatViewRepository.findByViewName(viewName).isEmpty()) {
            throw new MissingViewDcatException();
        }

        dcatViewRepository.update(DcatView.from(viewName, dcat));
    }

    @Override
    public void remove(ViewName viewName) {
        dcatViewRepository.remove(viewName);
    }

}
