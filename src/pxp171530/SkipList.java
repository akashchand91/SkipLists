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
//		Entry prev;

		public Entry(E x, int lev) {
			element = x;
			next = new Entry[lev];
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
		head = new Entry<T>(null,33);
		tail = new Entry<T>(null,33);
		size=0;
		maxLevel=1;
		last=new Entry[33];
		random = new Random();
		for(int i = 32;i>=0;i--) {
			head.next[i] = tail;
		}
	}

	// Add x to list. If x already exists, reject it. Returns true if new node is
	// added to list
	public boolean add(T x) {
		if(contains(x)){
			return false;
		}
		int lev=chooseLevel();
		System.out.println("level: "+lev);
		Entry<T> ent = new Entry<T>(x,lev);
		// Assign pointers for upto max level
		for(int i=0;i< Math.min(lev ,this.maxLevel) ;i++){
			ent.next[i]=last[i].next[i];
			last[i].next[i]=ent;
		}
		
		//handle pointers if new level > max level
		for(int i = Math.min(lev ,this.maxLevel); i < lev; i++) {
			ent.next[i]=tail;
			head.next[i]=ent;
		}
		
//		ent.next[0].prev=ent;
//		ent.prev=last[0];
		if(lev>maxLevel){
			maxLevel=lev;
		}
		size++;
		return true;
	}

	private int chooseLevel() {
		int lev=1+Integer.numberOfTrailingZeros(random.nextInt());
		lev=lev<maxLevel+1?lev:maxLevel+1;
		return lev;
	}

	// Find smallest element that is greater or equal to x
	public T ceiling(T x) {
		if(contains(x)) {
			Entry<T> ele ;
			ele =  last[0].next[0];
			if(ele.next[0].element != null) {
				return (T) ele.next[0].element;
			}
			else{
				return ele.element;
			}	
		}else {
			return null;
		}
		
	}

	// Does list contain x?
	public boolean contains(T x) {
		find(x);
		if(last[0].next[0].element != null) {
			return last[0].next[0].element==x;
		}else {
			return false;
		}
	    
	}

	private void find(T x) {
		Entry<T> p=head;
		for (int i= this.maxLevel-1 ;i>=0;i--){
			while(p.next[i].element != null && p.next[i].element.compareTo(x) < 0 ){
				p = p.next[i];
			}
			last[i]=p;
		}
			
		
	}

	// Return first element of list
	public T first() {
		if(size==0){
			return null;
		}
		else{
		T ent= (T) head.next[0].element;
		return ent ;
			}
	}

	// Find largest element that is less than or equal to x
	public T floor(T x) {
		if(contains(x)) {
			Entry<T> ele = last[0];
			return ele.element;
		}
		return null;
	}

	// Return element at index n of list. First element is at index 0.
	public T get(int n) {
		return null;
	}

	// O(n) algorithm for get(n)
	public T getLinear(int n) {
		if(n<0||n>size-1){
			throw new  NoSuchElementException();
		}
		Entry<T> p=head;
		for(int i=0;i<=n;i++){
			p=p.next[0];
		}
		return p.element;
	}

	// Optional operation: Eligible for EC.
	// O(log n) expected time for get(n). Requires maintenance of spans, as
	// discussed in class.
	public T getLog(int n) {
		return null;
	}

	// Is the list empty?
	public boolean isEmpty() {
		if(size==0){
			return true;
		}
		return false;
	}

	// Iterate through the elements of list in sorted order
	public Iterator<T> iterator() {
		return null;
	}

	// Return last element of list
	public T last() {
		return null;
	}

	// Optional operation: Reorganize the elements of the list into a perfect skip
	// list
	// Not a standard operation in skip lists. Eligible for EC.
	public void rebuild() {

	}

	// Remove x from list. Removed element is returned. Return null if x not in list
	public T remove(T x) {
		if(!contains(x)){
			return null;
		}
		Entry<T> ent=last[0].next[0];
		for(int i=0;i<=ent.next.length-1;i++){
			last[i].next[i]=ent.next[i];
		}		
		size=size-1;
		return ent.element;
	}

	// Return the number of elements in the list
	public int size() {
		return size;
	}
}