package be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities;

import org.apache.jena.rdf.model.Model;

public record DcatDataset(String id, Model model) {
}
