package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.apache.jena.rdf.model.Model;

import static org.apache.commons.lang3.Validate.notNull;

public class DcatView {

    private final ViewName viewName;
    private final Model dcat;

    private DcatView(ViewName viewName, Model dcat) {
        this.viewName = viewName;
        this.dcat = dcat;
    }

    public static DcatView from(ViewName viewName, Model dcat) {
        return new DcatView(notNull(viewName), dcat);
    }

    // TODO TVB: 24/05/2023 eq and hash

}
