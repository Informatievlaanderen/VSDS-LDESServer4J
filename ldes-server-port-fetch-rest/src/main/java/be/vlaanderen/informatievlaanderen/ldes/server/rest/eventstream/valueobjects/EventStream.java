package be.vlaanderen.informatievlaanderen.ldes.server.rest.eventstream.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.entities.TreeNode;

import java.util.List;
import java.util.Objects;

/**
 * @param timestampPath The ldes:EventStream instance MAY have a property ldes:timestampPath
 * @param versionOfPath The ldes:EventStream instance MAY have a property ldes:versionOfPath
 * @param shape         The ldes:EventStream instance SHOULD have a property tree:shape
 */
public record EventStream(String collection, String timestampPath, String versionOfPath, String shape,
						  List<TreeNode> views) {

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || obj.getClass() != this.getClass())
			return false;
		var that = (EventStream) obj;
		return Objects.equals(this.collection, that.collection) &&
				Objects.equals(this.timestampPath, that.timestampPath) &&
				Objects.equals(this.versionOfPath, that.versionOfPath) &&
				Objects.equals(this.shape, that.shape) &&
				Objects.equals(this.views, that.views);
	}

}
