package be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories;

import java.util.List;

public interface MemberRepository {
    /**
     * Update the isFragmented flag for a list of members
     * @param isFragmented true or false, the value that should be set
     * @param memberIds list of member ids that should be updated
     */
    void updateIsFragmented(boolean isFragmented, List<Long> memberIds);
}
