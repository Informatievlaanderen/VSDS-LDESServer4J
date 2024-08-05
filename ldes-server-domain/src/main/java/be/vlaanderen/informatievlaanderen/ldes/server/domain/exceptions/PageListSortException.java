package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

import java.util.List;

public class PageListSortException extends RuntimeException {
    private final List<String> ids;

    public PageListSortException(List<String> pageIds) {
        super();
        this.ids = pageIds;
    }

    @Override
    public String getMessage() {
        StringBuilder msg = new StringBuilder("Could not find last page out of list. Page ids: ");
        ids.forEach(id -> msg.append(id).append(", "));
        msg.delete(msg.length() - 2, msg.length() - 1);
        return msg.toString();
    }

}
