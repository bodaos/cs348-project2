/*
 * FineList.java
 *
 * Created on January 3, 2006, 6:50 PM
 *
 * From "Multiprocessor Synchronization and Concurrent Data Structures",
 * by Maurice Herlihy and Nir Shavit.
 * Copyright 2006 Elsevier Inc. All rights reserved.
 */

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Fine-grained synchronization: lock coupling (hand-over-hand locking).
 * 
 * Modified by Laura Effinger-Dean to use read-write locks.
 * 
 * @param <T> Item type.
 * @author Maurice Herlihy
 */
public class ReadWriteList<T> {
	/**
	 * First list entry
	 */
	private Node head;

	/**
	 * Constructor
	 */
	public ReadWriteList() {
		// Add sentinels to start and end
		head = new Node(Integer.MIN_VALUE);
		head.next = new Node(Integer.MAX_VALUE);
	}

	/**
	 * Add an element.
	 * 
	 * @param item element to add
	 * @return true iff element was not there already
	 */
	public boolean add(T item) {
		int key = item.hashCode();
		head.writeLock();
		Node pred = head;
		try {
			Node curr = pred.next;
			curr.writeLock();
			try {
				while (curr.key < key) {
					pred.writeUnlock();
					pred = curr;
					curr = curr.next;
					curr.writeLock();
				}
				if (curr.key == key) {
					return false;
				}
				Node newNode = new Node(item);
				newNode.next = curr;
				pred.next = newNode;
				return true;
			} finally {
				curr.writeUnlock();
			}
		} finally {
			pred.writeUnlock();
		}
	}

	/**
	 * Remove an element.
	 * 
	 * @param item element to remove
	 * @return true iff element was present
	 */
	public boolean remove(T item) {
		Node pred = null, curr = null;
		int key = item.hashCode();
		head.writeLock();
		try {
			pred = head;
			curr = pred.next;
			curr.writeLock();
			try {
				while (curr.key < key) {
					pred.writeUnlock();
					pred = curr;
					curr = curr.next;
					curr.writeLock();
				}
				if (curr.key == key) {
					pred.next = curr.next;
					return true;
				}
				return false;
			} finally {
				curr.writeUnlock();
			}
		} finally {
			pred.writeUnlock();
		}
	}

	/**
	 * Check if the list contains an element.
	 * 
	 * @param item element to check for
	 * @return true if it is in the list
	 */
	public boolean contains(T item) {
		Node pred = null, curr = null;
		int key = item.hashCode();
		head.readLock();
		try {
			pred = head;
			curr = pred.next;
			curr.readLock();
			try {
				while (curr.key < key) {
					pred.readUnlock();
					pred = curr;
					curr = curr.next;
					curr.readLock();
				}
				return (curr.key == key);
			} finally {
				curr.readUnlock();
			}
		} finally {
			pred.readUnlock();
		}
	}

	/**
	 * list Node
	 */
	private class Node {
		/**
		 * actual item
		 */
		@SuppressWarnings("unused")
		T item;
		/**
		 * item's hash code
		 */
		int key;
		/**
		 * next Node in list
		 */
		Node next;
		/**
		 * synchronizes individual Node
		 */
		ReadWriteLock lock;

		/**
		 * Constructor for usual Node
		 * 
		 * @param item element in list
		 */
		Node(T item) {
			this.item = item;
			this.key = item.hashCode();
			this.lock = new ReentrantReadWriteLock();
		}

		/**
		 * Constructor for sentinel Node
		 * 
		 * @param key should be min or max int value
		 */
		Node(int key) {
			this.item = null;
			this.key = key;
			this.lock = new ReentrantReadWriteLock();
		}

		/**
		 * Lock node for reading
		 */
		void readLock() {
			lock.readLock().lock();
		}

		/**
		 * Unlock node for reading
		 */
		void readUnlock() {
			lock.readLock().unlock();
		}
		
		/**
		 * Lock node for writing
		 */
		void writeLock() {
			lock.writeLock().lock();
		}
		
		/**
		 * Lock node for reading
		 */
		void writeUnlock() {
			lock.writeLock().unlock();
		}
	}
}
