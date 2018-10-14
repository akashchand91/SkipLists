package pxp171530;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

//Skeleton for skip list implementation.

public class SkipList<T extends Comparable<? super T>>  {
	static final int PossibleLevels = 33;

	static class Entry<E extends Comparable<? super E>> {
		E element;
		Entry[] next;
		Integer[] span;
//		Entry prev;

		public Entry(E x, int lev) {
			element = x;
			next = new Entry[lev];
			span = new Integer[lev];
//			prev = new Entry(null,0,null);
			// add more code if needed
		}
		
//		private Entry(E x, int lev, Entry p) {
//			element = x;
//			next = new Entry[lev];
//			prev = p;
//		}
	
		public E getElement() {
			return element;
		}

	}
	
	Entry<T> head,tail;
	int size,maxLevel;
	Entry<T>[] last;//used by find function
	Random random;


	// Constructor
	public SkipList() {
		head = new Entry<T>(null, PossibleLevels);
		tail = new Entry<T>(null, PossibleLevels);
		size=0;
		maxLevel=1;
		last = new Entry[PossibleLevels];
		random = new Random();
		for (int i = PossibleLevels - 1; i >= 0; i--) {
			head.next[i] = tail;
			head.span[i]=0;
		}
	}

	// Add x to list. If x already exists, reject it. Returns true if new node is
	// added to list
	public boolean add(T x) {
		if (contains(x)) {
			return false;
		}

		int lev = chooseLevel();
		System.out.println("level: " + lev);
		Entry<T> ent = new Entry<T>(x,lev);

		// Assign pointers for upto max level
		for (int i = 0; i < Math.min(lev, this.maxLevel); i++) {
			ent.next[i] = last[i].next[i];
			last[i].next[i] = ent;
			if(i==0) {
				last[i].span[i]=1;
				ent.span[i]=1;
			}
			else {
				
				ent.span[i]=updateSpan(ent, i); //update new node's span values
				last[i].span[i]=updateSpan(last[i], i); // update span values of nodes in last array
			}
		}
		//handle span values for the levels in between lev and maxlevel if lev< maxlevel
		for(int i=Math.min(lev ,this.maxLevel);i< this.maxLevel;i++) {
			last[i].span[i]++; //increasing the span count by one as these levels pass over the added node
		}
		//handle pointers if new level > max level
		for(int i = Math.min(lev ,this.maxLevel); i < lev; i++) {
			ent.next[i] = tail;
			head.next[i] = ent;
			
			//code for handling span if newlevel > maxlevel goes here
			head.span[i]=updateSpan(head, i );
			ent.span[i]=updateSpan(ent, i);
		}
		
//		ent.next[0].prev=ent;
//		ent.prev=last[0];

		// update maxLevel after adding the new element
		if(lev>maxLevel){
			maxLevel=lev;
		}
		size++;
		return true;
	}

	/**
	 * Calculates the i-th span value for a particular node
	 * @param i- level value
	 * @return 
	 */
	private int updateSpan(Entry<T> node, int i) {
		int count= 0;
		Entry<T> targetNode = node.next[i];
		do {
			count+=node.span[i-1];
			node = node.next[i-1];
			
		}while( node != targetNode);
		return count;
	}

	private int chooseLevel() {
		int lev = 1 + Integer.numberOfTrailingZeros(random.nextInt());
		lev = lev < maxLevel + 1 ? lev : maxLevel + 1;
		return lev;
	}

	// Find smallest element that is greater or equal to x
	public T ceiling(T x) {
		if (!contains(x)) {
			Entry<T> ele = last[0].next[0];
			if (ele != null && ele.next[0] != null && ele.next[0].element != null) {
				return (T) ele.next[0].element;
			} else {
				return ele.element;
			}	
		} else {
			return x;
		}
		
	}

	// Does list contain x?
	public boolean contains(T x) {
		find(x);
		if (last[0].next[0] != null && last[0].next[0].element != null) {
			return last[0].next[0].element.equals(x);
		}else {
			return false;
		}
	    
	}

	private void find(T x) {
		Entry<T> p = head;
		for (int i = this.maxLevel - 1; i >= 0; i--) {
			while (p.next[i].element != null && p.next[i].element.compareTo(x) < 0) {
				p = p.next[i];
			}
			last[i] = p;
		}
	}

	// Return first element of list
	public T first() {
		if (size == 0) {
			return null;
		} else {
			T ent = (T) head.next[0].element;
			return ent;
		}
	}

	// Find largest element that is less than or equal to x
	public T floor(T x) {
		if (!contains(x)) {
			Entry<T> ele = last[0];
			return ele.element;
		} else {
			return x;
		}
	}

	// Return element at index n of list. First element is at index 0.
	public T get(int n) {
		return getLog(n);
	}

	// O(n) algorithm for get(n)
	public T getLinear(int n) {
		if (n < 0 || n > size - 1) {
			throw new  NoSuchElementException();
		}
		Entry<T> p = head;
		for (int i = 0; i <= n; i++) {
			p = p.next[0];
		}
		return p.element;
	}

	// Optional operation: Eligible for EC.
	// O(log n) expected time for get(n). Requires maintenance of spans, as
	// discussed in class.
	public T getLog(int n) {
		if (n < 0 || n > size - 1) {
			throw new  NoSuchElementException();
		}
		Entry<T> p = head;
		int index = -1;
		for (int i = this.maxLevel - 1; i >= 0; i--) {
			if(index+p.span[i]>n)
				continue;
			index +=p.span[i];
			while (p.next[i].element != null &&  index <= n) {	
				p = p.next[i];
				if(index+p.span[i]>n)
					break;
				index +=p.span[i];
			}
//			if(index==n)
//				break;
		}
		
		return p.element;
	}

	// Is the list empty?
	public boolean isEmpty() {
		if (size == 0) {
			return true;
		}
		return false;
	}

	// Iterate through the elements of list in sorted order
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
        };
        return it;
	}

	// Return last element of list
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
		System.out.println("Last : "+cursor.element);
		return cursor.element;
	}

	// Optional operation: Reorganize the elements of the list into a perfect skip
	// list
	// Not a standard operation in skip lists. Eligible for EC.
	public void rebuild() {
		Entry<T>[] newList;
		int newMaxLevel = (int) Math.ceil((Math.log(size + 1) / Math.log(2)));
		if (maxLevel < newMaxLevel) {
			newList = new Entry[size];
			rebuild(newList, 0, size - 1, maxLevel + 1);
			rebuildList(newList, maxLevel + 1);
			this.maxLevel = maxLevel + 1;
		} else if (maxLevel > newMaxLevel) {
			newList = new Entry[size];
			rebuild(newList, 0, size - 1, maxLevel - 1);
			rebuildList(newList, maxLevel - 1);
			this.maxLevel = maxLevel - 1;
		}
	}

	private void rebuild(Entry<T>[] newList, int start, int end, int newMaxLevel) { // recursively create entries of new
																					// levels
		if (start <= end) {
			if (newMaxLevel == 0) {
				for (int i = start; i <= end; i++) {
					newList[i] = new Entry<T>(null, 0);
				}
			} else {
				int mid = (start + end) / 2;
				newList[mid] = new Entry<T>(null, newMaxLevel);
				rebuild(newList, start, mid - 1, newMaxLevel - 1);
				rebuild(newList, mid + 1, end, newMaxLevel - 1);

			}
		}
	}

	private void rebuildList(Entry<T>[] newList, int newMaxLevel) { // set up links for newly created skip list
		Entry<T> newHeader = new Entry<T>(null, newMaxLevel);
		Entry<T>[] prev = new Entry[newMaxLevel + 1]; // store the current entry to update its nextPointers later
		Entry<T> p = head.next[0];
		int newListIndex = 0;
		int headerNextIndex = 0; // variable to keep track of next index at which nextPointers has to be filled
		while (p != null) {
			for (int i = 0; i < newList[newListIndex].next.length; i++) {
				if (headerNextIndex <= newMaxLevel && headerNextIndex == i) {
					newHeader.next[headerNextIndex] = newList[newListIndex];
					headerNextIndex++;
				}
				if (prev[i] != null) {
					prev[i].next[i] = newList[newListIndex]; // update the nextPointers of previously stored entry
				}
				prev[i] = newList[newListIndex]; // store the current entry to update its nextPointers later
			}
			newList[newListIndex].element = p.element;
			newListIndex++;
			p = p.next[0];
		}
		tail = newList[newList.length - 1];
		this.head = newHeader;
	}

	// Remove x from list. Removed element is returned. Return null if x not in list
	public T remove(T x) {
		if (!contains(x)) {
			return null;
		}
		Entry<T> ent = last[0].next[0];
		int lev = ent.next.length;
		for (int i = 0; i < lev ; i++) {
			last[i].next[i] = ent.next[i];
		}		
		size = size - 1;
		for(int i=1;i<lev;i++) {
			Entry<T> node = last[i];
			last[i].span[i]=updateSpan(node, i);
		}
		for(int i=Math.min(lev ,this.maxLevel);i< this.maxLevel;i++) {
			--last[i].span[i];
		}
		return ent.element;
	}

	// Return the number of elements in the list
	public int size() {
		return size;
	}
}