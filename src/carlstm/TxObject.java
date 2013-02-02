package carlstm;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/**
 * A TxObject is a special kind of object that can be read and written as part
 * of a transaction.
 * 
 * @param <T> type of the value stored in this TxObject
 */
public final class TxObject<T> {
	T value;
	ReadWriteLock lock;
	public TxObject(T value) {
		this.value = value;
		this.lock = new ReentrantReadWriteLock();
	}

	public T read() throws NoActiveTransactionException,
			TransactionAbortedException {
		// TODO implement me
		T value =(T) CarlSTM.threadLocal.get().read(this);
		return value;
	}

	public void write(T value) throws NoActiveTransactionException,
			TransactionAbortedException {
		// TODO implement me
		CarlSTM.threadLocal.get().write(this, value);
	}
}
