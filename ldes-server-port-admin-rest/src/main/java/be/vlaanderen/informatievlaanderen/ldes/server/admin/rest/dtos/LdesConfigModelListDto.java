package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.dtos;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;

import java.util.List;

public class LdesConfigModelListDto {
	private List<LdesConfigModel> ldesConfigModelList;

	public LdesConfigModelListDto(List<LdesConfigModel> ldesConfigModelList) {
		this.ldesConfigModelList = ldesConfigModelList;
	}

	public List<LdesConfigModel> getLdesConfigModelList() {
		return ldesConfigModelList;
	}
}
