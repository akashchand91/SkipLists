package pxp171530;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * 
 * @author pushpita panigrahi - pxp171530
 * @author deeksha lakshmeesh mestha - dxm172630
 * @author sneha hulivan girisha - sxh173730
 * @author akash chand - axc173730 SkipList class Implement the operations of
 *         skip lists where insert, delete,find functions take O(log n) expected
 *         time per operation.
 */
public class SkipList<T extends Comparable<? super T>> {
	static final int PossibleLevels = 33;

	static class Entry<E extends Comparable<? super E>> {
		E element;
		Entry[] next;
		Integer[] span;// used for indexing
		// Entry prev;

		public Entry(E x, int lev) {
			element = x;
			next = new Entry[lev];
			span = new Integer[lev];
		}

		public E getElement() {
			return element;
		}
		@Override
		public String toString() {
			return this.element+"";
		}

		public void setNext(int level, Entry<E> node) {
			// update next[] to level, if existing length < level
			if (next.length - 1 < level) {
				Entry[] newArr = new Entry[level + 1];
				System.arraycopy(next, 0, newArr, 0, next.length);
				newArr[level] = node;
				next = newArr;
			} else {
				next[level] = node;
			}
		}
	}

	Entry<T> head, tail;// dummy nodes
	int size, maxLevel;
	Entry<T>[] last;// used by find function
	Random random;

	// Constructor
	public SkipList() {
		head = new Entry<T>(null, PossibleLevels);
		tail = new Entry<T>(null, PossibleLevels);
		size = 0;
		maxLevel = 1;
		last = new Entry[PossibleLevels];
		random = new Random();
		for (int i = PossibleLevels - 1; i >= 0; i--) {
			head.next[i] = tail;
			head.span[i] = 0;
		}
	}

	/**
	 * Adds x to list. If x already exists, reject it. Returns true if new node
	 * is added to list.
	 * 
	 * @param x
	 * @return boolean
	 */
	public boolean add(T x) {
		if (contains(x)) {// reject if present
			return false;
		}

		int lev = chooseLevel();// choose level randomly
		Entry<T> ent = new Entry<T>(x, lev);

		// Assign pointers for upto max level
		for (int i = 0; i < Math.min(lev, this.maxLevel); i++) {
			ent.next[i] = last[i].next[i];
			last[i].next[i] = ent;
			if (i == 0) {
				last[i].span[i] = 1;
				ent.span[i] = 1;
			} else {
				ent.span[i] = updateSpan(ent, i);
				last[i].span[i] = updateSpan(last[i], i);
			}
		}

		// increasing the span count by one as these levels pass over the added node,
		// for the levels between lev and maxlevel
		for (int i = Math.min(lev, this.maxLevel); i < this.maxLevel; i++) {
			last[i].span[i]++; 
		}
		// handle pointers if new level > max level
		for (int i = Math.min(lev, this.maxLevel); i < lev; i++) {
			ent.next[i] = tail;
			head.next[i] = ent;
			head.span[i] = updateSpan(head, i);
			ent.span[i] = updateSpan(ent, i);
		}

		// update maxLevel after adding the new element
		if (lev > maxLevel) {
			maxLevel = lev;
		}
		size++;
		return true;
	}

	/**
	 * Calculates the i-th span value for a particular node
	 * @param i-level value
	 * @return int
	 */
	private int updateSpan(Entry<T> node, int i) {
		int count = 0;
		Entry<T> targetNode = node.next[i];
		//System.out.println(targetNode.element);
		do {
			count += node.span[i - 1];
			node = node.next[i - 1];

		} while (node != targetNode && node !=tail);
		return count;
	}

	/**
	 * Chooses level randomly.Helper for add
	 * @return int
	 */
	private int chooseLevel() {
		int lev = 1 + Integer.numberOfTrailingZeros(random.nextInt());
		lev = lev < maxLevel + 1 ? lev : maxLevel + 1;
		return lev;
	}

	/**
	 * Finds smallest element that is greater or equal to x.
	 * @param x
	 * @return T
	 */
	public T ceiling(T x) {
		if (!contains(x)) {
			Entry<T> ele = last[0].next[0];
			if (ele != null && ele.element != null) {
				return (T) ele.element;
			} else {
				return ele.element;
			}
		} else {
			return x;
		}

	}

	/**
	 * checks if list contain x.
	 * @param x
	 * @return boolean
	 */
	public boolean contains(T x) {
		find(x);
		// also handles null pointer case
		if (last[0].next[0] != null && last[0].next[0].element != null) {
			return last[0].next[0].element.equals(x);
		} else {
			return false;
		}

	}

	/**
	 * Helper function for contains. Sets last[i] = node at which search came
	 * down from level i to i-1
	 * @param x
	 */
	private void find(T x) {
		Entry<T> p = head;
		for (int i = this.maxLevel - 1; i >= 0; i--) {
			while (p.next[i].element != null && p.next[i].element.compareTo(x) < 0) {
				p = p.next[i];
			}
			last[i] = p;
		}
	}

	/**
	 * Returns first element of list
     * @return T
	 */
	public T first() {
		if (size == 0) {
			return null;
		} else {
			T ent = (T) head.next[0].element;
			return ent;
		}
	}

	/**
	 * Finds largest element that is less than or equal to x
	 * @param x
	 * @return T
	 */
	public T floor(T x) {
		if (!contains(x)) {
			Entry<T> ele = last[0];
			return ele.element;
		} else {
			return x;
		}
	}

	/**
	 * Returns element at index n of list. First element is at index 0.
	 * @param n
	 * @return T
	 */
	public T get(int n) {
		return getLog(n);
	}

	/**
	 * O(n) algorithm for get( index n)
	 * 
	 * @param n
	 * @return
	 */
	public T getLinear(int n) {
		if (n < 0 || n > size - 1) {
			throw new NoSuchElementException();
		}
		Entry<T> p = head;
		for (int i = 0; i <= n; i++) {
			p = p.next[0];
		}
		return p.element;
	}

	/**
	 * O(log n) expected time for get(index n).
	 * 
	 * @param n
	 * @return T
	 */
	public T getLog(int n) {
		if (n < 0 || n > size - 1) {
			throw new NoSuchElementException();
		}
		Entry<T> p = head;
		int index = -1;
		for (int i = this.maxLevel - 1; i >= 0; i--) {
			if (index + p.span[i] > n)
				continue;
			index += p.span[i];
			while (p.next[i].element != null && index <= n) {
				p = p.next[i];
				if (index + p.span[i] > n)
					break;
				index += p.span[i];
			}

		}
		return p.element;
	}

	/**
	 * Checks if list is empty.
	 * @return boolean
	 */
	public boolean isEmpty() {
		if (size == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Iterates through the elements of list in sorted order
	 * @return Iterator
	 */
	public Iterator<T> iterator() {
		Iterator<T> it = new Iterator<T>() {
			private Entry<T> cursor = head;

			@Override
			public boolean hasNext() {
				return cursor.next[0] != tail;
			}

			@Override
			public T next() {
				cursor = cursor.next[0];
				return cursor.element;
			}

			@Override
			public void remove() {
				if (SkipList.this.remove(cursor.element).equals(null)) {
					System.out.println("Could not remove element:" + cursor.element);
				}
			}
		};
		return it;
	}

	/**
	 * Returns last element of list
	 * @return T
	 */
	public T last() {
		Entry<T> cursor = head;
		while (cursor.next[0] != tail) {
			for (int i = cursor.next.length - 1; i >= 0; i--) {
				if (cursor.next[i] == tail) {
					continue;
				} else {
					cursor = cursor.next[i];
					break;
				}
			}
		}
		return cursor.element;
	}

	public void printList() {
		Entry<T> cursor = head;
		while (cursor != tail) {
			System.out.print(cursor.element + ":" + cursor.next.length + " ");
			cursor = cursor.next[0];
		}
		System.out.println();
	}
	
	/**
	 * Reorganize the elements of the list into a perfect skip list. We rebuild ONLY
	 * when the size of list increases by power of 2 We need to convert size in
	 * terms of power 2 to get the new maxlevel
	 */
	public void rebuild() {
		Entry<T> cursor = head;
		Entry<T>[] newNext;
		int newmaxlevel = (int) Math.ceil((Math.log(size() + 1) / Math.log(2)));

		for (int i = 1; i < newmaxlevel; i++) {
			cursor = head;
			while (cursor.next != null && cursor.next[0] != null && cursor.next[i - 1] != null) {
				newNext = cursor.next[i - 1].next;
				if (newNext[0] == null) {
					cursor.setNext(i, cursor.next[i - 1]);
				} else {
					cursor.setNext(i, newNext[i - 1]);
				}
				cursor = cursor.next[i];
			}
		}
		this.maxLevel = newmaxlevel;
		rebuildSpan(head);
	}

/**
 * Updates the span values of each node
 * @param head - head node of the list
 */
	private void rebuildSpan(Entry<T> head) {
		if(head.next[0]!=tail) {
			rebuildSpan(head.next[0]); // recursively iterating to last but one node
		}
		if(head==tail)
			return;
		head.span=new Integer[head.next.length];
		head.span[0]=1;
		for(int i=1;i<head.next.length;i++) {
			head.span[i]=updateSpan(head, i);
		}
		
	}

	/**
	 * Removes x from list. Removed element is returned. Returns null if x not
	 * in list.
	 * @param x
	 * @return T
	 */
	public T remove(T x) {
		if (!contains(x)) {
			return null;
		}
		Entry<T> ent = last[0].next[0];
		int lev = ent.next.length;
		for (int i = 0; i < lev; i++) {
			last[i].next[i] = ent.next[i];
		}
		size = size - 1;
		for (int i = 1; i < lev; i++) {
			Entry<T> node = last[i];
			last[i].span[i] = updateSpan(node, i);
		}
		for (int i = Math.min(lev, this.maxLevel); i < this.maxLevel; i++) {
			--last[i].span[i];
		}
		return ent.element;
	}

	/**
	 * Returns the number of elements in the list.
	 * @return int
	 */
	public int size() {
		return size;
	}
}