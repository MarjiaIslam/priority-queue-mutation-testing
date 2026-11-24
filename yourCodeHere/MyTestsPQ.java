//NOTE: You may NOT use reflection in this project (nothing from java.lang.reflect)

import org.junit.*;
import static org.junit.Assert.*;
//^ This is something good to learn, btw.
//It's a "static import" meaning you can
//use any static methods (and variables)
//from this class as if they were part of
//the current file instead. This allows
//you to call assertEquals() as if it was
//a method in your test class below.

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Comparator;
import java.util.TreeSet; //this is a SortedSet
import java.util.ArrayList;
import java.util.Collection;

/**
 *  Test suite for the Java PriorityQueue class.
 *  @author MarjiaIslam
 */
public class MyTestsPQ {

	public MyTestsPQ() {
	}


	private static final Comparator<Integer> DESC = new Comparator<Integer>() {
		public int compare(Integer a, Integer b) {
			return b.compareTo(a);
		}
	};

	private static final Comparator<String> BY_LENGTH = new Comparator<String>() {
		public int compare(String a, String b) {
			return a.length() - b.length();
		}
	};

	private static class Item implements Comparable<Item> {
		private final int key;
		private final String tag;

		public Item(int key, String tag) {
			this.key = key;
			this.tag = tag;
		}

		public int compareTo(Item other) {
			if (this.key < other.key) {
				return -1;
			}
			if (this.key > other.key) {
				return 1;
			}
			return 0;
		}

		public boolean equals(Object o) {
			if (!(o instanceof Item)) {
				return false;
			}
			Item other = (Item) o;
			return this.key == other.key && this.tag.equals(other.tag);
		}

		public int hashCode() {
			return key;
		}

		public String toString() {
			return key + tag;
		}
	}

	private static String level(PriorityQueue<?> pq) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < pq.size; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(pq.queue[i]);
		}
		return sb.append("]").toString();
	}

	private static String join(Object[] items) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < items.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(items[i]);
		}
		return sb.append("]").toString();
	}

	private static String iterated(PriorityQueue<?> pq) {
		StringBuilder sb = new StringBuilder("[");
		Iterator<?> it = pq.iterator();
		boolean first = true;
		while (it.hasNext()) {
			if (!first) {
				sb.append(", ");
			}
			sb.append(it.next());
			first = false;
		}
		return sb.append("]").toString();
	}

	private static String polled(PriorityQueue<?> pq) {
		StringBuilder sb = new StringBuilder("[");
		int n = pq.size();
		for (int i = 0; i < n; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(pq.poll());
		}
		return sb.append("]").toString();
	}

	private static ArrayList<Integer> listOf(Integer... values) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (Integer v : values) {
			list.add(v);
		}
		return list;
	}

	private static PriorityQueue<Integer> build(int... values) {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		for (int v : values) {
			pq.add(v);
		}
		return pq;
	}

	private static PriorityQueue<Item> buildItems(Item... items) {
		PriorityQueue<Item> pq = new PriorityQueue<Item>();
		for (Item i : items) {
			pq.add(i);
		}
		return pq;
	}

	@Test(timeout=2000)
	public void testDefaultConstructor() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		assertEquals("A new queue should have the default capacity (11).", 11, pq.queue.length);
		assertEquals("A new queue should be empty.", 0, pq.size);
		assertEquals("A new queue should report size 0.", 0, pq.size());
		assertEquals("A new queue has not been modified yet.", 0, pq.modCount);
		assertEquals("A queue built with no comparator uses natural ordering.", null, pq.comparator());
		assertEquals("Peeking at an empty queue gives null.", null, pq.peek());
	}

	@Test(timeout=2000)
	public void testDefaultInitialCapacityConstant() {
		assertEquals("The default initial capacity should be 11.",
			11, PriorityQueue.DEFAULT_INITIAL_CAPACITY);
	}

	@Test(timeout=2000)
	public void testCapacityConstructor() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(5);
		assertEquals("The queue should use the requested capacity.", 5, pq.queue.length);
		assertEquals("A new queue is empty.", 0, pq.size);
		assertEquals("A new queue has no comparator.", null, pq.comparator());
		assertEquals("A new queue has not been modified.", 0, pq.modCount);
	}

	@Test(timeout=2000)
	public void testCapacityConstructorOneIsLegal() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(1);
		assertEquals("Capacity 1 is legal and should be used as-is.", 1, pq.queue.length);
	}

	@Test(timeout=2000, expected=IllegalArgumentException.class)
	public void testCapacityConstructorZeroThrows() {
		new PriorityQueue<Integer>(0);
	}

	@Test(timeout=2000, expected=IllegalArgumentException.class)
	public void testCapacityConstructorNegativeThrows() {
		new PriorityQueue<Integer>(-5);
	}

	@Test(timeout=2000)
	public void testComparatorConstructor() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(DESC);
		assertEquals("A comparator queue still gets the default capacity (11).", 11, pq.queue.length);
		assertEquals("The comparator given should be the comparator stored.", DESC, pq.comparator());
		assertEquals("A new queue is empty.", 0, pq.size);
		assertEquals("A new queue has not been modified.", 0, pq.modCount);
	}

	@Test(timeout=2000)
	public void testComparatorConstructorOrders() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(DESC);
		pq.add(1);
		pq.add(3);
		pq.add(2);
		assertEquals("With a descending comparator the largest item is the head.",
			(Integer) 3, pq.peek());
	}

	@Test(timeout=2000)
	public void testCapacityAndComparatorConstructor() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(3, DESC);
		assertEquals("The requested capacity should be used.", 3, pq.queue.length);
		assertEquals("The comparator given should be the comparator stored.", DESC, pq.comparator());
	}

	@Test(timeout=2000)
	public void testCapacityAndNullComparatorConstructor() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(4, null);
		assertEquals("The requested capacity should be used.", 4, pq.queue.length);
		assertEquals("A null comparator means natural ordering.", null, pq.comparator());
		pq.add(2);
		pq.add(1);
		assertEquals("Natural ordering should put the smallest item first.", (Integer) 1, pq.peek());
	}

	@Test(timeout=2000)
	public void testCapacityOneWithComparator() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(1, DESC);
		assertEquals("Capacity 1 is legal.", 1, pq.queue.length);
		assertEquals("The comparator should be stored.", DESC, pq.comparator());
	}

	@Test(timeout=2000, expected=IllegalArgumentException.class)
	public void testCapacityZeroWithComparatorThrows() {
		new PriorityQueue<Integer>(0, DESC);
	}

	@Test(timeout=2000)
	public void testCollectionConstructorHeapifies() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(listOf(9, 8, 7, 6, 5, 4, 3));
		assertEquals("A plain collection should be heapified (Floyd's algorithm).",
			"[3, 5, 4, 6, 8, 9, 7]", level(pq));
		assertEquals("All 7 items should be in the queue.", 7, pq.size);
		assertEquals("The array should be exactly as big as the collection.", 7, pq.queue.length);
		assertEquals("A plain collection gives natural ordering.", null, pq.comparator());
		assertEquals("Heapifying 7 items sifts down 3 times.", 3, pq.modCount);
	}

	@Test(timeout=2000)
	public void testCollectionConstructorHeapifyCount() {
		PriorityQueue<Integer> one = new PriorityQueue<Integer>(listOf(1));
		assertEquals("A 1-item collection needs no sift downs.", 0, one.modCount);

		PriorityQueue<Integer> two = new PriorityQueue<Integer>(listOf(2, 1));
		assertEquals("A 2-item collection needs exactly 1 sift down.", 1, two.modCount);
		assertEquals("A 2-item collection should end up ordered.", "[1, 2]", level(two));

		PriorityQueue<Integer> six = new PriorityQueue<Integer>(listOf(6, 5, 4, 3, 2, 1));
		assertEquals("A 6-item collection needs exactly 3 sift downs.", 3, six.modCount);
		assertEquals("A 6-item collection should end up as a heap.", "[1, 2, 4, 3, 5, 6]", level(six));
	}

	@Test(timeout=2000)
	public void testCollectionConstructorSortedInput() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(listOf(1, 2, 3, 4, 5));
		assertEquals("A sorted collection is already a heap and should not move.",
			"[1, 2, 3, 4, 5]", level(pq));
		assertEquals("Heapifying 5 items sifts down 2 times.", 2, pq.modCount);
	}

	@Test(timeout=2000)
	public void testCollectionConstructorEmpty() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(new ArrayList<Integer>());
		assertEquals("An empty collection should still leave room for one element.",
			1, pq.queue.length);
		assertEquals("An empty collection gives an empty queue.", 0, pq.size);
		assertEquals("Peeking at an empty queue gives null.", null, pq.peek());
		assertEquals("Nothing was heapified.", 0, pq.modCount);
	}

	@Test(timeout=2000, expected=IllegalArgumentException.class)
	public void testCollectionConstructorNullElementThrows() {
		new PriorityQueue<Integer>(listOf(1, null, 3));
	}

	@Test(timeout=2000, expected=IllegalArgumentException.class)
	public void testCollectionConstructorNullFirstElementThrows() {
		new PriorityQueue<Integer>(listOf((Integer) null));
	}

	@Test(timeout=2000)
	public void testCollectionConstructorFromSortedSet() {
		TreeSet<Integer> set = new TreeSet<Integer>(DESC);
		set.addAll(listOf(1, 2, 3, 4));
		Collection<Integer> asCollection = set;
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(asCollection);
		assertEquals("A sorted set keeps its own comparator.", DESC, pq.comparator());
		assertEquals("A sorted set is already ordered and should be copied as-is.",
			"[4, 3, 2, 1]", level(pq));
		assertEquals("All 4 items should be present.", 4, pq.size);
		assertEquals("A sorted set should NOT be heapified.", 0, pq.modCount);
	}

	@Test(timeout=2000)
	public void testCollectionConstructorFromNaturalSortedSet() {
		TreeSet<Integer> set = new TreeSet<Integer>(listOf(5, 1, 3));
		Collection<Integer> asCollection = set;
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(asCollection);
		assertEquals("A naturally sorted set has no comparator.", null, pq.comparator());
		assertEquals("The sorted order should be preserved.", "[1, 3, 5]", level(pq));
	}

	@Test(timeout=2000)
	public void testCollectionConstructorFromPriorityQueue() {
		PriorityQueue<Integer> source = new PriorityQueue<Integer>(DESC);
		source.add(1);
		source.add(2);
		source.add(3);
		Collection<Integer> asCollection = source;
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(asCollection);
		assertEquals("A copied queue keeps the source's comparator.", DESC, pq.comparator());
		assertEquals("A copied queue keeps the source's layout.", "[3, 1, 2]", level(pq));
		assertEquals("A copied queue keeps the source's size.", 3, pq.size);
		assertEquals("The copy is trimmed to exactly the source's size.", 3, pq.queue.length);
		assertEquals("Copying does not count as a heapify.", 0, pq.modCount);
	}

	@Test(timeout=2000)
	public void testPriorityQueueCopyConstructor() {
		PriorityQueue<Integer> source = build(5, 3, 8, 1);
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(source);
		assertEquals("The copy should match the source's layout.", level(source), level(pq));
		assertEquals("The copy should have the source's size.", 4, pq.size);
		assertEquals("The copy array is exactly the source's size.", 4, pq.queue.length);
		assertEquals("A natural-order source gives a natural-order copy.", null, pq.comparator());
		assertEquals("Copying does not heapify.", 0, pq.modCount);
	}

	@Test(timeout=2000)
	public void testPriorityQueueCopyConstructorKeepsComparator() {
		PriorityQueue<Integer> source = new PriorityQueue<Integer>(DESC);
		source.add(1);
		source.add(2);
		source.add(3);
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(source);
		assertEquals("The copy must use the source's comparator.", DESC, pq.comparator());
		assertEquals("The copy should match the source's layout.", "[3, 1, 2]", level(pq));
		pq.add(9);
		assertEquals("The copy should keep ordering by the comparator.", (Integer) 9, pq.peek());
	}

	@Test(timeout=2000)
	public void testPriorityQueueCopyConstructorIsIndependent() {
		PriorityQueue<Integer> source = build(5, 3, 8);
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(source);
		source.add(1);
		assertEquals("Adding to the source must not change the copy.", "[3, 5, 8]", level(pq));
		assertEquals("The copy's size must not change.", 3, pq.size);
	}

	@Test(timeout=2000)
	public void testPriorityQueueCopyConstructorEmpty() {
		PriorityQueue<Integer> source = new PriorityQueue<Integer>();
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(source);
		assertEquals("An empty copy still leaves room for one element.", 1, pq.queue.length);
		assertEquals("An empty copy is empty.", 0, pq.size);
		assertEquals("Peeking an empty copy gives null.", null, pq.peek());
	}

	@Test(timeout=2000)
	public void testSortedSetConstructor() {
		TreeSet<Integer> set = new TreeSet<Integer>(DESC);
		set.addAll(listOf(1, 2, 3));
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(set);
		assertEquals("The sorted set's comparator should be kept.", DESC, pq.comparator());
		assertEquals("The sorted set's order should be kept.", "[3, 2, 1]", level(pq));
		assertEquals("All items should be present.", 3, pq.size);
		assertEquals("The array is exactly the set's size.", 3, pq.queue.length);
		assertEquals("A sorted set is not heapified.", 0, pq.modCount);
	}

	@Test(timeout=2000)
	public void testSortedSetConstructorEmpty() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(new TreeSet<Integer>());
		assertEquals("An empty sorted set still leaves room for one element.", 1, pq.queue.length);
		assertEquals("An empty sorted set gives an empty queue.", 0, pq.size);
		assertEquals("Peeking gives null.", null, pq.peek());
		assertEquals("A naturally sorted set has no comparator.", null, pq.comparator());
	}

	@Test(timeout=2000, expected=IllegalArgumentException.class)
	public void testSortedSetConstructorNullElementThrows() {
		TreeSet<Integer> set = new TreeSet<Integer>(new Comparator<Integer>() {
			public int compare(Integer a, Integer b) {
				if (a == null) {
					return b == null ? 0 : -1;
				}
				if (b == null) {
					return 1;
				}
				return a.compareTo(b);
			}
		});
		set.add(1);
		set.add(null);
		new PriorityQueue<Integer>(set);
	}

	@Test(timeout=2000)
	public void testAddReturnsTrue() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		assertEquals("add() always reports true.", true, pq.add(7));
		assertEquals("The item added should be the head.", (Integer) 7, pq.peek());
		assertEquals("One item means size 1.", 1, pq.size);
	}

	@Test(timeout=2000)
	public void testOfferReturnsTrue() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		assertEquals("offer() always reports true.", true, pq.offer(7));
		assertEquals("The item offered should be the head.", (Integer) 7, pq.peek());
		assertEquals("One item means size 1.", 1, pq.size);
	}

	@Test(timeout=2000)
	public void testElement() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		pq.add(1);
		assertEquals("Added an item, should be able to peek at it.", 1, (int) pq.element());
	}

	@Test(timeout=2000, expected=IllegalArgumentException.class)
	public void testAddNullThrows() {
		new PriorityQueue<Integer>().add(null);
	}

	@Test(timeout=2000, expected=IllegalArgumentException.class)
	public void testOfferNullThrows() {
		new PriorityQueue<Integer>().offer(null);
	}

	@Test(timeout=2000)
	public void testAddNullLeavesQueueAlone() {
		PriorityQueue<Integer> pq = build(4, 2);
		int before = pq.modCount;
		try {
			pq.add(null);
			fail("Adding null should have thrown IllegalArgumentException.");
		}
		catch (IllegalArgumentException e) {
		}
		assertEquals("A rejected null must not change the contents.", "[2, 4]", level(pq));
		assertEquals("A rejected null must not change the size.", 2, pq.size);
		assertEquals("A rejected null is not a modification.", before, pq.modCount);
	}

	@Test(timeout=2000)
	public void testAddCountsAsModification() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		assertEquals("Nothing has happened yet.", 0, pq.modCount);
		pq.add(1);
		assertEquals("One add is one modification.", 1, pq.modCount);
		pq.add(2);
		pq.add(3);
		assertEquals("Three adds are three modifications.", 3, pq.modCount);
	}

	@Test(timeout=2000)
	public void testAddIncreasesSizeByOne() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		for (int i = 1; i <= 5; i++) {
			pq.add(i);
			assertEquals("Size should be exactly the number of adds so far.", i, pq.size());
			assertEquals("The size field should agree with size().", i, pq.size);
		}
	}

	@Test(timeout=2000)
	public void testAddSiftsUp() {
		PriorityQueue<Integer> pq = build(5, 4, 3, 2, 1);
		assertEquals("Each new smallest item should bubble to the root.",
			"[1, 2, 4, 5, 3]", level(pq));
		assertEquals("The smallest item should be the head.", (Integer) 1, pq.peek());
	}

	@Test(timeout=2000)
	public void testAddDoesNotSiftUpWhenLarger() {
		PriorityQueue<Integer> pq = build(1, 5, 2, 6, 7, 3, 4);
		assertEquals("Items larger than their parent should stay put.",
			"[1, 5, 2, 6, 7, 3, 4]", level(pq));
	}

	@Test(timeout=2000)
	public void testAddTieDoesNotSwap() {
		Item first = new Item(5, "a");
		Item second = new Item(5, "b");
		PriorityQueue<Item> pq = buildItems(first, second);
		assertEquals("An item equal to its parent should not move above it.",
			"[5a, 5b]", level(pq));
		assertEquals("The first item added should still be the head.", first, pq.peek());
	}

	@Test(timeout=2000)
	public void testAddTieWithComparatorDoesNotSwap() {
		PriorityQueue<String> pq = new PriorityQueue<String>(BY_LENGTH);
		pq.add("aa");
		pq.add("bb");
		assertEquals("A tie under the comparator should not cause a swap.",
			"[aa, bb]", level(pq));
		assertEquals("The first item added should still be the head.", "aa", pq.peek());
	}

	@Test(timeout=2000)
	public void testAddSiftsUpWithComparator() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(DESC);
		pq.add(1);
		pq.add(2);
		pq.add(3);
		pq.add(4);
		assertEquals("A descending queue should bubble the biggest item to the root.",
			"[4, 3, 2, 1]", level(pq));
	}

	@Test(timeout=2000)
	public void testAddSiftsUpFromDeepPosition() {
		PriorityQueue<Integer> pq = build(10, 20, 30, 40, 50, 60, 70, 5);
		assertEquals("A tiny item added deep in the tree should climb to the root.",
			"[5, 10, 30, 20, 50, 60, 70, 40]", level(pq));
	}

	@Test(timeout=2000)
	public void testNoGrowWhenExactlyFull() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(2);
		pq.add(1);
		pq.add(2);
		assertEquals("Filling the array exactly should not resize it.", 2, pq.queue.length);
		assertEquals("Both items should be stored.", 2, pq.size);
	}

	@Test(timeout=2000)
	public void testGrowWhenFull() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(2);
		pq.add(1);
		pq.add(2);
		pq.add(3);
		assertEquals("A small array should double when it fills up.", 4, pq.queue.length);
		assertEquals("Nothing should be lost when growing.", "[1, 2, 3]", level(pq));
	}

	@Test(timeout=2000)
	public void testGrowFromDefaultCapacity() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		for (int i = 0; i < 11; i++) {
			pq.add(i);
		}
		assertEquals("Eleven items fit without growing.", 11, pq.queue.length);
		pq.add(11);
		assertEquals("A small array doubles: 11 becomes 22.", 22, pq.queue.length);
		assertEquals("All twelve items should still be there.", 12, pq.size);
		assertEquals("The head should be unchanged by growing.", (Integer) 0, pq.peek());
	}

	@Test(timeout=2000)
	public void testGrowAtSixtyFourGrowsByHalf() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(64);
		for (int i = 0; i < 65; i++) {
			pq.add(i);
		}
		assertEquals("A big array grows by 50%: 64 becomes 96.", 96, pq.queue.length);
		assertEquals("All 65 items should be stored.", 65, pq.size);
	}

	@Test(timeout=2000)
	public void testGrowBelowSixtyFourDoubles() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(63);
		for (int i = 0; i < 64; i++) {
			pq.add(i);
		}
		assertEquals("A small array doubles: 63 becomes 126.", 126, pq.queue.length);
		assertEquals("All 64 items should be stored.", 64, pq.size);
	}

	@Test(timeout=2000)
	public void testGrowKeepsAllItems() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(1);
		for (int i = 20; i >= 1; i--) {
			pq.add(i);
		}
		assertEquals("Everything added should still be in the queue.", 20, pq.size);
		assertEquals("The queue should still come out in sorted order after growing.",
			"[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20]", polled(pq));
	}

	@Test(timeout=2000)
	public void testPeekEmpty() {
		assertEquals("Peeking at an empty queue gives null.",
			null, new PriorityQueue<Integer>().peek());
	}

	@Test(timeout=2000)
	public void testPeekDoesNotRemove() {
		PriorityQueue<Integer> pq = build(5, 1, 3);
		assertEquals("Peek should give the smallest item.", (Integer) 1, pq.peek());
		assertEquals("Peek should give the same item again.", (Integer) 1, pq.peek());
		assertEquals("Peek should not change the size.", 3, pq.size);
		assertEquals("Peek should not change the contents.", "[1, 5, 3]", level(pq));
		assertEquals("Peek should not count as a modification.", 3, pq.modCount);
	}

	@Test(timeout=2000, expected=NoSuchElementException.class)
	public void testElementOnEmptyThrows() {
		new PriorityQueue<Integer>().element();
	}

	@Test(timeout=2000)
	public void testPollEmpty() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		assertEquals("Polling an empty queue gives null.", null, pq.poll());
		assertEquals("Polling an empty queue leaves it empty.", 0, pq.size);
		assertEquals("Polling an empty queue is not a modification.", 0, pq.modCount);
	}

	@Test(timeout=2000)
	public void testPollRemovesHead() {
		PriorityQueue<Integer> pq = build(5, 1, 3);
		assertEquals("Poll should return the smallest item.", (Integer) 1, pq.poll());
		assertEquals("Poll should shrink the queue by one.", 2, pq.size);
		assertEquals("The next smallest item should move to the head.", (Integer) 3, pq.peek());
		assertEquals("Poll should count as one modification.", 4, pq.modCount);
	}

	@Test(timeout=2000)
	public void testPollClearsVacatedSlot() {
		PriorityQueue<Integer> pq = build(5, 1, 3);
		pq.poll();
		assertEquals("The slot after the last item should be cleared.", null, pq.queue[2]);
		assertEquals("The remaining items should be re-heaped.", "[3, 5]", level(pq));
	}

	@Test(timeout=2000)
	public void testPollGivesSortedOrder() {
		PriorityQueue<Integer> pq = build(4, 7, 1, 9, 2, 8, 3, 5, 6);
		assertEquals("Polling repeatedly should produce sorted order.",
			"[1, 2, 3, 4, 5, 6, 7, 8, 9]", polled(pq));
		assertEquals("The queue should be empty afterwards.", 0, pq.size);
	}

	@Test(timeout=2000)
	public void testPollGivesComparatorOrder() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(DESC);
		for (int v : new int[] {4, 7, 1, 9, 2}) {
			pq.add(v);
		}
		assertEquals("Polling should follow the comparator, not natural order.",
			"[9, 7, 4, 2, 1]", polled(pq));
	}

	@Test(timeout=2000)
	public void testPollSiftsDown() {
		PriorityQueue<Integer> pq = build(1, 5, 2, 6, 7, 3, 4);
		assertEquals("Poll should return the root.", (Integer) 1, pq.poll());
		assertEquals("The last item should be sifted down into place.",
			"[2, 5, 3, 6, 7, 4]", level(pq));
	}

	@Test(timeout=2000)
	public void testPollSiftsDownWithComparator() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(DESC);
		for (int v : new int[] {7, 3, 6, 1, 2, 5, 4}) {
			pq.add(v);
		}
		assertEquals("The descending heap should be built as expected.",
			"[7, 3, 6, 1, 2, 5, 4]", level(pq));
		assertEquals("Poll should return the largest item.", (Integer) 7, pq.poll());
		assertEquals("The comparator should drive the sift down.",
			"[6, 3, 5, 1, 2, 4]", level(pq));
	}

	@Test(timeout=2000)
	public void testPollPrefersSmallerRightChild() {
		PriorityQueue<Integer> pq = build(1, 8, 2, 9);
		assertEquals("The starting heap should look like this.", "[1, 8, 2, 9]", level(pq));
		assertEquals("Poll should return the root.", (Integer) 1, pq.poll());
		assertEquals("The smaller right child should be promoted.", "[2, 8, 9]", level(pq));
	}

	@Test(timeout=2000)
	public void testPollTieBetweenChildrenKeepsLeft() {
		Item root = new Item(1, "a");
		Item left = new Item(5, "a");
		Item right = new Item(5, "b");
		Item deep1 = new Item(9, "a");
		Item deep2 = new Item(9, "b");
		PriorityQueue<Item> pq = buildItems(root, left, right, deep1, deep2);
		assertEquals("The starting heap should look like this.",
			"[1a, 5a, 5b, 9a, 9b]", level(pq));
		assertEquals("Poll should return the root.", root, pq.poll());
		assertEquals("On a tie the left child should be chosen.",
			"[5a, 9b, 5b, 9a]", level(pq));
	}

	@Test(timeout=2000)
	public void testPollStopsSinkingOnTie() {
		Item root = new Item(1, "a");
		Item left = new Item(5, "a");
		Item right = new Item(6, "a");
		Item tail = new Item(5, "b");
		PriorityQueue<Item> pq = buildItems(root, left, right, tail);
		assertEquals("The starting heap should look like this.",
			"[1a, 5a, 6a, 5b]", level(pq));
		assertEquals("Poll should return the root.", root, pq.poll());
		assertEquals("An item tied with its child should stop at that spot.",
			"[5b, 5a, 6a]", level(pq));
	}

	@Test(timeout=2000)
	public void testPollTiesWithComparator() {
		PriorityQueue<String> pq = new PriorityQueue<String>(BY_LENGTH);
		pq.add("a");
		pq.add("bb");
		pq.add("cc");
		pq.add("dddd");
		pq.add("eeee");
		assertEquals("The starting heap should look like this.",
			"[a, bb, cc, dddd, eeee]", level(pq));
		assertEquals("Poll should return the shortest string.", "a", pq.poll());
		assertEquals("Ties should keep the left child and stop the sink.",
			"[bb, eeee, cc, dddd]", level(pq));
	}

	@Test(timeout=2000)
	public void testPollWithOnlyALeftChild() {
		PriorityQueue<Integer> pq = build(1, 2, 3, 9, 4);
		assertEquals("The starting heap should look like this.", "[1, 2, 3, 9, 4]", level(pq));
		assertEquals("Poll should return the root.", (Integer) 1, pq.poll());
		assertEquals("A lone left child should still be considered.", "[2, 4, 3, 9]", level(pq));
	}

	@Test(timeout=2000)
	public void testPollSingleItem() {
		PriorityQueue<Integer> pq = build(42);
		assertEquals("Poll should return the only item.", (Integer) 42, pq.poll());
		assertEquals("The queue should report empty.", 0, pq.size);
		assertEquals("size() should agree.", 0, pq.size());
		assertEquals("The queue should be empty.", true, pq.isEmpty());
	}

	@Test(timeout=2000)
	public void testContainsHead() {
		PriorityQueue<Integer> pq = build(5, 1, 3);
		assertEquals("The item at the root should be found.", true, pq.contains(1));
	}

	@Test(timeout=2000)
	public void testContainsEveryItem() {
		PriorityQueue<Integer> pq = build(1, 5, 2, 6, 7, 3, 4);
		for (int i = 1; i <= 7; i++) {
			assertEquals("Item " + i + " was added, so it should be found.", true, pq.contains(i));
		}
	}

	@Test(timeout=2000)
	public void testContainsMissingItem() {
		PriorityQueue<Integer> pq = build(5, 1, 3);
		assertEquals("An item never added should not be found.", false, pq.contains(99));
	}

	@Test(timeout=2000)
	public void testContainsOnEmpty() {
		assertEquals("An empty queue contains nothing.",
			false, new PriorityQueue<Integer>().contains(1));
	}

	@Test(timeout=2000)
	public void testContainsStopsAtSize() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(3);
		pq.add(1);
		pq.add(2);
		pq.add(3);
		assertEquals("The array is exactly full here.", 3, pq.queue.length);
		assertEquals("A missing item should simply not be found.", false, pq.contains(99));
	}

	@Test(timeout=2000)
	public void testContainsNull() {
		PriorityQueue<Integer> pq = build(5, 1, 3);
		assertEquals("A priority queue never holds null.", false, pq.contains(null));
	}

	@Test(timeout=2000)
	public void testRemoveMissingItem() {
		PriorityQueue<Integer> pq = build(5, 1, 3);
		int before = pq.modCount;
		assertEquals("Removing a missing item should report false.", false, pq.remove(99));
		assertEquals("The queue should be untouched.", "[1, 5, 3]", level(pq));
		assertEquals("The size should be untouched.", 3, pq.size);
		assertEquals("Nothing was modified.", before, pq.modCount);
	}

	@Test(timeout=2000)
	public void testRemoveHead() {
		PriorityQueue<Integer> pq = build(1, 5, 2, 6, 7, 3, 4);
		assertEquals("Removing an item in the queue should report true.", true, pq.remove(1));
		assertEquals("The heap should be repaired after removal.", "[2, 5, 3, 6, 7, 4]", level(pq));
		assertEquals("The size should drop by one.", 6, pq.size);
	}

	@Test(timeout=2000)
	public void testRemoveLastItemInArray() {
		PriorityQueue<Integer> pq = build(1, 5, 2, 6, 7, 3, 4);
		assertEquals("Removing the tail item should report true.", true, pq.remove(4));
		assertEquals("Nothing else should move.", "[1, 5, 2, 6, 7, 3]", level(pq));
		assertEquals("The vacated slot should be cleared.", null, pq.queue[6]);
		assertEquals("The size should drop by one.", 6, pq.size);
	}

	@Test(timeout=2000)
	public void testRemoveCausingSiftUp() {
		PriorityQueue<Integer> pq = build(1, 5, 2, 6, 7, 3, 4);
		assertEquals("Removing an interior item should report true.", true, pq.remove(7));
		assertEquals("The moved item should climb into place.", "[1, 4, 2, 6, 5, 3]", level(pq));
		assertEquals("The size should drop by one.", 6, pq.size);
	}

	@Test(timeout=2000)
	public void testRemoveLeavingMovedItemInPlace() {
		PriorityQueue<Integer> pq = build(1, 5, 2, 6, 7, 3, 4);
		assertEquals("Removing an interior item should report true.", true, pq.remove(5));
		assertEquals("The moved item fits right where it landed.", "[1, 4, 2, 6, 7, 3]", level(pq));
	}

	@Test(timeout=2000)
	public void testRemoveCountsAsModification() {
		PriorityQueue<Integer> pq = build(5, 1, 3);
		int before = pq.modCount;
		pq.remove(5);
		assertEquals("A successful remove is one modification.", before + 1, pq.modCount);
	}

	@Test(timeout=2000)
	public void testRemoveOnlyOneCopy() {
		PriorityQueue<Integer> pq = build(2, 2, 2);
		assertEquals("Removing a duplicate should report true.", true, pq.remove(2));
		assertEquals("Only one copy should go.", 2, pq.size);
		assertEquals("The other copies should still be found.", true, pq.contains(2));
	}

	@Test(timeout=2000)
	public void testRemoveNoArgs() {
		PriorityQueue<Integer> pq = build(5, 1, 3);
		assertEquals("remove() should hand back the head.", (Integer) 1, pq.remove());
		assertEquals("The queue should shrink.", 2, pq.size);
	}

	@Test(timeout=2000)
	public void testRemoveFromComparatorQueue() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(DESC);
		for (int v : new int[] {7, 3, 6, 1, 2, 5, 4}) {
			pq.add(v);
		}
		assertEquals("The starting heap should look like this.",
			"[7, 3, 6, 1, 2, 5, 4]", level(pq));
		assertEquals("Removing a stored item should report true.", true, pq.remove(3));
		assertEquals("The comparator should drive the repair.", "[7, 4, 6, 1, 2, 5]", level(pq));
		assertEquals("The size should drop by one.", 6, pq.size);
	}

	@Test(timeout=2000)
	public void testRemoveAtLastIndex() {
		PriorityQueue<Integer> pq = build(1, 5, 2, 6, 7, 3, 4);
		assertEquals("Removing the tail moves nothing, so nothing is reported.",
			null, pq.removeAt(6));
		assertEquals("The rest of the heap is untouched.", "[1, 5, 2, 6, 7, 3]", level(pq));
	}

	@Test(timeout=2000)
	public void testRemoveAtLeavingItemInPlaceReportsNull() {
		PriorityQueue<Integer> pq = build(1, 5, 2, 6, 7, 3, 4);
		assertEquals("An item that does not move is not reported.", null, pq.removeAt(1));
		assertEquals("The heap should be repaired.", "[1, 4, 2, 6, 7, 3]", level(pq));
	}

	@Test(timeout=2000)
	public void testRemoveAtReportsMovedItem() {
		PriorityQueue<Integer> pq = build(1, 5, 2, 6, 7, 3, 4);
		assertEquals("The item that jumped backwards should be reported.",
			(Integer) 4, pq.removeAt(4));
		assertEquals("The heap should be repaired.", "[1, 4, 2, 6, 5, 3]", level(pq));
	}

	@Test(timeout=2000)
	public void testRemoveAtSinkingReportsNull() {
		PriorityQueue<Integer> pq = build(1, 5, 2, 6, 7, 3, 4);
		assertEquals("An item that sinks forwards is not reported.", null, pq.removeAt(0));
		assertEquals("The heap should be repaired.", "[2, 5, 3, 6, 7, 4]", level(pq));
	}

	@Test(timeout=2000)
	public void testRemoveAtSinkingWithComparator() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(DESC);
		for (int v : new int[] {7, 3, 6, 1, 2, 5, 4}) {
			pq.add(v);
		}
		assertEquals("An item that sinks forwards is not reported.", null, pq.removeAt(0));
		assertEquals("The comparator should drive the sink.", "[6, 3, 5, 1, 2, 4]", level(pq));
	}

	@Test(timeout=2000)
	public void testRemoveAtClimbingWithComparator() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(DESC);
		for (int v : new int[] {7, 3, 6, 1, 2, 5, 4}) {
			pq.add(v);
		}
		assertEquals("The item that jumped backwards should be reported.",
			(Integer) 4, pq.removeAt(4));
		assertEquals("The comparator should drive the climb.", "[7, 4, 6, 1, 3, 5]", level(pq));
	}

	@Test(timeout=2000)
	public void testRemoveEq() {
		Item first = new Item(1, "a");
		Item second = new Item(5, "b");
		Item third = new Item(2, "c");
		PriorityQueue<Item> pq = buildItems(first, second, third);
		assertEquals("The starting heap should look like this.", "[1a, 5b, 2c]", level(pq));
		pq.removeEq(second);
		assertEquals("Only the identified item should go.", "[1a, 2c]", level(pq));
		assertEquals("The size should drop by one.", 2, pq.size);
	}

	@Test(timeout=2000)
	public void testRemoveEqMissing() {
		Item first = new Item(1, "a");
		Item second = new Item(5, "b");
		PriorityQueue<Item> pq = buildItems(first, second);
		int before = pq.modCount;
		pq.removeEq(new Item(1, "a"));
		assertEquals("An equal-but-different object should not be removed.",
			"[1a, 5b]", level(pq));
		assertEquals("The size should be unchanged.", 2, pq.size);
		assertEquals("Nothing was modified.", before, pq.modCount);
	}

	@Test(timeout=2000)
	public void testRemoveEqStopsAtSize() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(3);
		pq.add(1);
		pq.add(2);
		pq.add(3);
		assertEquals("The array is exactly full here.", 3, pq.queue.length);
		pq.removeEq(99);
		assertEquals("Nothing should be removed.", "[1, 2, 3]", level(pq));
		assertEquals("The size should be unchanged.", 3, pq.size);
	}

	@Test(timeout=2000)
	public void testRemoveEqRemovesOnlyOne() {
		Item first = new Item(1, "a");
		Item second = new Item(2, "b");
		Item third = new Item(3, "c");
		Item fourth = new Item(4, "d");
		PriorityQueue<Item> pq = buildItems(first, second, third, fourth);
		pq.removeEq(first);
		assertEquals("Exactly one item should be removed.", 3, pq.size);
		assertEquals("The heap should be repaired.", "[2b, 4d, 3c]", level(pq));
	}

	@Test(timeout=2000)
	public void testToArray() {
		PriorityQueue<Integer> pq = build(1, 5, 2, 6);
		Object[] array = pq.toArray();
		assertEquals("The array should be exactly the size of the queue.", 4, array.length);
		assertEquals("The array should hold the level order contents.",
			"[1, 5, 2, 6]", join(array));
	}

	@Test(timeout=2000)
	public void testToArrayEmpty() {
		assertEquals("An empty queue gives a zero-length array.",
			0, new PriorityQueue<Integer>().toArray().length);
	}

	@Test(timeout=2000)
	public void testToArrayIsACopy() {
		PriorityQueue<Integer> pq = build(1, 5, 2);
		Object[] array = pq.toArray();
		array[0] = 99;
		assertEquals("Changing the returned array must not change the queue.",
			(Integer) 1, pq.peek());
		assertEquals("The queue's own array must be untouched.", "[1, 5, 2]", level(pq));
	}

	@Test(timeout=2000)
	public void testToArrayTypedTooSmall() {
		PriorityQueue<String> pq = new PriorityQueue<String>();
		pq.add("b");
		pq.add("a");
		pq.add("c");
		String[] given = new String[0];
		String[] result = pq.toArray(given);
		assertEquals("A new array should be made when the given one is too small.",
			false, result == given);
		assertEquals("The new array should be exactly the queue's size.", 3, result.length);
		assertEquals("The new array should hold the level order contents.",
			"[a, b, c]", join(result));
		assertEquals("The new array should keep the given array's type.",
			String[].class, result.getClass());
	}

	@Test(timeout=2000)
	public void testToArrayTypedExactFit() {
		PriorityQueue<String> pq = new PriorityQueue<String>();
		pq.add("b");
		pq.add("a");
		pq.add("c");
		String[] given = new String[3];
		String[] result = pq.toArray(given);
		assertEquals("An exactly sized array should be reused.", true, result == given);
		assertEquals("The given array should be filled in.", "[a, b, c]", join(result));
	}

	@Test(timeout=2000)
	public void testToArrayTypedTooLarge() {
		PriorityQueue<String> pq = new PriorityQueue<String>();
		pq.add("b");
		pq.add("a");
		pq.add("c");
		String[] given = {"x", "x", "x", "x", "x"};
		String[] result = pq.toArray(given);
		assertEquals("A larger array should be reused.", true, result == given);
		assertEquals("The queue should be copied in, then a null terminator added.",
			"[a, b, c, null, x]", join(result));
	}

	@Test(timeout=2000)
	public void testToArrayTypedEmptyQueue() {
		PriorityQueue<String> pq = new PriorityQueue<String>();
		String[] given = {"x", "y"};
		String[] result = pq.toArray(given);
		assertEquals("The first slot should be the null terminator.", "[null, y]", join(result));
	}

	@Test(timeout=2000)
	public void testIteratorLevelOrder() {
		PriorityQueue<Integer> pq = build(1, 5, 2, 6, 7, 3, 4);
		assertEquals("The iterator should walk the heap in level order.",
			"[1, 5, 2, 6, 7, 3, 4]", iterated(pq));
	}

	@Test(timeout=2000)
	public void testIteratorOnEmptyQueue() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		Iterator<Integer> it = pq.iterator();
		assertEquals("An empty queue's iterator has no next item.", false, it.hasNext());
	}

	@Test(timeout=2000, expected=NoSuchElementException.class)
	public void testIteratorPastEndThrows() {
		PriorityQueue<Integer> pq = build(1, 2);
		Iterator<Integer> it = pq.iterator();
		it.next();
		it.next();
		it.next();
	}

	@Test(timeout=2000)
	public void testIteratorHasNextAtEachStep() {
		PriorityQueue<Integer> pq = build(1, 2, 3);
		Iterator<Integer> it = pq.iterator();
		assertEquals("Three items left.", true, it.hasNext());
		assertEquals("First item.", (Integer) 1, it.next());
		assertEquals("Two items left.", true, it.hasNext());
		assertEquals("Second item.", (Integer) 2, it.next());
		assertEquals("One item left.", true, it.hasNext());
		assertEquals("Third item.", (Integer) 3, it.next());
		assertEquals("Nothing left.", false, it.hasNext());
	}

	@Test(timeout=2000, expected=ConcurrentModificationException.class)
	public void testIteratorDetectsAdd() {
		PriorityQueue<Integer> pq = build(1, 2, 3);
		Iterator<Integer> it = pq.iterator();
		it.next();
		pq.add(99);
		it.next();
	}

	@Test(timeout=2000, expected=ConcurrentModificationException.class)
	public void testIteratorDetectsPoll() {
		PriorityQueue<Integer> pq = build(1, 2, 3);
		Iterator<Integer> it = pq.iterator();
		it.next();
		pq.poll();
		it.next();
	}

	@Test(timeout=2000, expected=ConcurrentModificationException.class)
	public void testIteratorRemoveDetectsOutsideChange() {
		PriorityQueue<Integer> pq = build(1, 2, 3);
		Iterator<Integer> it = pq.iterator();
		it.next();
		pq.add(99);
		it.remove();
	}

	@Test(timeout=2000, expected=IllegalStateException.class)
	public void testIteratorRemoveBeforeNextThrows() {
		PriorityQueue<Integer> pq = build(1, 2, 3);
		pq.iterator().remove();
	}

	@Test(timeout=2000, expected=IllegalStateException.class)
	public void testIteratorRemoveTwiceThrows() {
		PriorityQueue<Integer> pq = build(1, 2, 3);
		Iterator<Integer> it = pq.iterator();
		it.next();
		it.remove();
		it.remove();
	}

	@Test(timeout=2000)
	public void testIteratorRemove() {
		PriorityQueue<Integer> pq = build(1, 2, 3);
		Iterator<Integer> it = pq.iterator();
		assertEquals("The first item should be the head.", (Integer) 1, it.next());
		it.remove();
		assertEquals("The removed item should be gone.", false, pq.contains(1));
		assertEquals("The queue should shrink by one.", 2, pq.size);
	}

	@Test(timeout=2000)
	public void testIteratorRemoveDoesNotSkip() {
		PriorityQueue<Integer> pq = build(1, 5, 2, 6, 7, 3, 4);
		Iterator<Integer> it = pq.iterator();
		StringBuilder seen = new StringBuilder();
		seen.append(it.next()).append(" ");
		it.remove();
		while (it.hasNext()) {
			seen.append(it.next()).append(" ");
		}
		assertEquals("Every remaining item should still be visited exactly once.",
			"1 2 5 3 6 7 4 ", seen.toString());
		assertEquals("The queue should be one smaller.", 6, pq.size);
	}

	@Test(timeout=2000)
	public void testIteratorKeepsGoingAfterRemove() {
		PriorityQueue<Integer> pq = build(1, 2, 3, 4);
		Iterator<Integer> it = pq.iterator();
		it.next();
		it.remove();
		assertEquals("The iterator should still have items.", true, it.hasNext());
		assertEquals("The item moved into the gap should be visited.", (Integer) 2, it.next());
	}

	@Test(timeout=2000)
	public void testIteratorRemoveWithoutMovingAnItemBackwards() {
		PriorityQueue<Integer> pq = build(1, 5, 2, 6, 7, 3, 4);
		Iterator<Integer> it = pq.iterator();
		StringBuilder seen = new StringBuilder();
		seen.append(it.next()).append(" ");
		seen.append(it.next()).append(" ");
		it.remove();
		assertEquals("The heap should be repaired by the removal.",
			"[1, 4, 2, 6, 7, 3]", level(pq));
		while (it.hasNext()) {
			seen.append(it.next()).append(" ");
		}
		assertEquals("Each remaining item should be visited exactly once, in place.",
			"1 5 4 2 6 7 3 ", seen.toString());
		assertEquals("The queue should be one smaller.", 6, pq.size);
	}

	@Test(timeout=2000)
	public void testIteratorRemembersItemMovedBackwards() {
		PriorityQueue<Integer> pq = build(1, 5, 2, 6, 7, 3, 4);
		Iterator<Integer> it = pq.iterator();
		StringBuilder seen = new StringBuilder();
		for (int i = 0; i < 5; i++) {
			seen.append(it.next()).append(" ");
		}
		it.remove();
		assertEquals("The heap should be repaired by the removal.",
			"[1, 4, 2, 6, 5, 3]", level(pq));
		assertEquals("There should be more to see.", true, it.hasNext());
		while (it.hasNext()) {
			seen.append(it.next()).append(" ");
		}
		assertEquals("The item that jumped backwards should still be handed over.",
			"1 5 2 6 7 3 4 ", seen.toString());
		assertEquals("The queue should be one smaller.", 6, pq.size);
	}

	@Test(timeout=2000)
	public void testIteratorRemembersTwoItemsMovedBackwards() {
		PriorityQueue<Integer> pq = build(0, 100, 10, 101, 102, 11, 12);
		assertEquals("The starting heap should look like this.",
			"[0, 100, 10, 101, 102, 11, 12]", level(pq));
		Iterator<Integer> it = pq.iterator();
		StringBuilder seen = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			seen.append(it.next()).append(" ");
		}
		it.remove();
		assertEquals("The first unlucky removal should repair the heap.",
			"[0, 12, 10, 100, 102, 11]", level(pq));
		seen.append(it.next()).append(" ");
		it.remove();
		assertEquals("The second unlucky removal should repair the heap.",
			"[0, 11, 10, 100, 12]", level(pq));
		while (it.hasNext()) {
			seen.append(it.next()).append(" ");
		}
		assertEquals("Both remembered items should be handed over at the end.",
			"0 100 10 101 102 12 11 ", seen.toString());
		assertEquals("Two items should have been removed.", 5, pq.size);
	}

	@Test(timeout=2000)
	public void testIteratorRemovesRememberedItem() {
		PriorityQueue<Integer> pq = build(1, 5, 2, 6, 7, 3, 4);
		Iterator<Integer> it = pq.iterator();
		for (int i = 0; i < 5; i++) {
			it.next();
		}
		it.remove();
		while (it.hasNext()) {
			it.next();
		}
		it.remove();
		assertEquals("Both removed items should be gone.", 5, pq.size);
		assertEquals("The heap should still be correct.", "[1, 3, 2, 6, 5]", level(pq));
		assertEquals("The remembered item should have been removed.", false, pq.contains(4));
	}

	@Test(timeout=2000, expected=NoSuchElementException.class)
	public void testIteratorFinishesAfterRememberedItem() {
		PriorityQueue<Integer> pq = build(1, 5, 2, 6, 7, 3, 4);
		Iterator<Integer> it = pq.iterator();
		for (int i = 0; i < 5; i++) {
			it.next();
		}
		it.remove();
		while (it.hasNext()) {
			it.next();
		}
		it.next();
	}

	@Test(timeout=2000)
	public void testClear() {
		PriorityQueue<Integer> pq = build(1, 5, 2, 6);
		int before = pq.modCount;
		pq.clear();
		assertEquals("A cleared queue reports size 0.", 0, pq.size());
		assertEquals("The size field should be 0.", 0, pq.size);
		assertEquals("Peeking a cleared queue gives null.", null, pq.peek());
		assertEquals("Clearing counts as one modification.", before + 1, pq.modCount);
		assertEquals("Slot 0 should be released.", null, pq.queue[0]);
		assertEquals("Slot 1 should be released.", null, pq.queue[1]);
		assertEquals("Slot 2 should be released.", null, pq.queue[2]);
		assertEquals("Slot 3 should be released.", null, pq.queue[3]);
	}

	@Test(timeout=2000)
	public void testClearStopsAtSize() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>(2);
		pq.add(1);
		pq.add(2);
		assertEquals("The array is exactly full here.", 2, pq.queue.length);
		pq.clear();
		assertEquals("The queue should be empty.", 0, pq.size);
		assertEquals("Slot 0 should be released.", null, pq.queue[0]);
		assertEquals("Slot 1 should be released.", null, pq.queue[1]);
	}

	@Test(timeout=2000)
	public void testClearEmptyQueue() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		pq.clear();
		assertEquals("Still empty.", 0, pq.size);
		assertEquals("Clearing still counts as a modification.", 1, pq.modCount);
	}

	@Test(timeout=2000)
	public void testClearThenReuse() {
		PriorityQueue<Integer> pq = build(1, 5, 2, 6);
		pq.clear();
		pq.add(8);
		pq.add(4);
		assertEquals("The queue should work again after clearing.", "[4, 8]", level(pq));
		assertEquals("The size should count only the new items.", 2, pq.size);
	}

	@Test(timeout=2000)
	public void testSizeTracksChanges() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		assertEquals("A new queue is empty.", 0, pq.size());
		pq.add(3);
		pq.add(1);
		assertEquals("Two adds means size 2.", 2, pq.size());
		pq.poll();
		assertEquals("A poll brings it back to 1.", 1, pq.size());
		pq.remove(3);
		assertEquals("A remove empties it.", 0, pq.size());
	}

	@Test(timeout=2000)
	public void testComparatorIsNullByDefault() {
		assertEquals("A natural ordering queue reports no comparator.",
			null, new PriorityQueue<Integer>().comparator());
	}

	@Test(timeout=2000)
	public void testComparatorIsReturned() {
		assertEquals("The comparator given should be the comparator returned.",
			DESC, new PriorityQueue<Integer>(DESC).comparator());
	}

	@Test(timeout=2000)
	public void testManyAddsAndPolls() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		int[] values = {50, 13, 7, 99, 2, 41, 8, 23, 16, 4, 77, 1, 62, 35, 28};
		for (int v : values) {
			pq.add(v);
		}
		assertEquals("Everything added should be in the queue.", 15, pq.size());
		assertEquals("Polling should give sorted order.",
			"[1, 2, 4, 7, 8, 13, 16, 23, 28, 35, 41, 50, 62, 77, 99]", polled(pq));
	}

	@Test(timeout=2000)
	public void testInterleavedAddsAndPolls() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		pq.add(10);
		pq.add(20);
		assertEquals("Smallest first.", (Integer) 10, pq.poll());
		pq.add(5);
		pq.add(15);
		assertEquals("The newly added smaller item should come out next.", (Integer) 5, pq.poll());
		assertEquals("Then the next smallest.", (Integer) 15, pq.poll());
		assertEquals("Then the last one.", (Integer) 20, pq.poll());
		assertEquals("The queue should be empty.", 0, pq.size());
	}

	@Test(timeout=2000)
	public void testDuplicatesAreAllKept() {
		PriorityQueue<Integer> pq = build(3, 1, 3, 1, 3);
		assertEquals("All five items should be kept.", 5, pq.size());
		assertEquals("Duplicates should come out together.", "[1, 1, 3, 3, 3]", polled(pq));
	}

	@Test(timeout=2000)
	public void testAddAll() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		assertEquals("addAll should report that the queue changed.",
			true, pq.addAll(listOf(5, 1, 3)));
		assertEquals("Every item should be added.", 3, pq.size());
		assertEquals("The smallest should be on top.", (Integer) 1, pq.peek());
	}

	@Test(timeout=2000)
	public void testIsEmpty() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		assertEquals("A new queue is empty.", true, pq.isEmpty());
		pq.add(1);
		assertEquals("A queue with an item is not empty.", false, pq.isEmpty());
		pq.poll();
		assertEquals("Emptying the queue makes it empty again.", true, pq.isEmpty());
	}

	public static void main(String args[]) {
		org.junit.runner.JUnitCore.main("MyTestsPQ");
	}
}
