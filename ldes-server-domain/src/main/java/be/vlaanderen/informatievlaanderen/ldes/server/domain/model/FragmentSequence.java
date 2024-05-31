package be.vlaanderen.informatievlaanderen.ldes.server.domain.model;

public record FragmentSequence(ViewName viewName, long sequenceNr) {

    /**
     * Creates a sequence with a negative number. The ingest module starts sequencing from 0.
     * This negative sequenceNr indicates that this view has never been processed for fragmentation.
     */
    public static FragmentSequence createNeverProcessedSequence(ViewName viewName) {
        return new FragmentSequence(viewName, -1);
    }

}
