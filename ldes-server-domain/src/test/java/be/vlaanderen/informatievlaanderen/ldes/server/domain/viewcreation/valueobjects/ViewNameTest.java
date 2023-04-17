package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ViewNameTest {

	@Test
	void withCollectionName() {
		ViewName base = new ViewName("colA", "viewA");
		ViewName cloneWithOtherCollectionName = base.withCollectionName("colB");
		assertEquals("colB", cloneWithOtherCollectionName.getCollectionName());
		assertEquals("colB/viewA", cloneWithOtherCollectionName.getFullName());
	}

	@Test
	void getFullName() {
		ViewName base = new ViewName("colA", "viewA");
		assertEquals("colA/viewA", base.getFullName());
	}

	@Test
	void getCollectionName() {
		ViewName base = new ViewName("colA", "viewA");
		assertEquals("colA", base.getCollectionName());
	}

	@Test
	void testEqualsAndHashCode() {
		String colA = "colA";
		String nameA = "nameA";

		ViewName viewNameA = new ViewName(colA, nameA);
		assertEquals(viewNameA, viewNameA);
		assertEquals(new ViewName(colA, nameA), viewNameA);
		assertNotEquals(new ViewName("other", nameA), viewNameA);
		assertNotEquals(new ViewName(colA, "other"), viewNameA);
		assertNotEquals(new ViewName("other", "other"), viewNameA);
		assertNotEquals(viewNameA, null);

		assertEquals(new ViewName(colA, nameA).hashCode(), viewNameA.hashCode());
		assertNotEquals(new ViewName("other", nameA).hashCode(), new ViewName(colA, nameA).hashCode());
		assertNotEquals(new ViewName(colA, "other").hashCode(), new ViewName(colA, nameA).hashCode());
		assertNotEquals(new ViewName("other", "other").hashCode(), new ViewName(colA, nameA).hashCode());
	}
}