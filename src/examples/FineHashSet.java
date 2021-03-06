package examples;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * This is a simple implementation of a Hash Set with separate chaining and no
 * rehashing. You should base your implementations of CoarseHashSet,
 * FineHashSet, and TransactionalHashSet on this class.
 * 
 * @param <T> type of the objects in the set.
 */

public class FineHashSet<T> implements Set<T> {

	public Lock[] lockArray; 

	/**
	 * Helper class - basically is a linked list of items that happen to map to
	 * the same hash code.
	 */
	private static class Bucket {
		/**
		 * The item stored at this entry. This is morally of type T, but Java
		 * generics do not play well with arrays, so we have to use Object
		 * instead.
		 */
		Object item;

		/**
		 * Next item in the list.
		 */
		Bucket next;

		/**
		 * Create a new bucket.
		 * 
		 * @param item item to be stored
		 * @param next next item in the list
		 */
		public  Bucket(Object item, Bucket next) {
				this.item = item;
				this.next = next;
		}
	}

	/**
	 * Our array of items. Each location in the array stores a linked list items
	 * that hash to that locations.
	 */
	private Bucket[] table;

	/**
	 * Capacity of the array. Since we do not support resizing, this is a
	 * constant.
	 */
	public static int CAPACITY = 1024;
	public static  void SetCapacity(int cap){
		CAPACITY = cap; 
	}
	/**
	 * Create a new FineHashSet.
	 */
	public FineHashSet() {
		this.table = new Bucket[CAPACITY];
		this.lockArray = new Lock[CAPACITY]; 
		for (int i = 0; i < CAPACITY; i++){
			this.lockArray[i] = new ReentrantLock(); 
		}
	}

	/**
	 * A helper method to see if an item is stored at a given bucket.
	 * 
	 * @param bucket bucket to be searched
	 * @param item item to be searched for
	 * @return true if the item is in the bucket
	 */
	private boolean contains(Bucket bucket, T item) {
		if (bucket == null) return false; 
			while (bucket != null) {

				if (item.equals(bucket.item)) {
					return true;
				}
				bucket = bucket.next;
			}
			return false;
	}

	/*
	 * (non-Javadoc)
	 * @see examples.Set#add(java.lang.Object)
	 */
	@Override
	public boolean add(T item) {
		// Java returns a negative number for the hash; this is just converting
		// the negative number to a location in the array.
		int hash = (item.hashCode() % CAPACITY + CAPACITY) % CAPACITY;
		Bucket bucket = table[hash];
		lockArray[hash].lock(); 
		try {
			if (contains(bucket, item)) {
				return false;
			}
			table[hash] = new Bucket(item, bucket);
			return true;
		} finally {
			lockArray[hash].unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see examples.Set#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(T item) {
		int hash = (item.hashCode() % CAPACITY + CAPACITY) % CAPACITY;
		if (lockArray[hash] == null ) lockArray[hash] = new ReentrantLock(); 
		lockArray[hash].lock(); 
		try{
			Bucket bucket = table[hash];
			return contains(bucket, item);
		} finally{
			lockArray[hash].unlock(); 
		}
	}
}
