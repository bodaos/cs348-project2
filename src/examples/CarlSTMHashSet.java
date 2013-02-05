package examples;

import carlstm.NoActiveTransactionException;
import carlstm.TransactionAbortedException;
import carlstm.TxObject;

public class CarlSTMHashSet<T> implements Set<T> {
	/**
	 *Constructor
	 */
	private static class Bucket {
		/**
		 * The item stored at this entry. This is morally of type T, but Java
		 * generics do not play well with arrays, so we have to use Object
		 * instead.
		 */
		TxObject item;

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
		public Bucket(TxObject item, Bucket next) {
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
	private static final int CAPACITY = 1024;
	/**
	 * Create a new CarlSTMH ashSet.
	 */
	public CarlSTMHashSet() {
		this.table = new Bucket[CAPACITY];
	}
	/**
	 * A helper method to see if an item is stored at a given bucket.
	 * 
	 * @param bucket bucket to be searched
	 * @param item item to be searched for
	 * @return true if the item is in the bucket
	 */
	private boolean contains(Bucket bucket, TxObject item) {
		while (bucket != null) {

			try {
				if (item.read() == bucket.item.read()) {
					return true;
				}
			} catch (NoActiveTransactionException | TransactionAbortedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bucket = bucket.next;
		}
		return false;
	}
	@Override
	public boolean add(T item) {
		// Java returns a negative number for the hash; this is just converting
				// the negative number to a location in the array.
				int hash = (item.hashCode() % CAPACITY + CAPACITY) % CAPACITY;
				Bucket bucket = table[hash];
				if (contains(bucket, (TxObject)item)) {
					return false;
				}
				table[hash] = new Bucket((TxObject)item, bucket);
				return true;
	}


	@Override
	public boolean contains(T item) {
		int hash = (item.hashCode() % CAPACITY + CAPACITY) % CAPACITY;
		Bucket bucket = table[hash];
		return contains(bucket, (TxObject)item);
	}
}
