package be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;

@Component
public class MemberAllocationBatchWriter implements ItemWriter<List<MemberAllocation>> {
	private final DataSource dataSource;
	private final ItemWriter<MemberAllocation> writer;

	public MemberAllocationBatchWriter(DataSource dataSource) {
		this.writer = batchWriter();
		this.dataSource = dataSource;
	}

	@Override
	public void write(Chunk<? extends List<MemberAllocation>> chunk) throws Exception {
		Chunk<? extends MemberAllocation> memberAllocations = new Chunk(chunk.getItems()
				.stream()
				.flatMap(List::stream)
				.toList());

		writer.write(memberAllocations);
	}

	private ItemWriter<MemberAllocation> batchWriter() {
		return new ItemWriter<MemberAllocation>() {
			private final String SQL = "insert into fetch_allocation (id, collection_name, fragment_id, member_id, view_name) " +
			                                  "values (?, ?, ?, ?, ?)";
			@Override
			public void write(Chunk<? extends MemberAllocation> chunk) throws Exception {
				PreparedStatement ps = dataSource.getConnection().prepareStatement(SQL);
				for (MemberAllocation allocation : chunk) {
					// Set the variables
					ps.setString(1, allocation.getId());
					ps.setString(2, allocation.getCollectionName());
					ps.setString(3, allocation.getFragmentId());
					ps.setString(4, allocation.getMemberId());
					ps.setString(5, allocation.getViewName());
					// Add it to the batch
					ps.addBatch();

				}
				ps.executeBatch();
			}
		};
	}
}
