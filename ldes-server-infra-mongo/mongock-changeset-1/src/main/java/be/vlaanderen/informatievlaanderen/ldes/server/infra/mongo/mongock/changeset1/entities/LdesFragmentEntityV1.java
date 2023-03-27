package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset1.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset1.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset1.valueobjects.TreeRelation;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("ldesfragment")
@CompoundIndex(name = "index_view_fragmentPairs", def = "{'viewName' : 1, 'fragmentPairs': 1}")
public class LdesFragmentEntityV1 {
	@Id
	private final String id;
	@Indexed
	private final Boolean root;
	@Indexed
	private final String viewName;
	@Indexed
	private final List<FragmentPair> fragmentPairs;
	@Indexed
	private final Boolean immutable;
	private final List<TreeRelation> relations;

	private final List<String> members;

	public LdesFragmentEntityV1(String id, Boolean root, String viewName,
			List<FragmentPair> fragmentPairs,
			Boolean immutable,
			List<TreeRelation> relations, List<String> members) {
		this.id = id;
		this.root = root;
		this.viewName = viewName;
		this.fragmentPairs = fragmentPairs;
		this.immutable = immutable;
		this.relations = relations;
		this.members = members;
	}

	public String getId() {
		return id;
	}

	public Boolean isImmutable() {
		return immutable;
	}

	public List<String> getMembers() {
		return members;
	}

	public List<TreeRelation> getRelations() {
		return relations;
	}

	public Boolean getRoot() {
		return root;
	}

	public String getViewName() {
		return viewName;
	}

	public List<FragmentPair> getFragmentPairs() {
		return fragmentPairs;
	}

}
