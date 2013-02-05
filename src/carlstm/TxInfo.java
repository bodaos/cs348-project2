package carlstm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
/**
 * This class holds transactional state for a single thread. You should use
 * {@link java.lang.ThreadLocal} to allocate a TxInfo to each Java thread. This
 * class is only used within the STM implementation, so it and its members are
 * set to package (default) visibility.
 */

class TxInfo<T> {
	/**
	 * variable to indicate if a thread is running
	 *
	 */

	public boolean active = false;
	/**
	 * Data structure to hold all the updates
	 *
	 */
	public HashMap<TxObject<T>, Update<T>> updates = new HashMap<TxObject<T>, Update<T>>();
	/**
	 * Start a transaction by initializing any necessary state. This method
	 * should throw {@link TransactionAlreadyActiveException} if a transaction
	 * is already being executed.
	 */
	private ArrayList<TxObject<T>> txReadLockAcquired = new ArrayList<TxObject<T>>();
	private ArrayList<TxObject<T>> txWriteLockAcquired = new ArrayList<TxObject<T>>();
	void end(){
		// end a current transaction when it is aborted
		active = false;
		updates.clear();
	}
	void start() {
		// TODO implement me
		if (active) throw new TransactionAlreadyActiveException();
		active = true;
	}
	//Delegate method to read a value of an item updates
	public T read(TxObject<T> object) throws TransactionAbortedException{
		if(updates.containsKey(object)){
			return  updates.get(object).newValue;
		}else{
			if(object.lock.readLock().tryLock()){
				Update<T> newUpdate = new Update<T>(object, object.value,
						object.value);
				updates.put(object, newUpdate);
				object.lock.readLock().unlock();
				return newUpdate.newValue;
			}else{
				throw new TransactionAbortedException();
			}

		} 
	}
	public void write(TxObject<T> object, T value) throws TransactionAbortedException{
		if(updates.containsKey(object)){
			updates.get(object).newValue = value;
		}else{
			if(object.lock.writeLock().tryLock()){
			Update<T> newUpdate = new Update<T>(object, object.value, value);
			updates.put(object, newUpdate);
			object.lock.readLock().unlock();
			}else{
				throw new TransactionAbortedException();
			}
		}
	}
	//Delegate method to write a value of an item to updates

	/**
	 * Try to commit a completed transaction. This method should update any
	 * written TxObjects, acquiring locks on those objects as needed.
	 * 
	 * @return true if the commit succeeds, false if the transaction aborted
	 * @throws TransactionAbortedException 
	 */

	boolean commit() throws TransactionAbortedException {
		// TODO implement me
		//Iterate through the hashmap: 
		boolean success = true;
		Iterator<Entry<TxObject<T>, Update<T>>> it = updates.entrySet().iterator();
		while(it.hasNext()){
			Entry<TxObject<T>, Update<T>> curEntry =(Entry<TxObject<T>, Update<T>>) it.next();
			TxObject<T> curObject = (TxObject<T>) curEntry.getKey();
			T oldValue = (T) curEntry.getValue().oldValue;
			T newValue = (T) curEntry.getValue().newValue;
			//Acquire all the locks
			if (oldValue == newValue) {
				//since the value is not changed we acquire only the read locks
				if(curObject.lock.readLock().tryLock()){
					//lock acquired
					txReadLockAcquired.add(curObject);
					//check "expected old value"
					if (curObject.value != updates.get(curObject).oldValue){
						this.abort();
						return false;
					}
				}else{
						this.abort();
						throw new TransactionAbortedException();
					}
				}else{
					//the oldValue != newValue then we have to acquire the write lock
					if(curObject.lock.writeLock().tryLock()){
						txWriteLockAcquired.add(curObject);
						//check "expected old value"
						if (curObject.value != updates.get(curObject).oldValue){
							this.abort();
							return false;
						}

					}else{
						this.abort();
						throw new TransactionAbortedException();
					}
				}
				//Release the readLock()
				this.ReleaseReadLocks();
				//Perform the write update
				for (int i = 0; i <  txWriteLockAcquired.size() ; i++){
					TxObject<T> cur =  txWriteLockAcquired.get(i);
					cur.value = updates.get(cur).newValue;
				}
				//Release the write locks
				this.ReleaseWriteLocks();
			}

			return success;
		}

		/**
		 * This method cleans up any transactional state if a transaction aborts.
		 */
		void ReleaseReadLocks(){
			for (int i = 0; i <  txReadLockAcquired.size() ; i++){
				TxObject<T> cur =  txReadLockAcquired.get(i);
				cur.lock.readLock().unlock();
				txReadLockAcquired.remove(i);
			}
		}
		void ReleaseWriteLocks(){
			for (int i = 0; i <  txWriteLockAcquired.size() ; i++){
				TxObject<T> cur =  txWriteLockAcquired.get(i);
				cur.lock.writeLock().unlock();
				txWriteLockAcquired.remove(i);
			}
		}
		void abort() {
			this.ReleaseReadLocks();
			this.ReleaseWriteLocks();
			updates.clear();
		}
	}
