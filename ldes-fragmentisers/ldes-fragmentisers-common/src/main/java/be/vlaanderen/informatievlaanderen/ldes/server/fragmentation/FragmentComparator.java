package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;

import java.util.Comparator;

public class FragmentComparator implements Comparator<Fragment> {
	@Override
	public int compare(Fragment f1, Fragment f2) {
		return f1.isConnectedTo(f2) ? -1 : f2.isConnectedTo(f1) ? 1 : 0;
	}
}
