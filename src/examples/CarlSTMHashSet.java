package examples;

import carlstm.CarlSTM;
import carlstm.NoActiveTransactionException;
import carlstm.Transaction;
import carlstm.TransactionAbortedException;
import carlstm.TxObject;

public class CarlSTMHashSet<T> implements Set<T> {
	/**
	 *Constructor
	 */
	public class HashTransactionAdd implements Transaction<Boolean> {
		@SuppressWarnings("rawtypes")
		CarlSTMHashSet someSet;
		T someObject;
		public HashTransactionAdd(@SuppressWarnings("rawtypes") CarlSTMHashSet set, T object){
			this.someSet = set;
			this.someObject = object;
		}

		@Override
		public Boolean run() throws NoActiveTransactionException,
				TransactionAbortedException {
			int hash = (someObject.hashCode() % CAPACITY + CAPACITY) % CAPACITY;
			@SuppressWarnings("unchecked")
			TxObject<Bucket> head = someSet.table[hash];

			Bucket oldHeadBucket = head.read();
			if (contains(oldHeadBucket, someObject)) {
				return false;
			}
			head.write(new Bucket(someObject, oldHeadBucket)); 
			return true;
			
		}
		
	}
	public  class HashTransactionContains implements Transaction<Boolean> {
		public 
		CarlSTMHashSet<T> someSet;
		T someObject;
		public HashTransactionContains(CarlSTMHashSet<T> set, T object){
			this.someSet = set;
			this.someObject = object;
		}
		@Override
		public Boolean run() throws NoActiveTransactionException,
				TransactionAbortedException {
			// TODO Auto-generated method stub
			int hash = (someObject.hashCode() % CAPACITY + CAPACITY) % CAPACITY;
			TxObject<Bucket> head = someSet.table[hash];
			Bucket oldHeadBucket = head.read();
			return contains(oldHeadBucket, someObject);
		}
		
	}
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
		public Bucket(Object item, Bucket next) {
			this.item = item;
			this.next = next;
		}
	}
	/**
	 * Our array of items. Each location in the array stores a linked list items
	 * that hash to that locations.
	 */
	private TxObject<Bucket>[] table;
	/**
	 * Capacity of the array. Since we do not support resizing, this is a
	 * constant.
	 */
	public static int CAPACITY = 1024;
	public static void SetCapacity(int cap){
		CAPACITY = cap; 
	}
	/**
	 * Create a new CarlSTMH ashSet.
	 */
	
	@SuppressWarnings("unchecked")
	public CarlSTMHashSet() {
		this.table = new TxObject[CAPACITY];
		for (int i = 0; i < this.table.length; i ++){
			this.table[i] = new TxObject(null); 
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
		while (bucket != null) {
			if (item.equals(bucket.item)) {
				return true;
			} 
			bucket = bucket.next;
		}
		return false;
	}
	@Override
	public boolean add(T item) {
		HashTransactionAdd tx = new HashTransactionAdd(this, item);
		return CarlSTM.execute(tx); 
	}


	@Override
	public boolean contains(T item) {
		HashTransactionContains tx = new HashTransactionContains(this, item ); 
		return CarlSTM.execute(tx); 
	}
}
