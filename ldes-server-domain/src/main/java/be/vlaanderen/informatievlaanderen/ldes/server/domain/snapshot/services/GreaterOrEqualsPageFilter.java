package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;

import java.util.function.Predicate;

public class GreaterOrEqualsPageFilter implements Predicate<LdesFragment> {
    private final String page;

    public GreaterOrEqualsPageFilter(String lastFragment) {
        page = lastFragment.split("\\?")[1].split("=")[1];
    }

    @Override
    public boolean test(LdesFragment ldesFragment) {
        return Integer.parseInt(ldesFragment.getValueOfKey("pageNumber").get())>=Integer.parseInt(page);
    }
}
