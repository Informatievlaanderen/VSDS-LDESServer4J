package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper.BucketMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageEntity;

public class PageMapper {
	private PageMapper() {}

//	public static PageEntity toEntity(Page page) {
//		return new PageEntity(
//				page.pageId(),
//
//		)
//	}

	public static Page fromEntity(PageEntity entity) {
		return Page.createPageWithPartialUrl(
				entity.getId(),
				BucketMapper.fromEntity(entity.getBucket()),
				entity.getExpiration(),
				entity.getPartialUrl()
		);
	}
}
