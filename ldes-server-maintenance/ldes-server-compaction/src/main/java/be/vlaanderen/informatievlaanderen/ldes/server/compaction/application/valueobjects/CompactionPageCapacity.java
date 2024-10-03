package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.valueobjects;

public class CompactionPageCapacity {
	private final int maxCapacity;
	private int currentCapacity;

	public CompactionPageCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public void reset() {
		currentCapacity = 0;
	}

	public boolean exceedsMaxCapacity() {
		return currentCapacity > maxCapacity;
	}

	public void increase(int delta) {
		currentCapacity += delta;
	}
}
