package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.config.EndpointConfiguration;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entity.LdesFragmentEntity;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LdesFragmentCreator {

    private final EndpointConfiguration endpointConfiguration;

    public JSONObject createLdesFragmentPage(Page<LdesFragmentEntity> pageable) {
        JSONObject ldesFragment = new JSONObject();
        addPointersToLDesFragment(pageable, ldesFragment);
        addItemsToLdesFragment(pageable, ldesFragment);
        return ldesFragment;
    }

    private void addItemsToLdesFragment(Page<LdesFragmentEntity> pageable, JSONObject ldesFragment) {
        List<JSONObject> items = pageable.getContent().stream().map(LdesFragmentEntity::getLdesFragment).collect(Collectors.toList());
        ldesFragment.put("items", items);
    }

    private void addPointersToLDesFragment(Page<LdesFragmentEntity> pageable, JSONObject ldesFragment) {
        addPointerToPage(pageable.getNumber(), pageable.nextOrLastPageable(), ldesFragment, "next");
        addPointerToPage(pageable.getNumber(), pageable.previousOrFirstPageable(), ldesFragment, "previous");
    }

    private void addPointerToPage(int currentPageNumber, Pageable referralPage, JSONObject jsonObject, String keyToPage) {
        int referralPageNumber = referralPage.getPageNumber();
        if (currentPageNumber != referralPageNumber)
            jsonObject.put(keyToPage, updatedLink(referralPageNumber));
    }

    private String updatedLink(int referralPageNumber) {
        return endpointConfiguration.getEndpoint() + "/ldes-fragment?page=PAGE".replace("PAGE", String.valueOf(referralPageNumber));
    }
}
